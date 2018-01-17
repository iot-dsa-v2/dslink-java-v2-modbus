package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.dslink.dframework.DFHelpers;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.modbus.slave.IpSlaveConnectionNode;
import org.iot.dsa.dslink.modbus.slave.SerialSlaveConnectionNode;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class RootNode extends DSRootNode {
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACTION_ADD_IP, DFUtil.getAddAction(IPConnectionNode.class));
        declareDefault(Constants.ACTION_ADD_SERIAL, DFUtil.getAddAction(SerialConnectionNode.class));
        declareDefault(Constants.ACTION_RESCAN_PORTS, getRescanAction());
        declareDefault(Constants.ACTION_ADD_IP_SLAVES, DFUtil.getAddAction(IpSlaveConnectionNode.class));
        declareDefault(Constants.ACTION_ADD_SERIAL_SLAVES, DFUtil.getAddAction(SerialSlaveConnectionNode.class));
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

    private void rescanSerialPorts() {
        put(Constants.ACTION_ADD_SERIAL, DFUtil.getAddAction(SerialConnectionNode.class));
        put(Constants.ACTION_ADD_SERIAL_SLAVES, DFUtil.getAddAction(SerialSlaveConnectionNode.class));
        for (DSInfo info: this) {
            if (info.isNode()) {
                DSNode n = info.getNode();
                if (n instanceof SerialConnectionNode) {
                    SerialConnectionNode scn = (SerialConnectionNode) n;
                    scn.put(DFHelpers.ACTION_EDIT, scn.makeEditAction());
                } else if (n instanceof SerialSlaveConnectionNode) {
                    SerialSlaveConnectionNode sscn = (SerialSlaveConnectionNode) n;
                    sscn.put(DFHelpers.ACTION_EDIT, sscn.makeEditAction());
                }
            }
        }
    }
}
