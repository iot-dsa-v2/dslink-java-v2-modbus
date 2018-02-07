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
        FuzzTest.MAX_CON = FuzzTest.MAX_CON * 3;
        FuzzTest.MAX_DEV = FuzzTest.MAX_DEV * 3;
        FuzzTest.MAX_PNT = FuzzTest.MAX_PNT * 3;
        FuzzTest.UNPLUG_DEVICES = false;
        FuzzTest.PING_POLL_RATE = 50;
        FuzzTest.SEED = 42;


        FuzzTest.buildMockTree(500, new ModbusSlaveTestingIPConnection());
        Scanner usrIn = new Scanner(System.in);
        System.out.println("Press Enter to release slaves");
        usrIn.nextLine();
    }

    @Test
    public void buildModbusActionTree() {
        FuzzTest.MAX_CON = FuzzTest.MAX_CON * 3;
        FuzzTest.MAX_DEV = FuzzTest.MAX_DEV * 3;
        FuzzTest.MAX_PNT = FuzzTest.MAX_PNT * 3;
//        FuzzTest.PROB_OFF_CON_STATE = .99;
//        FuzzTest.PROB_OFF_DEV_STATE = .99;
//        FuzzTest.PROB_ON_CON_STATE = .1;
//        FuzzTest.PROB_ON_CON_STATE = .1;

        FuzzTest.SEED = 42;
        FuzzTest.PROB_ACTION = .8;
        //FuzzTest.UNPLUG_DEVICES = false;


        FuzzTest.PING_POLL_RATE = 50;
        FuzzTest.builFuzzDoubleTree(500, new MainNode(), new ModbusSlaveTestingIPConnection(), new ModbusFuzzNodeAction());
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
