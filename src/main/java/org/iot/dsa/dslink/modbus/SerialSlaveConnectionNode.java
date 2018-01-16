package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.modbus.Constants.SerialParity;
import org.iot.dsa.dslink.modbus.Constants.SerialTransportType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;
import com.serotonin.modbus4j.BasicProcessImage;

public class SerialSlaveConnectionNode extends SlaveConnectionNode {
    protected static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.SERIAL_TRANSPORT_TYPE, DSJavaEnum.valueOf(SerialTransportType.RTU), null, null));
        parameterDefinitions.add(new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_BAUD_RATE, DSLong.valueOf(Constants.DEFAULT_BAUD_RATE), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_DATA_BITS, DSLong.valueOf(Constants.DEFAULT_DATA_BITS), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_STOP_BITS, DSLong.valueOf(Constants.DEFAULT_STOP_BITS), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_PARITY, DSJavaEnum.valueOf(SerialParity.NONE), null, null));
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    public SerialSlaveConnectionNode() {
        
    }

    @Override
    protected BasicProcessImage getProcessImage(SlaveDeviceNode slave) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void deleteProcessImage(SlaveDeviceNode slave) {
        // TODO Auto-generated method stub
        
    }

}
