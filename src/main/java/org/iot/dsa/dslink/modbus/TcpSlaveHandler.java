package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import org.iot.dsa.DSRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
public class TcpSlaveHandler {
    private static final Map<Integer, ModbusSlaveSet> slaveSets = new ConcurrentHashMap<>();

    public static BasicProcessImage getProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
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

    private static ModbusSlaveSet getSlaveSet(int port, SlaveDeviceNode devNode) {
        ModbusSlaveSet set = slaveSets.get(port);
        if (set == null) {
            set = createSet(port, devNode);
        }
        return set;
    }

    private static ModbusSlaveSet createSet(int port, SlaveDeviceNode devNode) {
        final ModbusSlaveSet tcpSlave = new TcpSlave(port, false);
        slaveSets.put(port, tcpSlave);

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
