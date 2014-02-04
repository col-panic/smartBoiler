package at.fhv.smartdevices.scheduling;

import java.io.File;
import java.lang.reflect.Field;
import at.fhv.smartdevices.commons.IClock;
import at.fhv.smartdevices.scheduling.DataAquisition;
import at.fhv.smartgrid.rasbpi.ISmartController;

/**
 * @author kepe_nb
 * 
 */
public class TestHelper {

	public static void ClearDataManagerSerialization(IClock clock, ISmartController controller) {

		long timeStep = 3600 * 1000;
		clock.waitFor(timeStep);
		DataAquisition dm = new DataAquisition(controller, clock, true);
		Field fields[] = DataAquisition.class.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getName().contains("FILENAME")) {
				File file;
				try {
					file = new File(field.get(dm).toString());
					if (file.exists()) {
						file.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
