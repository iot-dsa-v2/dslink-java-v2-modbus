package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusMockSlavePointParameters extends MockParameters {
    public ModbusMockSlavePointParameters(Random rand) {
        super(SlavePointNode.class, rand);
    }
}
