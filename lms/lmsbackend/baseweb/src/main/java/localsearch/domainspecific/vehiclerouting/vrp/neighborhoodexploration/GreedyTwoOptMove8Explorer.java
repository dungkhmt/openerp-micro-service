
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
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove8;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyTwoOptMove8Explorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	private boolean firstImprovement = true;
	public GreedyTwoOptMove8Explorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	public GreedyTwoOptMove8Explorer(VarRoutesVR XR, LexMultiFunctions F, boolean firstImprovement) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.firstImprovement = firstImprovement;
	}
	
	public GreedyTwoOptMove8Explorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}
	public String name(){
		return "GreedyTwoOptMove8Explorer";
	}
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub 
		if(firstImprovement && N.hasImprovement()){
			System.out.println(name() + "::exploreNeighborhood, has improvement --> RETURN");
			return;
		}
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			for (int j = i + 1; j <= XR.getNbRoutes(); j++) {
				for (Point x = XR.next(XR.getStartingPointOfRoute(i)); XR.isClientPoint(x); x = XR.next(x)) {
					for (Point y = XR.next(XR.getStartingPointOfRoute(j)); XR.isClientPoint(y); y = XR.next(y)) {
						if (XR.checkPerformTwoOptMove(x, y)) {
							LexMultiValues eval = F.evaluateTwoOptMove8(x, y);
							if (eval.lt(bestEval)){
								N.clear();
								N.add(new TwoOptMove8(mgr, eval, x, y,this));
								bestEval.set(eval);
							} else if (eval.eq(bestEval)) {
								N.add(new TwoOptMove8(mgr, eval, x, y,this));
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
	
	public void performMove(IVRMove m){
		//DO NOTHING
	}
}
