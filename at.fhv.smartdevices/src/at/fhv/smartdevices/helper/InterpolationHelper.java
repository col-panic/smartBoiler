/**
 * 
 */
package at.fhv.smartdevices.helper;

import java.util.Arrays;

import at.fhv.smartdevices.commons.SerializableTreeMap;

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
			// if value known
			if (index >= 0) {
				y1[i] = y0[index];
			}
			// if not known, interpolation necessary
			else {
				//calculate insertion index
				int j = -index - 1;
				if (j > 0 && j < x0.length) {
					y1[i] = y0[j-1] + ((y0[j] - y0[j-1]) / (x0[j] - x0[j-1]) * (x1[i] - x0[j-1]));
					}
				// extrapolate
				else if (j == 0) {
					y1[i] = y0[j] - ((y0[j + 1] - y0[j]) / (x0[j + 1] - x0[j]) * (x0[j]) - x1[i]);
				} else if (j == x0.length) {
					y1[i] = y0[j-1] + ((y0[j-1] - y0[j - 2]) / (x0[j-1] - x0[j - 2]) * (x1[i] - x0[j-1]));
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
