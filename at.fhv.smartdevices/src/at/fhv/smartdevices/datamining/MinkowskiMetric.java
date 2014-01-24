/**
 * 
 */
package at.fhv.smartdevices.datamining;

import java.util.InputMismatchException;

import at.fhv.smartdevices.commons.INumericMetric;

/**
 * @author kepe
 * 
 */
public class MinkowskiMetric implements INumericMetric {
	
	private double _p;
		
	public MinkowskiMetric(double p){
		if (!(p > 0)) {
			throw new IllegalArgumentException("The p must be positive");
		}		
		_p=p;
	}		

	@Override
	public double calculateDistance(double[] a, double[] b) {		
				
		if (a.length != b.length) {
			throw new InputMismatchException("Vectors must be of same length");
		}		
		
		double retVal = 0;

		if (_p != Double.POSITIVE_INFINITY) {
			for (int i = 0; i < a.length; i++) {
				retVal += Math.pow((a[i] - b[i]),_p);
			}
			retVal = Math.pow(retVal, 1 / _p);
		} else {
			for (int i = 0; i < a.length; i++) {
				retVal += Math.max(a[i], b[i]);
			}
		}
		return retVal;
	}
}
