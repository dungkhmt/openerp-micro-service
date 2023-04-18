/**
 * Copyright (c) 2015,
 *      Pham Quang Dung (dungkhmt@gmail.com),
 *      Nguyen Thanh Hoang (thnbk56@gmail.com),
 *	Nguyen Van Thanh (nvthanh1994@gmail.com),
 *	Le Kim Thu (thulek@gmail.com) 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 11/09/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class MaxVR implements IFunctionVR {

	private ArrayList<IFunctionVR> functions;
	private double value;
	
	public MaxVR(IFunctionVR[] f){
		functions = new ArrayList<IFunctionVR>();
		for(int i = 0; i < f.length; i++)
			functions.add(f[i]);
		
		functions.get(0).getVRManager().post(this);
	}
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return functions.get(0).getVRManager();
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	@Override
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateOnePointMove(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoPointsMove(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove1(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove2(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove3(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove4(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove5(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove6(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove7(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoOptMove8(x, y) 
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateOrOptMove1(x1, x2, y)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateOrOptMove2(x1, x2, y)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove1(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove2(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove3(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove4(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove5(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove6(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove7(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreeOptMove8(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateCrossExchangeMove(x1, y1, x2, y2)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		value = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			value = value > f.getValue() ? value : f.getValue();
		}
		//System.out.println(name() + "::initPropagation, value = " + value);
		//System.exit(-1);
	}

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
	}

	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public String name(){
		return "Max";
	}
	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	
	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	
	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	
	@Override
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateTwoPointsMove(x1, x2, y1, y2)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	@Override
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	@Override
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	@Override
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateAddOnePoint(x, y)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	@Override
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateRemoveOnePoint(x)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	
	@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateAddTwoPoints(x1, y1, x2, y2)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateRemoveTwoPoints(x1, x2)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	
	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	@Override
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateAddRemovePoints(x, y, z)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	
	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	
	@Override
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		double nMax = 1-CBLSVR.MAX_INT;
		for(IFunctionVR f : functions){
			double v = f.evaluateKPointsMove(x, y)
					+ f.getValue();
			nMax = nMax > v ? nMax : v;
		}
		return nMax - value;
	}
	
}
