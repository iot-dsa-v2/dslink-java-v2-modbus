package org.iot.dsa.dslink.modbus;

import java.util.List;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.DSAction;

public class Util {
    
    public static void makeAddParameters(DSAction action, List<ParameterDefinition> parameterDefinitions) {
        action.addParameter("Name", DSValueType.STRING, null);
        for (ParameterDefinition paramDefn: parameterDefinitions) {
            paramDefn.addToAction(action);
        }
    }
    
    public static void makeEditParameters(DSAction action, List<ParameterDefinition> parameterDefinitions, DSMap parameters) {
        for (ParameterDefinition paramDefn: parameterDefinitions) {
            DSElement def = parameters.get(paramDefn.name);
            paramDefn.addToAction(action, def);
        }
    }

}
