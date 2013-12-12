/**
 * 
 */
package at.fhv.smartdevices.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.Clock;
import at.fhv.smartdevices.DataAquisition;
import at.fhv.smartdevices.SchedulableSwitch;
import at.fhv.smartdevices.Scheduler;
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
				e.printStackTrace();
			}
		}
		
		
		
	}
	@Test
	public void testConcurrentDataAquisitionAndSwitch(){		
		long totalTimeInMillis = (long) 1000*60*60*24;
		ArrayList<ISchedulable> schedulables = new ArrayList<ISchedulable>();
		schedulables.add(_dm);
		SerializableTreeMap<Long, Boolean> switchingTimes = new SerializableTreeMap<Long,Boolean>();
		Boolean switchState = false;
		long now = _clock.getDate();
		for (int i = 0; i <= totalTimeInMillis; i+=1000*60*15) {
			switchState = !switchState;
			switchingTimes.put(now+(i),switchState);
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
				e.printStackTrace();
			}
		}
		TreeMap<Long,Boolean> psh = _dm.getRelaisPowerStateHistory();
		Boolean relaisState= true;		
		assertEquals(psh.size()+1,switchingTimes.size());
		
		for (Long key : psh.navigableKeySet()) {
			assertEquals(psh.get(key), relaisState);
			relaisState=!relaisState;
		}		
		
	}

}
