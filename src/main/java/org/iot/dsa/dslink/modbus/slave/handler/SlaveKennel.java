package org.iot.dsa.dslink.modbus.slave.handler;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ProcessImage;
import com.serotonin.modbus4j.exception.ModbusInitException;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.modbus.slave.SlaveDeviceNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
abstract class SlaveKennel <P, K> {
    private final Map <K, ModbusSlaveSet> slaveSets = new ConcurrentHashMap<>();

    abstract ModbusSlaveSet createSlaveSet(P connParameters);

    abstract K getKeyFromPort(P port);

    void deleteProcessImage(P port, int slaveId) {
        ModbusSlaveSet set = slaveSets.get(getKeyFromPort(port));
        if (set != null) {
            if (set.removeProcessImage(slaveId)) {
                if (set.getProcessImages().size() == 0)
                    slaveSets.remove(getKeyFromPort(port)).stop();
            }
        }
    }

    BasicProcessImage getProcessImage(P port, int slaveId, SlaveDeviceNode devNode) {
        if (devNode != null) devNode.clearError();
        ModbusSlaveSet set = getSlaveSet(port, devNode);
        ProcessImage img = set.getProcessImage(slaveId);
        if (img == null) {
            img = createProcessImage(slaveId, devNode);
            set.addProcessImage(img);
        } else {
            String error = "Duplicate Slave Device. Port:" + port + ", slave:" + slaveId;
            if (devNode != null) {
                devNode.setError(error);
            } else {
                System.out.println(error);
            }
        }
        return (BasicProcessImage) img;
    }

    private static BasicProcessImage createProcessImage(int slaveId, SlaveDeviceNode devNode) {
        BasicProcessImage processImage = new BasicProcessImage(slaveId);
        processImage.setAllowInvalidAddress(true);
        processImage.setInvalidAddressValue((short) 0);
        processImage.setExceptionStatus((byte) 151);
        processImage.addListener(devNode.makeListener());

        return processImage;
    }

    private ModbusSlaveSet getSlaveSet(P port, SlaveDeviceNode devNode) {
        ModbusSlaveSet set = slaveSets.get(getKeyFromPort(port));
        if (set == null) {
            set = createSet(port, devNode);
        }
        return set;
    }

    private ModbusSlaveSet createSet(P port, SlaveDeviceNode devNode) {
        final ModbusSlaveSet slaveSet = createSlaveSet(port);
        slaveSets.put(getKeyFromPort(port), slaveSet);

        DSRuntime.run(new Runnable() {
            @Override
            public void run() {
                try {
                    slaveSet.start();
                } catch (ModbusInitException e) {
                    if (devNode != null) {
                        devNode.warn(e);
                        devNode.setError("Slave device failed.");
                    } else {
                        System.out.println("SlaveDeviceNode not set. ModbusException generated: " + e);
                    }
                }
            }
        });

        return slaveSet;
    }
}
