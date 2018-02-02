package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.FuzzTest;
import org.iot.dsa.dslink.modbus.slave.ModbusSlaveTestingIPConnection;
import org.junit.Test;

import java.util.Scanner;

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
        Scanner usrIn = new Scanner(System.in);
        System.out.println("Press Enter to release slaves");
        usrIn.nextLine();
    }

    @Test
    public void buildModbusActionTree() {
        FuzzTest.PING_POLL_RATE = 50;
        FuzzTest.buildActionTree(1000, new MainNode(), new ModbusTestingIPConnection(), new ModbusFuzzNodeAction());
    }
}
