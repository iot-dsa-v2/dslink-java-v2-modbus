package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.Constants.ObjectType;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

public class ModbusPointNode extends DFPointNode implements DSIValue {
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_OBJECT_TYPE, DSJavaEnum.valueOf(ObjectType.COIL), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.POINT_OFFSET, DSValueType.NUMBER, null, null));
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_DATA_TYPE, DSJavaEnum.valueOf(DataTypeEnum.BINARY), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_BIT, DSLong.valueOf(0), "Only applies for Input/Holding Registers with Binary data type", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_REGISTER_COUNT, DSLong.valueOf(0), "Only applies for string data types (Char and Varchar)", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POLL_RATE, DSLong.valueOf(ModbusDeviceNode.DEFAULT_PING_RATE), null, null));
    }
    
    DSMap parameters;
    private DSInfo value = getInfo("Value");
    
    public ModbusPointNode() {
        
    }

    public ModbusPointNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get("parameters");
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
            Util.verifyParameters(parameters, parameterDefinitions);
        } else {
            Util.verifyParameters(parameters, parameterDefinitions);
            put("parameters", parameters.copy());
        }
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Value", DSString.EMPTY);
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
                ((ModbusPointNode) info.getParent()).edit(invocation.getParameters());
                return null;
            }
        };
        Util.makeEditParameters(act, parameterDefinitions, parameters);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        Util.verifyParameters(newParameters, parameterDefinitions);
        this.parameters = newParameters;
        put("parameters", parameters.copy());
        put("Edit", makeEditAction());
        restartNode();
    }
    

    @Override
    public DSValueType getValueType() {
        DataTypeEnum dataType = DataTypeEnum.valueOf(parameters.getString(Constants.POINT_DATA_TYPE));
        switch(dataType) {
            case BINARY:
                return DSValueType.BOOL;
            case CHAR:
            case VARCHAR:
                return DSValueType.STRING;
            default:
                return DSValueType.NUMBER;
        }
    }

    @Override
    public DSElement toElement() {
        return value.getValue().toElement();
    }

    @Override
    public DSIValue valueOf(DSElement element) {
        return value.getValue().valueOf(element);
    }
    
    void updateValue(DSElement val) {
        put(value, val);
        getParent().childChanged(getInfo());
    }
    
    /* ================================================================== */
    
    @Override
    public long getPollRate() {
        DSElement rate = parameters.get(Constants.POLL_RATE);
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPollRate();
    }

}
