/**
 * 
 */
package at.fhv.smartdevices.commons;

/**
 * @author kepe
 *
 */
public interface IClock extends IReadOnlyClock {
	public void waitFor(long time);
}
