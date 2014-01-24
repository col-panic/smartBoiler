package at.fhv.smartdevices.singleNodeDHWH.tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import at.fhv.smartdevices.singleNodeDHWH.SimulatedDHWHController;
import at.fhv.smartgrid.rasbpi.internal.*;

public class ControllerTests {

	Calendar _currentTime = Calendar.getInstance();

	@Before
	public void before() {
		_currentTime = Calendar.getInstance();
		_currentTime.set(Calendar.HOUR, 1);
		_currentTime.set(Calendar.AM_PM, Calendar.AM);
		_currentTime.set(Calendar.MINUTE, 0);
		_currentTime.set(Calendar.SECOND, 0);
	}

	@Test
	public void testTemperatureDecrease() {
		try {
			SimulatedDHWHController sc = new SimulatedDHWHController();
			sc.SetTime(getDate(0, 0, 3));
			sc.SetTime(getDate(1, 0, 0));
			float oldSensorValue = -Float.MAX_VALUE;
			List<SensorInformation> sis = sc.getSensorInformation();
			for (SensorInformation si : sis) {
				oldSensorValue = si.getSensorValue();
				break;
			}

			sc.SetTime(getDate(1, 0, 0));
			sis = sc.getSensorInformation();
			for (SensorInformation si : sis) {
				assertTrue(si.getSensorValue() < oldSensorValue);
				break;
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testTemperatureIncrease() {
		try {
			SimulatedDHWHController sc = new SimulatedDHWHController();
			sc.SetTime(getDate(0, 0, 3));
			sc.SetTime(getDate(1, 0, 0));
			float oldSensorValue = Float.MAX_VALUE;
			List<SensorInformation> sis = sc.getSensorInformation();
			for (SensorInformation si : sis) {
				oldSensorValue = si.getSensorValue();
				break;
			}
			sc.setRelaisPowerState(true);
			sc.SetTime(getDate(3, 0, 0));
			sis = sc.getSensorInformation();
			for (SensorInformation si : sis) {
				assertTrue(si.getSensorValue() > oldSensorValue);
				break;
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void TestPriceSignalTimeStamp() {

		// set the clock to 1pm to ensure a first price signal
		SimulatedDHWHController sc = new SimulatedDHWHController();
		sc.SetTime(getDate(0, 0, 3));
		_currentTime.getTime().toString();
		sc.SetTime(getDate(0, 13, 0));
		assertTrue(sc.getCurrentMarketPricesListTimestamp() > 0);

		// set the clock to 4pm and check, whether still the same price signal
		long st = getDate(0, 3, 0);
		sc.SetTime(st);
		long ct1 = sc.getCurrentMarketPricesListTimestamp();
		assertTrue(ct1 < st);

		// set the clock to 8pm and ensure still the same price signal
		st = getDate(0, 4, 0);
		sc.SetTime(st);
		long ct2 = sc.getCurrentMarketPricesListTimestamp();
		assertTrue(ct2 == ct1);

		// set the clock to 12:10 at noon the next day and ensure another price
		// signal
		long st2 = getDate(0, 16, 10);
		sc.SetTime(st2);
		long ct3 = sc.getCurrentMarketPricesListTimestamp();
		assertTrue(ct3 > ct1);

		System.out.println("state timestamp: " + (new Date(st2)));
		System.out.println("price list timestamp: " + (new Date(ct3)));

		List<MarketPriceAtom> mpas = sc.getCurrentMarketPrices();
		long oldmpaValidity = ct3;
		for (MarketPriceAtom mpa : mpas) {
			System.out.println((new Date(mpa.validFrom)));
			assertTrue(mpa.validFrom > oldmpaValidity);
			oldmpaValidity = mpa.validFrom;
		}
	}

	@Test
	public void testImpulsCounter() {
		try {
			SimulatedDHWHController sc = new SimulatedDHWHController();
			sc.SetTime(getDate(0, 0, 3));

			long startDate = getDate(1, 0, 0);
			sc.SetTime(startDate);
			sc.setRelaisPowerState(true);

			sc.SetTime(getDate(0, 1, 0));

			ImpulsCounterInformation ici = sc.getImpulsCounterInformation().get(0);
			assertTrue(ici.countingStart.getTime() == startDate);
			assertTrue(ici.impulsOccurences.size() == 2175);

		} catch (Exception e) {
			fail();
		}
	}

	// Helper method to make a time step.
	private long getDate(int days, int hours, int min) {
		_currentTime.add(Calendar.DAY_OF_MONTH, days);
		_currentTime.add(Calendar.HOUR, hours);
		_currentTime.add(Calendar.MINUTE, min);
		return _currentTime.getTimeInMillis();
	}

}
