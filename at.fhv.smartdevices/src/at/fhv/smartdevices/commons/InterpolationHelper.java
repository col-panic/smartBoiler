/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author kepe
 * 
 */
public class InterpolationHelper {

	/**
	 * Interpolate the function f(x0)=y0 at the points x1 and return the result
	 * f(x1)
	 * 
	 * @return f(x1) as double array
	 */
	public static double[] interpolateLinear(double[] x0, double[] y0, double[] x1) {
		double[] y1 = new double[x1.length];

		for (int i = 0; i < x1.length; i++) {
			int index = Arrays.binarySearch(x0, x1[i]);
			// if contained
			if (index >= 0) {
				y1[i] = y0[index];
			}
			// if not contained, interpolation necessary
			else {
				index = -index - 1;
				if (index > 0 && index < x0.length - 1) {
					y1[i] = y0[index]
							+ ((y0[index + 1] - y0[index]) / (x0[index + 1] - x0[index]) * (x1[i] - x0[index]));
				}
				// extrapolate
				else if (index == 0) {
					y1[i] = y0[index]
							- ((y0[index + 1] - y0[index]) / (x0[index + 1] - x0[index]) * (x0[index]) - x1[i]);
				} else if (index == x0.length - 1) {
					y1[i] = y0[index]
							- ((y0[index] - y0[index - 1]) / (x0[index] - x0[index - 1]) * (x1[i] - x0[index]));
				}
			}
		}

		return y1;
	}

	public static double[] createLinearArray(double x_start, double deltaT, double x_end) {
		int amount =(int) Math.round((x_end-x_start)/deltaT);
		double[] retVal = new double[(int) Math.round((x_end-x_start)/deltaT)];
		double x_current = x_start;
		for (int i=0; i<amount;i++) {
			retVal[i]=(x_current);
			x_current += deltaT;
		}
		return retVal;
	}

	public static double[] interpolateBinary(double[] x0, double[] y0, double[] x1) {
		double [] y1 = new double[x1.length];

		for (int i = 0; i < x1.length; i++) {
			int index = Arrays.binarySearch(x0, x1[i]);
			// if contained
			if (index >= 0) {
				y1[i] = y0[index];
			} else {
				index = -index - 1;
				if (index > 0) {
					y1[i] = y0[index - 1];
				} else {
					y1[i] = y0[0];
				}
			}
		}
		return y1;
	}
	
	public static <K extends Number, V>  double[] keysToDoubleArray(SerializableTreeMap<K, V> map)
	{
		int amount = map.keySet().size();
		Number[] keys = (Number[]) map.keySet().toArray(new Number[amount]);	
		double[] retVal = new double[amount];
		for (int i=0;i<amount;i++) {
			retVal[i] = keys[i].doubleValue();
		}		
		return retVal;		
	}		
	
	public static <K, V extends Number>  double[] valuesToDoubleArray(SerializableTreeMap<K, V> map)
	{
		int amount = map.values().size();
		Number[] values = (Number[]) map.values().toArray(new Number[amount]);
		
		double[] retVal = new double[amount];
		for (int i=0;i<amount;i++) {			
			retVal[i] = values[i].doubleValue();
		}		
		return retVal;		
	}

	public static SerializableTreeMap<Long, Byte> ConvertTreeMapBooleanValueToByte(SerializableTreeMap<Long, Boolean> map){
		SerializableTreeMap<Long, Byte> retVal = new SerializableTreeMap<Long, Byte>();
			for (Long key : map.keySet()) {
				retVal.put(key,map.get(key)?(byte)1:(byte)0);
			}
		return retVal;
	}
}
