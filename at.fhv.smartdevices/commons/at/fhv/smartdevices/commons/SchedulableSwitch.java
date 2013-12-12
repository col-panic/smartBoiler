/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe_nb controls the correct switching of the device by setting the
 *         relais state(s).
 */
public class SchedulableSwitch implements ISchedulable {

	private ISmartController _controller;
	private IReadOnlyClock _clock;

	private SerializableTreeMap<Long, Boolean> _switchingTimes = new SerializableTreeMap<Long, Boolean>();
	private Long _next;
	private Lock _lock = new ReentrantLock();
	private final Long TimeLoop = (long) 600000;

	public SchedulableSwitch(ISmartController controller, IReadOnlyClock clock) {
		_controller = controller;

		_clock = clock;
	}

	public void setSwitchingTimes(SerializableTreeMap<Long, Boolean> switchingTimes) {
		_lock.lock();

		_switchingTimes.putAll(switchingTimes);

		_lock.unlock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		_lock.lock();
		try {
			if (_switchingTimes != null) {
				if (_switchingTimes.containsKey(_next)) {
					_controller.setRelaisPowerState(_switchingTimes.get(_next));
					_switchingTimes.remove(_next);
				}
			}
		} finally {
			_lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhv.smartdevices.commons.ISchedulable#getScheduleTimeStep()
	 */
	@Override
	public long getScheduleTimeStep() {
		_lock.lock();
		try {
			_next = _switchingTimes.ceilingEntry(_clock.getDate()).getKey();			
		} catch (NullPointerException e) {
			_switchingTimes.put(_clock.getDate() + TimeLoop, _switchingTimes.lastEntry().getValue());
			_next = _switchingTimes.lastKey();

		} finally {
			_lock.unlock();
		}
		return _next - _clock.getDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhv.smartdevices.commons.ISchedulable#getPriority()
	 */
	@Override
	public int getPriority() {

		return 10;
	}

}
