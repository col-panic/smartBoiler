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

	private void testInverseModel(int deltat, int interpolationFactor, float maxDemand, int iterations, double error) {
		SerializableTreeMap<Long, Boolean> switchMap = new SerializableTreeMap<Long, Boolean>();
		SerializableTreeMap<Long, Float> temp = new SerializableTreeMap<Long, Float>();
		SerializableTreeMap<Long, Float> demands = new SerializableTreeMap<Long, Float>();
		float temp0 = 40.0f;

		Random random = new Random();
		Boolean u = false;
		for (Long i = 0L; i < iterations; i++) {
			temp.put(i * deltat, temp0);
			float demand = random.nextFloat() * maxDemand;
			demands.put(i * deltat, demand);
			byte uByte = 0;
			if (u) {
				uByte = 1;
			}
			double[][] temp1 = SingleNodeDHWHThermalModel.simulateDHWH(uByte, temp0, demand, deltat);
			switchMap.put(i * deltat, temp1[0][0] > 0);
			temp0 = (float) temp1[1][1];
			u = random.nextBoolean();
		}
		TreeMap<Long, Float> measureddemands = DemandCalculationModel.calculateDemand(switchMap, temp, deltat
				/ interpolationFactor);
		if (interpolationFactor == 1) {
			for (Long key : measureddemands.keySet()) {
				System.out.println("real:" + demands.get(key));
				System.out.println("measured:" + measureddemands.get(key));
				assertEquals(demands.get(key), measureddemands.get(key), error);
			}			
		}
		else{
			float measuredSum = getSum(measureddemands.values());
			float realSum = getSum(demands.values());
			assertEquals(realSum*interpolationFactor, measuredSum, error*measureddemands.size());
		}
	}

	private float getSum(Collection<Float> values) {
		float retVal = 0;
		for (Float value : values) {
			retVal+=value;
		}

		return retVal;
	}
}
