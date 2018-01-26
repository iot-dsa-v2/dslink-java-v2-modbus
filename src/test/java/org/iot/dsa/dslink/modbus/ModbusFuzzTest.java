package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.FuzzTest;
import org.iot.dsa.dslink.dftest.TestingConnection;
import org.junit.Before;
import org.junit.Test;

/**
 * @author James (Juris) Puchin
 * Created on 1/25/2018
 */
public class ModbusFuzzTest {

    @Test
    public void buildModbusMockTreeTest() {
        FuzzTest.buildMockTree(100, new ModbusTestingConnection());
    }

}
