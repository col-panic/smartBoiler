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
package com.joptimizer.solvers;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * H.v = -g, <br>
 * s.t H diagonal
 * 
 * @see "S.Boyd and L.Vandenberghe, Convex Optimization, p. 542"
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class DiagonalKKTSolver extends KKTSolver {

	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Returns two vectors v and w.
	 */
	@Override
	public double[][] solve() throws Exception {

		RealVector v = null;
		RealVector w = null;

		if (log.isDebugEnabled()) {
			log.debug("H: " + ArrayUtils.toString(H.getData()));
			log.debug("g: " + ArrayUtils.toString(g.toArray()));
		}

		v = new ArrayRealVector(g.getDimension());
		for (int i = 0; i < v.getDimension(); i++) {
			v.setEntry(i, -g.getEntry(i) / H.getEntry(i, i));
		}

		// solution checking
		if (this.checkKKTSolutionAccuracy
				&& !this.checkKKTSolutionAccuracy(v, w)) {
			log.error("KKT solution failed");
			throw new Exception("KKT solution failed");
		}

		double[][] ret = new double[2][];
		ret[0] = v.toArray();
		ret[1] = (w != null) ? w.toArray() : null;
		return ret;
	}
}
