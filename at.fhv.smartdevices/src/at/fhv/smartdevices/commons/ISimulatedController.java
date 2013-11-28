/**
 * 
 */
package at.fhv.smartdevices.commons;

import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe
 *
 */
public interface ISimulatedController extends ISmartController {
	public void SetTime(long date);

}
