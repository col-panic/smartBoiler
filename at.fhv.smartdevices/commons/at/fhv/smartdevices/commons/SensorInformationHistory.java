/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.HashMap;

import org.simpleframework.xml.*;

/**
 * @author kepe
 *
 */
@Root
public class SensorInformationHistory {
	
	//private HashMap<String, HashMap<Long,Float>> _sensorInformationHistory;
	@ElementMap(entry="valueHistory", key="timeStamp", attribute=true, inline=true)
	private HashMap<String, ValueHistory<Float>> _sensorInformationHistory;

	public HashMap<String, ValueHistory<Float>> getSensorInformationHistory() {
		return _sensorInformationHistory;
	}

	public void setSensorInformationHistory(HashMap<String, ValueHistory<Float>> _sensorInformationHistory) {
		this._sensorInformationHistory = _sensorInformationHistory;
	}

	
}
