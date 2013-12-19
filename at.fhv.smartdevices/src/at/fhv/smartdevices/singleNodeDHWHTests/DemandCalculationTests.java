/**
 * 
 */
package at.fhv.smartdevices.singleNodeDHWHTests;

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
 * 
 */
public class DemandCalculationTests {

	@Test
	public void inverseModelNoDemandNoInterpolationTest() {
		testInverseModel(600, 1, 0, 100, 1e-3);
	}

	@Test
	public void inverseModelNoDemandWithInterpolationTest() {
		testInverseModel(600, 10, 0, 100, 1e-3);
	}

	@Test
	public void inverseModelWithDemandNoInterpolationTest() {
		testInverseModel(600, 1, 1000, 100, 1e-3);
	}
	
	@Test
	public void inverseModelWithDemandWithInterpolationTest() {
		testInverseModel(600, 10, 1000, 100, 1e2);
	}

	private void testInverseModel(int deltat, int interpolationFactor, double maxDemand, int iterations, double error) {
		SerializableTreeMap<Long, Boolean> switchMap = new SerializableTreeMap<Long, Boolean>();
		SerializableTreeMap<Long, Double> temp = new SerializableTreeMap<Long, Double>();
		SerializableTreeMap<Long, Double> demands = new SerializableTreeMap<Long, Double>();
		double temp0 = 40.0;

		Random random = new Random();
		Boolean u = false;
		for (Long i = 0L; i < iterations; i++) {
			temp.put(i * deltat, temp0);
			double demand = random.nextDouble() * maxDemand;
			demands.put(i * deltat, demand);
			byte uByte = 0;
			if (u) {
				uByte = 1;
			}
			double[][] temp1 = SingleNodeDHWHThermalModel.simulateDHWH(uByte, temp0, demand, deltat);
			switchMap.put(i * deltat, temp1[0][0] > 0);
			temp0 = temp1[1][1];
			u = random.nextBoolean();
		}
		TreeMap<Long, Double> measureddemands = DemandCalculationModel.calculateDemand(switchMap, temp, deltat
				/ interpolationFactor);
		if (interpolationFactor == 1) {
			for (Long key : measureddemands.keySet()) {
				System.out.println("real:" + demands.get(key));
				System.out.println("measured:" + measureddemands.get(key));
				assertEquals(demands.get(key), measureddemands.get(key), error);
			}			
		}
		else{
			double measuredSum = getSum(measureddemands.values());
			double realSum = getSum(demands.values());
			assertEquals(realSum*interpolationFactor, measuredSum, error*measureddemands.size());
		}
	}

	private double getSum(Collection<Double> values) {
		double retVal = 0;
		for (Double value : values) {
			retVal+=value;
		}

		return retVal;
	}
}
