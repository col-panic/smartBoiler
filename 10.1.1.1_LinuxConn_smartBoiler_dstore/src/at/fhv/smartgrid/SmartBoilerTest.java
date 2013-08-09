package at.fhv.smartgrid;

import java.io.IOException;
import java.util.List;

import at.fhv.smartgrid.rasbpi.RasbPiController;
import at.fhv.smartgrid.rasbpi.internal.SensorInformation;

public class SmartBoilerTest {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// try {
		// SystemInfoOutput.printSystemInfo();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		List<SensorInformation> sl = RasbPiController.getSensorInformation();
		for (SensorInformation sensorInformation : sl) {
			System.out.println(sensorInformation.sensorId + " "
					+ sensorInformation.sensorValue);
		}

		RasbPiController.setRelaisPowerState(true);

	
			Thread.sleep(5000);

		
		RasbPiController.setRelaisPowerState(false);

		sl = RasbPiController.getSensorInformation();
		for (SensorInformation sensorInformation : sl) {
			System.out.println(sensorInformation.sensorId + " "
					+ sensorInformation.sensorValue);
		}

	}
}
