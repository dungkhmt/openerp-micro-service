
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
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OrOptMove2;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyOrOptMove2Explorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	private boolean firstImprovement = true;
	public GreedyOrOptMove2Explorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	public GreedyOrOptMove2Explorer(VarRoutesVR XR, LexMultiFunctions F, boolean firstImprovement) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.firstImprovement = firstImprovement;
	}
	
	public GreedyOrOptMove2Explorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}

	public String name(){
		return "GreedyOrOptMove2Explorer";
	}
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		if(firstImprovement && N.hasImprovement()){
			System.out.println(name() + "::exploreNeighborhood, has improvement --> RETURN");
			return;
		}

		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			for (int j = 1; j <= XR.getNbRoutes(); j++) {
				if (i != j) {
					for (Point x1 = XR.next(XR.getStartingPointOfRoute(i)); x1 != XR.getTerminatingPointOfRoute(i); x1 = XR.next(x1)) {
						for (Point x2 = XR.next(x1); x2 != XR.getTerminatingPointOfRoute(i); x2 = XR.next(x2)) {
							for (Point y = XR.getStartingPointOfRoute(j); y != XR.getTerminatingPointOfRoute(j); y = XR.next(y)) {
								if (XR.checkPerformOrOptMove(x1, x2, y)) {
									LexMultiValues eval = F.evaluateOrOptMove2(x1, x2, y);
									if (eval.lt(bestEval)){
										N.clear();
										N.add(new OrOptMove2(mgr, eval, x1, x2, y, this));
										bestEval.set(eval);
									} else if (eval.eq(bestEval)) {
										N.add(new OrOptMove2(mgr, eval, x1, x2, y, this));
									}
									if(firstImprovement){
										if(eval.lt(0)) return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	
	public void performMove(IVRMove m){
		//DO NOTHING
	}
}
