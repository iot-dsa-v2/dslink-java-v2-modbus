package org.iot.dsa.dslink.modbus;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.Random;
import org.iot.dsa.dslink.modbus.ModbusPointNode;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.DSMap;

public class ModbusPointNodeTests {
    private static double DELTA = .00001;
    
    @Test
    public void applyScalingNoChange() {
        ModbusPointNode mpn = new ModbusPointNode();
        mpn.parameters = new DSMap().put(Constants.SCALING, 1).put(Constants.SCALING_OFFSET, 0);
        assertEquals(28.6, mpn.applyScaling(28.6), DELTA);
        assertEquals(0.0, mpn.applyScaling(0.0), DELTA);
        assertEquals(-1.0, mpn.applyScaling(-1.0), DELTA);
    }
    
    @Test
    public void applyScalingOnlyAdd() {
        ModbusPointNode mpn = new ModbusPointNode();
        mpn.parameters = new DSMap().put(Constants.SCALING, 1).put(Constants.SCALING_OFFSET, 6);
        assertEquals(6.0, mpn.applyScaling(0.0), DELTA);
        assertEquals(4.8, mpn.applyScaling(-1.2), DELTA);
    }
    
    @Test
    public void applyScalingMultAdd() {
        ModbusPointNode mpn = new ModbusPointNode();
        mpn.parameters = new DSMap().put(Constants.SCALING, 3).put(Constants.SCALING_OFFSET, 6);
        assertEquals(6.0, mpn.applyScaling(0.0), DELTA);
        assertEquals(2.4, mpn.applyScaling(-1.2), DELTA);
    }
    
    @Test
    public void applyScalingDivAdd() {
        ModbusPointNode mpn = new ModbusPointNode();
        mpn.parameters = new DSMap().put(Constants.SCALING, .25).put(Constants.SCALING_OFFSET, 6);
        assertEquals(6.0, mpn.applyScaling(0.0), DELTA);
        assertEquals(-6.0, mpn.applyScaling(-48.0), DELTA);
    }
    
    @Test
    public void removeScalingNoChange() {
        ModbusPointNode mpn = new ModbusPointNode();
        mpn.parameters = new DSMap().put(Constants.SCALING, 1).put(Constants.SCALING_OFFSET, 0);
        assertEquals(28.6, mpn.removeScaling(28.6), DELTA);
        assertEquals(0.0, mpn.removeScaling(0.0), DELTA);
        assertEquals(-1.0, mpn.removeScaling(-1.0), DELTA);
    }
    
    @Test
    public void removeScalingReversesApplyScaling() {
        Random rand = new Random(65535);
        ModbusPointNode mpn = new ModbusPointNode();
        for (int i=0; i<100; i++) {
            mpn.parameters = new DSMap().put(Constants.SCALING, randomDouble(rand)).put(Constants.SCALING_OFFSET, randomDouble(rand));
            double num = randomDouble(rand);
            assertEquals(num, mpn.removeScaling(mpn.applyScaling(num)), DELTA);
            assertEquals(num, mpn.applyScaling(mpn.removeScaling(num)), DELTA);
        }
    }
    
    private double randomDouble(Random rand) {
        return (rand.nextInt(200) - 100) + rand.nextDouble();
    }

}
