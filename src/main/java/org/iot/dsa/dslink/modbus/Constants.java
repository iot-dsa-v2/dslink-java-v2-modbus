package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;

public class Constants {
    public static final String PING_RATE = "Ping Rate";
    public static final String IP_TRANSPORT_TYPE = "Transport Type";
    public static final String IP_HOST = "Host";
    public static final String IP_PORT = "Port";

    public static enum IpTransportType {
        TCP,
        UDP
    }
    
    public static final String SLAVE_ID = "Slave ID";
    
    public static final String POLL_RATE = "Poll Rate";
    public static final String POINT_OBJECT_TYPE = "Object Type";
    public static final String POINT_OFFSET = "Offset";
    public static final String POINT_DATA_TYPE = "Data Type";
    public static final String POINT_BIT = "Bit";
    public static final String POINT_REGISTER_COUNT = "Number of Registers";
    
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
