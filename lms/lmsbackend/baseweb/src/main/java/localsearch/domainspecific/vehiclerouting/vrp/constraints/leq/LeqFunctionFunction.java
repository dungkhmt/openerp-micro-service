package localsearch.domainspecific.vehiclerouting.vrp.constraints.leq;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class LeqFunctionFunction implements IConstraintVR {

	private IFunctionVR f1;
	private IFunctionVR f2;
	private int violations;
	
	public LeqFunctionFunction(IFunctionVR f1, IFunctionVR f2){
		// f1 <= f2
		this.f1 = f1; this.f2 = f2;
		f1.getVRManager().post(this);
	}
	
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return f1.getVRManager();
	}

	
	public void initPropagation() {
		// TODO Auto-generated method stub
		violations = f1.getValue() <= f2.getValue() ? 0 : (int)Math.ceil(f1.getValue() - f2.getValue());
	}

	
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public String name() {
		// TODO Auto-generated method stub
		return "LeqFunctionFunction";
	}

	
	public int violations() {
		// TODO Auto-generated method stub
		return violations;
	}

	
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateOnePointMove(x, y) + f1.getValue();
		double nf2 = f2.evaluateOnePointMove(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoPointsMove(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoPointsMove(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove1(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove1(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove2(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove2(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove3(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove3(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove4(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove4(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove5(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove5(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove6(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove6(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove7(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove7(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoOptMove8(x, y) + f1.getValue();
		double nf2 = f2.evaluateTwoOptMove8(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateOrOptMove1(x1, x2, y) + f1.getValue();
		double nf2 = f2.evaluateOrOptMove1(x1, x2, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateOrOptMove2(x1, x2, y) + f1.getValue();
		double nf2 = f2.evaluateOrOptMove2(x1, x2, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove1(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove1(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove2(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove2(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove3(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove3(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove4(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove4(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove5(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove5(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove6(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove6(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove7(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove7(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreeOptMove8(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateThreeOptMove8(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateCrossExchangeMove(x1, y1, x2, y2) + f1.getValue();
		double nf2 = f2.evaluateCrossExchangeMove(x1, y1, x2, y2) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateTwoPointsMove(x1, x2, y1, y2) + f1.getValue();
		double nf2 = f2.evaluateTwoPointsMove(x1, x2, y1, y2) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3) + f1.getValue();
		double nf2 = f2.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;

	}

	
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4) + f1.getValue();
		double nf2 = f2.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateKPointsMove(x, y) + f1.getValue();
		double nf2 = f2.evaluateKPointsMove(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateAddOnePoint(x, y) + f1.getValue();
		double nf2 = f2.evaluateAddOnePoint(x, y) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateRemoveOnePoint(x) + f1.getValue();
		double nf2 = f2.evaluateRemoveOnePoint(x) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateAddTwoPoints(x1, y1, x2, y2) + f1.getValue();
		double nf2 = f2.evaluateAddTwoPoints(x1, y1, x2, y2) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}

	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateRemoveTwoPoints(x1, x2) + f1.getValue();
		double nf2 = f2.evaluateRemoveTwoPoints(x1, x2) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}
	
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double nf1 = f1.evaluateAddRemovePoints(x, y, z) + f1.getValue();
		double nf2 = f2.evaluateAddRemovePoints(x, y, z) + f2.getValue();
		int nv = nf1 <= nf2 ? 0 : (int)Math.ceil(nf1-nf2);
		return nv - violations;
	}


	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

}
