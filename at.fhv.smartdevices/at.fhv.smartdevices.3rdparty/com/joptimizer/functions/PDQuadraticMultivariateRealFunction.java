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
package com.joptimizer.functions;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 * 1/2 * x.P.x + q.x + r,  
 * P positive definite
 * 
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class PDQuadraticMultivariateRealFunction extends PSDQuadraticMultivariateRealFunction implements StrictlyConvexMultivariateRealFunction{

	public PDQuadraticMultivariateRealFunction(double[][] PMatrix, double[] qVector, double r) {
		this(PMatrix, qVector, r, true);
	}
	
	public PDQuadraticMultivariateRealFunction(double[][] PMatrix, double[] qVector, double r, boolean checkPD) {
		super(PMatrix, qVector, r, false);
		if(checkPD){
			EigenvalueDecomposition eDecomp = new EigenvalueDecomposition(P);
			DoubleMatrix1D realEigenvalues= eDecomp.getRealEigenvalues();
			for(int i=0; i<realEigenvalues.size(); i++){
				if(realEigenvalues.get(i)<=0){
					throw new IllegalArgumentException("Not positive definite matrix");
				}
			}
		}
	}
}
