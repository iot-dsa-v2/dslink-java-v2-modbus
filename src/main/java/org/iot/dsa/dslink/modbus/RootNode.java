package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class RootNode extends DSRootNode {
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).addIPConnection(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, IPConnectionNode.parameterDefinitions);
        declareDefault("Add IP Connection", act);
        
        act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((RootNode) info.getParent()).addSerialConnection(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, SerialConnectionNode.parameterDefinitions);
        declareDefault("Add Serial Connection", act);
    }
    
    private void addIPConnection(DSMap parameters) {
        String name = parameters.getString("Name");
        put(name, new IPConnectionNode(parameters));
    }
    
    private void addSerialConnection(DSMap parameters) {
        String name = parameters.getString("Name");
        put(name, new SerialConnectionNode(parameters));
    }

}
