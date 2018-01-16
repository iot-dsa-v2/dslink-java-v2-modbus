package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import com.serotonin.modbus4j.ip.udp.UdpSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
public class UdpSlaveKennel extends SlaveKennel {
    @Override
    public ModbusSlaveSet createSlaveSet(int port, boolean encapsulated) {
        return new UdpSlave(port, encapsulated);
    }
}
