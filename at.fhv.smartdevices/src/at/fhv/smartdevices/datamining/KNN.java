/**
 * 
 */
package at.fhv.smartdevices.datamining;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.primitives.*;
import org.apache.commons.collections.primitives.adapters.DoubleListList;
import org.apache.commons.collections.primitives.adapters.ListDoubleList;

import at.fhv.smartdevices.commons.INumericMetric;
import at.fhv.smartdevices.commons.ISchedulable;
import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.scheduling.DataAquisition;
import at.fhv.smartdevices.scheduling.SchedulableSwitch;
import at.fhv.smartdevices.singleNodeDHWH.DemandCalculationModel;

/**
 * @author kepe
 *
 */
public class KNN implements ISchedulable{
	
	private DataAquisition _dataAquisition;
	private SchedulableSwitch _switcher;
	private Lock _lock = new ReentrantLock();
	private final String FILE_DEMAND="/dataStore/demand";
	private long _deltat=60000; // in milliseconds.
	private String _tempID;
	private INumericMetric _metric;
	private long _timeStepsBack=60*12;//in delta t
	private long _timeStepsForward = 60*24; // in delta t
	private long _timeStep;
	private static short _k;
	
	private static long getDayInDeltatSteps(long deltat){
		return (24*60*60*1000)/deltat;
	}
	
	
	public KNN(DataAquisition dA,  SchedulableSwitch switcher, String tempID, INumericMetric metric, long timeStep){
		_tempID=tempID;		
		_dataAquisition = dA;
		_switcher = switcher;
		_metric=metric;
		_timeStep = timeStep;
	}
	
	/**
	 * 
	 * @param relaisStateHistory
	 * @param sensorInfoHistory
	 * @param deltat
	 * @param timeStepsBack
	 * @param timeStepsForward
	 * @return
	 */
	static SerializableTreeMap<Long, Boolean> calculateSwitchingTimes(SerializableTreeMap<Long, Boolean> relaisStateHistory, SerializableTreeMap<Long, Float> sensorInfoHistory, long deltat, long timeStepsBack, long timeStepsForward){
		
		TreeMap<Long,double[]> historicData = getHistoricData(relaisStateHistory,sensorInfoHistory, deltat, timeStepsBack, timeStepsForward);
		double[] currentTimeSeries= historicData.lastEntry().getValue();
		TreeMap<Long,Double> distances = new TreeMap<Long,Double>();
		for (Long key : historicData.navigableKeySet()) {
			double d = MetricHelper.calculateMinkowskiMetric(historicData.get(key), currentTimeSeries, 2);
			distances.put(key, d);
		}
		
		short i=0;
		double[][] candidates =  new double[_k][];
		for(Long key : distances.navigableKeySet()){
			if(i>=_k){
				break;
			}
			else{
				candidates[i] = historicData.get(key);
			}
		}
		
		
		
		return null;	
	}
	
	
	/**
	 * 
	 * @param relaisStateHistory
	 * @param sensorInfoHistory
	 * @param deltat
	 * @param timeStepsBack
	 * @param timeStepsForward
	 * @return
	 */
	static TreeMap<Long,double[]> getHistoricData(SerializableTreeMap<Long, Boolean> relaisStateHistory, SerializableTreeMap<Long, Float> sensorInfoHistory, long deltat, long timeStepsBack, long timeStepsForward){		
		SerializableTreeMap<Long, Double> demands = DemandCalculationModel.calculateDemand(relaisStateHistory, sensorInfoHistory, deltat, true);
			
		long[] cuttingPoints = findCuttingPoints(demands,timeStepsBack, deltat);
		TreeMap<Long, double[]> historicData = new TreeMap<Long, double[]>();			
		for (int i = 0; i < cuttingPoints.length; i++) {
			NavigableMap<Long, Double> submap = demands.subMap(cuttingPoints[i], true, cuttingPoints[i]+(deltat*timeStepsBack)+(deltat*timeStepsForward), true);
			
			DoubleList values =  new ArrayDoubleList();
			for (double value : submap.values()) {				
				values.add(value);
			}						
			historicData.put(cuttingPoints[i], values.toArray());							
		}		
		return historicData;
	}	
	

	/**
	 * 
	 * @param demands
	 * @param timeStepsBack
	 * @param deltat
	 * @return
	 */
	private static long[] findCuttingPoints(TreeMap<Long, Double> demands, long timeStepsBack, long deltat) {
		
		ArrayLongList retVal = new ArrayLongList();		
		for (int counter=0; counter<Integer.MAX_VALUE; counter++){
			Long key = demands.floorKey(demands.lastKey()-(timeStepsBack*deltat)-(getDayInDeltatSteps(deltat)*deltat*counter));
			if(key!=null)
			{
				retVal.add(key);
			}
			else{
				break;
			}
		}
		
		return retVal.toArray();
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
			_switcher.setSwitchingTimes(calculateSwitchingTimes(_dataAquisition.getRelaisStateHistory(), _dataAquisition.GetSensorInformationHistory().get(_tempID), _deltat, _timeStepsBack, _timeStepsForward));
			
		}
		finally
		{
			_lock.unlock();
		}
	}
}
