package org.iot.dsa.dslink.modbus.utils;

import org.iot.dsa.dslink.modbus.utils.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.utils.Constants.PointType;
import org.iot.dsa.node.DSMap;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DataTypeParameterTests {
    
    @Test
    public void verifyDatatypeMissing() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.COIL.name());
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter " + Constants.POINT_DATA_TYPE));
        }
    }
    
    @Test
    public void verifyDatatypeNotString() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.COIL.name()).put(Constants.POINT_DATA_TYPE, 23);
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter " + Constants.POINT_DATA_TYPE));
        }
    }
    
    @Test
    public void verifyDatatypeInvalidString() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.COIL.name()).put(Constants.POINT_DATA_TYPE, "FIVE_BYTE_INT_SIGNED");
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Unexpected Type on Parameter " + Constants.POINT_DATA_TYPE));
        }
    }
    
    @Test
    public void verifyCoilBinary() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.COIL.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.BINARY.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyDiscreteBinary() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.DISCRETE.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.BINARY.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyCoilInt() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.COIL.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.FOUR_BYTE_INT_UNSIGNED.name());
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Coils and Discrete Inputs must have Binary data type"));
        }
    }
    
    @Test
    public void verifyCoilString() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.COIL.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.CHAR.name());
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Coils and Discrete Inputs must have Binary data type"));
        }
    }
    
    @Test
    public void verifyDiscreteInt() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.DISCRETE.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.TWO_BYTE_INT_UNSIGNED_SWAPPED.name());
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Coils and Discrete Inputs must have Binary data type"));
        }
    }
    
    @Test
    public void verifyDiscreteString() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.DISCRETE.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.VARCHAR.name());
        try {
            dtp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Coils and Discrete Inputs must have Binary data type"));
        }
    }
    
    @Test
    public void verifyHoldingBinary() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.HOLDING.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.BINARY.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyHoldingInt() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.HOLDING.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.EIGHT_BYTE_INT_SIGNED_SWAPPED.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyHoldingFloat() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.HOLDING.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.EIGHT_BYTE_FLOAT.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyHoldingString() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.HOLDING.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.CHAR.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyInputBinary() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.INPUT.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.BINARY.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyInputInt() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.INPUT.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.EIGHT_BYTE_INT_SIGNED_SWAPPED.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyInputFloat() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.INPUT.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.EIGHT_BYTE_FLOAT.name());
        dtp.verify(parameters);
    }
    
    @Test
    public void verifyInputString() {
        DataTypeParameter dtp = new DataTypeParameter(null, null);
        DSMap parameters = new DSMap().put(Constants.POINT_OBJECT_TYPE, PointType.INPUT.name()).put(Constants.POINT_DATA_TYPE, DataTypeEnum.CHAR.name());
        dtp.verify(parameters);
    }

}
