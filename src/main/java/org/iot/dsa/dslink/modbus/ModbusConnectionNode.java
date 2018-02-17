package org.iot.dsa.dslink.modbus;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReportSlaveIdRequest;
import org.iot.dsa.dslink.dframework.DFConnectionNode;
import org.iot.dsa.dslink.dframework.DFUtil;
import org.iot.dsa.dslink.dframework.ParameterDefinition;
import org.iot.dsa.dslink.dframework.bounds.DoubleBounds;
import org.iot.dsa.dslink.dframework.bounds.EnumBounds;
import org.iot.dsa.dslink.dframework.bounds.IntegerBounds;
import org.iot.dsa.dslink.modbus.utils.Constants;
import org.iot.dsa.node.*;
import java.net.SocketTimeoutException;
import java.util.List;

public abstract class ModbusConnectionNode extends DFConnectionNode {

    protected static void addCommonParameterDefinitions(List<ParameterDefinition> definitions) {
        definitions.add(ParameterDefinition.makeParamWithBoundsAndDef(
                Constants.PING_RATE,
                DSDouble.valueOf(Constants.DEFAULT_PING_RATE),
                new DoubleBounds(.001, Double.MAX_VALUE),
                "interval between pings, in seconds",
                null)
        );
        definitions.add(ParameterDefinition.makeParamWithBoundsAndDef(
                Constants.TIMEOUT,
                DSInt.valueOf(Constants.DEFAULT_TIMEOUT),
                new IntegerBounds(1, Integer.MAX_VALUE),
                null,
                null)
        );
        definitions.add(ParameterDefinition.makeParamWithBoundsAndDef(
                Constants.RETRIES,
                DSInt.valueOf(Constants.DEFAULT_RETRIES),
                new IntegerBounds(0, Integer.MAX_VALUE),
                null,
                null)
        );
        definitions.add(ParameterDefinition.makeParamWithBoundsAndDef(
                Constants.USE_MULTIPLE_WRITE_COMMAND,
                DSJavaEnum.valueOf(Constants.MultipleWriteEnum.DEFAULT),
                new EnumBounds(DSJavaEnum.valueOf(Constants.MultipleWriteEnum.DEFAULT)),
                null,
                null)
        );
    }


    public ModbusConnectionNode() {

    }

    public ModbusConnectionNode(DSMap parameters) {
        this.parameters = parameters;
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACTION_ADD_DEVICE, DFUtil.getAddAction(ModbusDeviceNode.class));
    }

    @Override
    protected void onStable() {
        super.onStable();
    }

    /* ==================================================================== */
    ModbusMaster master;
    ModbusFactory modbusFactory = new ModbusFactory();


    @Override
    public boolean createConnection() {

        int timeout = parameters.get(Constants.TIMEOUT).toInt();
        int retries = parameters.get(Constants.RETRIES).toInt();
        if (parameters.get(Constants.USE_MULTIPLE_WRITE_COMMAND).toString().equals(Constants.MultipleWriteEnum.ALWAYS.toString())) {
            master.setMultipleWritesOnly(true);
        }

        master.setTimeout(timeout);
        master.setRetries(retries);

        try {
            master.init();
            return master.isInitialized();
        } catch (ModbusInitException e) {
            warn(e);
            return false;
        }
    }


    @Override
    public boolean ping() {
        if (!master.isInitialized()) {
            return false;
        }
        try {
            master.send(new ReportSlaveIdRequest(1));
        } catch (ModbusTransportException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void closeConnection() {
        master.destroy();
        master = null;
    }

    @Override
    public long getPingRate() {
        DSElement rate = parameters.get(Constants.PING_RATE);
        double seconds = Constants.DEFAULT_PING_RATE;
        if (rate != null && rate.isNumber()) {
            seconds = rate.toDouble();
        }
        return (long) (seconds * 1000);
    }

}
