package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.serial.rtu.RtuSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/16/2018
 */
public class RtuSlaveKennel extends SlaveKennel<SerialPortWrapperImpl, String> {

    @Override
    String getKeyFromPort(SerialPortWrapperImpl port) {
        return port.getSerialPortName();
    }

    @Override
    public ModbusSlaveSet createSlaveSet(SerialPortWrapperImpl wrapper) {
        return new RtuSlave(wrapper);
    }
}
