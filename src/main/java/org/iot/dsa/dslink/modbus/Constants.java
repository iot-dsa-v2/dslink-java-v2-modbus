package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;

public class Constants {
    public static final String PARAMETERS = "parameters";
    
    public static final String ACTION_EDIT = "Edit";
    public static final String ACTION_ADD_IP = "Add IP Connection";
    public static final String ACTION_ADD_SERIAL = "Add Serial Connection";
    public static final String ACTION_RESCAN_PORTS = "Rescan For Serial Ports";
    public static final String ACTION_ADD_DEVICE = "Add Device";
    public static final String ACTION_ADD_POINT = "Add Point";
    public static final String ACTION_ADD_SLAVE = "Add Slave Device";
    
    public static final String NAME = "Name";
    public static final String PING_RATE = "Ping Rate";
    public static final String TIMEOUT = "Timeout";
    public static final String RETRIES = "Retries";
    public static final String USE_MULTIPLE_WRITE_COMMAND = "use multiple write commands";
    public static final String IP_TRANSPORT_TYPE = "Transport Type";
    public static final String IP_HOST = "Host";
    public static final String IP_PORT = "Port";
    public static final String SERIAL_TRANSPORT_TYPE = "Transport Type";
    public static final String SERIAL_PORT_DROPDOWN = "Serial Port";
    public static final String SERIAL_PORT_MANUAL = "Other Serial Port";
    public static final String OTHER_SERIAL_PORT = "Other";
    public static final String SERIAL_BAUD_RATE = "Baud Rate";
    public static final String SERIAL_DATA_BITS = "Data Bits";
    public static final String SERIAL_STOP_BITS = "Stop Bits";
    public static final String SERIAL_PARITY = "Parity";

    public static final int DEFAULT_TIMEOUT = 500;
    public static final int DEFAULT_RETRIES = 2;
    public static final int DEFAULT_SLAVE_ID = 1;
    public static final int DEFAULT_IP_PORT = 502;
    public static final int DEFAULT_SLAVE_IP_PORT = 1025;
    public static final int DEFAULT_BAUD_RATE = 9600;
    public static final int DEFAULT_DATA_BITS = 8;
    public static final int DEFAULT_STOP_BITS = 1;
    
    
    public static enum IpTransportType {
        TCP,
        UDP
    }
    
    public static enum SerialTransportType {
        RTU,
        ASCII
    }

    public static enum MultipleWriteEnum {
        DEFAULT,
        ALWAYS,
        NEVER
    }
    
    public static enum SerialParity {
        NONE(0),
        ODD(1),
        EVEN(2),
        MARK(3),
        SPACE(4);
        
        private final int id;
        private SerialParity(int id) {
            this.id = id;
        }
        
        public int toId() {
            return id;
        }
    }
    
    public static final String SLAVE_ID = "Slave ID";
    public static final String CONTIGUOUS_READS = "Contiguous Batch Reads Only";
    
    public static final String POLL_RATE = "Poll Rate";
    public static final String SCALING = "Scaling";
    public static final String SCALING_OFFSET = "Scaling Offset";
    public static final String POINT_OBJECT_TYPE = "Object Type";
    public static final String POINT_OFFSET = "Offset";
    public static final String POINT_DATA_TYPE = "Data Type";
    public static final String POINT_BIT = "Bit";
    public static final String POINT_REGISTER_COUNT = "Number of Registers";
    
    public static final String POINT_VALUE = "Value";
    public static final String POINT_ERROR = "Error Result";
    
    public static enum ObjectType {
        COIL("Coil", RegisterRange.COIL_STATUS),
        DISCRETE("Discrete Input", RegisterRange.INPUT_STATUS),
        INPUT("Input Register", RegisterRange.INPUT_REGISTER),
        HOLDING("Holding Register", RegisterRange.HOLDING_REGISTER);
        
        private final String name;
        private final int range;
        private ObjectType(String name, int range) {
            this.name = name;
            this.range = range;
        }
        public int toRange() {
            return range;
        }
        @Override
        public String toString() {
            return name;
        }
        public static ObjectType parse(String name) {
            for (ObjectType ot: ObjectType.values()) {
                if (ot.toString().equals(name)) {
                    return ot;
                }
            }
            return ObjectType.valueOf(name);
        }
    }
    
    public static enum DataTypeEnum {
        BINARY(DataType.BINARY),
        TWO_BYTE_INT_UNSIGNED(DataType.TWO_BYTE_INT_UNSIGNED),
        TWO_BYTE_INT_SIGNED(DataType.TWO_BYTE_INT_SIGNED),
        TWO_BYTE_INT_UNSIGNED_SWAPPED(DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED),
        TWO_BYTE_INT_SIGNED_SWAPPED(DataType.TWO_BYTE_INT_SIGNED_SWAPPED),
        FOUR_BYTE_INT_UNSIGNED(DataType.FOUR_BYTE_INT_UNSIGNED),
        FOUR_BYTE_INT_SIGNED(DataType.FOUR_BYTE_INT_SIGNED),
        FOUR_BYTE_INT_UNSIGNED_SWAPPED(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED),
        FOUR_BYTE_INT_SIGNED_SWAPPED(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED),
        FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED),
        FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED),
        FOUR_BYTE_FLOAT(DataType.FOUR_BYTE_FLOAT),
        FOUR_BYTE_FLOAT_SWAPPED(DataType.FOUR_BYTE_FLOAT_SWAPPED),
        FOUR_BYTE_FLOAT_SWAPPED_INVERTED(DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED),
        EIGHT_BYTE_INT_UNSIGNED(DataType.EIGHT_BYTE_INT_UNSIGNED),
        EIGHT_BYTE_INT_SIGNED(DataType.EIGHT_BYTE_INT_SIGNED),
        EIGHT_BYTE_INT_UNSIGNED_SWAPPED(DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED),
        EIGHT_BYTE_INT_SIGNED_SWAPPED(DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED),
        EIGHT_BYTE_FLOAT(DataType.EIGHT_BYTE_FLOAT),
        EIGHT_BYTE_FLOAT_SWAPPED(DataType.EIGHT_BYTE_FLOAT_SWAPPED),
        TWO_BYTE_BCD(DataType.TWO_BYTE_BCD),
        FOUR_BYTE_BCD(DataType.FOUR_BYTE_BCD),
        FOUR_BYTE_BCD_SWAPPED(DataType.FOUR_BYTE_BCD_SWAPPED),
        CHAR(DataType.CHAR),
        VARCHAR(DataType.VARCHAR);
        private final int id;
        private DataTypeEnum(int id) {
            this.id = id;
        }
        public int toId() {
            return id;
        }
    }
}
