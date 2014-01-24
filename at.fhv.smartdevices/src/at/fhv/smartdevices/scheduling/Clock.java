/**
 * 
 */
package at.fhv.smartdevices.scheduling;

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
	public long SimulationFactor = 1500 * 60; // 1500*60 refers to about 1sec,
												// 1500 to about 1min per day

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
	public void waitFor(long timeInMillis) {
		waitUntil(getDate() + timeInMillis);
	}

	@Override
	public void waitUntil(long dateInMillis) {
		_lock.lock();
		try {
			if (dateInMillis < getDate()) {
				return;
			}
			if (_simulation) {
				((ISimulatedSmartController) _controller).SetTime(dateInMillis);
				try {
					Thread.sleep((dateInMillis - _time) / SimulationFactor);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				_time = dateInMillis;
			} else {

				try {
					this.wait(dateInMillis - getDate());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			_lock.unlock();
		}
	}
}
