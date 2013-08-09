package at.fhv.smartgrid.rasbpi.internal.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * pi4j requires wiringPi see https://projects.drogon.net/raspberry-pi/wiringpi/download-and-install/
 * 
 * +----------+-Rev2-+------+--------+------+-------+
 * | wiringPi | GPIO | Phys | Name   | Mode | Value |
 * +----------+------+------+--------+------+-------+
 * |      0   |  17  |  11  | GPIO 0 | OUT  | Low   | <-- GPIO17 on Gertboard
 * |      1   |  18  |  12  | GPIO 1 | OUT  | High  | <-- GPIO18 on Gertboard
 * |      2   |  27  |  13  | GPIO 2 | IN   | Low   |
 * |      3   |  22  |  15  | GPIO 3 | IN   | Low   |
 * |      4   |  23  |  16  | GPIO 4 | IN   | Low   |
 * |      5   |  24  |  18  | GPIO 5 | IN   | Low   |
 * |      6   |  25  |  22  | GPIO 6 | IN   | Low   |
 * |      7   |   4  |   7  | GPIO 7 | IN   | High  |
 * |      8   |   2  |   3  | SDA    | IN   | High  |
 * |      9   |   3  |   5  | SCL    | OUT  | Low   |
 * |     10   |   8  |  24  | CE0    | IN   | Low   |
 * |     11   |   7  |  26  | CE1    | IN   | Low   |
 * |     12   |  10  |  19  | MOSI   | IN   | Low   |
 * |     13   |   9  |  21  | MISO   | IN   | Low   |
 * |     14   |  11  |  23  | SCLK   | IN   | Low   |
 * |     15   |  14  |   8  | TxD    | ALT0 | High  |
 * |     16   |  15  |  10  | RxD    | OUT  | Low   |
 * |     17   |  28  |   3  | GPIO 8 | OUT  | Low   |
 * |     18   |  29  |   4  | GPIO 9 | OUT  | High  |
 * |     19   |  30  |   5  | GPIO10 | IN   | Low   |
 * |     20   |  31  |   6  | GPIO11 | IN   | Low   |
 * +----------+------+------+--------+------+-------+
 */
public class GpioControl {

	private static final GpioController gpio = GpioFactory.getInstance();
	private static final GpioPinDigitalOutput pin18 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Relais", PinState.LOW);
	
	public static void setRelaisPowerState(boolean newState) {
		gpio.setState(newState, pin18);
	}

	public static boolean getRelaisPowerState() {
		return pin18.getState().equals(true);
	}
	

}
