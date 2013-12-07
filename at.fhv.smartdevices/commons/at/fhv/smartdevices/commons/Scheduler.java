/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author kepe_nb
 * 
 */
public class Scheduler implements Runnable{	
	private Clock _clock;
	private ArrayList<ISchedulable> _schedulables;
	private Long _loops;

	public Scheduler(Clock clock, ArrayList<ISchedulable> schedulables) {
		
		//TODO: check Long.Max for correct behavior;
		this(clock, schedulables, Long.MAX_VALUE);		
	}

	public Scheduler(Clock clock, ArrayList<ISchedulable> schedulables, Long loops)
	{
		_loops=loops;
		_clock = clock;
		_schedulables=schedulables;		
	}	

	@Override
	public void run() {
		startScheduling(new ArrayList<ISchedulable>(_schedulables), _loops);		
	}
	
	private void startScheduling(ArrayList<ISchedulable> schedulables, Long loops) {

		long loop=1;
		HashMap<ISchedulable, Long> map = new HashMap<ISchedulable, Long>();

		for (ISchedulable schedulable : schedulables) {
			map.put(schedulable, schedulable.getScheduleTimeStep() + _clock.getDate());
		}

		while (loop<loops) {
			long nextScheduledTime = Long.MAX_VALUE;
			ISchedulable scheduledClass = null;

			for (ISchedulable schedulable : map.keySet()) {
				if (map.get(schedulable) < nextScheduledTime) {
					nextScheduledTime = map.get(schedulable);
					scheduledClass = schedulable;
				}
			}

			_clock.waitFor(nextScheduledTime);

			if (!scheduledClass.isRunning()) {
				Thread thread = new Thread(scheduledClass);
				thread.setPriority(scheduledClass.getPriority());
				thread.run();
			}
			map.put(scheduledClass, scheduledClass.getScheduleTimeStep() + _clock.getDate());
			loop++;
		}
	}
}
