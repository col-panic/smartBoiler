/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import java.io.File;
import java.lang.reflect.Field;

import at.fhv.smartdevices.commons.Clock;
import at.fhv.smartdevices.commons.DataManager;
import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe_nb
 *
 */
public class TestHelper {

	public static void ClearDataManagerSerialization(Clock clock, ISmartController controller)
	{
		
		long timeStep = 3600*1000;
		clock.waitFor(timeStep);
		DataManager dm= new DataManager(controller, clock);
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
}
