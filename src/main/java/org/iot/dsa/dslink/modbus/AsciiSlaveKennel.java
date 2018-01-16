package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.serial.ascii.AsciiSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
public class AsciiSlaveKennel extends SlaveKennel<SerialPortWrapperImpl, String> {

    @Override
    String getKeyFromPort(SerialPortWrapperImpl port) {
        return port.getSerialPortName();
    }

    @Override
    public ModbusSlaveSet createSlaveSet(SerialPortWrapperImpl wrapper) {
        return new AsciiSlave(wrapper);
    }
}
