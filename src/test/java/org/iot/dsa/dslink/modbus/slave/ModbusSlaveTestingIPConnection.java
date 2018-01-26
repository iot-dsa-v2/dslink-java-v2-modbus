package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingConnection;
import org.iot.dsa.dslink.dftest.TestingDevice;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingIPConnection extends TestingConnection {
    public ModbusSlaveTestingIPConnection() {

    }

    ModbusSlaveTestingIPConnection(String name, MockParameters pars) {
        super(name, pars);
        System.out.println(pars.getParamMap()); //DEBUG
    }

    @Override
    public ModbusSlaveTestingIPConnection addNewConnection(String name, Random rand) {
        ModbusMockSlaveIPParameters pars = new ModbusMockSlaveIPParameters(rand);
        ModbusSlaveTestingIPConnection conn = new ModbusSlaveTestingIPConnection(name, pars);
        addConnection(name, conn);
        return conn;
    }

    @Override
    protected TestingDevice addNewDevice(String name, Random rand) {
        ModbusMockSlaveDeviceParameters pars = new ModbusMockSlaveDeviceParameters(rand);
        TestingDevice dev = new ModbusSlaveTestingDevice(name, pars);
        addDevice(name, dev);
        return dev;
    }
}
