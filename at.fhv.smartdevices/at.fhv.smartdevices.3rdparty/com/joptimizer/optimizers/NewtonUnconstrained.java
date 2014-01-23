/*
 * Copyright 2011-2013 JOptimizer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.joptimizer.optimizers;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.math.Mult;

import com.joptimizer.functions.StrictlyConvexMultivariateRealFunction;
import com.joptimizer.solvers.BasicKKTSolver;
import com.joptimizer.solvers.KKTSolver;

/**
 * @see "S.Boyd and L.Vandenberghe, Convex Optimization, p. 487"
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class NewtonUnconstrained extends OptimizationRequestHandler {

	private Algebra ALG = Algebra.DEFAULT;
	private DoubleFactory1D F1 = DoubleFactory1D.dense;
	private DoubleFactory2D F2 = DoubleFactory2D.dense; 
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public NewtonUnconstrained(boolean activateChain){
		if(activateChain){
			this.successor = new NewtonLEConstrainedFSP(true);
		}
	}
	
	public NewtonUnconstrained(){
		this(false);
	}

	@Override
	public int optimize() throws Exception {
		log.debug("optimize");
		OptimizationResponse response = new OptimizationResponse();

    // checking responsibility
		if (getA() != null || getFi() != null) {
			// forward to the chain
			return forwardOptimizationRequest();
		}
		if (getF0() instanceof StrictlyConvexMultivariateRealFunction) {
			// OK, it's my duty
		} else {
			throw new Exception("Unsolvable problem");
		}

		long tStart = System.currentTimeMillis();
		DoubleMatrix1D X0 = getInitialPoint();
		if (X0 == null) {
			X0 = F1.make(getDim());
		}
		if(log.isDebugEnabled()){
			log.debug("X0:  " + ArrayUtils.toString(X0.toArray()));
		}

		DoubleMatrix1D X = X0;
		double previousLambda = Double.NaN;
		int iteration = 0;
		while (true) {
			iteration++;
			double F0X = getF0(X);
			if(log.isDebugEnabled()){
				log.debug("iteration " + iteration);
				log.debug("X=" + ArrayUtils.toString(X.toArray()));
				log.debug("f(X)=" + F0X);
			}
			
			// custom exit condition
			if(checkCustomExitConditions(X)){
				response.setReturnCode(OptimizationResponse.SUCCESS);
				break;
			}
			
			DoubleMatrix1D gradX = getGradF0(X);
			DoubleMatrix2D hessX = getHessF0(X);

			// Newton step and decrement
			DoubleMatrix1D step = calculateNewtonStep(hessX, gradX); 
			//DoubleMatrix1D step = calculateNewtonStepCM(hessX, gradX);
			if(log.isDebugEnabled()){
				log.debug("step: " + ArrayUtils.toString(step.toArray()));
			}

			//Newton decrement
			double lambda = Math.sqrt(-ALG.mult(gradX, step));
			log.debug("lambda: " + lambda);
			if (lambda / 2. <= getTolerance()) {
				response.setReturnCode(OptimizationResponse.SUCCESS);
				break;
			}
			
			// iteration limit condition
			if (iteration == getMaxIteration()) {
				response.setReturnCode(OptimizationResponse.WARN);
				log.warn("Max iterations limit reached");
				break;
			}
			
			// progress conditions
			if(isCheckProgressConditions()){
				log.debug("previous: " + previousLambda);
				if (!Double.isNaN(previousLambda) && previousLambda <= lambda) {
					log.warn("No progress achieved, exit iterations loop without desired accuracy");
					response.setReturnCode(OptimizationResponse.WARN);
					break;
				} 
			}
			previousLambda = lambda;
			
			// backtracking line search
			double s = 1d;
			DoubleMatrix1D X1 = null;
			int cnt = 0;
			while (cnt < 25) {
              cnt++;
				// @TODO: can we use semplification 9.7.1 (Pre-computation for line searches)?
				X1 = X.copy().assign(step.copy().assign(Mult.mult(s)), Functions.plus);// x + t*step
				double condSX = getF0(X1);
				//NB: this will also check !Double.isNaN(getF0(X1))
				double condDX = F0X + getAlpha() * s * ALG.mult(gradX, step);
				if (condSX <= condDX) {
					break;
				}
				s = getBeta() * s;
			}
			log.debug("s: " + s);

			// update
			X = X1;
		}

		long tStop = System.currentTimeMillis();
		log.debug("time: " + (tStop - tStart));
		response.setSolution(X.toArray());
		setOptimizationResponse(response);
		return response.getReturnCode();
	}

  //@TODO: can we use semplification 9.7.2 ??
	//NB: the matrix hessX is square
	//Hess.step = -Grad
	private DoubleMatrix1D calculateNewtonStep(DoubleMatrix2D hessX, DoubleMatrix1D gradX) throws Exception {
		KKTSolver kktSolver = new BasicKKTSolver();
		if(isCheckKKTSolutionAccuracy()){
			kktSolver.setCheckKKTSolutionAccuracy(isCheckKKTSolutionAccuracy());
			kktSolver.setToleranceKKT(getToleranceKKT());
		}
		kktSolver.setHMatrix(hessX.toArray());
		kktSolver.setGVector(gradX.toArray());
		double[][] sol = kktSolver.solve();
		DoubleMatrix1D step = F1.make(sol[0]);
		return step;
	}
	
}
