package org.iot.dsa.dslink.modbus.utils;

import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSFlexEnum;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMetadata;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.DSAction;

public class SerialPortParameter extends ParameterDefinition {
    
    private String dropdownName;
    private String dropdownDescription;
    private String manualName;
    private String manualDescription;
    
    public SerialPortParameter(String dropdownName, String dropdownDescription, String manualName, String manualDescription) {
        super(manualName, null, null, null, manualDescription, null);
        this.dropdownName = dropdownName;
        this.dropdownDescription = dropdownDescription;
        this.manualName = manualName;
        this.manualDescription = manualDescription;
    }

    @Override
    public DSMetadata addToAction(DSAction action, DSIValue defOverride) {
        String[] portList = Util.getCommPorts();
        DSList enumRange = DSList.valueOf(portList);
        enumRange.add(Constants.OTHER_SERIAL_PORT);
        String ddDefStr;
        DSString manDefStr;
        if (defOverride != null) {
            if (enumRange.contains(defOverride.toElement())) {
                ddDefStr = defOverride.toElement().toString();
                manDefStr = DSString.EMPTY;
            } else {
                ddDefStr = Constants.OTHER_SERIAL_PORT;
                manDefStr = DSString.valueOf(defOverride.toElement().toString());
            }
        } else {
            ddDefStr = Constants.OTHER_SERIAL_PORT;
            manDefStr = DSString.EMPTY;
        }
        DSFlexEnum portEnum = DSFlexEnum.valueOf(ddDefStr, enumRange);

        action.addDefaultParameter(dropdownName, portEnum, dropdownDescription);
        return action.addDefaultParameter(manualName, manDefStr, manualDescription);
    }
    
    @Override
    public void verify(DSMap parameters) {
        String ddPort = parameters.getString(dropdownName);
        if (!Constants.OTHER_SERIAL_PORT.equals(ddPort)) {
            parameters.put(manualName, ddPort);
        }
        DSElement port = parameters.get(manualName);
        if (port == null || !port.isString() || port.toString().isEmpty()) {
            throw new RuntimeException("Missing Parameter " + manualName);
        }
    }
}
