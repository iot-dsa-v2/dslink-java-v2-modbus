package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.IllegalDataAddressException;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.node.*;
import org.iot.dsa.util.DSException;

import java.util.ArrayList;
import java.util.List;

import static org.iot.dsa.dslink.modbus.Constants.PointType;

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

    @Override
    protected void onStable() {
        super.onStable();
        submitToSlaveHandler();
    }

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

    private void escapeSlaveHandler() {
        //Unregister from device node
        SlaveDeviceNode par = getParentNode();
        if (getPointType().equals(PointType.COIL)) {
            par.removeCoilPoint(getPointOffset());
        } else if (getPointType().equals(PointType.HOLDING)) {
            int sum = getPointRegisterCount() + getPointOffset();
            //TODO: make collision safe (i.e. keep user from creating overlapping points)
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
    private void submitToSlaveHandler() {
        //Add to lists for the benefit of the listener
        if (getPointType().equals(PointType.COIL)) {
            getParentNode().registerCoilPoint(getPointOffset(), this);
        } else if (getPointType().equals(PointType.HOLDING)) {
            int sum = getPointRegisterCount() + getPointOffset();
            //TODO: make collision safe (i.e. keep user from creating overlapping points)
            for (int o = getPointOffset(); o < sum; o++) {
                getParentNode().registerHoldingPoint(o, this);
            }
        }

        setValueToImage(value.getValue(), getParentProcessImage());
    }

    //TODO: Is this not needed?
//    void startListening() {
//        if (listenerStpe != null) {
//
//            listenerStpe.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        statusNode.setValue(new Value(STATUS_START_LISTENING));
//                        activeListener.start();
//                    } catch (ModbusInitException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        }
//    }
//
//    void stopListening() {
//        if (listenerStpe != null) {
//
//            listenerStpe.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        statusNode.setValue(new Value(STATUS_STOP_LISTENING));
//                        activeListener.stop();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        }
//    }

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
        super.preEdit(newParameters);
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
