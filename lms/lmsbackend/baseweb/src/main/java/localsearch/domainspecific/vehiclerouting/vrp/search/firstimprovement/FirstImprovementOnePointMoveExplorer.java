
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
import localsearch.domainspecific.vehiclerouting.vrp.moves.OnePointMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class FirstImprovementOnePointMoveExplorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	
	public FirstImprovementOnePointMoveExplorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	
	public FirstImprovementOnePointMoveExplorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}
	
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		for (Point x : XR.getClientPoints()) {
			for (Point y : XR.getAllPoints()) {
				//System.out.println(name() + "::exploreNeighborhood, consider (" + x.ID + "," + y.ID + " of route " + XR.route(y) + ", index " + XR.index(y) + ")");
				if (XR.checkPerformOnePointMove(x, y)) {
					//System.out.println(name() + "::exploreNeighborhood, accept (" + x.ID + "," + y.ID + " of route " + XR.route(y) + ", index " + XR.index(y) + ")");
					LexMultiValues eval = F.evaluateOnePointMove(x, y);
					//System.out.println(name() + "::exploreNeighborhood, accept (" + x.ID + "," + y.ID + " of route " + XR.route(y) + ", index " + XR.index(y) + ") eval = " + eval.toString());
					if(eval.lt(bestEval)){
						mgr.performOnePointMove(x, y);
						System.out.println(name() + "::exploreNeighborhood, F = " + F.getValues().toString());
					}
				}
			}
		}
		//System.out.println(name() + "::exploreNeighborhood finished");
	}
	public String name(){
		return "FirstImprovementOnePointMoveExplorer";
	}
	public void performMove(IVRMove m){
		// DO NOTHING
	}
}
