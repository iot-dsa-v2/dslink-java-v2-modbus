package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingPoint;

/**
 * @author James (Juris) Puchin
 * Created on 1/23/2018
 */
public class ModbusTestingPoint extends TestingPoint {
    ModbusTestingPoint(String name, String val, MockParameters pars) {
        super(name, val, pars);
        System.out.println(pars.getParamMap());
    }
}
