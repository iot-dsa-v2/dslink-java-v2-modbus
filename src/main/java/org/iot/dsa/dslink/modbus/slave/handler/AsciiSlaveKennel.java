package org.iot.dsa.dslink.modbus.slave.handler;

import org.iot.dsa.dslink.modbus.utils.SerialPortWrapperImpl;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.serial.ascii.AsciiSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
class AsciiSlaveKennel extends SlaveKennel<SerialPortWrapperImpl, String> {

    @Override
    String getKeyFromPort(SerialPortWrapperImpl port) {
        return port.getSerialPortName();
    }

    @Override
    ModbusSlaveSet createSlaveSet(SerialPortWrapperImpl wrapper) {
        return new AsciiSlave(wrapper);
    }
}
