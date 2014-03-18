package at.fhv.smartdevices.common;

public class SensorInformation {

	private String sensorId;
	private float sensorValue;
	
	public SensorInformation(String sensorId, float sensorValue) {
		this.sensorId = sensorId;
		this.sensorValue = sensorValue;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public float getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(float sensorValue) {
		this.sensorValue = sensorValue;
	}
}
