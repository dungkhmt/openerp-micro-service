package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util.RandomUtil;
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

public class GreedyOneRequestMoveExplorerLimit implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	//private ISearch search;
	private LexMultiFunctions F;
	//private LexMultiValues bestValue;
	int K;
	double p;
	ArrayList<Point>pickup;
	ArrayList<Point> delivery;
	public GreedyOneRequestMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, int K, 
			ArrayList<Point>pickup, 
			ArrayList<Point> delivery) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
		this.pickup = pickup;
		this.delivery = delivery;
		this.p = 1;
	}
	public GreedyOneRequestMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F,double p, int K, 
												ArrayList<Point>pickup, 
												ArrayList<Point> delivery,
												HashMap<Point, Double> scoreReq) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
		this.pickup = pickup;
		this.delivery = delivery;
		this.p = p;
	}
	
	public String name(){
		return "GreedyOneRequestMoveExplorerLimit";
	}
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		ArrayList<Integer>listJ = RandomUtil.randomKFromN(K, XR.getNbRoutes());
		
		for (int j : listJ) 
		{
			for (int i =0; i < pickup.size(); ++i) {
				if(Math.random() > p)
					continue;
				Point x1 = pickup.get(i);
				Point x2 = delivery.get(i);
				ArrayList<Point> x = new ArrayList<>();
				x.add(x1);
				x.add(x2);
				for (Point y1 = XR.getStartingPointOfRoute(j); y1 != XR.getTerminatingPointOfRoute(j); y1 = XR.next(y1)) {
					for(Point y2 = y1; y2 != XR.endPoint(j); y2 = XR.next(y2))
					{
						ArrayList<Point> y = new ArrayList<>();
						y.add(y1);
						y.add(y2);
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
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//get minimal violation
//		ArrayList<Integer>listJ = RandomUtil.randomKFromN(K, XR.getNbRoutes());
//		
//		for (int j : listJ) 
//		{
//			for (int i =0; i < pickup.size(); ++i) {
//				if(Math.random() > p)
//					continue;
//				Point x1 = pickup.get(i);
//				Point x2 = delivery.get(i);
//				ArrayList<Point> x = new ArrayList<>();
//				x.add(x1);
//				x.add(x2);
//				for (Point y1 = XR.getStartingPointOfRoute(j); y1 != XR.getTerminatingPointOfRoute(j); y1 = XR.next(y1)) {
//					for(Point y2 = y1; y2 != XR.endPoint(j); y2 = XR.next(y2))
//					{
//						ArrayList<Point> y = new ArrayList<>();
//						y.add(y1);
//						y.add(y2);
//						if (XR.checkPerformKPointsMove(x,y)) {
//							LexMultiValues eval = F.evaluateKPointsMove(x, y);
//							if (eval.lt(bestEval)){
//								N.clear();
//								N.add(new KPointsMove(mgr, eval, x, y));
//								bestEval.set(eval);
//							} else if (eval.eq(bestEval)) {
//								N.add(new KPointsMove(mgr, eval, x, y));
//							}
//						}
//					}
//					
//				}
//				
//			}
//		}
	}
	

	@Override
	public void performMove(IVRMove m){
		// DO NOTHING
	}
}