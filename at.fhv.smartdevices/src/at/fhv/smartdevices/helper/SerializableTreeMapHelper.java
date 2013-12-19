package at.fhv.smartdevices.helper;

import at.fhv.smartdevices.commons.SerializableTreeMap;

public class SerializableTreeMapHelper {
	public static <K extends Number, V>  double[] keysToDoubleArray(SerializableTreeMap<K, V> map)
	{
		int amount = map.keySet().size();
		Number[] keys = (Number[]) map.keySet().toArray(new Number[amount]);	
		double[] retVal = new double[amount];
		for (int i=0;i<amount;i++) {
			retVal[i] = keys[i].doubleValue();
		}		
		return retVal;		
	}		
	
	public static <K, V extends Number>  double[] valuesToDoubleArray(SerializableTreeMap<K, V> map)
	{
		int amount = map.values().size();
		Number[] values = (Number[]) map.values().toArray(new Number[amount]);
		
		double[] retVal = new double[amount];
		for (int i=0;i<amount;i++) {			
			retVal[i] = values[i].doubleValue();
		}		
		return retVal;		
	}

	public static SerializableTreeMap<Long, Byte> ConvertTreeMapBooleanValueToByte(SerializableTreeMap<Long, Boolean> map){
		SerializableTreeMap<Long, Byte> retVal = new SerializableTreeMap<Long, Byte>();
			for (Long key : map.keySet()) {
				retVal.put(key,map.get(key)?(byte)1:(byte)0);
			}
		return retVal;
	}
}
