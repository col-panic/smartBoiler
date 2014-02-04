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
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import at.fhv.smartdevices.commons.INumericMetric;
import at.fhv.smartdevices.commons.ISchedulable;
import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.datamining.TimeSeriesHelper;
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



	/**
	 * Public constructor
	 * @param dA dataAquistion
	 * @param switcher the schedulable switch to apply the optimal control
	 * @param tempID the sensor ID of the temperature sensor
	 * @param metric the metric to define distance
	 * @param deltat the time step to base the time series analysis on
	 * @param timeStepsBack the amount of delta t steps back to take into account
	 * @param timeStepsForward the amount of delta t steps forward to optimize for
	 * @param k the number of nearest neighbors
	 */
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
	 * Calculates the optimal switching times based on a nearest neighbor approach 
	 * @return a tree map of optimal switching times (time, state) for the relais. 
	 */
	protected SerializableTreeMap<Long, Boolean> calculateSwitchingTimes() {
		
		SerializableTreeMap<Long, Boolean> relaisStateHistory= _dataAquisition.getRelaisStateHistory();
		SerializableTreeMap<Long, Float> sensorInfoHistory = _dataAquisition.GetSensorInformationHistory().get(_tempID);
		TreeMap<Long, Integer> futureCosts= _dataAquisition.getFutureCosts();
		
		SerializableTreeMap<Long, Double> demands = DemandCalculationModel.calculateDemand(relaisStateHistory, sensorInfoHistory, _deltat, true);
		
		TreeMap<Long, double[]> historicData = TimeSeriesHelper.cutTimeSeries(demands, _deltat, _timeStepsBack, _timeStepsForward, 24 * 60 * 60 * 1000);
		double[] currentTimeSeries = historicData.lastEntry().getValue();
		long nowInterpolated = currentTimeSeries.length * _deltat + historicData.lastKey();
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
		double[] t = InterpolationHelper.createLinearArray(nowInterpolated, _deltat, futureCosts.lastKey());
		double[] interpolatedFutureCosts = InterpolationHelper.interpolateLinear(MapHelper.keysToDoubleArray(futureCosts), MapHelper.valuesToDoubleArray(futureCosts), t);

		double[] u_opt = LinearDeterministicOptimizer.optimize(futureDemand, interpolatedFutureCosts, _deltat, sensorInfoHistory.lastEntry().getValue());
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
			_switcher.setSwitchingTimes(calculateSwitchingTimes());

		} finally {
			_lock.unlock();
		}
	}
}
