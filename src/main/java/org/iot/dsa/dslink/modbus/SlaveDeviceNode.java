package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSLong;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/8/2018
 */
public class SlaveDeviceNode extends EditableNode {

    public static List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
    private Map<Integer, SlavePointNode> offsetToPointNode = new ConcurrentHashMap<>();
    BasicProcessImage procImg = null;

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

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Slave Point", DFUtil.getAddAction(SlavePointNode.class));
        declareDefault("Remove", makeRemoveAction());
    }

    @Override
    protected void onStarted() {
        super.onStarted();
        startSlave();
    }

    //TODO: Check if any errors are thrown by the slave device creation

    private void startSlave() {
        if (procImg == null) {
            int port = parameters.get(Constants.IP_PORT).toInt();
            int slaveId = parameters.get(Constants.SLAVE_ID).toInt();
            procImg = TcpSlaveHandler.getProcessImage(port, slaveId);
        }
    }

    public void registerSlavePoint(int offset, SlavePointNode node) {
        offsetToPointNode.put(offset,node);
    }

    public SlavePointNode getSlavePointFromOffset(int offset) {
        return offsetToPointNode.get(offset);
    }

    @Override
    public void onEdit() {
        // TODO Auto-generated method stub
        
    }
}
