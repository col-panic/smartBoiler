/**
 * 
 */
package at.fhv.smartdevices.singleNodeDHWH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.primitives.*;

import at.fhv.smartdevices.commons.INumericMetric;
import at.fhv.smartdevices.commons.ISchedulable;
import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.helper.InterpolationHelper;
import at.fhv.smartdevices.helper.MapHelper;
import at.fhv.smartdevices.scheduling.DataAquisition;
import at.fhv.smartdevices.scheduling.SchedulableSwitch;

/**
 * @author kepe
 * 
 */
public class KNNBasedOptimization implements ISchedulable {

	private DataAquisition _dataAquisition;
	private SchedulableSwitch _switcher;
	private Lock _lock = new ReentrantLock();
	// TODO: Store already calculated demand! private final String FILE_DEMAND = "/dataStore/demand";
	private String _tempID;
	private static INumericMetric _metric;
	private long _timeStepsBack = 5;// in delta t
	private long _timeStepsForward = 5; // in delta t
	private long _timeStep;
	private long _deltat;
	private static short _k = 1;



	private static long getDayInDeltatSteps(long deltat) {
		return (24 * 60 * 60 * 1000) / deltat;
	}

	public KNNBasedOptimization(DataAquisition dA, SchedulableSwitch switcher, String tempID, INumericMetric metric, long deltat , long timeStepsBack, long timeStepsForward, short k) {
		_tempID = tempID;
		if(!dA.getSencorIds().contains(tempID)){
			throw new IllegalArgumentException("Sensor must exist! Sensor ID not valid");
		}
		_dataAquisition = dA;
		_switcher = switcher;
		_metric = metric;
		if(deltat<=0){
			throw new IllegalArgumentException("time step delta t needs to be positive");
		}		
		_deltat = deltat;
		_timeStepsBack = timeStepsBack;
		_timeStepsForward = timeStepsForward;
		
		if(k<1)
		{
			throw new IllegalArgumentException("Number of nearest neighbors (k) needs to be greater or equal to 1");
		}
		_k=k;
	}

	/**
	 * 
	 * @param relaisStateHistory
	 * @param sensorInfoHistory
	 * @param futureCosts
	 * @param deltat
	 * @param timeStepsBack
	 * @param timeStepsForward
	 * @return
	 */
	private SerializableTreeMap<Long, Boolean> calculateSwitchingTimes(SerializableTreeMap<Long, Boolean> relaisStateHistory,
			SerializableTreeMap<Long, Float> sensorInfoHistory, TreeMap<Long, Integer> futureCosts, long deltat, long timeStepsBack, long timeStepsForward) {

		TreeMap<Long, double[]> historicData = getHistoricData(relaisStateHistory, sensorInfoHistory, deltat, timeStepsBack, timeStepsForward);
		double[] currentTimeSeries = historicData.lastEntry().getValue();
		long nowInterpolated = currentTimeSeries.length * deltat + historicData.lastKey();
		historicData.remove(historicData.lastKey());
		TreeMap<Long, Double> distances = new TreeMap<Long, Double>();
		for (Long key : historicData.navigableKeySet()) {
			double d = _metric.calculateDistance(Arrays.copyOf(historicData.get(key), currentTimeSeries.length), currentTimeSeries);
			distances.put(key, d);
		}

		short i = 0;
		double[][] futureDemand = new double[_k][];
		for (java.util.Map.Entry<?, Double> entry : sortHashMapByDoubleValue(distances)) {
			if (i >= _k) {
				break;
			} else {
				double[] timeSeries = historicData.get(entry.getKey());
				futureDemand[i] = Arrays.copyOfRange(timeSeries, timeSeries.length - currentTimeSeries.length, timeSeries.length - 1);
				i++;
			}
		}
		double[] t = InterpolationHelper.createLinearArray(nowInterpolated, deltat, futureCosts.lastKey());
		double[] interpolatedFutureCosts = InterpolationHelper.interpolateLinear(MapHelper.keysToDoubleArray(futureCosts), MapHelper.valuesToDoubleArray(futureCosts), t);

		double[] u_opt = LinearDeterministicOptimizer.optimize(futureDemand, interpolatedFutureCosts, deltat, sensorInfoHistory.lastEntry().getValue());
		SerializableTreeMap<Long, Boolean> switchingTimes = new SerializableTreeMap<Long, Boolean>();
		for (int j = 0; j < u_opt.length; j++) {
			switchingTimes.put(Math.round(t[j]), u_opt[j] > 0.1);
		}
		return switchingTimes;
	}

	private static final Comparator<Map.Entry<?, Double>> DOUBLE_VALUE_COMPARATOR = new Comparator<Map.Entry<?, Double>>() {
		@Override
		public int compare(Map.Entry<?, Double> o1, Map.Entry<?, Double> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	};

	private static final List<Map.Entry<?, Double>> sortHashMapByDoubleValue(TreeMap<Long, Double> distances) {
		Set<java.util.Map.Entry<Long, Double>> entryOfMap = distances.entrySet();

		List<Map.Entry<?, Double>> entries = new ArrayList<Map.Entry<?, Double>>(entryOfMap);
		Collections.sort(entries, DOUBLE_VALUE_COMPARATOR);
		return entries;
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
	static TreeMap<Long, double[]> getHistoricData(SerializableTreeMap<Long, Boolean> relaisStateHistory, SerializableTreeMap<Long, Float> sensorInfoHistory,
			long deltat, long timeStepsBack, long timeStepsForward) {
		SerializableTreeMap<Long, Double> demands = DemandCalculationModel.calculateDemand(relaisStateHistory, sensorInfoHistory, deltat, true);

		long[] cuttingPoints = findCuttingPoints(demands, timeStepsBack, deltat);
		TreeMap<Long, double[]> historicData = new TreeMap<Long, double[]>();
		for (int i = 0; i < cuttingPoints.length; i++) {
			NavigableMap<Long, Double> submap = demands.subMap(cuttingPoints[i], true, cuttingPoints[i] + (deltat * timeStepsBack)
					+ (deltat * timeStepsForward), true);

			DoubleList values = new ArrayDoubleList();
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
		for (int counter = 0; counter < Integer.MAX_VALUE; counter++) {
			Long key = demands.floorKey(demands.lastKey() - (timeStepsBack * deltat) - (getDayInDeltatSteps(deltat) * deltat * counter));
			if (key != null) {
				retVal.add(key);
			} else {
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
		try {
			_switcher.setSwitchingTimes(calculateSwitchingTimes(_dataAquisition.getRelaisStateHistory(),
					_dataAquisition.GetSensorInformationHistory().get(_tempID), _dataAquisition.getFutureCosts(), _deltat, _timeStepsBack, _timeStepsForward));

		} finally {
			_lock.unlock();
		}
	}
}
