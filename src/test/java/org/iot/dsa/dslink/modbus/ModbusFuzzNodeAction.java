package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.dslink.dftest.FuzzNodeActionContainer;
import org.iot.dsa.dslink.dftest.FuzzTest;
import org.iot.dsa.dslink.modbus.slave.ModbusMockSlaveDeviceParameters;
import org.iot.dsa.dslink.modbus.slave.ModbusMockSlaveIPParameters;
import org.iot.dsa.dslink.modbus.slave.ModbusMockSlavePointParameters;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/29/2018
 */
public class ModbusFuzzNodeAction extends FuzzNodeActionContainer {

    @Override
    public String invokeAction(DSInfo actionInfo, Random rand) {
        String name = actionInfo.getName();
        DSNode parent = actionInfo.getParent();
        String path = parent.getPath();
        path = path.endsWith("/") ? path + name : path + "/" + name;
        DSMap params;
        if (name.equals(Constants.ACTION_ADD_IP)) {
            params = new ModbusMockIPConnectionParameters(rand).getParamMap();
            String c = addConnectionHelper(parent, params);
            params.put(Constants.IP_HOST, "localhost");
            params.put(Constants.NAME, c).put(Constants.PING_RATE, getFuzzPingRateSec());
        } else if (name.equals(Constants.ACTION_ADD_DEVICE)) {
            params = new ModbusMockDeviceParameters(rand).getParamMap();
            String d = addDeviceHelper(parent, params);
            params.put(Constants.NAME, d).put(Constants.PING_RATE, getFuzzPingRateSec());
        } else if (name.equals(Constants.ACTION_ADD_POINT)) {
            params = new ModbusMockPointParameters(rand).getParamMap();
            String p = addPintHelper(parent, params);
            //Scale only half the values
            if (rand.nextInt(2) == 1) {
                params.put(Constants.SCALING, DSLong.valueOf(1)).put(Constants.SCALING_OFFSET, DSLong.valueOf(0));
            }
            params.put(Constants.NAME, p).put(Constants.POLL_RATE, getFuzzPingRateSec());
        } else if (name.equals(DFHelpers.REMOVE)) {
            if (parent instanceof ModbusConnectionNode) {
                removeConnectionHelper(parent);
            } else if (parent instanceof ModbusDeviceNode) {
                removeDeviceHelper(parent);
            } else if (parent instanceof ModbusPointNode) {
                removePointHelper(parent.getInfo());
            } else {
                throw new RuntimeException("Trying to remove a node of the wrong class.");
            }
            params = new DSMap();
        } else if (name.equals(DFHelpers.START)) {
            if (rand.nextDouble() < FuzzTest.PROB_ON_CON_STATE) {
                params = new DSMap();
            } else {
                return "Skipping START Action";
            }
        } else if (name.equals(DFHelpers.STOP)) {
            if (rand.nextDouble() < FuzzTest.PROB_OFF_CON_STATE) {
                params = new DSMap();
            } else {
                return "Skipping STOP Action";
            }
        }
        //TODO: do we need to test salve point nodes? Implement here.
        /*
        else if (name.equals(Constants.ACTION_ADD_SLAVE_POINT)) {
            params = new ModbusMockSlavePointParameters(rand).getParamMap();
            String p = addPintHelper(parent);
            params.put(Constants.NAME, p);
        } else if (name.equals(Constants.ACTION_ADD_SLAVE)) {
            params = new ModbusMockSlaveDeviceParameters(rand).getParamMap();
            String d = addDeviceHelper(parent);
            params.put(Constants.NAME, d);
        } else if (name.equals(Constants.ACTION_ADD_IP_SLAVE)) {
            params = new ModbusMockSlaveIPParameters(rand).getParamMap();
            String c = addConnectionHelper(parent);
            params.put(Constants.NAME, c);
        }
        */ else {
            return "Action not implemented: " + name;
        }
        FuzzTest.requester.invoke(path, params, new FuzzTest.InvokeHandlerImpl());
        return "Invoking " + path + " with parameters " + params;
    }
}
