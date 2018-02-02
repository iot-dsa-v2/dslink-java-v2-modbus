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

    IpSlaveConnectionNode myNode;

    public ModbusSlaveTestingIPConnection() {

    }

    ModbusSlaveTestingIPConnection(String name, MockParameters pars) {
        super(name, pars);
        myNode = new IpSlaveConnectionNode();
        myNode.parameters = pars.getParamMap();
        System.out.println(pars.getParamMap()); //DEBUG
    }

    ///////////////////////////////////////////////////////////////////////////
    // Connection Controls
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public ModbusSlaveTestingIPConnection addNewConnection(String name, Random rand) {
        ModbusMockSlaveIPParameters pars = new ModbusMockSlaveIPParameters(rand);
        ModbusSlaveTestingIPConnection conn = new ModbusSlaveTestingIPConnection(name, pars);
        addConnection(name, conn);
        return conn;
    }

    @Override
    public boolean flipPowerSwitch() {
        pluggedIn = !pluggedIn;
        updatePowerState();
        return pluggedIn;
    }

    @Override
    public void setPowerState(boolean newState) {
        pluggedIn = newState;
        updatePowerState();
    }

    private void updatePowerState() {
        if (pluggedIn) myNode.startSlaves();
        else myNode.stopSlaves();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Device Controls
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected TestingDevice addNewDevice(String name, Random rand) {
        ModbusMockSlaveDeviceParameters pars = new ModbusMockSlaveDeviceParameters(rand);
        ModbusSlaveTestingDevice dev = new ModbusSlaveTestingDevice(name, pars);
        myNode.add(name, dev.myNode);
        dev.myNode.startSlave();
        addDevice(name, dev);
        return dev;
    }
}
