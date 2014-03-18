package at.fhv.smartdevices.anext.nodes;

import at.anext.os.Event;
import at.anext.os.firmware.devsupport.AbstractNode;
import at.fhv.smartdevices.anext.SmartDeviceConstants;
import static at.fhv.smartdevices.anext.SmartDeviceConstants.*;

public class SmartDeviceNode extends AbstractNode {

	private static SmartDeviceNode instance;

	private float temperatureA, temperatureB, temperatureC,
			temperatureD = -273.15f;

	@Override
	public void cleanup() {
		// wird aufgerufen wenn node gelöscht wird
		log.debug("cleanup()");
		// TODO Auto-generated method stub
	}

	@Override
	public void init() {
		instance = this;
	}

	@Override
	public void nodeDescChange() {
		log.debug("nodeDescChange()");
	}

	public static SmartDeviceNode getInstance() {
		return instance;
	}

	@Override
	public void setProperty(String key, String value) {
		System.out.println("setProperty " + key + "/" + value);
		// wird aufgerufen bei jeder property Änderung
		super.setProperty(key, value);
		if (isInitialized()) {
			// alle die hier sind sind vom user gemacht
		}
	}

	public void onTemperatureA(Event event) {
		temperatureA = event.getFloat(VALUE, 0);
	}

	public void onTemperatureB(Event event) {
		temperatureB = event.getFloat(VALUE, 0);
	}

	public void onTemperatureC(Event event) {
		temperatureC = event.getFloat(VALUE, 0);
	}

	public void onTemperatureD(Event event) {
		temperatureD = event.getFloat(VALUE, 0);
	}

	public void onPriceSignal(Event event) {
		// handle price signal
	}

	public float getTemperatureA() {
		return temperatureA;
	}

	public float getTemperatureB() {
		return temperatureB;
	}

	public float getTemperatureC() {
		return temperatureC;
	}

	public float getTemperatureD() {
		return temperatureD;
	}

	public void setPowerState(boolean newState) {
		Event pwr = createEvent(SmartDeviceConstants.OUTPUT_EVENT_POWER);
		pwr.setProperty(SmartDeviceConstants.VALUE, newState);
		updateOutput(pwr);
	}

	public boolean getPowerState() {
		Object output = getOutputValue(SmartDeviceConstants.OUTPUT_EVENT_POWER,
				SmartDeviceConstants.VALUE);
		return Boolean.parseBoolean((String) output);
	}
}
