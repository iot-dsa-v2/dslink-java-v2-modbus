package org.iot.dsa.dslink.modbus;

import java.math.BigInteger;
import java.util.List;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIEnum;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.DSAction;
import com.serotonin.modbus4j.code.DataType;

public class Util {
    
    public static void makeAddParameters(DSAction action, List<ParameterDefinition> parameterDefinitions) {
        action.addParameter("Name", DSValueType.STRING, null);
        for (ParameterDefinition paramDefn: parameterDefinitions) {
            paramDefn.addToAction(action);
        }
    }
    
    public static void makeEditParameters(DSAction action, List<ParameterDefinition> parameterDefinitions, DSMap parameters) {
        for (ParameterDefinition paramDefn: parameterDefinitions) {
            DSElement def = parameters.get(paramDefn.name);
            paramDefn.addToAction(action, def);
        }
    }
    
    public static void verifyParameters(DSMap parameters, List<ParameterDefinition> parameterDefinitions) {
        for (ParameterDefinition defn: parameterDefinitions) {
            DSElement paramVal = parameters.get(defn.name);
            if (paramVal == null) {
                if (defn.def != null) {
                    paramVal = defn.def.toElement();
                    parameters.put(defn.name, paramVal);
                } else {
                    throw new RuntimeException("Missing Parameter " + defn.name);
                }
            } else {
                boolean rightType = false;
                if (defn.def != null) {
                    if (defn.def.getValueType().equals(DSValueType.ENUM)) {
                        rightType = paramVal.isString() && defn.def instanceof DSIEnum
                                && ((DSIEnum) defn.def).getEnums(null).contains(paramVal);
                    } else {
                        rightType = defn.def.getValueType().equals(paramVal.getValueType());
                    }
                } else if (defn.enumtype != null) {
                    rightType = paramVal.isString() && defn.enumtype.getEnums(null).contains(paramVal);
                } else if (defn.type != null) {
                    rightType = defn.type.equals(paramVal.getValueType());
                }
                if (!rightType) {
                    throw new RuntimeException("Unexpected Type on Parameter " + defn.name);
                }
            }
                
        }
    }
    
    public static Object valueToObject(DSIValue value, DataTypeEnum type) {
        Class<?> javaType = DataType.getJavaType(type.toId());
        if (Boolean.class.equals(javaType)) {
            return value.toElement().toBoolean();
        } else if (Short.class.equals(javaType)) {
            return (short) value.toElement().toInt();
        } else if (Integer.class.equals(javaType)) {
            return value.toElement().toInt();
        } else if (Long.class.equals(javaType)) {
            return value.toElement().toLong();
        } else if (Float.class.equals(javaType)) {
            return value.toElement().toFloat();
        } else if (BigInteger.class.equals(javaType)) {
            return value.toElement().toLong();
        } else if (Double.class.equals(javaType)) {
            return value.toElement().toDouble();
        } else if (String.class.equals(javaType)) {
            return value.toElement().toString();
        }
        //TODO scaling?
        return null;
    }

}
