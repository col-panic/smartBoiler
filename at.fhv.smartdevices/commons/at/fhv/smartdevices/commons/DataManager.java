/**
 * Central class to manage data acquisition and data persistence
 */
package at.fhv.smartdevices.commons;

import java.util.*;

import at.fhv.smartgrid.rasbpi.*;
import at.fhv.smartgrid.rasbpi.internal.*;

/**
 * @author kepe_nb 
 */

public class DataManager {

	private final String SIH_FILENAME = "sih.xml";
	private final String RELAIS_FILENAME = "relais.xml";
	private final String COSTS_FILENAME = "costs.xml";
	private final String ICI_FILENAME = "ici.xml";

	private ISmartController _controller;

	private long _timeStamp;
	private long _pricesTimeStamp = -1;

	private HashMap<String, Float> _sensorSensitivity;

	private SerializableTreeMap<String, SerializableTreeMap<Long, Float>> _sensorInformationHistory;

	private SerializableTreeMap<Long, Boolean> _relaisPowerStateHistory;

	private SerializableTreeMap<Long, Integer> _costsHistory;

	private SerializableTreeMap<Long, Long> _iciHistory;

	/**
	 * 
	 * @param controller the smart controller to collect the data from
	 */
	public DataManager(ISmartController controller) {
		_controller = controller;

		restoreData();

		// TODO! Generalize as input parameter check for errors
		_sensorSensitivity = new HashMap<String, Float>();
		for (String id : _sensorInformationHistory.keySet()) {
			_sensorSensitivity.put(id, (float) 1.0);
		}
	}

	/**
	 * Called to perform a data collection on the controller sensors and persist them
	 * 
	 * @param dateTime current time
	 */
	public void collectData(long dateTime) {
		_timeStamp = dateTime;
		Boolean changeInData = false;

		List<SensorInformation> siList = _controller.getSensorInformation();
		for (SensorInformation si : siList) {
			Float newValue = si.getSensorValue();
			SerializableTreeMap<Long, Float> values = _sensorInformationHistory.get(si.getSensorId());
			if (values.lastEntry() == null || 
					(Math.abs(values.lastEntry().getValue() - newValue) > _sensorSensitivity.get(si.getSensorId()))) {
				values.put(dateTime, newValue);
				changeInData = true;
			}
			_sensorInformationHistory.put(si.getSensorId(), values);
		}

		if (_pricesTimeStamp != _controller.getCurrentMarketPricesListTimestamp()) {

			List<MarketPriceAtom> atoms = _controller.getCurrentMarketPrices();
			if (!atoms.isEmpty()) {
				_pricesTimeStamp = _controller.getCurrentMarketPricesListTimestamp();
				for (MarketPriceAtom priceAtom : atoms) {
					_costsHistory.put(priceAtom.validFrom, priceAtom.price);
				}
				changeInData = true;
			}
		}
		Boolean currentState = _controller.getRelaisPowerState();
		if (_relaisPowerStateHistory.size() == 0 || _relaisPowerStateHistory.lastEntry().getValue() != currentState) {
			_relaisPowerStateHistory.put(_timeStamp, currentState);
			changeInData = true;
		}

		List<ImpulsCounterInformation> iciList = _controller.getImpulsCounterInformation();
		for (ImpulsCounterInformation ici : iciList) {
			long offset = ici.countingStart.getTime();
			if(!_iciHistory.containsKey(offset) || _iciHistory.get(offset)!=iciList.size()){
				_iciHistory.put(offset, (long) iciList.size());
				changeInData=true;
			}
		}

		if (changeInData) {
			persistData();
		}
	}
	
	/**
	 * @return the _sensorInformationHistory
	 */
	public SerializableTreeMap<String, SerializableTreeMap<Long, Float>> GetSensorInformationHistory()
	{
		return _sensorInformationHistory;
	}	
	
	/**
	 * @return the _pricesTimeStamp
	 */
	public long get_pricesTimeStamp() {
		return _pricesTimeStamp;
	}

	/**
	 * @return the _relaisPowerStateHistory
	 */
	public SerializableTreeMap<Long, Boolean> getRelaisPowerStateHistory() {
		return _relaisPowerStateHistory;
	}

	/**
	 * @return the _costsHistory
	 */
	public SerializableTreeMap<Long, Integer> getCostsHistory() {
		return _costsHistory;
	}

	/**
	 * @return the _iciHistory
	 */
	public SerializableTreeMap<Long, Long> getIciHistory() {
		return _iciHistory;
	}

	/**
	 * Helper method to create the sensor history data structure
	 * 
	 * @returns the created sensor information history object
	 */
	private SerializableTreeMap<String, SerializableTreeMap<Long, Float>> createSensorHistoryMaps(
			List<SensorInformation> siList) {
		SerializableTreeMap<String, SerializableTreeMap<Long, Float>> retVal = new SerializableTreeMap<String, SerializableTreeMap<Long, Float>>();

		for (SensorInformation si : siList) {
			SerializableTreeMap<Long, Float> siHistory = new SerializableTreeMap<Long, Float>();
			retVal.put(si.getSensorId(), siHistory);
		}
		return retVal;
	}	

	/**
	 * Helper Method to check for locally stored data, create new objects
	 * otherwise.
	 */
	private void restoreData() {

		_costsHistory = SerializationHelper.deserialize(new SerializableTreeMap<Long, Integer>(), COSTS_FILENAME);

		_sensorInformationHistory = SerializationHelper.deserialize(createSensorHistoryMaps(_controller.getSensorInformation()),
				SIH_FILENAME);

		_relaisPowerStateHistory = SerializationHelper.deserialize(new SerializableTreeMap<Long, Boolean>(), RELAIS_FILENAME);

		_iciHistory = SerializationHelper.deserialize(new SerializableTreeMap<Long, Long>(), ICI_FILENAME);
	}	

	/**
	 * Helper method to serialize all data
	 */	 
	private void persistData() {
		SerializationHelper.serialize(_sensorInformationHistory, SIH_FILENAME);
		SerializationHelper.serialize(_costsHistory, COSTS_FILENAME);
		SerializationHelper.serialize(_relaisPowerStateHistory, RELAIS_FILENAME);
		SerializationHelper.serialize(_iciHistory, ICI_FILENAME);
	}
}
