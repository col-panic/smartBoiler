package at.fhv.smartdevices.simulatedDHWH;

import java.util.Arrays;
import java.util.TreeMap;

import at.fhv.smartdevices.commons.SerializableTreeMap;

/**
 * @author kepe
 * 
 */
public class SimulatedDHWH {

	// private static final double c_p=4.18e3;//[J/kgK] specific heat
	static final double pEl = 2175;// [W] el. power of the heater
	// private static final double tempIn=12;//[°C] temperature of inlet water;
	private static final double tempEnv = 20;// [°C] outside temperature;
	private static final double tempMax = 90;// [°C] max allowed temperature in
												// water heater;
	// private static final double tempMin=38;//[°C] lowest allowed temperature
	// in water heater;
	private static final double p1 = 1.1972;// [W/K] heat conduction * area,
											// Water below Iso/2;
	private static final double p2 = 6.815e5;// [J/K] thermal mass (m_i*c_i)
												// envelope (St,Iso,Bl);
	// private static final double tempDemand = 38;
	private static final double deltat = 60;

	/**
	 * @param u
	 *            the switch
	 * @param startTemp
	 *            start temperature
	 * @param demand
	 *            the demand in W
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte u, double startTemp, double demand) {
		return simulateDHWH(new byte[] { u }, startTemp, new double[] { demand }, 1);
	}

	/**
	 * @param u
	 *            the switch
	 * @param startTemp
	 *            start temperature
	 * @param demand
	 *            the demand in W
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand) {
		int timeSteps = Math.min(u.length, demand.length);
		return simulateDHWH(u, startTemp, demand, timeSteps);
	}

	/**
	 * @param u
	 *            the switch
	 * @param startTemp
	 *            start temperature
	 * @param demand
	 *            the demand in W
	 * @param timeSteps
	 *            the number of steps to take
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand, int timeSteps) {
		double[] temp = new double[timeSteps + 1];
		double[][] retVal = new double[2][timeSteps + 1];
		temp[0] = startTemp;
		double lambda = Math.exp(-(deltat * p1 / p2));
		for (int i = 1; i <= timeSteps; i++) {
			if (temp[i - 1] > tempMax) {
				u[i - 1] = 0;
			} else {
				temp[i] = (temp[i - 1] * lambda)
						+ ((1 - lambda) * ((u[i - 1] * pEl / p1) - (demand[i - 1] / p1) + tempEnv));
			}
		}
		for (int i = 0; i < timeSteps; i++) {
			retVal[0][i] = u[i];
			retVal[1][i] = temp[i];
		}
		retVal[1][timeSteps] = temp[timeSteps];
		return retVal;
	}

	public static SerializableTreeMap<Long, Double> calculateDemand(TreeMap<Long, Boolean> u, TreeMap<Long, Double> temp, double deltaT) {
		SerializableTreeMap<Long, Double> retVal = new SerializableTreeMap<Long, Double>();
		Double[] x0 = temp.keySet().toArray(new Double[temp.keySet().size()]);
		Double[] y0 = temp.values().toArray(new Double[temp.values().size()]);
		Double[] x1 = u.keySet().toArray(new Double[u.keySet().size()]);
		Double[] y1 = interpolateLinear(x0, y0, x1);
		
		
		return retVal;
	}

	private static Double[] interpolateLinear(Double[] x0, Double[] y0, Double[] x1) {
		Double[] y1 = new Double[x1.length];
		
		for (int i = 0; i < x1.length; i++) {
			int index = Arrays.binarySearch(x0, x1[i]);
			//if contained
			if(index>0){
				y1[i]=y0[index];				
			}
			//if not contained, interpolation necessary
			else{
				index=-index+1;
				if(index>0 && index<x0.length-1){
					y1[i] = y0[index]+((y0[index+1]-y0[index])/(x0[index+1]-x0[index])*(x1[i]-x0[index]));
				}
				//extrapolate
				else if(index==0){
					y1[i] = y0[index]-((y0[index+1]-y0[index])/(x0[index+1]-x0[index])*(x0[index])-x1[i]);
					}
				else if (index==x0.length-1){
					y1[i] = y0[index]-((y0[index]-y0[index-1])/(x0[index]-x0[index-1])*(x1[i]-x0[index]));
				}					
			}
		}
		
		return y1;
	}
}
