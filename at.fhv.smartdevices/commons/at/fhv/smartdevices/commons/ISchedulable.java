/**
 * 
 */
package at.fhv.smartdevices.commons;

/**
 * @author kepe
 *
 */
public interface ISchedulable {
	public void scheduledMethod(Long time);
	public long getScheduleTimeStep();
	public void setScheduleTimeStep(Long value);
	
}
