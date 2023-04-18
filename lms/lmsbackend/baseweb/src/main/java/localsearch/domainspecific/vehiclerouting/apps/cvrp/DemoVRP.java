package localsearch.domainspecific.vehiclerouting.apps.cvrp;

import java.io.File;
import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.eq.Eq;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.RouteIndex;
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
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMoveOneRouteExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;

public class DemoVRP {

	int K;// number of routes
	int N;// number of clients
	int capacity;
	int[] demand;
	int[] x;
	int[] y;// (x[i],y[i]) is coordinate of point i
	
	ArrayList<Point> start;
	ArrayList<Point> end;
	ArrayList<Point> clientPoints;
	ArrayList<Point> allPoints;
	ArcWeightsManager awm;// luu tru trong so tren canh noi giua cac point
	NodeWeightsManager nwm;// luu tru trong so tren cac point
	
	HashMap<Point, Integer> mapPoint2ID;
	
	
	// modelling
	VRManager mgr;
	VarRoutesVR XR;// bien loi giai (luu tap cac route)
	ConstraintSystemVR CS;
	LexMultiFunctions F;
	IFunctionVR obj;
	IFunctionVR[] d;// d[k] la demand cua route k
	IFunctionVR[] cost;// cost[k] la chieu dai cua route thu k
	
	public void readData(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			N = in.nextInt();
			K = in.nextInt();
			capacity = in.nextInt();
			demand = new int[N+1]; 
			x = new int[N+1];
			y = new int[N+1];
			
			for(int i = 0; i <= N; i++){
				int id = in.nextInt();
				x[id] = in.nextInt();
				y[id] = in.nextInt();
				demand[id] = in.nextInt();
			}
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void mapping(){
		// map from raw data to objects
		start = new ArrayList<Point>();
		end = new ArrayList<Point>();
		clientPoints = new ArrayList<Point>();
		allPoints = new ArrayList<Point>();
		
		mapPoint2ID = new HashMap<Point, Integer>();
		// khoi tao cac diem bat dau va ket thuc cua cac xe (route)
		for(int k = 1; k <= K; k++){
			Point s = new Point(0);
			Point t = new Point(0);
			start.add(s);
			end.add(t);
			allPoints.add(s);
			allPoints.add(t);
			mapPoint2ID.put(s, 0);
			mapPoint2ID.put(t,0);
		}
		
		// khoi tao cac diem clients
		for(int i = 1; i <= N; i++){
			Point p = new Point(i);
			clientPoints.add(p);
			allPoints.add(p);
			mapPoint2ID.put(p, i);
		}
		
		
		// khoi tao object quan ly trong so
		awm = new ArcWeightsManager(allPoints);
		nwm = new NodeWeightsManager(allPoints);
		
		for(Point p: allPoints){
			for(Point q: allPoints){
				int ip = mapPoint2ID.get(p);
				int iq = mapPoint2ID.get(q);
				double w = Math.sqrt(Math.pow(x[ip]-x[iq],2) + Math.pow(y[ip]-y[iq],2));
				awm.setWeight(p,q, w);
			}
		}
		for(Point p: allPoints)
			nwm.setWeight(p, demand[mapPoint2ID.get(p)]);
	}
	
	public void stateModel(){
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		for(int i = 0; i < start.size(); i++){
			Point s = start.get(i);
			Point t = end.get(i);
			XR.addRoute(s, t);// them 1 route vao phuong an (s --> t)
		}
		for(Point p: clientPoints)
			XR.addClientPoint(p);// khai bao XR co the se di qua diem p
		
		// thiet lap rang buoc
		CS = new ConstraintSystemVR(mgr);
		AccumulatedWeightNodesVR accDemand = new AccumulatedWeightNodesVR(XR, nwm);
		AccumulatedWeightEdgesVR accW = new AccumulatedWeightEdgesVR(XR, awm);
		
		d = new IFunctionVR[K];// demand on routes
		for(int k = 1; k <= K; k++){
			Point tk = XR.endPoint(k);// diem cuoi cung cua route thu k
			d[k-1] = new AccumulatedNodeWeightsOnPathVR(accDemand, tk);
			CS.post(new Leq(d[k-1],capacity));
		}
		
		cost = new IFunctionVR[K];
		for(int k =1; k <= K; k++){
			Point tk = XR.endPoint(k);
			cost[k-1] = new AccumulatedEdgeWeightsOnPathVR(accW, tk);
		}
		
		
		// demo routeIndex
		Point pickup = clientPoints.get(3);
		Point dropoff = clientPoints.get(4);
		IFunctionVR ip = new RouteIndex(XR, pickup);
		IFunctionVR id = new RouteIndex(XR,dropoff);
		//CS.post(new Eq(ip,id));
		
		
		
		
		obj = new TotalCostVR(XR, awm);// tong khoang cach di chuyen cua K xe (route)
		F = new LexMultiFunctions();
		F.add(new ConstraintViolationsVR(CS));
		F.add(obj);		
		mgr.close();
	}
	
	public void print(){
		for(int k = 1; k <= K; k++){
			Point s = XR.startPoint(k);
			System.out.print("Route[" + k + "] = ");
			for(Point p = s; p != XR.endPoint(k); p = XR.next(p)){
				System.out.print(p.ID + " -> ");
			}
			System.out.println(XR.endPoint(k).ID + " d[k] = " + d[k-1].getValue() + 
					", cost[k] = " + cost[k-1].getValue());
		}
		System.out.println("length = " + obj.getValue());
	}
	
	public void init(){
		Point s = XR.startPoint(1);
		for(int i = 0; i < clientPoints.size(); i++){
			Point p = clientPoints.get(i);
			mgr.performAddOnePoint(p, s);
			s = p;
		}
	}
	public void step(){
		
		Point s = XR.startPoint(1);
		mgr.performAddOnePoint(clientPoints.get(2), s);
		mgr.performAddOnePoint(clientPoints.get(1), clientPoints.get(2));
		
		
		mgr.performAddOnePoint(clientPoints.get(0), XR.startPoint(2));
		mgr.performAddOnePoint(clientPoints.get(3), clientPoints.get(0));
		Point x = clientPoints.get(0);
		Point y = clientPoints.get(2);
		
		print();
		double eval = obj.evaluateOnePointMove(x, y);
		mgr.performOnePointMove(x, y);
		System.out.println("eval = " + eval + ", obj = " + obj.getValue());
		print();
	}
	
	public void search(){
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		//NE.add(new GreedyTwoOptMoveOneRouteExplorer(XR, F)); not complete implementation of all functions (except TotalCostVR) for this operator (TwoOptMoveOneRoute)

		GenericLocalSearch se = new GenericLocalSearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);

		se.search(10, 3);
		
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DemoVRP A = new DemoVRP();
		A.readData("cvrp.txt");
		A.mapping();
		A.stateModel();
		A.print();
		A.step();
		A.print();
		A.search();
		//A.init();
		//System.out.println("-------------");
		//A.print();	
	}

}
