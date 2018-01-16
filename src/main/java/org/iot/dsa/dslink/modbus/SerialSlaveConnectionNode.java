package org.iot.dsa.dslink.modbus;

import java.util.ArrayList;
import java.util.List;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.modbus.Constants.SerialParity;
import org.iot.dsa.dslink.modbus.Constants.SerialTransportType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.serial.SerialPortWrapper;

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
    
    private SerialPortWrapperImpl portWrapper = null;

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    public SerialSlaveConnectionNode() {
        
    }
    
    private SerialTransportType getSerialConnectionTransportType() {
        return SerialTransportType.valueOf(parameters.getString(Constants.SERIAL_TRANSPORT_TYPE));
    }
    
    @Override
    protected BasicProcessImage getProcessImage(SlaveDeviceNode slave) {
        SerialTransportType transportType = getSerialConnectionTransportType();
        switch (transportType) {
            case ASCII:
                return ModbusSlaveHandler.getAsciiProcessImage(getPortWrapper(), slave.getDeviceSlaveID(), slave);
            case RTU:
                return ModbusSlaveHandler.getRtuProcessImage(getPortWrapper(), slave.getDeviceSlaveID(), slave);
            default:
                throw new RuntimeException("Only RTU and ASCII transports supported for Serial Connections");
        }
    }

    @Override
    protected void deleteProcessImage(SlaveDeviceNode slave) {
        SerialTransportType transportType = getSerialConnectionTransportType();
        switch (transportType) {
            case ASCII:
                ModbusSlaveHandler.deleteAsciiProcessImage(getPortWrapper(), slave.getDeviceSlaveID());
                break;
            case RTU:
                ModbusSlaveHandler.deleteRtuProcessImage(getPortWrapper(), slave.getDeviceSlaveID());
                break;
        }
    }
    
    private SerialPortWrapperImpl getPortWrapper() {
        if (portWrapper == null) {
            String portName = parameters.getString(Constants.SERIAL_PORT_MANUAL);
            int baudRate = parameters.getInt(Constants.SERIAL_BAUD_RATE);
            int dataBits = parameters.getInt(Constants.SERIAL_DATA_BITS);
            int stopBits = parameters.getInt(Constants.SERIAL_STOP_BITS);
            SerialParity parity = SerialParity.valueOf(parameters.getString(Constants.SERIAL_PARITY));
            portWrapper = new SerialPortWrapperImpl(portName, baudRate, dataBits, stopBits, parity.toId());
        }
        return portWrapper;
    }
    
    @Override
    public void onEdit() {
        portWrapper = null;
        getPortWrapper();
        super.onEdit();
    }

}
