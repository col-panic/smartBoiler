package at.fhv.smartdevices.helper;

import java.util.SortedMap;

import at.fhv.smartdevices.commons.SerializableTreeMap;

public class MapHelper {
	public static <K extends Number, V>  double[] keysToDoubleArray(SortedMap<K, V> map)
	{
		int amount = map.keySet().size();
		Number[] keys = (Number[]) map.keySet().toArray(new Number[amount]);	
		double[] retVal = new double[amount];
		for (int i=0;i<amount;i++) {
			retVal[i] = keys[i].doubleValue();
		}		
		return retVal;		
	}		
	
	public static <K, V extends Number>  double[] valuesToDoubleArray(SortedMap<K, V> map)
	{
		int amount = map.values().size();
		Number[] values = (Number[]) map.values().toArray(new Number[amount]);
		
		double[] retVal = new double[amount];
		for (int i=0;i<amount;i++) {			
			retVal[i] = values[i].doubleValue();
		}		
		return retVal;		
	}

	public static SerializableTreeMap<Long, Byte> ConvertTreeMapBooleanValueToByte(SortedMap<Long, Boolean> map){
		SerializableTreeMap<Long, Byte> retVal = new SerializableTreeMap<Long, Byte>();
			for (Long key : map.keySet()) {
				retVal.put(key,map.get(key)?(byte)1:(byte)0);
			}
		return retVal;
	}
}
