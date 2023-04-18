package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint;
import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.LeqConstantFunction;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class ScaleConstraint implements IConstraintVR{
	private IConstraintVR cstr;
	private int scale;
	public ScaleConstraint(IConstraintVR cstr,int scale) {
		// TODO Auto-generated constructor stub
		this.cstr = cstr;
		this.scale = scale;
	}
	
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return cstr.getVRManager();
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		// DO NOTHING
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
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return scale*cstr.violations();
	}

	@Override
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateOnePointMove(x, y);
	}

	@Override
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoPointsMove(x, y);
	}

	@Override
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove1(x, y);
	}

	@Override
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove2(x, y);
	}

	@Override
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove3(x, y);
	}

	@Override
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove4(x, y);
	}

	@Override
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove5(x, y);
	}

	@Override
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove6(x, y);
	}

	@Override
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove7(x, y);
	}

	@Override
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoOptMove8(x, y);
	}

	@Override
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateOrOptMove1(x1, x2, y);
	}

	@Override
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateOrOptMove2(x1, x2, y);
	}

	@Override
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove1(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove2(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove3(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove4(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove5(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove6(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove7(x, y, z);
	}

	@Override
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreeOptMove8(x, y, z);
	}

	@Override
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateCrossExchangeMove(x1, y1, x2, y2);
	}

	@Override
	public String name(){
		return "Leq";
	}
	
	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		
	}
	
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("ScaleConstraint::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("ScaleConstraint::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}
	
	@Override
	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateTwoPointsMove(x1, x2, y1, y2);
	}

	@Override
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3);
	}

	@Override
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
	}
	@Override
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateAddOnePoint(x, y);
	}
	@Override
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateRemoveOnePoint(x);
	}
	
	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateAddTwoPoints(x1, y1, x2, y2);
	}

	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateRemoveTwoPoints(x1, x2);
	}
	
	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateAddRemovePoints(x, y, z);
	}
	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		return scale*cstr.evaluateKPointsMove(x, y);
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

}
