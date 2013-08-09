package at.fhv.smartgrid;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LCDController {
	public static GpioPinDigitalOutput RSpin;
	public static GpioPinDigitalOutput ENpin;

	public class LCDCharaterMap {
		private String charList = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[~]^_`abcdefghijklmnopqrstuvwxyz{|}~~~~~ ";
		private int startCode = 33;
		private LCDCharater map[];
		private int mapSize = 0;

		LCDCharaterMap() {
			map = new LCDCharater[charList.length()];
			for (int i = 0; i < charList.length(); i++) {
				map[i] = new LCDCharater(charList.charAt(i), startCode + i);
				mapSize = i;
			}
			mapSize++;
			System.out.println("Map size = " + mapSize);
		}

		public int mapChar(char c) {
			for (int i = 0; i < mapSize; i++) {
				if (map[i].isCharacter(c)) {
					return map[i].getCode();
				}
			}
			return 0;
		}
	}

	public class LCDCharater {
		private int code;
		private char character;

		LCDCharater(char c, int val) {
			setCode(val);
			setCharacter(c);
		}

		private int setCode(int val) {
			code = val;
			return code;
		}

		private char setCharacter(char c) {
			character = c;
			return character;
		}

		public char getCharacter() {
			return character;
		}

		public int getCode() {
			return code;
		}

		public boolean isCharacter(char c) {
			if (getCharacter() == c) {
				return true;
			}
			return false;
		}
	}

	public static class PortExtender {
		public static final String DESCRIPTION = "MCP23017 GPIO Provider";
		public static final int DEFAULT_ADDRESS = 0x20;
		private static final int REGISTER_IODIR_A = 0x00;
		private static final int REGISTER_IODIR_B = 0x01;
		private static final int REGISTER_GPINTEN_A = 0x04;
		private static final int REGISTER_GPINTEN_B = 0x05;
		private static final int REGISTER_DEFVAL_A = 0x06;
		private static final int REGISTER_DEFVAL_B = 0x07;
		private static final int REGISTER_INTCON_A = 0x08;
		private static final int REGISTER_INTCON_B = 0x09;
		private static final int REGISTER_GPPU_A = 0x0C;
		private static final int REGISTER_GPPU_B = 0x0D;
		// private static final int REGISTER_INTF_A = 0x0E;
		// private static final int REGISTER_INTF_B = 0x0F;
		// private static final int REGISTER_INTCAP_A = 0x10;
		// private static final int REGISTER_INTCAP_B = 0x11;
		private static final int REGISTER_GPIO_A = 0x12;
		private static final int REGISTER_GPIO_B = 0x13;
		// private static final int GPIO_A_OFFSET = 0;
		// private static final int GPIO_B_OFFSET = 1000;
		private int currentStatesA = 0;
		private int currentStatesB = 0;
		private int currentDirectionA = 0;
		private int currentDirectionB = 0;
		private int currentPullupA = 0;
		private int currentPullupB = 0;
		private I2CBus bus;
		private I2CDevice device;

		public PortExtender(int busNumber, int address) throws IOException {
			// create I2C communications bus instance
			bus = I2CFactory.getInstance(busNumber);
			// create I2C device instance
			device = bus.getDevice(address);
			// set all default pins directions
			device.write(REGISTER_IODIR_A, (byte) currentDirectionA);
			device.write(REGISTER_IODIR_B, (byte) currentDirectionB);
			// set all default pin interrupts
			device.write(REGISTER_GPINTEN_A, (byte) currentDirectionA);
			device.write(REGISTER_GPINTEN_B, (byte) currentDirectionB);
			// set all default pin interrupt default values
			device.write(REGISTER_DEFVAL_A, (byte) 0x00);
			device.write(REGISTER_DEFVAL_B, (byte) 0x00);
			// set all default pin interrupt comparison behaviors
			device.write(REGISTER_INTCON_A, (byte) 0x00);
			device.write(REGISTER_INTCON_B, (byte) 0x00);
			// set all default pin states
			device.write(REGISTER_GPIO_A, (byte) currentStatesA);
			device.write(REGISTER_GPIO_B, (byte) currentStatesB);
			// set all default pin pull up resistors
			device.write(REGISTER_GPPU_A, (byte) currentPullupA);
			device.write(REGISTER_GPPU_B, (byte) currentPullupB);
		}

		void write_gpio_a(int a) {
			try {
				// System.out.println("Write data = " + a + " to GPIO Port A");
				device.write(REGISTER_GPIO_A, (byte) a);
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws InterruptedException,
			IOException {
		System.out.println("<--Pi4J--> LCD Controller ... started.");
		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();
		LCDController lc = new LCDController();
		RSpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "RegSelect",
				PinState.LOW);
		ENpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "Enter",
				PinState.LOW);
		System.out.println("--> GPIO state should be: ON");
		final PortExtender pe = new PortExtender(I2CBus.BUS_1, 0x20);
		lc.clear_screen(pe);
		lc.setup_screen(pe);
//		lc.setup_display(pe, true, true, true);
//		lc.scroll_message(
//				// "ABCDEFG",
//				"This is a very long message to see how my scrolling approach works on the LCD screen!!!!!",
//				pe);
//		Thread.sleep(5000);
//		lc.clear_screen(pe);
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and
		// scheduled tasks)
		gpio.shutdown();
	}

	public void execute_command(PortExtender pe, int value) {
		ENpin.setState(PinState.LOW);
		RSpin.setState(PinState.LOW);
		pe.write_gpio_a(value);
		ENpin.setState(PinState.HIGH);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ENpin.setState(PinState.LOW);
	}

	public void clear_screen(PortExtender pe) {
		execute_command(pe, 0x01);
	}

	public void setup_screen(PortExtender pe) {
		execute_command(pe, 0x30);
	}

	public void setup_display(PortExtender pe, boolean display, boolean cursor,
			boolean blink) {
		int reg = 8;
		if (display) {
			reg = reg + 4;
		}
		if (cursor) {
			reg = reg + 2;
		}
		if (blink) {
			reg = reg + 1;
		}
		execute_command(pe, reg);
	}

	public void write_to_screen(PortExtender pe, byte c) {
		ENpin.setState(PinState.LOW);
		RSpin.setState(PinState.HIGH);
		pe.write_gpio_a(c);
		ENpin.setState(PinState.HIGH);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ENpin.setState(PinState.LOW);
	}

	public void scroll_message(String inMsg, PortExtender pe) {
		System.out.println("entering scroll_message with msg = " + inMsg);
		byte msg[] = encode_message(inMsg);
		int scroll_count = inMsg.length() - 16;
		if (scroll_count < 1)
			scroll_count = 1;
		for (int i = 0; i < scroll_count; i++) {
			clear_screen(pe);
			for (int y = 0; y < 16; y++) {
				write_to_screen(pe, msg[i + y]);
			}
		}
	}

	public byte[] encode_message(String message) {
		byte encoded_string[] = new byte[message.length()];
		LCDCharaterMap map = new LCDCharaterMap();
		for (int i = 0; i < message.length(); i++) {
			encoded_string[i] = (byte) map.mapChar(message.charAt(i));
		}
		return encoded_string;
	}
}
