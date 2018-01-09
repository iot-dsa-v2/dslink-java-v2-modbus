package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.modbus.Constants.SerialParity;
import org.iot.dsa.dslink.modbus.Constants.SerialTransportType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import com.serotonin.modbus4j.serial.SerialPortWrapper;

public class SerialConnectionNode extends ModbusConnectionNode {
    protected static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.SERIAL_TRANSPORT_TYPE, DSJavaEnum.valueOf(SerialTransportType.RTU), null, null));
        parameterDefinitions.add(new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_BAUD_RATE, DSLong.valueOf(Constants.DEFAULT_BAUD_RATE), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_DATA_BITS, DSLong.valueOf(Constants.DEFAULT_DATA_BITS), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_STOP_BITS, DSLong.valueOf(Constants.DEFAULT_STOP_BITS), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SERIAL_PARITY, DSJavaEnum.valueOf(SerialParity.NONE), null, null));
        ModbusConnectionNode.addCommonParameterDefinitions(parameterDefinitions);
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    public SerialConnectionNode() {
        super();
    }
    
    public SerialConnectionNode(DSMap parameters) {
        super(parameters);
    }
    
    @Override
    public boolean createConnection() {
        SerialTransportType transportType = SerialTransportType.valueOf(parameters.getString(Constants.SERIAL_TRANSPORT_TYPE));
        String portName = parameters.getString(Constants.SERIAL_PORT_MANUAL);
        int baudRate = parameters.getInt(Constants.SERIAL_BAUD_RATE);
        int dataBits = parameters.getInt(Constants.SERIAL_DATA_BITS);
        int stopBits = parameters.getInt(Constants.SERIAL_STOP_BITS);
        SerialParity parity = SerialParity.valueOf(parameters.getString(Constants.SERIAL_PARITY));
        SerialPortWrapper wrapper = new SerialPortWrapperImpl(portName, baudRate, dataBits, stopBits, parity.toId());
        
        switch(transportType) {
            case ASCII:
                master = modbusFactory.createAsciiMaster(wrapper);
                break;
            case RTU:
                master = modbusFactory.createRtuMaster(wrapper);
                break;
        }
        
        return super.createConnection();
    }

}
