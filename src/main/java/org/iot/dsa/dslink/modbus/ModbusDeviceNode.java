package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.Constants.ObjectType;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;

public class ModbusDeviceNode extends DFDeviceNode {
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.SLAVE_ID, DSValueType.NUMBER, null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.PING_RATE, DSLong.valueOf(DEFAULT_PING_RATE), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.CONTIGUOUS_READS, DSBool.FALSE, null, null));
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
            Util.verifyParameters(parameters, parameterDefinitions);
        } else {
            Util.verifyParameters(parameters, parameterDefinitions);
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
        Util.verifyParameters(newParameters, parameterDefinitions);
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
        int slaveId = parameters.getInt(Constants.SLAVE_ID);
        return getParentNode().master.testSlaveNode(slaveId);
    }

    @Override
    public boolean ping() {
        int slaveId = parameters.getInt(Constants.SLAVE_ID);
        return getParentNode().master.testSlaveNode(slaveId);
    }

    @Override
    public void closeConnection() {
        // TODO Auto-generated method stub
    }

    BaseLocator<?> createPointLocator(ModbusPointNode point) {
        int slaveId = parameters.getInt(Constants.SLAVE_ID);
        return createPointLocator(slaveId, point);
    }

    BaseLocator<?> createPointLocator(int slaveId, ModbusPointNode point) {
        ObjectType objType = ObjectType.valueOf(point.parameters.getString(Constants.POINT_OBJECT_TYPE));
        int offset = point.parameters.getInt(Constants.POINT_OFFSET);
        DataTypeEnum dataType = DataTypeEnum.valueOf(point.parameters.getString(Constants.POINT_DATA_TYPE));
        int bit = point.parameters.getInt(Constants.POINT_BIT);
        int registerCount = point.parameters.getInt(Constants.POINT_REGISTER_COUNT);
        return BaseLocator.createLocator(slaveId, objType.toRange(), offset, dataType.toId(), bit, registerCount);
    }

    @Override
    public boolean batchPoll(Set<DFPointNode> points) {
        int slaveId = parameters.getInt(Constants.SLAVE_ID);
        boolean contig = parameters.getBoolean(Constants.CONTIGUOUS_READS);
        BatchRead<ModbusPointNode> batch = new BatchRead<ModbusPointNode>();
        batch.setContiguousRequests(contig);
        batch.setErrorsInResults(true);
//        batch.setExceptionsInResults(exceptionsInResults);
        
        for (DFPointNode point: points) {
            ModbusPointNode mpoint = (ModbusPointNode) point;
            BaseLocator<?> locator = createPointLocator(slaveId, mpoint);
            batch.addLocator(mpoint, locator);
        }
        
        try {
            BatchResults<ModbusPointNode> results = getParentNode().master.send(batch);
            for (DFPointNode point: points) {
                ModbusPointNode mpoint = (ModbusPointNode) point;
                DataTypeEnum dataType = DataTypeEnum.valueOf(mpoint.parameters.getString(Constants.POINT_DATA_TYPE));
                DSElement val;
                Object result = results.getValue(mpoint);
                if (result instanceof ExceptionResult) {
                    mpoint.updateError((ExceptionResult) result);
                } else {
                    switch(dataType) {
                        case BINARY:
                            val = DSBool.valueOf((Boolean) result);
                            break;
                        case CHAR:
                        case VARCHAR:
                            val = DSString.valueOf((String) result);
                            break;
                        default:
                            Double raw = ((Number) result).doubleValue();
                            Double scaled = mpoint.applyScaling(raw);
                            val = DSDouble.valueOf(scaled);
                            break;
                    }
                    mpoint.updateValue(val);
                }
            }
            return true;
        } catch (ModbusTransportException e) {
            warn(e);
            return false;
        } catch (ErrorResponseException e) {
            warn(e);
            return false;
        }
    }
    
    @Override
    public long getPingRate() {
        DSElement rate = parameters.get(Constants.PING_RATE);
        if (rate != null && rate.isNumber()) {
            return rate.toLong();
        }
        return super.getPingRate();
    }
    
    ModbusConnectionNode getParentNode() {
        DSNode parent = getParent();
        if (parent instanceof ModbusConnectionNode) {
            return (ModbusConnectionNode) parent;
        } else {
            throw new RuntimeException("Wrong parent class");
        }
    }

}
