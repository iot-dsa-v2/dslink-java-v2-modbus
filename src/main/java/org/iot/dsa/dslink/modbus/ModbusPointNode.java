package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.locator.NumericLocator;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.Constants.MultipleWriteEnum;
import org.iot.dsa.dslink.modbus.Constants.ObjectType;
import org.iot.dsa.node.*;
import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.BINARY;
import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.CHAR;
import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.VARCHAR;

public class ModbusPointNode extends DFPointNode implements DSIValue {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_OBJECT_TYPE, DSJavaEnum.valueOf(ObjectType.COIL), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.POINT_OFFSET, DSValueType.NUMBER, null, null));
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_DATA_TYPE, DSJavaEnum.valueOf(BINARY), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_BIT, DSLong.valueOf(0), "Only applies for Input/Holding Registers with Binary data type", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_REGISTER_COUNT, DSLong.valueOf(0), "Only applies for string data types (Char and Varchar)", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POLL_RATE, DSLong.valueOf(ModbusDeviceNode.DEFAULT_PING_RATE), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SCALING, DSLong.valueOf(1), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SCALING_OFFSET, DSLong.valueOf(0), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    @Override
    public void addNewInstance(DSNode parent, DSMap newParameters) {
        String name = newParameters.getString(Constants.NAME);
        ModbusPointNode point = new ModbusPointNode(newParameters);
        parent.put(name, point);
    }
    
    private DSInfo value = getInfo(Constants.POINT_VALUE);
    private DSInfo error = getInfo(Constants.POINT_ERROR);

    private Long getPointScaling() {
        Long ans = parameters.get(Constants.SCALING).toLong();
        if (ans == 0) throw new RuntimeException("Zero is not a valid scaling factor.");
        return ans;
    }

    private Long getPointOffset() {
        return parameters.get(Constants.SCALING_OFFSET).toLong();
    }
    Double applyScaling(Double val) {
        return val * getPointScaling() + getPointOffset();
    }

    Double removeScaling(Double val) {
        return (val - getPointOffset()) / getPointScaling();
    }
    
    public ModbusPointNode() {
        
    }

    public ModbusPointNode(DSMap parameters) {
        this.parameters = parameters;
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.POINT_VALUE, DSString.EMPTY);
        declareDefault(Constants.POINT_ERROR, DSString.EMPTY).setHidden(true).setReadOnly(true);
    }

    @Override
    public void onSet(DSIValue value) {
        //TODO: move implementation to Device?
        BaseLocator<?> locator = getParentNode().createPointLocator(this);
        try {
            DataTypeEnum dataType = DataTypeEnum.valueOf(parameters.getString(Constants.POINT_DATA_TYPE));

            //Scale value if needed
            if (dataType != BINARY && dataType != CHAR && dataType != VARCHAR) {
                Double newVal = removeScaling(value.toElement().toDouble());
                value = DSDouble.valueOf(newVal);
            }

            //Check if never, set each value separately
            ObjectType objType = ObjectType.valueOf(parameters.getString(Constants.POINT_OBJECT_TYPE));
            if (objType.equals(ObjectType.HOLDING) && neverMultiple()) {
                short[] shorts = new VTSHelper<>(locator).valueToShortsHelper(Util.valueToObject(value, dataType));
                if (shorts.length > 1) {
                    int offset = parameters.getInt(Constants.POINT_OFFSET);
                    for (int i = 0; i < shorts.length; i++) {
                        BaseLocator<?> tempLocator = new NumericLocator(getParentNode().parameters.getInt(Constants.SLAVE_ID), objType.toRange(), offset + i, DataType.TWO_BYTE_INT_SIGNED);
                        getParentNode().getParentNode().master.setValue(tempLocator, shorts[i]);
                    }
                    return;
                }
            }

            getParentNode().getParentNode().master.setValue(locator, Util.valueToObject(value, dataType));
        } catch (ModbusTransportException e) {
            warn(e);
        } catch (ErrorResponseException e) {
            warn(e);
        }
    }

    private boolean neverMultiple() {
        MultipleWriteEnum option = MultipleWriteEnum.valueOf(getParentNode().getParentNode().parameters.getString(Constants.USE_MULTIPLE_WRITE_COMMAND));
        return MultipleWriteEnum.NEVER.equals(option);
    }

    private class VTSHelper <T> {
        BaseLocator <T> loc;

        VTSHelper (BaseLocator<T> locator) {
            this.loc = locator;
        }

        @SuppressWarnings("unchecked")
        private short[] valueToShortsHelper (Object obj) {
            return loc.valueToShorts((T) obj);
        }
    }

    @Override
    public void onSet(DSInfo info, DSIValue value) {
        //String name = info.getName();
        if (this.value.equals(info)) {
            onSet(value);
        }
    }

    @Override
    protected void onStable() {
        super.onStable();
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
        error.setHidden(true);
        put(error, DSString.EMPTY);
        put(value, val);
        getParent().childChanged(getInfo());
    }
    
    void updateError(ExceptionResult resp) {
        error.setHidden(false);
        put(error, DSString.valueOf(resp.getExceptionMessage()));
    }

    ModbusDeviceNode getParentNode() {
        DSNode parent = getParent();
        if (parent instanceof ModbusDeviceNode) {
            return (ModbusDeviceNode) parent;
        } else {
            throw new RuntimeException("Wrong parent class");
        }
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
