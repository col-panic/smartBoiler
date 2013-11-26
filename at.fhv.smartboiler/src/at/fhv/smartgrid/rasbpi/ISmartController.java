package at.fhv.smartgrid.rasbpi;

import java.util.List;

import at.fhv.smartgrid.rasbpi.internal.ImpulsCounterInformation;
import at.fhv.smartgrid.rasbpi.internal.MarketPriceAtom;
import at.fhv.smartgrid.rasbpi.internal.SensorInformation;

public interface ISmartController {

	/**
	 * Deliver a list of 1-wire sensor information entries
	 * @return
	 */
	public abstract List<SensorInformation> getSensorInformation();

	/**
	 * Deliver a list of connected impuls counter information entries
	 * @return
	 */
	public abstract List<ImpulsCounterInformation> getImpulsCounterInformation();

	public abstract long getCurrentMarketPricesListTimestamp();

	public abstract List<MarketPriceAtom> getCurrentMarketPrices();

	/**
	 * 
	 * @param newState state to toggle the relais to
	 */
	public abstract void setRelaisPowerState(boolean newState);

	/**
	 * @return current state of the relais
	 */
	public abstract boolean getRelaisPowerState();

}