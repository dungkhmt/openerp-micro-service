package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;

public class AccumulatedNodeWeightsOnPathVR implements IFunctionVR {

	private Point v;
	private VarRoutesVR XR;
	private VRManager mgr;
	private AccumulatedWeightNodesVR accWN;
	
	public AccumulatedNodeWeightsOnPathVR(AccumulatedWeightNodesVR accWN, Point v){
		this.accWN = accWN;
		this.v = v;
		//System.out.println(name() + "::constructor, v = " + v.ID);
		post();
	}
	
	private void post() {
		mgr = accWN.getVRManager();
		this.XR = accWN.getVarRoutesVR();
		mgr.post(this);
	}
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
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

	////
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	////
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub

	}
	
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("AccumulatedNodeWeightsOnPathVR::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	////
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("AccumulatedNodeWeightsOnPathVR::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	////
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	////
	public String name() {
		// TODO Auto-generated method stub
		return "AccumulatedEdgeWeightsOnPathVR";
	}

	////
	public double getValue() {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::getValue, v = " + v.ID);
		return accWN.getSumWeights(v);
	}

	private double calc(Point s, Point t) {
     	if (XR.route(s) != XR.route(t)){
    		System.out.println(name() + "::calc(" + s + "," + t + ") EXCEPTION, " + s + " and " + t + " are not the the same route");
    		mgr.exit(-1);
    	}
        return XR.isBefore(s, t) ? accWN.getSumWeights(t) - accWN.getSumWeights(s) + accWN.getWeights(s) 
        						 : accWN.getSumWeights(s) - accWN.getSumWeights(t) + accWN.getWeights(t);
    }
	
	private void swap(Point[] a, int i, int j) {
		Point tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}
	
	private double evaluateMove(Point[] x, Point[] y, Point[] z) {
		boolean ok = false;
		for (int i = 0; i < z.length; i++) {
			if (z[i] == v) {
				for (int j = 0; j < x.length; j++) {
					if (x[j] == v) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					return 0;
				}
			}
		}

		for (int i = 0; i < x.length - 1; i++) {
			for (int j = x.length - 1; j > i; j--) {
				if (XR.isBefore(y[j], y[j - 1])) {
					swap(x, j, j - 1);
					swap(y, j, j - 1);
				}
			}
		}
		for (int i = 0; i < z.length - 1; i++) {
			for (int j = i + 1; j < z.length; j++) {
				if (XR.isBefore(z[j], z[i])) {
					swap(z, i, j);
				}
			}
		}
		int i = 0;
		int j = 0;
		double d = 0;
		while (i < x.length || j < z.length) {
			if (j == z.length || (i < x.length && XR.isBefore(y[i], z[j]))) {
				if ((!ok) && XR.route(v) == XR.route(y[i]) && (!XR.isBefore(y[i], v))) {
					return d;
				}
				d += accWN.getWeights(x[i]);
				if (v == x[i]) {
					return d + accWN.getSumWeights(y[i]) - accWN.getSumWeights(v);
				} 
				while (i < x.length - 1 && y[i] == y[i + 1]) {
					i++;
					d += accWN.getWeights(x[i]);
					if (v == x[i]) {
						return d + accWN.getSumWeights(y[i]) - accWN.getSumWeights(v);
					}
				}
				i++;
			} else {
				if ((!ok) && XR.isBefore(v, z[j])) {
					return d;
				}
				d -= accWN.getWeights(z[j]);
				while (j < z.length - 1 && XR.next(z[j]) == z[j + 1]) {
					d -= accWN.getWeights(z[j + 1]);
					j++;
				}
				j++;
			}
		}
		
		return d;
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	
	////
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOnePointMove(x, y)) {
			System.out.println(name() + ":: Error evaluateOnePointMove: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 1;
		Point[] X = {x};
		Point[] Y = {y};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(X[i]));
			route.add(XR.route(Y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(Y[i]) == r) {
					xx.add(X[i]);
					yy.add(Y[i]);
					ok |= X[i] == v;
				}
				if (XR.route(X[i]) == r) {
					zz.add(X[i]);
				}
			}
			if (ok || XR.route(v) == r) {
				Point[] XX = new Point[xx.size()];
				Point[] YY = new Point[yy.size()];
				Point[] ZZ = new Point[zz.size()];
				for (int i = 0; i < XX.length; i++) {
					XX[i] = xx.get(i);
					YY[i] = yy.get(i);
				}
				for (int i = 0; i < ZZ.length; i++) {
					ZZ[i] = zz.get(i);
				}
				eval += evaluateMove(XX, YY, ZZ);
			}
		}
		return eval;
	}

	////
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

	////
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove1: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWN.getSumWeights(x) + accWN.getSumWeights(y) 
						- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x))) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return 0;
		}
		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				return calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ calc(XR.next(y), v) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return accWN.getSumWeights(x) + calc(v, y) - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove2: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWN.getSumWeights(x) + accWN.getSumWeights(y) 
						- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x))) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return calc(x, v) + accWN.getSumWeights(y) 
					- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					- accWN.getSumWeights(v);
		}
		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				return calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ calc(XR.next(y), v) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x))) 
					- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)));
		}
		return 0;
	}

	////
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove3: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWN.getSumWeights(x) + accWN.getSumWeights(y) 
						- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						+ calc(XR.next(x), v)
						- accWN.getSumWeights(v);
			}
			return 0;
		}
		if (XR.route(y) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(y, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(y))) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return accWN.getSumWeights(x) + calc(v, y) - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove4: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWN.getSumWeights(x) + accWN.getSumWeights(y) 
						- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						+ calc(XR.next(x), v)
						- accWN.getSumWeights(v);
			}
			return calc(x, v) + accWN.getSumWeights(y) 
					- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					- accWN.getSumWeights(v);
		}
		if (XR.route(y) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(y, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(y))) 
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x))) 
					- accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)));
		}
		return 0;
	}

	////
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove5: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return accWN.getSumWeights(x) 
					+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWN.getSumWeights(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			return accWN.getSumWeights(y) 
					+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWN.getSumWeights(v);
		}
		if (XR.isBefore(x, v)) {
			return accWN.getSumWeights(y) + calc(XR.next(x), v) - accWN.getSumWeights(v);
		}
		if (XR.isBefore(y, v)) {
			return accWN.getSumWeights(x) + calc(XR.next(y), v) - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove6: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return accWN.getSumWeights(x) 
					+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWN.getSumWeights(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			return accWN.getSumWeights(y) 
					+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWN.getSumWeights(v);
		}
		if (XR.route(v) == XR.route(x)) {
			if (XR.isBefore(x, v)) {
				return accWN.getSumWeights(y) + calc(XR.next(x), v) - accWN.getSumWeights(v);
			}
			return calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					+ calc(x, v)
					- accWN.getSumWeights(v);
		}
		
		if (XR.isBefore(y, v)) {
			return calc(v, XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					- accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove7: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		return evaluateTwoOptMove6(y, x);
	}

	////
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove8: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return accWN.getSumWeights(x) 
					+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y))) 
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWN.getSumWeights(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			return accWN.getSumWeights(y) 
					+ calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x))) 
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					+ accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWN.getSumWeights(v);
		}
		if (XR.route(v) == XR.route(x)) {
			if (XR.isBefore(x, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
						- accWN.getSumWeights(v);
			}
			return calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					+ calc(x, v)
					- accWN.getSumWeights(v);
		}
		if (XR.route(v) == XR.route(y)) {
			if (XR.isBefore(y, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(y)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(y)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			return calc(XR.next(x), XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(y)))
					+ calc(y, v)
					- accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove1: " + x1 + " " + x2 + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v)) {
			if (XR.isBefore(x2, v)) {
				return accWN.getSumWeights(XR.prev(x1)) + calc(XR.next(x2), v) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(XR.prev(x1), v)) { 
				return accWN.getSumWeights(y) + calc(x1, v) - accWN.getSumWeights(v);
			}
			return 0;
		}
		if (XR.route(y) == XR.route(v) && XR.isBefore(y, v)){
			return calc(x1, x2);
		}
		return 0;
	}

	////
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove2: " + x1 + " " + x2 + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v)) {
			if (XR.isBefore(x2, v)) {
				return accWN.getSumWeights(XR.prev(x1)) + calc(XR.next(x2), v) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(XR.prev(x1), v)) { 
				return accWN.getSumWeights(y) + calc(x2, v) - accWN.getSumWeights(v);
			}
			return 0;
		}
		if (XR.route(y) == XR.route(v) && XR.isBefore(y, v)){
			return calc(x1, x2);
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove1: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(v, XR.next(z)) && XR.isBefore(y, v)) {
				return accWN.getSumWeights(x) + calc(v, z) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(v, XR.next(y)) && XR.isBefore(x, v)) {
				return accWN.getSumWeights(x) + calc(XR.next(y), z) + calc(XR.next(x), v) - accWN.getSumWeights(v);
			}
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove2: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
								- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(x), y) + calc(XR.next(y), v);
				return newV - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				double newV = accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
								- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(v, y);
				return newV - accWN.getSumWeights(v);
			}
			double newV = accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
							- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));;
			newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
			newV += calc(XR.next(x), y) + calc(XR.next(y), z) + calc(v, x);
			return newV - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove3: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return 0;
			}
			if (XR.isBefore(y, v)) {
				return accWN.getSumWeights(x) + calc(XR.next(x), y) + calc(v, z) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return accWN.getSumWeights(x) + calc(v, y) - accWN.getSumWeights(v);
			}
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove4: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = calc(XR.next(y), v)
								+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
								- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				return newV - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				double newV = calc(XR.next(y), z) + calc(XR.next(x), v);
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				return newV - accWN.getSumWeights(v);
			}
			double newV = calc(XR.next(y), z) + calc(XR.next(x), y) + calc(x, v);
			newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)))
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove5: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return 0;
			}
			if (XR.isBefore(y, v)) {
				return accWN.getSumWeights(x) + calc(XR.next(y), v) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return accWN.getSumWeights(x) + calc(XR.next(y), z) + calc(XR.next(x), v) - accWN.getSumWeights(v);
			}
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove6: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = calc(z, v) + calc(y, XR.next(x))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				return newV - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				double newV = calc(y, v) + accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				return newV - accWN.getSumWeights(v);
			}
			double newV = calc(XR.next(y), z) + calc(XR.next(x), y) + calc(x, v)
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
			newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove7: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return 0;
			}
			if (XR.isBefore(y, v)) {
				return accWN.getSumWeights(x) + calc(XR.next(y), v) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				return accWN.getSumWeights(x) + calc(XR.next(y), z) + calc(y, v) - accWN.getSumWeights(v);
			}
		}
		return 0;
	}

	////
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluate three opt move8: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x)) || v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return calc(v, XR.getTerminatingPointOfRoute(XR.route(x))) 
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWN.getSumWeights(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = calc(z, v) + calc(y, XR.next(x))
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				return newV - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x, v)) {
				double newV = calc(XR.next(x), v)
						+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
						- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));;
				newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
				return newV - accWN.getSumWeights(v);
			}
			double newV = calc(XR.next(y), z) + calc(XR.next(x), y) + calc(x, v)
					+ accWN.getWeights(XR.getStartingPointOfRoute(XR.route(x)))
					- accWN.getWeights(XR.getTerminatingPointOfRoute(XR.route(x)));
			newV += calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWN.getSumWeights(v);
		}
		return 0;
	}

	////
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateCrosExchangeMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v)) {
			if (XR.isBefore(y1, v)) {
				return accWN.getSumWeights(x1) + calc(XR.next(x2), y2) + calc(XR.next(y1), v) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x1, v)) {
				return accWN.getSumWeights(x2) + calc(XR.next(x1), v) - accWN.getSumWeights(v);
			}
		}
		if (XR.route(x2) == XR.route(v)) {
			if (XR.isBefore(y2, v)) {
				return accWN.getSumWeights(x2) + calc(XR.next(x1), y1) + calc(XR.next(y2), v) - accWN.getSumWeights(v);
			}
			if (XR.isBefore(x2, v)) {
				return accWN.getSumWeights(x1) + calc(XR.next(x2), v) - accWN.getSumWeights(v);
			}
		}
		return 0;
	}

	////
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x1, x2, y1, y2)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x1 + " " + x2 + " " + y1 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 2;
		Point[] X = {x1, x2};
		Point[] Y = {y1, y2};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(X[i]));
			route.add(XR.route(Y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(Y[i]) == r) {
					xx.add(X[i]);
					yy.add(Y[i]);
					ok |= X[i] == v;
				}
				if (XR.route(X[i]) == r) {
					zz.add(X[i]);
				}
			}
			if (ok || XR.route(v) == r) {
				Point[] XX = new Point[xx.size()];
				Point[] YY = new Point[yy.size()];
				Point[] ZZ = new Point[zz.size()];
				for (int i = 0; i < XX.length; i++) {
					XX[i] = xx.get(i);
					YY[i] = yy.get(i);
				}
				for (int i = 0; i < ZZ.length; i++) {
					ZZ[i] = zz.get(i);
				}
				eval += evaluateMove(XX, YY, ZZ);
			}
		}
		return eval;
	}

	////
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreePointsMove(x1, x2, x3, y1, y2, y3)) {
			System.out.println(name() + ":: Error evaluate ThreePointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + " " + x3 + " " + y3 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 3;
		Point[] X = {x1, x2, x3};
		Point[] Y = {y1, y2, y3};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(X[i]));
			route.add(XR.route(Y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(Y[i]) == r) {
					xx.add(X[i]);
					yy.add(Y[i]);
					ok |= X[i] == v;
				}
				if (XR.route(X[i]) == r) {
					zz.add(X[i]);
				}
			}
			if (ok || XR.route(v) == r) {
				Point[] XX = new Point[xx.size()];
				Point[] YY = new Point[yy.size()];
				Point[] ZZ = new Point[zz.size()];
				for (int i = 0; i < XX.length; i++) {
					XX[i] = xx.get(i);
					YY[i] = yy.get(i);
				}
				for (int i = 0; i < ZZ.length; i++) {
					ZZ[i] = zz.get(i);
				}
				eval += evaluateMove(XX, YY, ZZ);
			}
		}
		return eval;
	}

	////
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4)) {
			System.out.println(name() + ":: Error evaluate FourPointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2  + " " + x3 + " " + y3 + " " + x4 + " " + y4 + "\n" + XR.toString());
    		System.exit(-1);
		}
		int n = 4;
		Point[] X = {x1, x2, x3, x4};
		Point[] Y = {y1, y2, y3, y4};
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(X[i]));
			route.add(XR.route(Y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(Y[i]) == r) {
					xx.add(X[i]);
					yy.add(Y[i]);
					ok |= X[i] == v;
				}
				if (XR.route(X[i]) == r) {
					zz.add(X[i]);
				}
			}
			if (ok || XR.route(v) == r) {
				Point[] XX = new Point[xx.size()];
				Point[] YY = new Point[yy.size()];
				Point[] ZZ = new Point[zz.size()];
				for (int i = 0; i < XX.length; i++) {
					XX[i] = xx.get(i);
					YY[i] = yy.get(i);
				}
				for (int i = 0; i < ZZ.length; i++) {
					ZZ[i] = zz.get(i);
				}
				eval += evaluateMove(XX, YY, ZZ);
			}
		}
		return eval;
	}

	////
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddOnePoint(x, y)) {
			System.out.println(name() + ":: Error evaluateAddOnePoint: " + x + " " + y + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == x) {
			return accWN.getSumWeights(y) + accWN.getWeights(x);
		}
		if (XR.isBefore(y, v)) {
			return accWN.getWeights(x);
		}
		return 0;
	}

	////
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveOnePoint(x)) {
			System.out.println(name() + ":: Error evaluateRemoveOnePoint: " + x + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == x) {
			return -(accWN.getSumWeights(XR.prev(x)) + accWN.getWeights(x));
		}
		if (XR.isBefore(x, v)) {
			return -accWN.getWeights(x);
		}
		return 0;
	}
	
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddTwoPoints(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateAddTwoPoints: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == x1) {
			return accWN.getSumWeights(y1) + accWN.getWeights(x1);
		}
		if(v == x2){
			return accWN.getSumWeights(y2) + accWN.getWeights(x1) + accWN.getWeights(x2);
		}
		if (XR.isBefore(y1, v) && !XR.isBefore(y2, v)) {
			return accWN.getWeights(x1);
		}
		if(XR.isBefore(y2, v)){
			return accWN.getWeights(x1) + accWN.getWeights(x2);
		}
		return 0;
	}
	
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveTwoPoints(x1, x2)) {
			System.out.println(name() + ":: Error evaluateRemoveTwoPoints: " + x1 + " " + x2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		if (v == x1) {
			return -(accWN.getSumWeights(XR.prev(x1)) + accWN.getWeights(x1));
		}
		if(v == x2){
			return - accWN.getSumWeights(x2);
		}
		if (XR.isBefore(x1, v) && !XR.isBefore(x2, v)) {
			return -accWN.getWeights(x1);
		}
		if(XR.isBefore(x2, v)){
			return -accWN.getWeights(x1) - accWN.getWeights(x2);
		}
		return 0;
	}

	////
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddRemovePoints(x, y, z)) {
    		System.out.println(name() + ":: Error evaluate AddRemovePoints: " + x + " " + y + " " + z + "\n" + XR.toString());
    		System.exit(-1);
    	}
		if (XR.route(x) == XR.route(z)) {
			if (y != v && XR.route(x) != XR.route(v)) {
				return 0;
			}
			if (x == v) {
				return -accWN.getSumWeights(x);
			}
			Point[] X = {y};
			Point[] Y = {z};
			Point[] Z = {x};
			return evaluateMove(X, Y, Z);
		} else {
			if (XR.route(x) == XR.route(v)) {
				if (v == x) {
					return -(accWN.getSumWeights(XR.prev(x)) + accWN.getWeights(x));
				}
				if (XR.isBefore(x, v)) {
					return -accWN.getWeights(x);
				}
			}
			if (v == y) {
				return accWN.getSumWeights(z) + accWN.getWeights(y);
			}
			if (XR.isBefore(z, v)) {
				return accWN.getWeights(y);
			}
			
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

	////
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		
	}

	
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformKPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateKPointsMove:\n" + XR.toString());
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
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (y.get(i) != CBLSVR.NULL_POINT && XR.route(y.get(i)) == r) {
					xx.add(x.get(i));
					yy.add(y.get(i));
					ok |= x.get(i) == v;
				} else if (y.get(i) == CBLSVR.NULL_POINT){
					if (x.get(i) == v) {
						return -accWN.getSumWeights(v);
					}
				}
				if (XR.route(x.get(i)) == r) {
					zz.add(x.get(i));
				}
			}
			if (ok || XR.route(v) == r) {
				Point[] X = new Point[xx.size()];
				Point[] Y = new Point[yy.size()];
				Point[] Z = new Point[zz.size()];
				for (int i = 0; i < X.length; i++) {
					X[i] = xx.get(i);
					Y[i] = yy.get(i);
				}
				for (int i = 0; i < Z.length; i++) {
					Z[i] = zz.get(i);
				}
				eval += evaluateMove(X, Y, Z);
			}
		}
		return eval;
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
			nwm.setWeight(p[i], rand.nextInt(50));
		}
		AccumulatedWeightNodesVR accWN = new AccumulatedWeightNodesVR(XR, nwm);
		IFunctionVR[] f = new IFunctionVR[N];
		for (int i = 0; i < f.length; i++) {
			f[i] = new AccumulatedNodeWeightsOnPathVR(accWN, p[i]);
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
