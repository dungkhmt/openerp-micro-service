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
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;

public class TotalCostVR implements IFunctionVR {

	private VarRoutesVR XR;
	private VRManager mgr;
	private ArcWeightsManager awm;

	private double value;
	private double[] costLeft;
	private double[] costRight;

	HashMap<Point, Integer> map;
	
	private HashMap<String, Double> vhCode2lowerCapacity;
	private HashMap<String, Double> vhCode2upperCapacity;
	private HashMap<Point, String> startPoint2vhCode;
	
	public TotalCostVR(VarRoutesVR XR, ArcWeightsManager awm,
			HashMap<String, Double> vhCode2lowerCapacity,
			HashMap<String, Double> vhCode2upperCapacity,
			HashMap<Point, String> startPoint2vhCode) {
		this.XR = XR;
		this.awm = awm;
		mgr = XR.getVRManager();
		this.vhCode2lowerCapacity = vhCode2lowerCapacity;
		this.vhCode2upperCapacity = vhCode2upperCapacity;
		this.startPoint2vhCode = startPoint2vhCode;
		post();
	}

	public TotalCostVR(VarRoutesVR XR, ArcWeightsManager awm) {
		this.XR = XR;
		this.awm = awm;
		mgr = XR.getVRManager();
		post();
	}

	private void post() {
		costLeft = new double[XR.getTotalNbPoints()];
		costRight = new double[XR.getTotalNbPoints()];
		ArrayList<Point> points = XR.getAllPoints();
		map = new HashMap<Point, Integer>();
		for (int i = 0; i < points.size(); i++) {
			map.put(points.get(i), i);
		}
		mgr.post(this);
	}

	private int getIndex(Point p) {
		return map.get(p);
	}

	private double getCostLeft(Point p) {
		return costLeft[getIndex(p)];
	}

	private double getCostRight(Point p) {
		return costRight[getIndex(p)];
	}

	private double getCost(Point p, Point q) {
		return awm.getWeight(p, q);
	}

	private void update(int k) {
		Point sp = XR.getStartingPointOfRoute(k);
		Point tp = XR.getTerminatingPointOfRoute(k);
		costRight[getIndex(sp)] = 0;
		for (Point u = sp; u != tp; u = XR.next(u)) {
			costRight[getIndex(XR.next(u))] = costRight[getIndex(u)]
					+ awm.getWeight(u, XR.next(u));
		}
		costLeft[getIndex(tp)] = 0;
		for (Point u = tp; u != sp; u = XR.prev(u)) {
			costLeft[getIndex(XR.prev(u))] = costLeft[getIndex(u)]
					+ awm.getWeight(u, XR.prev(u));
		}
	}

	private double calc(Point s, Point t) {
		if (XR.route(s) != XR.route(t)) {
			System.out.println(name() + "::calc(" + s + "," + t
					+ ") EXCEPTION, " + s + " and " + t
					+ " are not the the same route");
			mgr.exit(-1);
		}
		return (XR.isBefore(s, t)) ? getCostRight(t) - getCostRight(s)
				: getCostLeft(t) - getCostLeft(s);
	}

	// @Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}

