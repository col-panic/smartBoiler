/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.Calendar;

import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe
 *
 */
public class Clock {
	private long _time;
	private ISmartController _controller;
	private Boolean _simulation = false;
	
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
		//just to instantiate.
		waitFor(1000);
	}
	
	public void waitFor(long time) {
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
