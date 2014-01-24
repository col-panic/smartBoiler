/**
 * 
 */
package at.fhv.smartdevices.datamining;

/**
 * @author kepe
 *
 */
public class MetricHelper {
	public static <T extends Number> double[] convertToDouble(T[] n){
		double[] parsed = new double[n.length];
		for (int i = 0; i<n.length; i++) {
			parsed[i] = (n[i].doubleValue());
		}		
		return parsed;
	}
}
