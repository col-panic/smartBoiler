/**
 * 
 */
package at.fhv.smartdevices.datamining;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.primitives.*;
import org.apache.commons.collections.primitives.adapters.DoubleListList;
import org.apache.commons.collections.primitives.adapters.ListDoubleList;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

import at.fhv.smartdevices.commons.INumericMetric;
import at.fhv.smartdevices.commons.ISchedulable;
import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.helper.InterpolationHelper;
import at.fhv.smartdevices.helper.MapHelper;
import at.fhv.smartdevices.scheduling.DataAquisition;
import at.fhv.smartdevices.scheduling.SchedulableSwitch;
import at.fhv.smartdevices.singleNodeDHWH.DemandCalculationModel;
import cern.colt.matrix.linalg.SeqBlas.*;

/**
 * @author kepe
 * 
 */
public class KNN implements ISchedulable {

	private DataAquisition _dataAquisition;
	private SchedulableSwitch _switcher;
	private Lock _lock = new ReentrantLock();
	private final String FILE_DEMAND = "/dataStore/demand";
	private long _deltat = 60000; // in milliseconds.
	private String _tempID;
	private INumericMetric _metric;
	private long _timeStepsBack = 60 * 12;// in delta t
	private long _timeStepsForward = 60 * 24; // in delta t
	private long _timeStep;
	private static short _k = 1;

	static final double pEl = 2175.0d;// [W] el. power of the heater
	// private static final double tempIn=12;//[°C] temperature of inlet water;
	private static final double tempEnv = 20.0d;// [°C] outside temperature;
	private static final double tempMax = 90.0d;// [°C] max allowed temperature
												// in
												// water heater;
	private static final double tempMin = 38;// [°C] lowest allowed temperature
	// in water heater;
	private static final double p1 = 1.19720d;// [W/K] heat conduction * area,
												// Water below Iso/2;
	private static final double p2 = 6.815e5d;// [J/K] thermal mass (m_i*c_i)
												// envelope (St,Iso,Bl);

	private static long getDayInDeltatSteps(long deltat) {
		return (24 * 60 * 60 * 1000) / deltat;
	}

