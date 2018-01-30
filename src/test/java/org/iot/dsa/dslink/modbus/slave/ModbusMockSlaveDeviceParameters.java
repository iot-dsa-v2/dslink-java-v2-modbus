package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusMockSlaveDeviceParameters extends MockParameters {
    public ModbusMockSlaveDeviceParameters(Random rand) {
        super(SlaveDeviceNode.class, rand);
    }
}
