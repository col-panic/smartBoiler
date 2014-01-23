/**
 * 
 */
package at.fhv.smartdevices.singleNodeDHWH.tests;

import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.*;

import org.junit.Test;

import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.singleNodeDHWH.DemandCalculationModel;
import at.fhv.smartdevices.singleNodeDHWH.SingleNodeDHWHThermalModel;

/**
 * @author kepe
 * Tests for the class @link DemandCalculationModel
 * All test use @see {@link DemandCalculationTests#testInverseModel(int, int, double, int, double)}
 * @class 
 */
public class DemandCalculationTests {
		
	@Test
	public void inverseModelCoolDownProcessTest() {
		testInverseModel(60000, 1, 0, 60*24*5, 1e-1, 0, 60);
	}
	@Test
	public void inverseModelHeatUpProcessTest() {
		testInverseModel(60000, 1, 0, 4*24,  1e-1, 1, 12);
	}
	@Test
	public void inverseModelNoDemandWithInterpolationTest() {
		testInverseModel(60000, 10, 0, 60*24,  1e-1, 0, 40);
	}

	@Test
	public void inverseModelWithDemandNoInterpolationTest() {
		testInverseModel(60000, 1, 20, 60*24,  1e-1, 0, 40);
	}
	
	@Test
	public void inverseModelWithDemandWithInterpolationTest() {
		testInverseModel(60000, 10, 20, 60*24,  1e-1, 0, 40);
	}

	/**
	 * Creates random demands and on/off switch times, simulates the dhwh and finally, calculates the demand by using the inverse model.
	 * @param deltat time step (ms)
	 * @param interpolationFactor (nr of steps to interpolate per time step)
	 * @param maxDemand max demand in W
	 * @param iterations nr of time steps
	 * @param error allowed error
	 * @param switchProb probability the switch is on
	 */
	private void testInverseModel(int deltat, int interpolationFactor, double maxDemand, int iterations, double error, double switchProb, float initialTemp) {
		SerializableTreeMap<Long, Boolean> switchMap = new SerializableTreeMap<Long, Boolean>();
		SerializableTreeMap<Long, Float> temp = new SerializableTreeMap<Long, Float>();
		SerializableTreeMap<Long, Double> demands = new SerializableTreeMap<Long, Double>();
		float temp0 = initialTemp;
		System.out.println("interpolation factor:" + interpolationFactor + " / maxDemand: "+maxDemand);
		Random random = new Random();
		Boolean u = false;
		
		for (Long i = 0L; i < iterations; i++) {
			temp.put(i * deltat, temp0);
			double demand=0;
			if(maxDemand>0){demand = random.nextDouble()>0.3? random.nextDouble() * maxDemand/deltat*1000:0;}
			demands.put(i * deltat, demand);
			byte uByte = 0;
			if (u) {
				uByte = 1;
			}
			double[][] simResult = SingleNodeDHWHThermalModel.simulateDHWH(uByte, temp0, demand, deltat);
			switchMap.put(i * deltat, simResult[0][0] > 0);
			temp0 =  (float) simResult[1][1];
			u=random.nextDouble()<switchProb;
		}
		TreeMap<Long, Double> measureddemands = DemandCalculationModel.calculateDemand(switchMap, temp, deltat
				/ interpolationFactor, false);
		TreeMap<Long, Double> measureddemandsOde = DemandCalculationModel.calculateDemand(switchMap, temp, deltat
				/ interpolationFactor, true);
		if (interpolationFactor == 1) {
			for (Long key : measureddemands.keySet()) {
				System.out.println("demand (W):" + demands.get(key));
				System.out.println("switch: "+switchMap.get(key));
				System.out.println("temp: "+temp.get(key));
				System.out.println("measured error solving energy balance (W):" + (measureddemands.get(key)- demands.get(key)));
				System.out.println("measured error solving ode (W):" + (measureddemandsOde.get(key)- demands.get(key)));
				assertEquals(demands.get(key), measureddemandsOde.get(key), error);
			}			
		}
		double realSum = getSum(demands.values());			
			System.out.println("sum of demand (W):" + realSum*interpolationFactor);
			System.out.println("sum error solving energy balance (W):" +((getSum(measureddemands.values()))-realSum*interpolationFactor));
			System.out.println("sum error solving ODE (W):" +((getSum(measureddemandsOde.values()))-realSum*interpolationFactor));
			assertEquals(realSum*interpolationFactor, getSum(measureddemandsOde.values()), error*measureddemandsOde.size());
		
	}

	private double getSum(Collection<Double> values) {
		double retVal = 0;
		for (Double value : values) {
			retVal+=value;
		}

		return retVal;
	}
}
