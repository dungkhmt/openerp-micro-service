package localsearch.domainspecific.vehiclerouting.vrp.functions;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NumberCoveredPoints implements IFunctionVR {
	private int value;
	private VarRoutesVR XR;
	private HashMap<Point, HashSet<Integer>> cover;
	private VRManager mgr;
	private HashMap<Integer, Integer> coverSet;
	
	
	public NumberCoveredPoints(VarRoutesVR XR, HashMap<Point, HashSet<Integer>> cover){
		// cover[v] is the set of points that are covered by the point v
		// semantic: sum of weight of points covered by the tour
		this.XR = XR;
		this.cover = cover;
		this.mgr = XR.getVRManager();
		post();
	}
	private void post(){
		mgr.post(this);
		value = 0;
		coverSet = new HashMap<Integer, Integer>();
	}
	
	
	public void initPropagation() {
		// TODO Auto-generated method stub
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			for(Point v = XR.getStartingPointOfRoute(k); v != XR.getTerminatingPointOfRoute(k); v = XR.next(v)){
				for(int x : cover.get(v)){
					if (!coverSet.containsKey(x)) {
						coverSet.put(x, 0);
					}
					coverSet.put(x, coverSet.get(x) + 1);
				}
			}
		}
		value = coverSet.size();
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
		for(int u : cover.get(x)) {
			if (!coverSet.containsKey(u)) {
				coverSet.put(u, 0);
			}
			coverSet.put(u, coverSet.get(u) + 1);
		}
		value = coverSet.size();
	}

	
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		for (int u : cover.get(x)) {
			int t = coverSet.get(u);
			if (t == 1) {
				coverSet.remove(u);
			} else {
				coverSet.put(u, t - 1);
			}
		}
		value = coverSet.size();
	}

	
	public String name() {
		// TODO Auto-generated method stub
		return "NumbercoveredPoints";
	}

	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}

	
	public double getValue() {
		// TODO Auto-generated method stub
		return value;
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
		return 0;
	}

	
	public double evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
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
		return 0;
	}

	
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int eval = 0;
		for (int u : cover.get(x)) {
			if (!coverSet.containsKey(u)) {
				eval++;
			}
		}
		return eval;
	}

	
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int eval = 0;
		for (int u : cover.get(x)) {
			if (coverSet.get(u) == 1) {
				eval--;
			}
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

	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		for(int u : cover.get(y)){
			if (!coverSet.containsKey(u)) {
				coverSet.put(u, 0);
			}
			coverSet.put(u, coverSet.get(u) + 1);
		}
		for (int u : cover.get(x)) {
			int t = coverSet.get(u);
			if (t == 1) {
				coverSet.remove(u);
			} else {
				coverSet.put(u, t - 1);
			}
		}
		value = coverSet.size();
	}
	
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int eval = 0;
		HashSet<Integer> s = new HashSet<Integer>(cover.get(y));
		for (int u : cover.get(x)) {
			if (s.contains(u)) {
				s.remove(u);
			} else {
				if (coverSet.get(u) == 1) {
					eval--;
				}
			}
		}
		for (int u : s) {
			if (!coverSet.containsKey(u)) {
				eval++;
			}
		}
		return eval;
	}
	
	
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		for (int i = 0; i < x.size(); i++) {
			Point p = x.get(i);
			Point q = y.get(i);
			if (XR.oldRoute(p) == Constants.NULL_POINT) {
				for(int u : cover.get(p)){
					if (!coverSet.containsKey(u)) {
						coverSet.put(u, 0);
					}
					coverSet.put(u, coverSet.get(u) + 1);
				}
			}
			if (q == CBLSVR.NULL_POINT) {
				for (int u : cover.get(p)) {
					int t = coverSet.get(u);
					if (t == 1) {
						coverSet.remove(u);
					} else {
						coverSet.put(u, t - 1);
					}
				}
			}
		}
		value = coverSet.size();
	}
	
	
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (int i = 0; i < x.size(); i++) {
			Point p = x.get(i);
			Point q = y.get(i);
			if (XR.route(p) == Constants.NULL_POINT) {
				for(int u : cover.get(p)){
					if (!m.containsKey(u)) {
						m.put(u, 0);
					}
					m.put(u, m.get(u) + 1);
				}
			}
			if (q == CBLSVR.NULL_POINT) {
				for (int u : cover.get(p)) {
					if (!m.containsKey(u)) {
						m.put(u, 0);
					}
					m.put(u, m.get(u) - 1);
				}
			}
		}
		double eval = 0;
		for (int u : m.keySet()) {
			int v = m.get(u);
			if (v != 0) {
				if (!coverSet.containsKey(u)) {
					eval += 1;
				} else {
					v += coverSet.get(u);
					if (v == 0) {
						eval -= 1;
					}
				}
			}
		}
		return eval;
	}
	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
