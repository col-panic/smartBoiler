package at.fhv.smartdevices.simulatedDHWH;

import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.helper.InterpolationHelper;
import at.fhv.smartdevices.helper.SerializableTreeMapHelper;

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
	//private static final double deltat = 60;
	
	/**
	 * @param u
	 *            the switch
	 * @param startTemp
	 *            start temperature
	 * @param demand
	 *            the demand in W
	 * @param deltat
	 * 			  the time step size
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte u, double startTemp, double demand, double deltat) {
		return simulateDHWH(new byte[] { u }, startTemp, new double[] { demand }, deltat);
	}

	/**
	 * @param u
	 *            the switch
	 * @param startTemp
	 *            start temperature
	 * @param demand
	 *            the demand in W
	 * @param deltat
	 * 			  the time step size
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand, double deltat) {
		int timeSteps = Math.min(u.length, demand.length);
		return simulateDHWH(u, startTemp, demand, timeSteps, deltat);
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
	 * @param deltat
	 * 			  the time step size
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand, int timeSteps, double deltat) {
		double[] temp = new double[timeSteps + 1];
		double[][] retVal = new double[2][timeSteps + 1];
		temp[0] = startTemp;
		double lambda = Math.exp(-(deltat * p1 / p2));
		for (int i = 1; i <= timeSteps; i++) {
			if (temp[i - 1] > tempMax) {
				u[i - 1] = 0;
			} 
			temp[i] = (temp[i - 1] * lambda)+ ((1 - lambda) * ((u[i - 1] * pEl / p1) - (demand[i - 1] / p1) + tempEnv));
			
		}
		for (int i = 0; i < timeSteps; i++) {
			retVal[0][i] = u[i];
			retVal[1][i] = temp[i];
		}
		retVal[1][timeSteps] = temp[timeSteps];
		return retVal;
	}
	
	public static double[] calculateDemand(double[] temp, double[] u, double deltat){
		double[] Q_dem = new double[temp.length];
		for (int i=1;i<temp.length;i++) {	    	
			Q_dem[i-1]=calculateDemand(temp[i], temp[i-1], u[i-1], deltat);
		}	
		return Q_dem;
	}
	
	public static double calculateDemand(double temp_1, double temp_0, double u, double deltat){
		double dem = -((p2*(temp_1-temp_0)/deltat)-(u*pEl)-(p1*(tempEnv-((temp_0+temp_1)/2))));
		//double lambda = Math.exp(-p1/p2*deltat);
		//double dem = ((temp_0*lambda -(temp_1))*p1/(1-lambda)+(u*pEl)+(tempEnv*p1));
		return dem;
	}

	public static SerializableTreeMap<Long, Double> calculateDemand(SerializableTreeMap<Long, Boolean> switchBoolMap, SerializableTreeMap<Long, Double> temp, long deltat) {
		SerializableTreeMap<Long, Double> retVal = new SerializableTreeMap<Long, Double>();
		SerializableTreeMap<Long, Byte> switchMap = SerializableTreeMapHelper.ConvertTreeMapBooleanValueToByte(switchBoolMap); 
		
		double t_start = (double) Math.min(switchMap.firstKey(), temp.firstKey());
		double t_end = (double) Math.max(switchMap.lastKey(), temp.lastKey());
		double[] t = InterpolationHelper.createLinearArray(t_start, deltat, t_end);
		
		double[] t_T = SerializableTreeMapHelper.keysToDoubleArray(temp);
		double[] T = SerializableTreeMapHelper.valuesToDoubleArray(temp);
		double[] T_int = InterpolationHelper.interpolateLinear(t_T, T, t);
		
		double[] t_u = SerializableTreeMapHelper.keysToDoubleArray(switchMap);
		double[] u = SerializableTreeMapHelper.valuesToDoubleArray(switchMap);
		double[] u_int = InterpolationHelper.interpolateBinary(t_u, u, t);
		
		double[] Q_dem = calculateDemand(T_int,u_int,deltat);
		
		for (int i=1;i<Q_dem.length;i++) {
			Q_dem[i-1]=calculateDemand(T_int[i], T_int[i-1], u_int[i-1], deltat);
			retVal.put((Long) Math.round(t[i-1]), Q_dem[i-1]);
		}		
		return retVal;
	}	
}
