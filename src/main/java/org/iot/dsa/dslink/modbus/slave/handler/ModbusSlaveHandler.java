package org.iot.dsa.dslink.modbus.slave.handler;

import com.serotonin.modbus4j.*;
import org.iot.dsa.dslink.modbus.slave.SlaveDeviceNode;
import org.iot.dsa.dslink.modbus.utils.SerialPortWrapperImpl;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
public class ModbusSlaveHandler {

    private static final SlaveKennel<Integer, Integer> tcpSlaveSets = new TcpSlaveKennel();
    private static final SlaveKennel<Integer, Integer> udpSlaveSets = new UdpSlaveKennel();
    private static final SlaveKennel<SerialPortWrapperImpl, String> asciiSlaveSets = new AsciiSlaveKennel();
    private static final SlaveKennel<SerialPortWrapperImpl, String> rtuSlaveSets = new RtuSlaveKennel();

    static public void deleteRtuProcessImage(SerialPortWrapperImpl port, int slaveId) {
        rtuSlaveSets.deleteProcessImage(port, slaveId);
    }

    static public void deleteAsciiProcessImage(SerialPortWrapperImpl port, int slaveId) {
        asciiSlaveSets.deleteProcessImage(port, slaveId);
    }

    static public void deleteUdpProcessImage(int port, int slaveId) {
        udpSlaveSets.deleteProcessImage(port, slaveId);
    }

    static public void deleteTcpProcessImage(int port, int slaveId) {
        tcpSlaveSets.deleteProcessImage(port, slaveId);
    }

    static public BasicProcessImage getAsciiProcessImage(SerialPortWrapperImpl port, int slaveId, SlaveDeviceNode devNode) {
        return asciiSlaveSets.getProcessImage(port, slaveId, devNode);
    }

    static public BasicProcessImage getRtuProcessImage(SerialPortWrapperImpl port, int slaveId, SlaveDeviceNode devNode) {
        return rtuSlaveSets.getProcessImage(port, slaveId, devNode);
    }

    static public BasicProcessImage getUdpProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        return udpSlaveSets.getProcessImage(port, slaveId, devNode);
    }

    static public BasicProcessImage getTcpProcessImage(int port, int slaveId, SlaveDeviceNode devNode) {
        return tcpSlaveSets.getProcessImage(port, slaveId, devNode);
    }
}
