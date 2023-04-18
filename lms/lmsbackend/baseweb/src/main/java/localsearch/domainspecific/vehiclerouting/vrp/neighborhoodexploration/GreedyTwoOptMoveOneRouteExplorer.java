package localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove1;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMoveOneRoute;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyTwoOptMoveOneRouteExplorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	private boolean firstImprovement = true;
	public GreedyTwoOptMoveOneRouteExplorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		if(firstImprovement && N.hasImprovement()){
			System.out.println(name() + "::exploreNeighborhood, has improvement --> RETURN");
			return;
		}
		
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
				for (Point x = XR.startPoint(i); x != XR.endPoint(i); x = XR.next(x)) {
					for (Point y = XR.next(x); y != XR.endPoint(i); y = XR.next(y)) {
						if (XR.checkPerformTwoOptMoveOneRoute(x, y)) {
							LexMultiValues eval = F.evaluateTwoOptMoveOneRoute(x, y);
							//System.out.println(name() + "::exploreNeighborhood(" + x.ID + "," + y.ID + "), eval = " +
							//eval.toString() + ", bestEval = " + bestEval.toString());
							
							if (eval.lt(bestEval)){
								N.clear();
								N.add(new TwoOptMoveOneRoute(mgr, eval, x, y, this));
								bestEval.set(eval);
							} else if (eval.eq(bestEval)) {
								N.add(new TwoOptMoveOneRoute(mgr, eval, x, y, this));
							}
							if(firstImprovement){
								if(eval.lt(0)) return;
							}
						}
					}
				}
			
		}

	}

	@Override
	public void performMove(IVRMove m) {
		// TODO Auto-generated method stub

	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GreedyTwoOptMoveExplorer";
	}

}
