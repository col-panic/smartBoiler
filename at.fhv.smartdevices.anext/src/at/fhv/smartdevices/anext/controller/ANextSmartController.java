package at.fhv.smartdevices.anext.controller;

import java.util.ArrayList;
import java.util.List;

import at.anext.os.Event;
import at.fhv.smartdevices.anext.SmartDeviceConstants;
import at.fhv.smartdevices.anext.nodes.SmartDeviceNode;
import at.fhv.smartdevices.common.ISmartController;
import at.fhv.smartdevices.common.ImpulsCounterInformation;
import at.fhv.smartdevices.common.MarketPriceAtom;
import at.fhv.smartdevices.common.SensorInformation;

public class ANextSmartController implements ISmartController {

	@Override
	public List<SensorInformation> getSensorInformation() {
		List<SensorInformation> ret = new ArrayList<SensorInformation>();
		
		ret.add(new SensorInformation("temperatureA", SmartDeviceNode.getInstance().getTemperatureA()));
		ret.add(new SensorInformation("temperatureB", SmartDeviceNode.getInstance().getTemperatureB()));
		ret.add(new SensorInformation("temperatureC", SmartDeviceNode.getInstance().getTemperatureC()));
		ret.add(new SensorInformation("temperatureD", SmartDeviceNode.getInstance().getTemperatureC()));
		
		return ret;
	}

	@Override
	public List<ImpulsCounterInformation> getImpulsCounterInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCurrentMarketPricesListTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MarketPriceAtom> getCurrentMarketPrices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRelaisPowerState(boolean newState) {
		SmartDeviceNode.getInstance().setPowerState(newState);
	}

	@Override
	public boolean getRelaisPowerState() {
		return SmartDeviceNode.getInstance().getPowerState();
	}

}
