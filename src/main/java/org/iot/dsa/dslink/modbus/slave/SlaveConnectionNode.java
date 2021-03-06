package org.iot.dsa.dslink.modbus.slave;

import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.EditableNode;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import com.serotonin.modbus4j.BasicProcessImage;

public abstract class SlaveConnectionNode extends EditableNode {
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACTION_ADD_SLAVE, DFUtil.getAddAction(SlaveDeviceNode.class));
        declareDefault("Remove", makeRemoveAction());
    }
    
    protected abstract BasicProcessImage getProcessImage(SlaveDeviceNode slave);
    
    protected abstract void deleteProcessImage(SlaveDeviceNode slave);
    
    @Override
    public void delete() {
        stopSlaves();
        super.delete();
    }
    
    @Override
    public void preEdit(DSMap newParameters) {
        super.preEdit(newParameters);
        stopSlaves();
    }
    
    @Override
    public void onEdit() {
        super.onEdit();
        startSlaves();
    }
    
    protected void stopSlaves() {
        for (DSInfo info: this) {
            if (info.isNode() && info.getNode() instanceof SlaveDeviceNode) {
                SlaveDeviceNode device = (SlaveDeviceNode) info.getNode();
                device.stopSlave();
            }
        }
    }
    
    protected void startSlaves() {
        for (DSInfo info: this) {
            if (info.isNode() && info.getNode() instanceof SlaveDeviceNode) {
                SlaveDeviceNode device = (SlaveDeviceNode) info.getNode();
                device.startSlave();
            }
        }
    }

}
