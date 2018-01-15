package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.modbus.Constants.DataTypeEnum;
import org.iot.dsa.dslink.modbus.Constants.PointType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSMap;

class DataTypeParameter extends ParameterDefinition {

    protected DataTypeParameter(String description, String placeholder) {
        super(Constants.POINT_DATA_TYPE, null, DSJavaEnum.valueOf(DataTypeEnum.BINARY), null, description, placeholder);
    }
    
    @Override
    public void verify(DSMap parameters) {
        super.verify(parameters);
        DataTypeEnum dt = DataTypeEnum.valueOf(parameters.getString(name));
        PointType pt = PointType.valueOf(parameters.getString(Constants.POINT_OBJECT_TYPE));
        if (pt.equals(PointType.COIL) || pt.equals(PointType.DISCRETE)) {
            if (!dt.equals(DataTypeEnum.BINARY)) {
                throw new RuntimeException("Coils and Discrete Inputs must have Binary data type");
            }
        }
    }
    
}