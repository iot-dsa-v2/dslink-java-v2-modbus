package org.iot.dsa.dslink.modbus.slave;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ProcessImageListener;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.*;

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
    private DSInfo error = getInfo(Constants.SLAVE_ERROR);

    static {
        parameterDefinitions.add(ParameterDefinition.makeParamWithBoundsAndDef(Constants.SLAVE_ID, DSLong.valueOf(Constants.DEFAULT_SLAVE_ID), new IntegerBounds(1, 247), null, null));
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
        declareDefault(Constants.ACTION_ADD_SLAVE_POINT, DFUtil.getAddAction(SlavePointNode.class));
        declareDefault("Remove", makeRemoveAction());
        declareDefault(Constants.SLAVE_ERROR, DSString.EMPTY).setHidden(true).setReadOnly(true);
    }

    @Override
    protected void onStable() {
        super.onStable();
        startSlave();
    }

    int getDeviceSlaveID() {
        return parameters.get(Constants.SLAVE_ID).toInt();
    }
    
    SlaveConnectionNode getParentNode() {
        DSNode parent = getParent();
        if (parent instanceof SlaveConnectionNode) {
            return (SlaveConnectionNode) parent;
        } else {
            throw new RuntimeException("Wrong parent class, expected SlaveConnectionNode");
        }
    }

    public void startSlave() {
        if (procImg == null) {
            procImg = getParentNode().getProcessImage(this);
            for (DSInfo info: this) {
                if (info.isNode() && info.getNode() instanceof SlavePointNode) {
                    SlavePointNode point = (SlavePointNode) info.getNode();
                    point.submitToSlaveHandler();
                }
            }
        }
    }

    public boolean registerCoilPoint(int offset, SlavePointNode node) {
        if (offsetToCoilNode.get(offset) != null) return false;
        offsetToCoilNode.put(offset,node);
        return true;
    }

    public SlavePointNode getCoilPoint(int offset) {
        return offsetToCoilNode.get(offset);
    }

    public SlavePointNode removeCoilPoint(int offset) {
        return offsetToCoilNode.remove(offset);
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

    public List<SlavePointNode> removeHoldingPoints(int offset) {
        return offsetToHoldingList.remove(offset);
    }

    @Override
    public void preEdit(DSMap newParameters) {
        super.preEdit(newParameters);
        stopSlave();
        procImg = null;
    }

    @Override
    public void onEdit() {
        startSlave();
    }

    @Override
    public void delete() {
        super.delete();
        stopSlave();
    }
    
    public void stopSlave() {
        List<SlavePointNode> toStop = new ArrayList<SlavePointNode>();
        for (SlavePointNode point : offsetToCoilNode.values()) {
            toStop.add(point);
        }
        for (List<SlavePointNode> pointList : offsetToHoldingList.values()) {
            for (SlavePointNode point : pointList) {
                toStop.add(point);
            }
        }
        for (SlavePointNode point: toStop) {
            point.escapeSlaveHandler();
        }
        getParentNode().deleteProcessImage(this);
    }

    public BasicProcessImageListener makeListener() {
        return new BasicProcessImageListener();
    }

    public void setError(String err) {
        error.setHidden(false);
        put(error, DSString.valueOf(err));
    }

    public void clearError() {
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
