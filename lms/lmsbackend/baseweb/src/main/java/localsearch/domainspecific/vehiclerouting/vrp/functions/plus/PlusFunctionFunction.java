package localsearch.domainspecific.vehiclerouting.vrp.functions.plus;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class PlusFunctionFunction implements IFunctionVR {
	private IFunctionVR f1;
	private IFunctionVR f2;
	private double value;
	
	public PlusFunctionFunction(IFunctionVR f1, IFunctionVR f2){
		this.f1 = f1; this.f2 = f2;
	}
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return f1.getVRManager();
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		value = f1.getValue() + f2.getValue();
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
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
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
		System.out.println("PlusFunctionFunction::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("PlusFunctionFunction::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}
	
	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "PlusFunctionFunction";
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
		System.out.println(name()
				+ "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	@Override
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateOnePointMove(x, y) + f2.evaluateOnePointMove(x, y);
	}

	@Override
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoPointsMove(x, y) + f2.evaluateTwoPointsMove(x, y);
	}

	@Override
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove1(x, y) + f2.evaluateTwoOptMove1(x, y);
	}

	@Override
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove2(x, y) + f2.evaluateTwoOptMove2(x, y);
	}

	@Override
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove3(x, y) + f2.evaluateTwoOptMove3(x, y);
	}

	@Override
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove4(x, y) + f2.evaluateTwoOptMove4(x, y);
	}

	@Override
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove5(x, y) + f2.evaluateTwoOptMove5(x, y);
	}

	@Override
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove6(x, y) + f2.evaluateTwoOptMove6(x, y);
	}

	@Override
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove7(x, y) + f2.evaluateTwoOptMove7(x, y);
	}

	@Override
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoOptMove8(x, y) + f2.evaluateTwoOptMove8(x, y);
	}

	@Override
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateOrOptMove1(x1, x2, y) + f2.evaluateOrOptMove1(x1, x2, y);
	}

	@Override
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateOrOptMove2(x1, x2, y) + f2.evaluateOrOptMove2(x1, x2, y);
	}

	@Override
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove1(x, y, z) + f2.evaluateThreeOptMove1(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove2(x, y, z) + f2.evaluateThreeOptMove2(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove3(x, y, z) + f2.evaluateThreeOptMove3(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove4(x, y, z) + f2.evaluateThreeOptMove4(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove5(x, y, z) + f2.evaluateThreeOptMove5(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove6(x, y, z) + f2.evaluateThreeOptMove6(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove7(x, y, z) + f2.evaluateThreeOptMove7(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateThreeOptMove8(x, y, z) + f2.evaluateThreeOptMove8(x, y, z);
	}

	@Override
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		return f1.evaluateCrossExchangeMove(x1, y1, x2, y2) + f2.evaluateCrossExchangeMove(x1, y1, x2, y2);
	}

	@Override
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return f1.evaluateTwoPointsMove(x1, x2, y1, y2) + f2.evaluateTwoPointsMove(x1, x2, y1, y2);
	}

	@Override
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		return f1.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3) + f2.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3);
	}

	@Override
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return f1.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4) + f2.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
	}

	@Override
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		return f1.evaluateKPointsMove(x, y) + f2.evaluateKPointsMove(x, y);
	}

	@Override
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		return f1.evaluateAddOnePoint(x, y) + f2.evaluateAddOnePoint(x, y);
	}

	@Override
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return f1.evaluateRemoveOnePoint(x) + f2.evaluateRemoveOnePoint(x);
	}
	
	@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return f1.evaluateAddTwoPoints(x1, y1, x2, y2) + f2.evaluateAddTwoPoints(x1, y1, x2, y2);
	}
	
	@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return f1.evaluateRemoveTwoPoints(x1, x2) + f2.evaluateRemoveTwoPoints(x1, x2);
	}

	@Override
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f1.evaluateAddRemovePoints(x, y, z) + f2.evaluateAddRemovePoints(x, y, z);
	}

}
