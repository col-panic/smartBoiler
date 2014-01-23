/**
 * Provides methods to calculate the demand of a DHWH by using the single node thermal model.
 */
package at.fhv.smartdevices.singleNodeDHWH;

import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.helper.InterpolationHelper;
import at.fhv.smartdevices.helper.MapHelper;

/**
 * @author kepe
 *
 */
public class DemandCalculationModel {
	
	/**
	 * 
	 * @param switchBoolMap a timeseries for the switch 
	 * @param temp a timeseries of temperatures 
	 * @param deltat time step in millis
	 * @return
	 */
	public static SerializableTreeMap<Long, Double> calculateDemand(SerializableTreeMap<Long, Boolean> switchBoolMap, SerializableTreeMap<Long, Float> temp, long deltat, boolean solveOde) {
		SerializableTreeMap<Long, Double> retVal = new SerializableTreeMap<Long, Double>();
		SerializableTreeMap<Long, Byte> switchMap = MapHelper.ConvertTreeMapBooleanValueToByte(switchBoolMap); 
		
		double t_start = (double) Math.min(switchMap.firstKey(), temp.firstKey());
		double t_end = (double) Math.max(switchMap.lastKey(), temp.lastKey());
		double[] t = InterpolationHelper.createLinearArray(t_start, deltat, t_end);
		
		double[] t_T = MapHelper.keysToDoubleArray(temp);
		double[] T = MapHelper.valuesToDoubleArray(temp);
		double[] T_int = InterpolationHelper.interpolateLinear(t_T, T, t);
		
		double[] t_u = MapHelper.keysToDoubleArray(switchMap);
		double[] u = MapHelper.valuesToDoubleArray(switchMap);
		double[] u_int = InterpolationHelper.interpolateBinary(t_u, u, t);
				
		for (int i=1;i<t.length;i++) {			
			retVal.put((long) Math.round(t[i-1]), SingleNodeDHWHThermalModel.calculateDemand(T_int[i], T_int[i-1], u_int[i-1], deltat, solveOde));
		}		
		return retVal;
	}	

}
