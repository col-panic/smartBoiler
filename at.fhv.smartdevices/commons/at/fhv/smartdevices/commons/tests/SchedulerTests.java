/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import java.util.ArrayList;

import org.junit.Test;

import at.fhv.smartdevices.commons.*;
import at.fhv.smartdevices.simulatedDHWH.SimulatedDHWHController;
import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe
 *
 */
public class SchedulerTests {
	
	@Test
	public void testDataManager(){
		ISmartController controller = new SimulatedDHWHController();
		Clock clock = new Clock(controller);
		
		DataManager dm = new DataManager(controller, clock);
		
		Scheduler scheduler = new Scheduler(clock);
		
		ArrayList<ISchedulable> schedulables = new ArrayList<ISchedulable>();
		schedulables.add(dm);
		
		scheduler.startScheduling(schedulables,30000);
	}

}
