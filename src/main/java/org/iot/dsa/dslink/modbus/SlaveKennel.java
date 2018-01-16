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
abstract class SlaveKennel<T> {
    private final Map<T, ModbusSlaveSet> slaveSets = new ConcurrentHashMap<>();

    abstract ModbusSlaveSet createSlaveSet(T connParameters);

    void deleteProcessImage(T port, int slaveId) {
        ModbusSlaveSet set = slaveSets.get(port);
        if (set != null) {
            if (set.removeProcessImage(slaveId)) {
                if (set.getProcessImages().size() == 0)
                    slaveSets.remove(port);
            }
        }
    }

    BasicProcessImage getProcessImage(T port, int slaveId, SlaveDeviceNode devNode, SlaveKennel slaveSetMap) {
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

    private ModbusSlaveSet getSlaveSet(T port, SlaveDeviceNode devNode) {
        ModbusSlaveSet set = slaveSets.get(port);
        if (set == null) {
            set = createSet(port, devNode);
        }
        return set;
    }

    private ModbusSlaveSet createSet(T port, SlaveDeviceNode devNode) {
        final ModbusSlaveSet slaveSet = createSlaveSet(port);
        slaveSets.put(port, slaveSet);

        DSRuntime.run(new Runnable() {
            @Override
            public void run() {
                try {
                    slaveSet.start();
                } catch (ModbusInitException e) {
                    devNode.warn(e);
                    devNode.setError("TCP Slave device failed.");
                }
            }
        });

        return slaveSet;
    }
}
