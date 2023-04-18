/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com), Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 21/10/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.utils.ScannerInput;

public class AccumulatedEdgeWeightsOnPathVR implements IFunctionVR {
	private VarRoutesVR XR;
	private Point v;
	private AccumulatedWeightEdgesVR accWE;
	private VRManager mgr;

	public AccumulatedEdgeWeightsOnPathVR(AccumulatedWeightEdgesVR accWE,
			Point v) {
		this.accWE = accWE;
		this.v = v;
		post();
	}

	private void post() {
		mgr = accWE.getVRManager();
		this.XR = accWE.getVarRoutesVR();
		mgr.post(this);
	}

	//
	public void initPropagation() {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		// DO NOTHING, this was done by accWE before
	}

	//
	public String name() {
		// TODO Auto-generated method stub
		return "AccumulatedEdgeWeightsOnPathVR";
	}

	private double calc(Point s, Point t) {
		if (XR.route(s) != XR.route(t)) {
			System.out.println(name() + "::calc(" + s + "," + t
					+ ") EXCEPTION, " + s + " and " + t
					+ " are not the the same route");
			mgr.exit(-1);
		}
		// accWE.getCostRight(t] > accWE.getCostRight(s])
		return (XR.isBefore(s, t)) ? accWE.getCostRight(t)
				- accWE.getCostRight(s) : accWE.getCostLeft(t)
				- accWE.getCostLeft(s);
	}

