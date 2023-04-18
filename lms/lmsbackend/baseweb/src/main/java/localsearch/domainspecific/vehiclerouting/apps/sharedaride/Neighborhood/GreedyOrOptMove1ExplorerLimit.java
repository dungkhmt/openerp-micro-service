package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;

import java.util.ArrayList;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util.RandomUtil;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OrOptMove1;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyOrOptMove1ExplorerLimit implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	int K;
	HashSet<Point>pickups;
	HashSet<Point>delivery;
	int id1,id2;
	public GreedyOrOptMove1ExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, int K) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
		pickups = null;
		id1 = id2 = 0;
	}
	public GreedyOrOptMove1ExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, int K, ArrayList<Point>pickup, ArrayList<Point>deli) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
		pickups = new HashSet<>();
		for(Point p : pickup)
			pickups.add(p);
		delivery = new HashSet<Point>(deli);
		id1 = id2 = 0;
	}
	
	public String name(){
		return "GreedyOrOptMove1ExplorerLimit";
	}
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		ArrayList<Integer>listI = RandomUtil.randomKFromN(K, XR.getNbRoutes());
		ArrayList<Integer>listJ = RandomUtil.randomKFromN(K, XR.getNbRoutes());
		for (int i : listI)
		{
			for (int j : listJ) {
				if (i != j) {
					for (Point x1 = XR.next(XR.getStartingPointOfRoute(i)); x1 != XR.getTerminatingPointOfRoute(i); x1 = XR.next(x1)) {
						if(pickups!=null && !pickups.contains(x1))
							continue;
						for (Point x2 = XR.next(x1); x2 != XR.getTerminatingPointOfRoute(i); x2 = XR.next(x2)) {
							int d = XR.index(x2) - XR.index(x1);
							if(d%2==0)
								continue;
							if(delivery!=null&&!delivery.contains(x2))
								continue;
							if(x1.getID() == id1 && x2.getID() == id2)
								continue;
							for (Point y = XR.getStartingPointOfRoute(j); y != XR.getTerminatingPointOfRoute(j); y = XR.next(y)) {
								if (XR.checkPerformOrOptMove(x1, x2, y)) {
									LexMultiValues eval = F.evaluateOrOptMove1(x1, x2, y);
									if (eval.lt(bestEval)){
										N.clear();
										N.add(new OrOptMove1(mgr, eval, x1, x2, y, this));
										bestEval.set(eval);
									} else if (eval.eq(bestEval)) {
										N.add(new OrOptMove1(mgr, eval, x1, x2, y, this));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void performMove(IVRMove m){
		if(m instanceof OrOptMove1)
		{
			OrOptMove1 mv1 = (OrOptMove1)m;
			id1 = mv1.getX1().getID();
			id2 = mv1.getX2().getID();
		}
	}
}
