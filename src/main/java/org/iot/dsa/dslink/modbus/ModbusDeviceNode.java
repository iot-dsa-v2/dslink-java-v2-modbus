package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class ModbusDeviceNode extends DFDeviceNode {

    public static final String PING_RATE = "Ping Rate";
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        //TODO add Modbus Device parameters here
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(PING_RATE, DSLong.valueOf(DEFAULT_PING_RATE), null, null));
        //parameterDefinitions.add(ParameterDefinition.makeParam(name, type, description, placeholder))
    }
    
    DSMap parameters;
    
    public ModbusDeviceNode() {
        
    }

    public ModbusDeviceNode(DSMap parameters) {
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
        declareDefault("Add Point", makeAddPointAction());
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
                ((ModbusDeviceNode) info.getParent()).edit(invocation.getParameters());
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
    
    private DSAction makeAddPointAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((ModbusDeviceNode) info.getParent()).addPoint(invocation.getParameters());
                return null;
            }
        };
        Util.makeAddParameters(act, ModbusPointNode.parameterDefinitions);
        return act;
    }

    void addPoint(DSMap pointParameters) {
        String name = pointParameters.getString("Name");
        ModbusPointNode point = new ModbusPointNode(pointParameters);
        put(name, point);
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
    public boolean batchPoll(Set<DFPointNode> points) {
        // TODO Auto-generated method stub
        return false;
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
