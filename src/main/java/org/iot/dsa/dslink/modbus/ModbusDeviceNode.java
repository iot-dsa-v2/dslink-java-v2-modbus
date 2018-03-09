package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.iot.dsa.dslink.dframework.DFDeviceNode;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.BooleanBounds;
import org.iot.dsa.dslink.dframework.bounds.DoubleBounds;
import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.dslink.modbus.utils.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.utils.Constants.PointType;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ReportSlaveIdRequest;

public class ModbusDeviceNode extends DFDeviceNode {
    
    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeParamWithBounds(Constants.SLAVE_ID, DSValueType.NUMBER, new IntegerBounds(1, 247), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.PING_RATE, DSDouble.valueOf(Constants.DEFAULT_PING_RATE), new DoubleBounds(.001, Double.MAX_VALUE),"interval between pings, in seconds", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.CONTIGUOUS_READS, DSBool.FALSE, new BooleanBounds(), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }    
    
    public ModbusDeviceNode() {
        
    }

    public ModbusDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACTION_ADD_POINT, DFUtil.getAddAction(ModbusPointNode.class));
    }
    
    @Override
    protected void onStable() {
        super.onStable();
    }

    void addPoint(DSMap pointParameters) {
        String name = pointParameters.getString(Constants.NAME);
        ModbusPointNode point = new ModbusPointNode(pointParameters);
        put(name, point);
    }
    
    /* ================================================================== */
    

    @Override
    public boolean createConnection() {
        return getParentNode().modbus != null && ping();
    }

    @Override
    public boolean ping() {
        int slaveId = parameters.getInt(Constants.SLAVE_ID);
        
        info(Thread.currentThread().getId() + ") Ping Start: " + slaveId);

        
        try {
            getParentNode().modbus.send(new ReportSlaveIdRequest(slaveId), this, slaveId);
        } catch (ModbusTransportException e) {
            info(Thread.currentThread().getId() + ") Ping End (Fail): " + slaveId);
            return false;
        }
        info(Thread.currentThread().getId() + ") Ping End (Success): " + slaveId);
        return true;
        
        
        //return getParentNode().master.testSlaveNode(slaveId);
    }

    @Override
    public void closeConnection() {
        //Do nothing
    }

    BaseLocator<?> createPointLocator(ModbusPointNode point) {
        int slaveId = parameters.getInt(Constants.SLAVE_ID);
        return createPointLocator(slaveId, point);
    }

    BaseLocator<?> createPointLocator(int slaveId, ModbusPointNode point) {
        PointType objType = PointType.valueOf(point.parameters.getString(Constants.POINT_OBJECT_TYPE));
        int offset = point.parameters.getInt(Constants.POINT_OFFSET);
        DataTypeEnum dataType = DataTypeEnum.valueOf(point.parameters.getString(Constants.POINT_DATA_TYPE));
        int bit = point.parameters.getInt(Constants.POINT_BIT);
        int registerCount = point.parameters.getInt(Constants.POINT_REGISTER_COUNT);
        return BaseLocator.createLocator(slaveId, objType.toRange(), offset, dataType.toId(), bit, registerCount);
    }

    @Override
    public Map<DFPointNode, Boolean> batchPoll(Set<DFPointNode> points) {
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
        
        
        Map<DFPointNode, Boolean> successes = new ConcurrentHashMap<DFPointNode, Boolean>();
        try {
            info(Thread.currentThread().getId() + ") Poll Start: " + slaveId);
            BatchResults<ModbusPointNode> results = getParentNode().modbus.send(batch, this, slaveId);
            info(Thread.currentThread().getId() + ") Poll End: " + slaveId);
            for (DFPointNode point: points) {
                ModbusPointNode mpoint = (ModbusPointNode) point;
                DataTypeEnum dataType = DataTypeEnum.valueOf(mpoint.parameters.getString(Constants.POINT_DATA_TYPE));
                DSElement val;
                Object result = results.getValue(mpoint);
                if (result == null) {
                    successes.put(mpoint, false);
                } else if (result instanceof ExceptionResult) {
                    mpoint.updateError((ExceptionResult) result);
                    successes.put(mpoint, false);
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
                    successes.put(mpoint, true);
                }
            }
        } catch (ModbusTransportException e) {
            warn(e);
            info(Thread.currentThread().getId() + ") Poll End: " + slaveId);
        } catch (ErrorResponseException e) {
            warn(e);
            info(Thread.currentThread().getId() + ") Poll End: " + slaveId);
        }
        return successes;
    }
    
    @Override
    public long getPingRate() {
        DSElement rate = parameters.get(Constants.PING_RATE);
        double seconds = Constants.DEFAULT_PING_RATE;
        if (rate != null && rate.isNumber()) {
            seconds = rate.toDouble();
        }
        return (long) (seconds * 1000);
    }
    
    ModbusConnectionNode getParentNode() {
        DSNode parent = getParent();
        if (parent instanceof ModbusConnectionNode) {
            return (ModbusConnectionNode) parent;
        } else {
            throw new RuntimeException("Wrong parent class, expected ModbusConnectionNode");
        }
    }
}
