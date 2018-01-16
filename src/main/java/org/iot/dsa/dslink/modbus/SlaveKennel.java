package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusSlaveSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author James (Juris) Puchin
 * Created on 1/15/2018
 */
abstract class SlaveKennel {
    private final Map<Integer, ModbusSlaveSet> slaveSets = new ConcurrentHashMap<>();

    abstract ModbusSlaveSet createSlaveSet(int port, boolean encapsulated);

    public ModbusSlaveSet get(int port) {
        return slaveSets.get(port);
    }

    public ModbusSlaveSet remove(int port) {
        return slaveSets.remove(port);
    }

    public ModbusSlaveSet put(int port, ModbusSlaveSet set) {
        return slaveSets.put(port, set);
    }
}
