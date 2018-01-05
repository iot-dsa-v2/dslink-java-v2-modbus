package org.iot.dsa.dslink.modbus;

import org.iot.dsa.node.DSFlexEnum;
import org.iot.dsa.node.DSIEnum;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSMetadata;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.DSAction;

public class ParameterDefinition {
    
    public final String name;
    public final DSValueType type;
    public final DSIEnum enumtype;
    public final DSIValue def;
    public final String description;
    public final String placeholder;
    
    
    private ParameterDefinition(String name, DSValueType type, DSIEnum enumtype, DSIValue def,
            String description, String placeholder) {
        super();
        this.name = name;
        this.type = type;
        this.enumtype = enumtype;
        this.def = def;
        this.description = description;
        this.placeholder = placeholder;
    }
    
    public static ParameterDefinition makeParam(String name, DSValueType type, String description, String placeholder) {
        return new ParameterDefinition(name, type, null, null, description, placeholder);
    }
    
    public static ParameterDefinition makeEnumParam(String name, DSIEnum enumtype, String description, String placeholder) {
        return new ParameterDefinition(name, null, enumtype, null, description, placeholder);
    }
    
    public static ParameterDefinition makeParamWithDefault(String name, DSIValue def, String description, String placeholder) {
        return new ParameterDefinition(name, null, null, def, description, placeholder);
    }
    
    public DSMetadata addToAction(DSAction action, DSIValue defOverride) {
        DSMetadata metadata;
        if (defOverride != null) {
            DSIEnum et = null;
            if (enumtype != null) {
                et = enumtype;
            } else if (def instanceof DSIEnum) {
                et = (DSIEnum) def;
            }
            
            if (et == null) {
                metadata = action.addDefaultParameter(name, defOverride, description);
            } else {
                DSIEnum def;
                if (et instanceof DSJavaEnum) {
                    def = ((DSJavaEnum) et).valueOf(defOverride.toElement().toString());
                } else if (et instanceof DSFlexEnum) {
                    def = ((DSFlexEnum) et).valueOf(defOverride.toElement().toString());
                } else {
                    throw new RuntimeException("Unexpected runtime class for DSIEnum");
                }
                metadata = action.addDefaultParameter(name, (DSIValue) def, description);
            }
        } else if (def != null) {
            metadata = action.addDefaultParameter(name, def, description);
        } else if (enumtype != null) {
            metadata = action.addParameter(name, (DSIValue) enumtype, description);
        } else {
            metadata = action.addParameter(name, type, description);
        }
        
        if (placeholder != null) {
            metadata.setPlaceHolder(placeholder);
        }
        return metadata;
    }
    
    public DSMetadata addToAction(DSAction action) {
        return addToAction(action, null);
    }
    

}
