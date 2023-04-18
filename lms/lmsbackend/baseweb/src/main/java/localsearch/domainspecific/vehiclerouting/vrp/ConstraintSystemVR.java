

package localsearch.domainspecific.vehiclerouting.vrp;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 25/08/2015
 */
public class ConstraintSystemVR implements IConstraintVR {
	
	private ArrayList<IConstraintVR> _constraints;
	private int _violations;
	private VRManager _mgr;
	
	public ConstraintSystemVR(VRManager mgr){
		_constraints = new ArrayList<IConstraintVR>();
		this._mgr = mgr;
		mgr.postConstraintSystemVR(this);
	}
	public void post(IConstraintVR f){
		_constraints.add(f);
	}
	//@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return _mgr;
	}

	//@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	//@Override
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateOnePointMove(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoPointsMove(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove1(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove2(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove3(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove4(x, y);
		return eval;

	}

	//@Override
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove5(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove6(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoOptMove7(x, y);
		return eval;
	}

	//@Override
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) {
			eval += f.evaluateTwoOptMove8(x, y);
		}
		return eval;
	}

	//@Override
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateOrOptMove1(x1, x2, y);
		return eval;
	}

	//@Override
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateOrOptMove1(x1, x2, y);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove1(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove2(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove3(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove4(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove5(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove6(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove7(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreeOptMove8(x,y,z);
		return eval;
	}

	//@Override
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateCrossExchangeMove(x1,y1,x2,y2);
		return eval;
	}

	//@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		_violations = 0;
		for(IConstraintVR f : _constraints) _violations += f.violations();
		//System.out.println(name() + "::initPropagation, violations = " + _violations);
	}

	//@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	//@Override
	public String name(){
		return "ConstraintSystemVR";
	}

	//@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2,
			Point y3) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
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
	
	//@Override
	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateTwoPointsMove(x1, x2, y1, y2);
		return eval;
	}
	//@Override
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2,
			Point y3) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3);
		return eval;
	}
	//@Override
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
		return eval;
	}
	//@Override
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateAddOnePoint(x, y);
		return eval;
	}
	//@Override
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateRemoveOnePoint(x);
		return eval;
	}
	
	//@Override
	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateAddTwoPoints(x1, y1, x2, y2);
		return eval;
	}
	//@Override
	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) eval += f.evaluateRemoveTwoPoints(x1, x2);
		return eval;
	}
	
	//@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) {
			//System.out.println(f.name() + " " + f.evaluateAddRemovePoints(x, y, z));
			eval += f.evaluateAddRemovePoints(x, y, z);
		}
		return eval;
	}
	//@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		initPropagation();
	}
	//@Override
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for(IConstraintVR f : _constraints) {
			eval += f.evaluateKPointsMove(x, y);
		}
		return eval;
	}
	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}
}
