package org.iot.dsa.dslink.modbus.utils;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import org.iot.dsa.node.*;

import java.nio.ByteBuffer;
import java.util.Random;

public class Constants {
    public static final String PARAMETERS = "parameters";
    
    public static final String CONFIG_RECONNECT_DELAY_MULTIPLIER = "reconnectDelayMultiplier";

    public static final String ACTION_EDIT = "Edit";
    public static final String ACTION_ADD_IP = "Add IP Connection";
    public static final String ACTION_ADD_SERIAL = "Add Serial Connection";
    public static final String ACTION_RESCAN_PORTS = "Rescan For Serial Ports";
    public static final String ACTION_ADD_DEVICE = "Add Device";
    public static final String ACTION_ADD_POINT = "Add Point";
    public static final String ACTION_ADD_IP_SLAVE = "Add Slave IP Connection";
    public static final String ACTION_ADD_SERIAL_SLAVES = "Add Slave Serial Connection";
    public static final String ACTION_ADD_SLAVE = "Add Slave Device";
    public static final String ACTION_ADD_SLAVE_POINT = "Add Slave Point";

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

    public static final int UNSIGED_SHORT_MAX = 65535;
    public static final double DEFAULT_PING_RATE = 5;
    public static final int DEFAULT_TIMEOUT = 500;
    public static final int DEFAULT_RETRIES = 2;
    public static final int DEFAULT_SLAVE_ID = 1;
    public static final int DEFAULT_IP_PORT = 502;
    public static final int DEFAULT_SLAVE_IP_PORT = 1025;
    public static final int DEFAULT_BAUD_RATE = 9600;
    public static final int DEFAULT_DATA_BITS = 8;
    public static final int DEFAULT_STOP_BITS = 1;


    public static enum IpTransportType {
        TCP
        //,UDP
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
    public static final String SLAVE_ERROR = "Error Status";

    public static enum PointType {
        COIL("Coil", RegisterRange.COIL_STATUS),
        DISCRETE("Discrete Input", RegisterRange.INPUT_STATUS),
        INPUT("Input Register", RegisterRange.INPUT_REGISTER),
        HOLDING("Holding Register", RegisterRange.HOLDING_REGISTER);

        private final String name;
        private final int range;

        private PointType(String name, int range) {
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

        public static PointType parse(String name) {
            for (PointType ot : PointType.values()) {
                if (ot.toString().equals(name)) {
                    return ot;
                }
            }
            return PointType.valueOf(name);
        }
    }

    public static enum DataTypeEnum {
        BINARY(DataType.BINARY, null, null),
        TWO_BYTE_INT_UNSIGNED(DataType.TWO_BYTE_INT_UNSIGNED, 0, 65535),
        TWO_BYTE_INT_SIGNED(DataType.TWO_BYTE_INT_SIGNED, Short.MIN_VALUE, Short.MAX_VALUE),
        TWO_BYTE_INT_UNSIGNED_SWAPPED(DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED, 0, 65535),
        TWO_BYTE_INT_SIGNED_SWAPPED(DataType.TWO_BYTE_INT_SIGNED_SWAPPED, Short.MIN_VALUE, Short.MAX_VALUE),
        FOUR_BYTE_INT_UNSIGNED(DataType.FOUR_BYTE_INT_UNSIGNED, 0L, 4294967295L),
        FOUR_BYTE_INT_SIGNED(DataType.FOUR_BYTE_INT_SIGNED, Integer.MIN_VALUE, Integer.MAX_VALUE),
        FOUR_BYTE_INT_UNSIGNED_SWAPPED(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED, 0L, 4294967295L),
        FOUR_BYTE_INT_SIGNED_SWAPPED(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED, Integer.MIN_VALUE, Integer.MAX_VALUE),
        FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED(DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED, 0L, 4294967295L),
        FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED(DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED, Integer.MIN_VALUE, Integer.MAX_VALUE),
        FOUR_BYTE_FLOAT(DataType.FOUR_BYTE_FLOAT, null, null),
        FOUR_BYTE_FLOAT_SWAPPED(DataType.FOUR_BYTE_FLOAT_SWAPPED, null, null),
        EIGHT_BYTE_INT_UNSIGNED(DataType.EIGHT_BYTE_INT_UNSIGNED, 0L, null),
        EIGHT_BYTE_INT_SIGNED(DataType.EIGHT_BYTE_INT_SIGNED, Long.MIN_VALUE, Long.MAX_VALUE),
        EIGHT_BYTE_INT_UNSIGNED_SWAPPED(DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED, 0L, null),
        EIGHT_BYTE_INT_SIGNED_SWAPPED(DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED, Long.MIN_VALUE, Long.MAX_VALUE),
        EIGHT_BYTE_FLOAT(DataType.EIGHT_BYTE_FLOAT, null, null),
        EIGHT_BYTE_FLOAT_SWAPPED(DataType.EIGHT_BYTE_FLOAT_SWAPPED, null, null),
        TWO_BYTE_BCD(DataType.TWO_BYTE_BCD, 0, 9999),
        FOUR_BYTE_BCD(DataType.FOUR_BYTE_BCD, 0, 99999999),
        CHAR(DataType.CHAR, null, null),
        VARCHAR(DataType.VARCHAR, null, null);

