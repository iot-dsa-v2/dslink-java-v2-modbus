package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingDevice;
import org.iot.dsa.dslink.dftest.TestingPoint;
import org.iot.dsa.dslink.modbus.ModbusMockPointParameters;
import org.iot.dsa.dslink.modbus.ModbusTestingPoint;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSMap;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingDevice extends TestingDevice {

    SlaveDeviceNode myNode;

    ModbusSlaveTestingDevice(String name, MockParameters pars) {
        super(name, pars);
        myNode = new SlaveDeviceNode();
        myNode.parameters = pars.getParamMap();
        System.out.println(pars.getParamMap()); //DEBUG
    }

    @Override
    protected void addPoint(String name, String value, Random rand) {
        ModbusMockSlavePointParameters pars = new ModbusMockSlavePointParameters(rand);
        ModbusSlaveTestingPoint pnt = new ModbusSlaveTestingPoint(name, value, pars);
        myNode.put(name, pnt.myNode);
        points.put(name, pnt);
        pnt.setValue(value);
        System.out.println("Making a Point: \n" + myNode.getParentNode().parameters + "\n" + myNode.parameters + "\n" + pars.getParamMap());
    }

    @Override
    protected boolean flipDev() {
        active = !active;
        whipTheSlaves();
        return active;
    }

    @Override
    protected void setDevActive(boolean act) {
        active = act;
        whipTheSlaves();
    }

    @Override
    protected void removePoint(String name) {
        points.remove(name);
        myNode.remove(name);
    }

    private void whipTheSlaves() {
        if (active) myNode.startSlave();
        else myNode.stopSlave();
    }
}
