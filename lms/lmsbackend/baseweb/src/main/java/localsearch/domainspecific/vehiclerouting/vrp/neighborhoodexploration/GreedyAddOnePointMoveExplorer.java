package localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration;

import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.AddOnePoint;
import localsearch.domainspecific.vehiclerouting.vrp.moves.CrossExchangeMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyAddOnePointMoveExplorer implements INeighborhoodExplorer {

	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	private boolean firstImprovement = true;
	
	public GreedyAddOnePointMoveExplorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		
	}
	public GreedyAddOnePointMoveExplorer(VarRoutesVR XR, LexMultiFunctions F, boolean firstImprovement) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.firstImprovement = firstImprovement;
	}
	
	
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		if(firstImprovement && N.hasImprovement()){
			System.out.println(name() + "::exploreNeighborhood, has improvement --> RETURN");
			return;
		}
		
		// TODO Auto-generated method stub
		for (Point x : XR.getClientPoints()) {
			for (Point y : XR.getAllPoints()) {
				if (XR.checkPerformAddOnePoint(x, y)) {
					LexMultiValues eval = F.evaluateAddOnePoint(x, y);
					if (eval.lt(bestEval)) {
						N.clear();
						N.add(new AddOnePoint(mgr, eval, x, y, this));
						bestEval.set(eval);
					} else if (eval.eq(bestEval)) {
						N.add(new AddOnePoint(mgr, eval, x, y, this));
					}
					if(firstImprovement){
						if(eval.lt(0)) return;
					}
					
				}
			}
		}
	}

	public String name(){
		return "GreedyAddOnePointMoveExplorer";
	}
	public void performMove(IVRMove m) {
		// TODO Auto-generated method stub

	}

}
