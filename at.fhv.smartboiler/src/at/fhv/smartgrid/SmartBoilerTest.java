package at.fhv.smartgrid;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import at.fhv.smartgrid.rasbpi.internal.MarketPriceAtom;
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

		System.out.println("| - Current Prices ");
		List<MarketPriceAtom> mpa = RasbPiController.getInstance().getCurrentMarketPrices();
		for (MarketPriceAtom marketPriceAtom : mpa) {
			System.out.println("|  "+new Date(marketPriceAtom.validFrom)+" "+marketPriceAtom.price);
		}
		
		List<SensorInformation> sl = RasbPiController.getInstance().getSensorInformation();
		for (SensorInformation sensorInformation : sl) {
			System.out.println(sensorInformation.getSensorId() + " "
					+ sensorInformation.getSensorValue());
		}

		RasbPiController.getInstance().setRelaisPowerState(true);

		Thread.sleep(1000);
	
		for (int i = 0; i < 20; i++) {
			Thread.sleep(20000);
			List<ImpulsCounterInformation> ici = RasbPiController.getInstance().getImpulsCounterInformation();
			for (ImpulsCounterInformation impulsCounterInformation : ici) {
				System.out.println(impulsCounterInformation.impulsCounterId+" "+impulsCounterInformation.countingStart+" "+impulsCounterInformation.impulsOccurences);
			}
			sl = RasbPiController.getInstance().getSensorInformation();
			for (SensorInformation sensorInformation : sl) {
				System.out.println(sensorInformation.getSensorId() + " "
						+ sensorInformation.getSensorValue());
			}
		}
			
		
		RasbPiController.getInstance().setRelaisPowerState(false);

		System.exit(0);

	}
}
