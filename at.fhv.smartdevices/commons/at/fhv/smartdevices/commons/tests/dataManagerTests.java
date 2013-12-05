/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import java.io.*;
import java.lang.reflect.Field; 

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.commons.DataManager;
import at.fhv.smartdevices.simulatedDHWH.SimulatedController;
/**
 * @author kepe
 *
 */
public class dataManagerTests {
	
	private SimulatedController _controller = new SimulatedController(0);
	
	@Before
	public void before()
	{		
		DataManager dm= new DataManager(_controller);
		Field fields[] = DataManager.class.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.getName().contains("FILENAME")){
				File file;
				try {
					file = new File(field.get(dm).toString());
					if(file.exists()){
						file.delete();
					}					
				} catch (Exception e) {					
					e.printStackTrace();				 
				}				
			}				
		}
	}

	@Test
	public void testSerializationDeserialization()
	{			
		DataManager dm = new DataManager(_controller);
		for (int i = 1; i < 100; i++) {
			long time = 3600*i*1000;
			_controller.SetTime(time);
			dm.collectData(time);			
		}		
		DataManager dm2 = new DataManager(_controller);
		assertArrayEquals(dm.getCostsHistory().keySet().toArray(),dm2.getCostsHistory().keySet().toArray());
		assertArrayEquals(dm.getIciHistory().values().toArray(),dm2.getIciHistory().values().toArray());
		assertArrayEquals(dm.getRelaisPowerStateHistory().values().toArray(), dm2.getRelaisPowerStateHistory().values().toArray());
		assertArrayEquals(dm.GetSensorInformationHistory().values().toArray(), dm2.GetSensorInformationHistory().values().toArray());
	}
		
}
