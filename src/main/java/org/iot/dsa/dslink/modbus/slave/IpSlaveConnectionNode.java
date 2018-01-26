package org.iot.dsa.dslink.modbus.slave;

import com.serotonin.modbus4j.BasicProcessImage;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.dslink.modbus.slave.handler.ModbusSlaveHandler;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.dslink.modbus.utils.Constants.IpTransportType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;

import java.util.ArrayList;
import java.util.List;


public class IpSlaveConnectionNode extends SlaveConnectionNode {

    protected static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.IP_TRANSPORT_TYPE,
                DSJavaEnum.valueOf(IpTransportType.TCP), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.IP_PORT,
                DSLong.valueOf(Constants.DEFAULT_SLAVE_IP_PORT), new IntegerBounds(0, Constants.UNSIGED_SHORT_MAX), null, null));
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    public IpSlaveConnectionNode() {

    }

    private int getConnectionPort() {
        return parameters.get(Constants.IP_PORT).toInt();
    }

    private IpTransportType getIpConnectionTransportType() {
        return IpTransportType.valueOf(parameters.getString(Constants.IP_TRANSPORT_TYPE));
    }

    @Override
    protected BasicProcessImage getProcessImage(SlaveDeviceNode slave) {
        IpTransportType type = getIpConnectionTransportType();
        int port = getConnectionPort();
        int slaveId = slave.getDeviceSlaveID();
        switch (type) {
            case TCP:
                return ModbusSlaveHandler.getTcpProcessImage(port, slaveId, slave);
            case UDP:
                return ModbusSlaveHandler.getUdpProcessImage(port, slaveId, slave);
            default:
                throw new RuntimeException("Only TCP and UDP transports supported for IP Connections");
        }
    }

    @Override
    protected void deleteProcessImage(SlaveDeviceNode slave) {
        IpTransportType type = getIpConnectionTransportType();
        int port = getConnectionPort();
        int slaveId = slave.getDeviceSlaveID();
        switch (type) {
            case TCP:
                ModbusSlaveHandler.deleteTcpProcessImage(port, slaveId);
                break;
            case UDP:
                ModbusSlaveHandler.deleteUdpProcessImage(port, slaveId);
                break;
        }
    }

}
