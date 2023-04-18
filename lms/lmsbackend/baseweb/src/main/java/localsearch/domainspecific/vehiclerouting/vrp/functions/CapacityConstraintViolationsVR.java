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
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class CapacityConstraintViolationsVR implements IFunctionVR {
	
	private VRManager mgr;
	private VarRoutesVR XR;
	private NodeWeightsManager nwm;
	
	private double[] capacity;
	private double[] sumWeights;
	
	private double violations;
	
	private HashMap<Point, Integer> map;
	
	public CapacityConstraintViolationsVR(VarRoutesVR XR, NodeWeightsManager nwm, double capacity) {
		this.XR = XR;
		this.nwm = nwm;
		this.capacity = new double[XR.getNbRoutes() + 1];
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			this.capacity[i] = capacity;
		}
		post();
	}
	
	private void post() {
		mgr = XR.getVRManager();
		sumWeights = new double[XR.getTotalNbPoints()];
		ArrayList<Point> points = XR.getAllPoints();
		map = new HashMap<Point, Integer>();
		for (int i = 0; i < points.size(); i++) {
			map.put(points.get(i), i);
		}
		mgr.post(this);
	}
	
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}

	private double getViolations(double s, int k) {
        return (s < capacity[k] || CBLSVR.equal(s, capacity[k])) ? 0 : s - capacity[k];
    }
	
	private int getIndex(Point p) {
		return map.get(p);
	}
	
	private double getWeights(Point p){
		return nwm.getWeight(p);
	}
	
	private double getSumWeights(Point p){
		return sumWeights[getIndex(p)];
	}
	
	public VarRoutesVR getVarRoutesVR(){
		return this.XR;
	}
	
	private void update(int k) {
    	Point sp = XR.getStartingPointOfRoute(k);
        Point tp = XR.getTerminatingPointOfRoute(k);
        sumWeights[getIndex(sp)] = nwm.getWeight(sp);
        for (Point u = sp; u != tp; u = XR.next(u)){
        	sumWeights[getIndex(XR.next(u))] = sumWeights[getIndex(u)] + nwm.getWeight(XR.next(u));
        }
    }
	
	
	public void initPropagation() {
		// TODO Auto-generated method stub
		violations = 0;
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			update(i);
			violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(i)), i);
		}
	}

	private double calc(Point s, Point t) {
     	if (XR.route(s) != XR.route(t)){
    		System.out.println(name() + "::calc(" + s + "," + t + ") EXCEPTION, " + s + " and " + t + " are not the the same route");
    		mgr.exit(-1);
    	}
        return XR.isBefore(s, t) ? getSumWeights(t) - getSumWeights(s) + getWeights(s) 
        						 : getSumWeights(s) - getSumWeights(t) + getWeights(t);
    }
	
	
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x)); 
    	oldR.add(XR.oldRoute(y));
    	for (int r : oldR) {
    		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    		update(r);
    		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
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
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x))), XR.oldRoute(x));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x1))), XR.oldRoute(x1));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x1));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x1))), XR.oldRoute(x1));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x1))), XR.oldRoute(x1));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        update(XR.oldRoute(x1));
        update(XR.oldRoute(y));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x1))), XR.oldRoute(x1));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(y))), XR.oldRoute(y));
        violations += newX + newY - oldX - oldY;
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

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x1))), XR.oldRoute(x1));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x2))), XR.oldRoute(x2));
        update(XR.oldRoute(x1));
        update(XR.oldRoute(x2));
        double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x1))), XR.oldRoute(x1));
        double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.oldRoute(x2))), XR.oldRoute(x2));
        violations += newX + newY - oldX - oldY;
	}

	
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	for (int r : oldR) {
    		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    		update(r);
    		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    	}
	}

	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	oldR.add(XR.oldRoute(x3)); 
    	oldR.add(XR.oldRoute(y3));
    	for (int r : oldR) {
    		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    		update(r);
    		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    	}
	}

	
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
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
    		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    		update(r);
    		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
    	}
	}

	
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int r = XR.route(y);
		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
		update(r);
		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
	}

	
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int r = XR.oldRoute(x);
		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
		update(r);
		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
	}

	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int r = XR.route(y1);
		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
		update(r);
		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
	}

	
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		int r = XR.oldRoute(x1);
		violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
		update(r);
		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (XR.oldRoute(x) == XR.oldRoute(z)) {
			int r = XR.oldRoute(x);
			violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			update(r);
			violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
		} else {
			int r = XR.oldRoute(x);
			violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			update(r);
			violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			r = XR.oldRoute(z);
			violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			update(r);
			violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
		}
	}

	
	public String name() {
		// TODO Auto-generated method stub
		return "AccumulatedNodeWeightsOnPathVR";
	}

	
	public double getValue() {
		// TODO Auto-generated method stub
		return violations;
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
		if (!XR.checkPerformOnePointMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateOnePointMove: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
    	int n = 1;
    	Point[] xx = {x};
    	Point[] yy = {y};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(xx[i]));
			route.add(XR.route(yy[i]));
		}
		int eval = 0;
		for (int r : route) {
			eval -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			double s = getSumWeights(XR.getTerminatingPointOfRoute(r));
			for (int i = 0; i < n; i++) {
				if (XR.route(xx[i]) == r) {
					s -= getWeights(xx[i]);
				}
				if (XR.route(yy[i]) == r) {
					s += getWeights(xx[i]);
				}
			}
			eval += getViolations(s, r);
		}
		return eval;
	}

	
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
    	if (XR.next(x) == y) {
    		return evaluateTwoPointsMove(y, x, XR.prev(x), XR.prev(x));
    	} else if (XR.next(y) == x) {
    		return evaluateTwoPointsMove(x, y, XR.prev(y), XR.prev(y));
    	} else {
    		return evaluateTwoPointsMove(x, y, XR.prev(y), XR.prev(x));
    	}
	}

	
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove1: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) + getSumWeights(y) 
				- getWeights(XR.getStartingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getStartingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove2: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) + getSumWeights(y) 
				- getWeights(XR.getStartingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getStartingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove3: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) + getSumWeights(y) 
				- getWeights(XR.getStartingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getStartingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove4: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) + getSumWeights(y) 
				- getWeights(XR.getStartingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getStartingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove5: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = getSumWeights(y) 
				+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove6: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = getSumWeights(y) 
				+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove7: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = getSumWeights(y) 
				+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateTwoOptMove8: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(x) 
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
        double sy = getSumWeights(y) 
				+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
				- getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
				+ getWeights(XR.getTerminatingPointOfRoute(XR.route(y)));
        double newX = getViolations(sx, XR.route(x));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove1: " + x1 + " " + x2 + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x1))), XR.route(x1));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(XR.prev(x1)) + calc(XR.next(x2), XR.getTerminatingPointOfRoute(XR.route(x1)));
        double sy = getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))) + calc(x1, x2);
        double newX = getViolations(sx, XR.route(x1));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
	}

	
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove2: " + x1 + " " + x2 + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x1))), XR.route(x1));
        double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
        double sx = getSumWeights(XR.prev(x1)) + calc(XR.next(x2), XR.getTerminatingPointOfRoute(XR.route(x1)));
        double sy = getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))) + calc(x1, x2);
        double newX = getViolations(sx, XR.route(x1));
        double newY = getViolations(sy, XR.route(y));
		return newX + newY - oldX - oldY;
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
		if (!XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
    		System.out.println(name() + ":: Error evaluateCrossExchangeMove: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
    	}
    	
        double oldX1 = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x1))), XR.route(x1));
        double oldX2 = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x2))), XR.route(x2));
        double delta = calc(XR.next(x1), y1) - calc(XR.next(x2), y2);
        double newX1 = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x1))) - delta, XR.route(x1));
        double newX2 = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x2))) + delta, XR.route(x2));
        return newX1 + newX2 - oldX1 - oldX2;
	}

	
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x1, x2, y1, y2)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x1 + " " + x2 + " " + y1 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
    	
    	int n = 2;
    	Point[] xx = {x1, x2};
    	Point[] yy = {y1, y2};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(xx[i]));
			route.add(XR.route(yy[i]));
		}
		int eval = 0;
		for (int r : route) {
			eval -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			double s = getSumWeights(XR.getTerminatingPointOfRoute(r));
			for (int i = 0; i < n; i++) {
				if (XR.route(xx[i]) == r) {
					s -= getWeights(xx[i]);
				}
				if (XR.route(yy[i]) == r) {
					s += getWeights(xx[i]);
				}
			}
			eval += getViolations(s, r);
		}
		return eval;
	}

	
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreePointsMove(x1, x2, x3, y1, y2, y3)) {
			System.out.println(name() + ":: Error evaluateThreePointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + " " + x3 + " " + y3 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 3;
    	Point[] xx = {x1, x2, x3};
    	Point[] yy = {y1, y2, y3};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(xx[i]));
			route.add(XR.route(yy[i]));
		}
		int eval = 0;
		for (int r : route) {
			eval -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			double s = getSumWeights(XR.getTerminatingPointOfRoute(r));
			for (int i = 0; i < n; i++) {
				if (XR.route(xx[i]) == r) {
					s -= getWeights(xx[i]);
				}
				if (XR.route(yy[i]) == r) {
					s += getWeights(xx[i]);
				}
			}
			eval += getViolations(s, r);
		}
		return eval;
	}

	
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4)) {
			System.out.println(name() + ":: Error evaluateFourPointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + " " + x3 + " " + y3 + " " + x4 + " " + y4 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 4;
    	Point[] xx = {x1, x2, x3, x4};
    	Point[] yy = {y1, y2, y3, y4};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(xx[i]));
			route.add(XR.route(yy[i]));
		}
		int eval = 0;
		for (int r : route) {
			eval -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			double s = getSumWeights(XR.getTerminatingPointOfRoute(r));
			for (int i = 0; i < n; i++) {
				if (XR.route(xx[i]) == r) {
					s -= getWeights(xx[i]);
				}
				if (XR.route(yy[i]) == r) {
					s += getWeights(xx[i]);
				}
			}
			eval += getViolations(s, r);
		}
		return eval;
	}

	
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddOnePoint(x, y)) {
			System.out.println(name() + ":: Error evaluateAddOnePoint: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))), XR.route(y));
		double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y))) + getWeights(x), XR.route(y));
		return newY - oldY;
	}

	
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveOnePoint(x)) {
			System.out.println(name() + ":: Error evaluateRemoveOnePoint: " + x + "\n" + XR.toString());
    		System.exit(-1);
		}
		double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
		double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))) - getWeights(x), XR.route(x));
		return newY - oldY;
	}

	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddTwoPoints(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateAddTwoPoints: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y1))), XR.route(y1));
		double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(y1))) + getWeights(x1) + getWeights(x2), XR.route(y1));
		return newY - oldY;
	}
	
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveTwoPoints(x1, x2)) {
			System.out.println(name() + ":: Error evaluateRemoveTwoPoints: " + x1 + " " + x2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x1))), XR.route(x1));
		double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x1))) - getWeights(x1) - getWeights(x2), XR.route(x1));
		return newY - oldY;
	}
	
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddRemovePoints(x, y, z)) {
    		System.out.println(name() + ":: Error evaluateAddRemovePoints: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
    	}
		double eval = 0;
		if (XR.route(x) == XR.route(z)) {
			double oldV = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
			double newV = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))) - getWeights(x) + getWeights(y), XR.route(x));
			eval = newV - oldV;
		} else {
			double oldX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))), XR.route(x));
			double newX = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(x))) - getWeights(x), XR.route(x));
			double oldY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(z))), XR.route(z));
			double newY = getViolations(getSumWeights(XR.getTerminatingPointOfRoute(XR.route(z))) + getWeights(y), XR.route(z));
			eval = newY + newX - oldY - oldX;
		}
		
		return eval;
	}

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
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
				violations -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
	    		update(r);
	    		violations += getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			}
    	}
	}

	
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformKPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateKPointsMove: \n" + XR.toString());
    		System.exit(-1);
		}
		int n = x.size();
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			if (XR.route(x.get(i)) != Constants.NULL_POINT) {
				route.add(XR.route(x.get(i)));
			}
			if (y.get(i) != CBLSVR.NULL_POINT) {
				route.add(XR.route(y.get(i)));
			}
		}
		int eval = 0;
		for (int r : route) {
			eval -= getViolations(getSumWeights(XR.getTerminatingPointOfRoute(r)), r);
			double s = getSumWeights(XR.getTerminatingPointOfRoute(r));
			for (int i = 0; i < n; i++) {
				if (XR.route(x.get(i)) == r) {
					s -= getWeights(x.get(i));
				}
				if (y.get(i) != CBLSVR.NULL_POINT && XR.route(y.get(i)) == r) {
					s += getWeights(x.get(i));
				}
			}
			eval += getViolations(s, r);
		}
		return eval;
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
    		s += getViolations(sumWeights[getIndex(x)], k) + "\n";
    	}
    	return s;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int N = 60;
		int n = 50;
		int K = 5;
		Point[] p = new Point[N];
		SecureRandom rand = new SecureRandom();
		for (int i = 0; i < N; i++) {
			p[i] = new Point(i, rand.nextInt(50), rand.nextInt(50));
		}
		VRManager mgr = new VRManager();
		VarRoutesVR XR = new VarRoutesVR(mgr);
		for (int i = 0; i < n; i++) {
			XR.addClientPoint(p[i]);
		}
		for (int i = n; i < N - K; i++) {
			XR.addRoute(p[i], p[i + K]);
		}
		XR.initSequential();
		NodeWeightsManager nwm = new NodeWeightsManager(XR.getAllPoints());
		for (int i = 0; i < p.length; i++) {
			nwm.setWeight(p[i], rand.nextInt(10));
		}
		IFunctionVR f = new CapacityConstraintViolationsVR(XR, nwm, 50);
		
		mgr.close();
		mgr.performRemoveOnePoint(p[0]);
		
		int iter = 0;
		double oldV = 0;
		double newV = 0;
		double delta = 0;
		while (iter < 1000) {
			System.out.println(iter++ + "\n" + XR + "\n");
			//for (int i = 0; i < N; i++) {
				oldV = f.getValue();
			//}
//			int x1 = rand.nextInt(N);
//			int x2 = rand.nextInt(N);
//			int y1 = rand.nextInt(N);
//			int y2 = rand.nextInt(N);
//			int x3 = rand.nextInt(N);
//			int y3 = rand.nextInt(N);
//			int x4 = rand.nextInt(N);
//			int y4 = rand.nextInt(N);
//			while (!XR.checkPerformAddRemovePoints(p[x1], p[x2], p[x3])) {
//				x1 = rand.nextInt(N);
//				y1 = rand.nextInt(N);
//				x2 = rand.nextInt(N);
//				y2 = rand.nextInt(N);
//				x3 = rand.nextInt(N);
//				y3 = rand.nextInt(N);
//				x4 = rand.nextInt(N);
//				y4 = rand.nextInt(N);
//			}
//
//			System.out.println(p[x1] + " " + p[y1] + " " );
//			//for (int i = 0; i < N; i++) {
//				delta = f.evaluateAddRemovePoints(p[x1], p[x2], p[x3]);
//			//}
//			mgr.performAddRemovePoints(p[x1], p[x2], p[x3]);
//			//for (int i = 0; i < N; i++) {
//				newV = f.getValue();
//			//}
				ArrayList<Point> x = new ArrayList<Point>();
				ArrayList<Point> y = new ArrayList<Point>();
				int count = rand.nextInt(5) + 5;
				for (int i = 0; i < count; i++) {
					x.add(p[rand.nextInt(N)]);
					if (rand.nextInt(5) == 0) {
						y.add(CBLSVR.NULL_POINT);
					} else {
						y.add(p[rand.nextInt(N)]);
					}
				}
				while (!XR.checkPerformKPointsMove(x, y)) {
					x.clear();
					y.clear();
					count = rand.nextInt(5) + 5;
					for (int i = 0; i < count; i++) {
						x.add(p[rand.nextInt(N)]);
						if (rand.nextInt(5) == 0) {
							y.add(CBLSVR.NULL_POINT);
						} else {
							y.add(p[rand.nextInt(N)]);
						}
					}
				}
				System.out.println(count);
				for (int i = 0; i < count; i++) {
					System.out.println(x.get(i) + " " + y.get(i));
				}
				//for (int i = 0; i < N; i++) {
					delta = f.evaluateKPointsMove(x, y);
				//} 
				mgr.performKPointsMove(x, y);
				//for (int i = 0; i < N; i++) {
					newV = f.getValue();
				//}
			System.out.println(XR + " " + f);
			//for (int i = 0; i < N; i++) {
				if (Math.abs(oldV + delta - newV) > 1e-6) {
					System.out.println("WTFFFFFFFFFFFFFFFFFFF " + " "
							+ oldV + " " + delta + " " + newV);
					System.exit(-1);
				}
			//}
		}
		System.out.println("Okkkkkkkkkkkkkk");
	}

	

}
