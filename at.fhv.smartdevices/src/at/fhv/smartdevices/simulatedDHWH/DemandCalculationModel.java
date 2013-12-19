/**
 * 
 */
package at.fhv.smartdevices.simulatedDHWH;

import at.fhv.smartdevices.commons.SerializableTreeMap;
import at.fhv.smartdevices.helper.InterpolationHelper;
import at.fhv.smartdevices.helper.SerializableTreeMapHelper;

/**
 * @author kepe
 *
 */
public class DemandCalculationModel {
	public static SerializableTreeMap<Long, Double> calculateDemand(SerializableTreeMap<Long, Boolean> switchBoolMap, SerializableTreeMap<Long, Double> temp, long deltat) {
		SerializableTreeMap<Long, Double> retVal = new SerializableTreeMap<Long, Double>();
		SerializableTreeMap<Long, Byte> switchMap = SerializableTreeMapHelper.ConvertTreeMapBooleanValueToByte(switchBoolMap); 
		
		double t_start = (double) Math.min(switchMap.firstKey(), temp.firstKey());
		double t_end = (double) Math.max(switchMap.lastKey(), temp.lastKey());
		double[] t = InterpolationHelper.createLinearArray(t_start, deltat, t_end);
		
		double[] t_T = SerializableTreeMapHelper.keysToDoubleArray(temp);
		double[] T = SerializableTreeMapHelper.valuesToDoubleArray(temp);
		double[] T_int = InterpolationHelper.interpolateLinear(t_T, T, t);
		
		double[] t_u = SerializableTreeMapHelper.keysToDoubleArray(switchMap);
		double[] u = SerializableTreeMapHelper.valuesToDoubleArray(switchMap);
		double[] u_int = InterpolationHelper.interpolateBinary(t_u, u, t);
				
		for (int i=1;i<t.length;i++) {			
			retVal.put((Long) Math.round(t[i-1]), SimulatedDHWH.calculateDemand(T_int[i], T_int[i-1], u_int[i-1], deltat));
		}		
		return retVal;
	}	

}
