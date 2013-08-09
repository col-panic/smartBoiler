package at.fhv.smartgrid;

import java.io.IOException;
import java.util.List;

import at.fhv.smartgrid.rasbpi.RasbPiController;
import at.fhv.smartgrid.rasbpi.internal.ImpulsCounterInformation;
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
			System.out.println(sensorInformation.getSensorId() + " "
					+ sensorInformation.getSensorValue());
		}

		RasbPiController.setRelaisPowerState(true);

	
		for (int i = 0; i < 20; i++) {
			Thread.sleep(20000);
			List<ImpulsCounterInformation> ici = RasbPiController.getImpulsCounterInformation();
			for (ImpulsCounterInformation impulsCounterInformation : ici) {
				System.out.println(impulsCounterInformation.impulsCounterId+" "+impulsCounterInformation.countingStart+" "+impulsCounterInformation.impulsOccurences);
			}
			sl = RasbPiController.getSensorInformation();
			for (SensorInformation sensorInformation : sl) {
				System.out.println(sensorInformation.getSensorId() + " "
						+ sensorInformation.getSensorValue());
			}
		}
		
		RasbPiController.setRelaisPowerState(false);

		

	}
}
