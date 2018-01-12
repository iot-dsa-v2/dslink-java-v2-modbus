package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.ProcessImageListener;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.*;
import org.iot.dsa.dslink.modbus.Constants;
import org.iot.dsa.util.DSException;

import java.util.ArrayList;
import java.util.List;

import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
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
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_DATA_TYPE, DSJavaEnum.valueOf(DataTypeEnum.BINARY), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_REGISTER_COUNT, DSLong.valueOf(0), "Only applies for string data types (Char and Varchar)", null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.POINT_BIT, DSLong.valueOf(0), "Only applies for Input/Holding Registers with Binary data type", null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    //TODO: handle incorrect setting edge cases (trying to put INT into COIL)
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

    private int getPointRegisterCount() {
        return parameters.getInt(Constants.POINT_REGISTER_COUNT);
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

    @Override
    public void onSet(DSIValue val) {
        updateValue(val.toElement());
        setValue(val, getParentProcessImage());
    }

    @Override
    public void onSet(DSInfo info, DSIValue val) {
        if (this.value.equals(info)) {
            onSet(val);
        }
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

    private BasicProcessImage setValue(DSIValue val, BasicProcessImage img) {

        DSElement element = val.toElement();

        DataTypeEnum dataType = getPointDataType();

        Double n = doubleOrNull(element);
        boolean withinBounds = (n == null || dataType.checkBounds(n));

        if (!withinBounds) {
            //TODO: Is this how we want to handle it?
            DSException.throwRuntime(new RuntimeException("Slave node value out of bounds!"));
            return img;
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
                    int regCnt = getPointRegisterCount();
                    String s = stringOrNull(element);
                    img.setString(range, offset, getPointDataTypeInt(), regCnt,
                            s != null ? s : "");
                } else {
                    img.setNumeric(range, offset, getPointDataTypeInt(), n != null ? n : 0);
                }
                break;
        }

        return img;
    }

    // oh my god this method name
    // I know, right
    private void submitToSlaveHandler() {
        getParentNode().registerSlavePoint(getPointOffset(), this);
        setValue(value.getValue(), getParentProcessImage());
    }

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
    public void onEdit() {
        // TODO Auto-generated method stub
        
    }

/*    private class BasicProcessImageListener implements ProcessImageListener {

        @Override
        public void coilWrite(int offset, boolean oldValue, boolean newValue) {
            if (oldValue != newValue) {
                SlavePointNode pointNode = getParentNode().getSlavePointFromOffset(offset);
                pointNode.updateValue(DSBool.valueOf(newValue));
            }
        }

        @Override
        public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
            if (oldValue != newValue) {
                //TODO: Update register value
                SlavePointNode pointNode = getParentNode().getSlavePointFromOffset(offset);
                DataTypeEnum dataType = getPointDataType();

                if (dataType.isString()) {
                    ByteBuffer buffer = ByteBuffer.allocate(2);
                    buffer.putShort(newValue);
                    String str = new String(buffer.array(), StandardCharsets.UTF_8);
                    pointNode.setValue(new Value(str));
                } else {
                    pointNode.setValue(new Value(newValue));
                }

                pointNode.updateValue();
            }
        }
    }*/
}
