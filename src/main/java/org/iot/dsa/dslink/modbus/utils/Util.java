package org.iot.dsa.dslink.modbus.utils;

import java.math.BigInteger;
import java.util.regex.Pattern;
import org.iot.dsa.dslink.modbus.utils.Constants.DataTypeEnum;
import org.iot.dsa.node.DSIValue;
import com.serotonin.modbus4j.code.DataType;
import jssc.SerialNativeInterface;
import jssc.SerialPortList;

public class Util {
    
    public static SerialPortLister serialPortLister = new SerialPortLister();
    
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
        return serialPortLister.listPorts();
    }
    
    public static class SerialPortLister {
        public String[] listPorts() {
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

}
