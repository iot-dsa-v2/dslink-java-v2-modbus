package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.FuzzTest;
import org.iot.dsa.dslink.modbus.slave.ModbusSlaveTestingIPConnection;
import org.junit.Test;

/**
 * @author James (Juris) Puchin
 * Created on 1/25/2018
 */
public class ModbusFuzzTest {

    @Test
    public void buildModbusMockTreeTest() {
        FuzzTest.buildMockTree(100, new ModbusTestingIPConnection());
    }

    @Test
    public void buildModbusMockSlaveTreeTest() {
        FuzzTest.buildMockTree(100, new ModbusSlaveTestingIPConnection());
    }

}
