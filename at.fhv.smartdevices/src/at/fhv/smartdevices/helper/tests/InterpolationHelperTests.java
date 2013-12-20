/**
 * 
 */
package at.fhv.smartdevices.helper.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import at.fhv.smartdevices.helper.InterpolationHelper;

/**
 * @author kepe
 *
 */
public class InterpolationHelperTests {

	@Test
	public void testLinearInterpolation(){
		float[] x0 = new float[]{0,1,2,3};
		float[] y0 = new float[]{0,1,2,3};
		float[] x1= new float[]{0,1,2,3,-0.5f,0.5f,1.5f,2.5f,3.5f};
		float[] y1= InterpolationHelper.interpolateLinear(x0, y0, x1);
		assertArrayEquals(y1, x1, 0);
	}
	
}
