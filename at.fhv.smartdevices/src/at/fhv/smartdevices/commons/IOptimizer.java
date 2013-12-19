/**
 * 
 */
package at.fhv.smartdevices.commons;

import at.fhv.smartdevices.scheduling.DataAquisition;

/**
 * @author kepe
 *
 */
public interface IOptimizer extends ISchedulable{
	public SerializableTreeMap<Long, Boolean> CalculateSwitchingTimes(DataAquisition dA);

}
