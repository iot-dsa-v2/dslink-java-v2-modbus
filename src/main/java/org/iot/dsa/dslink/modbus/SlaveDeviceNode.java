package org.iot.dsa.dslink.modbus;

import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James (Juris) Puchin
 * Created on 1/8/2018
 */
public class SlaveDeviceNode extends DSNode {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SLAVE_ID, DSLong.valueOf(Constants.DEFAULT_SLAVE_ID), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.IP_PORT, DSLong.valueOf(Constants.DEFAULT_SLAVE_IP_PORT), null, null));
    }

    DSMap parameters;

    public SlaveDeviceNode() {

    }

    public SlaveDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }

    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get(Constants.PARAMETERS);
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
            Util.verifyParameters(parameters, parameterDefinitions);
        } else {
            Util.verifyParameters(parameters, parameterDefinitions);
            put(Constants.PARAMETERS, parameters.copy());
        }
    }
}
