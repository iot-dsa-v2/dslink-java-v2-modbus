package org.iot.dsa.dslink.modbus;

import org.apache.commons.lang3.StringUtils;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.dslink.dftest.FuzzTest;
import org.iot.dsa.dslink.modbus.slave.ModbusSlaveTestingIPConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.fail;

/**
 * @author James (Juris) Puchin
 * Created on 1/25/2018
 */
public class ModbusFuzzTest {

    private static String OUTPUT_FILE_NAME = "modbus_output.txt";
    private static String MASTER_FILE_NAME = "modbus_master.txt";
    private static boolean REDO_FUZZ = false; //Set to false to prevent re-running the test
    private static long TEST_LENGTH = 3000;

    @Before
    public void setUp() {
        if (REDO_FUZZ) {
            PrintWriter writer = FuzzTest.getNewPrintWriter(OUTPUT_FILE_NAME);
            FuzzTest.builFuzzDoubleTree(TEST_LENGTH, writer, new MainNode(), new ModbusSlaveTestingIPConnection(), new ModbusFuzzNodeAction());
            writer.close();
            REDO_FUZZ = false;
        }
    }

    /**
     * Checks whether the output file is an exact match to the golden output.
     *
     * @throws IOException Failed to find the required inputs/outputs
     */
    @Test
    public void exactMatchTest() throws IOException {
        FuzzTest.performDiff(OUTPUT_FILE_NAME, MASTER_FILE_NAME);
    }

    /**
     * This tests whether the python testing framework is working correctly
     */
    @Test
    public void pythonFrameworkTest() throws Exception {
        String t_name = "helloo_world.py";
        FuzzTest.runPythonTest(t_name);
    }

    /**
     * Checks that, at any point in time, if a point is not "Stopped", then it was subscribed
     * to at some point in the past, and was not unsubscribed from since then.
     */
    @Test
    public void connected_was_subbed() throws Exception {
        String t_name = "connected_was_subbed.py";
        FuzzTest.runPythonTest(t_name);
    }

    /**
     * Checks that whenever a point or device node is "Connected" or "Failed", its parent is "Connected"
     */
    @Test
    public void parent_connected() throws Exception {
        String t_name = "parent_connected.py";
        FuzzTest.runPythonTest(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point exists on the device, then the node's status is "Connected" and its value
     * is the same as the value of the point on the device
     */
    @Test
    public void subbed_is_connected() throws Exception {
        String t_name = "subbed_is_connected.py";
        FuzzTest.runPythonTest(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point doesn't exist on the device, then the node's status is "Failed"
     */
    @Test
    public void subbed_is_failed() throws Exception {
        String t_name = "subbed_is_failed.py";
        FuzzTest.runPythonTest(t_name);
    }

    /**
     * After a point node is unsubscribed from, checks that if its parent is "Connected", then the
     * node's status is "Stopped"
     */
    @Test
    public void unsubbed_is_stopped() throws Exception {
        String t_name = "unsubbed_is_stopped.py";
        FuzzTest.runPythonTest(t_name);
    }

    /**
     * Checks that active ("Connected") point nodes update their values correctly
     */
    @Test
    public void value_updates() throws Exception {
        String t_name = "value_updates.py";
        FuzzTest.runPythonTest(t_name);
    }

    //@Test
    public void buildModbusMockTreeTest() {
        FuzzTest.buildMockTree(100, new ModbusTestingIPConnection());
    }

    //@Test
    public void buildModbusMockSlaveTreeTest() {
        FuzzTest.MAX_CON = FuzzTest.MAX_CON * 3;
        FuzzTest.MAX_DEV = FuzzTest.MAX_DEV * 3;
        FuzzTest.MAX_PNT = FuzzTest.MAX_PNT * 3;
        FuzzTest.UNPLUG_DEVICES = false;
        FuzzTest.PING_POLL_RATE = 50;


        FuzzTest.buildMockTree(500, new ModbusSlaveTestingIPConnection());
        Scanner usrIn = new Scanner(System.in);
        System.out.println("Press Enter to release slaves");
        usrIn.nextLine();
    }

    //@Test
    public void buildModbusActionTree() {
 //       FuzzTest.MAX_CON = FuzzTest.MAX_CON * 3;
 //       FuzzTest.MAX_DEV = FuzzTest.MAX_DEV * 3;
//        FuzzTest.MAX_PNT = FuzzTest.MAX_PNT * 3;
//        FuzzTest.PROB_OFF_CON_STATE = .99;
//        FuzzTest.PROB_OFF_DEV_STATE = .99;
//        FuzzTest.PROB_ON_CON_STATE = .1;
//        FuzzTest.PROB_ON_CON_STATE = .1;

        FuzzTest.PROB_ACTION = .8;
        //FuzzTest.UNPLUG_DEVICES = false;


        FuzzTest.PING_POLL_RATE = 50;
        FuzzTest.builFuzzDoubleTree(500, null, new MainNode(), new ModbusSlaveTestingIPConnection(), new ModbusFuzzNodeAction());
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
