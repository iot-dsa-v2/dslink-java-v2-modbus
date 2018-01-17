package org.iot.dsa.dslink.modbus.slave;

import com.serotonin.modbus4j.*;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.modbus.utils.SerialPortWrapperImpl;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
class ModbusSlaveHandler {

    private static final SlaveKennel<Integer, Integer> tcpSlaveSets = new TcpSlaveKennel();
    private static final SlaveKennel<Integer, Integer> udpSlaveSets = new UdpSlaveKennel();
    private static final SlaveKennel<SerialPortWrapperImpl, String> asciiSlaveSets = new AsciiSlaveKennel();
    private static final SlaveKennel<SerialPortWrapperImpl, String> rtuSlaveSets = new RtuSlaveKennel();

    static void deleteRtuProcessImage(SerialPortWrapperImpl port, int slaveId) {
        rtuSlaveSets.deleteProcessImage(port, slaveId);
    }

    static void deleteAsciiProcessImage(SerialPortWrapperImpl port, int slaveId) {
        asciiSlaveSets.deleteProcessImage(port, slaveId);
    }

    static void deleteUdpProcessImage(int port, int slaveId) {
        udpSlaveSets.deleteProcessImage(port, slaveId);
    }

    static void deleteTcpProcessImage(int port, int slaveId) {
        tcpSlaveSets.deleteProcessImage(port, slaveId);
    }

    static BasicProcessImage getAsciiProcessImage(SerialPortWrapperImpl port, int slaveId, SlaveDeviceNode devNode) {
        return asciiSlaveSets.getProcessImage(port, slaveId, devNode);
    }

    static BasicProcessImage getRtuProcessImage(SerialPortWrapperImpl port, int slaveId, SlaveDeviceNode devNode) {
        return rtuSlaveSets.getProcessImage(port, slaveId, devNode);
    }

    static BasicProcessImage getUdpProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        return udpSlaveSets.getProcessImage(port, slaveId, devNode);
    }

    static BasicProcessImage getTcpProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        return tcpSlaveSets.getProcessImage(port, slaveId, devNode);
    }
}
