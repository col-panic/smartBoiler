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
public class Scheduler {
	private Boolean _abort = false;
	private Clock _clock;

	public Scheduler(Clock clock) {
		_clock = clock;
	}

	public void startScheduling(ArrayList<ISchedulable> schedulables, long loops) {

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
