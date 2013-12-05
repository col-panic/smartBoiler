/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.commons.DataManager;
import at.fhv.smartdevices.commons.ISimulatedSmartController;
import at.fhv.smartdevices.simulatedDHWH.SimulatedController;
import at.fhv.smartgrid.rasbpi.*;
/**
 * @author kepe
 *
 */
public class dataManagerTests {

	@Test
	public void testSerializationDeserialization()
	{		
		ISimulatedSmartController controller = new SimulatedController(0);
		DataManager dm = new DataManager(controller);
		for (int i = 1; i < 100; i++) {
			long time = 3600*i*1000;
			controller.SetTime(time);
			dm.collectData(time);
			
		}
		
		
	}
		
}
