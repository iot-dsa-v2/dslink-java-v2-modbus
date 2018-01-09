package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class RootNode extends DSRootNode {
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACTION_ADD_IP, getAddIpConnectionAction());
        declareDefault(Constants.ACTION_ADD_SERIAL, getAddSerialConnectionAction());
        declareDefault(Constants.ACTION_RESCAN_PORTS, getRescanAction());
        declareDefault(Constants.ACTION_ADD_SLAVE, getAddSlaveDeviceAction());
    }

    private DSAction getAddSlaveDeviceAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).addSlaveConnection(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, SlaveDeviceNode.parameterDefinitions);
        return act;
    }

    private DSAction getAddIpConnectionAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).addIPConnection(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, IPConnectionNode.parameterDefinitions);
        return act;
    }

    private DSAction getAddSerialConnectionAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).addSerialConnection(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, SerialConnectionNode.parameterDefinitions);
        return act;
    }

    private DSAction getRescanAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).rescanSerialPorts();
                return null;
            }
        };
        return act;
    }
    
    private void addIPConnection(DSMap parameters) {
        String name = parameters.getString(Constants.NAME);
        put(name, new IPConnectionNode(parameters));
    }
    
    private void addSerialConnection(DSMap parameters) {
        String name = parameters.getString(Constants.NAME);
        put(name, new SerialConnectionNode(parameters));
    }

    private void rescanSerialPorts() {
        put(Constants.ACTION_ADD_SERIAL, getAddSerialConnectionAction());
        for (DSInfo info: this) {
            if (info.isNode()) {
                DSNode n = info.getNode();
                if (n instanceof SerialConnectionNode) {
                    SerialConnectionNode scn = (SerialConnectionNode) n;
                    scn.put(Constants.ACTION_EDIT, scn.makeEditAction());
                }
            }
        }
    }

    private void addSlaveConnection(DSMap parameters) {
        String name = parameters.getString("Name");
        put(name, new SlaveDeviceNode(parameters));
    }

}
