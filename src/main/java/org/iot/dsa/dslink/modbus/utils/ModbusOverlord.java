package org.iot.dsa.dslink.modbus.utils;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.iot.dsa.dslink.dframework.DFPointNode;
import org.iot.dsa.dslink.modbus.ModbusDeviceNode;
import org.iot.dsa.dslink.modbus.ModbusPointNode;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;

public class ModbusOverlord {
    
    private ModbusMaster master;
    private final Lock lock = new ReentrantLock(true);
    
    public ModbusOverlord(ModbusMaster master) {
        this.master = master;
    }
    
    /* ===== OPERATIONS =========================================================== */
    
    public ModbusResponse send(ModbusRequest request, ModbusDeviceNode dev, int slaveid) throws ModbusTransportException {
        lock.lock();
        dev.info(Thread.currentThread().getId() + ") Ping Middle: " + slaveid);
        try {
            return master.send(request);
        } finally {
            lock.unlock();
        }
    }
    
    public BatchResults<ModbusPointNode> sendBatchRead(Set<DFPointNode> points, ModbusDeviceNode dev, int slaveid) throws ModbusTransportException, ErrorResponseException {
       lock.lock();
       dev.info(Thread.currentThread().getId() + ") Poll Middle: " + slaveid);
       BatchRead<ModbusPointNode> batch = dev.makeBatch(points);
       try {
           return master.send(batch);
       } finally {
           lock.unlock();
       }
    }
    
    public synchronized <T> void setValue(BaseLocator<T> locator, Object value) throws ModbusTransportException, ErrorResponseException {
        lock.lock();
        try {
            master.setValue(locator, value);
        } finally {
            lock.unlock();
        }
    }
    
    /* ===== CONTROL ============================================================== */
    
    public void init() throws ModbusInitException {
        master.init();
    }
    
    public void destroy() {
        master.destroy();
    }
    
    /* ===== STATUS =============================================================== */
    
    public boolean isInitialized() {
        return master.isInitialized();
    }
    
    /* ===== CONFIGURATION ======================================================== */
    
    public void setTimeout(int timeout) {
        master.setTimeout(timeout);
    }
    
    public void setRetries(int retries) {
        master.setRetries(retries);
    }
    
    public void setMultipleWritesOnly(boolean multipleWritesOnly) {
        master.setMultipleWritesOnly(multipleWritesOnly);
    }
}