	//
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}

	//
	public double getValue() {
		// TODO Auto-generated method stub
		return accWE.getCostRight(v);
	}

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name()
				+ "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	//
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOnePointMove(x, y)) {
			System.out.println(name() + ":: Error evaluateOnePointMove: " + x
					+ " " + y + "\n" + toString());
			System.exit(-1);
		}
		int n = 1;
		Point[] XX = { x };
		Point[] YY = { y };
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(XX[i]));
			route.add(XR.route(YY[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(YY[i]) == r) {
					xx.add(XX[i]);
					yy.add(YY[i]);
					ok |= XX[i] == v;
				}
				if (XR.route(XX[i]) == r) {
					zz.add(XX[i]);
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

	//
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

	//
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove1: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return accWE.getCostRight(x)
					+ accWE.getCost(x, y)
					+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWE.getCostRight(v);
		}

		if (XR.route(x) == XR.route(v) && XR.isBefore(x, v)) {
			return calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))), v)
					+ accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.next(y))
							+ calc(XR.next(y), v) - accWE.getCostRight(v);
				}
				return calc(
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
						XR.next(x))
						+ accWE.getCost(
								XR.getStartingPointOfRoute(XR.route(y)), XR
										.prev(XR.getTerminatingPointOfRoute(XR
												.route(x))))
						+ accWE.getCost(XR.next(x), XR.next(y))
						+ calc(XR.next(y), v) - accWE.getCostRight(v);
			}
			return accWE.getCostRight(x) + accWE.getCost(x, y) + calc(y, v)
					- accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove2: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}

		if (XR.route(x) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return calc(XR.next(XR.getStartingPointOfRoute(XR.route(y))), y)
						+ accWE.getCost(
								XR.getStartingPointOfRoute(XR.route(x)), XR
										.next(XR.getStartingPointOfRoute(XR
												.route(y))))
						+ accWE.getCost(y, x)
						+ calc(x, XR.next(XR.getStartingPointOfRoute(XR
								.route(x))))
						+ accWE.getCost(XR.next(XR.getStartingPointOfRoute(XR
								.route(x))), XR.getTerminatingPointOfRoute(XR
								.route(x))) - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				return calc(
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))), v)
						+ accWE.getCost(
								XR.getStartingPointOfRoute(XR.route(y)), XR
										.prev(XR.getTerminatingPointOfRoute(XR
												.route(x))))
						- accWE.getCostRight(v);
			}
			return calc(XR.next(XR.getStartingPointOfRoute(XR.route(y))), y)
					+ accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
							XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ accWE.getCost(y, x) + calc(x, v) - accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.next(y))
							+ calc(XR.next(y), v) - accWE.getCostRight(v);
				}
				return calc(
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
						XR.next(x))
						+ accWE.getCost(
								XR.getStartingPointOfRoute(XR.route(y)), XR
										.prev(XR.getTerminatingPointOfRoute(XR
												.route(x))))
						+ accWE.getCost(XR.next(x), XR.next(y))
						+ calc(XR.next(y), v) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					- accWE.getCostRight(XR.next(XR.getStartingPointOfRoute(XR
							.route(y))));
		}
		return 0;
	}

	//
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove3: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}

		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			return accWE.getCostRight(x)
					+ accWE.getCost(x, y)
					+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWE.getCostRight(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			if (XR.next(y) != XR.getTerminatingPointOfRoute(XR.route(y))) {
				if (XR.next(x) != XR.getTerminatingPointOfRoute(XR.route(x))) {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.prev(XR.getTerminatingPointOfRoute(XR
											.route(y))))
							+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
									.route(y))), XR.next(y))
							+ accWE.getCost(XR.next(y), XR.next(x))
							+ calc(XR.next(x), XR.prev(XR
									.getTerminatingPointOfRoute(XR.route(x))))
							+ accWE.getCost(XR.prev(XR
									.getTerminatingPointOfRoute(XR.route(x))),
									XR.getTerminatingPointOfRoute(XR.route(y)))
							- accWE.getCostRight(v);
				} else {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.prev(XR.getTerminatingPointOfRoute(XR
											.route(y))))
							+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
									.route(y))), XR.next(y))
							+ accWE.getCost(XR.next(y),
									XR.getTerminatingPointOfRoute(XR.route(y)))
							- accWE.getCostRight(v);
				}
			}
			if (XR.next(x) != XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.next(x))
						+ calc(XR.next(x), XR.prev(XR
								.getTerminatingPointOfRoute(XR.route(x))))
						+ accWE.getCost(XR.prev(XR
								.getTerminatingPointOfRoute(XR.route(x))), XR
								.getTerminatingPointOfRoute(XR.route(y)))
						- accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWE.getCostRight(v);
		}

		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), v) - accWE.getCostRight(v);
			}
			return accWE.getCostRight(x) + accWE.getCost(x, y) + calc(y, v)
					- accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v) && XR.isBefore(x, v)) {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.next(x))
						+ calc(XR.next(x), v)
						- accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ accWE.getCost(XR.next(y), XR.next(x))
					+ calc(XR.next(x), v) - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove4: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			if (XR.next(y) != XR.getTerminatingPointOfRoute(XR.route(y))) {
				if (XR.next(x) != XR.getTerminatingPointOfRoute(XR.route(x))) {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.prev(XR.getTerminatingPointOfRoute(XR
											.route(y))))
							+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
									.route(y))), XR.next(y))
							+ accWE.getCost(XR.next(y), XR.next(x))
							+ calc(XR.next(x), XR.prev(XR
									.getTerminatingPointOfRoute(XR.route(x))))
							+ accWE.getCost(XR.prev(XR
									.getTerminatingPointOfRoute(XR.route(x))),
									XR.getTerminatingPointOfRoute(XR.route(y)))
							- accWE.getCostRight(v);
				} else {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.prev(XR.getTerminatingPointOfRoute(XR
											.route(y))))
							+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
									.route(y))), XR.next(y))
							+ accWE.getCost(XR.next(y),
									XR.getTerminatingPointOfRoute(XR.route(y)))
							- accWE.getCostRight(v);
				}
			}
			if (XR.next(x) != XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.next(x))
						+ calc(XR.next(x), XR.prev(XR
								.getTerminatingPointOfRoute(XR.route(x))))
						+ accWE.getCost(XR.prev(XR
								.getTerminatingPointOfRoute(XR.route(x))), XR
								.getTerminatingPointOfRoute(XR.route(y)))
						- accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), v) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					- accWE.getCostRight(XR.next(XR.getStartingPointOfRoute(XR
							.route(y))));
		}
		if (XR.route(x) == XR.route(v)) {
			if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
						XR.next(XR.getStartingPointOfRoute(XR.route(y))))
						+ calc(XR.next(XR.getStartingPointOfRoute(XR.route(y))),
								y)
						+ accWE.getCost(y, x)
						+ calc(x, XR.next(XR.getStartingPointOfRoute(XR
								.route(x))))
						+ accWE.getCost(XR.next(XR.getStartingPointOfRoute(XR
								.route(x))), XR.getTerminatingPointOfRoute(XR
								.route(x))) - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
					return accWE
							.getCost(XR.getStartingPointOfRoute(XR.route(y)),
									XR.next(x))
							+ calc(XR.next(x), v) - accWE.getCostRight(v);
				}
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), XR.next(y))
						+ accWE.getCost(XR.next(y), XR.next(x))
						+ calc(XR.next(x), v) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ calc(XR.next(XR.getStartingPointOfRoute(XR.route(y))), y)
					+ accWE.getCost(y, x) + calc(x, v) - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove5: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return accWE.getCostRight(x)
						+ accWE.getCost(x,
								XR.getTerminatingPointOfRoute(XR.route(x)))
						- accWE.getCostRight(v);
			}
			return accWE.getCostRight(x)
					+ accWE.getCost(x, XR.next(y))
					+ calc(XR.next(y),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ accWE.getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWE.getCostRight(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE.getCostRight(y)
						+ accWE.getCost(y,
								XR.getTerminatingPointOfRoute(XR.route(y)))
						- accWE.getCostRight(v);
			}
			return accWE.getCostRight(y)
					+ accWE.getCost(y, XR.next(x))
					+ calc(XR.next(x),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v) && XR.isBefore(x, v)) {
			return accWE.getCostRight(y) + accWE.getCost(y, XR.next(x))
					+ calc(XR.next(x), v) - accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v) && XR.isBefore(y, v)) {
			return accWE.getCostRight(x) + accWE.getCost(x, XR.next(y))
					+ calc(XR.next(y), v) - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove6: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return accWE
						.getCost(XR.getStartingPointOfRoute(XR.route(x)), x)
						+ calc(x, XR.next(XR.getStartingPointOfRoute(XR
								.route(x))))
						+ accWE.getCost(XR.next(XR.getStartingPointOfRoute(XR
								.route(x))), XR.getTerminatingPointOfRoute(XR
								.route(x))) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ accWE.getCost(XR.next(y), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWE.getCostRight(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE.getCostRight(y)
						+ accWE.getCost(y,
								XR.getTerminatingPointOfRoute(XR.route(y)))
						- accWE.getCostRight(v);
			}
			return accWE.getCostRight(y)
					+ accWE.getCost(y, XR.next(x))
					+ calc(XR.next(x),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWE.getCostRight(v);
		}

		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(x, v)) {
				return accWE.getCostRight(y) + accWE.getCost(y, XR.next(x))
						+ calc(XR.next(x), v) - accWE.getCostRight(v);
			}
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return accWE
						.getCost(XR.getStartingPointOfRoute(XR.route(x)), x)
						+ calc(x, v) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ accWE.getCost(XR.next(y), x)
					+ calc(x, v) - accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v) && XR.isBefore(y, v)) {
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							v) - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove7: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		return evaluateTwoOptMove6(y, x);
	}

	//
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoOptMove(x, y)) {
			System.out.println(name() + ":: Error evaluateTwoOptMove8: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))
				|| v == XR.getStartingPointOfRoute(XR.route(y))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return accWE
						.getCost(XR.getStartingPointOfRoute(XR.route(x)), x)
						+ calc(x, XR.next(XR.getStartingPointOfRoute(XR
								.route(x))))
						+ accWE.getCost(XR.next(XR.getStartingPointOfRoute(XR
								.route(x))), XR.getTerminatingPointOfRoute(XR
								.route(x))) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ accWE.getCost(XR.next(y), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)))
					- accWE.getCostRight(v);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(y))) {
			if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE
						.getCost(XR.getStartingPointOfRoute(XR.route(y)), y)
						+ calc(y, XR.next(XR.getStartingPointOfRoute(XR
								.route(y))))
						+ accWE.getCost(XR.next(XR.getStartingPointOfRoute(XR
								.route(y))), XR.getTerminatingPointOfRoute(XR
								.route(y))) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.next(x))
					+ accWE.getCost(XR.next(x), y)
					+ calc(y, XR.next(XR.getStartingPointOfRoute(XR.route(y))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(y))),
							XR.getTerminatingPointOfRoute(XR.route(y)))
					- accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(x, v)) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(x))), v) - accWE.getCostRight(v);
			}
			if (XR.next(y) == XR.getTerminatingPointOfRoute(XR.route(y))) {
				return accWE
						.getCost(XR.getStartingPointOfRoute(XR.route(x)), x)
						+ calc(x, v) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))),
							XR.next(y))
					+ accWE.getCost(XR.next(y), x)
					+ calc(x, v) - accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v)) {
			if (XR.isBefore(y, v)) {
				return accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
						XR.prev(XR.getTerminatingPointOfRoute(XR.route(y))))
						+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR
								.route(y))), v) - accWE.getCostRight(v);
			}
			if (XR.next(x) == XR.getTerminatingPointOfRoute(XR.route(x))) {
				return accWE
						.getCost(XR.getStartingPointOfRoute(XR.route(y)), y)
						+ calc(y, v) - accWE.getCostRight(v);
			}
			return accWE.getCost(XR.getStartingPointOfRoute(XR.route(y)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					+ calc(XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))),
							XR.next(x))
					+ accWE.getCost(XR.next(x), y)
					+ calc(y, v) - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove1: " + x1
					+ " " + x2 + " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v)) {
			if (XR.isBefore(x2, v)) {
				return accWE.getCostRight(XR.prev(x1))
						+ accWE.getCost(XR.prev(x1), XR.next(x2))
						+ calc(XR.next(x2), v) - accWE.getCostRight(v);
			}
			if (XR.isBefore(v, x1)) {
				return 0;
			}
			return accWE.getCostRight(y) + accWE.getCost(y, x1) + calc(x1, v)
					- accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v) && XR.isBefore(y, v)) {
			return accWE.getCostRight(y) + accWE.getCost(y, x1) + calc(x1, x2)
					+ accWE.getCost(x2, XR.next(y)) + calc(XR.next(y), v)
					- accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformOrOptMove(x1, x2, y)) {
			System.out.println(name() + ":: Error evaluateOrOptMove2: " + x1
					+ " " + x2 + " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v)) {
			if (XR.isBefore(x2, v)) {
				return accWE.getCostRight(XR.prev(x1))
						+ accWE.getCost(XR.prev(x1), XR.next(x2))
						+ calc(XR.next(x2), v) - accWE.getCostRight(v);
			}
			if (XR.isBefore(v, x1)) {
				return 0;
			}
			return accWE.getCostRight(y) + accWE.getCost(y, x2) + calc(x2, v)
					- accWE.getCostRight(v);
		}
		if (XR.route(y) == XR.route(v) && XR.isBefore(y, v)) {
			return accWE.getCostRight(y) + accWE.getCost(y, x2) + calc(x2, x1)
					+ accWE.getCost(x1, XR.next(y)) + calc(XR.next(y), v)
					- accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove1: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = accWE.getCostRight(x);
			newV += accWE.getCost(x, z) + calc(z, XR.next(y));
			newV += accWE.getCost(XR.next(y), XR.next(x)) + calc(XR.next(x), y);
			newV += accWE.getCost(y, XR.next(z))
					+ calc(XR.next(z),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, z) + calc(z, XR.next(y));
				newV += accWE.getCost(XR.next(y), XR.next(x))
						+ calc(XR.next(x), y);
				newV += accWE.getCost(y, XR.next(z)) + calc(XR.next(z), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {
				return accWE.getCostRight(x) + accWE.getCost(x, z) + calc(z, v)
						- accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, z) + calc(z, XR.next(y));
				newV += accWE.getCost(XR.next(y), XR.next(x))
						+ calc(XR.next(x), v);
				return newV - accWE.getCostRight(v);
			}
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove2: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- accWE.getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = accWE.getCostLeft(XR.next(z)) + d1
					+ accWE.getCost(XR.next(z), y);
		} else {
			d2 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)), y);
		}

		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {

			double newV = d2;
			newV += calc(y, XR.next(x));
			newV += accWE.getCost(XR.next(x), XR.next(y)) + calc(XR.next(y), z);
			newV += accWE.getCost(z, x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return accWE.getCostLeft(v) + d1 - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = d2;
				newV += calc(y, XR.next(x));
				newV += accWE.getCost(XR.next(x), XR.next(y))
						+ calc(XR.next(y), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				return d2 + calc(y, v) - accWE.getCostRight(v);
			}
			double newV = d2 + calc(y, XR.next(x));
			newV += accWE.getCost(XR.next(x), XR.next(y)) + calc(XR.next(y), z);
			newV += accWE.getCost(z, x) + calc(x, v);
			return newV - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove3: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = accWE.getCostRight(x);
			newV += accWE.getCost(x, y) + calc(y, XR.next(x));
			newV += accWE.getCost(XR.next(x), z) + calc(z, XR.next(y));
			newV += accWE.getCost(XR.next(y), XR.next(z))
					+ calc(XR.next(z),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, y) + calc(y, XR.next(x));
				newV += accWE.getCost(XR.next(x), z) + calc(z, XR.next(y));
				newV += accWE.getCost(XR.next(y), XR.next(z))
						+ calc(XR.next(z), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, y) + calc(y, XR.next(x));
				newV += accWE.getCost(XR.next(x), z) + calc(z, v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				return accWE.getCostRight(x) + accWE.getCost(x, y) + calc(y, v)
						- accWE.getCostRight(v);
			}
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove4: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- accWE.getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = accWE.getCostLeft(XR.next(z)) + d1
					+ accWE.getCost(XR.next(z), XR.next(y));
		} else {
			d2 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.next(y));
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = d2 + calc(XR.next(y), z);
			newV += accWE.getCost(z, XR.next(x)) + calc(XR.next(x), y);
			newV += accWE.getCost(y, x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}

		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return accWE.getCostLeft(v) + d1 - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {
				return d2 + calc(XR.next(y), v) - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				double newV = d2 + calc(XR.next(y), z);
				newV += accWE.getCost(z, XR.next(x)) + calc(XR.next(x), v);
				return newV - accWE.getCostRight(v);
			}
			double newV = d2 + calc(XR.next(y), z);
			newV += accWE.getCost(z, XR.next(x)) + calc(XR.next(x), y);
			newV += accWE.getCost(y, x) + calc(x, v);

			return newV - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove5: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = accWE.getCostRight(x);
			newV += accWE.getCost(x, XR.next(y)) + calc(XR.next(y), z);
			newV += accWE.getCost(z, XR.next(x)) + calc(XR.next(x), y);
			newV += accWE.getCost(y, XR.next(z))
					+ calc(XR.next(z),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {// if (accWE.getCostRight(v] >
									// accWE.getCostRight(z]) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, XR.next(y)) + calc(XR.next(y), z);
				newV += accWE.getCost(z, XR.next(x)) + calc(XR.next(x), y);
				newV += accWE.getCost(y, XR.next(z)) + calc(XR.next(z), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {// if (accWE.getCostRight(v] >
									// accWE.getCostRight(y]) {
				return accWE.getCostRight(x) + accWE.getCost(x, XR.next(y))
						+ calc(XR.next(y), v) - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {// if (accWE.getCostRight(v] >
									// accWE.getCostRight(x]) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, XR.next(y)) + calc(XR.next(y), z);
				newV += accWE.getCost(z, XR.next(x)) + calc(XR.next(x), v);
				return newV - accWE.getCostRight(v);
			}
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove6: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- accWE.getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = accWE.getCostLeft(XR.next(z)) + d1
					+ accWE.getCost(XR.next(z), y);
		} else {
			d2 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)), y);
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = d2 + calc(y, XR.next(x));
			newV += accWE.getCost(XR.next(x), z) + calc(z, XR.next(y));
			newV += accWE.getCost(XR.next(y), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}

		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return accWE.getCostLeft(v) + d1 - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = d2 + calc(y, XR.next(x));
				newV += accWE.getCost(XR.next(x), z) + calc(z, v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				return d2 + calc(y, v) - accWE.getCostRight(v);
			}
			double newV = d2 + calc(y, XR.next(x));
			newV += accWE.getCost(XR.next(x), z) + calc(z, XR.next(y));
			newV += accWE.getCost(XR.next(y), x) + calc(x, v);
			return newV - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove7: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = accWE.getCostRight(x);
			newV += accWE.getCost(x, XR.next(y)) + calc(XR.next(y), z);
			newV += accWE.getCost(z, y) + calc(y, XR.next(x));
			newV += accWE.getCost(XR.next(x), XR.next(z))
					+ calc(XR.next(z),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {// if (accWE.getCostRight(v] >
									// accWE.getCostRight(z]) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, XR.next(y)) + calc(XR.next(y), z);
				newV += accWE.getCost(z, y) + calc(y, XR.next(x));
				newV += accWE.getCost(XR.next(x), XR.next(z))
						+ calc(XR.next(z), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {// if (accWE.getCostRight(v] >
									// accWE.getCostRight(y]) {
				return accWE.getCostRight(x) + accWE.getCost(x, XR.next(y))
						+ calc(XR.next(y), v) - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {// if (accWE.getCostRight(v] >
									// accWE.getCostRight(x]) {
				double newV = accWE.getCostRight(x);
				newV += accWE.getCost(x, XR.next(y)) + calc(XR.next(y), z);
				newV += accWE.getCost(z, y) + calc(y, v);
				return newV - accWE.getCostRight(v);
			}
		}
		return 0;
	}

	//
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformThreeOptMove(x, y, z)) {
			System.out.println(name() + ":: Error evaluateThreeOptMove8: " + x
					+ " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (v == XR.getStartingPointOfRoute(XR.route(x))) {
			return 0;
		}
		double d1 = 0;
		double d2 = 0;
		if (XR.next(z) != XR.getTerminatingPointOfRoute(XR.route(x))) {
			d1 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))))
					- accWE.getCost(XR.getTerminatingPointOfRoute(XR.route(x)),
							XR.prev(XR.getTerminatingPointOfRoute(XR.route(x))));
			d2 = accWE.getCostLeft(XR.next(z)) + d1
					+ accWE.getCost(XR.next(z), XR.next(x));
		} else {
			d2 = accWE.getCost(XR.getStartingPointOfRoute(XR.route(x)),
					XR.next(x));
		}
		if (v == XR.getTerminatingPointOfRoute(XR.route(x))) {
			double newV = d2 + calc(XR.next(x), y);
			newV += accWE.getCost(y, z) + calc(z, XR.next(y));
			newV += accWE.getCost(XR.next(y), x)
					+ calc(x, XR.next(XR.getStartingPointOfRoute(XR.route(x))))
					+ accWE.getCost(
							XR.next(XR.getStartingPointOfRoute(XR.route(x))),
							XR.getTerminatingPointOfRoute(XR.route(x)));
			return newV - accWE.getCostRight(v);
		}
		if (XR.route(x) == XR.route(v)) {
			if (XR.isBefore(z, v)) {
				return accWE.getCostLeft(v) + d1 - accWE.getCostRight(v);
			}
			if (XR.isBefore(y, v)) {
				double newV = d2 + calc(XR.next(x), y);
				newV += accWE.getCost(y, z) + calc(z, v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(x, v)) {
				return d2 + calc(XR.next(x), v) - accWE.getCostRight(v);
			}
			double newV = d2 + calc(XR.next(x), y);
			newV += accWE.getCost(y, z) + calc(z, XR.next(y));
			newV += accWE.getCost(XR.next(y), x) + calc(x, v);
			return newV - accWE.getCostRight(v);
		}
		return 0;
	}

	//
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluateCrossExchangeMove: "
					+ x1 + " " + y1 + " " + x2 + " " + y2 + "\n"
					+ XR.toString());
			System.exit(-1);
		}
		if (XR.route(x1) == XR.route(v)) {
			if (XR.isBefore(y1, v)) {
				double newV = accWE.getCostRight(x1)
						+ accWE.getCost(x1, XR.next(x2));
				newV += calc(XR.next(x2), y2) + accWE.getCost(y2, XR.next(y1))
						+ calc(XR.next(y1), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(x1, v)) {
				return accWE.getCostRight(x2) + accWE.getCost(x2, XR.next(x1))
						+ calc(XR.next(x1), v) - accWE.getCostRight(v);
			}
		}
		if (XR.route(x2) == XR.route(v)) {
			if (XR.isBefore(y2, v)) {
				double newV = accWE.getCostRight(x2)
						+ accWE.getCost(x2, XR.next(x1));
				newV += calc(XR.next(x1), y1) + accWE.getCost(y1, XR.next(y2))
						+ calc(XR.next(y2), v);
				return newV - accWE.getCostRight(v);
			}
			if (XR.isBefore(x2, v)) {
				return accWE.getCostRight(x1) + accWE.getCost(x1, XR.next(x2))
						+ calc(XR.next(x2), v) - accWE.getCostRight(v);
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

	//
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub

	}

	//
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub

	}

	//
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub

	}

	//
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	//
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub

	}
	
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		//System.out.println("AccumulatedEdgeWeightsOnPathVR::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		//System.exit(-1);
	}

	//
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		//System.out.println("AccumulatedEdgeWeightsOnPathVR::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		//System.exit(-1);
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
				if ((!ok) && XR.route(v) == XR.route(y[i])
						&& (!XR.isBefore(y[i], v))) {
					return d;
				}
				d += accWE.getCost(y[i], x[i]);
				if (v == x[i]) {
					return d + accWE.getCostRight(y[i]) - accWE.getCostRight(v);
				}
				while (i < x.length - 1 && y[i] == y[i + 1]) {
					d += accWE.getCost(x[i], x[i + 1]);
					i++;
					if (v == x[i]) {
						return d + accWE.getCostRight(y[i])
								- accWE.getCostRight(v);
					}
				}
				d += accWE.getCost(x[i], XR.next(y[i]))
						- accWE.getCost(y[i], XR.next(y[i]));
				i++;
			} else {
				if ((!ok) && XR.isBefore(v, z[j])) {
					return d;
				}
				Point prevZ = XR.prev(z[j]);
				if (i > 0 && XR.next(y[i - 1]) == z[j]) {
					prevZ = x[i - 1];
				}
				d -= accWE.getCost(prevZ, z[j]);
				while (j < z.length - 1 && XR.next(z[j]) == z[j + 1]) {
					d -= accWE.getCost(z[j], z[j + 1]);
					j++;
				}
				Point nextZ = XR.next(z[j]);
				d += accWE.getCost(prevZ, nextZ) - accWE.getCost(z[j], nextZ);
				j++;
			}
		}

		return d;
	}

	//
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformTwoPointsMove(x1, x2, y1, y2)) {
			System.out.println(name() + ":: Error evaluateTwoPointsMove: " + x1
					+ " " + y1 + " " + x2 + " " + y2 + "\n" + XR.toString());
			System.exit(-1);
		}
		int n = 2;
		Point[] x = { x1, x2 };
		Point[] y = { y1, y2 };
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(x[i]));
			route.add(XR.route(y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(y[i]) == r) {
					xx.add(x[i]);
					yy.add(y[i]);
					ok |= x[i] == v;
				}
				if (XR.route(x[i]) == r) {
					zz.add(x[i]);
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

	//
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
		Point[] x = { x1, x2, x3 };
		Point[] y = { y1, y2, y3 };
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(x[i]));
			route.add(XR.route(y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(y[i]) == r) {
					xx.add(x[i]);
					yy.add(y[i]);
					ok |= x[i] == v;
				}
				if (XR.route(x[i]) == r) {
					zz.add(x[i]);
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
				// System.out.println("--------- " + r);
				eval += evaluateMove(X, Y, Z);
			}
		}
		return eval;
	}

	//
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
		Point[] x = { x1, x2, x3, x4 };
		Point[] y = { y1, y2, y3, y4 };
		HashSet<Integer> route = new HashSet<Integer>();
		for (int i = 0; i < n; i++) {
			route.add(XR.route(x[i]));
			route.add(XR.route(y[i]));
		}
		double eval = 0;
		for (int r : route) {
			boolean ok = false;
			ArrayList<Point> xx = new ArrayList<Point>();
			ArrayList<Point> yy = new ArrayList<Point>();
			ArrayList<Point> zz = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				if (XR.route(y[i]) == r) {
					xx.add(x[i]);
					yy.add(y[i]);
					ok |= x[i] == v;
				}
				if (XR.route(x[i]) == r) {
					zz.add(x[i]);
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
				// System.out.println("--------- " + r);
				eval += evaluateMove(X, Y, Z);
			}
		}
		return eval;
	}

	public String toString() {
		String s = "";
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			s += "route[" + k + "] : ";
			for (Point x = XR.getStartingPointOfRoute(k); x != XR
					.getTerminatingPointOfRoute(k); x = XR.next(x)) {
				s += x.getID() + " " + accWE.getCostRight(x) + " "
						+ accWE.getCostLeft(x) + " | ";
			}
			s += "\n";
		}
		return s;
	}

	//
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddOnePoint(x, y)) {
			System.out.println(name() + ":: Error evaluate AddOnePoint: " + x
					+ " " + y + "\n" + XR.toString());
			System.exit(-1);
		}
		if (x == v) {
			return accWE.getCostRight(y) + accWE.getCost(y, x);
		}
		if (XR.isBefore(y, v)) {
			return accWE.getCost(y, x) + accWE.getCost(x, XR.next(y))
					- accWE.getCost(y, XR.next(y));
		}
		return 0;
	}

	//
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveOnePoint(x)) {
			System.out.println(name() + ":: Error evaluate RemoveOnePoint: "
					+ x + "\n" + XR.toString());
			System.exit(-1);
		}
		if (x == v) {
			return -accWE.getCostRight(x);
		}
		if (XR.isBefore(x, v)) {
			return accWE.getCost(XR.prev(x), XR.next(x))
					- accWE.getCost(XR.prev(x), x)
					- accWE.getCost(x, XR.next(x));
		}
		return 0;
	}

	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2){
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddTwoPoints(x1, y1, x2, y2)) {
			System.out.println(name() + ":: Error evaluate AddTwoPoint: " + x1
					+ " " + y1 + " " + x2
					+ " " + y2 + "\n" + XR.toString());
			System.exit(-1);
		}
		if (x1 == v)
			return accWE.getCostRight(y1) + accWE.getCost(y1, x1);

		if (x2 == v) {
			if(y1 != y2){
				Point ny1 = XR.next(y1);
				return accWE.getCostRight(y2) - accWE.getCost(y1, ny1)
				+ accWE.getCost(y1, x1) + accWE.getCost(x1, ny1) + accWE.getCost(y2, x2);
			}
			else
				return accWE.getCostRight(y1) + accWE.getCost(y1, x1) + accWE.getCost(x1, x2);
		}
		
		if (XR.isBefore(y1, v) && !XR.isBefore(y2, v))
			return accWE.getCost(y1, x1) + accWE.getCost(x1, XR.next(y1))
					- accWE.getCost(y1, XR.next(y1));
		
		if(XR.isBefore(y2, v)){
			if(y1 != y2)
				return accWE.getCost(y1, x1) + accWE.getCost(x1, XR.next(y1)) - accWE.getCost(y1, XR.next(y1))
				+ accWE.getCost(y2, x2) + accWE.getCost(x2, XR.next(y2)) - accWE.getCost(y2, XR.next(y2));
			else
				return accWE.getCost(y1, x1) + accWE.getCost(x1, x2) 
				+ accWE.getCost(x2, XR.next(y1)) - accWE.getCost(y1,  XR.next(y1));
		}
		
		return 0;
	}
	
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformRemoveTwoPoints(x1, x2)) {
			System.out.println(name() + ":: Error evaluate RemoveTwoPoints: "
					+ x1 + " " + x2 + "\n" + XR.toString());
			System.exit(-1);
		}
		if (x1 == v)
			return -accWE.getCostRight(x1);
		
		if(x2 == v)
			return -accWE.getCostRight(x2);
		
		if (XR.isBefore(x1, v) && !XR.isBefore(x2, v))
			return accWE.getCost(XR.prev(x1), XR.next(x1))
					- accWE.getCost(XR.prev(x1), x1)
					- accWE.getCost(x1, XR.next(x1));
		
		if(XR.isBefore(x2, v))
			if(x2 == XR.next(x1)){
				return accWE.getCost(XR.prev(x1), XR.next(x2)) - accWE.getCost(XR.prev(x1), x1)
						- accWE.getCost(x1, x2) - accWE.getCost(x2, XR.next(x2));
			}
			else{
				return accWE.getCost(XR.prev(x1), XR.next(x1))
						- accWE.getCost(XR.prev(x1), x1)
						- accWE.getCost(x1, XR.next(x1))
						+ accWE.getCost(XR.prev(x2), XR.next(x2))
						- accWE.getCost(XR.prev(x2), x2)
						- accWE.getCost(x2, XR.next(x2));
			}
		return 0;
	}
	
	//
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	//
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformAddRemovePoints(x, y, z)) {
			System.out.println(name() + ":: Error evaluate AddRemovePoints: "
					+ x + " " + y + " " + z + "\n" + XR.toString());
			System.exit(-1);
		}
		if (XR.route(x) == XR.route(z)) {
			if (v != y && XR.route(x) != XR.route(v)) {
				return 0;
			}
			if (x == v) {
				return -accWE.getCostRight(x);
			}

			Point[] X = { y };
			Point[] Y = { z };
			Point[] Z = { x };
			return evaluateMove(X, Y, Z);
		} else {
			if (XR.route(x) == XR.route(v)) {
				if (v == x) {
					return -accWE.getCostRight(x);
				}
				if (XR.isBefore(x, v)) {
					return accWE.getCost(XR.prev(x), XR.next(x))
							- accWE.getCost(XR.prev(x), x)
							- accWE.getCost(x, XR.next(x));
				}
			}
			if (v == y) {

				return accWE.getCostRight(z) + accWE.getCost(z, y);
			}
			if (XR.isBefore(z, v)) {
				return accWE.getCost(z, y) + accWE.getCost(y, XR.next(z))
						- accWE.getCost(z, XR.next(z));
			}

		}

		return 0;
	}

	//
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub

	}

	//
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		if (!XR.checkPerformKPointsMove(x, y)) {
			System.out.println(name() + ":: Error evaluateKPointsMove:\n"
					+ XR.toString());
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
				} else if (y.get(i) == CBLSVR.NULL_POINT) {
					if (x.get(i) == v) {
						return -accWE.getCostRight(v);
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
		AccumulatedWeightEdgesVR accWE = new AccumulatedWeightEdgesVR(XR, awm);
		IFunctionVR[] f = new IFunctionVR[N];
		for (int i = 0; i < f.length; i++) {
			f[i] = new AccumulatedEdgeWeightsOnPathVR(accWE, p[i]);
		}
		mgr.close();
		mgr.performRemoveOnePoint(p[0]);

		int iter = 0;
		double[] oldV = new double[N];
		double[] newV = new double[N];
		double[] delta = new double[N];
		while (iter < 10000) {
			System.out.println(iter++ + "\n" + XR + "\n" + accWE);
			for (int i = 0; i < N; i++) {
				oldV[i] = f[i].getValue();
			}
			// int x1 = rand.nextInt(N);
			// int x2 = rand.nextInt(N);
			// int y1 = rand.nextInt(N);
			// int y2 = rand.nextInt(N);
			// int x3 = rand.nextInt(N);
			// int y3 = rand.nextInt(N);
			// int x4 = rand.nextInt(N);
			// int y4 = rand.nextInt(N);
			// while (!XR.checkPerformAddRemovePoints(p[x1], p[x2], p[x3])) {
			// x1 = rand.nextInt(N);
			// y1 = rand.nextInt(N);
			// x2 = rand.nextInt(N);
			// y2 = rand.nextInt(N);
			// x3 = rand.nextInt(N);
			// y3 = rand.nextInt(N);
			// x4 = rand.nextInt(N);
			// y4 = rand.nextInt(N);
			// }

			// System.out.println(x1 + " " + p[x1] + " " + x2 + " " + p[x2] +
			// " " + x3 + " "+ p[x3] + " " );
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
			System.out.println(XR + "\n" + accWE);
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
