package at.fhv.smartdevices.helper;

import java.util.SortedMap;

import at.fhv.smartdevices.commons.SerializableTreeMap;

public class MapHelper {
	public static <K extends Number, V>  float[] keysToDoubleArray(SortedMap<K, V> map)
	{
		int amount = map.keySet().size();
		Number[] keys = (Number[]) map.keySet().toArray(new Number[amount]);	
		float[] retVal = new float[amount];
		for (int i=0;i<amount;i++) {
			retVal[i] = keys[i].floatValue();
		}		
		return retVal;		
	}		
	
	public static <K, V extends Number>  float[] valuesToDoubleArray(SortedMap<K, V> map)
	{
		int amount = map.values().size();
		Number[] values = (Number[]) map.values().toArray(new Number[amount]);
		
		float[] retVal = new float[amount];
		for (int i=0;i<amount;i++) {			
			retVal[i] = values[i].floatValue();
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
