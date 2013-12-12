/**
 * 
 */
package at.fhv.smartdevices.processManagement.tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.commons.*;
import at.fhv.smartdevices.processManagement.Clock;
import at.fhv.smartdevices.processManagement.DataAquisition;
import at.fhv.smartdevices.processManagement.Scheduler;
import at.fhv.smartdevices.simulatedDHWH.SimulatedDHWHController;
import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe
 *
 */
public class SchedulerTests {
	
	ISmartController _controller;
	Clock _clock;
	DataAquisition _dm;
	SchedulableSwitch _switch;
	
	@Before
	public void before()
	{				
		_controller = new SimulatedDHWHController();
		_clock = new Clock(_controller);
		TestHelper.ClearDataManagerSerialization(_clock, _controller);
		_dm = new DataAquisition(_controller, _clock);
		_switch = new SchedulableSwitch(_controller, _clock);
	}
	
	@Test
	public void testDataAquisition(){		
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
	@Test
	public void testConcurrency(){		
		long totalTimeInMillis = (long) 3600000*48;
		ArrayList<ISchedulable> schedulables = new ArrayList<ISchedulable>();
		schedulables.add(_dm);
		SerializableTreeMap<Long, Boolean> switchingTimes = new SerializableTreeMap<Long,Boolean>();
		Boolean switchState = false;
		for (int i = 0; i < totalTimeInMillis; i+=900000) {
			switchState = !switchState;
			switchingTimes.put(_clock.getDate()+(i),switchState);
		}
		_switch.setSwitchingTimes(switchingTimes);
		schedulables.add(_switch);
		
		Scheduler scheduler = new Scheduler(_clock, schedulables, totalTimeInMillis);	
		Thread thread =  new Thread(scheduler);
		thread.start();	
		
		while(thread.isAlive())
		{
			try {				
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
