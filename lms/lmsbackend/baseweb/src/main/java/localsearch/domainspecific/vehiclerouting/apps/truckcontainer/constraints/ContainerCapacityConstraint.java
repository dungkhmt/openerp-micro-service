package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.constraints;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;

public class ContainerCapacityConstraint implements IConstraintVR {
	private VarRoutesVR XR;
	private AccumulatedWeightNodesVR accContainerInvr;
	private int violations;
	
	public ContainerCapacityConstraint(VarRoutesVR XR, AccumulatedWeightNodesVR accContainerInvr){
		this.XR = XR;
		this.accContainerInvr = accContainerInvr;
		XR.getVRManager().post(this);
	}

	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		violations = 0;
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		violations = 0;
		int r = XR.route(y);
		for(Point p = XR.getStartingPointOfRoute(r); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
			int wY = (int)(accContainerInvr.getSumWeights(p));
			if(wY > 2)
				violations += wY - 2;
		}
	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		violations = 0;
	}

	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		violations = 0;
		int r = XR.route(y1);
		for(Point p = XR.getStartingPointOfRoute(r); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
			int wY = (int)(accContainerInvr.getSumWeights(p));
			if(wY > 2)
				violations += wY - 2;
		}
	}

	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		violations = 0;
	}

	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return violations;
	}

	@Override
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int vio = 0;

		for(Point p = y; p != XR.getTerminatingPointOfRoute(XR.route(y)); p = XR.next(p)){
			int wY = (int)(accContainerInvr.getSumWeights(p) 
					+ accContainerInvr.getWeights(x));
			if(wY > 2)
				vio += wY - 2;
		}
		
		return vio;
	}

	@Override
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int vio = 0;
		for(Point p = y1; p != XR.next(y2); p = XR.next(p)){
			int wY1 = (int)(accContainerInvr.getSumWeights(p) + accContainerInvr.getWeights(x1));
			if(wY1 > 2)
				vio += wY1 - 2;
		}
		for(Point p = y2; p != XR.getTerminatingPointOfRoute(XR.route(y1)); p = XR.next(p)){
			int wY2 = (int)(accContainerInvr.getSumWeights(p) 
					+ accContainerInvr.getWeights(x1)
					+ accContainerInvr.getWeights(x2));
			if(wY2 > 2)
				vio += wY2 - 2;
		}
		
		return vio;
	}

	@Override
	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}
}
