package at.fhv.smartgrid.rasbpi.internal.gpio;

import java.util.ArrayList;
import java.util.Date;

import at.fhv.smartgrid.rasbpi.internal.ImpulsCounterInformation;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GpioCountingImpulsListener implements GpioPinListenerDigital {

	private ImpulsCounterInformation icInfo;	

	public GpioCountingImpulsListener(ImpulsCounterInformation icInfo) {
		this.icInfo = icInfo;
		this.icInfo.impulsOccurences = new ArrayList<>();
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(
			GpioPinDigitalStateChangeEvent event) {
			if(event.getState() == PinState.HIGH) {
				System.out.println(GpioCountingImpulsListener.class.getName()+": "+event);
				long countTime = new Date().getTime()-icInfo.countingStart.getTime();
				this.icInfo.impulsOccurences.add(countTime);
			}
	}

	
	
}
