package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import com.serotonin.modbus4j.serial.ascii.AsciiSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
public class AsciiSlaveKennel extends SlaveKennel<SerialPortWrapper> {

    @Override
    public ModbusSlaveSet createSlaveSet(SerialPortWrapper wrapper) {
        return new AsciiSlave(wrapper);
    }
}
