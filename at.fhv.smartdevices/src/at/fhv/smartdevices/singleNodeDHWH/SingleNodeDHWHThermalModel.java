package at.fhv.smartdevices.singleNodeDHWH;
/**
 * @author kepe
 * 
 */
public class SingleNodeDHWHThermalModel {

	// private static final double c_p=4.18e3;//[J/kgK] specific heat
	static final double pEl = 2175;// [W] el. power of the heater
	// private static final double tempIn=12;//[�C] temperature of inlet water;
	private static final double tempEnv = 20;// [�C] outside temperature;
	private static final double tempMax = 90;// [�C] max allowed temperature in
												// water heater;
	// private static final double tempMin=38;//[�C] lowest allowed temperature
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
	 * 			  the time step size in millis
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
	 * 			  the time step size in millis
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
	 * 			  the time step size in millis
	 * @return a two dimensional array holding: the adapted switch [0][*] and
	 *         the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand, int timeSteps, double deltat) {
		deltat = deltat/1000;
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
	
	/**
	 * Calculates the loss due to demand (\dot Q_dem) for a given equi-distant time window
	 * Provide the inputs in a way, that the relais and temperature entries refer to the same positions in time by index
	 * @param temp temperature
	 * @param u relais state
	 * @param deltat time step size in millis
	 * @return the values of the demand as a double array
	 */
	public static float[] calculateDemand(float[] temp, float[] u, float deltat){
		float[] Q_dem = new float[temp.length];
		for (int i=1;i<temp.length;i++) {	    	
			Q_dem[i-1]=calculateDemand(temp[i], temp[i-1], u[i-1], deltat);
		}	
		return Q_dem;
	}
	
	/**
	 * Calculates the loss due to demand (\dot Q_dem) 
	 * @param temp_1 resulting temperature
	 * @param temp_0 intial temperature
	 * @param u relais state
	 * @param deltat time step size in millis
	 * @return the value of the demand
	 */
	public static float calculateDemand(float temp_1, float temp_0, float u, float deltat){
		deltat = deltat/1000;
		float dem = (float) -((p2*(temp_1-temp_0)/deltat)-(u*pEl)-(p1*(tempEnv-((temp_0+temp_1)/2))));
		//double lambda = Math.exp(-p1/p2*deltat);
		//double dem = ((temp_0*lambda -(temp_1))*p1/(1-lambda)+(u*pEl)+(tempEnv*p1));
		return dem;
	}

	
}
