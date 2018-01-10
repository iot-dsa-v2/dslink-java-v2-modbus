package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.ProcessImageListener;
import com.serotonin.modbus4j.exception.ModbusInitException;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.iot.dsa.dslink.modbus.Constants.DataTypeEnum.BINARY;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
public class SlavePointNode extends EditableNode implements DSIValue {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_OBJECT_TYPE, DSJavaEnum.valueOf(Constants.ObjectType.COIL), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.POINT_OFFSET, DSValueType.NUMBER, null, null));
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.POINT_DATA_TYPE, DSJavaEnum.valueOf(BINARY), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    @Override
    public void addNewInstance(DSNode parent, DSMap newParameters) {
        // TODO Auto-generated method stub
        
    }

    private DSInfo value = getInfo(Constants.POINT_VALUE);
    private DSInfo error = getInfo(Constants.POINT_ERROR);

    public SlavePointNode() {

    }

    public SlavePointNode(DSMap params) {
        this.parameters = params;
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.POINT_VALUE, DSString.EMPTY);
        declareDefault(Constants.POINT_ERROR, DSString.EMPTY).setHidden(true).setReadOnly(true);
    }

    @Override
    public DSValueType getValueType() {
        Constants.DataTypeEnum dataType = Constants.DataTypeEnum.valueOf(parameters.getString(Constants.POINT_DATA_TYPE));
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

    @Override
    protected void onStarted() {
        submitToSlaveHandler();
    }

    // oh my god this method name
    private void submitToSlaveHandler() {
        Constants.ObjectType objType = Constants.ObjectType.valueOf(parameters.getString(Constants.POINT_OBJECT_TYPE));


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
            //TODO: Update coil value
            if (oldValue != newValue) {
                Node pointNode = offsetToPoint.get(offset);
                pointNode.setValue(new Value(newValue));
            }

        }

        @Override
        public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
            if (oldValue != newValue) {
                //TODO: Update register value
                Node pointNode = offsetToPoint.get(offset);
                DataType dataType = DataType.valueOf(pointNode.getAttribute(ATTRIBUTE_DATA_TYPE).getString());
                if (dataType.isString()) {
                    ByteBuffer buffer = ByteBuffer.allocate(2);
                    buffer.putShort(newValue);
                    String str = new String(buffer.array(), StandardCharsets.UTF_8);
                    pointNode.setValue(new Value(str));

                } else {
                    pointNode.setValue(new Value(newValue));
                }

            }
        }

    }*/
}
