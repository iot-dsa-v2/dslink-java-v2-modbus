package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingPoint;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingPoint extends TestingPoint {

    SlavePointNode myNode;

    ModbusSlaveTestingPoint(String name, String val, MockParameters pars) {
        super(name, val, pars);
        myNode = new SlavePointNode();
        myNode.parameters = pars.getParamMap();
        System.out.println(pars.getParamMap()); //DEBUG
    }
}
