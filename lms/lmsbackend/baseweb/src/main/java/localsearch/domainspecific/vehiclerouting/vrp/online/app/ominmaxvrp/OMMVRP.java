package localsearch.domainspecific.vehiclerouting.vrp.online.app.ominmaxvrp;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Request;
import localsearch.domainspecific.vehiclerouting.vrp.entities.RequestsQueue;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.MoveTwoPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.online.NodeWeightsManagerOnline;
import localsearch.domainspecific.vehiclerouting.vrp.online.TimeHorizon;
import localsearch.domainspecific.vehiclerouting.vrp.online.VREuclideanTimeDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.online.VRManagerOnline;
import localsearch.domainspecific.vehiclerouting.vrp.online.VarRoutesVROnline;
import localsearch.domainspecific.vehiclerouting.vrp.online.app.DARPRequest;
import localsearch.domainspecific.vehiclerouting.vrp.online.app.RequestType;
import localsearch.domainspecific.vehiclerouting.vrp.online.functions.OAccumulatedEdgeWeightsOnPath;
import localsearch.domainspecific.vehiclerouting.vrp.online.functions.OAccumulatedNodeWeightsOnPath;
import localsearch.domainspecific.vehiclerouting.vrp.online.functions.OFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.online.functions.OSumVR;
import localsearch.domainspecific.vehiclerouting.vrp.online.invariants.OAccumulatedWeightEdges;
import localsearch.domainspecific.vehiclerouting.vrp.online.invariants.OAccumulatedWeightNodes;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class OMMVRP {

	private VRManagerOnline mgr;
	private VarRoutesVROnline XR;
	private OAccumulatedWeightEdges oae;
	private OAccumulatedWeightNodes oan;
	private OSumVR cost;
	private HashMap<Integer, OFunctionVR> mCost;
	private HashMap<Integer, OFunctionVR> mAccCapacity;
	private int capacity = 100;
	private VREuclideanTimeDistanceManager TDM;
	private NodeWeightsManagerOnline nwm;
	private TimeHorizon T;
	private int timehorizon;
	private double speed = 1.0 / 60;// km per second
	private int dT = 10;
	private int maxNbClientsPerRoute = 5;
	private ArrayList<DeliveryRequest> requests;
	private HashMap<Request, Point> location;
	private HashMap<Point, DeliveryRequest> mPoint2Req;
	private Point depot;
	private RequestsQueue requestQueue;
	private PrintWriter log = null;

	private double bestEval;
	private double[] costovertime;

	public OMMVRP() {
		try {
			log = new PrintWriter(new File("OMMVRP-log.txt"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void genData(String fn, int W, int H, int minDT, int maxDT, int minDemand, int maxDemand,
			int TimeHorizon) {
		try {
			PrintWriter out = new PrintWriter(fn);
			java.util.Random R = new java.util.Random();

			out.println("depot (x,y)");
			out.println("0 0");
			out.println("time horizon");
			out.println(TimeHorizon);
			out.println("#id x(delivery) y(delivery) time call");
			int id = 0;
			int t = 0;
			while (true) {
				int DT = R.nextInt(maxDT - minDT + 1) + minDT;
				t = t + DT;
				if (t > TimeHorizon)
					break;
				int x_delivery = R.nextInt(W);
				int y_delivery = R.nextInt(H);
				int d = R.nextInt(maxDemand - minDemand + 1) + minDemand;
				out.println(id + " " + x_delivery + " " + y_delivery + " " + d + " " + t + " ");
				id++;
			}
			out.println(-1);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void loadRequests(String fn) {
		try {
			
			requests = new ArrayList<DeliveryRequest>();
			location = new HashMap<Request, Point>();
			Scanner in = new Scanner(new File(fn));
			String s = in.nextLine();
			// System.out.println("s = " + s);
			int depot_x = in.nextInt();
			int depot_y = in.nextInt();
			s = in.nextLine();
			s = in.nextLine();
			timehorizon = in.nextInt();
			timehorizon = 1000000;
			s = in.nextLine();
			s = in.nextLine();
			int idPoint = 0;
			// System.out.println("s = " + s);
			while (true) {
				int id = in.nextInt();
				// System.out.println("id = " + id);
				if (id == -1)
					break;
				int delivery_x = in.nextInt();
				int delivery_y = in.nextInt();
				int demand = in.nextInt();
				int timeCall = in.nextInt();
				if (id > 0) {
					idPoint++;
					Point delivery = new Point(idPoint, delivery_x, delivery_y);
					DeliveryRequest req = new DeliveryRequest(idPoint, timeCall, delivery, demand); 
					requests.add(req);

				} else {
					depot = new Point(depot_x, depot_y);
				}
			}
			in.close();
			// System.exit(-1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean delivery(Point x) {
		DeliveryRequest r = mPoint2Req.get(x);
		return x == r.delivery;
	}


	public String name() {
		return "OOMMVRP";
	}

	public void update(int t) {
		// TDM.updateWhenReachingTimePoint(t);
		mgr.update(t);
	}

	public void stateModel() {
		mgr = new VRManagerOnline();
		XR = new VarRoutesVROnline(mgr);
		XR.setLog(log);
		TDM = new VREuclideanTimeDistanceManager(XR);
		nwm = new NodeWeightsManagerOnline(XR);
		oae = new OAccumulatedWeightEdges(XR, TDM);
		oan = new OAccumulatedWeightNodes(XR, nwm);
		oae.setLog(log);
		cost = new OSumVR(mgr);// new
								// ArrayList<OAccumulatedEdgeWeightsOnPath>();
		T = new TimeHorizon(0, timehorizon, timehorizon);
		requestQueue = new RequestsQueue();
		mPoint2Req = new HashMap<Point, DeliveryRequest>();
		mCost = new HashMap<Integer, OFunctionVR>();
		mAccCapacity = new HashMap<Integer, OFunctionVR>();
		System.out.println(name() + "::stateModel OK");
	}

	public void receiveRequests() {
		requestQueue.clear();
		while (requests.size() > 0) {
			Request r = requests.get(0);
			if (r.arrivalTime <= T.currentTimePoint + dT) {
				requestQueue.add(r);
				requests.remove(0);
			} else {
				break;
			}
		}
	}

	private void exploreTwoPointsMove(Neighborhood N) {
	}


	public void reoptimize(int maxTime) {
		Neighborhood N = new Neighborhood(mgr);

		maxTime = maxTime * 1000;
		double t0 = System.currentTimeMillis();
		while (true) {
			double t = System.currentTimeMillis() - t0;
			if (t > maxTime)
				break;
			bestEval = CBLSVR.MAX_INT;
			N.clear();
			exploreTwoPointsMove(N);
			if (N.hasMove()) {
				IVRMove m = N.getAMove();
				// System.out.println("ODARP::reoptimize, move eval = "
				// + m.evaluation());
				m.move();
			} else {
				// System.out.println("ODARP, no move BREAK");
				break;
			}
		}
	}

	public int createNewRoute() {
		Point sp = depot.clone();
		Point tp = depot.clone();
		XR.addRoute(sp, tp);
		mgr.engage(sp);
		mgr.engage(tp);
		//nwm.addPoint(sp);
		//nwm.addPoint(tp);
		nwm.setWeight(sp, capacity);
		nwm.setWeight(tp, 0);
		//oan.addPoint(sp);
		oan.setAccumulatedWeightStartPoint(XR.getNbRoutes(), capacity);
		oan.init();
		//oan.addPoint(tp);
		
		OAccumulatedEdgeWeightsOnPath f = new OAccumulatedEdgeWeightsOnPath(
				oae, XR.endPoint(XR.getNbRoutes()));
		
		OAccumulatedNodeWeightsOnPath accCap =
				new OAccumulatedNodeWeightsOnPath(oan, XR.endPoint(XR.getNbRoutes()));
		cost.add(f);
		mCost.put(XR.getNbRoutes(), f);
		mAccCapacity.put(XR.getNbRoutes(), accCap);
		TDM.setSpeed(XR.getNbRoutes(), speed);
		
		log.println("At T = " + T.currentTimePoint + ", createNewRoute, oan.getAccumulateNodeWeights of last route = " + accCap.getValue());
		
		return XR.getNbRoutes();

	}

	private void init() {
		// System.out.println(name() + "::simulate1 start....");
		//T = new TimeHorizon(0, timehorizon, timehorizon);
		T = new TimeHorizon(0, 10000, 100000);
		requestQueue = new RequestsQueue();
		mPoint2Req = new HashMap<Point, DeliveryRequest>();

		costovertime = new double[timehorizon];
		Arrays.fill(costovertime, -1);
	}


	private boolean finishExecute() {
		if (requests.size() != 0)
			return false;
		if (requestQueue.size() != 0)
			return false;
		for (int k = 1; k < XR.getNbRoutes(); k++)
			if (XR.isMoving(k))
				return false;
		return true;
	}

	private int findTruck(DeliveryRequest req){
		double maxLen = -10000;
		int sel_r = -1;
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			if(mCost.get(k).getValue() >= 1000) continue;
			if(mAccCapacity.get(k).getValue() < req.demand) continue;
			if(maxLen < mCost.get(k).getValue()){
				maxLen = mCost.get(k).getValue();
				sel_r = k;
			}
		}
		
		return sel_r;
	}
	
	private void insert2End(DeliveryRequest r, int sel_route){
		Point s = XR.startingPoint(sel_route);
		Point e = XR.endPoint(sel_route);
		Point p = XR.prev(e);
		mgr.performAddOnePoint(r.delivery, p);
	}
	
	private void insertGreedy(DeliveryRequest r, int sel_r){
		OFunctionVR f = mCost.get(sel_r);
		Point sel_p = null;
		double minDelta = Integer.MAX_VALUE;
		for(Point p = XR.startingPoint(sel_r); p != XR.endPoint(sel_r); p = XR.next(p)){
			double delta = f.evaluateAddOnePoint(r.delivery, p);
			if(delta < minDelta){
				minDelta = delta;
				sel_p = p;
			}
		}
		mgr.performAddOnePoint(r.delivery, sel_p);
	}
	public void strategy_select_shortest_route_insert_requets_to_last(DeliveryRequest r){
		int sel_r = findTruck(r);
		if(sel_r == -1){
			createNewRoute();
			sel_r = XR.getNbRoutes();
			System.out.println(name() + "::execute, T = " + T.currentTimePoint + ", sel_r = -1 --> createNewRoute, request.delivery = " + r.delivery.ID +
					", demand = " + r.demand + ", createNewRoute --> OK, sel_r = " + sel_r);
			
			log.println(name() + "::execute, sel_r = -1 --> createNewRoute, request.delivery = " + r.delivery.ID +
					", demand = " + r.demand + ", createNewRoute --> OK, sel_r = " + sel_r);
		}
		
		System.out.println(name() + "::execute, XR.NbRoutes = " + XR.getNbRoutes() + ", sel_r = " + sel_r);
		insert2End(r, sel_r);
		System.out.println(name() + "::execute, insert point two route --> OK, accCap[" + sel_r + "] = " +
		mAccCapacity.get(sel_r).getValue());
		
		if (TDM.getDepartureTime(XR.startPoint(sel_r)) < 0)
			TDM.setDepartureTime(XR.startPoint(sel_r),
					T.currentTimePoint + dT);
		XR.setMoving(sel_r, true);
		
	}
	
	public void strategy_select_shortest_route_insert_greedy_requets(DeliveryRequest r){
		int sel_r = findTruck(r);
		if(sel_r == -1){
			createNewRoute();
			sel_r = XR.getNbRoutes();
			System.out.println(name() + "::strategy_select_shortest_route_insert_greedy_requets, T = " + T.currentTimePoint + ", sel_r = -1 --> createNewRoute, request.delivery = " + r.delivery.ID +
					", demand = " + r.demand + ", createNewRoute --> OK, sel_r = " + sel_r);
			
			log.println(name() + "::strategy_select_shortest_route_insert_greedy_requets, sel_r = -1 --> createNewRoute, request.delivery = " + r.delivery.ID +
					", demand = " + r.demand + ", createNewRoute --> OK, sel_r = " + sel_r);
		}
		
		System.out.println(name() + "::strategy_select_shortest_route_insert_greedy_requets, XR.NbRoutes = " + XR.getNbRoutes() + ", sel_r = " + sel_r);
		insertGreedy(r, sel_r);
		
		System.out.println(name() + "::strategy_select_shortest_route_insert_greedy_requets, insert point two route --> OK, accCap[" + sel_r + "] = " +
		mAccCapacity.get(sel_r).getValue());
		
		if (TDM.getDepartureTime(XR.startPoint(sel_r)) < 0)
			TDM.setDepartureTime(XR.startPoint(sel_r),
					T.currentTimePoint + dT);
		XR.setMoving(sel_r, true);
		
	}
	public void strategy_insert_greedy(DeliveryRequest r){
		double maxLength = 0;
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			if(maxLength < mCost.get(k).getValue()) maxLength = mCost.get(k).getValue();
		}
		int sel_r = -1;
		Point sel_p = null;
		double newMinMaxLength = Integer.MAX_VALUE;
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			for(Point p = XR.startingPoint(k); p != XR.endPoint(k); p = XR.next(p)){
				if(mAccCapacity.get(k).getValue() > r.demand){
					double d = mCost.get(k).evaluateAddOnePoint(r.delivery, p) + mCost.get(k).getValue();
					double newMaxLength = d;
					for(int k1 = 1; k1 <= XR.getNbRoutes(); k1++)if(k1 != k){
						if(newMaxLength < mCost.get(k1).getValue()){
							newMaxLength = mCost.get(k1).getValue();
						}
					}
					
					if(newMinMaxLength > newMaxLength){
						newMinMaxLength = newMaxLength;
						sel_r = k;
						sel_p = p;
					}
				}
				
			}
		}
		
		if(sel_r == -1){
			createNewRoute();
			sel_r = XR.getNbRoutes();
			sel_p = XR.startingPoint(sel_r);
			System.out.println(name() + "::strategy_insert_greedy, T = " + T.currentTimePoint + ", sel_r = -1 --> createNewRoute, request.delivery = " + r.delivery.ID +
					", demand = " + r.demand + ", createNewRoute --> OK, sel_r = " + sel_r);
			
			log.println(name() + "::strategy_insert_greedy, sel_r = -1 --> createNewRoute, request.delivery = " + r.delivery.ID +
					", demand = " + r.demand + ", createNewRoute --> OK, sel_r = " + sel_r);
		}
		
		// move
		mgr.performAddOnePoint(r.delivery, sel_p);
		
		if (TDM.getDepartureTime(XR.startPoint(sel_r)) < 0)
			TDM.setDepartureTime(XR.startPoint(sel_r),
					T.currentTimePoint + dT);
		XR.setMoving(sel_r, true);
	}
	public void execute(boolean reoptimize, boolean oneone) {
		init();
		receiveRequests();
		int nbReq = 0;
		while (!T.finished()) {
			if (finishExecute())
				break;
			//System.out.println(name() + "::execute before mgr.update");
			mgr.update(T.currentTimePoint + dT);
			//System.out.println(name() + "::execute after mgr.update");
			
			while (requestQueue.size() > 0) {
				System.out.println(name() + "::execute, Remains requets.sz = " + requests.size());
				DeliveryRequest r = (DeliveryRequest) requestQueue.pop();
				nbReq++;
				
				mgr.engage(r.delivery);
				mPoint2Req.put(r.delivery, r);
				nwm.addPoint(r.delivery);
				nwm.setWeight(r.delivery, -r.demand);
				oan.addPoint(r.delivery);
				
				log.println("At T = " + T.currentTimePoint + ", request " + r.ID + " arrives, delivery = " + r.delivery.ID + " demand = " + r.demand);
				/*
				if(XR.getNbRoutes() == 0){
					createNewRoute();
					System.out.println(name() + "::execute, T = " + T.currentTimePoint + ", XR.NbRoutes = 0 --> createNewRoute, request.delivery = " + r.delivery.ID + 
							", demand = " + r.demand + ", createNewRoute --> OK");
					log.println(name() + "::execute, XR.NbRoutes = 0 --> createNewRoute, request.delivery = " + r.delivery.ID + 
							", demand = " + r.demand + ", createNewRoute --> OK");
					
				}
				*/
				
				//strategy_select_shortest_route_insert_requets_to_last(r);
				//strategy_select_shortest_route_insert_greedy_requets(r);
				strategy_insert_greedy(r);
				
				
				log.println("At T = " + T.currentTimePoint + ", XR = " + XR.toString());
				
				//System.exit(-1);
				
				
				TDM.updateArrivalDepartureTimes();

				//System.out.println(name() + "::execute, before optimize, cost = " + cost.getValue());
				// local search performed here
				if (reoptimize) {
					reoptimize(5);
					TDM.updateArrivalDepartureTimes();
					System.out
							.println(name() + "::execute, after optimize, cost = "
									+ cost.getValue());
				}
			}
			System.out.println(name() + "::execute, current time point write cost = " + T.currentTimePoint + ", XR = " + XR.toString());
			for(int k = 1; k <= XR.getNbRoutes(); k++){
				System.out.println("cost[" + k + "] = " + mCost.get(k).getValue() + ", AccCap[" + k + "] = " 
			+ mAccCapacity.get(k).getValue());
				log.println("At T = " + T.currentTimePoint + ", cost[" + k + "] = " + mCost.get(k).getValue() + ", AccCap[" + k + "] = " 
						+ mAccCapacity.get(k).getValue());
			}
			
			//System.exit(-1);
			costovertime[T.currentTimePoint] = cost.getValue();
			T.move(dT);
			receiveRequests();

			//if(nbReq >=5) break;
		}

		double totalCost = 0;
		double maxLength = 0;
		System.out.println("Final results:");
		System.out.println("Routes = " + XR.toString());
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			totalCost += cost.get(k - 1).getValue();
			System.out.println("Cost[" + k + "] = "
					+ cost.get(k - 1).getValue());
			if(maxLength < mCost.get(k).getValue())
				maxLength = mCost.get(k).getValue();
		}
		System.out
				.println("totalCost = " + totalCost + " = " + cost.getValue() + ", maxLength = " + maxLength);
	}

	public void writeCostOverTimeToLog(String fn) {
		try {
			PrintWriter out = new PrintWriter(fn);
			int i = costovertime.length - 1;
			while (costovertime[i] < 0)
				i--;
			for (int t = 0; t <= i + 1; t = t + dT) {
				out.println(t + "\t" + costovertime[t]);
			}
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void finalize() {
		log.close();
	}

	public void printResultRoutes() {
		log.print(XR.toStringFull());
	}

	public static void runBatch(){
		int W = 20;// km
		int H = 20;// km
		int[] minDT = new int[] { 120, 300 };// seconds
		int[] maxDT = new int[] { 300, 900 };// seconds: the internal time
												// between two consecutive
												// request time points are
												// random of [minDT, maxDT]
		
		int minDemand = 10;
		int maxDemand = 50;
		int TimeHorizon = 72000;// 10h
		// String dir =
		// "C:\\DungPQ\\research\\projects\\cblsvr\\output\\osar\\";
		String dir = "output/ominmaxvrp/";
		// String ins = "example.txt";

		OMMVRP simgen = new OMMVRP();
		for (int i = 0; i <= 1; i++) {
			for (int j = 1; j <= 5; j++) {

				String ins = "ommvrp-W" + W + "-H" + H + "-minDT" + minDT[i]
						+ "-maxDT" + maxDT[i] + "-minDemand" + minDemand + "-maxDemand" + maxDemand + "-Horizon" + TimeHorizon
						+ ".ins" + j + ".txt";
				//System.out.println(ins); if(true)continue;
				
				//simgen.genData("data/ominmaxvrp/" + ins, W, H, minDT[i], maxDT[i],minDemand, maxDemand,
				//TimeHorizon);
				//if(true) continue;

				OMMVRP sim = new OMMVRP();

				sim.loadRequests("data/ominmaxvrp/" + ins);
				// sim.loadRequests("data\\Dial-A-Ride\\example.txt");
				sim.stateModel();
				// sim.simulate();
				boolean reoptimize = false;
				boolean oneone = true;
				sim.execute(reoptimize, oneone);
				sim.printResultRoutes();
				sim.writeCostOverTimeToLog(dir + ins
						+ "-costovertime-reoptimize-" + reoptimize
						+ "-one-one-" + oneone + ".txt");

				sim.finalize();
			}
		}
		
	}
	
	public static void run(){
		String dir = "output/ominmaxvrp/";
		
		String ins = "ommvrp-W20-H20-minDT300-maxDT900-minDemand10-maxDemand50-Horizon72000.ins1.txt";
		//System.out.println(ins); if(true)continue;
		
		//simgen.genData("data/ominmaxvrp/" + ins, W, H, minDT[i], maxDT[i],minDemand, maxDemand,
		//TimeHorizon);
		//if(true) continue;

		
		OMMVRP sim = new OMMVRP();

		sim.loadRequests("data/ominmaxvrp/" + ins);
		// sim.loadRequests("data\\Dial-A-Ride\\example.txt");
		sim.stateModel();
		// sim.simulate();
		boolean reoptimize = false;
		boolean oneone = true;
		sim.execute(reoptimize, oneone);
		sim.printResultRoutes();
		sim.writeCostOverTimeToLog(dir + ins
				+ "-costovertime-reoptimize-" + reoptimize
				+ "-one-one-" + oneone + ".txt");

		sim.finalize();
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		run();
	}

}
