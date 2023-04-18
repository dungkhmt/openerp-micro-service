
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 27/09/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove6;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class FirstImprovementTwoOptMove6Explorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	
	public FirstImprovementTwoOptMove6Explorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	
	public FirstImprovementTwoOptMove6Explorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}
	
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub 
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			for (int j = i + 1; j <= XR.getNbRoutes(); j++) {
				for (Point x = XR.next(XR.getStartingPointOfRoute(i)); XR.isClientPoint(x); x = XR.next(x)) {
					for (Point y = XR.next(XR.getStartingPointOfRoute(j)); XR.isClientPoint(y); y = XR.next(y)) {
						if (XR.checkPerformTwoOptMove(x, y)) {
							LexMultiValues eval = F.evaluateTwoOptMove6(x, y);
							if (eval.lt(bestEval)){
								mgr.performTwoOptMove6(x, y);
								System.out.println(name() + "::exploreNeighborhood, F = " + F.getValues().toString());
							}
						}
					}
				}
			}
		}
	}
	public String name(){
		return "FirstImprovementTwoOptMove6Explorer";
	}

	public void performMove(IVRMove m){
		//DO NOTHING
	}
}
