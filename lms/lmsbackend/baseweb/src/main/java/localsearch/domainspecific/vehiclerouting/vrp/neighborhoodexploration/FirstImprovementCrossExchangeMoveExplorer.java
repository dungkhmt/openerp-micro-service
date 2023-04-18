
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 27/09/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration;

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

public class FirstImprovementCrossExchangeMoveExplorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	
	public FirstImprovementCrossExchangeMoveExplorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	
	public FirstImprovementCrossExchangeMoveExplorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}
	
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		for (int i = 1; i < XR.getNbRoutes(); i++) {
			if(N.size() > 0) break;
			for (int j = i + 1; j < XR.getNbRoutes(); j++) {
				if(N.size() > 0) break;
				for (Point x1 = XR.next(XR.getStartingPointOfRoute(i)); x1 != XR.getTerminatingPointOfRoute(i); x1 = XR.next(x1)) {
					if(N.size() > 0) break;
					for (Point y1 = XR.next(x1); y1 != XR.getTerminatingPointOfRoute(i); y1 = XR.next(y1)) {
						if(N.size() > 0) break;
						for (Point x2 = XR.next(XR.getStartingPointOfRoute(j)); x2 != XR.getTerminatingPointOfRoute(j); x2 = XR.next(x2)) {
							if(N.size() > 0) break;
							for (Point y2 = XR.next(x2); y2 != XR.getTerminatingPointOfRoute(j); y2 = XR.next(y2)) {
								if(N.size() > 0) break;
								if (XR.checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
									LexMultiValues eval = F.evaluateCrossExchangeMove(x1, y1, x2, y2);
									//System.out.println(name() + "::exploreNeighborhood (" + x1.ID + "," + 
									//y1.ID + "," + x2.ID + "," + y2.ID + ", eval = " + eval.toString());
									if (eval.lt(bestEval)) {
										N.add(new CrossExchangeMove(mgr, eval, x1, y1, x2, y2));
									}
								}
							}
						}
					}
				}
			}
		}
		//System.out.println(name() + "::exploreNeighborhood finished");
	}

	public String name(){
		return "FirstImprovementCrossExchangeMoveExplorer";
	}
	public void performMove(IVRMove m){
		// DO NOTHING
	}
}
