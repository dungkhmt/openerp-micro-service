package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util.RandomUtil;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.AddTwoPoints;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.KPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OnePointMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.RemoveTwoPoints;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyExchangeRequestWithPeriodTime implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private LexMultiFunctions F;
	int K;
	double p;
	ArrayList<Point>pickup;
	ArrayList<Point> delivery;
	HashMap<Point, Point> pickup2Delivery;
	HashMap<Point, Integer> earliestAllowedArrivalTime;

	public GreedyExchangeRequestWithPeriodTime(VarRoutesVR XR, LexMultiFunctions F,double p, int K, 
												ArrayList<Point>pickup, 
												ArrayList<Point> delivery,
												HashMap<Point, Point> pickup2Delivery,
												HashMap<Point, Integer> earliestAllowedArrivalTime,
												HashMap<Point, Double> scoreReq) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
		this.pickup = pickup;
		this.delivery = delivery;
		this.pickup2Delivery = pickup2Delivery;
		this.earliestAllowedArrivalTime = earliestAllowedArrivalTime;
		this.p = p;
	}
	
	public String name(){
		return "GreedyExchangeRequestWithPeriodTime";
	}
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		ArrayList<Integer> listJ = RandomUtil.randomKFromN(K, XR.getNbRoutes());
		double t = System.currentTimeMillis();
		try{
			PrintWriter out = new PrintWriter("greedyExchangeRequest.txt");
			out.println("ShareARide::exploreNeighborhood: first vio = " + bestEval.toString() + 
					", K = " + listJ.size() + ", p = " + p + ", starting time = " + t);
			out.close();
		}
		catch(Exception e){
			
		}
		for (int j : listJ){
			for (int i : listJ) {
				if(i == j)
					continue;
				System.out.println("Loop: j = " + j + ", i = " + i);
				for (Point x1 = XR.getStartingPointOfRoute(i); x1 != XR.getTerminatingPointOfRoute(i); x1 = XR.next(x1)) {
					if(Math.random() > p){
						continue;
					}
					
					Point x2 = pickup2Delivery.get(x1);
					if(x2 != null){
						for (Point y1 = XR.getStartingPointOfRoute(j); y1 != XR.getTerminatingPointOfRoute(j); y1 = XR.next(y1)) {
							int timeX1 = earliestAllowedArrivalTime.get(x1);
							int timeY1 = earliestAllowedArrivalTime.get(y1);
							Point y2 = pickup2Delivery.get(y1);
							if(y2 != null && timeX1 - 1800 <= timeY1 && timeY1 <= timeX1 + 1800){
								ArrayList<Point> x = new ArrayList<Point>();
								x.add(x1);
								x.add(x2);
								x.add(y1);
								x.add(y2);
								ArrayList<Point> addX = getPositionForInsertion(x1, x2, j);
								ArrayList<Point> addY = getPositionForInsertion(y1, y2, XR.route(x1));
								ArrayList<Point> y = new ArrayList<Point>();
								y.add(addX.get(0));
								y.add(addX.get(1));
								y.add(addY.get(0));
								y.add(addY.get(1));
								if(XR.checkPerformKPointsMove(x, y)){
									LexMultiValues eval = F.evaluateKPointsMove(x, y);
									if(eval.lt(bestEval)){
										N.clear();
										N.add(new KPointsMove(mgr, eval, x, y));
										bestEval.set(eval);
										try{
											PrintWriter out = new PrintWriter(new FileOutputStream("greedyExchangeRequest.txt", true));
											out.println("ShareARide::exploreNeighborhood vio = " + bestEval.toString());
											out.close();
										}
										catch(Exception e){
											
										}
									} else if (eval.eq(bestEval)) {
										N.add(new KPointsMove(mgr, eval, x, y, this));
									}
								}
							}
						}
					}
				}
			}
		}
		try{
			PrintWriter out = new PrintWriter(new FileOutputStream("greedyExchangeRequest.txt", true));
			out.println("ShareARide::exploreNeighborhood the last vio = " + bestEval.toString() 
				+ ", ending time = " + (System.currentTimeMillis() - t));
			out.close();
		}
		catch(Exception e){
			
		}
	}
	
	public ArrayList<Point> getPositionForInsertion(Point x1, Point x2, int j){
		LexMultiValues minEval = null;
		Point prex1 = XR.getStartingPointOfRoute(j);
		Point prex2 = XR.getStartingPointOfRoute(j);
		for (Point y1 = XR.getStartingPointOfRoute(j); y1 != XR.getTerminatingPointOfRoute(j); y1 = XR.next(y1)) {
			for(Point y2 = y1; y2 != XR.getTerminatingPointOfRoute(j); y2 = XR.next(y2)){
				ArrayList<Point> x = new ArrayList<Point>();
				ArrayList<Point> y = new ArrayList<Point>();
				x.add(x1);
				x.add(x2);
				y.add(y1);
				y.add(y2);
				LexMultiValues eval = F.evaluateKPointsMove(x, y);
				if(minEval == null){
					minEval = eval;
					prex1 = y1;
					prex2 = y2;
				}
				else{
					if (eval.lt(minEval)){
						minEval = eval;
						prex1 = y1;
						prex2 = y2;
					}
				}
			}
		}
		ArrayList<Point> output = new ArrayList<Point>();
		output.add(prex1);
		output.add(prex2);
		return output;
	}

	@Override
	public void performMove(IVRMove m){
		// DO NOTHING
	}
}