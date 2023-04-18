package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;
import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util.RandomUtil;
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

public class GreedyOnePointMoveExplorerLimit implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	int K;
	public GreedyOnePointMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F,int K) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
	}
	
	public String name(){
		return "GreedyOnePointMoveExplorerLimit";
	}
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		ArrayList<Integer>listI = RandomUtil.randomKFromN(K, XR.getNbRoutes());
		ArrayList<Integer>listJ = RandomUtil.randomKFromN(K, XR.getNbRoutes());
		for (int i : listI)
		{
			for (int j : listJ) 
			{
				for (Point x = XR.startPoint(i); x != XR.endPoint(i); x = XR.next(x)) {
					for (Point y = XR.startPoint(j); y != XR.endPoint(j); y = XR.next(y)) {
						
						if (XR.checkPerformOnePointMove(x, y)) {
							LexMultiValues eval = F.evaluateOnePointMove(x, y);
							if(eval.lt(bestEval)){
								N.clear();
								N.add(new OnePointMove(mgr, eval, x, y, this));
								bestEval.set(eval);
							} else if (eval.eq(bestEval)) {
								N.add(new OnePointMove(mgr, eval, x, y, this));
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