	// @Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		value = 0;
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			update(i);
			value += getCostRight(XR.getTerminatingPointOfRoute(i));
		}
	}

	// @Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x));
		oldR.add(XR.oldRoute(y));
		for (int r : oldR) {
			value -= getCostRight(XR.getTerminatingPointOfRoute(r));
			update(r);
			value += getCostRight(XR.getTerminatingPointOfRoute(r));
		}
	}

	// @Override
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

	// @Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(x)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x1)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x1));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x1)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x1)));
		double oldY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(x1));
		update(XR.oldRoute(y));
		double newX = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x1)));
		double newY = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		double oldV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		update(XR.oldRoute(y));
		double newV = getCostRight(XR
				.getTerminatingPointOfRoute(XR.oldRoute(y)));
		value += newV - oldV;
	}

	// @Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x1)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x2)));
		update(XR.oldRoute(x1));
		update(XR.oldRoute(x2));
		double newX = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x1)));
		double newY = getCostRight(XR.getTerminatingPointOfRoute(XR
				.oldRoute(x2)));
		value += newX + newY - oldX - oldY;
	}

	// @Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1));
		oldR.add(XR.oldRoute(y1));
		oldR.add(XR.oldRoute(x2));
		oldR.add(XR.oldRoute(y2));
		for (int r : oldR) {
			value -= getCostRight(XR.getTerminatingPointOfRoute(r));
			update(r);
			value += getCostRight(XR.getTerminatingPointOfRoute(r));
		}
	}

	// @Override
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
			value -= getCostRight(XR.getTerminatingPointOfRoute(r));
			update(r);
			value += getCostRight(XR.getTerminatingPointOfRoute(r));
		}
	}

	// @Override
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
			value -= getCostRight(XR.getTerminatingPointOfRoute(r));
			update(r);
			value += getCostRight(XR.getTerminatingPointOfRoute(r));
		}
	}

	// @Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int r = XR.route(y);
		value -= getCostRight(XR.getTerminatingPointOfRoute(r));
		update(r);
		value += getCostRight(XR.getTerminatingPointOfRoute(r));
	}

	// @Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int r = XR.oldRoute(x);
		value -= getCostRight(XR.getTerminatingPointOfRoute(r));
		update(r);
		value += getCostRight(XR.getTerminatingPointOfRoute(r));
	}
	
	//@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int r = XR.route(y1);
		value -= getCostRight(XR.getTerminatingPointOfRoute(r));
		update(r);
		value += getCostRight(XR.getTerminatingPointOfRoute(r));
	}

	//@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		int r = XR.oldRoute(x1);
		value -= getCostRight(XR.getTerminatingPointOfRoute(r));
		update(r);
		value += getCostRight(XR.getTerminatingPointOfRoute(r));
	}

	// @Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int r = XR.oldRoute(x);
		value -= getCostRight(XR.getTerminatingPointOfRoute(r));
		update(r);
		value += getCostRight(XR.getTerminatingPointOfRoute(r));
		if (XR.oldRoute(x) != XR.oldRoute(z)) {
			r = XR.oldRoute(z);
			value -= getCostRight(XR.getTerminatingPointOfRoute(r));
			update(r);
			value += getCostRight(XR.getTerminatingPointOfRoute(r));
		}
	}

	// @Override
	public String name() {
		// TODO Auto-generated method stub
		return "TotalCostVR";
	}

	// @Override
	public double getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		if (!XR.checkPerformTwoOptMoveOneRoute(x, y)) {
			System.out.println(name()
					+ "::evaluateTwoOptMoveOneRoute, check failed");
			System.exit(-1);
		}
		// int idx = getIndex(x);
		// int idy = getIndex(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		return awm.getWeight(x, y) + awm.getWeight(nx, ny) + calc(y, nx)
				- (awm.getWeight(x, nx) + awm.getWeight(y, ny) + calc(nx, y));

	}

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])

	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		if (!XR.checkPerformTwoOptMoveOneRoute(x, y)) {
			System.out.println(name()
					+ "::propagateTwoOptMoveOneRoute, check failed");
			System.exit(-1);
		}
		int k = XR.oldRoute(x);
		value -= getCostRight(XR.getTerminatingPointOfRoute(k));
		update(k);
		value += getCostRight(XR.getTerminatingPointOfRoute(k));
	}

	// @Override
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOnePointMove(x, y)) {
			System.out.println(name() + ":: Error evaluateOnePointMove: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		int n = 1;
		Point[] xx = { x };
		Point[] yy = { y };
		double eval = 0;
		HashSet<Point> V = new HashSet<Point>();
		for (int i = 0; i < n; i++) {
			Point prev = XR.prev(xx[i]);
			while (V.contains(prev)) {
				prev = XR.prev(prev);
			}
			Point next = XR.next(xx[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			eval += getCost(prev, next) - getCost(prev, xx[i])
					- getCost(xx[i], next);
			V.add(xx[i]);
		}
		HashMap<Point, Point> M = new HashMap<Point, Point>();
		for (int i = 0; i < n; i++) {
			Point next = XR.next(yy[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			Point prev = yy[i];
			if (M.containsKey(yy[i])) {
				prev = M.get(yy[i]);
			}
			eval += getCost(prev, xx[i]) + getCost(xx[i], next)
					- getCost(prev, next);
			M.put(yy[i], xx[i]);
		}
		return eval;
	}

	// @Override
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x
					+ " " + y + "\n" + XR.toString());
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

	// @Override
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove1: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = getCostRight(x)
				+ getCost(x, y)
				+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(y))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		double newY = 0;
		if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
			newY = getCost(XR.getStartingPointOfRoute(XR.route(y)), XR.next(y));
		} else {
			newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.next(x)) + getCost(XR.next(x), XR.next(y));
		}
		newY += calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)));
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove2: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = getCost(XR.getStartingPointOfRoute(XR.route(x)),
				XR.next(XR.getStartingPointOfRoute(XR.route(y))))
				+ calc(XR.next(XR.getStartingPointOfRoute(XR.route(y))), y)
				+ getCost(y, x)
				+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		double newY = 0;
		if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
			newY = getCost(XR.getStartingPointOfRoute(XR.route(y)), XR.next(y));
		} else {
			newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.next(x)) + getCost(XR.next(x), XR.next(y));
		}
		newY += calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)));
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove3: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = getCostRight(x)
				+ getCost(x, y)
				+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(y))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		double newY = 0;
		if (XR.next(x) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.next(x));
			} else {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), XR.next(y))
						+ getCost(XR.next(y), XR.next(x));
			}
			newY += calc(XR.next(x),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(y)));
		} else {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.getTerminatingPointOfRoute(XR.route(y)));
			} else {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), XR.next(y))
						+ getCost(XR.next(y),
								XR.getTerminatingPointOfRoute(XR.route(y)));
			}
		}

		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove4: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = getCost(XR.getStartingPointOfRoute(XR.route(x)),
				XR.next(XR.getStartingPointOfRoute(XR.route(y))))
				+ calc(XR.next(XR.getStartingPointOfRoute(XR.route(y))), y)
				+ getCost(y, x)
				+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		double newY = 0;
		if (XR.next(x) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.next(x));
			} else {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), XR.next(y))
						+ getCost(XR.next(y), XR.next(x));
			}
			newY += calc(XR.next(x),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(y)));
		} else {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.getTerminatingPointOfRoute(XR.route(y)));
			} else {
				newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), XR.next(y))
						+ getCost(XR.next(y),
								XR.getTerminatingPointOfRoute(XR.route(y)));
			}
		}
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove5: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = 0;
		if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
			newX = getCostRight(x)
					+ getCost(x, XR.getTerminatingPointOfRoute(XR.route(x)));
		} else {
			newX = getCostRight(x)
					+ getCost(x, XR.next(y))
					+ calc(XR.next(y),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
		}
		double newY = 0;
		if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
			newY = getCostRight(y)
					+ getCost(y, XR.getTerminatingPointOfRoute(XR.route(y)));
		} else {
			newY = getCostRight(y)
					+ getCost(y, XR.next(x))
					+ calc(XR.next(x),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(y)));
		}
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove6: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = 0;
		if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
			newX = getCost(XR.getStartingPointOfRoute(XR.route(x)), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
		} else {
			newX = getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ getCost(XR.next(y), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
		}
		double newY = 0;
		if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
			newY = getCostRight(y)
					+ getCost(y, XR.getTerminatingPointOfRoute(XR.route(y)));
		} else {
			newY = getCostRight(y)
					+ getCost(y, XR.next(x))
					+ calc(XR.next(x),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(y)));
		}
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove7: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		return evaluateTwoOptMove6(y, x);
	}

	// @Override
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove8: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = 0;
		if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
			newX = getCost(XR.getStartingPointOfRoute(XR.route(x)), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
		} else {
			newX = getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ getCost(XR.next(y), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
		}
		double newY = 0;
		if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
			newY = getCost(XR.getStartingPointOfRoute(XR.route(y)), y)
					+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(y)));
		} else {
			newY = getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.next(x))
					+ getCost(XR.next(x), y)
					+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(y)));
		}
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove1: " + x1
					+ " " + x2 + " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x1)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = getCostRight(XR.prev(x1))
				+ getCost(XR.prev(x1), XR.next(x2))
				+ calc(XR.next(x2), XR.getTerminatingPointOfRoute(XR.route(x1)));
		double newY = getCostRight(y) + getCost(y, x1) + calc(x1, x2)
				+ getCost(x2, XR.next(y))
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)));
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove2: " + x1
					+ " " + x2 + " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		double oldX = getCostRight(XR.getTerminatingPointOfRoute(XR.route(x1)));
		double oldY = getCostRight(XR.getTerminatingPointOfRoute(XR.route(y)));
		double newX = getCostRight(XR.prev(x1))
				+ getCost(XR.prev(x1), XR.next(x2))
				+ calc(XR.next(x2), XR.getTerminatingPointOfRoute(XR.route(x1)));
		double newY = getCostRight(y) + getCost(y, x2) + calc(x2, x1)
				+ getCost(x1, XR.next(y))
				+ calc(XR.next(y), XR.getTerminatingPointOfRoute(XR.route(y)));
		return newX + newY - oldX - oldY;
	}

	// @Override
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove1: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double newV = getCostRight(x);
		newV += getCost(x, z) + calc(z, XR.next(y));
		newV += getCost(XR.next(y), XR.next(x)) + calc(XR.next(x), y);
		newV += getCost(y, XR.next(z))
				+ calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove2: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = getCostLeft(XR.next(z)) + d1 + getCost(XR.next(z), y);
		} else {
			d2 = getCost(XR.getStartingPointOfRoute(XR.route(x)), y);
		}
		double newV = d2;
		newV += calc(y, XR.next(x));
		newV += getCost(XR.next(x), XR.next(y)) + calc(XR.next(y), z);
		newV += getCost(z, x)
				+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));

	}

	// @Override
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove3: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double newV = getCostRight(x);
		newV += getCost(x, y) + calc(y, XR.next(x));
		newV += getCost(XR.next(x), z) + calc(z, XR.next(y));
		newV += getCost(XR.next(y), XR.next(z))
				+ calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove4: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = getCostLeft(XR.next(z)) + d1 + getCost(XR.next(z), XR.next(y));
		} else {
			d2 = getCost(XR.getStartingPointOfRoute(XR.route(x)), XR.next(y));
		}
		double newV = d2 + calc(XR.next(y), z);
		newV += getCost(z, XR.next(x)) + calc(XR.next(x), y);
		newV += getCost(y, x)
				+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove5: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double newV = getCostRight(x);
		newV += getCost(x, XR.next(y)) + calc(XR.next(y), z);
		newV += getCost(z, XR.next(x)) + calc(XR.next(x), y);
		newV += getCost(y, XR.next(z))
				+ calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove6: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = getCostLeft(XR.next(z)) + d1 + getCost(XR.next(z), y);
		} else {
			d2 = getCost(XR.getStartingPointOfRoute(XR.route(x)), y);
		}
		double newV = d2 + calc(y, XR.next(x));
		newV += getCost(XR.next(x), z) + calc(z, XR.next(y));
		newV += getCost(XR.next(y), x)
				+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove7: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double newV = getCostRight(x);
		newV += getCost(x, XR.next(y)) + calc(XR.next(y), z);
		newV += getCost(z, y) + calc(y, XR.next(x));
		newV += getCost(XR.next(x), XR.next(z))
				+ calc(XR.next(z), XR.getTerminatingPointOfRoute(XR.route(x)));

		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove8: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = getCostLeft(XR.next(z)) + d1 + getCost(XR.next(z), XR.next(x));
		} else {
			d2 = getCost(XR.getStartingPointOfRoute(XR.route(x)), XR.next(x));
		}
		double newV = d2 + calc(XR.next(x), y);
		newV += getCost(y, z) + calc(z, XR.next(y));
		newV += getCost(XR.next(y), x)
				+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
				+ getCost(XR.next(XR.getStartingPointOfRoute(XR.route(x))),
						XR.getTerminatingPointOfRoute(XR.route(x)));
		return newV - getCostRight(XR.getTerminatingPointOfRoute(XR.route(x)));
	}

	// @Override
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateCrossExchangeMove: "
					+ x1 + " " + y1 + " " + x2 + " " + y2 + "\n"
					+ XR.toString());
			System.exit(-1);
		}

		double oldX1 = calc(x1, XR.next(y1));
		double oldX2 = calc(x2, XR.next(y2));
		double newX1 = getCost(x1, XR.next(x2)) + calc(XR.next(x2), y2)
				+ getCost(y2, XR.next(y1));
		double newX2 = getCost(x2, XR.next(x1)) + calc(XR.next(x1), y1)
				+ getCost(y1, XR.next(y2));
		return newX1 + newX2 - oldX1 - oldX2;
	}

	// @Override
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x1, x2, y1, y2)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x1
					+ " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
			System.exit(-1);
		}
		int n = 2;
		Point[] xx = { x1, x2 };
		Point[] yy = { y1, y2 };
		double eval = 0;
		HashSet<Point> V = new HashSet<Point>();
		for (int i = 0; i < n; i++) {
			Point prev = XR.prev(xx[i]);
			while (V.contains(prev)) {
				prev = XR.prev(prev);
			}
			Point next = XR.next(xx[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			eval += getCost(prev, next) - getCost(prev, xx[i])
					- getCost(xx[i], next);
			V.add(xx[i]);
		}
		HashMap<Point, Point> M = new HashMap<Point, Point>();
		for (int i = 0; i < n; i++) {
			Point next = XR.next(yy[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			Point prev = yy[i];
			if (M.containsKey(yy[i])) {
				prev = M.get(yy[i]);
			}
			eval += getCost(prev, xx[i]) + getCost(xx[i], next)
					- getCost(prev, next);
			M.put(yy[i], xx[i]);
		}
		return eval;
	}

	// @Override
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreePointsMove(x1, x2, x3, y1, y2, y3)) {
			System.out.println(name() + ":: Error evaluateThreePointsMove: "
					+ x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3
					+ "\n" + XR.toString());
			System.exit(-1);
		}
		int n = 3;
		Point[] xx = { x1, x2, x3 };
		Point[] yy = { y1, y2, y3 };
		double eval = 0;
		HashSet<Point> V = new HashSet<Point>();
		for (int i = 0; i < n; i++) {
			Point prev = XR.prev(xx[i]);
			while (V.contains(prev)) {
				prev = XR.prev(prev);
			}
			Point next = XR.next(xx[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			eval += getCost(prev, next) - getCost(prev, xx[i])
					- getCost(xx[i], next);
			V.add(xx[i]);
		}
		HashMap<Point, Point> M = new HashMap<Point, Point>();
		for (int i = 0; i < n; i++) {
			Point next = XR.next(yy[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			Point prev = yy[i];
			if (M.containsKey(yy[i])) {
				prev = M.get(yy[i]);
			}
			eval += getCost(prev, xx[i]) + getCost(xx[i], next)
					- getCost(prev, next);
			M.put(yy[i], xx[i]);
		}
		return eval;
	}

	// @Override
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3,
			Point x4, Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4)) {
			System.out.println(name() + ":: Error evaluateFourPointsMove: "
					+ x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3
					+ " " + x4 + " " + y4 + "\n" + XR.toString());
			System.exit(-1);
		}
		int n = 4;
		Point[] xx = { x1, x2, x3, x4 };
		Point[] yy = { y1, y2, y3, y4 };
		double eval = 0;
		HashSet<Point> V = new HashSet<Point>();
		for (int i = 0; i < n; i++) {
			Point prev = XR.prev(xx[i]);
			while (V.contains(prev)) {
				prev = XR.prev(prev);
			}
			Point next = XR.next(xx[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			eval += getCost(prev, next) - getCost(prev, xx[i])
					- getCost(xx[i], next);
			V.add(xx[i]);
		}
		HashMap<Point, Point> M = new HashMap<Point, Point>();
		for (int i = 0; i < n; i++) {
			Point next = XR.next(yy[i]);
			while (V.contains(next)) {
				next = XR.next(next);
			}
			Point prev = yy[i];
			if (M.containsKey(yy[i])) {
				prev = M.get(yy[i]);
			}
			eval += getCost(prev, xx[i]) + getCost(xx[i], next)
					- getCost(prev, next);
			M.put(yy[i], xx[i]);
		}
		return eval;
	}

	// @Override
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddOnePoint(x, y)) {
			System.out.println(name() + ":: Error evaluateAddOnePoint: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		return getCost(y, x) + getCost(x, XR.next(y)) - getCost(y, XR.next(y));
	}

	// @Override
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveOnePoint(x)) {
			System.out.println(name() + ":: Error evaluate RemoveOnePoint: "
					+ x + "\n" + XR.toString());
			System.exit(-1);
		}
		return getCost(XR.prev(x), XR.next(x)) - getCost(XR.prev(x), x)
				- getCost(x, XR.next(x));
	}
	
	//@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddTwoPoints(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateAddTwoPoints: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		if(y1 == y2){
			return getCost(y1, x1) + getCost(x1, x2) + getCost(x2, XR.next(y1)) - getCost(y1, XR.next(y1));
		}
		return getCost(y1, x1) + getCost(x1, XR.next(y1)) - getCost(y1, XR.next(y1))
			+ getCost(y2, x2) + getCost(x2, XR.next(y2)) - getCost(y2, XR.next(y2));
	}
	
	//@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveTwoPoints(x1, x2)) {
			System.out.println(name() + ":: Error evaluate RemoveTwoPoints: " + x1 + " " + x2 + "\n" + XR.toString());
    		System.exit(-1);
		}
		if(x2 == XR.next(x1)){
			return getCost(XR.prev(x1), XR.next(x2)) - getCost(XR.prev(x1), x1)
					- getCost(x1, x2) - getCost(x2, XR.next(x2));
		}
		return getCost(XR.prev(x1), XR.next(x1)) - getCost(XR.prev(x1), x1) - getCost(x1, XR.next(x1))
				+ getCost(XR.prev(x2), XR.next(x2)) - getCost(XR.prev(x2), x2) - getCost(x2, XR.next(x2));
	}

	// @Override
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddRemovePoints(x, y, z)) {
			System.out.println(name() + ":: Error evaluate AddRemovePoints: "
					+ x + " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		double eval = 0;
		eval -= getCost(XR.prev(x), x) + getCost(x, XR.next(x));
		eval += getCost(XR.prev(x), XR.next(x));
		if (XR.prev(x) == z) {
			eval += getCost(XR.prev(x), y) + getCost(y, XR.next(x));
			eval -= getCost(XR.prev(x), XR.next(x));
		} else {
			eval += getCost(z, y) + getCost(y, XR.next(z));
			eval -= getCost(z, XR.next(z));
		}
		return eval;
	}

	// @Override
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
				costRight[getIndex(p)] = costLeft[getIndex(p)] = 0;
			}
		}
		for (int r : oldR) {
			if (r != Constants.NULL_POINT) {
				value -= getCostRight(XR.getTerminatingPointOfRoute(r));
				update(r);
				value += getCostRight(XR.getTerminatingPointOfRoute(r));
			}
		}
	}

	// @Override
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformKPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateKPointsMove: \n"
					+ XR.toString());
			System.exit(-1);
		}
		int n = x.size();
		double eval = 0;
		HashSet<Point> V = new HashSet<Point>();
		for (int i = 0; i < n; i++) {
			if (XR.route(x.get(i)) != Constants.NULL_POINT) {
				Point prev = XR.prev(x.get(i));
				while (V.contains(prev)) {
					prev = XR.prev(prev);
				}
				Point next = XR.next(x.get(i));
				while (V.contains(next)) {
					next = XR.next(next);
				}
				eval += getCost(prev, next) - getCost(prev, x.get(i))
						- getCost(x.get(i), next);
				V.add(x.get(i));
			}
		}
		HashMap<Point, Point> M = new HashMap<Point, Point>();
		for (int i = 0; i < n; i++) {
			if (y.get(i) != CBLSVR.NULL_POINT) {
				Point next = XR.next(y.get(i));
				while (V.contains(next)) {
					next = XR.next(next);
				}
				Point prev = y.get(i);
				if (M.containsKey(y.get(i))) {
					prev = M.get(y.get(i));
				}
				eval += getCost(prev, x.get(i)) + getCost(x.get(i), next)
						- getCost(prev, next);
				M.put(y.get(i), x.get(i));
			}
		}
		return eval;
	}

	public static void main(String[] avgr) {
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
		ArcWeightsManager awm = new ArcWeightsManager(XR.getAllPoints());
		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p.length; j++) {
				awm.setWeight(p[i], p[j], p[i].distance(p[j]));
			}
		}
		IFunctionVR f = new TotalCostVR(XR, awm);

		mgr.close();

		int iter = 0;
		double oldV = 0;
		double newV = 0;
		double delta = 0;
		while (iter < 10000) {
			System.out.println(iter++ + "\n" + XR + "\n");
			// for (int i = 0; i < N; i++) {
			oldV = f.getValue();
			// }
			// int x1 = rand.nextInt(N);
			// int x2 = rand.nextInt(N);
			// int y1 = rand.nextInt(N);
			// int y2 = rand.nextInt(N);
			// int x3 = rand.nextInt(N);
			// int y3 = rand.nextInt(N);
			// int x4 = rand.nextInt(N);
			// int y4 = rand.nextInt(N);
			// while (!XR.checkPerformFourPointsMove(p[x1], p[x2], p[x3], p[x4],
			// p[y1], p[y2], p[y3], p[y4])) {
			// x1 = rand.nextInt(N);
			// y1 = rand.nextInt(N);
			// x2 = rand.nextInt(N);
			// y2 = rand.nextInt(N);
			// x3 = rand.nextInt(N);
			// y3 = rand.nextInt(N);
			// x4 = rand.nextInt(N);
			// y4 = rand.nextInt(N);
			// }
			//
			// System.out.println(p[x1] + " " + p[y1] + " " );
			// //for (int i = 0; i < N; i++) {
			// delta = f.evaluateFourPointsMove(p[x1], p[x2], p[x3], p[x4],
			// p[y1], p[y2], p[y3], p[y4]);
			// //}
			// mgr.performFourPointsMove(p[x1], p[x2], p[x3], p[x4], p[y1],
			// p[y2], p[y3], p[y4]);
			// //for (int i = 0; i < N; i++) {
			// newV = f.getValue();
			// //}
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
			// for (int i = 0; i < N; i++) {
			delta = f.evaluateKPointsMove(x, y);
			// }
			mgr.performKPointsMove(x, y);
			// for (int i = 0; i < N; i++) {
			newV = f.getValue();
			// }
			System.out.println(XR);
			// for (int i = 0; i < N; i++) {
			if (Math.abs(oldV + delta - newV) > 1e-6) {
				System.out.println("WTFFFFFFFFFFFFFFFFFFF " + " " + oldV + " "
						+ delta + " " + newV);
				System.exit(-1);
			}
			// }
		}
		System.out.println("Okkkkkkkkkkkkkk");
	}

}
