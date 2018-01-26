package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingConnection;
import org.iot.dsa.dslink.dftest.TestingDevice;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/23/2018
 */
public class ModbusTestingIPConnection extends TestingConnection {

    ModbusTestingIPConnection() {

    }

    ModbusTestingIPConnection(String name, MockParameters pars) {
        super(name, pars);
        System.out.println(pars.getParamMap());
    }

    @Override
    public ModbusTestingIPConnection addNewConnection(String name, Random rand) {
        ModbusMockIPConnectionParameters pars = new ModbusMockIPConnectionParameters(rand);
        ModbusTestingIPConnection conn = new ModbusTestingIPConnection(name, pars);
        addConnection(name, conn);
        return conn;
    }

    @Override
    protected TestingDevice addNewDevice(String name, Random rand) {
        ModbusMockDeviceParameters pars = new ModbusMockDeviceParameters(rand);
        TestingDevice dev = new ModbusTestingDevice(name, pars);
        addDevice(name, dev);
        return dev;
    }
}
