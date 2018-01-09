package org.iot.dsa.dslink.modbus;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.DSAction;
import com.serotonin.modbus4j.code.DataType;
import jssc.SerialNativeInterface;
import jssc.SerialPortList;

public class Util {
    
    public static void makeAddParameters(DSAction action, List<ParameterDefinition> parameterDefinitions) {
        action.addParameter(Constants.NAME, DSValueType.STRING, null);
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
            defn.verify(parameters);
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
        return null;
    }
    
    public static String[] getCommPorts() {
        String[] portNames;

        switch (SerialNativeInterface.getOsType()) {
        case SerialNativeInterface.OS_LINUX:
            portNames = SerialPortList
                    .getPortNames(Pattern.compile("(cu|ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm|ttyO)[0-9]{1,3}"));
            break;
        case SerialNativeInterface.OS_MAC_OS_X:
            portNames = SerialPortList.getPortNames(Pattern.compile("(cu|tty)..*")); // Was
                                                                                        // "tty.(serial|usbserial|usbmodem).*")
            break;
        default:
            portNames = SerialPortList.getPortNames();
            break;
        }

        return portNames;

    }

}