	public KNN(DataAquisition dA, SchedulableSwitch switcher, String tempID,
			INumericMetric metric, long timeStep) {
		_tempID = tempID;
		_dataAquisition = dA;
		_switcher = switcher;
		_metric = metric;
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
	static SerializableTreeMap<Long, Boolean> calculateSwitchingTimes(
			SerializableTreeMap<Long, Boolean> relaisStateHistory,
			SerializableTreeMap<Long, Float> sensorInfoHistory,
			TreeMap<Long, Integer> futureCosts, long deltat,
			long timeStepsBack, long timeStepsForward) {

		TreeMap<Long, double[]> historicData = getHistoricData(
				relaisStateHistory, sensorInfoHistory, deltat, timeStepsBack,
				timeStepsForward);
		double[] currentTimeSeries = historicData.lastEntry().getValue();
		historicData.remove(historicData.lastKey());
		TreeMap<Long, Double> distances = new TreeMap<Long, Double>();
		for (Long key : historicData.navigableKeySet()) {
			double d = MetricHelper.calculateMinkowskiMetric(Arrays.copyOf(
					historicData.get(key), currentTimeSeries.length),
					currentTimeSeries, 2);
			distances.put(key, d);
		}

		short i = 0;
		double[][] candidates = new double[_k][];
		for (java.util.Map.Entry<?, Double> entry : sortHashMapByDoubleValue(distances)) {
			if (i >= _k) {
				break;
			} else {
				double[] timeSeries = historicData.get(entry.getKey());
				candidates[i] = Arrays.copyOfRange(timeSeries,
						timeSeries.length - currentTimeSeries.length,
						timeSeries.length - 1);
				i++;
			}
		}
		double[] t = InterpolationHelper.createLinearArray(
				historicData.lastKey(), deltat, futureCosts.lastKey());
		double[] costs = InterpolationHelper.interpolateLinear(
				MapHelper.keysToDoubleArray(futureCosts),
				MapHelper.valuesToDoubleArray(futureCosts), t);

		double[] u_opt = solveOptimizationProblem(candidates, costs, deltat,
				sensorInfoHistory.lastEntry().getValue());
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

	private static final List<Map.Entry<?, Double>> sortHashMapByDoubleValue(
			TreeMap<Long, Double> distances) {
		Set<java.util.Map.Entry<Long,Double>> entryOfMap = distances.entrySet();

		List<Map.Entry<?, Double>> entries = new ArrayList<Map.Entry<?, Double>>(
				entryOfMap);
		Collections.sort(entries, DOUBLE_VALUE_COMPARATOR);
		return entries;
	}

	private static double[] solveOptimizationProblem(double[][] candidates,
			double[] costs, double deltat, double temp0) {

		int maxSteps = Math.min(candidates[0].length, costs.length);
		for (double[] candidate : candidates) {

		}

		costs = Arrays.copyOf(costs, maxSteps);

		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(
				costs, 0);

		double[][] A = new double[maxSteps][];
		double[][] A_neg = new double[maxSteps][];
		double[] a = new double[maxSteps];
		double[] b = new double[maxSteps];
		double lambda = Math.exp((-p1 * deltat) / (1000.0d * p2));
		for (int i = 0; i < maxSteps; i++) {
			double[] Ai = new double[maxSteps];
			double[] Ai_neg = new double[maxSteps];
			a[i] = tempMax - (temp0 * Math.pow(lambda, i));
			b[i] = tempMin - (temp0 * Math.pow(lambda, i));
			for (int j = 0; j < maxSteps; j++) {
				Ai[j] = (1 - lambda) * Math.pow(lambda, i - 1 - j) * pEl / p1;
				Ai_neg[j] = -Ai[j];
				double currentTemp = ((1 - lambda) * Math.pow(lambda, i - j) * (tempEnv - (candidates[1][j] / p1)));
				a[i] += (j < i) ? currentTemp : 0;
				b[i] += (j < i) ? currentTemp : 0;
			}
			A[i] = Ai;
			A_neg[i] = Ai_neg;
		}

		// inequalities (polyhedral feasible set A.X<a && -Ax<-b )
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[maxSteps * 2];
		for (int l = 0; l < maxSteps; l++) {
			inequalities[l] = new LinearMultivariateRealFunction(A[l], -a[l]);
			inequalities[maxSteps + l] = new LinearMultivariateRealFunction(
					A_neg[l], -b[l]);
		}

		// optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		// or.setInitialPoint(new double[] {0.0, 0.0});//initial feasible point,
		// not mandatory
		or.setToleranceFeas(1.E-9);
		or.setTolerance(1.E-9);

		// optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		try {
			int returnCode = opt.optimize();
			return opt.getOptimizationResponse().getSolution();
		} catch (Exception e) {
			System.out.println("Optimization routine failed");
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
	static TreeMap<Long, double[]> getHistoricData(
			SerializableTreeMap<Long, Boolean> relaisStateHistory,
			SerializableTreeMap<Long, Float> sensorInfoHistory, long deltat,
			long timeStepsBack, long timeStepsForward) {
		SerializableTreeMap<Long, Double> demands = DemandCalculationModel
				.calculateDemand(relaisStateHistory, sensorInfoHistory, deltat,
						true);

		long[] cuttingPoints = findCuttingPoints(demands, timeStepsBack, deltat);
		TreeMap<Long, double[]> historicData = new TreeMap<Long, double[]>();
		for (int i = 0; i < cuttingPoints.length; i++) {
			NavigableMap<Long, Double> submap = demands.subMap(
					cuttingPoints[i], true, cuttingPoints[i]
							+ (deltat * timeStepsBack)
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
	private static long[] findCuttingPoints(TreeMap<Long, Double> demands,
			long timeStepsBack, long deltat) {

		ArrayLongList retVal = new ArrayLongList();
		for (int counter = 0; counter < Integer.MAX_VALUE; counter++) {
			Long key = demands.floorKey(demands.lastKey()
					- (timeStepsBack * deltat)
					- (getDayInDeltatSteps(deltat) * deltat * counter));
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
			_switcher.setSwitchingTimes(calculateSwitchingTimes(_dataAquisition
					.getRelaisStateHistory(), _dataAquisition
					.GetSensorInformationHistory().get(_tempID),
					_dataAquisition.getFutureCosts(), _deltat, _timeStepsBack,
					_timeStepsForward));

		} finally {
			_lock.unlock();
		}
	}
}
