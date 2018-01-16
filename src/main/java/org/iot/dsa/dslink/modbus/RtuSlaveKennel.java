package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import com.serotonin.modbus4j.serial.rtu.RtuSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/16/2018
 */
public class RtuSlaveKennel extends SlaveKennel<SerialPortWrapper> {

    @Override
    public ModbusSlaveSet createSlaveSet(SerialPortWrapper wrapper) {
        return new RtuSlave(wrapper);
    }
}
