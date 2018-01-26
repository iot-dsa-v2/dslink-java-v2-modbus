package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ip.IpParameters;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.IPBounds;
import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.dslink.modbus.utils.Constants.IpTransportType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;

import java.util.ArrayList;
import java.util.List;

public class IPConnectionNode extends ModbusConnectionNode {

    protected static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.IP_TRANSPORT_TYPE,
                DSJavaEnum.valueOf(IpTransportType.TCP), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBounds(Constants.IP_HOST,
                DSValueType.STRING, new IPBounds(), null, "10.0.1.199"));
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.IP_PORT,
                DSLong.valueOf(Constants.DEFAULT_IP_PORT), new IntegerBounds(0, Constants.UNSIGED_SHORT_MAX), null, null));
        ModbusConnectionNode.addCommonParameterDefinitions(parameterDefinitions);
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    public IPConnectionNode() {
        super();
    }

    public IPConnectionNode(DSMap parameters) {
        super(parameters);
    }

    @Override
    public boolean createConnection() {
        IpTransportType ipType = IpTransportType.valueOf(parameters.get(Constants.IP_TRANSPORT_TYPE).toString());
        String host = parameters.getString(Constants.IP_HOST);
        int port = parameters.getInt(Constants.IP_PORT);
        boolean keepAlive = true;

        IpParameters params = new IpParameters();
        params.setHost(host);
        params.setPort(port);

        switch (ipType) {
            case TCP:
                master = modbusFactory.createTcpMaster(params, keepAlive);
                break;
            case UDP:
                master = modbusFactory.createUdpMaster(params);
                break;
        }

        return super.createConnection();
    }
}
