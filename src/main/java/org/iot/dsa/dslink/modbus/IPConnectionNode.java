package org.iot.dsa.dslink.modbus;

import org.iot.dsa.node.DSMap;

public class IPConnectionNode extends ModbusConnectionNode {
    
    static {
        //TODO add Modbus IP Connection parameters here
        //parameterDefinitions.add(ParameterDefinition.makeParam(name, type, description, placeholder))
    }
    
    public IPConnectionNode() {
        super();
    }
    
    public IPConnectionNode(DSMap parameters) {
        super(parameters);
    }

    

}
