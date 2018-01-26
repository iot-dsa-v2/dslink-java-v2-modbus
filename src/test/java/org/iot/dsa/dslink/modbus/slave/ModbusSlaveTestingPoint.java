package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingPoint;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingPoint extends TestingPoint {
    ModbusSlaveTestingPoint(String name, String val, MockParameters pars) {
        super(name, val, pars);
        System.out.println(pars.getParamMap()); //DEBUG
    }
}
