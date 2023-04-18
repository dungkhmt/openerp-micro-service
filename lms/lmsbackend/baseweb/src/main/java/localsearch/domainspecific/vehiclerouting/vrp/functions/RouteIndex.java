package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;

public class RouteIndex implements IFunctionVR {

	private Point v;
	private VarRoutesVR XR;
	private VRManager mgr;
	
	public RouteIndex(VarRoutesVR XR, Point v){
		// semantic: index of route containing point v
		this.XR = XR;
		this.v = v;
		post();
	}

	private void post() {
		mgr = XR.getVRManager();
		mgr.post(this);
	}
	
	
	public void initPropagation() {
		// TODO Auto-generated method stub
		
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

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		
	}

	
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		
	}

	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		
	}

	
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
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
		System.out.println("RouteIndex::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}
	
	public String name() {
		// TODO Auto-generated method stub
		return "RouteIndex";
	}

	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}

	
	public double getValue() {
		// TODO Auto-generated method stub
		return XR.route(v);
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
		if (x == v) {
			return XR.route(y) - XR.route(x);
		}
		return 0;
	}

	
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (x == v) {
			return XR.route(y) - XR.route(v);
		}
		if (y == v) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove1: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(v, XR.next(y)) && v != XR.getStartingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove2: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(v, XR.next(y)) && v != XR.getStartingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove3: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(v, XR.next(y)) && v != XR.getStartingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove4: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(v, XR.next(y)) && v != XR.getStartingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove5: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(y, v) && v != XR.getTerminatingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove6: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(y, v) && v != XR.getTerminatingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove7: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(y, v) && v != XR.getTerminatingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove8: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x, v) && v != XR.getTerminatingPointOfRoute(XR.route(x))) {
			return XR.route(y) - XR.route(v);
		}
		if (XR.isBefore(y, v) && v != XR.getTerminatingPointOfRoute(XR.route(y))) {
			return XR.route(x) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove1: " + x1 + " " + x2 + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v) && XR.index(x1) <= XR.index(v) && XR.index(v) <= XR.index(x2)) {
			return XR.route(y) - XR.route(v);
		}
		return 0;
	}

	
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove2: " + x1 + " " + x2 + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v) && XR.index(x1) <= XR.index(v) && XR.index(v) <= XR.index(x2)) {
			return XR.route(y) - XR.route(v);
		}
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

	
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateCrossExchangeMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.isBefore(x1, v) && XR.isBefore(v, XR.next(y1))) {
			return XR.route(x2) - XR.route(x1);
		}
		if (XR.isBefore(x2, v) && XR.isBefore(v, XR.next(y2))) {
			return XR.route(x1) - XR.route(x2);
		}
		return 0;
	}

	
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x1, x2, y1, y2)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 2;
		Point[] x = {x1, x2};
		Point[] y = {y1, y2};
		for (int i = 0; i < n; i++) {
			if (v == x[i]) {
				return XR.route(y[i]) - XR.route(v);
			}
		}
		return 0;
	}

	
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreePointsMove(x1, x2, x3, y1, y2, y3)) {
			System.out.println(name() + ":: Error evaluateThreePointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + " " + x3 + " " + y3 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 3;
		Point[] x = {x1, x2, x3};
		Point[] y = {y1, y2, y3};
		for (int i = 0; i < n; i++) {
			if (v == x[i]) {
				return XR.route(y[i]) - XR.route(v);
			}
		}
		return 0;
	}

	
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4)) {
			System.out.println(name() + ":: Error evaluateFourPointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + " " + x3 + " " + y3 + " " + x4 + " " + y4 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 4;
		Point[] x = {x1, x2, x3, x4};
		Point[] y = {y1, y2, y3, y4};
		for (int i = 0; i < n; i++) {
			if (v == x[i]) {
				return XR.route(y[i]) - XR.route(v);
			}
		}
		return 0;
	}
	
	
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddOnePoint(x, y)) {
			System.out.println(name() + ":: Error evaluateAddOnePoint: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (x == v) {
			return XR.route(y) - XR.route(x);
		}
		return 0;
	}
	
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveOnePoint(x)) {
			System.out.println(name() + ":: Error evaluateRemoveOnePoint: " + x + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (x == v) {
			return Constants.NULL_POINT - XR.route(x);
		}
		return 0;
	}

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
	}

	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddTwoPoints(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateAddTwoPoints: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (x1 == v) {
			return XR.route(y1) - XR.route(x1);
		}
		if (x2 == v) {
			return XR.route(y2) - XR.route(x2);
		}
		return 0;
	}
	
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveTwoPoints(x1, x2)) {
			System.out.println(name() + ":: Error evaluateRemoveTwoPoints: " + x1 + " " + x2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (x1 == v || x2 == v) {
			return Constants.NULL_POINT - XR.route(x1);
		}
		return 0;
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddRemovePoints(x, y, z)) {
    		System.out.println(name() + ":: Error evaluateAddRemovePoints: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
    	}
		if (v == x) {
			return Constants.NULL_POINT - XR.route(x);
		}
		if (v == y) {
			return XR.route(z) - XR.route(y);
		}
		return 0;
	}

	
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		
	}

	
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformKPointsMove(x, y)) {
    		System.out.println(name() + ":: Error evaluateKPointsMove: \n" + XR.toString());
    		System.exit(-1);
    	}
		for (int i = 0; i < x.size(); i++) {
			Point p = x.get(i);
			Point q = y.get(i);
			if (p == v) {
				if (q != CBLSVR.NULL_POINT) {
					return XR.route(q) - XR.route(p);
				} else {
					return Constants.NULL_POINT - XR.route(p);
				}
			}
		}
		return 0;
	}
	
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
			nwm.setWeight(p[i], rand.nextInt(50));
		}
		AccumulatedWeightNodesVR accWN = new AccumulatedWeightNodesVR(XR, nwm);
		IFunctionVR[] f = new IFunctionVR[N];
		for (int i = 0; i < f.length; i++) {
			f[i] = new RouteIndex(XR, p[i]);
		}
		
		mgr.close();
		mgr.performRemoveOnePoint(p[0]);
		
		int iter = 0;
		double[] oldV = new double[N];
		double[] newV = new double[N];
		double[] delta = new double[N];
		while (iter < 10000) {
			System.out.println(iter++ + "\n" + XR + "\n" + accWN);
			for (int i = 0; i < N; i++) {
				oldV[i] = f[i].getValue();
			}
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
//			System.out.println(x1 + " " + y1);
//			for (int i = 0; i < N; i++) {
//				delta[i] = f[i].evaluateAddRemovePoints(p[x1], p[x2], p[x3]);
//			}
//			mgr.performAddRemovePoints(p[x1], p[x2], p[x3]);
//			for (int i = 0; i < N; i++) {
//				newV[i] = f[i].getValue();
//			}
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
			for (int i = 0; i < N; i++) {
				delta[i] = f[i].evaluateKPointsMove(x, y);
			} 
			mgr.performKPointsMove(x, y);
			for (int i = 0; i < N; i++) {
				newV[i] = f[i].getValue();
			}
			System.out.println(XR + "\n" + accWN);
			for (int i = 0; i < N; i++) {
				if (Math.abs(oldV[i] + delta[i] - newV[i]) > 1e-6) {
					System.out.println("WTFFFFFFFFFFFFFFFFFFF " + i + " "
							+ oldV[i] + " " + delta[i] + " " + newV[i]);
					System.exit(-1);
				}
			}
		}
		System.out.println("Okkkkkkkkkkkkkk");
	}

	
}
