package localsearch.domainspecific.vehiclerouting.apps.mctp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

// import sun.misc.Cleaner;
import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.ValueRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.IndexOnRoute;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.NumberCoveredPoints;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.moves.AddOnePoint;
import localsearch.domainspecific.vehiclerouting.vrp.moves.AddRemovePoints;
import localsearch.domainspecific.vehiclerouting.vrp.moves.CrossExchangeMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OnePointMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OrOptMove1;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OrOptMove2;
import localsearch.domainspecific.vehiclerouting.vrp.moves.RemoveOnePoint;
import localsearch.domainspecific.vehiclerouting.vrp.moves.ThreeOptMove1;
import localsearch.domainspecific.vehiclerouting.vrp.moves.ThreeOptMove3;
import localsearch.domainspecific.vehiclerouting.vrp.moves.ThreeOptMove5;
import localsearch.domainspecific.vehiclerouting.vrp.moves.ThreeOptMove7;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove1;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove2;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove5;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyAddOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyKPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;
import localsearch.domainspecific.vehiclerouting.vrp.utils.ScannerInput;

public class MultiVehicleTourCovering {

	public VarRoutesVR XR;
	public VRManager mgr;
	public LexMultiFunctions f;
	public LexMultiValues bestValue;
	public ValueRoutesVR bestSolution;
	public NumberCoveredPoints ncp;
	
	public int nbNodes;
	public int nbRoutes;
	public int nbObligatoryNodes;
	public int nbNodesPerRoute;
	public int nbCustomers;
	public int[][] coveringMatrix;
	public double[][] cost;
	public int[] mark;
	
	public NodeWeightsManager nwm;
	public ArcWeightsManager  awm;
	public ArrayList<Point> points;
	public ArrayList<Point> startPoints;
	public ArrayList<Point> endPoints;
	public ArrayList<Point> clientPoints;
	public ArrayList<Point> coveringPoints;
	public HashSet<Point> mandatory;
	SecureRandom rand;
	
	public MultiVehicleTourCovering(int nbNodes, int nbObligatoryNodes, int nbCustomers, int nbNodesPerRoute, 
			double[][] cost, int[][] coveringMatrix) {
		this.nbNodes = nbNodes;
		this.nbObligatoryNodes = nbObligatoryNodes;
		this.nbNodesPerRoute = nbNodesPerRoute;
		this.nbCustomers = nbCustomers;
		this.coveringMatrix = coveringMatrix;
		this.cost = cost;
		
		/*
		int[] s = new int[]{14,8,13,22};
		int[] c = new int[nbCustomers];
		for(int i = 0; i < nbCustomers; i++) c[i] = 0;
		for(int i = 0; i < s.length; i++){
			for(int j = 0; j < nbCustomers; j++)
				if(coveringMatrix[s[i]-nbObligatoryNodes][j] == 1)
					c[j] = 1;
		}
		for(int j = 0; j < nbCustomers; j++){
			if(c[j] == 0) System.out.println("FAILED.......");
			System.out.print(c[j]);
		}
		System.exit(-1);
		*/
	}
	
	public void stateModel() { 
		points = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		clientPoints = new ArrayList<Point>();
		coveringPoints = new ArrayList<Point>();
		nbRoutes = (nbNodes - 1) / nbNodesPerRoute + 1;
		mandatory = new HashSet<Point>();
		
		for(int k = 1; k <= nbRoutes; k++){
			Point sp = new Point(0,0,0);
			Point tp = new Point(0,0,0);
			startPoints.add(sp);
			endPoints.add(tp);
			points.add(sp);
			points.add(tp);
		}
		for (int i = 1; i < nbNodes; i++) {
			Point p = new Point(i,0,0);
			clientPoints.add(p);
			points.add(p);
			if (i >= nbObligatoryNodes) {
				coveringPoints.add(p);
			}else{
				mandatory.add(p);
			}
		}
		
		
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		for (int i = 0; i < nbRoutes; i++) {
			XR.addRoute(startPoints.get(i), endPoints.get(i));
		}
		for (int i = 0; i < clientPoints.size(); i++) {
			XR.addClientPoint(clientPoints.get(i));
		}
		
		ArcWeightsManager awm = new ArcWeightsManager(points);
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			for (int j = 0; j < points.size(); j++) {
				Point q = points.get(j); 
				awm.setWeight(p, q, cost[p.getID()][q.getID()]);
			}
		}
		
