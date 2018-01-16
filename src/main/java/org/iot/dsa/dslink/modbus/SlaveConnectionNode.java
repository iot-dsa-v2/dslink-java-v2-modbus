package org.iot.dsa.dslink.modbus;

import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.EditableNode;
import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusSlaveSet;

public abstract class SlaveConnectionNode extends EditableNode {
    
    ModbusSlaveSet slaveSet = null;
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACTION_ADD_SLAVE, DFUtil.getAddAction(SlaveDeviceNode.class));
        declareDefault("Remove", makeRemoveAction());
//        declareDefault(Constants.SLAVE_ERROR, DSString.EMPTY).setHidden(true).setReadOnly(true);
    }
    
    protected abstract BasicProcessImage getProcessImage(SlaveDeviceNode slave);
    
    protected abstract void deleteProcessImage(SlaveDeviceNode slave);

}
