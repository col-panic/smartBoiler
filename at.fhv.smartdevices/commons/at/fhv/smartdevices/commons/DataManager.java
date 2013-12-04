/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.*;

import org.simpleframework.xml.*;

import at.fhv.smartgrid.rasbpi.*;
import at.fhv.smartgrid.rasbpi.internal.*;

/**
 * @author kepe_nb
 *
 */

public class DataManager {
	
	private ISmartController _controller;
		
	private long _timeStamp;
	private long _pricesTimeStamp=-1;
		
	//private SensorInformationHistory _sensorInformationHistory;
	private HashMap<String, HashMap<Long,Float>> _sensorInformationHistory;
	private HashMap<Long, Boolean> _relaisPowerStateHistory;
	
	private HashMap<Long, Integer> _pricesHistory;
	

	/**
	 * 
	 * @param controller ... the smart controller to collect the data from
	 */
	public DataManager(ISmartController controller)
	{
		_controller = controller;
		
		_pricesHistory = new HashMap<Long, Integer>();		
		
		_sensorInformationHistory = createSensorHistoryMaps(_controller.getSensorInformation());
		
		_relaisPowerStateHistory = new HashMap<Long,Boolean>();
		
		
		
		/*TODO:impuls counter information
		 * List<ImpulsCounterInformation> iciList = _controller.getImpulsCounterInformation();
		for (ImpulsCounterInformation ici : iciList) {
			ici.countingStart.getTime();
			ici.
		}*/
		
	}

	
	

	/**
	 * Called to perform a data collection on sensors and relais
	 * @param dateTime ... should be the current timestamp
	 */
	public void collectData(long dateTime) {
		_timeStamp = dateTime;
		
		List<SensorInformation> siList = _controller.getSensorInformation();
		for (SensorInformation si : siList) {	
			Float newValue = si.getSensorValue();			
			HashMap<Long, Float> values = _sensorInformationHistory.get(si.getSensorId());
			values.put(dateTime, newValue);	
		}
		
		
		if(_pricesTimeStamp != _controller.getCurrentMarketPricesListTimestamp())
		{
			
		    List<MarketPriceAtom> atoms  = _controller.getCurrentMarketPrices();
		    if(!atoms.isEmpty())
		    {
		    	_pricesTimeStamp = _controller.getCurrentMarketPricesListTimestamp();
		    	for (MarketPriceAtom priceAtom : atoms) {
		    		_pricesHistory.put(priceAtom.validFrom,priceAtom.price);
		    	}
		    }
		}		
		_relaisPowerStateHistory.put(_timeStamp, _controller.getRelaisPowerState());
		
		persistData();
		//TODO: collect impuls counter info.
	}
	
		
	/**
	 * Helper method to create the sensor history data structure
	 * @returns a hashmap
	 */
	private HashMap<String,HashMap<Long,Float>> createSensorHistoryMaps(List<SensorInformation> siList) {
		HashMap<String,HashMap<Long,Float>> retVal =  new HashMap<String,HashMap<Long,Float>>();			
		
		for (SensorInformation si : siList) {			
			HashMap<Long,Float> siHistoryMap = new HashMap<Long,Float>();
			retVal.put(si.getSensorId(), siHistoryMap);		
		}
		return retVal;
	}
	
	/**
	 * Helper method to serialize the data
	 */
	private void persistData()
	{
		
	}	
}
