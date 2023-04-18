package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.experiments;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.MMSearch;
import localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.MinMaxCVRP;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.MaxVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
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

public class MinMaxCVRPNeighborhoodsOfBLGolden extends MinMaxCVRP {

	public double zmax = 0;
	public double z_ = 0;
	public double time = 0;
	public double maxValue = 0;
	public double avg = 0;
	int p;
	int q;
	int r;
	
	public void mapping(){
		clientPoints = new ArrayList<Point>();
		allPoints = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
	
		HashMap<Point, Integer> mPoint2ID = new HashMap<Point, Integer>();
		for(int k = 1; k <= nbClients; k++){
			Point s = new Point(depot);
			startPoints.add(s);
			allPoints.add(s);
			mPoint2ID.put(s, depot);
			
			Point t = new Point(depot);
			endPoints.add(t);
			allPoints.add(t);
			mPoint2ID.put(t, depot);
		}
		
		for(int i= 1; i <= nbClients+1; i++){
			if(i != depot){
				Point p = new Point(i);
				clientPoints.add(p);
				allPoints.add(p);
				mPoint2ID.put(p, p.ID);
			}
		}
		
		nwm = new NodeWeightsManager(allPoints);
		awm = new ArcWeightsManager(allPoints);
		for(Point p: clientPoints)
			nwm.setWeight(p, demand[mPoint2ID.get(p)]);
		for(Point p: startPoints)
			nwm.setWeight(p, 0);
		for(Point p: endPoints)
			nwm.setWeight(p, 0);
		
		for(Point p1: allPoints){
			int i = mPoint2ID.get(p1);
			for(Point p2: allPoints){
				int j = mPoint2ID.get(p2);
				awm.setWeight(p1, p2, cost[i][j]);
			}
		}
		
		
	}
	
	public void stateModel(){
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		int depot = startPoints.get(0).ID;
		for(int i = 0; i < startPoints.size(); i++){
			Point s = startPoints.get(i);
			Point t = endPoints.get(i);
			XR.addRoute(s, t);
		}
		
		for(Point p: clientPoints)
			XR.addClientPoint(p);
		
		CS = new ConstraintSystemVR(mgr);
		AccumulatedWeightNodesVR awn = new AccumulatedWeightNodesVR(XR, nwm);
		AccumulatedWeightEdgesVR awe = new AccumulatedWeightEdgesVR(XR, awm);
		accDemand = new IFunctionVR[XR.getNbRoutes()];
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			accDemand[k-1] = new AccumulatedNodeWeightsOnPathVR(awn, XR.endPoint(k));
			CS.post(new Leq(accDemand[k-1], capacity));
		}
		
