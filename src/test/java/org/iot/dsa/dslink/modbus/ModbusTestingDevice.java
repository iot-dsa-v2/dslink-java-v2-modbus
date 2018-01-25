package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingDevice;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/23/2018
 */
public class ModbusTestingDevice extends TestingDevice {
    ModbusTestingDevice(String name, MockParameters pars) {
        super(name, pars);
    }

    @Override
    protected void addPoint(String name, String value, Random rand) {
        ModbusMockPointParameters pars = new ModbusMockPointParameters(rand);
        points.put(name, new ModbusTestingPoint(value, value, pars));
    }
}
