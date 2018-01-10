package org.iot.dsa.dslink.modbus;

import org.iot.dsa.node.*;

/**
 * @author James (Juris) Puchin
 * Created on 1/9/2018
 */
public interface DFNodeValue extends DSIValue {
    
    DSInfo value = null;
    DSMap parameters = null;
    
    @Override
    default DSElement toElement() {
        return value.getValue().toElement();
    }

    @Override
    default DSIValue valueOf(DSElement element) {
        return value.getValue().valueOf(element);
    }

    @Override
    default DSValueType getValueType() {
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
}
