
/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 05/09/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.constraints.eq;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class EqConstantFunction implements IConstraintVR {

	private IFunctionVR f;
	private double c;
	private int violations;
	
	public EqConstantFunction(double c, IFunctionVR f){
		this.f = f;
		this.c = c;
		f.getVRManager().post(this);
	}
	
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return f.getVRManager();
	}

	
	public void initPropagation() {
		// TODO Auto-generated method stub
		violations = c == f.getValue() ? 0 : (int)Math.abs(c-f.getValue());
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

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub	
		initPropagation();
	}

	
	public int violations() {
		// TODO Auto-generated method stub
		return this.violations;
	}

	
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateOnePointMove(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoPointsMove(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove1(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove2(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove3(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove4(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove5(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove6(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove7(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoOptMove8(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateOrOptMove1(x1, x2, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateOrOptMove2(x1, x2, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove1(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove2(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove3(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove4(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove5(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove6(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove7(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreeOptMove8(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		double v = f.evaluateCrossExchangeMove(x1, y1, x2, y2) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}
	
	
	public String name(){
		return "EqConstantFunction";
	}

	public static void main(String[] args){
		
	}

	
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
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
	
	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		double v = f.evaluateTwoPointsMove(x1, x2, y1, y2) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2,
			Point y3) {
		// TODO Auto-generated method stub
		double v = f.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		double v = f.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		double v = f.evaluateAddOnePoint(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		double v = f.evaluateRemoveOnePoint(x) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		double v = f.evaluateAddTwoPoints(x1, y1, x2, y2) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		double v = f.evaluateRemoveTwoPoints(x1, x2) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double v = f.evaluateAddRemovePoints(x, y, z) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}

	
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		double v = f.evaluateKPointsMove(x, y) + f.getValue();
		int nv = c == v ? 0 : (int)Math.abs(c-v);
		return nv - violations;
	}


	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}
}
