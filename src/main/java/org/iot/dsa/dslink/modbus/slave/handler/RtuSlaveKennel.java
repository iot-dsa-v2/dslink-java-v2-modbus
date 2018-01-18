package org.iot.dsa.dslink.modbus.slave.handler;

import org.iot.dsa.dslink.modbus.utils.SerialPortWrapperImpl;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.serial.rtu.RtuSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/16/2018
 */
class RtuSlaveKennel extends SlaveKennel<SerialPortWrapperImpl, String> {

    @Override
    String getKeyFromPort(SerialPortWrapperImpl port) {
        return port.getSerialPortName();
    }

    @Override
    ModbusSlaveSet createSlaveSet(SerialPortWrapperImpl wrapper) {
        return new RtuSlave(wrapper);
    }
}
