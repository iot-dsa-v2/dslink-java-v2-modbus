package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dftest.FuzzTest;
import org.iot.dsa.dslink.modbus.slave.ModbusSlaveTestingIPConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author James (Juris) Puchin
 * Created on 1/25/2018
 */
public class ModbusFuzzTest {

    private static boolean REDO_FUZZ = false; //Set to false to prevent re-running the test
    static double PROB_SCALING = 0.5; //Set to high probability to have lots of points with scaling
    private static long TEST_LENGTH = 800;

    @Before
    public void setUp() {
        if (REDO_FUZZ) {

//            FuzzTest.PROB_ON_CON_STATE = .8;
//            FuzzTest.PROB_OFF_CON_STATE = .2;

//            FuzzTest.MAX_CON = FuzzTest.MAX_CON * 3;
//            FuzzTest.MAX_DEV = FuzzTest.MAX_DEV * 3;
//            FuzzTest.MAX_PNT = FuzzTest.MAX_PNT * 3;
//            FuzzTest.MIN_CON = FuzzTest.MIN_CON * 3;
//            FuzzTest.MIN_DEV = FuzzTest.MIN_DEV * 3;
//            FuzzTest.MIN_PNT = FuzzTest.MIN_PNT * 3;
            FuzzTest.UNPLUG_DEVICES = false;

            FuzzTest.SUBSCRIBE_DELAY_RETRIES = 100;
            FuzzTest.SUBSCRIBE_DELAY_WAIT_MILIS = 300;
            FuzzTest.PING_POLL_RATE = 10;
            FuzzTest.INTERSTEP_WAIT_TIME = 1500;
            PrintWriter writer = FuzzTest.getNewPrintWriter(FuzzTest.TESTING_OUT_FILENAME);
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
    //@Test
    public void exactMatchTest() throws IOException {
        FuzzTest.performDiff(FuzzTest.MASTER_OUT_FILENAME, FuzzTest.TESTING_OUT_FILENAME);
    }

    /**
     * This tests whether the python testing framework is working correctly
     */
    @Test
    public void pythonFrameworkTest() throws Exception {
        new FuzzTest().pythonFrameworkTest();
//        String t_name = "helloo_world.py";
//        FuzzTest.runPythonTest(t_name);
    }

    /**
     * Checks that, at any point in time, if a point is not "Stopped", then it was subscribed
     * to at some point in the past, and was not unsubscribed from since then.
     */
    @Test
    public void connected_was_subbed() throws Exception {
        new FuzzTest().connected_was_subbed();
//        String t_name = "connected_was_subbed.py";
//        FuzzTest.runPythonTestFromDir(t_name);
    }

    /**
     * Checks that whenever a point or device node is "Connected" or "Failed", its parent is "Connected"
     */
    @Test
    public void parent_connected() throws Exception {
        new FuzzTest().parent_connected();
//        String t_name = "parent_connected.py";
//        FuzzTest.runPythonTest(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point exists on the device, then the node's status is "Connected" and its value
     * is the same as the value of the point on the device
     */
    @Test
    public void subbed_is_connected() throws Exception {
//        new FuzzTest().subbed_is_connected();
        String t_name = "subbed_is_connected_scaling.py";
        FuzzTest.runPythonTestFromDir(t_name);
    }

    /**
     * After a point node is subscribed to, checks that if its parent is "Connected" and the
     * corresponding point doesn't exist on the device, then the node's status is "Failed"
     */
    //@Test
    //TODO: REMOVE or REWRITE this test
    public void subbed_is_failed() throws Exception {
        new FuzzTest().subbed_is_failed();
//        String t_name = "subbed_is_failed.py";
//        FuzzTest.runPythonTest(t_name);
    }

    /**
     * After a point node is unsubscribed from, checks that if its parent is "Connected", then the
     * node's status is "Stopped"
     */
    @Test
    public void unsubbed_is_stopped() throws Exception {
        new FuzzTest().unsubbed_is_stopped();
//        String t_name = "unsubbed_is_stopped.py";
//        FuzzTest.runPythonTest(t_name);
    }

    /**
     * Checks that active ("Connected") point nodes update their values correctly
     */
    @Test
    public void value_updates() throws Exception {
//        new FuzzTest().value_updates();
        String t_name = "value_updates_scaling.py";
        FuzzTest.runPythonTestFromDir(t_name);
    }

    /**
     * Checks that a new node appears after the add action is called.
     *
     * @throws Exception
     */
    @Test
    public void add_works() throws Exception {
        new FuzzTest().add_works();
    }

    /**
     * Checks that the right node disappears after the add action is called.
     *
     * @throws Exception
     */
    @Test
    public void remove_works() throws Exception {
        new FuzzTest().remove_works();
    }

    /**
     * Checks that the right node is stopped/started after the right action is called.
     *
     * @throws Exception
     */
    @Test
    public void stop_start_works() throws Exception {
        new FuzzTest().stop_start_works();
    }

    @Test
    public void all_subscriptions_work() throws Exception {
        new FuzzTest().all_subscriptions_work();
    }

    //@Test
    public void buildModbusMockTreeTest() {
        FuzzTest.buildMockTree(100, new ModbusTestingIPConnection());
    }

    //@Test
    public void buildModbusMockSlaveTreeTest() {
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
