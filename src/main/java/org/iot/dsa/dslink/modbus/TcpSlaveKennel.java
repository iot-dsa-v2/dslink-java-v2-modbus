package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
public class TcpSlaveKennel extends SlaveKennel {

    @Override
    public ModbusSlaveSet createSlaveSet(int port, boolean encapsulated) {
        return new TcpSlave(port, encapsulated);
    }
}
