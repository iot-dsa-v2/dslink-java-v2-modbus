package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.MockParameters;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/23/2018
 */
public class ModbusMockDeviceParameters extends MockParameters {

    ModbusMockDeviceParameters(Random rand) {
        super(ModbusDeviceNode.class, rand);
    }
}