		distance = new IFunctionVR[XR.getNbRoutes()];
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			distance[k-1] = new AccumulatedEdgeWeightsOnPathVR(awe, XR.endPoint(k));
		}
		
		obj = new MaxVR(distance);
		
		totalDistance = new TotalCostVR(XR, awm);
		
		F = new LexMultiFunctions();
		F.add(new ConstraintViolationsVR(CS));
		F.add(obj);
		F.add(totalDistance);
		mgr.close();
	}
	
	public void generateInitialSolution(double z){
		//create all routes: v0 --> client --> v0
		
		Random ran = new Random();
		
		double zz = z;
		
		while(true){
			//System.out.println("generateInitialSolution:: zz = " + zz + ", 1.5z_ = " + (1.5*z));
			double maxD = 1000000;
			Point x1 = null;
			Point x2 = null;
			Point y = null;
			int type = 0;
			for(int s = 1; s <= XR.getNbRoutes(); s++){
				Point start1 = XR.getStartingPointOfRoute(s);
				Point end1 = XR.getTerminatingPointOfRoute(s);
				if(XR.next(start1) == end1)
					continue;
				for(int t = 1; t <= XR.getNbRoutes() && t != s; t++){
					for(Point v = XR.getStartingPointOfRoute(t); v!= XR.getTerminatingPointOfRoute(t); v = XR.next(v)){
						if(XR.next(XR.getStartingPointOfRoute(t)) == XR.getTerminatingPointOfRoute(t))
							continue;
						double estD = 0;
						int tp = 0;
						if(XR.next(start1) == XR.prev(end1)){
							estD = distance[t-1].evaluateOnePointMove(XR.next(start1), v) + distance[t-1].getValue();
							tp = 1;
						}
						else{
							double estD1 = distance[t-1].evaluateOrOptMove1(XR.next(start1), XR.prev(end1), v) + distance[t-1].getValue();
							double estD2 = distance[t-1].evaluateOrOptMove2(XR.next(start1), XR.prev(end1), v) + distance[t-1].getValue();
							estD = estD1;
							if(estD1 >= estD2){
								estD = estD1;
								tp = 2;
							}
							else{
								estD = estD2;
								tp = 3;
							}
						}
						double phi = (double)(90 + ran.nextInt(20)) / 100;
						
						if(maxD > estD && (phi * estD) <= zz){
							maxD = estD;
							x1 = XR.next(start1);
							x2 = XR.prev(end1);
							y = v;
							type = tp;
						}
					}
				}
			}
			if(x1 != null){
				int rx = XR.route(x1);
				if(type == 1)
					mgr.performOnePointMove(x1, y);
				else if(type == 2)
					mgr.performOrOptMove1(x1, x2, y);
				else if(type == 3)
					mgr.performOrOptMove2(x1, x2, y);
			}
			zz = zz*1.02;
			if(zz > 1.5*z)
				break;
		}
		System.out.println("generateInitialSolution:: end==============================");
	}
	
	public void generateNewSolution(){
		//create all routes: v0 --> client --> v0
		
		Random ran = new Random();
		
		double zz = z_;
		
		while(true){
			//System.out.println("generateInitialSolution:: zz = " + zz + ", 1.5z_ = " + (1.5*z));
			double maxD = 1000000;
			Point x1 = null;
			Point x2 = null;
			Point y = null;
			int type = 0;
			for(int s = 1; s <= XR.getNbRoutes(); s++){
				Point start1 = XR.getStartingPointOfRoute(s);
				Point end1 = XR.getTerminatingPointOfRoute(s);
				if(XR.next(start1) == end1)
					continue;
				for(int t = 1; t <= XR.getNbRoutes() && t != s; t++){
					for(Point v = XR.getStartingPointOfRoute(t); v!= XR.getTerminatingPointOfRoute(t); v = XR.next(v)){
						if(XR.next(XR.getStartingPointOfRoute(t)) == XR.getTerminatingPointOfRoute(t))
							continue;
						double estD = 0;
						int tp = 0;
						if(XR.next(start1) == XR.prev(end1)){
							estD = distance[t-1].evaluateOnePointMove(XR.next(start1), v) + distance[t-1].getValue();
							tp = 1;
						}
						else{
							double estD1 = distance[t-1].evaluateOrOptMove1(XR.next(start1), XR.prev(end1), v) + distance[t-1].getValue();
							double estD2 = distance[t-1].evaluateOrOptMove2(XR.next(start1), XR.prev(end1), v) + distance[t-1].getValue();
							estD = estD1;
							if(estD1 >= estD2){
								estD = estD1;
								tp = 2;
							}
							else{
								estD = estD2;
								tp = 3;
							}
						}
					
						double phi = (double)(90 + ran.nextInt(20)) / 100;
						//System.out.println("phi = " + phi + ", zz = " + zz);
						if(maxD > estD && (phi * estD) <= zz){
							maxD = estD;
							x1 = XR.next(start1);
							x2 = XR.prev(end1);
							y = v;
							type = tp;
						}
					}
				}
			}
			if(x1 != null){
				int rx = XR.route(x1);
				if(type == 1)
					mgr.performOnePointMove(x1, y);
				else if(type == 2)
					mgr.performOrOptMove1(x1, x2, y);
				else if(type == 3)
					mgr.performOrOptMove2(x1, x2, y);
			}
			zz = zz*1.02;
			if(getNumberOfRoute() <= nbVehicles)
				break;
		}
		System.out.println("generateNewSolution:: end===================== nbRoute = " + getNumberOfRoute() + ", z_ max = " + getMaxLength());
	}
	
	public void improvementPhaseOfModifiedTaillard(double zz){
		Random ran = new Random();
		HashMap<Point, Integer> pointFromR = new HashMap<Point, Integer>();
		HashMap<Point, Integer> pointToR = new HashMap<Point, Integer>();
		HashMap<Point, Integer> pointNbIt = new HashMap<Point, Integer>();
		for(int i = 0; i < clientPoints.size(); i++){
			pointFromR.put(clientPoints.get(i), XR.route(clientPoints.get(i)));
			pointToR.put(clientPoints.get(i), 0);
			pointNbIt.put(clientPoints.get(i), 0);
		}
		int nbIt = 0;
		int reInsIt = (int)(0.4*nbClients);
		while(true){
			//System.out.println("improvementPhaseOfModifiedTaillard:: nbIt = " + nbIt);
			nbIt++;
			if(nbIt > q)
				break;
			double Dmax = 0;
			double Dmin = 1000000;
			int ss = -1;
			int s = -1;
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				double D = distance[r-1].getValue();
				//z_ = max {c0j + cj0}
				if(D > Dmax){
					Dmax = D;
					s = r;
				}
				if(D < Dmin){
					Dmin = D;
					ss = r;
				}
			}
				
				int startIdx = (int)(XR.index(XR.getTerminatingPointOfRoute(s)) / 2 - XR.index(XR.getTerminatingPointOfRoute(s)) / 4);

//				Point startP = null;
//				for(int i = 1; i <= XR.getNbRoutes(); i++){
//					if(XR.next(XR.getStartingPointOfRoute(i)) == XR.getTerminatingPointOfRoute(i)){
//						startP = XR.getStartingPointOfRoute(i);
//						break;
//					}
//				}
				Point startP = XR.getStartingPointOfRoute(ss);
				if(startP != null){
					Point preAdd = startP;
					int idxEnd = XR.index(XR.getTerminatingPointOfRoute(s));
					int ac = startIdx + (int)(XR.index(XR.getTerminatingPointOfRoute(s)) / 2);
					for(Point v = XR.getStartingPointOfRoute(s); v != XR.getTerminatingPointOfRoute(s); v = XR.next(v)){
						if(XR.index(v) > startIdx + (int)(XR.index(XR.getTerminatingPointOfRoute(s)) / 2)){
							break;
						}
						if(XR.index(v) >= startIdx){
							Point nextAdd = XR.next(v);
							Point curAdd = v;
							for(int i = 0; i < startIdx + (int)(XR.index(XR.getTerminatingPointOfRoute(s)) / 2); i++){
								mgr.performOnePointMove(curAdd, preAdd);
								preAdd = curAdd;
								curAdd = nextAdd;
								nextAdd = XR.next(curAdd);
							}
							break;
						}
					}
				}
			double minD = 100000;
			Point p1 = null;
			Point p2 = null;
			Point q1 = null;
			Point q2 = null;
			for(int t = 1; t <= XR.getNbRoutes() && t != s; t++){
				for(Point x1 = XR.next(XR.getStartingPointOfRoute(s)); x1!= XR.getTerminatingPointOfRoute(s); x1 = XR.next(x1)){
					for(Point y1= XR.getStartingPointOfRoute(t); y1!= XR.getTerminatingPointOfRoute(t); y1 = XR.next(y1)){
						for(Point x2 = XR.next(XR.getStartingPointOfRoute(t)); x2!= XR.getTerminatingPointOfRoute(t) && x2 != y1; x2 = XR.next(x2)){
							for(Point y2= XR.getStartingPointOfRoute(s); y2!= XR.getTerminatingPointOfRoute(s) && y2 != x1; y2 = XR.next(y2)){
								if((nbIt - pointNbIt.get(x1) < reInsIt && pointFromR.get(x1) == s && pointToR.get(x1) == t)
									|| (nbIt - pointNbIt.get(x2) < reInsIt && pointFromR.get(x2) == t && pointToR.get(x2) == s))
									continue;
								if(distance[s-1].getValue() + distance[s-1].evaluateTwoPointsMove(x1, x2, y1, y2) < minD ){
									minD = distance[s-1].getValue() + distance[s-1].evaluateTwoPointsMove(x1, x2, y1, y2);
									p1 = x1;
									p2 = x2;
									q1 = y1;
									q2 = y2;
								}
								//System.out.println("t = " + t);
							}
						}
					}
				}		
			}
			if(p1 != null){
				pointFromR.put(p1, XR.route(p1));
				pointFromR.put(p2, XR.route(p2));
				mgr.performTwoPointsMove(p1, p2, q1, q2);
				pointToR.put(p1, XR.route(p1));
				pointToR.put(p2, XR.route(p2));
				pointNbIt.put(p1, nbIt);
				pointNbIt.put(p2, nbIt);
			}
		}
	}
	
	public boolean checkStopCondition(){
		int nbRoute = getNumberOfRoute();
		if(nbRoute == 1)
			return true;
		if(nbRoute <= nbVehicles){
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				double D = distance[r-1].getValue();
				//z_ = max {c0j + cj0}
				if(D > zmax)
					zmax = D;
			}
			if(zmax == z_)
				return true;
		}
		return false;
	}
	
	public int getNumberOfRoute(){
		int nbRoute = 0;
		for(int i = 1; i <= XR.getNbRoutes(); i++){
			if(XR.next(XR.getStartingPointOfRoute(i)) != XR.getTerminatingPointOfRoute(i)){
				nbRoute++;
			}
		}
		return nbRoute;
	}
	
	public double getMaxLength(){
		for(int r = 1; r <= XR.getNbRoutes(); r++){
			double D = distance[r-1].getValue();
			//z_ = max {c0j + cj0}
			if(D > zmax)
				zmax = D;
		}
		System.out.println("zmax = " + zmax);
		return zmax;
	}
	
	public double getMinLength(){
		double min = 100000;
		for(int r = 1; r <= XR.getNbRoutes(); r++){
			double D = distance[r-1].getValue();
			//z_ = max {c0j + cj0}
			if(D < min && D != 0)
				min = D;
		}
		//System.out.println("zmax = " + zmax);
		return min;
	}
	
	public void saveLog(String output){
		try{
		PrintWriter out = new PrintWriter(output);
		out.println("n m r averageLength maxLength minLength second");
		LexMultiValues V = F.getValues();
		out.println(nbClients + " " + getNumberOfRoute() + " " + r + " " + ((double)Math.round((avg/10000)*100)/100 + getMinLength()) + " " + " " + getMaxLength() + " " + maxValue + " " + ((System.currentTimeMillis() - time)/100));
		out.close();
		}
		catch(Exception e){
			
		}
		
	}
	public void solutionAMPforCVRP(String output){
		int c = 0;
		
		for(int r = 1; r <= XR.getNbRoutes(); r++){
			Point start = XR.startPoint(r);
			mgr.performAddOnePoint(clientPoints.get(c), start);
			double D = distance[r-1].getValue();
			//z_ = max {c0j + cj0}
			if(D > z_)
				z_ = D;
			c++;
		}
		zmax = z_;
		generateInitialSolution(z_);
		
		if(checkStopCondition())
			return;
		double zz = z_;
		int minNb = 100000;
		int rt = 0;
		while(true){
			int nbR = getNumberOfRoute();
			if(minNb > nbR)
				minNb = nbR;
			System.out.println("solutionAMPforCVRP:: min nbr = " + minNb + ", number of Route = " + nbR);
			if( rt > 10000)
				break;
			rt++;
			improvementPhaseOfModifiedTaillard(zz);
			if(rt == 999){
				generateInitialSolution(z_*1.5);
				maxValue = getMaxLength();
			}
			avg += getMaxLength();
			//if(zz*1.02 < 1.5*z_)
			zz = zz*1.02;
			//generateNewSolution();
			getMaxLength();
		}
		generateNewSolution();
		
		System.out.println("==============result======================");
		System.out.println(XR.toString());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] fin = new String[]{
/*				"data/MinMaxVRP/F-VRP/std_all/F-n72-m4.vrp",
				"data/MinMaxVRP/F-VRP/std_all/F-n72-m5.vrp",
				"data/MinMaxVRP/F-VRP/std_all/F-n72-m6.vrp",
				"data/MinMaxVRP/F-VRP/std_all/F-n135-m7.vrp",
				"data/MinMaxVRP/F-VRP/std_all/F-n135-m8.vrp",
				"data/MinMaxVRP/CMT/std_all/vrpnc1-m5.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc1-m6.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc1-m7.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc2-m10.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc2-m11.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc2-m12.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc3-m8.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc3-m9.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc3-m10.txt",*/
				"data/MinMaxVRP/CMT/std_all/vrpnc4-m12.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc4-m13.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc5-m16.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc5-m17.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc11-m7.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc11-m8.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc12-m10.txt",
				"data/MinMaxVRP/CMT/std_all/vrpnc12-m11.txt"
		};
		String[] fout = new String[]{
/*				"data/output/MinMaxVRP/F-VRP/F-n72-m4.txt",
				"data/output/MinMaxVRP/F-VRP/F-n72-m5.txt",
				"data/output/MinMaxVRP/F-VRP/F-n72-m6.txt",
				"data/output/MinMaxVRP/F-VRP/F-n135-m7.txt",
				"data/output/MinMaxVRP/F-VRP/F-n135-m8.txt",
				"data/output/MinMaxVRP/CMT/vrpnc1-m5.txt",
				"data/output/MinMaxVRP/CMT/vrpnc1-m6.txt",
				"data/output/MinMaxVRP/CMT/vrpnc1-m7.txt",
				"data/output/MinMaxVRP/CMT/vrpnc2-m10.txt",
				"data/output/MinMaxVRP/CMT/vrpnc2-m11.txt",
				"data/output/MinMaxVRP/CMT/vrpnc2-m12.txt",
				"data/output/MinMaxVRP/CMT/vrpnc3-m8.txt",
				"data/output/MinMaxVRP/CMT/vrpnc3-m9.txt",
				"data/output/MinMaxVRP/CMT/vrpnc3-m10.txt",*/
				"data/output/MinMaxVRP/CMT/vrpnc4-m12.txt",
				"data/output/MinMaxVRP/CMT/vrpnc4-m13.txt",
				"data/output/MinMaxVRP/CMT/vrpnc5-m16.txt",
				"data/output/MinMaxVRP/CMT/vrpnc5-m17.txt",
				"data/output/MinMaxVRP/CMT/vrpnc11-m7.txt",
				"data/output/MinMaxVRP/CMT/vrpnc11-m8.txt",
				"data/output/MinMaxVRP/CMT/vrpnc12-m10.txt",
				"data/output/MinMaxVRP/CMT/vrpnc12-m11.txt"
		};
		int p = 10*20;
		double max = 0;
		double min = 100000;
		double sum = 0;
		double second = 0;
		double time = 0;
		for(int i = 0; i < fin.length; i++){
			time = System.currentTimeMillis();
			sum = 0;
			min = 10000;
			max = 0;
			second = 0;
			for(int t = 0; t < p; t++){
				MinMaxCVRPNeighborhoodsOfBLGolden vrp =
						new MinMaxCVRPNeighborhoodsOfBLGolden();
				vrp.time = System.currentTimeMillis();
				vrp.q = 5;
				if(t == 3)
					vrp.q = 1;
				//vrp.p = 10;
				vrp.r = 10;
				//vrp.readData("data/MinMaxVRP/Christophides/std-all/E-n101-k14.vrp");
				vrp.readData(fin[i]);
				vrp.mapping();
				vrp.stateModel();
				vrp.solutionAMPforCVRP(fout[i]);
				double obj = vrp.getMaxLength();
				if( obj > max)
					max = vrp.getMaxLength();
				if(obj < min)
					min = obj;
				sum += obj;
				
			}
			second = (System.currentTimeMillis() - time)/(10*p);
			try{
			PrintWriter out = new PrintWriter(fout[i]);
			out.println("averageLength & minLength & maxLength & second");
			out.println((double)Math.round((sum/p)*100)/100 + "\t&\t" + min + "\t&\t" + max + "\t&\t" + second);
			out.close();
			}
			catch(Exception e){
				
			}
		}

	}
}