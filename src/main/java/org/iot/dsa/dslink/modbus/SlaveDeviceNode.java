package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author James (Juris) Puchin
 * Created on 1/8/2018
 */
public class SlaveDeviceNode extends DSNode {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SLAVE_ID, DSLong.valueOf(Constants.DEFAULT_SLAVE_ID), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.IP_PORT, DSLong.valueOf(Constants.DEFAULT_SLAVE_IP_PORT), null, null));
    }

    DSMap parameters;

    public SlaveDeviceNode() {

    }

    public SlaveDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }

    @Override
    protected void onStarted() {
        if (this.parameters == null) {
            DSIObject o = get(Constants.PARAMETERS);
            if (o instanceof DSMap) {
                this.parameters = (DSMap) o;
            }
            Util.verifyParameters(parameters, parameterDefinitions);
        } else {
            Util.verifyParameters(parameters, parameterDefinitions);
            put(Constants.PARAMETERS, parameters.copy());
        }
        startSlave();
    }

    private void startSlave() {
        int port = parameters.get(Constants.IP_PORT).toInt();
        TcpSlaveHandler.getProcessImage(port, 1);
    }

    public void addSlavePoint(SlavePointNode node) {

    }
}
