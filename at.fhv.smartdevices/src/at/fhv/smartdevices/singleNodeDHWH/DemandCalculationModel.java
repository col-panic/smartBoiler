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
	public static SerializableTreeMap<Long, Float> calculateDemand(SerializableTreeMap<Long, Boolean> switchBoolMap, SerializableTreeMap<Long, Float> temp, long deltat) {
		SerializableTreeMap<Long, Float> retVal = new SerializableTreeMap<Long, Float>();
		SerializableTreeMap<Long, Byte> switchMap = MapHelper.ConvertTreeMapBooleanValueToByte(switchBoolMap); 
		
		float t_start = (float) Math.min(switchMap.firstKey(), temp.firstKey());
		float t_end = (float) Math.max(switchMap.lastKey(), temp.lastKey());
		float[] t = InterpolationHelper.createLinearArray(t_start, deltat, t_end);
		
		float[] t_T = MapHelper.keysToDoubleArray(temp);
		float[] T = MapHelper.valuesToDoubleArray(temp);
		float[] T_int = InterpolationHelper.interpolateLinear(t_T, T, t);
		
		float[] t_u = MapHelper.keysToDoubleArray(switchMap);
		float[] u = MapHelper.valuesToDoubleArray(switchMap);
		float[] u_int = InterpolationHelper.interpolateBinary(t_u, u, t);
				
		for (int i=1;i<t.length;i++) {			
			retVal.put((long) Math.round(t[i-1]), SingleNodeDHWHThermalModel.calculateDemand(T_int[i], T_int[i-1], u_int[i-1], deltat));
		}		
		return retVal;
	}	

}
