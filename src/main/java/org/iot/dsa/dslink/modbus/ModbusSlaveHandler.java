package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.exception.ModbusInitException;
import org.iot.dsa.DSRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
public class ModbusSlaveHandler {
    private static final SlaveKennel tcpSlaveSets = new TcpSlaveKennel();
    private static final SlaveKennel udpSlaveSets = new UdpSlaveKennel();
    private static final Map<Integer, ModbusSlaveSet> asciiSlaveSets = new ConcurrentHashMap<>();
    private static final Map<Integer, ModbusSlaveSet> rtuSlaveSets = new ConcurrentHashMap<>();

    public static void deleteUdpProcessImage(int port, int slaveId) {
        deleteProcessImage(port, slaveId, udpSlaveSets);
    }

    public static void deleteTcpProcessImage(int port, int slaveId) {
        deleteProcessImage(port, slaveId, tcpSlaveSets);
    }

    private static void deleteProcessImage(int port, int slaveId, SlaveKennel slaveSetMap) {
        ModbusSlaveSet set = slaveSetMap.get(port);
        if (set != null) {
            if (set.removeProcessImage(slaveId)) {
                if (set.getProcessImages().size() == 0)
                    slaveSetMap.remove(port);
            }
        }
    }

    public static BasicProcessImage getUdpProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        return getProcessImage(port, slaveId, devNode, udpSlaveSets);
    }

    public static BasicProcessImage getTcpProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        return getProcessImage(port, slaveId, devNode, tcpSlaveSets);
    }

    private static BasicProcessImage getProcessImage(int port, int slaveId, SlaveDeviceNode devNode, SlaveKennel slaveSetMap) {
        devNode.clearError();
        ModbusSlaveSet set = getSlaveSet(port, devNode, slaveSetMap);
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

    private static ModbusSlaveSet getSlaveSet(int port, SlaveDeviceNode devNode, SlaveKennel slaveSetMap) {
        ModbusSlaveSet set = slaveSetMap.get(port);
        if (set == null) {
            set = createSet(port, devNode, slaveSetMap);
        }
        return set;
    }

    private static ModbusSlaveSet createSet(int port, SlaveDeviceNode devNode, SlaveKennel slaveSetMap) {
        final ModbusSlaveSet tcpSlave = slaveSetMap.createSlaveSet(port, false);
        slaveSetMap.put(port, tcpSlave);

        DSRuntime.run(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpSlave.start();
                } catch (ModbusInitException e) {
                    devNode.warn(e);
                    devNode.setError("TCP Slave device failed.");
                }
            }
        });

        return tcpSlave;
    }
}