        //TODO: FOUR_BYTE_BCD_SWAPPED and FOUR_BYTE_FLOAT_SWAPPED_INVERTED not implemented in library. Build workaround or fix library.
        //FOUR_BYTE_BCD_SWAPPED(DataType.FOUR_BYTE_BCD_SWAPPED, 0, 99999999),
        //FOUR_BYTE_FLOAT_SWAPPED_INVERTED(DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED, null, null);

        //INT32M10K(-327680000, 327670000),
        //UINT32M10K(0, 655350000),
        //INT32M10KSWAP(-327680000, 327670000),
        //UINT32M10KSWAP(0, 655350000);

        private final int id;

        private Long lowerBound;
        private Long upperBound;

        DataTypeEnum(int id, Long lowerBound, Long upperBound) {
            this.id = id;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public boolean isString() {
            return (this == CHAR || this == VARCHAR);
        }

        public boolean checkBounds(Number n) {
            return (lowerBound == null || n.longValue() >= lowerBound) && (upperBound == null || n.longValue() <= upperBound);
        }

        private static byte[] makeBytes(String seed, int len) {
            Random rand = new Random(seed.hashCode());
            byte[] raw = new byte[len];
            rand.nextBytes(raw);
            return raw;
        }

        private static Float makeFloat(String seed) {
            byte[] raw = makeBytes(seed, 4);
            return ByteBuffer.wrap(raw).getFloat();
        }

        private static Double makeDouble(String seed) {
            byte[] raw = makeBytes(seed, 8);
            return ByteBuffer.wrap(raw).getDouble();
        }


        public DSElement createValidValue(String str) {
            switch (this) {
                case VARCHAR:
                case CHAR:
                    return DSString.valueOf(str);
                case BINARY:
                    return DSBool.valueOf(str.length() % 2 == 0);
                case FOUR_BYTE_FLOAT:
                case FOUR_BYTE_FLOAT_SWAPPED:
                    return DSDouble.valueOf(makeFloat(str));
                case EIGHT_BYTE_FLOAT:
                case EIGHT_BYTE_FLOAT_SWAPPED:
                    return DSDouble.valueOf(makeDouble(str));
                default:
                    long code = str.hashCode();
                    if (lowerBound != null && upperBound == null) {
                        code = Math.abs(code);
                    } else if (lowerBound != null && upperBound != null) {
                        if (lowerBound > code || upperBound < code) {
                            long range = upperBound - lowerBound;
                            code = Math.abs(code) % range + lowerBound;
                        }
                    }
                    return DSDouble.valueOf(code);
            }
        }

        DataTypeEnum(int id, int lowerBound, int upperBound) {
            this(id, (long) lowerBound, (long) upperBound);
        }

        public int toId() {
            return id;
        }
        }
}
