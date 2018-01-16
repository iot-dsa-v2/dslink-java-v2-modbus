package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.udp.UdpSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
class UdpSlaveKennel extends SlaveKennel<Integer, Integer> {

    @Override
    Integer getKeyFromPort(Integer port) {
        return port;
    }

    @Override
    ModbusSlaveSet createSlaveSet(Integer port) {
        return new UdpSlave(port, false);
    }
}
