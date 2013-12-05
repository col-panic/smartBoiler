package at.fhv.smartdevices.simulatedDHWH;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import at.fhv.smartdevices.commons.*;
import at.fhv.smartgrid.rasbpi.internal.ImpulsCounterInformation;
import at.fhv.smartgrid.rasbpi.internal.MarketPriceAtom;
import at.fhv.smartgrid.rasbpi.internal.SensorInformation;
/**
 * @author kepe
 *
 */
public class SimulatedController implements ISimulatedSmartController {

	private long stateTimeStamp;
	private List<SensorInformation> sis;
	private float currentTemp;
	private List<ImpulsCounterInformation> icis;
	private List<MarketPriceAtom> cmp;
	private long cmpTimeStamp;
	private boolean relaisState;
	private float[] demands;
	private float[][] prices;
	private final String COUNTER_ID="impuls";
	
	public SimulatedController(long now)
	{
		stateTimeStamp=now;
		currentTemp=40;
		relaisState=false;
		cmp = new ArrayList<MarketPriceAtom>();
					
		try {
			demands=readFromFile();
			prices = readPricesFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SetSensorInformation();		
	}	

	

	@Override
	public List<SensorInformation> getSensorInformation() {
		
		return sis;
	}

	@Override
	public List<ImpulsCounterInformation> getImpulsCounterInformation() {
		return icis;
	}

	@Override
	public long getCurrentMarketPricesListTimestamp() {
		return cmpTimeStamp;
	}

	@Override
	public List<MarketPriceAtom> getCurrentMarketPrices() {
		return cmp;
	}

	@Override
	public void setRelaisPowerState(boolean newState) {
		relaisState=newState;
	}

	@Override
	public boolean getRelaisPowerState() {		
		return relaisState;
	}

	@Override
	public void SetTime(long now) {
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(stateTimeStamp);
		Calendar dateNow=Calendar.getInstance();
		dateNow.setTimeInMillis(now);
		
		double elapsedTime=(dateNow.getTimeInMillis()-startDate.getTimeInMillis())/1000.0;
		if(elapsedTime<=60)
		{
			//no detectable change!
			return;
		}
		else
		{	
			//simulate dhwh behavior
			double[][] result = simulateDHWH(dateNow,startDate);
						
			//set current temperature		
			currentTemp = (float) result[1][result[1].length-1];
			
			//set sensor information accordingly
			SetSensorInformation();			
			
			//set relais state
			relaisState = result[0][result[0].length-1]==1;			
			
			//Set impuls counter
			SetImpulsCounter(startDate, result[0]);
			
			//check for new price signal.			
			CheckForNewPriceSignal(now);			
			
			//set new time
			stateTimeStamp=now;
			}	
		}

	private void SetImpulsCounter(Calendar startDate, double[] discreteSwitch) {
				
		icis = new ArrayList<ImpulsCounterInformation>();
		ImpulsCounterInformation ic = new ImpulsCounterInformation(); 
		ic.impulsCounterId=COUNTER_ID;
		ic.countingStart=startDate.getTime();
		ic.impulsOccurences = new ArrayList<Long>();
		
		//in Wh
		float consumption=0;
		for (int seconds=0;seconds<discreteSwitch.length*60;seconds++)
		{
			int minute = (int) Math.floor(seconds/60.0);
			consumption+=discreteSwitch[minute]*2200.0/3600.0;
			if(consumption>1)
			{
				//System.out.println(minute+":"+seconds+"/"+consumption);
				ic.impulsOccurences.add((long) (seconds*1000));
				consumption-=1;
			}
		}
		icis.add(ic);
	}



	private void SetSensorInformation() {
		sis=new ArrayList<SensorInformation>();
		SensorInformation si=new SensorInformation();
		si.setSensorId("0");
		si.setSensorValue(currentTemp);
		sis.add(si);
	}

	private void CheckForNewPriceSignal(long now) {
		Calendar cnow = Calendar.getInstance();
		cnow.setTimeInMillis(now);			
		boolean pastTwelve = cnow.get(Calendar.HOUR_OF_DAY)>=12;
		boolean oneDayPast =cmpTimeStamp/(24*3600*1000)<=(now/(24*3600*1000))-1;
		//if new price signal is available
		if(pastTwelve && oneDayPast)
		{				
			int dayOfTheYear = cnow.get(Calendar.DAY_OF_YEAR);
			//since 1st day of the year is index 0 in prices, this indexing is right:
			float[] tomorrowsPrices=prices[dayOfTheYear];		
			
			
			cmp = new ArrayList<MarketPriceAtom>();
			
			long startTime = getNextDayStart(cnow);
			for (int i=0; i<24; i++)
			{
				MarketPriceAtom a = new MarketPriceAtom();
				a.validFrom = startTime + (i*60*60*1000);
				a.price = (int)Math.floor(tomorrowsPrices[i]*100);
				cmp.add(a);
			}
			cmpTimeStamp = now;			
		}		
	}


	private long getNextDayStart(Calendar cnow) {
		
		Calendar dayStart = Calendar.getInstance();
		dayStart.setTimeInMillis( cnow.getTimeInMillis());
		dayStart.set(Calendar.SECOND, 0);
		dayStart.set(Calendar.MINUTE, 0);
		dayStart.set(Calendar.HOUR, 0);
		dayStart.set(Calendar.AM_PM, Calendar.AM);
		return dayStart.getTimeInMillis()+24*60*60*1000;
	}



	private double[][] simulateDHWH(Calendar dateNow, Calendar startDate) {
		int minNow = getMinutesSinceYearStarted(dateNow);
		int minStartDate = getMinutesSinceYearStarted(startDate);
		int amount = minNow-minStartDate;
		
		double[] demand = new double[amount];
		byte[] u = new byte[amount];
		byte currentRelaisState = getCurrentRelaisStateAsByte();
		
		for (int minute=0;minute<amount;minute++)
		{
			demand[minute]=demands[minStartDate+minute];
			u[minute]=currentRelaisState;
		}
		
		double[][] result = SimulatedDHWH.simulateDHWH(u, currentTemp, demand);
		return result;
	}

	private byte getCurrentRelaisStateAsByte() {
		byte currentRelaisState=0;
		if(relaisState)
		{
			currentRelaisState=1;
		}
		return currentRelaisState;
	}

	private int getMinutesSinceYearStarted(Calendar date) {		
		Calendar c =  Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.HOUR_OF_DAY, 1);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.YEAR, date.get(Calendar.YEAR));		
		int minutesSinceStartOfYear = (int) Math.max(0, Math.floor((date.getTimeInMillis()-c.getTimeInMillis())/1000.0/60.0));
		return minutesSinceStartOfYear;
	}
	
	private float[] readFromFile() throws IOException {
		ArrayList<String> values= new ArrayList<String>();
		FileReader fr =  new FileReader("./simData/Deterministic1min_DHW.txt");  
		BufferedReader br = new BufferedReader(fr);
		String line;
		while( ( line=br.readLine()) != null )
	    {
			values.add(line);
	    }
		br.close();
		fr.close();
		
		float[] retVal = new float[values.size()];
		for (int i=0; i< values.size(); i++) {
			retVal[i] = Float.parseFloat(values.get(i));
		}
		return retVal;
	}
	private float[][] readPricesFromFile() throws IOException {
		ArrayList<String> values= new ArrayList<String>();
		FileReader fr =  new FileReader("./simData/prices2012.csv");  
		BufferedReader br = new BufferedReader(fr);
		String line;
		while( ( line=br.readLine()) != null )
	    {
			values.add(line);
	    }
		br.close();
		fr.close();
		
		float[][] retVal = new float[values.size()][24];
		for (int i=0; i< values.size(); i++) {
			String value = values.get(i);
			String[] valueSplit = value.split(";");
			for (int j=0;j<24;j++)
			{
				try{retVal[i][j] = Float.parseFloat(valueSplit[j]);}
				catch(Exception e)
				{
					//e.printStackTrace();
				}
			}
		}
		return retVal;
	}

}
