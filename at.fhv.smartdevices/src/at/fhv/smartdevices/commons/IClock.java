/**
 * 
 */
package at.fhv.smartdevices.commons;

/**
 * @author kepe
 * 
 */
public interface IClock extends IReadOnlyClock {
	public void waitUntil(long dateInMillis);

	public void waitFor(long timeInMillis);
}
