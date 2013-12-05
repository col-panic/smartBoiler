/**
 * 
 */
package at.fhv.smartdevices.commons;

/**
 * @author kepe
 *
 */
public interface ISchedulable extends Runnable{
	 /** 
	 * @return the schedule time step
	 */
	public long getScheduleTimeStep();
	
	/**
	 * sets
	 * @param value the schedule time step
	 */
	public void setScheduleTimeStep(Long value);
	
	/**
	 * 
	 * @return The cpu priority
	 */
	public int getPriority();	
	
	public Boolean isRunning();
}
