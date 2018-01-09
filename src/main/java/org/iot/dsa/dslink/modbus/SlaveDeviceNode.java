package org.iot.dsa.dslink.modbus;

import org.iot.dsa.node.*;

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

    public SlaveDeviceNode(DSMap parameters) {
    }
}
