package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class TotalRequestsNotServed implements IFunctionVR {

	private VarRoutesVR XR;
	private VRManager mgr;
	private int total;
	private int value;
	private ArrayList<Point> pickup;
	private ArrayList<Point> delivery;
	
	public TotalRequestsNotServed(VarRoutesVR XR, ArrayList<Point> pickup, ArrayList<Point> delivery) {
    	this.XR 		= XR;
    	this.total 		= pickup.size();
    	this.value 		= this.total;
    	this.pickup 	= pickup;
    	this.delivery 	= delivery;
		mgr 			= XR.getVRManager();
		post();
    }
	
	private void post() {
		mgr.post(this);
	}
	
	//@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}

	private void update(){
		this.value = this.total - XR.getNbClientPointsOnRoutes()/2;
	}
	//@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		update();
	}
	
	//@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub;
		update();
	}

	//@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public String name() {
		// TODO Auto-generated method stub
		return "TotalRequestsNotServed";
	}

	//@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	//@Override
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddOnePoint(x, y)) {
			System.out.println(name() + ":: Error evaluateAddOnePoint: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}

		for(int i = 0; i < pickup.size(); i++){
			if(pickup.get(i) == x){
				if(XR.contains(delivery.get(i)))
					return -1;
				else
					return 0;
			}
		}
		for(int i = 0; i < delivery.size(); i++){
			if(delivery.get(i) == x){
				if(XR.contains(pickup.get(i)))
					return -1;
				else
					return 0;
			}
		}
		return 0;
	}

	//@Override
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveOnePoint(x)) {
			System.out.println(name() + ":: Error evaluate RemoveOnePoint: " + x + "\n" + XR.toString());
    		System.exit(-1);
		}
		
		for(int i = 0; i < pickup.size(); i++){
			if(pickup.get(i) == x){
				if(XR.contains(delivery.get(i)))
					return 1;
				else
					return 0;
			}
		}
		for(int i = 0; i < delivery.size(); i++){
			if(delivery.get(i) == x){
				if(XR.contains(pickup.get(i)))
					return 1;
				else
					return 0;
			}
		}
		return 0;
	}
	
	//@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddTwoPoints(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateAddTwoPoints: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		
		int p = 0;
		int t = -1;
		int k = -1;
		for(int i = 0; i < pickup.size(); i++){
			if(pickup.get(i) == x1){
				if(XR.contains(delivery.get(i)))
					p--;
				t = i;
				break;
			}
		}
		
		for(int i = 0; i < pickup.size(); i++){
			if(pickup.get(i) == x2){
				if(XR.contains(delivery.get(i)))
					p--;
				k = i;
				break;
			}
		}
		if(t == -1){
			for(int i = 0; i < delivery.size(); i++){
				if(delivery.get(i) == x1){
					if(XR.contains(pickup.get(i)))
						p--;
					t = i;
					break;
				}
			}
		}
		if(k == -1){
			for(int i = 0; i < delivery.size(); i++){
				if(delivery.get(i) == x2){
					if(XR.contains(pickup.get(i)))
						p--;
					k = i;
					break;
				}
			}
		}
		if(p == 0 && k == t)
			p--;
		return p;
	}
	
	//@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveTwoPoints(x1, x2)) {
			System.out.println(name() + ":: Error evaluate RemoveTwoPoints: " + x1 + " " + x2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int p = 0;
		int t = -1;
		int k = -1;
		for(int i = 0; i < pickup.size(); i++){
			if(pickup.get(i) == x1){
				if(XR.contains(delivery.get(i)))
					p++;
				t = i;
				break;
			}
		}
		
		for(int i = 0; i < pickup.size(); i++){
			if(pickup.get(i) == x2){
				if(XR.contains(delivery.get(i)))
					p++;
				k = i;
				break;
			}
		}
		if(t == -1){
			for(int i = 0; i < delivery.size(); i++){
				if(delivery.get(i) == x1){
					if(XR.contains(pickup.get(i)))
						p++;
					t = i;
					break;
				}
			}
		}
		if(k == -1){
			for(int i = 0; i < delivery.size(); i++){
				if(delivery.get(i) == x2){
					if(XR.contains(pickup.get(i)))
						p++;
					k = i;
					break;
				}
			}
		}
		if(p == 0 && k == t)
			p++;
		return p;
	}

	//@Override
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddRemovePoints(x, y, z)) {
    		System.out.println(name() + ":: Error evaluate AddRemovePoints: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
    	}
		
		return 0;
	}

	//@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		update();
	}

	//@Override
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformKPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateKPointsMove: \n" + XR.toString());
    		System.exit(-1);
		}

		int k = 0;
		for(int ix = 0; ix < x.size(); ix++){
			if(XR.route(x.get(ix)) != Constants.NULL_POINT && y.get(ix) == CBLSVR.NULL_POINT){
				for(int i = 0; i < pickup.size(); i++){
					if(pickup.get(i) == x.get(ix)){
						if(XR.contains(delivery.get(i)))
							k++;
						break;
					}
				}
				for(int i = 0; i < delivery.size(); i++){
					if(delivery.get(i) == x.get(ix)){
						if(XR.contains(pickup.get(i)))
							k++;
						break;
					}
				}
			}
			else if(XR.route(x.get(ix)) == Constants.NULL_POINT && y.get(ix) != CBLSVR.NULL_POINT){
				for(int i = 0; i < pickup.size(); i++){
					if(pickup.get(i) == x.get(ix)){
						if(XR.contains(delivery.get(i)))
							k--;
						break;
					}
				}
				for(int i = 0; i < delivery.size(); i++){
					if(delivery.get(i) == x.get(ix)){
						if(XR.contains(pickup.get(i)))
							k--;
						else if(x.contains(pickup.get(i)))
							k--;
						break;
					}
				}
			}
		}
		return k;
	}
	
	public static void main(String[] avgr) {
		
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

}
