
/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * start date: 29/05/2016
 */
package localsearch.domainspecific.vehiclerouting.apps.cvrp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.MaxVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;


class MySearch extends GenericLocalSearch{
	public MySearch(VRManager mgr){
		super(mgr);
	}
	public String name(){
		return "MySearch";
	}
	@Override
	public void generateInitialSolution(){
		System.out.println(name() + "::generateInitialSolution.....");
		super.generateInitialSolution();
		//System.exit(-1);
	}
}
public class CVRP {
	private ArcWeightsManager awm;
	private NodeWeightsManager nwm1;
	private NodeWeightsManager nwm2;
	
	private VRManager mgr;
	private VarRoutesVR XR;
	private ArrayList<Point> startPoints;
	private ArrayList<Point> endPoints;
	private ArrayList<Point> clientPoints;
	private ArrayList<Point> allPoints;
	private IFunctionVR[] costRoute;
	private IFunctionVR[] load1Route;
	private IFunctionVR[] load2Route;
	
	private IFunctionVR cost;
	private ConstraintSystemVR CS;
	private LexMultiFunctions F;

	public void readData(String fn) {
		try {
			HashMap<Point, Integer> mPoint2Index = new HashMap<Point, Integer>();
			Scanner in = new Scanner(new File(fn));
			int K;// number of vehicles
			int N;// number of client points
			K = in.nextInt();
			ArrayList<Double> x = new ArrayList<Double>();
			ArrayList<Double> y = new ArrayList<Double>();
			ArrayList<Integer> weight1 = new ArrayList<Integer>();
			ArrayList<Integer> weight2 = new ArrayList<Integer>();
			clientPoints = new ArrayList<Point>();
			startPoints = new ArrayList<Point>();
			endPoints = new ArrayList<Point>();
			allPoints = new ArrayList<Point>();

			for(int k = 0; k < K; k++){
				int id = in.nextInt();
				Point s = new Point(id);
				Point t = new Point(id);
				startPoints.add(s);
				endPoints.add(t);
				allPoints.add(s);
				allPoints.add(t);
				double ix = in.nextDouble();
				double iy = in.nextDouble();
				int iw1 = in.nextInt();
				int iw2 = in.nextInt();
				x.add(ix);
				y.add(iy);
				weight1.add(iw1);
				weight2.add(iw2);
				mPoint2Index.put(s, x.size()-1);
				mPoint2Index.put(t, x.size()-1);
			}
			
			N = in.nextInt();
			for(int n = 0; n < N; n++){
				int id = in.nextInt();
				double ix = in.nextDouble();
				double iy = in.nextDouble();
				int iw1 = in.nextInt();
				int iw2 = in.nextInt();
				Point p = new Point(id);
				x.add(ix);
				y.add(iy);
				weight1.add(iw1);
				weight2.add(iw2);
				clientPoints.add(p);
				allPoints.add(p);
				mPoint2Index.put(p, x.size()-1);
			}
			in.close();
			
			

			awm = new ArcWeightsManager(allPoints);
			nwm1 = new NodeWeightsManager(allPoints);
			nwm2 = new NodeWeightsManager(allPoints);
			
			for(Point p: allPoints){
				int idx = mPoint2Index.get(p);
				nwm1.setWeight(p, -weight1.get(idx));
				nwm2.setWeight(p, -weight2.get(idx));
			}
			for(int i = 0; i < startPoints.size(); i++){
				Point s = startPoints.get(i);
				Point t = endPoints.get(i);
				int idx = mPoint2Index.get(s);
				nwm1.setWeight(s, weight1.get(idx));
				nwm1.setWeight(t, 0);
				nwm2.setWeight(s, weight2.get(idx));
				nwm2.setWeight(t, 0);
			}
			
			for (int i = 0; i < clientPoints.size(); i++) {
				Point pi = clientPoints.get(i);
				int idx = mPoint2Index.get(pi);
				double xi = x.get(idx);			
				double yi = y.get(idx);
				for (int j = 0; j < clientPoints.size(); j++) {
					Point pj = clientPoints.get(j);
					int idxj = mPoint2Index.get(pj);
					double xj = x.get(idxj);
					double yj = y.get(idxj);
					double w = Math.abs(xi - xj) + Math.abs(yi - yj);
					awm.setWeight(pi, pj, w);
				}
			}

			for (int i = 0; i < clientPoints.size(); i++) {
				Point p = clientPoints.get(i);
				int idx = mPoint2Index.get(p);
				double xi = x.get(idx);
				double yi = y.get(idx);
				
				for (int k = 0; k < startPoints.size(); k++) {
					Point s = startPoints.get(k);
					Point t = endPoints.get(k);
					int idxs = mPoint2Index.get(s);
					double xk = x.get(idxs);
					double yk = y.get(idxs);
					double w = Math.abs(xi - xk) + Math.abs(yi - yk);
					awm.setWeight(p, s, w);
					awm.setWeight(p, t, 0);
					awm.setWeight(s, p, w);
					awm.setWeight(t, p, 0);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stateModel() {
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		for (int k = 0; k < startPoints.size(); k++) {
			XR.addRoute(startPoints.get(k), endPoints.get(k));
		}
		for (Point p : clientPoints) {
			XR.addClientPoint(p);
		}

		CS = new ConstraintSystemVR(mgr);
		AccumulatedWeightNodesVR awn1 = new AccumulatedWeightNodesVR(XR, nwm1);
		AccumulatedWeightNodesVR awn2 = new AccumulatedWeightNodesVR(XR, nwm2);
		load1Route = new IFunctionVR[XR.getNbRoutes()];
		load2Route = new IFunctionVR[XR.getNbRoutes()];
		for(int k = 0; k < XR.getNbRoutes(); k++){
			load1Route[k] = new AccumulatedNodeWeightsOnPathVR(awn1, XR.endPoint(k+1));
			load2Route[k] = new AccumulatedNodeWeightsOnPathVR(awn2, XR.endPoint(k+1));
			double L1 = nwm1.getWeight(XR.startPoint(k+1));
			double L2 = nwm2.getWeight(XR.startPoint(k+1));
			//CS.post(new Leq(load1Route[k],L1));
			//CS.post(new Leq(load2Route[k],L2));
			CS.post(new Leq(0,load1Route[k]));
			CS.post(new Leq(0,load2Route[k]));
		}
		
		AccumulatedWeightEdgesVR awe = new AccumulatedWeightEdgesVR(XR, awm);
		costRoute = new IFunctionVR[startPoints.size()];
		for (int k = 0; k < XR.getNbRoutes(); k++) {
			costRoute[k] = new AccumulatedEdgeWeightsOnPathVR(awe,
					XR.endPoint(k + 1));
		}
		cost = new MaxVR(costRoute);
		F = new LexMultiFunctions();
		F.add(CS);
		F.add(cost);

		mgr.close();

		System.out.println("stateModel, XR = " + XR.toString());
	}

	public void print() {
		System.out.println(XR.toString());
		for (int k = 1; k <= XR.getNbRoutes(); k++){
			System.out.println("cost[" + k + "] = "
					+ costRoute[k - 1].getValue() + ", load1 = " + load1Route[k-1].getValue() + 
					", load2 = " + load2Route[k-1].getValue() + ", capacity1 = " + nwm1.getWeight(XR.startPoint(k)) + 
					", capacity2 = " + nwm2.getWeight(XR.startPoint(k)));
			
		}
		System.out.println("Cost = " + cost.getValue());
	}

	public void search(int timeLimit){
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		NE.add(new GreedyOrOptMove1Explorer(XR, F));
		NE.add(new GreedyOrOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove1Explorer(XR, F));
		NE.add(new GreedyThreeOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove3Explorer(XR, F));
		NE.add(new GreedyThreeOptMove4Explorer(XR, F));
		NE.add(new GreedyThreeOptMove5Explorer(XR, F));
		NE.add(new GreedyThreeOptMove6Explorer(XR, F));
		NE.add(new GreedyThreeOptMove7Explorer(XR, F));
		NE.add(new GreedyThreeOptMove8Explorer(XR, F));
		NE.add(new GreedyTwoOptMove1Explorer(XR, F));
		NE.add(new GreedyTwoOptMove2Explorer(XR, F));
		NE.add(new GreedyTwoOptMove3Explorer(XR, F));
		NE.add(new GreedyTwoOptMove4Explorer(XR, F));
		NE.add(new GreedyTwoOptMove5Explorer(XR, F));
		NE.add(new GreedyTwoOptMove6Explorer(XR, F));
		NE.add(new GreedyTwoOptMove7Explorer(XR, F));
		NE.add(new GreedyTwoOptMove8Explorer(XR, F));
		// NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
		NE.add(new GreedyCrossExchangeMoveExplorer(XR, F));
		// NE.add(new GreedyAddOnePointMoveExplorer(XR, F));

		MySearch se = new MySearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);

		se.search(10, timeLimit);
		print();

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CVRP A = new CVRP();
		A.readData("data/cvrp/Test_instance_bug.txt");
		A.stateModel();
		A.search(1);
	}

}
