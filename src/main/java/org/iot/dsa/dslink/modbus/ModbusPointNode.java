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
import org.iot.dsa.dslink.modbus.Constants.PointType;
import org.iot.dsa.node.*;
import org.iot.dsa.util.DSException;

import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.BINARY;
import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.CHAR;
import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.VARCHAR;

public class ModbusPointNode extends DFPointNode implements DFNodeValue {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_OBJECT_TYPE, DSJavaEnum.valueOf(PointType.COIL), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.POINT_OFFSET, DSValueType.NUMBER, null, null));
        parameterDefinitions.add(new DataTypeParameter(null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_BIT, DSLong.valueOf(0), "Only applies for Input/Holding Registers with Binary data type", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_REGISTER_COUNT, DSLong.valueOf(0), "Only applies for string data types (Char and Varchar)", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POLL_RATE, DSDouble.valueOf(Constants.DEFAULT_PING_RATE), "polling rate in seconds", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SCALING, DSLong.valueOf(1), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SCALING_OFFSET, DSLong.valueOf(0), null, null));
    }

    @Override
    public DSInfo getNodeValue() {
        return value;
    }

    @Override
    public DSMap getNodeParameters() {
        return parameters;
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
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
        BaseLocator<?> locator = getParentNode().createPointLocator(this);
        try {
            DataTypeEnum dataType = DataTypeEnum.valueOf(parameters.getString(Constants.POINT_DATA_TYPE));

            //Scale value if needed
            if (dataType != BINARY && dataType != CHAR && dataType != VARCHAR) {
                Double newVal = removeScaling(value.toElement().toDouble());
                value = DSDouble.valueOf(newVal);
            }

            //Check if never, set each value separately
            PointType objType = PointType.valueOf(parameters.getString(Constants.POINT_OBJECT_TYPE));
            if (objType.equals(PointType.HOLDING) && neverMultiple()) {
                short[] shorts = new VTSHelper<>(locator).valueToShortsHelper(Util.valueToObject(value, dataType));
                if (shorts.length > 1) {
                    int offset = parameters.getInt(Constants.POINT_OFFSET);
                    for (int i = 0; i < shorts.length; i++) {
                        BaseLocator<?> tempLocator = new NumericLocator(getParentNode().parameters.getInt(Constants.SLAVE_ID),
                                objType.toRange(), offset + i, DataType.TWO_BYTE_INT_SIGNED);
                        getParentNode().getParentNode().master.setValue(tempLocator, shorts[i]);
                    }
                    return;
                }
            }

            getParentNode().getParentNode().master.setValue(locator, Util.valueToObject(value, dataType));
        } catch (ModbusTransportException e) {
            warn(e);
            DSException.throwRuntime(e);
        } catch (ErrorResponseException e) {
            warn(e);
            DSException.throwRuntime(e);
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
        double seconds = Constants.DEFAULT_PING_RATE;
        if (rate != null && rate.isNumber()) {
            seconds = rate.toDouble();
        }
        return (long) (seconds * 1000);
    }
}
