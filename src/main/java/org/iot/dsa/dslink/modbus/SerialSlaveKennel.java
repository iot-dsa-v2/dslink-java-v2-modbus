package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.serial.rtu.RtuSlave;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
public class SerialSlaveKennel extends SlaveKennel {

    @Override
    public ModbusSlaveSet createSlaveSet(int port, boolean encapsulated) {
        return null;
    }
}