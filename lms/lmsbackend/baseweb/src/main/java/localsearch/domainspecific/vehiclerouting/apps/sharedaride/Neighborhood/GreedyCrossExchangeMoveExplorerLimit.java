package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;

import java.util.ArrayList;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.CrossExchangeMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyCrossExchangeMoveExplorerLimit implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	int K;
	HashSet<Point>pickup, delivery;
	public GreedyCrossExchangeMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, int K) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
	}
	public String name(){
		return "GreedyCrossExchangeMoveExplorerLimit";
	}
	public GreedyCrossExchangeMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, int K, ArrayList<Point>pick, ArrayList<Point> deli) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
		pickup = new HashSet<Point>(pick);
		delivery = new HashSet<Point>(deli);
	
	}
	
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < XR.getNbRoutes(); ++i)
		{
			for (int j = 0; j < XR.getNbRoutes(); ++j) 
			{
				for (Point x1 = XR.next(XR.getStartingPointOfRoute(i)); x1 != XR.getTerminatingPointOfRoute(i); x1 = XR.next(x1)) {
					int numP1 = 1;
					if(pickup!=null&&!pickup.contains(XR.next(x1)))
						continue;
					for (Point y1 = XR.next(x1); y1 != XR.getTerminatingPointOfRoute(i)&& numP1 <= K; y1 = XR.next(y1), numP1++) {
						if(delivery != null &&!delivery.contains(y1))
							continue;
						if(numP1%2==1)
							continue;
						for (Point x2 = XR.next(XR.getStartingPointOfRoute(j)); x2 != XR.getTerminatingPointOfRoute(j); x2 = XR.next(x2)) {
							int numP2 = 1;
							if(pickup!=null&& !pickup.contains(XR.next(x2)))
								continue;
							for (Point y2 = XR.next(x2); y2 != XR.getTerminatingPointOfRoute(j) && numP2 <= K; y2 = XR.next(y2), numP2++) {
								if(delivery != null &&!delivery.contains(y2))
									continue;
								if(numP2%2==1)
									continue;
								if (XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
									LexMultiValues eval = F.evaluateCrossExchangeMove(x1, y1, x2, y2);
									if (eval.lt(bestEval)) {
										N.clear();
										N.add(new CrossExchangeMove(mgr, eval, x1, y1, x2, y2));
										bestEval.set(eval);
									} else if (eval.eq(bestEval)) {
										N.add(new CrossExchangeMove(mgr, eval, x1, y1, x2, y2));
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
		// DO NOTHING
	}
}
