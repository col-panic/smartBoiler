/**
 * 
 */
package at.fhv.smartdevices.processManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.fhv.smartdevices.commons.IClock;
import at.fhv.smartdevices.commons.ISchedulable;

/**
 * @author kepe_nb
 * 
 */
public class Scheduler implements Runnable{	
	private IClock _clock;
	private ArrayList<ISchedulable> _schedulables;
	private Long _timeFrame;

	public Scheduler(IClock clock, ArrayList<ISchedulable> schedulables) {
		
		//TODO: check Long.Max for correct behavior;
		this(clock, schedulables, Long.MAX_VALUE);		
	}

	public Scheduler(IClock clock, ArrayList<ISchedulable> schedulables, Long timeFrame)
	{
		_timeFrame=timeFrame;
		_clock = clock;
		_schedulables=schedulables;		
	}	

	@Override
	public void run() {
		List<Thread> threads = startScheduling(new ArrayList<ISchedulable>(_schedulables), _timeFrame);		
		for (Thread thread : threads) {
			if(thread.isAlive())
			{
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<Thread> startScheduling(ArrayList<ISchedulable> schedulables, Long elapsingTime) {

		List<Thread> retVal = new ArrayList<Thread>();
		long now =_clock.getDate();
		long end = now+elapsingTime;
		HashMap<ISchedulable, Long> map = new HashMap<ISchedulable, Long>();

		for (ISchedulable schedulable : schedulables) {
			map.put(schedulable, schedulable.getScheduleTimeStep() + now);
		}

		while (now<end) {
			long nextScheduledTime = Long.MAX_VALUE;
			ISchedulable scheduledClass = null;

			for (ISchedulable schedulable : map.keySet()) {
				if (map.get(schedulable) < nextScheduledTime) {
					nextScheduledTime = map.get(schedulable);
					scheduledClass = schedulable;
				}
			}

			_clock.waitUntil(nextScheduledTime);

			Thread thread = new Thread(scheduledClass);
			thread.setPriority(scheduledClass.getPriority());
			thread.run();
			retVal.add(thread);
			
			map.put(scheduledClass, scheduledClass.getScheduleTimeStep() + _clock.getDate());
			now=_clock.getDate();
		}
		return retVal;		
	}
}
