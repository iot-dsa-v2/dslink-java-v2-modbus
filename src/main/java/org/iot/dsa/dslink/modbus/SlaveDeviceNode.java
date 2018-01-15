package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.ProcessImageListener;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSString;

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
    private Map<Integer, SlavePointNode> offsetToCoilNode = new ConcurrentHashMap<>();
    private Map<Integer, List<SlavePointNode>> offsetToHoldingList = new ConcurrentHashMap<>();

    BasicProcessImage procImg = null;
    private DSInfo error = getInfo(Constants.POINT_ERROR);

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

    private void startSlave() {
        if (procImg == null) {
            int port = parameters.get(Constants.IP_PORT).toInt();
            int slaveId = parameters.get(Constants.SLAVE_ID).toInt();
            procImg = TcpSlaveHandler.getProcessImage(port, slaveId, this);
        }
    }

    public void registerCoilPoint(int offset, SlavePointNode node) {
        offsetToCoilNode.put(offset,node);
    }

    public SlavePointNode getCoilPoint(int offset) {
        return offsetToCoilNode.get(offset);
    }

    public void registerHoldingPoint(int offset, SlavePointNode node) {
        List<SlavePointNode> lst = offsetToHoldingList.get(offset);
        if (lst == null) {
            lst = new ArrayList<>();
            lst.add(node);
            offsetToHoldingList.put(offset, lst);
        } else {
            if (!lst.contains(node)) {
                lst.add(node);
            }
        }
    }

    public List<SlavePointNode> getHoldingPoints(int offset) {
        return offsetToHoldingList.get(offset);
    }

    @Override
    public void onEdit() {
        // TODO Create on edit action
        
    }

    BasicProcessImageListener makeListener() {
        return new BasicProcessImageListener();
    }

    void setError(String err) {
        error.setHidden(false);
        put(error, DSString.valueOf(err));
    }

    void clearError() {
        error.setHidden(true);
        put(error, DSString.EMPTY);
    }

    private class BasicProcessImageListener implements ProcessImageListener {

        @Override
        public void coilWrite(int offset, boolean oldValue, boolean newValue) {
            if (oldValue != newValue) {
                SlavePointNode pointNode = getCoilPoint(offset);
                pointNode.updateValue(DSBool.valueOf(newValue));
            }
        }

        @Override
        public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
            if (oldValue != newValue) {
                List<SlavePointNode> pointNodes = getHoldingPoints(offset);
                for (SlavePointNode node : pointNodes) {
                    node.updatePointValue();
                }
            }
        }
    }
}
