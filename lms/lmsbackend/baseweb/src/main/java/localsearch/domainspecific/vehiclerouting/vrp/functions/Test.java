package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class Test implements IFunctionVR {

	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initPropagation() {
		// TODO Auto-generated method stub

	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub

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

	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
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
		System.out.println("RouteIndex::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("RouteIndex::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("Test::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		return 0;
	}

	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("Test::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		return 0;
	}

	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

}
