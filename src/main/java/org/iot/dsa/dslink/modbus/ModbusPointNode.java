package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.locator.NumericLocator;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.DoubleBounds;
import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.dslink.dframework.bounds.LongBounds;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.dslink.modbus.utils.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.utils.Constants.MultipleWriteEnum;
import org.iot.dsa.dslink.modbus.utils.Constants.PointType;
import org.iot.dsa.dslink.modbus.utils.DataTypeParameter;
import org.iot.dsa.dslink.modbus.utils.Util;
import org.iot.dsa.node.*;
import org.iot.dsa.util.DSException;

import java.util.ArrayList;
import java.util.List;

import static org.iot.dsa.dslink.modbus.utils.Constants.DataTypeEnum.*;

public class ModbusPointNode extends DFPointNode implements DSIValue {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_OBJECT_TYPE, DSJavaEnum.valueOf(PointType.COIL), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBounds(Constants.POINT_OFFSET, DSValueType.NUMBER, new LongBounds(), null, null));
        parameterDefinitions.add(new DataTypeParameter(null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.POINT_BIT, DSLong.valueOf(0), new IntegerBounds(0, 15), "Only applies for Input/Holding Registers with Binary data type", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.POINT_REGISTER_COUNT, DSLong.valueOf(0), new IntegerBounds(0, Constants.UNSIGED_SHORT_MAX), "Only applies for string data types (Char and Varchar)", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.POLL_RATE, DSDouble.valueOf(Constants.DEFAULT_PING_RATE), new DoubleBounds(0.1, Double.MAX_VALUE), "polling rate in seconds", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.SCALING, DSDouble.valueOf(1), new DoubleBounds(), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.SCALING_OFFSET, DSDouble.valueOf(0), new DoubleBounds(), null, null));
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    private DSInfo value = getInfo(Constants.POINT_VALUE);
    private DSInfo error = getInfo(Constants.POINT_ERROR);

    private Double getPointScaling() {
        Double ans = parameters.getDouble(Constants.SCALING);
        if (ans == 0) throw new RuntimeException("Zero is not a valid scaling factor.");
        return ans;
    }

    private Double getPointScalingOffset() {
        return parameters.getDouble(Constants.SCALING_OFFSET);
    }

    public Double applyScaling(Double val) {
        return val * getPointScaling() + getPointScalingOffset();
    }

    public Double removeScaling(Double val) {
        return (val - getPointScalingOffset()) / getPointScaling();
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

    private class VTSHelper<T> {
        BaseLocator<T> loc;

        VTSHelper(BaseLocator<T> locator) {
            this.loc = locator;
        }

        @SuppressWarnings("unchecked")
        private short[] valueToShortsHelper(Object obj) {
            return loc.valueToShorts((T) obj);
        }
    }

    @Override
    public void onSet(DSInfo info, DSIValue value) {
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
        switch (dataType) {
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
        double seconds = Constants.DEFAULT_PING_RATE;
        if (rate != null && rate.isNumber()) {
            seconds = rate.toDouble();
        }
        return (long) (seconds * 1000);
    }
}
