package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.MaxVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyAddOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyKPointsMoveExplorer;
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
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;
/*
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementKPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementOrOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementThreeOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementThreeOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementThreeOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementThreeOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement.FirstImprovementTwoOptMove8Explorer;
*/

import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementKPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementOrOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementThreeOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementThreeOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementThreeOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementThreeOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.FirstImprovementTwoOptMove8Explorer;


class PairPoints{
	public Point x;
	public Point y;
	public PairPoints(Point x, Point y){
		this.x = x; this.y = y;
	}
}

public class MinMaxVRP {

	private ArcWeightsManager awm;
	private VRManager mgr;
	private VarRoutesVR XR;
	private ArrayList<Point> startPoints;
	private ArrayList<Point> endPoints;
	private ArrayList<Point> clientPoints;
	private ArrayList<Point> allPoints;
	private IFunctionVR[] costRoute;
	private IFunctionVR cost;
	private LexMultiFunctions F;

	private MMSearch se;
	public void readData(String fn) {
		try {
			Scanner in = new Scanner(new File(fn));
			int K;// number of vehicles
			K = in.nextInt();
			ArrayList<Integer> x = new ArrayList<Integer>();
			ArrayList<Integer> y = new ArrayList<Integer>();
			while (true) {
				int id = in.nextInt();
				if (id == -1)
					break;
				int cx = in.nextInt();
				int cy = in.nextInt();
				x.add(cx);
				y.add(cy);
			}
			in.close();
			clientPoints = new ArrayList<Point>();
			startPoints = new ArrayList<Point>();
			endPoints = new ArrayList<Point>();
			allPoints = new ArrayList<Point>();

			for (int i = 1; i < x.size(); i++) {
				Point p = new Point(i);
				clientPoints.add(p);
				allPoints.add(p);
			}
			int id = x.size() - 1;
			for (int k = 0; k < K; k++) {
				id++;
				Point s = new Point(id);
				startPoints.add(s);
				allPoints.add(s);
				id++;
				Point t = new Point(id);
				endPoints.add(t);
				allPoints.add(t);
			}

			awm = new ArcWeightsManager(allPoints);

			for (int i = 0; i < clientPoints.size(); i++) {
				Point pi = clientPoints.get(i);
				int xi = x.get(i + 1);
				int yi = y.get(i + 1);
				for (int j = 0; j < clientPoints.size(); j++) {
					Point pj = clientPoints.get(j);
					int xj = x.get(j + 1);
					int yj = y.get(j + 1);
					int w = Math.abs(xi - xj) + Math.abs(yi - yj);
					awm.setWeight(pi, pj, w);
				}
			}

			for (int i = 0; i < clientPoints.size(); i++) {
				int xi = x.get(i + 1);
				int yi = y.get(i + 1);
				Point p = clientPoints.get(i);

				for (int k = 0; k < startPoints.size(); k++) {
					Point s = startPoints.get(k);
					Point t = endPoints.get(k);
					int xk = x.get(0);
					int yk = y.get(0);
					int w = Math.abs(xi - xk) + Math.abs(yi - yk);
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

		AccumulatedWeightEdgesVR awe = new AccumulatedWeightEdgesVR(XR, awm);
		costRoute = new IFunctionVR[startPoints.size()];
		for (int k = 0; k < XR.getNbRoutes(); k++) {
			costRoute[k] = new AccumulatedEdgeWeightsOnPathVR(awe,
					XR.endPoint(k + 1));
		}
		cost = new MaxVR(costRoute);
		F = new LexMultiFunctions();
		F.add(cost);

		mgr.close();

		System.out.println("stateModel, XR = " + XR.toString());
	}

	public void print() {
		System.out.println(XR.toString());
		for (int k = 1; k <= XR.getNbRoutes(); k++)
			System.out.println("cost[" + k + "] = "
					+ costRoute[k - 1].getValue());
		System.out.println("Cost = " + cost.getValue());
	}

	public void initGreedy() {
		MMSearch S = new MMSearch(mgr);
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyAddOnePointMoveExplorer(XR, F));
		S.setNeighborhoodExplorer(NE);
		S.setObjectiveFunction(F);

		S.search(10000, 60000);

		print();
		System.exit(-1);
	}

	public void initSolution() {
		java.util.Random R = new java.util.Random();
		for (Point p : clientPoints) {
			ArrayList<Point> L = XR.collectCurrentClientAndStartPointsOnRoute();
			Point q = L.get(R.nextInt(L.size()));
			mgr.performAddOnePoint(p, q);
		}
	}

	public void initSolutionGreedy() {
		HashSet<Point> S = new HashSet<Point>();
		for (Point p : XR.getClientPoints()) {
			S.add(p);
		}
		while (S.size() > 0) {
			LexMultiValues eval = new LexMultiValues();
			eval.fill(1, CBLSVR.MAX_INT);
			Point sel_q = null;
			Point sel_p = null;
			for (Point p : S) {
				ArrayList<Point> L = XR
						.collectCurrentClientAndStartPointsOnRoute();
				for (Point q : L) {
					LexMultiValues e = F.evaluateAddOnePoint(p, q);
					if (e.lt(eval)) {
						sel_q = q;
						sel_p = p;
						eval = e;
					}
				}
				
			}
			mgr.performAddOnePoint(sel_p, sel_q);
			S.remove(sel_p);
		}
	}

	public void initBestSolution(String fn) {
		try {
			Scanner in = new Scanner(new File(fn));
			int K = in.nextInt();
			for (int k = 0; k < K; k++) {
				ArrayList<Integer> S = new ArrayList<Integer>();
				while (true) {
					int v = in.nextInt();
					if (v == -1)
						break;
					S.add(v);
				}
				Point s = XR.startPoint(k + 1);
				Point p = clientPoints.get(S.get(0) - 1);
				mgr.performAddOnePoint(p, s);
				for (int i = 1; i < S.size(); i++) {
					Point q = clientPoints.get(S.get(i) - 1);
					mgr.performAddOnePoint(q, p);
					p = q;
				}

				int d = 0;
				for (Point x = XR.startPoint(k + 1); x != XR.endPoint(k + 1); x = XR
						.next(x)) {
					Point nx = XR.next(x);
					double dx = awm.getDistance(x, nx);
					d += dx;
					System.out.println("d[" + x.ID + "," + nx.ID + "] = " + dx
							+ ", total d = " + d);
				}
				System.out.println("------------------------------");
			}

			print();

			System.exit(-1);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void searchFirstImprovement(int timeLimit) {
		HashSet<Point> S = new HashSet<Point>();
		for(Point p: XR.getClientPoints()) S.add(p);
		
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new FirstImprovementOnePointMoveExplorer(XR, F));
		NE.add(new FirstImprovementOrOptMove1Explorer(XR, F));
		NE.add(new FirstImprovementOrOptMove2Explorer(XR, F));
		NE.add(new FirstImprovementThreeOptMove1Explorer(XR, F));
		NE.add(new FirstImprovementThreeOptMove3Explorer(XR, F));
		NE.add(new FirstImprovementThreeOptMove5Explorer(XR, F));
		NE.add(new FirstImprovementThreeOptMove7Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove1Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove2Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove3Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove4Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove5Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove6Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove7Explorer(XR, F));
		NE.add(new FirstImprovementTwoOptMove8Explorer(XR, F));
		NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
		NE.add(new FirstImprovementCrossExchangeMoveExplorer(XR, F));
		// NE.add(new GreedyAddOnePointMoveExplorer(XR, F));
		//NE.add(new FirstImprovementKPointsMoveExplorer(XR, F, 2, S));
		
		se = new MMSearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);

		se.search(100000, timeLimit);
		print();
	}

	public void search(int timeLimit) {
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

		MMSearch se = new MMSearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);

		se.search(100000, timeLimit);
		print();

	}

	public void runBatch(int nbRuns, int timeLimit){
		double[] best = new double[nbRuns];
		double[] t = new double[nbRuns];
		double min = CBLSVR.MAX_INT;
		double max = 1-min;
		double avg = 0;
		double avg_t = 0;
		for(int run = 0; run < nbRuns; run++){
			searchFirstImprovement(timeLimit);
			best[run] = cost.getValue();
			t[run] = se.getTimeToBest();
			avg += best[run];
			avg_t += t[run];
			min = min < best[run] ? min : best[run];
			max = max > best[run] ? max : best[run];
		}
		for(int run = 0; run < nbRuns; run++){
			System.out.println("Run " + run + ", cost = " + best[run] + ", time = " + t[run]);
		}
		avg = avg * 1.0/nbRuns;
		avg_t = avg_t*1.0/nbRuns;
		System.out.println("min = " + min + ", max = " + max + ", avg = " + avg + ", avg_time = " + avg_t);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinMaxVRP A = new MinMaxVRP();
		A.readData("data/MinMaxVRP/whizzkids96.txt");
		A.stateModel();
		A.runBatch(10,3600);
		//A.search();
		//A.searchFirstImprovement();
		// A.initBestSolution("output\\MinMaxVRP\\best-sol-whizzkids96.txt");
	}

}
