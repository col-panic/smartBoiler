/**
 * 
 */
package at.fhv.smartdevices.datamining;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import at.fhv.smartdevices.commons.SerializableTreeMap;

/**
 * @author kepe
 *
 */
public class TimeSeriesHelperTests {

	@Test
	public void cutTest(){
		SerializableTreeMap<Long, Double> timeSeries  = new SerializableTreeMap<Long, Double>();
		
		long currentTime=1;
		long deltat=123;
		for (long i=1; i<=100; i++)
		{
			for (long j=0; j<100; j++){
				if(i==100 && j>49){
					break;						
				}
				else{
					timeSeries.put(currentTime, (double) (j));
					currentTime = currentTime+deltat;
				}
			}
			
		}
		
		TreeMap<Long, double[]> subSeries =  TimeSeriesHelper.cutTimeSeries(timeSeries,deltat, 5, 5, 100*deltat);
		double[] expected = subSeries.firstEntry().getValue();
		long startKey = subSeries.firstKey();
		for (Map.Entry<Long, double[]> series : subSeries.entrySet()) {
			assertEquals((series.getKey()-startKey), 100*deltat);
			for (int i = 1; i<series.getValue().length; i++) {
				assertEquals(expected[i], series.getValue()[i], 0);
			}
		}
	}
	
}
