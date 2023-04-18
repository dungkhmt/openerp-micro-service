package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.KPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyTwoRequestMoveExplorerLimit implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	ArrayList<Point>pickup;
	ArrayList<Point> delivery;
	double p;
	public GreedyTwoRequestMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, ArrayList<Point>pickup, ArrayList<Point> delivery) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.pickup = pickup;
		this.delivery = delivery;
		this.p = 1;
	}
	public String name(){
		return "GreedyTwoRequestMoveExplorerLimit";
	}
	public GreedyTwoRequestMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F,double p, ArrayList<Point>pickup, ArrayList<Point> delivery) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.pickup = pickup;
		this.delivery = delivery;
		this.p = p;
	}
	
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		
		for (int j = 0; j < pickup.size(); ++j) 
		{
			if(Math.random() > p)
				continue;
			Point y1 = pickup.get(j);
			Point y2 = delivery.get(j);
			for (int i =0; i < pickup.size(); ++i) {
				Point x1 = pickup.get(i);
				Point x2 = delivery.get(i);
				ArrayList<Point> x = new ArrayList<>();
				x.add(y1);
				x.add(y2);
				x.add(x1);
				x.add(x2);
				
				ArrayList<Point> y = new ArrayList<>();
				y.add(XR.prev(x1));
				if(x1 == XR.prev(x2))
					y.add(XR.prev(x1));
				else
					y.add(XR.prev(x2));
				y.add(XR.prev(y1));
				if(y1 == XR.prev(y2))
					y.add(XR.prev(y1));
				else
					y.add(XR.prev(y2));
				if (XR.checkPerformKPointsMove(x,y)) {
					LexMultiValues eval = F.evaluateKPointsMove(x, y);
					if (eval.lt(bestEval)){
						N.clear();
						N.add(new KPointsMove(mgr, eval, x, y));
						bestEval.set(eval);
					} else if (eval.eq(bestEval)) {
						N.add(new KPointsMove(mgr, eval, x, y));
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