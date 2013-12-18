/**
 * 
 */
package at.fhv.smartdevices.simulatedDHWHTests;

import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.*;

import org.junit.Test;

import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.simulatedDHWH.SimulatedDHWH;

/**
 * @author kepe
 *
 */
public class SimulatedDHWHTests {

	@Test
	public void testInverseModel(){
		SerializableTreeMap<Long, Boolean> switchMap = new SerializableTreeMap<Long, Boolean>();
		SerializableTreeMap<Long, Double> temp = new SerializableTreeMap<Long,Double>();
		double temp0=40.0;
		long deltat=6000;
		Random random = new Random();
		Boolean u = false;
		for (Long i=0L;i<100;i++){
			switchMap.put(i*deltat, u);
			temp.put(i*deltat, temp0);
			byte uByte = 0;
			if(u)
			{uByte=1;}
			double[][] temp1 = SimulatedDHWH.simulateDHWH(uByte, temp0, random.nextDouble(), deltat);
			temp0 = temp1[1][1];
			u = random.nextBoolean();				
		}	
		TreeMap<Long, Double> demand = SimulatedDHWH.calculateDemand(switchMap, temp, deltat);
		for (Double d : demand.values()) {
			assertEquals(d.doubleValue(), 0.0);
		}
		
	}	
}
