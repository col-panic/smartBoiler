package at.fhv.smartgrid.rasbpi;

import java.util.List;

import at.fhv.smartgrid.rasbpi.internal.ImpulsCounterInformation;
import at.fhv.smartgrid.rasbpi.internal.MarketPriceAtom;
import at.fhv.smartgrid.rasbpi.internal.SensorInformation;
import at.fhv.smartgrid.rasbpi.internal.gpio.GpioControl;
import at.fhv.smartgrid.rasbpi.internal.onewire.LinuxOneWireSensorInformation;

public class RasbPiController {

	/**
	 * Deliver a list of 1-wire sensor information entries
	 * @return
	 */
	public static List<SensorInformation> getSensorInformation() {
		return LinuxOneWireSensorInformation.getSensorInformationList();
	} 
	
	public static List<ImpulsCounterInformation> getImpulsCounterInformation() {
		return GpioControl.getImpulsCounterInformation();
	}
	
	public static List<Integer> getTipImpulsCountInformation() {
		return null;
	}
	
	public static long getCurrentMarketPricesListTimestamp() {
		return 0L;
	}
	
	public static List<MarketPriceAtom> getCurrentMarketPrices() {
		return null;
	}
	
	/**
	 * 
	 * @param newState state to toggle the relais to
	 */
	public static void setRelaisPowerState(boolean newState) {
		GpioControl.setRelaisPowerState(newState);
	}
	
	/**
	 * @return current state of the relais
	 */
	public static boolean getRelaisPowerState() {
		return GpioControl.getRelaisPowerState();
	}

}
