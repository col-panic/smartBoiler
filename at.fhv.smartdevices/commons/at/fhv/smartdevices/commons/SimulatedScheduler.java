/**
 * 
 */
package at.fhv.smartdevices.commons;

/**
 * @author kepe_nb
 *
 */
public class SimulatedScheduler implements IScheduler {

	/* (non-Javadoc)
	 * @see at.fhv.smartdevices.commons.IScheduler#pause(long)
	 */
	@Override
	public boolean pause(long timeInSec) {
		
		return false;
	}

}
