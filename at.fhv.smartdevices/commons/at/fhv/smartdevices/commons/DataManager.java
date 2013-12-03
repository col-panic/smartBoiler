/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.*;

import at.fhv.smartgrid.rasbpi.*;
import at.fhv.smartgrid.rasbpi.internal.*;

/**
 * @author kepe_nb
 *
 */
public class DataManager {

	private long _lastCollected;
	private ISmartController _controller;
	private BaseScheduler _scheduler;
	
	private HashMap<String, List<Float>> _sensorInformationHistory;
	private HashMap<Long, Boolean> _relaisPowerState;
	private long _pricesTimeStamp=-1;
	private HashMap<Long, Integer> _prices;

	public DataManager(ISmartController controller, BaseScheduler scheduler)
	{
		_lastCollected = scheduler.getDate();
		
		_scheduler = scheduler;
		_controller = controller;
		
		_prices = new HashMap<Long, Integer>();
		
		_sensorInformationHistory =  new HashMap<String,List<Float>>();		
		List<SensorInformation> siList = _controller.getSensorInformation();
		for (SensorInformation si : siList) {
			List<Float> values = new ArrayList<Float>();
			values.add(si.getSensorValue());
			_sensorInformationHistory.put(si.getSensorId(), values);
		}
		_relaisPowerState.put(_lastCollected, _controller.getRelaisPowerState());
		
		//TODO: implement impuls counter info.	
	}

	/**
	 * Called to perform a data collection on sensors and relais
	 */
	public void CollectData(long dateTime) {
		_lastCollected = _scheduler.getDate();
		
		List<SensorInformation> siList = _controller.getSensorInformation();
		for (SensorInformation si : siList) {			
			List<Float> values = _sensorInformationHistory.get(si.getSensorId());
			List<Float> newValues = new ArrayList<Float>();
			values.addAll(newValues);			
			_sensorInformationHistory.put(si.getSensorId(), values);
		}
		
		//TODO: implement impuls counter info.
		if(_pricesTimeStamp != _controller.getCurrentMarketPricesListTimestamp())
		{
			
		    List<MarketPriceAtom> atoms  = _controller.getCurrentMarketPrices();
		    if(!atoms.isEmpty())
		    {
		    	_pricesTimeStamp = _controller.getCurrentMarketPricesListTimestamp();
		    	for (MarketPriceAtom priceAtom : atoms) {
		    		_prices.put(priceAtom.validFrom,priceAtom.price);
		    	}
		    }
		}		
		_relaisPowerState.put(_lastCollected, _controller.getRelaisPowerState());
	}
	
	
}
