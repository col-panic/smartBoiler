/**
 * 
 */
package at.fhv.smartdevices.datamining;

import java.util.InputMismatchException;

/**
 * @author kepe
 *
 */
public class MetricHelper {

	public static double calculateMinkowskiMetric(double[] a, double[] b, double p){
		if(a.length!=b.length){
			throw new InputMismatchException("Vectors must be of same length");			
		}
		if(!(p>0)){
			throw new IllegalArgumentException("The p must be positive");
		}
		double retVal=0;
		
		if(p!=Double.POSITIVE_INFINITY){
		for (int i=0;i<a.length;i++){
			retVal += Math.pow((a[i]-b[i]),p);
		}
		retVal=Math.pow(retVal, 1/p);
		}
		else
		{
			for (int i=0;i<a.length;i++){
				retVal += Math.max(a[i],b[i]);
			}
		}
		return retVal;		
	}
}
