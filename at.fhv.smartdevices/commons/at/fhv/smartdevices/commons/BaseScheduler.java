/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe_nb
 * 
 */
public abstract class BaseScheduler {
	private ISmartController _controller;
	private long _time;
	private Boolean _abort = false;
	private Boolean _simulation = false;

	public long getDate() {
		if (_simulation) {
			return _time;
		} else {

			Calendar cal = Calendar.getInstance();
			return cal.getTimeInMillis();
		}
	}

	public boolean pause(long timeInSec) {
		if (ISimulatedSmartController.class.isInstance(_controller)) {
			ISimulatedSmartController _simController = (ISimulatedSmartController) _controller;
			_simController.SetTime(getDate() + timeInSec);
		}
		return true;
	}

	public BaseScheduler(ISmartController controller) {
		_controller = controller;
		_time = Calendar.getInstance().getTimeInMillis();

		_simulation = ISimulatedSmartController.class.isInstance(_controller);
	}

	public void startScheduling(ArrayList<ISchedulable> schedulables) {

		HashMap<ISchedulable, Long> map = new HashMap<ISchedulable, Long>();

		for (ISchedulable schedulable : schedulables) {
			map.put(schedulable, schedulable.getScheduleTimeStep() + getDate());
		}

		while (!_abort) {
			long nextScheduledTime = Long.MAX_VALUE;
			ISchedulable scheduledClass = null;

			for (ISchedulable schedulable : map.keySet()) {
				if (map.get(schedulable) < nextScheduledTime) {
					nextScheduledTime = map.get(schedulable);
					scheduledClass = schedulable;
				}
			}

			waitFor(nextScheduledTime);
			scheduledClass.scheduledMethod(getDate());
			map.put(scheduledClass, scheduledClass.getScheduleTimeStep() + getDate());
		}
	}

	private void waitFor(long time) {
		if (_simulation) {
			((ISimulatedSmartController) _controller).SetTime(time);
			_time = time;
		} else {
			if (time < getDate()) {
				return;
			}
			try {
				this.wait(time - getDate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
