package at.fhv.smartdevices.datamining;

import java.util.Random;
import java.util.TreeMap;

import org.junit.Test;

import at.fhv.smartdevices.commons.INumericMetric;
import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.scheduling.DataAquisition;
import at.fhv.smartdevices.scheduling.SchedulableSwitch;
import at.fhv.smartdevices.singleNodeDHWH.SingleNodeDHWHThermalModel;

public class KNNTests {
	@Test
	public void testKNN() {		
		long deltat = 600000;

		SerializableTreeMap<Long, Boolean> switchMap = new SerializableTreeMap<Long, Boolean>();
		SerializableTreeMap<Long, Float> temp = new SerializableTreeMap<Long, Float>();
		SerializableTreeMap<Long, Float> demands = new SerializableTreeMap<Long, Float>();
		TreeMap<Long, Integer> costs = new TreeMap<Long, Integer>();
		float temp0 = 40.0f;

		Random random = new Random();
		Boolean u = false;
		Long i = 0L;
		for (i = 0L; i < 100000; i++) {
			temp.put(i * deltat, temp0);
			float demand = random.nextFloat() > 0.5 ? random.nextFloat() * 10 : 0;
			demands.put(i * deltat, demand);
			byte uByte = 0;
			if (temp0 < 60) {
				uByte = 1;
			}
			double[][] temp1 = SingleNodeDHWHThermalModel.simulateDHWH(uByte, temp0, demand, deltat);
			switchMap.put(i * deltat, temp1[0][0] > 0);
			temp0 = (float) temp1[1][1];
			costs.put(i * deltat, random.nextInt(10));
		}
		for (Long j = i + 1; j < i + 100L; j++) {
			costs.put(j * deltat, random.nextInt(10));
		}

		//KNN knn = new KNN(null, null, "", new MinkowskiMetric(2),deltat, (short)3); 
		
		KNNBasedOptimization.calculateSwitchingTimes(switchMap, temp, costs, deltat, 5L, 5L);
	}

}
