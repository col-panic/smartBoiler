/**
 * 
 */
package at.fhv.smartdevices.helper;

import java.util.Arrays;

/**
 * @author kepe
 * provides methods for linear and boolean interpolation
 */
public class InterpolationHelper {

	/**
	 * Interpolate the function f(x0)=y0 at the points x1 and return the result
	 * f(x1)
	 * 
	 * @return f(x1) as float array
	 */
	public static float[] interpolateLinear(float[] x0, float[] y0, float[] x1) {
		float[] y1 = new float[x1.length];

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

	public static float[] createLinearArray(float x_start, float deltaT, float x_end) {
		int amount =(int) Math.round((x_end-x_start)/deltaT);
		float[] retVal = new float[(int) Math.round((x_end-x_start)/deltaT)];
		float x_current = x_start;
		for (int i=0; i<amount;i++) {
			retVal[i]=(x_current);
			x_current += deltaT;
		}
		return retVal;
	}

	public static float[] interpolateBinary(float[] x0, float[] y0, float[] x1) {
		float [] y1 = new float[x1.length];

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
}