		ConstraintSystemVR cs = new ConstraintSystemVR(mgr);
		for (Point p : endPoints) {
			cs.post(new Leq(new IndexOnRoute(XR, p), nbNodesPerRoute + 1));
		}
		HashMap<Point, HashSet<Integer>> cover = new HashMap<Point, HashSet<Integer>>();
		for (Point p : points) {
			//System.out.println(clientPoints.get(i).getID() + " " + nbObligatoryNodes);
			int k = p.getID() - nbObligatoryNodes;
			cover.put(p, new HashSet<Integer>());
			if (k >= 0) {
				for (int j = 0; j < nbCustomers; j++) {
					if (coveringMatrix[k][j] == 1) {
						cover.get(p).add(j);
					}
				}
			}
		}
		cs.post(new Leq(nbCustomers, new NumberCoveredPoints(XR, cover)));
		
		f = new LexMultiFunctions();
		f.add(new ConstraintViolationsVR(cs));
		f.add(new TotalCostVR(XR, awm));
		
		mgr.close();
		
		//System.out.println("model.close, XR = " + XR.toString()); System.exit(-1);
	}
	
	public String name(){
		return "MultiVehicleTourCovering";
	}
	public void resetSolution(){
		while(true){
			ArrayList<Point> L = XR.collectClientPointsOnRoutes();
			if(L.size() == 0) break;
			mgr.performRemoveOnePoint(L.get(0));
		}
	}
	public void initSolution() {
		/*
		System.out.println(name() + "::initSolution, clientPoints = " + clientPoints.size() + ", nbObligatoryNodes = " + nbObligatoryNodes);
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			Point s = XR.getStartingPointOfRoute(k);
			Point t = XR.getTerminatingPointOfRoute(k);
			System.out.println(name() + "::initSolution, rout " + k + ", s = " + s.ID + "," + XR.getIndex(s) + ", t = " +
			t.ID + "," + XR.getIndex(t));
		}
		for(int i = 0; i < clientPoints.size(); i++){
			Point p = clientPoints.get(i);
			System.out.println(name() + "::initSolution, client point " + p.ID + ", idx = " + XR.getIndex(p));
		}
		*/
		resetSolution();
		rand = new SecureRandom();
		for (Point x : clientPoints) {
			Point y = null;
			ArrayList<Point> L = XR.collectClientPointsOnRoutes();
			//System.out.println(name() + "::initSolution, XR = " + XR.toString() + ", L = " + L);
			if(L.size() == 0) y = XR.startPoint(rand.nextInt(XR.getNbRoutes())+1);
			else{
				int choice = rand.nextInt(2);
				if(choice == 0)
					y = L.get(rand.nextInt(L.size()));
				else 
					y = XR.startPoint(rand.nextInt(XR.getNbRoutes())+1);
			}
			
			mgr.performAddOnePoint(x, y);
			//System.out.println(name() + "::initSolution, addOnePoint(" + x.ID + "," + y.ID + "), XR = " + XR.toString());
			
			/*
			if (x.getID() < nbObligatoryNodes) {
				Point y = points.get(rand.nextInt(points.size()));
				while (!XR.checkPerformAddOnePoint(x, y)) {
					y = points.get(rand.nextInt(points.size()));
				}
				mgr.performAddOnePoint(x, y);
				//System.out.println("initSolution, after AddOnePoint(" + x.ID + "," + y.ID + "), XR = " + XR.toString());
			}
			*/
		}
		//System.exit(-1);
	}
	
	public void updateBest() {
		bestValue.set(f.getValues());
		bestSolution.store();
	}
	
	public void restart() {
		System.out.println(name() + "::restart................................+++++++++++++++++++++++++++++++");
		initSolution();
		//System.exit(-1);
		/*
		for (Point x : clientPoints) {
			if (x.getID() >= nbObligatoryNodes && XR.checkPerformRemoveOnePoint(x)) {
				if (rand.nextInt(2) == 0) {
					mgr.performRemoveOnePoint(x);
				}
			}
		}
		*/
		if(f.getValues().lt(bestValue)){
			updateBest();
		}
	}
	
	public double search(int maxIter, int timeLimit) {
		initSolution();
		System.out.println(XR);
		
		Neighborhood N = new Neighborhood(mgr);
		bestValue = new LexMultiValues(f.getValues());
		bestSolution = new ValueRoutesVR(XR);
		
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		
		
		NE.add(new GreedyOnePointMoveExplorer(XR, f));
		/*
		NE.add(new GreedyOrOptMove1Explorer(XR, f));
		NE.add(new GreedyOrOptMove2Explorer(XR, f));
		NE.add(new GreedyThreeOptMove1Explorer(XR, f));
		NE.add(new GreedyThreeOptMove3Explorer(XR, f));
		NE.add(new GreedyThreeOptMove5Explorer(XR, f));
		NE.add(new GreedyThreeOptMove7Explorer(XR, f));
		NE.add(new GreedyTwoOptMove1Explorer(XR, f));
		NE.add(new GreedyTwoOptMove5Explorer(XR, f));
		NE.add(new GreedyTwoPointsMoveExplorer(XR, f));
		NE.add(new GreedyCrossExchangeMoveExplorer(XR, f));
		NE.add(new GreedyAddOnePointMoveExplorer(XR, f));
		*/
		
		//NE.add(new GreedyKPointsMoveExplorer(XR, f, 2, mandatory));
		
		int nic = 0;
		int currentIter = 0;
		int maxStable = 10;
		double t0 = System.currentTimeMillis();
		System.out.println("::search, init bestValue = " + bestValue.toString());
		while (currentIter < maxIter) {
			double t = System.currentTimeMillis() - t0;
			if (t  > timeLimit)
				break;
			LexMultiValues bestEval = new LexMultiValues();
			bestEval.fill(f.size(), CBLSVR.MAX_INT);
			N.clear();
			for (INeighborhoodExplorer ne : NE) {
				ne.exploreNeighborhood(N, bestEval);
			}
			
			/*
			removeOnePointMoveExplorer(N, bestEval);
			addRemovePointsMoveExplorer(N, bestEval);
			*/
			
			if (N.hasMove()) { 
				IVRMove m = N.getAMove();
				//System.out.println(XR);
				LexMultiValues oldf = f.getValues();

				m.move();
				
				LexMultiValues newf = f.getValues();
				//System.out.println(XR);
				for (int i = 0; i < oldf.size(); i++) {
					if (Math.abs(oldf.get(i) + bestEval.get(i) - newf.get(i)) > 1e-6) {
						System.out.println("Error" + oldf + " " + bestEval + " " + newf);
						System.exit(-1);
					}
				}
				t = t*0.001;
				System.out.println("::search, step " + currentIter + ", F = " + f.getValues().toString() + 
						", best = " + bestValue.toString() + ", time = " + t);
				if(f.getValues().lt(bestValue)){
					updateBest();
				}else{
					nic++;
					if(nic > maxStable){
						restart();
						nic = 0;
					}
				}
			} else {
				System.out.println("::search --> no move available, break");
				break;
			}
			// System.out.println(obj.toString());

			currentIter++;
		}
		System.out.println(bestValue + "\n" + bestSolution);
		return bestValue.get(1);
	}
	public double searchVNS(int maxNeighbor, int maxIter, int timeLimit) {
		initSolution();
		System.out.println(XR);
		
		Neighborhood N = new Neighborhood(mgr);
		bestValue = new LexMultiValues(f.getValues());
		bestSolution = new ValueRoutesVR(XR);
		
		/*
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		
		
		NE.add(new GreedyOnePointMoveExplorer(XR, f));
		NE.add(new GreedyOrOptMove1Explorer(XR, f));
		NE.add(new GreedyOrOptMove2Explorer(XR, f));
		NE.add(new GreedyThreeOptMove1Explorer(XR, f));
		NE.add(new GreedyThreeOptMove3Explorer(XR, f));
		NE.add(new GreedyThreeOptMove5Explorer(XR, f));
		NE.add(new GreedyThreeOptMove7Explorer(XR, f));
		NE.add(new GreedyTwoOptMove1Explorer(XR, f));
		NE.add(new GreedyTwoOptMove5Explorer(XR, f));
		NE.add(new GreedyTwoPointsMoveExplorer(XR, f));
		NE.add(new GreedyCrossExchangeMoveExplorer(XR, f));
		NE.add(new GreedyAddOnePointMoveExplorer(XR, f));
		
		
		NE.add(new GreedyKPointsMoveExplorer(XR, f, 2, mandatory));
		*/
		
		mandatory.clear();
		
		GreedyKPointsMoveExplorer[] gke = new GreedyKPointsMoveExplorer[maxNeighbor];
		for(int i = 0; i < gke.length; i++)
			gke[i] = new GreedyKPointsMoveExplorer(XR,f,i+1,mandatory);
		
		int nic = 0;
		int currentIter = 0;
		int maxStable = 500;
		double t0 = System.currentTimeMillis();
		System.out.println("::search, init bestValue = " + bestValue.toString());
		LexMultiValues zeros = new LexMultiValues(0,0);
		while (currentIter < maxIter) {
			double t = System.currentTimeMillis() - t0;
			if (t  > timeLimit)
				break;
			LexMultiValues bestEval = new LexMultiValues();
			bestEval.fill(f.size(), CBLSVR.MAX_INT);
		
			int k = 0; 
			while(k < gke.length){
				System.out.println(name() + "::searchVNS, explore gke["+ k + "], start....");
				gke[k].exploreNeighborhood(N, bestEval);
				System.out.println(name() + "::searchVNS, explore gke["+ k + "], finished, bestEval = " + bestEval.toString());
				if(bestEval.lt(zeros)) break;
				k++;
			}
			if(k >= gke.length){
				System.out.println(name() + "::searchVNS, exceed largest neighbor --> restart...");
				restart(); nic = 0; continue;
			}
			
			/*
			for (INeighborhoodExplorer ne : NE) {
				ne.exploreNeighborhood(N, bestEval);
			}
			
			
			removeOnePointMoveExplorer(N, bestEval);
			addRemovePointsMoveExplorer(N, bestEval);
			*/
			
			if (N.hasMove()) { 
				IVRMove m = N.getAMove();
				//System.out.println(XR);
				LexMultiValues oldf = f.getValues();

				m.move();
				
				LexMultiValues newf = f.getValues();
				//System.out.println(XR);
				for (int i = 0; i < oldf.size(); i++) {
					if (Math.abs(oldf.get(i) + bestEval.get(i) - newf.get(i)) > 1e-6) {
						System.out.println("Error" + oldf + " " + bestEval + " " + newf);
						System.exit(-1);
					}
				}
				t = t*0.001;
				System.out.println("::search, step " + currentIter + ", F = " + f.getValues().toString() + 
						", best = " + bestValue.toString() + ", time = " + t);
				if(f.getValues().lt(bestValue)){
					updateBest();
				}else{
					nic++;
					if(nic > maxStable){
						System.out.println(name() + "::searchVNS, nic = " + nic + " > maxStable = " + maxStable + " --> restart");
						restart();
						nic = 0;
					}
				}
			} else {
				System.out.println("::search --> no move available, break");
				break;
			}
			// System.out.println(obj.toString());

			currentIter++;
		}
		System.out.println(bestValue + "\n" + bestSolution);
		return bestValue.get(1);
	}
		
	public void removeOnePointMoveExplorer(Neighborhood N, LexMultiValues bestEval) {
		for (Point x : clientPoints) {
			if (x.getID() >= nbObligatoryNodes && XR.checkPerformRemoveOnePoint(x)) {
				LexMultiValues eval = f.evaluateRemoveOnePoint(x);
				if (eval.lt(bestEval)) {
					N.clear();
					N.add(new RemoveOnePoint(mgr, eval, x));
					bestEval.set(eval);
				} else if (eval.eq(bestEval)) {
					N.add(new RemoveOnePoint(mgr, eval, x));
				}
			}
		}
	}
	
	public void addRemovePointsMoveExplorer(Neighborhood N, LexMultiValues bestEval) {
		for (Point x : clientPoints) {
			if (x.getID() >= nbObligatoryNodes && XR.checkPerformRemoveOnePoint(x)) {
				for (Point y : clientPoints) {
					for (Point z : points) {
						if (XR.checkPerformAddRemovePoints(x, y, z)) {
							LexMultiValues eval = f.evaluateAddRemovePoints(x, y, z);
							if (eval.lt(bestEval)) {
								N.clear();
								N.add(new AddRemovePoints(mgr, eval, x, y, z));
								bestEval.set(eval);
							} else if (eval.eq(bestEval)) {
								N.add(new AddRemovePoints(mgr, eval, x, y, z));
							}
						}
					}
				}
			}
		}
	}
	
	public static void runBatch(int timeLimit){
		try{
//		ScannerInput fi = new ScannerInput("data/VehicleRouting/mCTP/result.txt");
//		String[] s = new String[104 * 3];
//		for (int i = 0; i < 104; i++) {
//			s[i] = fi.readString();
//			s[i + 104] = fi.readString();
//			s[i + 2 * 104] = fi.readString();
//		}
//		for (int i = 0; i < 3; i++) {
//			for (int j = 0; j < 104; j++) {
//				System.out.println(s[j + i * 104]);
//			}
//			System.out.println("---------");
//		}

		/*
		File f = new File("data/VehicleRouting/mCTP/");
		String[] nameFile = f.list();
		Arrays.sort(nameFile);
		for (int i = 0; i < nameFile.length; i++) {
			System.out.println("\"" + nameFile[i] + "\",");		}
		System.exit(-1);
		*/
		
		String[] nameFile = {"A1-1-25-75-4.ctp",
"A1-1-25-75-5.ctp",
"A1-1-25-75-6.ctp",
"A1-1-25-75-8.ctp",
"A1-1-50-50-4.ctp",
"A1-1-50-50-5.ctp",
"A1-1-50-50-6.ctp",
"A1-1-50-50-8.ctp",
"A1-10-50-50-4.ctp",
"A1-10-50-50-5.ctp",
"A1-10-50-50-6.ctp",
"A1-10-50-50-8.ctp",
"A1-5-25-75-4.ctp",
"A1-5-25-75-5.ctp",
"A1-5-25-75-6.ctp",
"A1-5-25-75-8.ctp",
//"A2-1-100-100-4.ctp",
//"A2-1-100-100-5.ctp",
//"A2-1-100-100-6.ctp",
//"A2-1-100-100-8.ctp",
"A2-1-50-150-4.ctp",
"A2-1-50-150-5.ctp",
"A2-1-50-150-6.ctp",
"A2-1-50-150-8.ctp",
"A2-10-50-150-4.ctp",
"A2-10-50-150-5.ctp",
"A2-10-50-150-6.ctp",
"A2-10-50-150-8.ctp",
//"A2-20-100-100-4.ctp",
//"A2-20-100-100-5.ctp",
//"A2-20-100-100-6.ctp",
//"A2-20-100-100-8.ctp",
//"A2-25-100-100-4.ctp",
//"A2-25-100-100-5.ctp",
//"A2-25-100-100-6.ctp",
//"A2-25-100-100-8.ctp",
"B1-1-25-75-4.ctp",
"B1-1-25-75-5.ctp",
"B1-1-25-75-6.ctp",
"B1-1-25-75-8.ctp",
"B1-1-50-50-4.ctp",
"B1-1-50-50-5.ctp",
"B1-1-50-50-6.ctp",
"B1-1-50-50-8.ctp",
"B1-10-50-50-4.ctp",
"B1-10-50-50-5.ctp",
"B1-10-50-50-6.ctp",
"B1-10-50-50-8.ctp",
"B1-5-25-75-4.ctp",
"B1-5-25-75-5.ctp",
"B1-5-25-75-6.ctp",
"B1-5-25-75-8.ctp",
//"B2-1-100-100-4.ctp",
//"B2-1-100-100-5.ctp",
//"B2-1-100-100-6.ctp",
//"B2-1-100-100-8.ctp",
"B2-1-50-150-4.ctp",
"B2-1-50-150-5.ctp",
"B2-1-50-150-6.ctp",
"B2-1-50-150-8.ctp",
"B2-10-50-150-4.ctp",
"B2-10-50-150-5.ctp",
"B2-10-50-150-6.ctp",
"B2-10-50-150-8.ctp",
//"B2-20-100-100-4.ctp",
//"B2-20-100-100-5.ctp",
//"B2-20-100-100-6.ctp",
//"B2-20-100-100-8.ctp",
//"B2-25-100-100-4.ctp",
//"B2-25-100-100-5.ctp",
//"B2-25-100-100-6.ctp",
//"B2-25-100-100-8.ctp",
"C1-1-25-75-4.ctp",
"C1-1-25-75-5.ctp",
"C1-1-25-75-6.ctp",
"C1-1-25-75-8.ctp",
"C1-1-50-50-4.ctp",
"C1-1-50-50-5.ctp",
"C1-1-50-50-6.ctp",
"C1-1-50-50-8.ctp",
"C1-10-50-50-4.ctp",
"C1-10-50-50-5.ctp",
"C1-10-50-50-6.ctp",
"C1-10-50-50-8.ctp",
"C1-5-25-75-4.ctp",
"C1-5-25-75-5.ctp",
"C1-5-25-75-6.ctp",
"C1-5-25-75-8.ctp",
"D1-1-25-75-4.ctp",
"D1-1-25-75-5.ctp",
"D1-1-25-75-6.ctp",
"D1-1-25-75-8.ctp",
"D1-1-50-50-4.ctp",
"D1-1-50-50-5.ctp",
"D1-1-50-50-6.ctp",
"D1-1-50-50-8.ctp",
"D1-10-50-50-4.ctp",
"D1-10-50-50-5.ctp",
"D1-10-50-50-6.ctp",
"D1-10-50-50-8.ctp",
"D1-5-25-75-4.ctp",
"D1-5-25-75-5.ctp",
"D1-5-25-75-6.ctp",
"D1-5-25-75-8.ctp",
				 
		};
		
		/*
		String[] nameFile = {"pqd-800-4000-50-30.ctp",
				 //"pqd-200-1000-20-20.ctp",
		};
		*/
		//BufferedWriter fo = new BufferedWriter(new FileWriter("data/VehicleRouting/mCTP/result.txt"));
		//for (int test = 0; test < nameFile.length; test++) {
		for (int test = 0; test < 1; test++) {
			//System.out.println(nameFile[test]);
			String fn = nameFile[test];
			//String fn = "D1-5-25-75-8.ctp";
			//String fn = "A2-25-100-100-5.ctp";
			//String fn = "A1-5-25-75-6.ctp";
			//String fn = "A1-1-50-50-4.ctp";
			ScannerInput fi = new ScannerInput("data/mCTP/" + fn);
			//ScannerInput fi = new ScannerInput("data/VehicleRouting/mCTP/A1-10-50-50-6.ctp");
			System.out.println("filenam = " + fn);
			int nbNodes = fi.readInt();
			int nbCustomers = fi.readInt();
			int t = fi.readInt();
			int nbNodesPerRoute = fi.readInt();
			nbNodes += t;
			double best = fi.readDouble();
			System.out.println("nbNodes = " + nbNodes + ", nbCustomers = " + nbCustomers + ", t = " + t + ", nbNodesPerRoute = " + nbNodesPerRoute);
			//if(nbNodes > 50) continue;
			double[][] cost = new double[nbNodes][nbNodes];
			
			for (int i = 0; i < nbNodes; i++) {
				for (int j = i + 1; j < nbNodes; j++) {
					int u = fi.readInt();
					int v = fi.readInt();
					System.out.println("i = " + i + ", j = " + j + ", u = " + u + ", v = " + v);
					cost[i][j] = cost[j][i] = fi.readDouble();
				}
			}
			int[][] coveringMatrix = new int[nbNodes - t][nbCustomers];
			for (int i = 0; i < nbNodes - t; i++) {
				fi.readInt();
				for (int j = 0; j < nbCustomers; j++) {
					coveringMatrix[i][j] = fi.readInt();
				}
			}
			fi.close();
			
			for(int maxNeighbor = 1; maxNeighbor <= 2; maxNeighbor++){
			BufferedWriter fo = new BufferedWriter(new FileWriter("output/mCTP/" + fn + "-result-lns-" + maxNeighbor + ".txt"));
			fo.write(nameFile[test] + "\n" + best + "\n");
			
			double min = 10000000;
			double max = -min;
			double avg = 0;
			int nbRuns = 2;
			for(int run = 1; run <= nbRuns; run++){
				MultiVehicleTourCovering app = new MultiVehicleTourCovering(nbNodes, t, nbCustomers, nbNodesPerRoute, cost, coveringMatrix);
				app.stateModel();
				double rs = app.search(100000, 3600000);
				//double rs = app.searchVNS(maxNeighbor, 100000, timeLimit);
				fo.write(run + "\t" + rs + "\n");
				min = min < rs ? min : rs;
				max = max > rs ? max : rs;
				avg += rs;
			}
			avg = avg*1.0/nbRuns;
			fo.write("-1\n");
			fo.write(min + " " + max + " " + avg);
			fo.close();
			}
//			if (nbNodes < 100) {
//				fo.write(" " + app.search(10000, 60000) + "\n");
//			} else {
//				fo.write(" " + app.search(10000, 300000) + "\n");
//			}
		}
		
		//fo.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
		
	public static void statistic(){
		String[] nameFile = {"A1-1-25-75-4.ctp",
"A1-1-25-75-5.ctp",
"A1-1-25-75-6.ctp",
"A1-1-25-75-8.ctp",
"A1-1-50-50-4.ctp",
"A1-1-50-50-5.ctp",
"A1-1-50-50-6.ctp",
"A1-1-50-50-8.ctp",
"A1-10-50-50-4.ctp",
"A1-10-50-50-5.ctp",
"A1-10-50-50-6.ctp",
"A1-10-50-50-8.ctp",
"A1-5-25-75-4.ctp",
"A1-5-25-75-5.ctp",
"A1-5-25-75-6.ctp",
"A1-5-25-75-8.ctp",
//"A2-1-100-100-4.ctp",
//"A2-1-100-100-5.ctp",
//"A2-1-100-100-6.ctp",
//"A2-1-100-100-8.ctp",
"A2-1-50-150-4.ctp",
"A2-1-50-150-5.ctp",
"A2-1-50-150-6.ctp",
"A2-1-50-150-8.ctp",
"A2-10-50-150-4.ctp",
"A2-10-50-150-5.ctp",
"A2-10-50-150-6.ctp",
"A2-10-50-150-8.ctp",
//"A2-20-100-100-4.ctp",
//"A2-20-100-100-5.ctp",
//"A2-20-100-100-6.ctp",
//"A2-20-100-100-8.ctp",
//"A2-25-100-100-4.ctp",
//"A2-25-100-100-5.ctp",
//"A2-25-100-100-6.ctp",
//"A2-25-100-100-8.ctp",
"B1-1-25-75-4.ctp",
"B1-1-25-75-5.ctp",
"B1-1-25-75-6.ctp",
"B1-1-25-75-8.ctp",
"B1-1-50-50-4.ctp",
"B1-1-50-50-5.ctp",
"B1-1-50-50-6.ctp",
"B1-1-50-50-8.ctp",
"B1-10-50-50-4.ctp",
"B1-10-50-50-5.ctp",
"B1-10-50-50-6.ctp",
"B1-10-50-50-8.ctp",
"B1-5-25-75-4.ctp",
"B1-5-25-75-5.ctp",
"B1-5-25-75-6.ctp",
"B1-5-25-75-8.ctp",
//"B2-1-100-100-4.ctp",
//"B2-1-100-100-5.ctp",
//"B2-1-100-100-6.ctp",
//"B2-1-100-100-8.ctp",
"B2-1-50-150-4.ctp",
"B2-1-50-150-5.ctp",
"B2-1-50-150-6.ctp",
"B2-1-50-150-8.ctp",
"B2-10-50-150-4.ctp",
"B2-10-50-150-5.ctp",
"B2-10-50-150-6.ctp",
"B2-10-50-150-8.ctp",
//"B2-20-100-100-4.ctp",
//"B2-20-100-100-5.ctp",
//"B2-20-100-100-6.ctp",
//"B2-20-100-100-8.ctp",
//"B2-25-100-100-4.ctp",
//"B2-25-100-100-5.ctp",
//"B2-25-100-100-6.ctp",
//"B2-25-100-100-8.ctp",
//"C1-1-25-75-4.ctp",
//"C1-1-25-75-5.ctp",
//"C1-1-25-75-6.ctp",
//"C1-1-25-75-8.ctp",
//"C1-1-50-50-4.ctp",
//"C1-1-50-50-5.ctp",
//"C1-1-50-50-6.ctp",
//"C1-1-50-50-8.ctp",
//"C1-10-50-50-4.ctp",
//"C1-10-50-50-5.ctp",
//"C1-10-50-50-6.ctp",
//"C1-10-50-50-8.ctp",
//"C1-5-25-75-4.ctp",
//"C1-5-25-75-5.ctp",
//"C1-5-25-75-6.ctp",
//"C1-5-25-75-8.ctp",
//"D1-1-25-75-4.ctp",
//"D1-1-25-75-5.ctp",
//"D1-1-25-75-6.ctp",
//"D1-1-25-75-8.ctp",
//"D1-1-50-50-4.ctp",
//"D1-1-50-50-5.ctp",
//"D1-1-50-50-6.ctp",
//"D1-1-50-50-8.ctp",
//"D1-10-50-50-4.ctp",
//"D1-10-50-50-5.ctp",
//"D1-10-50-50-6.ctp",
//"D1-10-50-50-8.ctp",
//"D1-5-25-75-4.ctp",
//"D1-5-25-75-5.ctp",
//"D1-5-25-75-6.ctp",
//"D1-5-25-75-8.ctp",
				 
		};

		try{
		PrintWriter out = new PrintWriter(new File("output/mCTP/statistic-lns.txt"));
		for(int i = 0; i < nameFile.length; i++){
		//for(int i = 0; i < 1; i++){
			
			String fn = "output/mCTP/" + nameFile[i] + "-result-lns-1.txt";
			System.out.println(fn);
			Scanner in =  new Scanner(new File(fn));
			String line = in.nextLine(); 
			//System.out.println(line);
			//line = in.nextLine();
			double best = in.nextDouble();
			while(true){
				double x = in.nextDouble();
				if(x == -1) break;
				double f = in.nextDouble();
			}
			double min1 = in.nextDouble();
			double max1 = in.nextDouble();
			double avg1 = in.nextDouble();
			//out.println(nameFile[i] + "\t&\t"+ best + "\t&\t" + avg);
			in.close();
		
			fn = "output/mCTP/" + nameFile[i] + "-result-lns-2.txt";
			in =  new Scanner(new File(fn));
			line = in.nextLine(); 
			best = in.nextDouble();
			while(true){
				double x = in.nextDouble();
				if(x == -1) break;
				double f = in.nextDouble();
			}
			double min2 = in.nextDouble();
			double max2 = in.nextDouble();
			double avg2 = in.nextDouble();
			//out.println(nameFile[i] + "\t&\t"+ best + "\t&\t" + avg);
			in.close();
			
			out.println(nameFile[i] + "\t&\t"+ min1 + "\t&\t" + max1 + "\t&\t" + avg1 + 
					"\t&\t"+ min2 + "\t&\t" + max2 + "\t&\t" + avg2 + "\\\\");
			
		}
		out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		//MultiVehicleTourCovering.statistic();
		MultiVehicleTourCovering.runBatch(60000);
	}
}
