package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingDevice;
import org.iot.dsa.dslink.modbus.ModbusMockPointParameters;
import org.iot.dsa.dslink.modbus.ModbusTestingPoint;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingDevice extends TestingDevice {
    ModbusSlaveTestingDevice(String name, MockParameters pars) {
        super(name, pars);
        System.out.println(pars.getParamMap()); //DEBUG
    }

    @Override
    protected void addPoint(String name, String value, Random rand) {
        ModbusMockSlavePointParameters pars = new ModbusMockSlavePointParameters(rand);
        points.put(name, new ModbusSlaveTestingPoint(value, value, pars));
    }
}
