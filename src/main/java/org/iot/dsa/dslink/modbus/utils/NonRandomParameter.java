package org.iot.dsa.dslink.modbus.utils;

import java.util.Random;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.ParameterBounds;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;

public class NonRandomParameter extends ParameterDefinition {
    
    public NonRandomParameter(String name, DSIValue def, ParameterBounds<?> bounds, String description, String placeholder) {
        super(name, null, null, def, bounds, description, placeholder);
    }
    
    @Override
    public DSElement generateRandom(Random rand) {
        return def.toElement();
    }

}
