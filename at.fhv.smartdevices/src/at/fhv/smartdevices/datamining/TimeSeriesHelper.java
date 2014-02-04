/**
 * 
 */
package at.fhv.smartdevices.datamining;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.ArrayLongList;
import org.apache.commons.collections.primitives.DoubleList;

import at.fhv.smartdevices.commons.SerializableTreeMap;

/**
 * @author kepe
 *
 */
public class TimeSeriesHelper {
	/**
	 * cut a time series in reference to the last given entry, such that every sub series contains one <code>period</code> of the historic data, 
	 * <code>timeStepsBack</code> back and <code>timeStepsForward</code> forward.  
	 * @param timeSeries the time series to cut
	 * @param deltat time step of the times series data
	 * @param timeStepsBack the time steps one sub series should contain back from the periodic reference time
	 * @param timeStepsForward the time steps one sub series should contain forward from the periodic reference time#
	 * @param period underlying periodicity in the data (1e-3s)
	 * @return a map of sub time series.
	 */
	public static TreeMap<Long, double[]> cutTimeSeries(SerializableTreeMap<Long, Double> timeSeries,
			long deltat, long timeStepsBack, long timeStepsForward, long period) {
		
		long[] cuttingPoints = findCuttingPoints(timeSeries, timeStepsBack*deltat, period);
		TreeMap<Long, double[]> historicData = new TreeMap<Long, double[]>();
		for (int i = 0; i < cuttingPoints.length; i++) {
			NavigableMap<Long, Double> submap = timeSeries.subMap(cuttingPoints[i], true, cuttingPoints[i] + (deltat * timeStepsBack)
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
	 * finds the indices of the underlying time series, such that every index is the starting point of a new 
	 * sub time series, comprising the given <code>timeWindow</code>. Distance between the starting points of the sub series is given
	 * by <code>period</code>
	 * @param timeSeries the time series data key in milliseconds (1e-3s)
	 * @param timeWindow time window for a single entry (1e-3s)
	 * @param period underlying periodicity in the data (1e-3s)
	 * @return an array of all cutting points (1e-3s).
	 */
	protected static long[] findCuttingPoints(TreeMap<Long, Double> timeSeries, long timeWindow, long period) {

		ArrayLongList retVal = new ArrayLongList();
		for (int counter = 0; counter < Integer.MAX_VALUE; counter++) {
			Long key = timeSeries.floorKey(timeSeries.lastKey() - (timeWindow) - (period * counter));
			if (key != null) {
				retVal.add(key);
			} else {
				break;
			}
		}

		return retVal.toArray();
	}	
}
