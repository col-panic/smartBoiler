package at.fhv.smartdevices.simulatedDHWH;
/**
 * @author kepe
 *
 */
public class SimulatedDHWH{
	
	 //private static final double c_p=4.18e3;//[J/kgK] specific heat
	 static final double pEl=2175;//[W] el. power of the heater
	 //private static final double tempIn=12;//[°C] temperature of inlet water;
	 private static final double tempEnv=20;//[°C] outside temperature;	 
	 private static final double tempMax=90;//[°C] max allowed temperature in water heater;
	 //private static final double tempMin=38;//[°C] lowest allowed temperature in water heater;	 
	 private static final double p1=1.1972;//[W/K] heat conduction * area, Water below Iso/2;
	 private static final double p2=6.815e5;//[J/K] thermal mass (m_i*c_i) envelope (St,Iso,Bl);
	 //private static final double tempDemand = 38;	
	 private static final double deltat=60;
	 
	 /**
	 * @param u the switch 	 
	 * @param startTemp start temperature
	 * @param demand the demand in W
	 * @return a two dimensional array holding:
	 * the adapted switch [0][*] and the resulted temperature[1][*]
	 */
	 public static double[][] simulateDHWH(byte u, double startTemp, double demand)
	 {			 
		 return simulateDHWH(new byte[]{u}, startTemp, new double[]{demand}, 1);
	 }
	 
	 /**
	 * @param u the switch 
	 * @param startTemp start temperature
	 * @param demand the demand in W
	 * @return a two dimensional array holding:
	 * the adapted switch [0][*] and the resulted temperature[1][*]
		 */
	 public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand)
	 {
		 int timeSteps=Math.min(u.length, demand.length);
		 return simulateDHWH(u, startTemp, demand, timeSteps);
	 }
	 
	 /**
	 * @param u the switch 	
	 * @param startTemp start temperature
	 * @param demand the demand in W
	 * @param timeSteps the number of steps to take
	 * @return a two dimensional array holding:
	 * the adapted switch [0][*] and the resulted temperature[1][*]
	 */
	public static double[][] simulateDHWH(byte[] u, double startTemp, double[] demand, int timeSteps)
	 {
		 double[] temp = new double[timeSteps+1];
		 double[][] retVal =new double[2][timeSteps+1];
		 temp[0]=startTemp;
		 double lambda = Math.exp(-(deltat*p1/p2));
		 for (int i=1; i<=timeSteps; i++) 
		 {
			 if (temp[i-1]>tempMax)
	             {
				 	u[i-1]=0;
	             }
			 else
			 {
				 temp[i]=(temp[i-1]*lambda)+((1-lambda)*((u[i-1]*pEl/p1) - (demand[i-1]/p1)+tempEnv));
			 }			 
		 }
		 for (int i=0; i<timeSteps; i++) 
		 {
			 retVal[0][i]=u[i];
			 retVal[1][i]=temp[i];
		 }
		 retVal[1][timeSteps]=temp[timeSteps];
	     return retVal;
	 }
}
