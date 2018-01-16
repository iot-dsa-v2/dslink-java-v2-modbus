package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
class TcpSlaveKennel extends SlaveKennel<Integer, Integer> {

    @Override
    Integer getKeyFromPort(Integer port) {
        return port;
    }

    @Override
    ModbusSlaveSet createSlaveSet(Integer port) {
        return new TcpSlave(port, false);
    }
}
