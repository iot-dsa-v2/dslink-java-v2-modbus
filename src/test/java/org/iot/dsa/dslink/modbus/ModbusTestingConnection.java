package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingConnection;
import org.iot.dsa.dslink.dftest.TestingDevice;

/**
 * @author James (Juris) Puchin
 * Created on 1/23/2018
 */
public class ModbusTestingConnection extends TestingConnection {

    ModbusTestingConnection(String name, MockParameters pars) {
        super(name, pars);
    }

    @Override
    public ModbusTestingConnection addNewConnection(String name) {
        ModbusMockConnectionParameters pars = new ModbusMockConnectionParameters();
        ModbusTestingConnection conn = new ModbusTestingConnection(name, pars);
        addConnection(name, conn);
        return conn;
    }

    @Override
    protected TestingDevice addNewDevice(String name) {
        ModbusMockDeviceParameters pars = new ModbusMockDeviceParameters();
        TestingDevice dev = new ModbusTestingDevice(name, pars);
        addDevice(name, dev);
        return dev;
    }
}
