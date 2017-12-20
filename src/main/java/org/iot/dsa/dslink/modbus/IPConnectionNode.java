package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.modbus.Constants.IpTransportType;
import org.iot.dsa.node.DSJavaEnum;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import com.serotonin.modbus4j.ip.IpParameters;

public class IPConnectionNode extends ModbusConnectionNode {
    

    static {
        //TODO add Modbus IP Connection parameters here
        parameterDefinitions.add(ParameterDefinition.makeEnumParam(Constants.IP_TRANSPORT_TYPE,
                DSJavaEnum.valueOf(IpTransportType.TCP), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParam(Constants.IP_HOST, DSValueType.STRING, null, "10.0.1.199"));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.IP_PORT, DSLong.valueOf(502), null, null));
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
        //params.setEncapsulated(encapsulated); TODO maybe use this 
        switch (ipType) {
            case TCP:
                master = modbusFactory.createTcpMaster(params , keepAlive);
                break;
            case UDP:
                master = modbusFactory.createUdpMaster(params);
                break;
        }        
        
        return super.createConnection();
    }

    

}
