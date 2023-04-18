
/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com), Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 21/10/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.invariants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class AccumulatedWeightNodesVR implements InvariantVR {

	protected VarRoutesVR XR;
	protected VRManager mgr;
	protected NodeWeightsManager nwm;
	
	protected double[] sumWeights;
	protected HashMap<Point, Integer> map;
	
	public AccumulatedWeightNodesVR(VarRoutesVR XR, NodeWeightsManager nwm){
		this.nwm = nwm;
		this.XR = XR;
		this.mgr = XR.getVRManager();
		post();
	}
	
	private void post(){
		sumWeights = new double[XR.getTotalNbPoints()];
		map = new HashMap<Point, Integer>();
		ArrayList<Point> points = XR.getAllPoints();
		for (int i = 0; i < points.size(); i++) {
			map.put(points.get(i), i);
		}
		for(int k= 1; k <= XR.getNbRoutes(); k++){
			Point p = XR.startPoint(k);
			sumWeights[getIndex(p)] = nwm.getWeight(p);
		}
		
		mgr.post(this);
	}
	
	protected int getIndex(Point p) {
		return map.get(p);
	}
	
	public double getWeights(Point p){
		return nwm.getWeight(p);
	}
	public double getSumWeights(Point p){
		return sumWeights[getIndex(p)];
	}
	public VarRoutesVR getVarRoutesVR(){
		return this.XR;
	}
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}
	
	public String name() {
		return "AccumulatedWeightNodesVR";
	}
	public void setAccumulatedWeightStartPoint(int k, double w){
		Point sp = XR.startPoint(k);
		nwm.setWeight(sp, w);		
		sumWeights[getIndex(sp)] = w;
	}
	// update sumWeight of points of route k
    protected void update(int k) {
    	//System.out.println(name() + "::update(" + k + ")");
    	Point sp = XR.getStartingPointOfRoute(k);
        Point tp = XR.getTerminatingPointOfRoute(k);
        //sumWeights[getIndex(sp)] = nwm.getWeight(sp);
        for (Point u = sp; u != tp; u = XR.next(u)){
        	sumWeights[getIndex(XR.next(u))] = sumWeights[getIndex(u)] + nwm.getWeight(XR.next(u));
        }
    }
    
	
	public void initPropagation() {
		// TODO Auto-generated method stub
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			update(i);
		}
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
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x)); 
    	oldR.add(XR.oldRoute(y));
    	for (int r : oldR) {
    		update(r);
    	}
	
	}

	
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (XR.next(x) == y) {
    		propagateTwoPointsMove(y, x, XR.prev(x), XR.prev(x));
    	} else if (XR.next(y) == x) {
    		propagateTwoPointsMove(x, y, XR.prev(y), XR.prev(y));
    	} else {
    		propagateTwoPointsMove(x, y, XR.prev(y), XR.prev(x));
    	}
	}

	
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}	

	
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
        update(XR.oldRoute(y));
	}

	
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x1));
        update(XR.oldRoute(y));
	}

	
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x1));
        update(XR.oldRoute(y));
	}

	
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x1));
		update(XR.oldRoute(x2));
	}
	
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	for (int r : oldR) {
    		update(r);
    	}
	}
	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	oldR.add(XR.oldRoute(x3)); 
    	oldR.add(XR.oldRoute(y3));
    	for (int r : oldR) {
    		update(r);
    	}
	}
	
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	oldR.add(XR.oldRoute(x3)); 
    	oldR.add(XR.oldRoute(y3));
    	oldR.add(XR.oldRoute(x4)); 
    	oldR.add(XR.oldRoute(y4));
    	for (int r : oldR) {
    		update(r);
    	}
	}
	
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.route(y));
	}

	
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
		sumWeights[getIndex(x)] = 0;
	}
	
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		update(XR.route(y1));
	}

	
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x1));
		sumWeights[getIndex(x1)] = sumWeights[getIndex(x2)] = 0;
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x));
		sumWeights[getIndex(x)] = 0;
		if (XR.oldRoute(x) != XR.oldRoute(z)) {
			update(XR.oldRoute(z));
		}
	}
	
	
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		for (int i = 0; i < x.size(); i++) {
			Point p = x.get(i);
			Point q = y.get(i);
			if (q != CBLSVR.NULL_POINT) {
				oldR.add(XR.oldRoute(p));
				oldR.add(XR.oldRoute(q));
			} else {
				oldR.add(XR.oldRoute(p));
				sumWeights[getIndex(p)] = 0;
			}
		}
		for (int r : oldR) {
			if (r != Constants.NULL_POINT) {
				update(r);
			}
    	}
	}
	
	public String toString() {
		String s = "";
    	for (int k = 1; k <= XR.getNbRoutes(); k++) {
    		s += "route[" + k + "] : ";
    		Point x = XR.getStartingPointOfRoute(k);
    		while (x != XR.getTerminatingPointOfRoute(k)) {
    			s += x.getID() + " (" + sumWeights[getIndex(x)] + ") ";
    			x = XR.next(x);
    		}
    		s += x.getID() + " (" + sumWeights[getIndex(x)] + ") ";
    		s += "\n";
    	}
    	return s;
	}
}
