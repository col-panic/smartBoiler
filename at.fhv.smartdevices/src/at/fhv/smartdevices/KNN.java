/**
 * 
 */
package at.fhv.smartdevices;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import at.fhv.smartdevices.commons.ISchedulable;
import at.fhv.smartdevices.commons.SerializableTreeMap;

/**
 * @author kepe
 *
 */
public class KNN implements ISchedulable{

	private long _timeStep = 3600*1000;
	private DataAquisition _dataAquisition;
	private SchedulableSwitch _switcher;
	private Lock _lock = new ReentrantLock();
	
	public KNN(long timestep, DataAquisition dA,  SchedulableSwitch switcher){
		
		_timeStep=timestep;
		_dataAquisition = dA;
		_switcher = switcher;
	}
	
	private  SerializableTreeMap<Long, Boolean> CalculateSwitchingTimes(){
		 //TODO
		 return null;
	}
	
	@Override
	public long getScheduleTimeStep() {
				return _timeStep;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public Boolean getExeSingleThreaded() {
		return false;
	}

	@Override
	public void run() {
		_lock.lock();
		try
		{
			_switcher.setSwitchingTimes(CalculateSwitchingTimes());
			
		}
		finally
		{
			_lock.unlock();
		}
	}
}
