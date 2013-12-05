/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.io.File;
import java.util.*;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import at.fhv.smartgrid.rasbpi.*;
import at.fhv.smartgrid.rasbpi.internal.*;

/**
 * @author kepe_nb Central class to manage data acquisition
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
	 * Called to perform a data collection on sensors and relais
	 * 
	 * @param dateTime the current timestamp
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
			try {
				persistData();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper Method to check for locally stored data, create new objects
	 * otherwise.
	 */
	private void restoreData() {

		_costsHistory = deserialize(new SerializableTreeMap<Long, Integer>(), COSTS_FILENAME);

		_sensorInformationHistory = deserialize(createSensorHistoryMaps(_controller.getSensorInformation()),
				SIH_FILENAME);

		_relaisPowerStateHistory = deserialize(new SerializableTreeMap<Long, Boolean>(), RELAIS_FILENAME);

		_iciHistory = deserialize(new SerializableTreeMap<Long, Long>(), ICI_FILENAME);
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
	 * Tries to deserialize the data stored in the file to create an instance of
	 * type T and return that.
	 * 
	 * @param instance The instance to overwrite - is returned if deserialization
	 *            is not possible
	 * @param filename The file of serialized data
	 * @return the instance of type T resulting from deserialization, or the
	 *         passed instance otherwise.
	 */
	private <T> T deserialize(T instance, String filename) {
		Serializer serializer = new Persister();
		File file = new File(filename);
		if (file.exists()) {
			try {
				return serializer.read(instance, file);
			} catch (Exception e) {
				return instance;
			}
		}
		return instance;
	}

	/**
	 * Helper method to serialize the data
	 * 
	 * @throws Exception
	 */
	private void persistData() throws Exception {
		Serializer serializer = new Persister();
		File sIfile = new File(SIH_FILENAME);
		serializer.write(_sensorInformationHistory, sIfile);
		File pricesfile = new File(COSTS_FILENAME);
		serializer.write(_costsHistory, pricesfile);
		File relaisFile = new File(RELAIS_FILENAME);
		serializer.write(_relaisPowerStateHistory, relaisFile);
		File iciFile = new File(ICI_FILENAME);
		serializer.write(_iciHistory, iciFile);
	}
}
