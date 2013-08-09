package at.fhv.smartgrid.rasbpi.internal.onewire;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;

import at.fhv.smartgrid.rasbpi.internal.SensorInformation;

public class OneWireDeviceDirectoryWalker extends DirectoryWalker<SensorInformation> {

	public static final String BASE_LOCATION = "/sys/bus/w1/devices";
	private static String DIRECTORY_PREFIX = "28-";

	private SensorInformation si;
	private Pattern pat = Pattern.compile("^.*t=([0-9]+)$");

	public OneWireDeviceDirectoryWalker() {
		super();
	}

	public List<SensorInformation> collectDevices() {
		List<SensorInformation> results = new ArrayList<>();
		File basePath = new File(BASE_LOCATION);
		try {
			walk(basePath, results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	@Override
	protected boolean handleDirectory(File directory, int depth,
			Collection<SensorInformation> results) throws IOException {
		if (directory.getAbsolutePath().equals(BASE_LOCATION))
			return true;
		if (directory.getName().startsWith(DIRECTORY_PREFIX)) {
			si = new SensorInformation();
			si.setSensorId(directory.getName());

			String value = FileUtils.readFileToString(new File(directory,
					"w1_slave"));
			si.setSensorValue(parseOneWireSensorSlaveFile(value.split("\n")[1]));
			results.add(si);
		}

		// we have the information already, do not process the subdirectory
		return false;
	}

	private float parseOneWireSensorSlaveFile(String value) {
		Matcher match = pat.matcher(value);
		Float ret = 0F;
		if(match.matches()) {
			 ret = Float.valueOf(match.group(1)) / 1000;
		} else {
			System.out.println("no match in "+value);
		}
		return ret;
	}

}
