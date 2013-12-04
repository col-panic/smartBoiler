/**
 * 
 */
package at.fhv.smartdevices.commons;

import java.util.HashMap;
import org.simpleframework.xml.*;

/**
 * @author kepe
 *
 */
@Root
public class ValueHistory<T> extends HashMap<Long,T>{
	
	@ElementMap(entry="valueHistory", key="timeStamp", attribute=true, inline=true)
	private HashMap<Long,T> _valueHistory;
	
	public ValueHistory(HashMap<Long,T> valueHistory)
	{
		_valueHistory = valueHistory;
	}	
	
	public void setValueHistory(HashMap<Long, T> _valueHistory) {
		this._valueHistory = _valueHistory;
	}

	public HashMap<Long, T> getValueHistory() {
		return _valueHistory;
	}		
}
