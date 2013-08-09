package at.fhv.smartgrid.rasbpi.internal.onewire;

import java.util.List;

import at.fhv.smartgrid.rasbpi.internal.SensorInformation;

/**
 * We are not able to use OWFS for the w1 system, as these are different systems 
 * (see http://raspberrypi.stackexchange.com/questions/1835/can-i-use-a-one-wire-file-system-through-the-gpio)
 * we hence have to implement this manually :( 
 *
 */
public class LinuxOneWireSensorInformation {
	
	public static List<SensorInformation> getSensorInformationList() {
		return new OneWireDeviceDirectoryWalker().collectDevices();
	}

}
