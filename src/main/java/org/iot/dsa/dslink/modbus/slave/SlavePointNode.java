package org.iot.dsa.dslink.modbus.slave;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.IllegalDataAddressException;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.dslink.modbus.utils.DataTypeParameter;
import org.iot.dsa.dslink.modbus.utils.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.utils.Constants.PointType;
import org.iot.dsa.node.*;
import org.iot.dsa.util.DSException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
public class SlavePointNode extends EditableNode implements DSIValue {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_OBJECT_TYPE, DSJavaEnum.valueOf(PointType.COIL), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.POINT_OFFSET, DSValueType.NUMBER, null, null));
        parameterDefinitions.add(new DataTypeParameter(null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_REGISTER_COUNT, DSLong.valueOf(0), "Only applies for string data types (Char and Varchar)", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_BIT, DSLong.valueOf(0), "Only applies for Input/Holding Registers with Binary data type", null));
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    private DSInfo value = getInfo(Constants.POINT_VALUE);
    private DSInfo error = getInfo(Constants.POINT_ERROR);

    public SlavePointNode() {

    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.POINT_VALUE, DSString.EMPTY);
        declareDefault(Constants.POINT_ERROR, DSString.EMPTY).setHidden(true).setReadOnly(true);
        declareDefault("Remove", makeRemoveAction());
    }

    private int getPointRange() {
        return getPointType().toRange();
    }

    private int getPointBit() {
        return parameters.getInt(Constants.POINT_BIT);
    }

    private int getStringRegisterCount() {
        return parameters.getInt(Constants.POINT_REGISTER_COUNT);
    }

    private int getPointRegisterCount() {
        int num = DataType.getRegisterCount(getPointDataTypeInt());
        if (num == 0) {
            num = getStringRegisterCount();
        }
        return num;
    }

    private PointType getPointType() {
        return PointType.valueOf(parameters.getString(Constants.POINT_OBJECT_TYPE));
    }

    private int getPointOffset() {
        return parameters.getInt(Constants.POINT_OFFSET);
    }

    private DataTypeEnum getPointDataType() {
        return DataTypeEnum.valueOf(parameters.getString(Constants.POINT_DATA_TYPE));
    }

    private int getPointDataTypeInt() {
        return getPointDataType().toId();
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

    @Override
    public void onSet(DSIValue val) {
        updateValue(val.toElement());
        setValueToImage(val, getParentProcessImage());
    }

    @Override
    public void onSet(DSInfo info, DSIValue val) {
        if (this.value.equals(info)) {
            onSet(val);
        }
    }

    void updateValue(DSElement val) {
        put(value, val);
        getParent().childChanged(getInfo());
    }

//    @Override
//    protected void onStable() {
//        super.onStable();
//        //submitToSlaveHandler();
//    }

    private BasicProcessImage getParentProcessImage() {
        return getParentNode().procImg;
    }

    private static Boolean boolOrNull(DSElement element) {
        Boolean b;
        try {
            b = element.toBoolean();
        } catch (Exception e) {
            b = null;
        }
        return b;
    }

    private static Double doubleOrNull(DSElement element) {
        Double n;
        try {
            n = element.toDouble();
        } catch (Exception e) {
            n = null;
        }
        return n;
    }

    private static String stringOrNull(DSElement element) {
        String s;
        try {
            s = element.toString();
        } catch (Exception e) {
            s = null;
        }
        return s;
    }

    void setError(String err) {
        error.setHidden(false);
        put(error, DSString.valueOf(err));
    }

    void clearError() {
        error.setHidden(true);
        put(error, DSString.EMPTY);
    }

    //TODO: why is process image not stored in the SlavePoint? Can more than one exist?
    private void setValueToImage(DSIValue val, BasicProcessImage img) {

        DSElement element = val.toElement();

        DataTypeEnum dataType = getPointDataType();

        Double n = doubleOrNull(element);
        boolean withinBounds = (n == null || dataType.checkBounds(n));

        if (!withinBounds) {
            DSException.throwRuntime(new RuntimeException("Slave node value out of bounds!"));
            return;
        }

        int offset = getPointOffset();

        switch (getPointType()) {
            case COIL:
                Boolean b = boolOrNull(element);
                img.setCoil(offset, b != null ? b : false);
                break;
            case DISCRETE:
                b = boolOrNull(element);
                img.setInput(offset, b != null ? b : false);
                break;
            case HOLDING:
            case INPUT:
                int range = getPointRange();
                if (dataType.equals(DataTypeEnum.BINARY)) {
                    int bit = getPointBit();
                    b = boolOrNull(element);
                    img.setBit(range, offset, bit, b != null ? b : false);
                } else if (dataType.isString()) {
                    int regCnt = getStringRegisterCount();
                    String s = stringOrNull(element);
                    img.setString(range, offset, getPointDataTypeInt(), regCnt,
                            s != null ? s : "");
                } else {
                    img.setNumeric(range, offset, getPointDataTypeInt(), n != null ? n : 0);
                }
                break;
        }
    }

    @Override
    public void delete() {
        super.delete();
        escapeSlaveHandler();
    }

    void escapeSlaveHandler() {
        //Unregister from device node
        SlaveDeviceNode par = getParentNode();
        if (getPointType().equals(PointType.COIL)) {
            par.removeCoilPoint(getPointOffset());
        } else if (getPointType().equals(PointType.HOLDING)) {
            int sum = getPointRegisterCount() + getPointOffset();
            for (int o = getPointOffset(); o < sum; o++) {
                List<SlavePointNode> lst = par.getHoldingPoints(o);
                lst.remove(this);
                if (lst.size() == 0) par.removeHoldingPoints(o);
            }
        }

        //Set point value to zero in modbus image
        setValueToImage(DSLong.valueOf(0), getParentProcessImage());
    }

    // oh my god this method name
    // I know, right
    void submitToSlaveHandler() {
        //Add to lists for the benefit of the listener
        SlaveDeviceNode par = getParentNode();
        if (getPointType().equals(PointType.COIL)) {
            if (!par.registerCoilPoint(getPointOffset(), this)) {
                setError("Coil point already exists!");
                return;
            }
        } else if (getPointType().equals(PointType.HOLDING)) {
            boolean bool = getPointDataType().equals(DataTypeEnum.BINARY);
            int offset = getPointOffset();
            int sum = getPointRegisterCount() + offset;

            // Make sure point is safe to register
            for (int o = offset; o < sum; o++) {
                List<SlavePointNode> hLst = par.getHoldingPoints(o);
                if (hLst != null && hLst.size() > 0) {
                    if (bool) {
                        for (SlavePointNode n : hLst) {
                            if (n.getPointBit() == getPointBit()) {
                                setError("Holding binary point already exists!");
                                return;
                            }
                        }
                    } else {
                        setError("Holding point already exists!");
                        return;
                    }
                }
            }

            // Register point
            for (int o = offset; o < sum; o++) {
                par.registerHoldingPoint(o, this);
            }
        }

        //Set point value
        setValueToImage(value.getValue(), getParentProcessImage());
        clearError();
    }

    SlaveDeviceNode getParentNode() {
        DSNode parent = getParent();
        if (parent instanceof SlaveDeviceNode) {
            return (SlaveDeviceNode) parent;
        } else {
            throw new RuntimeException("Wrong parent class, expected SlaveDeviceNode");
        }
    }

    @Override
    public void preEdit(DSMap newParameters) {
        escapeSlaveHandler();
    }

    @Override
    public void onEdit() {
        submitToSlaveHandler();
    }

    void updatePointValue() {
        PointType pType = getPointType();
        DataTypeEnum dType = getPointDataType();
        BasicProcessImage img = getParentProcessImage();
        DSElement val = null;

        int offset = getPointOffset();
        try {
            switch (pType) {
                case COIL:
                    boolean v = img.getCoil(offset);
                    val = DSBool.valueOf(v);
                    break;
                case HOLDING:
                    switch (dType) {
                        case BINARY:
                            boolean b = img.getBit(pType.toRange(), offset, getPointBit());
                            val = DSBool.valueOf(b);
                            break;
                        case CHAR:
                        case VARCHAR:
                            String s = img.getString(pType.toRange(), offset, dType.toId(), getStringRegisterCount());
                            val = DSString.valueOf(s);
                        default:
                            Number n = img.getNumeric(pType.toRange(), offset, dType.toId());
                            val = DSDouble.valueOf(n.doubleValue());
                    }
            }
            updateValue(val);
        } catch (IllegalDataAddressException e) {
            warn("Point not correctly set up, value not found: " + e);
            DSException.throwRuntime(e);
        }
    }
}
