package at.fhv.smartdevices.common;

import java.util.Date;
import java.util.List;

public class ImpulsCounterInformation {

	
	public String impulsCounterId;
	public Date countingStart;
	public List<Long> impulsOccurences;
	
	public ImpulsCounterInformation() {
		countingStart = new Date();
	}
	
}
