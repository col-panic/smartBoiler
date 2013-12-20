/**
 * 
 */
package at.fhv.smartdevices.datamining;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.primitives.ArrayFloatList;
import org.apache.commons.collections.primitives.ArrayLongList;
import org.apache.commons.collections.primitives.FloatCollection;

import at.fhv.smartdevices.commons.INumericMetric;
import at.fhv.smartdevices.commons.ISchedulable;
import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.helper.InterpolationHelper;
import at.fhv.smartdevices.helper.MapHelper;
import at.fhv.smartdevices.scheduling.DataAquisition;
import at.fhv.smartdevices.scheduling.SchedulableSwitch;
import at.fhv.smartdevices.singleNodeDHWH.DemandCalculationModel;
import at.fhv.smartgrid.rasbpi.internal.SensorInformation;

import org.apache.commons.collections.*;

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
	
	static SerializableTreeMap<Long, Boolean> calculateSwitchingTimes(SerializableTreeMap<Long, Boolean> relaisStateHistory, SerializableTreeMap<Long, Float> sensorInfoHistory, long deltat, long timeStepsBack, long timeStepsForward){
		TreeMap<Long,float[]> historicData = getHistoricData(relaisStateHistory,sensorInfoHistory, deltat, timeStepsBack, timeStepsForward);
		return null;	
	}
	
	
	static TreeMap<Long,float[]> getHistoricData(SerializableTreeMap<Long, Boolean> relaisStateHistory, SerializableTreeMap<Long, Float> sensorInfoHistory, long deltat, long timeStepsBack, long timeStepsForward){		
		SerializableTreeMap<Long, Float> demands = DemandCalculationModel.calculateDemand(relaisStateHistory, sensorInfoHistory, deltat);
			
		long[] cuttingPoints = findCuttingPoints(demands,timeStepsBack, deltat);
		TreeMap<Long,float[]> historicData = new TreeMap<Long, float[]>();		
		
		for (int i = 0; i < cuttingPoints.length; i++) {
			NavigableMap<Long, Float> submap = demands.subMap(cuttingPoints[i], true, cuttingPoints[i]+(deltat*timeStepsBack)+(deltat*timeStepsForward), true);
			ArrayFloatList values = new ArrayFloatList();
			for (Float value : submap.values()) {
				values.add(value);
			}			
			values.trimToSize();
			historicData.put(cuttingPoints[i], values.toArray());							
		}		
		return historicData;
	}	
	

//	private TreeMap<Long, float[]> cutDemands(SerializableTreeMap<Long, Float> demands) {		
//		Calendar now = Calendar.getInstance();
//		now.setTimeInMillis(demands.lastKey());
//		Calendar windowStart = Calendar.getInstance();
//		windowStart.setTimeInMillis(demands.lastKey()-_timeWindowBack);	
//		
//		HashMap<Long,Long> cuttingPointsOfStart= findCuttingPoints(demands, _timeWindowBack, demands.lastKey());
//		TreeMap<Long,float[]> historicData= new TreeMap<Long, float[]>();
//		
//		for (int day=0; day<cuttingPointsOfStart.length-1; day++){			
//			SortedMap<Long,Float> dayMap = demands.subMap(cuttingPointsOfStart[day], cuttingPointsOfStart[day]+_timeWindowBack+_timeWindowForward);			
//			float[] x0 = MapHelper.keysToDoubleArray(dayMap);
//			float[] y0 = MapHelper.valuesToDoubleArray(dayMap);			
//			float[] x1 = InterpolationHelper.createLinearArray(cuttingPointsOfStart[day], _deltat, cuttingPointsOfStart[day]+_timeWindowBack+_timeWindowForward-1);
//			float[] y1= InterpolationHelper.interpolateLinear(x0, y0, x1);
//			historicData.put(cuttingPointsOfStart[day],y1);			
//		}
//		historicData.put
//		
//		return historicData;
//	}
//
	private static long[] findCuttingPoints(TreeMap<Long, Float> demands, long timeStepsBack, long deltat) {
		
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
