/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 05/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.constraints.neq;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class Neq implements IConstraintVR {
	private IConstraintVR cstr;

	public Neq(double c, IFunctionVR f) {
		cstr = new NeqConstantFunction(c, f);
	}

	public Neq(IFunctionVR f, double c) {
		cstr = new NeqConstantFunction(c, f);
	}

	public Neq(IFunctionVR f1, IFunctionVR f2) {
		cstr = new NeqFunctionFunction(f1, f2);
	}

	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return cstr.getVRManager();
	}

	public void initPropagation() {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public int violations() {
		// TODO Auto-generated method stub
		return cstr.violations();
	}

	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateOnePointMove(x, y);
	}

	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoPointsMove(x, y);
	}

	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove1(x, y);
	}

	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove2(x, y);
	}

	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove3(x, y);
	}

	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove4(x, y);
	}

	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove5(x, y);
	}

	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove6(x, y);
	}

	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove7(x, y);
	}

	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoOptMove8(x, y);
	}

	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateOrOptMove1(x1, x2, y);
	}

	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateOrOptMove2(x1, x2, y);
	}

	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove1(x, y, z);
	}

	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove2(x, y, z);
	}

	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove3(x, y, z);
	}

	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove4(x, y, z);
	}

	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove5(x, y, z);
	}

	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove6(x, y, z);
	}

	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove7(x, y, z);
	}

	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreeOptMove8(x, y, z);
	}

	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return cstr.evaluateCrossExchangeMove(x1, y1, x2, y2);
	}

	public String name() {
		return "Neq";
	}

	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub

	}

	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub

	}

	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub

	}

	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub

	}
	
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("Neq::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("Neq::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return cstr.evaluateTwoPointsMove(x1, x2, y1, y2);
	}

	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		return cstr.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3);
	}

	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return cstr.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
	}

	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		return cstr.evaluateAddOnePoint(x, y);
	}

	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return cstr.evaluateRemoveOnePoint(x);
	}

	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return cstr.evaluateAddTwoPoints(x1, y1, x2, y2);
	}

	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return cstr.evaluateRemoveTwoPoints(x1, x2);
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return cstr.evaluateAddRemovePoints(x, y, z);
	}

	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub

	}

	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		return cstr.evaluateKPointsMove(x, y);
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}
}
