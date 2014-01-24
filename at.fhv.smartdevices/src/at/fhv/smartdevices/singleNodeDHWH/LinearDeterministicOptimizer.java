/**
 * 
 */
package at.fhv.smartdevices.singleNodeDHWH;

import java.util.Arrays;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;


/**
 * @author kepe
 *
 */
public class LinearDeterministicOptimizer {

	static final double pEl = 2175.0d;// [W] el. power of the heater
	// private static final double tempIn=12;//[°C] temperature of inlet water;
	private static final double tempEnv = 20.0d;// [°C] outside temperature;
	private static final double tempMax = 90.0d;// [°C] max allowed temperature
												// in
												// water heater;
	private static final double tempMin = 38d;// [°C] lowest allowed temperature
	// in water heater;
	private static final double p1 = 1.19720d;// [W/K] heat conduction * area,
												// Water below Iso/2;
	private static final double p2 = 6.815e5d;// [J/K] thermal mass (m_i*c_i)
												// envelope (St,Iso,Bl);
	
	public static double[] optimize(double[][] futureDemand, double[] costs, double deltat, double temp0){
		double[] u_opt = solveOptimizationProblem(futureDemand, costs, deltat, temp0);
		return u_opt;
		
	}
		
		private static double[] solveOptimizationProblem(double[][] futureDemand, double[] costs, double deltat, double temp0) {

			int maxSteps = Math.min(futureDemand[0].length, costs.length);

			costs = Arrays.copyOf(costs, maxSteps);

			LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(costs, 0);

			double[][] A = new double[maxSteps][];
			double[][] A_neg = new double[maxSteps][];
			double[] a = new double[maxSteps];
			double[] b = new double[maxSteps];
			double lambda = Math.exp((-p1 * deltat) / (1000.0d * p2));
			for (int i = 0; i < maxSteps; i++) {
				double[] Ai = new double[maxSteps];
				double[] Ai_neg = new double[maxSteps];
				a[i] = tempMax - (temp0 * Math.pow(lambda, i + 1));
				b[i] = tempMin - (temp0 * Math.pow(lambda, i + 1));
				for (int j = 0; j < maxSteps; j++) {
					Ai[j] = (1 - lambda) * Math.pow(lambda, i - j) * pEl / p1;
					Ai_neg[j] = -Ai[j];
					double summand = ((1 - lambda) * Math.pow(lambda, i - j) * (tempEnv - (futureDemand[0][j] / p1)));
					a[i] += (j <= i) ? summand : 0;
					b[i] += (j <= i) ? summand : 0;
				}
				A[i] = Ai;
				A_neg[i] = Ai_neg;
			}

			// inequalities (polyhedral feasible set A.X<a && -Ax<-b )
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[maxSteps * 2 + maxSteps * 2];
			for (int l = 0; l < maxSteps; l++) {
				inequalities[maxSteps * 2 + l] = new LinearMultivariateRealFunction(A[l], -a[l]);
				inequalities[maxSteps * 3 + l] = new LinearMultivariateRealFunction(A_neg[l], b[l]);
				// x<1
				double[] oneEntryPos = new double[maxSteps];
				Arrays.fill(oneEntryPos, 0);
				oneEntryPos[l] = 1;
				inequalities[l] = new LinearMultivariateRealFunction(oneEntryPos, -1);
				// x>0
				double[] oneEntryNeg = new double[maxSteps];
				Arrays.fill(oneEntryNeg, 0);
				oneEntryNeg[l] = -1;
				inequalities[maxSteps + l] = new LinearMultivariateRealFunction(oneEntryNeg, 0);
			}

			// optimization problem
			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			or.setFi(inequalities);
			or.setToleranceFeas(1.E-9);
			or.setTolerance(1.E-9);

			// optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);

			try {
				int returnCode = opt.optimize();
				System.out.println("Optimization routine return code: " + returnCode);
				return opt.getOptimizationResponse().getSolution();
			} catch (Exception e) {
				System.out.println("Optimization routine failed: " + e.getMessage());
			}
			return null;
		}
}
