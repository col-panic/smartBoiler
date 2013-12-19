/**
 * 
 */
package at.fhv.smartdevices.commons.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import at.fhv.smartdevices.commons.InterpolationHelper;

/**
 * @author kepe
 *
 */
public class InterpolationHelperTests {

	@Test
	public void testLinearInterpolation(){
		double[] x0 = new double[]{0,1,2,3};
		double[] y0 = new double[]{0,1,2,3};
		double[] x1= new double[]{0,1,2,3,-0.5,0.5,1.5,2.5,3.5};
		double[] y1= InterpolationHelper.interpolateLinear(x0, y0, x1);
		assertArrayEquals(y1, x1, 0);
	}
	
}
