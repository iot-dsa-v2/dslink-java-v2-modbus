package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingDevice;

/**
 * @author James (Juris) Puchin
 * Created on 1/23/2018
 */
public class ModbusTestingDevice extends TestingDevice {
    ModbusTestingDevice(String name, MockParameters pars) {
        super(name, pars);
    }

    @Override
    protected void addPoint(String name, String value) {
        ModbusMockPointParameters pars = new ModbusMockPointParameters();
        points.put(name, new ModbusTestingPoint(value, value, pars));
    }
}
