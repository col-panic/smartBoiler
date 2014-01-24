/**
 * 
 */
package at.fhv.smartdevices.commons;

/**
 * @author kepe
 * 
 */
public interface ISchedulable extends Runnable {
	/**
	 * @return the schedule time step (-1 if only to run once)(0 for run all the
	 *         time)
	 */
	public long getScheduleTimeStep();

	/**
	 * 
	 * @return The cpu priority
	 */
	public int getPriority();

	public Boolean getExeSingleThreaded();

}
