package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import java.util.ArrayList;
import java.util.List;

/**
 * @author James (Juris) Puchin
 * Created on 1/8/2018
 */
public class SlaveDeviceNode extends EditableNode {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();

    static {
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.SLAVE_ID, DSLong.valueOf(Constants.DEFAULT_SLAVE_ID), null, null));
        parameterDefinitions.add(ParameterDefinition.makeParamWithDefault(Constants.IP_PORT, DSLong.valueOf(Constants.DEFAULT_SLAVE_IP_PORT), null, null));
    }
    
    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    public SlaveDeviceNode() {

    }

    public SlaveDeviceNode(DSMap parameters) {
        this.parameters = parameters;
    }

    @Override
    protected void onStarted() {
        super.onStarted();
        startSlave();
    }

    private void startSlave() {
        int port = parameters.get(Constants.IP_PORT).toInt();
        TcpSlaveHandler.getProcessImage(port, 1);
    }

    public void addSlavePoint(SlavePointNode node) {

    }

    @Override
    public void onEdit() {
        // TODO Auto-generated method stub
        
    }
}
