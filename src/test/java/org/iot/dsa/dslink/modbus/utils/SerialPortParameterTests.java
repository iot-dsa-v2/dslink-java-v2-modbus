package org.iot.dsa.dslink.modbus.utils;

import org.iot.dsa.dslink.modbus.utils.Util.SerialPortLister;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMetadata;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.DSAction;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Iterator;


public class SerialPortParameterTests {
    
    @Test
    public void verifyDd() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap().put(Constants.SERIAL_PORT_DROPDOWN, "COM3");
        spp.verify(parameters);
        assertEquals("COM3", parameters.getString(Constants.SERIAL_PORT_MANUAL));
    }
    
    @Test
    public void verifyManual() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap().put(Constants.SERIAL_PORT_MANUAL, "COM3");
        spp.verify(parameters);
        assertEquals("COM3", parameters.getString(Constants.SERIAL_PORT_MANUAL));
    }
    
    @Test
    public void verifyOtherManual() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap().put(Constants.SERIAL_PORT_DROPDOWN, Constants.OTHER_SERIAL_PORT).put(Constants.SERIAL_PORT_MANUAL, "COM3");
        spp.verify(parameters);
        assertEquals("COM3", parameters.getString(Constants.SERIAL_PORT_MANUAL));
    }
    
    @Test
    public void verifyDdAndManual() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap().put(Constants.SERIAL_PORT_DROPDOWN, "COM4").put(Constants.SERIAL_PORT_MANUAL, "COM3");
        spp.verify(parameters);
        assertEquals("COM4", parameters.getString(Constants.SERIAL_PORT_MANUAL));
    }
    
    @Test
    public void verifyMissing() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap();
        try {
            spp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter " + Constants.SERIAL_PORT_MANUAL));
        }
    }
    
    @Test
    public void verifyWrongType() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap().put(Constants.SERIAL_PORT_DROPDOWN, Constants.OTHER_SERIAL_PORT).put(Constants.SERIAL_PORT_MANUAL, 34);
        try {
            spp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter " + Constants.SERIAL_PORT_MANUAL));
        }
    }
    
    @Test
    public void verifyEmpty() {
        SerialPortParameter spp = new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
        DSMap parameters = new DSMap().put(Constants.SERIAL_PORT_DROPDOWN, Constants.OTHER_SERIAL_PORT).put(Constants.SERIAL_PORT_MANUAL, "");
        try {
            spp.verify(parameters);
            fail("Expected verify to fail with an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Missing Parameter " + Constants.SERIAL_PORT_MANUAL));
        }
    }
    
    @Test
    public void addToAddActionNoAvailablePorts() {
        addToAddAction(new String[]{}, new DSList().add(Constants.OTHER_SERIAL_PORT));
    }
    
    @Test
    public void addToAddActionSomeAvailablePorts() {
        addToAddAction(new String[]{"COM2", "COM3", "COM7"}, new DSList().add("COM2").add("COM3").add("COM7").add(Constants.OTHER_SERIAL_PORT));
    }
    
    @Test
    public void addToEditActionNoPortsNoDefault() {
        addToEditAction(new String[]{}, new DSList().add(Constants.OTHER_SERIAL_PORT), null, Constants.OTHER_SERIAL_PORT, "");
    }
    
    @Test
    public void addToEditActionNoPortsYesDefault() {
        addToEditAction(new String[]{}, new DSList().add(Constants.OTHER_SERIAL_PORT), DSString.valueOf("COM3"), Constants.OTHER_SERIAL_PORT, "COM3");
    }
    
    @Test
    public void addToEditActionSomePortsIncludingDefault() {
        addToEditAction(new String[]{"COM2", "COM3", "COM7"}, new DSList().add("COM2").add("COM3").add("COM7").add(Constants.OTHER_SERIAL_PORT), DSString.valueOf("COM3"), "COM3", "");
    }
    
    @Test
    public void addToEditActionSomePointsOtherDefault() {
        addToEditAction(new String[]{"COM2", "COM3", "COM7"}, new DSList().add("COM2").add("COM3").add("COM7").add(Constants.OTHER_SERIAL_PORT), DSString.valueOf("COM4"), Constants.OTHER_SERIAL_PORT, "COM4");
    }
    
    private void addToAddAction(String[] actualPorts, DSList expectedPorts) {
        SerialPortParameter spp = beforeAddToAction(actualPorts);
        DSAction act = new DSAction();
        spp.addToAction(act);
        afterAddToAction(act, expectedPorts, Constants.OTHER_SERIAL_PORT, "");
    }
    
    private SerialPortParameter beforeAddToAction(String[] actualPorts) {
        SerialPortLister mockLister = mock(Util.SerialPortLister.class);
        when(mockLister.listPorts()).thenReturn(actualPorts);
        Util.serialPortLister = mockLister;
        return new SerialPortParameter(Constants.SERIAL_PORT_DROPDOWN, null, Constants.SERIAL_PORT_MANUAL, null);
    }
    
    private void afterAddToAction(DSAction act, DSList expectedPorts, String expectedDdDef, String expectedManualDef) {
        boolean hasDropdown = false;
        boolean hasManual = false;
        for (Iterator<DSMap> iter = act.getParameters(); iter.hasNext();) {
            DSMap param = iter.next();
            if (Constants.SERIAL_PORT_DROPDOWN.equals(param.getString(DSMetadata.NAME))) {
                hasDropdown = true;
                assertEquals(DSValueType.ENUM.toString(), param.getString(DSMetadata.TYPE));
                assertEquals(expectedPorts, param.get(DSMetadata.ENUM_RANGE));
                assertEquals(expectedDdDef, param.getString(DSMetadata.DEFAULT));
            } else if (Constants.SERIAL_PORT_MANUAL.equals(param.getString(DSMetadata.NAME))) {
                hasManual = true;
                assertEquals(DSValueType.STRING.toString(), param.getString(DSMetadata.TYPE));
                assertEquals(expectedManualDef, param.getString(DSMetadata.DEFAULT));
            }
        }
        assertTrue(hasDropdown && hasManual);
    }
    
    
    private void addToEditAction(String[] actualPorts, DSList expectedPorts, DSIValue def, String expectedDdDef, String expectedManualDef) {
        SerialPortParameter spp = beforeAddToAction(actualPorts);
        DSAction act = new DSAction();
        spp.addToAction(act, def);
        afterAddToAction(act, expectedPorts, expectedDdDef, expectedManualDef);
    }

}
