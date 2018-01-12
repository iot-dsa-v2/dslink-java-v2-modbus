package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
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
    private static final Map<Integer, ModbusSlaveSet> listeners = new ConcurrentHashMap<>();

    public static void addSlavePoint(int port, int slaveId, SlaveDeviceNode devNode) {
        getProcessImage(port, slaveId, devNode);
        //ModbusMaster master = new ModbusFactory().createTcpMaster(params, false);
    }

    //TODO: make sure error is generated instead of giving the same image twice
    public static BasicProcessImage getProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        ModbusSlaveSet set = getSlaveSet(port);
        ProcessImage img = set.getProcessImage(slaveId);
        if (img == null) {
            img = createModscanProcessImage(slaveId, devNode);
            set.addProcessImage(img);
        }
        return (BasicProcessImage) img;
    }

    private static BasicProcessImage createModscanProcessImage(int slaveId, SlaveDeviceNode devNode) {
        BasicProcessImage processImage = new BasicProcessImage(slaveId);
        processImage.setAllowInvalidAddress(true);
        processImage.setInvalidAddressValue((short) 0);
        processImage.setExceptionStatus((byte) 151);
        processImage.addListener(devNode.makeListener());

        return processImage;
    }

    private static ModbusSlaveSet getSlaveSet(int port) {
        ModbusSlaveSet set = listeners.get(port);
        if (set == null) {
            set = createSet(port);
        }
        return set;
    }

    private static ModbusSlaveSet createSet(int port) {
        final ModbusSlaveSet listener = new TcpSlave(port, false);
        listeners.put(port, listener);

        DSRuntime.run(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.start();
                } catch (ModbusInitException e) {
                    e.printStackTrace();
                }
            }
        });

        return listener;
    }
}
