/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * start date: 07/11/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.online.app.osar;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Request;
import localsearch.domainspecific.vehiclerouting.vrp.entities.RequestsQueue;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.MoveTwoPointsMove;
//import localsearch.domainspecific.vehiclerouting.vrp.online.EuclideanTimeDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.online.TimeDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.online.TimeHorizon;
//import localsearch.domainspecific.vehiclerouting.vrp.online.TimeManager;
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

public class NiceOSARP {
	private VRManagerOnline mgr;
	private VarRoutesVROnline XR;
	private OAccumulatedWeightEdges oae;
	private OSumVR cost;
	private HashMap<Integer, OFunctionVR> mCost;
	private VREuclideanTimeDistanceManager TDM;
	private TimeHorizon T;
	private int timehorizon;
	private double speed = 1.0 / 60;// km per second
	private int dT = 10;
	private int maxNbClientsPerRoute = 5;
	private ArrayList<DARPRequest> requests;
	private HashMap<Request, Point> location;
	private HashMap<Point, DARPRequest> mPoint2Req;
	private Point depot;
	private RequestsQueue requestQueue;
	private PrintWriter log = null;

	private double bestEval;
	private double[] costovertime;

	public NiceOSARP() {
		try {
			log = new PrintWriter(new File("OSARP-log.txt"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void genData(String fn, int W, int H, int minDT, int maxDT,
			int TimeHorizon) {
		try {
			PrintWriter out = new PrintWriter(fn);
			java.util.Random R = new java.util.Random();

			out.println("depot (x,y)");
			out.println("0 0");
			out.println("time horizon");
			out.println(TimeHorizon);
			out.println("#id x(pickup) y(pickup) x(delivery) y(delivery) arrival_time people (0) or parcel (1)");
			int id = 0;
			int t = 0;
			while (true) {
				int DT = R.nextInt(maxDT - minDT + 1) + minDT;
				t = t + DT;
				if (t > TimeHorizon)
					break;
				int reqType = R.nextInt(2);// 0 = people, 1 = parcel
				int x_pickup = R.nextInt(W);
				int y_pickup = R.nextInt(H);
				int x_delivery = R.nextInt(W);
				int y_delivery = R.nextInt(H);
				out.println(id + " " + x_pickup + " " + y_pickup + " "
						+ x_delivery + " " + y_delivery + " " + t + " "
						+ reqType);
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
			requests = new ArrayList<DARPRequest>();
			location = new HashMap<Request, Point>();
			Scanner in = new Scanner(new File(fn));
			String s = in.nextLine();
			// System.out.println("s = " + s);
			int depot_x = in.nextInt();
			int depot_y = in.nextInt();
			s = in.nextLine();
			s = in.nextLine();
			timehorizon = in.nextInt();
			timehorizon = 100000;
			s = in.nextLine();
			s = in.nextLine();
			int idPoint = 0;
			// System.out.println("s = " + s);
			while (true) {
				int id = in.nextInt();
				// System.out.println("id = " + id);
				if (id == -1)
					break;
				int pickup_x = in.nextInt();
				int pickup_y = in.nextInt();
				int delivery_x = in.nextInt();
				int delivery_y = in.nextInt();
				int time = in.nextInt();
				int type = in.nextInt();
				if (id > 0) {
					idPoint++;
					Point pickup = new Point(idPoint, pickup_x, pickup_y);
					idPoint++;
					Point delivery = new Point(idPoint, delivery_x, delivery_y);
					DARPRequest req = null;
					if (type == 0)
						req = new DARPRequest(id, time, pickup, delivery,
								RequestType.PEOPLE_REQUEST);
					else
						req = new DARPRequest(id, time, pickup, delivery,
								RequestType.PARCEL_REQUEST);
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

	public boolean pickupPoint(Point x) {
		DARPRequest r = mPoint2Req.get(x);
		return x == r.pickup;
	}

	public boolean delivery(Point x) {
		DARPRequest r = mPoint2Req.get(x);
		return x == r.delivery;
	}

	public boolean peopleRequestPoint(Point x) {
		return mPoint2Req.get(x).type == RequestType.PEOPLE_REQUEST;
	}

	public boolean parcelRequestPoint(Point x) {
		return mPoint2Req.get(x).type == RequestType.PARCEL_REQUEST;
	}

	public String name() {
		return "Simulator";
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
		oae = new OAccumulatedWeightEdges(XR, TDM);
		oae.setLog(log);
		cost = new OSumVR(mgr);// new
								// ArrayList<OAccumulatedEdgeWeightsOnPath>();
		T = new TimeHorizon(0, timehorizon, timehorizon);
		requestQueue = new RequestsQueue();
		mPoint2Req = new HashMap<Point, DARPRequest>();
		mCost = new HashMap<Integer, OFunctionVR>();
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

	public Point getFirstParcelPoint(int k) {
		Point v = XR.next(XR.getStartingPointOfRoute(k));
		for (Point x = v; x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)) {
			if (peopleRequestPoint(x))
				v = XR.next(x);
		}
		if (v == XR.getTerminatingPointOfRoute(k))
			v = null;
		return v;
	}

	public Point getLastPeoplePoint(int k) {
		Point v = null;
		Point start = XR.next(XR.getStartingPointOfRoute(k));
		for (Point x = start; x != XR.getTerminatingPointOfRoute(k); x = XR
				.next(x)) {
			if (peopleRequestPoint(x))
				v = x;
		}
		return v;
	}

	private void exploreTwoPointsMove(Neighborhood N) {
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			Point s = getFirstParcelPoint(k);
			if (s == null)
				continue;
			for (Point x = s; x != XR.endPoint(k); x = XR.next(x)) {
				for (Point y = XR.next(x); y != XR.endPoint(k); y = XR.next(y)) {
					if (mPoint2Req.get(x) == mPoint2Req.get(y)) {
						for (int k1 = 1; k1 <= XR.getNbRoutes(); k1++) {
							Point s1 = getLastPeoplePoint(k1);
							if (s1 == null)
								s1 = XR.startPoint(k1);
							for (Point x1 = s1; x1 != XR.endPoint(k1); x1 = XR
									.next(x1)) {
								for (Point y1 = x1; y1 != XR.endPoint(k1); y1 = XR
										.next(y1)) {
									if (x != x1 && x != y1 && y != y1
											&& y != x1 && x1 != XR.prev(x)
											&& y1 != XR.prev(y)) {
										double eval = cost
												.evaluateTwoPointsMove(x, y,
														x1, y1);
										if (eval < bestEval && eval < 0) {
											N.clear();
											N.add(new MoveTwoPointsMove(mgr,
													eval, x, y, x1, y1));
											bestEval = eval;
										} else if (Math.abs(eval - bestEval) < CBLSVR.EPSILON) {
											N.add(new MoveTwoPointsMove(mgr,
													eval, x, y, x1, y1));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void exploreTwoPointsMoveOld(Neighborhood N) {
		// move one request to another position

		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			Point s = getFirstParcelPoint(k);
			Point t = XR.endPoint(k);
			if (s == null)
				continue;
			for (Point x = s; x != t; x = XR.next(x)) {
				for (Point y = XR.next(x); y != XR.endPoint(k); y = XR.next(y)) {
					if (x != t && y != t && XR.index(x) < XR.index(y)
							&& mPoint2Req.get(x) == mPoint2Req.get(y)) {
						for (int k1 = 1; k1 <= XR.getNbRoutes(); k1++) {
							Point s1 = getLastPeoplePoint(k1);
							if (s1 == null)
								s1 = XR.startPoint(k1);
							Point t1 = XR.endPoint(k1);
							for (Point x1 = s1; x1 != t1; x1 = XR.next(x1)) {
								for (Point y1 = s1; y1 != t1; y1 = XR.next(y1)) {
									if (x1 != t1 && y1 != t1
											&& XR.index(x1) <= XR.index(y1)
											&& x != x1 && x != y1 && y != y1
											&& y != x1 && x1 != XR.prev(x)
											&& y1 != XR.prev(y)) {
										double eval = cost
												.evaluateTwoPointsMove(x, y,
														x1, y1);
										if (eval < bestEval && eval < 0) {
											N.clear();
											N.add(new MoveTwoPointsMove(mgr,
													eval, x, y, x1, y1));
											bestEval = eval;
										} else if (Math.abs(eval - bestEval) < CBLSVR.EPSILON) {
											N.add(new MoveTwoPointsMove(mgr,
													eval, x, y, x1, y1));
										}
									}
								}
							}
						}
					}
				}
			}
		}
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

	public int findNearestRoute(DARPRequest r) {
		Point pickup = r.pickup;
		double minD = CBLSVR.MAX_INT;
		int sel_r = -1;
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			if (getLastPeoplePoint(k) != null)
				continue;// do not consider routes containing people
			Point s = XR.startPoint(k);
			double d = TDM.getDistance(s, pickup);
			if (d < minD) {
				minD = d;
				sel_r = k;
			}
		}
		return sel_r;
	}

	public int createNewRoute() {
		Point sp = depot.clone();
		Point tp = depot.clone();
		XR.addRoute(sp, tp);
		mgr.engage(sp);
		mgr.engage(tp);
		OAccumulatedEdgeWeightsOnPath f = new OAccumulatedEdgeWeightsOnPath(
				oae, XR.endPoint(XR.getNbRoutes()));
		cost.add(f);
		mCost.put(XR.getNbRoutes(), f);
		TDM.setSpeed(XR.getNbRoutes(), speed);
		return XR.getNbRoutes();

	}

	private void init() {
		// System.out.println(name() + "::simulate1 start....");
		T = new TimeHorizon(0, timehorizon, timehorizon);
		requestQueue = new RequestsQueue();
		mPoint2Req = new HashMap<Point, DARPRequest>();

		costovertime = new double[timehorizon];
		Arrays.fill(costovertime, -1);
	}

	private int insertPeople(DARPRequest r) {
		int sel_r = findNearestRoute(r);
		if (sel_r == -1) {
			sel_r = createNewRoute();
		} else {
			Point p = XR.startPoint(sel_r);
			if (p.distance(r.pickup) > depot.distance(r.pickup))
				sel_r = createNewRoute();
		}

		mgr.performAddOnePoint(r.pickup, XR.startPoint(sel_r));
		mgr.performAddOnePoint(r.delivery, r.pickup);
		return sel_r;
	}

	private int insertRequestOneOne(DARPRequest r) {
		int sel_r = createNewRoute();
		mgr.performAddOnePoint(r.pickup, XR.startingPoint(sel_r));
		mgr.performAddOnePoint(r.delivery, r.pickup);
		return sel_r;
	}

	private int findShortestRoute() {
		int sel_r = -1;
		double minD = CBLSVR.MAX_INT;
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			if (minD > mCost.get(k).getValue()) {
				minD = mCost.get(k).getValue();
				sel_r = k;
			}
		}

		return sel_r;
	}

	private void improveInsertionParcel(DARPRequest r) {
		double minDelta = CBLSVR.MAX_INT;
		Point sel_x = null;
		Point sel_y = null;
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			Point v = getLastPeoplePoint(k);
			if (v == null)
				v = XR.startPoint(k);

			for (Point x = v; x != XR.endPoint(k); x = XR.next(x)) {
				for (Point y = x; y != XR.endPoint(k); y = XR.next(y)) {
					if (r.pickup != x && r.pickup != y && r.delivery != x
							&& r.delivery != y) {
						double d = cost.evaluateTwoPointsMove(r.pickup,
								r.delivery, x, y);
						if (d < minDelta) {
							minDelta = d;
							sel_x = x;
							sel_y = y;
						}
					}
				}
			}
		}
		mgr.performTwoPointsMove(r.pickup, r.delivery, sel_x, sel_y);

	}

	private int insertParcel(DARPRequest r) {
		int sel_r = findShortestRoute();
		if (sel_r == -1)
			sel_r = createNewRoute();
		Point p = XR.prev(XR.endPoint(sel_r));
		mgr.performAddOnePoint(r.pickup, p);
		mgr.performAddOnePoint(r.delivery, r.pickup);

		// System.out.println("OSAR::insertParcel, before improving, cost = " +
		// cost.getValue());
		// improveInsertionParcel(r);
		// System.out.println("OSAR::insertParcel, after improving, cost = " +
		// cost.getValue());
		return sel_r;
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

	public void execute(boolean reoptimize, boolean oneone) {
		init();
		receiveRequests();
		while (!T.finished()) {
			if (finishExecute())
				break;
			mgr.update(T.currentTimePoint + dT);
			while (requestQueue.size() > 0) {
				System.out.println("Remains requets.sz = " + requests.size());
				DARPRequest r = (DARPRequest) requestQueue.pop();
				mgr.engage(r.pickup);
				mgr.engage(r.delivery);
				mPoint2Req.put(r.pickup, r);
				mPoint2Req.put(r.delivery, r);

				int sel_r = -1;
				if (r.type == RequestType.PEOPLE_REQUEST) {
					if (oneone)
						sel_r = insertRequestOneOne(r);
					else
						sel_r = insertPeople(r);

				} else {
					if (oneone)
						sel_r = insertRequestOneOne(r);
					else
						sel_r = insertParcel(r);

				}

				if (TDM.getDepartureTime(XR.startPoint(sel_r)) < 0)
					TDM.setDepartureTime(XR.startPoint(sel_r),
							T.currentTimePoint + dT);
				XR.setMoving(sel_r, true);
				TDM.updateArrivalDepartureTimes();

				System.out
						.println("NiceOSARP::execute, before optimize, cost = "
								+ cost.getValue());
				// local search performed here
				if (reoptimize) {
					reoptimize(5);
					TDM.updateArrivalDepartureTimes();
					System.out
							.println("NiceOSARP::execute, after optimize, cost = "
									+ cost.getValue());
				}
			}
			System.out
					.println("NiceOSARP::execute, current time point write cost = "
							+ T.currentTimePoint);
			costovertime[T.currentTimePoint] = cost.getValue();
			T.move(dT);
			receiveRequests();

		}

		double totalCost = 0;
		System.out.println("Final results:");
		System.out.println("Routes = " + XR.toString());
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			totalCost += cost.get(k - 1).getValue();
			System.out.println("Cost[" + k + "] = "
					+ cost.get(k - 1).getValue());
		}
		System.out
				.println("totalCost = " + totalCost + " = " + cost.getValue());
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int W = 20;// km
		int H = 20;// km
		int[] minDT = new int[] { 120, 300 };// seconds
		int[] maxDT = new int[] { 300, 900 };// seconds: the internal time
												// between two consecutive
												// request time points are
												// random of [minDT, maxDT]
		int TimeHorizon = 72000;// 10h
		// String dir =
		// "C:\\DungPQ\\research\\projects\\cblsvr\\output\\osar\\";
		String dir = "output/osar/";
		// String ins = "example.txt";

		for (int i = 0; i <= 1; i++) {
			for (int j = 1; j <= 5; j++) {

				String ins = "osar-W" + W + "-H" + H + "-minDT" + minDT[i]
						+ "-maxDT" + maxDT[i] + "-Horizon" + TimeHorizon
						+ ".ins" + j + ".txt";
				//System.out.println(ins); if(true)continue;
				
				// sim.genData("data\\Dial-A-Ride\\" + ins, W, H, minDT, maxDT,
				// TimeHorizon);
				// if(true) return;

				NiceOSARP sim = new NiceOSARP();

				sim.loadRequests("data/osar/" + ins);
				// sim.loadRequests("data\\Dial-A-Ride\\example.txt");
				sim.stateModel();
				// sim.simulate();
				boolean reoptimize = true;// false;
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

}
