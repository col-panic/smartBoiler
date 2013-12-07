/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.commons.*;
import at.fhv.smartdevices.simulatedDHWH.SimulatedDHWHController;
import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe
 *
 */
public class SchedulerTests {
	
	ISmartController _controller;
	Clock _clock;
	DataManager _dm;
	
	@Before
	public void before()
	{				
		_controller = new SimulatedDHWHController();
		_clock = new Clock(_controller);
		TestHelper.ClearDataManagerSerialization(_clock, _controller);
		_dm = new DataManager(_controller, _clock);
	}
	
	@Test
	public void testDataManager(){		
		ArrayList<ISchedulable> schedulables = new ArrayList<ISchedulable>();
		schedulables.add(_dm);
		
		Scheduler scheduler = new Scheduler(_clock, schedulables, (long) 36000);	
		Thread thread =  new Thread(scheduler);
		thread.start();	
		
		while(thread.isAlive())
		{
			try {
				_controller.setRelaisPowerState(!_controller.getRelaisPowerState());
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
