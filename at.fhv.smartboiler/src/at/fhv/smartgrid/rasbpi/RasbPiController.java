package at.fhv.smartgrid.rasbpi;

import java.util.List;

import at.fhv.smartgrid.rasbpi.internal.ImpulsCounterInformation;
import at.fhv.smartgrid.rasbpi.internal.MarketPriceAtom;
import at.fhv.smartgrid.rasbpi.internal.SensorInformation;
import at.fhv.smartgrid.rasbpi.internal.gpio.GpioControl;
import at.fhv.smartgrid.rasbpi.internal.onewire.LinuxOneWireSensorInformation;
import at.fhv.smartgrid.rasbpi.marketprice.MarketPriceHelper;

public class RasbPiController implements ISmartController {

	private static ISmartController instance;

	private RasbPiController() {
	}

	public static ISmartController getInstance() {
		if (instance == null) {
			instance = new RasbPiController();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhv.smartgrid.rasbpi.ISmartController#getSensorInformation()
	 */
	@Override
	public List<SensorInformation> getSensorInformation() {
		return LinuxOneWireSensorInformation.getSensorInformationList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.fhv.smartgrid.rasbpi.ISmartController#getImpulsCounterInformation()
	 */
	@Override
	public List<ImpulsCounterInformation> getImpulsCounterInformation() {
		return GpioControl.getImpulsCounterInformation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.fhv.smartgrid.rasbpi.ISmartController#getCurrentMarketPricesListTimestamp
	 * ()
	 */
	@Override
	public long getCurrentMarketPricesListTimestamp() {
		return MarketPriceHelper.getCurrentMarketPricesListTimestamp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhv.smartgrid.rasbpi.ISmartController#getCurrentMarketPrices()
	 */
	@Override
	public List<MarketPriceAtom> getCurrentMarketPrices() {
		return MarketPriceHelper.getCurrentMarketPrices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.fhv.smartgrid.rasbpi.ISmartController#setRelaisPowerState(boolean)
	 */
	@Override
	public void setRelaisPowerState(boolean newState) {
		GpioControl.setRelaisPowerState(newState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhv.smartgrid.rasbpi.ISmartController#getRelaisPowerState()
	 */
	@Override
	public boolean getRelaisPowerState() {
		return GpioControl.getRelaisPowerState();
	}

}
