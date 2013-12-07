/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.commons.Clock;
import at.fhv.smartdevices.commons.DataManager;
import at.fhv.smartdevices.simulatedDHWH.SimulatedDHWHController;
import at.fhv.smartgrid.rasbpi.ISmartController;
/**
 * @author kepe
 *
 */
public class dataManagerTests {
	
	ISmartController _controller;
	Clock _clock;
	
	@Before
	public void before()
	{				
		_controller = new SimulatedDHWHController();
		_clock = new Clock(_controller);
		TestHelper.ClearDataManagerSerialization(_clock, _controller);		
	}

	@Test
	public void testSerializationDeserialization()
	{		
		long timeStep = 3600*1000;
		DataManager dm = new DataManager(_controller, _clock);
		for (int i = 1; i < 100; i++) {			
			_clock.waitFor(timeStep);
			dm.collectData();			
		}		
		DataManager dm2 = new DataManager(_controller, _clock);
		assertArrayEquals(dm.getCostsHistory().keySet().toArray(),dm2.getCostsHistory().keySet().toArray());
		assertArrayEquals(dm.getIciHistory().values().toArray(),dm2.getIciHistory().values().toArray());
		assertArrayEquals(dm.getRelaisPowerStateHistory().values().toArray(), dm2.getRelaisPowerStateHistory().values().toArray());
		assertArrayEquals(dm.GetSensorInformationHistory().values().toArray(), dm2.GetSensorInformationHistory().values().toArray());
	}
		
}
