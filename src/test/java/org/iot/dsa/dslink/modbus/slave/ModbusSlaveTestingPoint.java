package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingPoint;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValue;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingPoint extends TestingPoint {

    SlavePointNode myNode;

    ModbusSlaveTestingPoint(String name, String val, MockParameters pars) {
        super(name, val, pars);
        myNode = new SlavePointNode();
        myNode.parameters = pars.getParamMap();
        System.out.println(pars.getParamMap()); //DEBUG
    }

    @Override
    protected void setValue(String value) {
        DSValue val = createPointValue(value);
        myNode.onSet(val);
        super.setValue(val.toString());
    }

    private DSElement createPointValue(String val) {
        String str = myNode.parameters.get(Constants.POINT_DATA_TYPE).toString();
        return Constants.DataTypeEnum.valueOf(str).createValidValue(val);
    }
}
