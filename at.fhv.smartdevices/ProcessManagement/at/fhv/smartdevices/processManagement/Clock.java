/**
 * 
 */
package at.fhv.smartdevices.processManagement;

import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import at.fhv.smartdevices.commons.IClock;
import at.fhv.smartdevices.commons.ISimulatedSmartController;
import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe
 * 
 */
public class Clock implements IClock {
	private long _time;
	private ISmartController _controller;
	private Boolean _simulation = false;
	private Lock _lock = new ReentrantLock();

	@Override
	public long getDate() {
		if (_simulation) {
			return _time;
		} else {

			Calendar cal = Calendar.getInstance();
			return cal.getTimeInMillis();
		}
	}

	public Clock(ISmartController controller) {
		_controller = controller;
		_time = Calendar.getInstance().getTimeInMillis();
		_simulation = ISimulatedSmartController.class.isInstance(_controller);
		// just to instantiate.
		waitFor(1000);
	}

	@Override
	public void waitFor(long time) {
		_lock.lock();
		if (time < getDate()) {
			return;
		}
		if (_simulation) {
			((ISimulatedSmartController) _controller).SetTime(time);
			_time = time;
		} else {

			try {
				this.wait(time - getDate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		_lock.unlock();
	}
}
