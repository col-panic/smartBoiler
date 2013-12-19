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
		long deltat=600;
		Random random = new Random();
		Boolean u = false;
		for (Long i=0L;i<100;i++){
			
			temp.put(i*deltat, temp0);
			byte uByte = 0;
			if(u)
			{uByte=1;}
			double[][] temp1 = SimulatedDHWH.simulateDHWH(uByte, temp0, random.nextDouble(), deltat);
			switchMap.put(i*deltat, temp1[0][0]>0);
			temp0 = temp1[1][1];
			u = random.nextBoolean();				
		}	
		TreeMap<Long, Double> demand = SimulatedDHWH.calculateDemand(switchMap, temp, deltat/600);
		for (Double d : demand.values()) {
			assertTrue(Math.abs(d)<10);
		}
		
	}	
}
