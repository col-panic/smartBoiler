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
	public static Double[] interpolateLinear(Double[] x0, Double[] y0, Double[] x1) {
		Double[] y1 = new Double[x1.length];

		for (int i = 0; i < x1.length; i++) {
			int index = Arrays.binarySearch(x0, x1[i]);
			// if contained
			if (index > 0) {
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

	public static Double[] createLinearArray(Double x_start, double deltaT, Double x_end) {
		ArrayList<Double> retVal = new ArrayList<Double>();
		Double x_current = x_start;
		while (x_current < x_end) {
			retVal.add(x_current);
			x_current += deltaT;
		}
		return (Double[]) retVal.toArray();
	}

	public static Double[] interpolateBinary(Double[] x0, Double[] y0, Double[] x1) {
		Double[] y1 = new Double[x1.length];

		for (int i = 0; i < x1.length; i++) {
			int index = Arrays.binarySearch(x0, x1[i]);
			// if contained
			if (index > 0) {
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
