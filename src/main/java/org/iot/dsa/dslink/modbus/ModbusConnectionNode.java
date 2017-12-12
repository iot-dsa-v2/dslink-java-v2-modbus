package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.DFConnectionNode;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public abstract class ModbusConnectionNode extends DFConnectionNode {
    
    public static final String PING_RATE = "Ping Rate";
    
    protected static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(PING_RATE, DSLong.valueOf(DEFAULT_PING_RATE), null, null));
    }
    
    DSMap parameters;
    
    public ModbusConnectionNode() {
        
    }

    public ModbusConnectionNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get("parameters");
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
        } else {
            put("parameters", parameters.copy());
        }
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Device", makeAddDeviceAction());
    }
    
    @Override
    protected void onStable() {
        put("Edit", makeEditAction());
        super.onStable();
    }
    
    private DSAction makeEditAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((ModbusConnectionNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        Util.makeEditParameters(act, parameterDefinitions, parameters);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartNode();
    }
    
    private DSIObject makeAddDeviceAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((ModbusConnectionNode) info.getParent()).addDevice(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, ModbusDeviceNode.parameterDefinitions);
        return act;
    }

    void addDevice(DSMap deviceParameters) {
        String name = deviceParameters.getString("Name");
        ModbusDeviceNode device = new ModbusDeviceNode(deviceParameters);
        put(name, device);
        device.startCarObject();
    }
    
    /* ================================================================== */

    @Override
    public boolean createConnection() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ping() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void closeConnection() {
        // TODO Auto-generated method stub

    }
    
    @Override
    public long getPingRate() {
        DSElement rate = parameters.get(PING_RATE);
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPingRate();
    }

}
