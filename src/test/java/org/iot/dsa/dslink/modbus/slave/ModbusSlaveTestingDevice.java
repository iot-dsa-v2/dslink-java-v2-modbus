package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dftest.MockParameters;
import org.iot.dsa.dslink.dftest.TestingDevice;
import org.iot.dsa.dslink.modbus.ModbusMockPointParameters;
import org.iot.dsa.dslink.modbus.ModbusTestingPoint;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSMap;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/26/2018
 */
public class ModbusSlaveTestingDevice extends TestingDevice {

    SlaveDeviceNode myNode;

    ModbusSlaveTestingDevice(String name, MockParameters pars) {
        super(name, pars);
        myNode = new SlaveDeviceNode();
        myNode.parameters = pars.getParamMap();
        System.out.println(pars.getParamMap()); //DEBUG
    }

    @Override
    protected void addPoint(String name, String value, Random rand) {
        ModbusMockSlavePointParameters pars = new ModbusMockSlavePointParameters(rand);
        DSElement val = createPointValue(value, pars.getParamMap());
        ModbusSlaveTestingPoint pnt = new ModbusSlaveTestingPoint(name, val.toString(), pars);
        myNode.add(name, pnt.myNode);

        pnt.myNode.onSet(val);
        points.put(name, pnt);

        System.out.println("Making a Point: \n" + myNode.getParentNode().parameters + "\n" + myNode.parameters + "\n" + pars.getParamMap());
    }

    private static DSElement createPointValue(String val, DSMap pars) {
        String str = pars.get(Constants.POINT_DATA_TYPE).toString();
        return Constants.DataTypeEnum.valueOf(str).createValidValue(val);
    }
}
