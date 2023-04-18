package localsearch.domainspecific.vehiclerouting.vrp.constraints;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class Implicate implements IConstraintVR {

	private IConstraintVR c1;
	private IConstraintVR c2;
	private int violations;
	
	public Implicate(IConstraintVR c1, IConstraintVR c2){
		this.c1 = c1; this.c2 = c2;
	}
	
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return c1.getVRManager();
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		if(c1.violations() > 0) violations = 0; else violations = c2.violations();
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

	//@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	
	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}	

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Implicate";
	}

	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return violations;
	}

	@Override
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateOnePointMove(x, y) + c1.violations();
		int v2 = c2.evaluateOnePointMove(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoPointsMove(x, y) + c1.violations();
		int v2 = c2.evaluateTwoPointsMove(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove1(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove1(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove2(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove2(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove3(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove3(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove4(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove4(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove5(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove5(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove6(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove6(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove7(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove7(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoOptMove8(x, y) + c1.violations();
		int v2 = c2.evaluateTwoOptMove8(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;

	}

	@Override
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateOrOptMove1(x1, x2, y) + c1.violations();
		int v2 = c2.evaluateOrOptMove1(x1, x2, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;

	}

	@Override
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateOrOptMove2(x1, x2, y) + c1.violations();
		int v2 = c2.evaluateOrOptMove2(x1, x2, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove1(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove1(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove2(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove2(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove3(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove3(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove4(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove4(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove5(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove5(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove6(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove6(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove7(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove7(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreeOptMove8(x, y, z) + c1.violations();
		int v2 = c2.evaluateThreeOptMove8(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateCrossExchangeMove(x1, y1, x2, y2) + c1.violations();
		int v2 = c2.evaluateCrossExchangeMove(x1, y1, x2, y2) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateTwoPointsMove(x1, x2, y1, y2) + c1.violations();
		int v2 = c2.evaluateTwoPointsMove(x1, x2, y1, y2) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3) + c1.violations();
		int v2 = c2.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4) + c1.violations();
		int v2 = c2.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateKPointsMove(x, y) + c1.violations();
		int v2 = c2.evaluateKPointsMove(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;

	}

	@Override
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateAddOnePoint(x, y) + c1.violations();
		int v2 = c2.evaluateAddOnePoint(x, y) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateRemoveOnePoint(x) + c1.violations();
		int v2 = c2.evaluateRemoveOnePoint(x) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}
	
	//@Override
	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateAddTwoPoints(x1, y1, x2, y2) + c1.violations();
		int v2 = c2.evaluateAddTwoPoints(x1, y1, x2, y2) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}
	
	//@Override
	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateRemoveTwoPoints(x1, x2) + c1.violations();
		int v2 = c2.evaluateRemoveTwoPoints(x1, x2) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int v1 = c1.evaluateAddRemovePoints(x, y, z) + c1.violations();
		int v2 = c2.evaluateAddRemovePoints(x, y, z) + c2.violations();
		int v = v1 > 0 ? 0 : v2;
		return v - violations;
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

}
