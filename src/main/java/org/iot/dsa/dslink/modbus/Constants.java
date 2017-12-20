package org.iot.dsa.dslink.modbus;

public class Constants {
    public static final String PING_RATE = "Ping Rate";
    public static final String IP_TRANSPORT_TYPE = "Transport Type";
    public static final String IP_HOST = "Host";
    public static final String IP_PORT = "Port";

    public static enum IpTransportType {
        TCP,
        UDP
    }
    
    public static final String SLAVE_ID = "Slave ID";
}
