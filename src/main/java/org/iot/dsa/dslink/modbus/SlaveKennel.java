package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ProcessImage;
import com.serotonin.modbus4j.exception.ModbusInitException;
import org.iot.dsa.DSRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
abstract class SlaveKennel<P, K> {
    private final Map<K, ModbusSlaveSet> slaveSets = new ConcurrentHashMap<>();

    abstract ModbusSlaveSet createSlaveSet(P connParameters);

    abstract K getKeyFromPort(P port);

    void deleteProcessImage(P port, int slaveId) {
        ModbusSlaveSet set = slaveSets.get(getKeyFromPort(port));
        if (set != null) {
            if (set.removeProcessImage(slaveId)) {
                if (set.getProcessImages().size() == 0)
                    slaveSets.remove(getKeyFromPort(port));
            }
        }
    }

    BasicProcessImage getProcessImage(P port, int slaveId, SlaveDeviceNode devNode) {
        devNode.clearError();
        ModbusSlaveSet set = getSlaveSet(port, devNode);
        ProcessImage img = set.getProcessImage(slaveId);
        if (img == null) {
            img = createProcessImage(slaveId, devNode);
            set.addProcessImage(img);
        } else {
            devNode.setError("Duplicate Slave Device!");
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
                    devNode.warn(e);
                    devNode.setError("Slave device failed.");
                }
            }
        });

        return slaveSet;
    }
}
