/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.Calendar;

import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe_nb
 *
 */
public abstract class BaseScheduler{
	private ISmartController _controller;	
	private long _start;
	private int _dataAquisitionTimeStep = 1000;
	private int _optimizationTimeStep = 3600000;
	
	public long getDate(){
			Calendar cal = Calendar.getInstance();
			return cal.getTimeInMillis();		
	}
		
	public boolean pause(long timeInSec)
	{
		if (ISimulatedSmartController.class.isInstance(_controller))
		{
			ISimulatedSmartController _simController = (ISimulatedSmartController) _controller;
			_simController.SetTime(getDate()+timeInSec);		
		}
		return true;
	}
	public BaseScheduler(ISmartController controller){
		_controller=controller;		
		_start = Calendar.getInstance().getTimeInMillis();
	}
	
	public void startScheduling(DataManager dataMngr, IModelBuilder modelBdr, IOptimizer optimizer){
		
	}
}
