package org.iot.dsa.dslink.modbus;

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
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;

public abstract class ModbusConnectionNode extends DFConnectionNode {
    
    protected static void addCommonParameterDefinitions(List<ParameterDefinition> definitions) {
        definitions.add(ParameterDefinition.makeParamWithDefault(Constants.PING_RATE, DSLong.valueOf(DEFAULT_PING_RATE), null, null));
    }
    
    public abstract List<ParameterDefinition> getParameterDefinitions();
    
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
            Util.verifyParameters(parameters, getParameterDefinitions());
        } else {
            Util.verifyParameters(parameters, getParameterDefinitions());
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
        Util.makeEditParameters(act, getParameterDefinitions(), parameters);
        return act;
    }
    
    private void edit(DSMap newParameters) {
        Util.verifyParameters(newParameters, getParameterDefinitions());
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
    
    /* ==================================================================== */
    ModbusMaster master;
    ModbusFactory modbusFactory = new ModbusFactory();
    
    @Override
    public boolean createConnection() {
        
        //master.setTimeout(timeout);
        //master.setRetries(retries);
        // TODO etc.
        
        try {
            master.init();
            return master.isInitialized();
        } catch (ModbusInitException e) {
            warn(e);
            return false;
        }
    }

    

    @Override
    public boolean ping() {
        return master.isInitialized();
    }

    @Override
    public void closeConnection() {
        master.destroy();
        master = null;
    }
    
    @Override
    public long getPingRate() {
        DSElement rate = parameters.get(Constants.PING_RATE);
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPingRate();
    }

}
