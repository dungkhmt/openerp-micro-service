package localsearch.domainspecific.vehiclerouting.vrp.functions.minus;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class Minus implements IFunctionVR {
	private IFunctionVR f;
	
	public Minus(IFunctionVR f1, IFunctionVR f2){
		f = new MinusFunctionFunction(f1,f2);
	}
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return f.getVRManager();
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		// DO NOTHING
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
		// DO NOTHING
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}
	
	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("Minus::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("Minus::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Minus";
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return f.getValue();
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
		return f.evaluateOnePointMove(x, y);
	}

	@Override
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoPointsMove(x, y);
	}

	@Override
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove1(x, y);
	}

	@Override
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove2(x, y);
	}

	@Override
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove3(x, y);
	}

	@Override
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove4(x, y);
	}

	@Override
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove5(x, y);
	}

	@Override
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove6(x, y);
	}

	@Override
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove7(x, y);
	}

	@Override
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateTwoOptMove8(x, y);
	}

	@Override
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateOrOptMove1(x1, x2, y);
	}

	@Override
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateOrOptMove2(x1, x2, y);
	}

	@Override
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove1(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove2(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove3(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove4(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove5(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove6(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove7(x, y, z);
	}

	@Override
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateThreeOptMove8(x, y, z);
	}

	@Override
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		return f.evaluateCrossExchangeMove(x1, y1, x2, y2);
	}

	@Override
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return f.evaluateTwoPointsMove(x1, x2, y1, y2);
	}

	@Override
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		return f.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3);
	}

	@Override
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return f.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
	}

	@Override
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		return f.evaluateKPointsMove(x, y);
	}

	@Override
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		return f.evaluateAddOnePoint(x, y);
	}

	@Override
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return f.evaluateRemoveOnePoint(x);
	}
	
	@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return f.evaluateAddTwoPoints(x1, y1, x2, y2);
	}
	
	@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return f.evaluateRemoveTwoPoints(x1, x2);
	}

	@Override
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return f.evaluateAddRemovePoints(x, y, z);
	}

}
