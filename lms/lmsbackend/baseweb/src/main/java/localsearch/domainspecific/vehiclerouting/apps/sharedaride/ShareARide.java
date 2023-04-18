package localsearch.domainspecific.vehiclerouting.apps.sharedaride;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search.ALNSwithSA;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows.CEarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.RelatedPointBuckets;


public class ShareARide{
	
	public static final Logger LOGGER = Logger.getLogger("Logger");
	
	public static int PEOPLE =1 ;
	public static int GOOD = 0;
	int scale = 100000;
	ArrayList<Point> points;
	public ArrayList<Point> pickupPoints;
	public ArrayList<Point> deliveryPoints;
	ArrayList<Integer> type;
	ArrayList<Point> startPoints;
	ArrayList<Point> stopPoints;
	
	public ArrayList<Point> rejectPoints;
	public ArrayList<Point> rejectPickupGoods;
	public ArrayList<Point> rejectPickupPeoples;
	public HashMap<Point, Integer> earliestAllowedArrivalTime;
	public HashMap<Point, Integer> serviceDuration;
	public HashMap<Point, Integer> lastestAllowedArrivalTime;
	public HashMap<Point,Point> pickup2DeliveryOfGood;
	public HashMap<Point,Point> pickup2DeliveryOfPeople;
	public HashMap<Point, Point> pickup2Delivery;
	public HashMap<Point,Point> delivery2Pickup;
	public int nVehicle;
	public static int nRequest;
	public static double MAX_DISTANCE;
	
	Point badPick;
	HashMap<Integer, ArrayList<Point>> bestS;
	
	ArcWeightsManager awm;
	VRManager mgr;
	VarRoutesVR XR;
	ConstraintSystemVR S;
	public IFunctionVR objective;
	public CEarliestArrivalTimeVR ceat;
	LexMultiValues valueSolution;
	EarliestArrivalTimeVR eat;
	CEarliestArrivalTimeVR cEarliest;
	RelatedPointBuckets buckets;
	
	AccumulatedWeightEdgesVR accDisInvr;
	HashMap<Point, IFunctionVR> accDisF;
	
	int cntTimeRestart;
	int cntInteration;
	
	public ShareARide(Info info){
		
		this.nVehicle = info.nbVehicle;
		this.nRequest = info.nRequest;
		points = new ArrayList<Point>();
		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
		serviceDuration = new HashMap<Point, Integer>();
		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
		
		pickupPoints = new ArrayList<Point>();
		deliveryPoints = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		stopPoints = new ArrayList<Point>();
		type = new ArrayList<>();
		
		rejectPoints = new ArrayList<Point>();
		rejectPickupGoods = new ArrayList<Point>();
		rejectPickupPeoples = new ArrayList<Point>();
		
		bestS = new HashMap<Integer, ArrayList<Point>>();
		for(int i=1; i <= info.nbVehicle; ++i)
		{
			int startPointId = i+info.nRequest*2;
			Point sp = new Point(startPointId,info.p[startPointId].getX(),info.p[startPointId].getY());
			int stopPointId = i+info.nRequest*2+info.nbVehicle;
			Point tp = new Point(stopPointId,info.p[stopPointId].getX(),info.p[stopPointId].getY());
			points.add(sp);
			points.add(tp);
			startPoints.add(sp);
			stopPoints.add(tp);
			
			earliestAllowedArrivalTime.put(sp,(int)( info.earliest[startPointId]));
			serviceDuration.put(sp, (int)(info.serviceTime[startPointId]));
			lastestAllowedArrivalTime.put(sp,(int)( info.lastest[startPointId]));
			
			earliestAllowedArrivalTime.put(tp,(int)( info.earliest[stopPointId]));
			serviceDuration.put(tp, (int)(info.serviceTime[stopPointId]));
			lastestAllowedArrivalTime.put(tp,(int)( info.lastest[stopPointId]));
		}
		
		
	
		for(int i=0;i<info.nRequest; ++i)
		{
			Point pickup = new Point(i*2+1,info.p[i*2+1].getX(),info.p[i*2+1].getY());
			Point delivery = new Point(2*i+2,info.p[2*i+2].getX(),info.p[2*i+2].getY());

			points.add(pickup);
			points.add(delivery);
			
			pickupPoints.add(pickup);
			deliveryPoints.add(delivery);
			
			earliestAllowedArrivalTime.put(pickup,(int)( info.earliest[2*i+1]));
			serviceDuration.put(pickup, (int)(info.serviceTime[2*i+1]));
			lastestAllowedArrivalTime.put(pickup,(int)( info.lastest[2*i+1]));
			
			earliestAllowedArrivalTime.put(delivery,(int)( info.earliest[2*i+2]));
			serviceDuration.put(delivery, (int)(info.serviceTime[2*i+2]));
			lastestAllowedArrivalTime.put(delivery,(int)( info.lastest[2*i+2]));
			
			type.add(info.type[i*2+1]);
			
		}
		awm = new ArcWeightsManager(points);
		double max_dist = Double.MIN_VALUE;
		for(Point px: points){
			for(Point py: points){
				double tmp_cost = info.cost[px.getID()][py.getID()];
				awm.setWeight(px, py, tmp_cost*3600/70000);
				max_dist = tmp_cost > max_dist ? tmp_cost : max_dist;
			}
		}
		MAX_DISTANCE = max_dist;
	}

	public void stateModel() {
		pickup2DeliveryOfGood = new HashMap<Point, Point>();
		pickup2DeliveryOfPeople = new HashMap<Point, Point>();
		pickup2Delivery = new HashMap<Point, Point>();
		delivery2Pickup = new HashMap<Point, Point>();
		for(int i=0; i < nRequest; ++i)
		{
			Point pickup = pickupPoints.get(i);
			Point delivery = deliveryPoints.get(i);
			pickup2Delivery.put(pickup, delivery);
			delivery2Pickup.put(delivery, pickup);
			if(type.get(i)==PEOPLE)
			{
				pickup2DeliveryOfPeople.put(pickup, delivery);
			}
			else{
				pickup2DeliveryOfGood.put(pickup, delivery);
			}
		}
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		S = new ConstraintSystemVR(mgr);
		for(int i=0;i<nVehicle;++i)
			XR.addRoute(startPoints.get(i), stopPoints.get(i));
		
		for(int i=0;i<nRequest; ++i)
		{
			Point pickup = pickupPoints.get(i);
			Point delivery = deliveryPoints.get(i);
			XR.addClientPoint(pickup);
			XR.addClientPoint(delivery);
		}
		
		//IConstraintVR goodC = new CPickupDeliveryOfGoodVR(XR, pickup2DeliveryOfGood);
		//IConstraintVR peopleC = new CPickupDeliveryOfPeopleVR(XR, pickup2DeliveryOfPeople);
		//S.post(goodC);
		//S.post(peopleC);
	
		//time windows
		eat = new EarliestArrivalTimeVR(XR,awm,earliestAllowedArrivalTime,serviceDuration);
		cEarliest = new CEarliestArrivalTimeVR(eat, lastestAllowedArrivalTime);
		
		// new accumulated distance
		accDisInvr = new AccumulatedWeightEdgesVR(XR, awm);
		//function mapping a point to F calculate distance when route exchanged
		accDisF = new HashMap<Point, IFunctionVR>();
		for(Point p: XR.getAllPoints()){
			IFunctionVR f =new AccumulatedEdgeWeightsOnPathVR(accDisInvr, p);
			accDisF.put(p, f);
		}
		S.post(cEarliest);
		buckets = new RelatedPointBuckets(XR, pickup2DeliveryOfPeople, eat.getEarliestArrivalTime(), lastestAllowedArrivalTime, serviceDuration, 7200);
		objective = new TotalCostVR(XR,awm);
		valueSolution = new LexMultiValues();
		valueSolution.add(S.violations());
		valueSolution.add(objective.getValue());
		
		mgr.close();
	}
	
//	public void printBucket(){
//		for(int i = 0; i < buckets.nbBuckets; i++){
//			ArrayList<Point> bk = buckets.getBucketWithIndex(i);
//			String str = "Bucket " + i + ": ";
//			for(int j = 0; j < bk.size(); j++)
//				str += bk.get(j).ID + ", ";
//			LOGGER.log(Level.INFO,str);
//		}
//	}
	
	/*
	 * Init
	 */
	public void greedyInitSolution(){
		double currtime = System.currentTimeMillis();
		for(int i = 0; i < pickupPoints.size(); i++){
			//printBucket();
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) != Constants.NULL_POINT)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE; 
			boolean isPeople = pickup2DeliveryOfPeople.containsKey(pickup);

			for(int r = 1; r <= XR.getNbRoutes(); r++){
				for(Point p = XR.getStartingPointOfRoute(r); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					if(pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					if(isPeople){
						//check constraint
						if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
							//cost improve
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
							//LOGGER.log(Level.INFO,"people pick point : " + pickup.ID + " can be add into point " + p.ID + " and del point add into " + p.ID);
							//LOGGER.log(Level.INFO,"pickup point : " + pickup.ID + " can be add into point " + p.ID + " and del point add into " + p.ID + ",  cost = " + cost);

							if( cost < best_objective || (cost == best_objective && p.ID < pre_pick.ID)){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = p;
							}
						}
					}
					//point is good
					else{
						//int r = XR.route(p);
						for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
							if(pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
								continue;
							if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
								double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
								if( cost < best_objective || (cost == best_objective && p.ID < pre_pick.ID)){
									best_objective = cost;
									pre_pick = p;
									pre_delivery = q;
								}
							}
						}
					}
				}
			}
			//LOGGER.log(Level.INFO,"point : " + pickup + ", mark: " + marks.size());
			//LOGGER.log(Level.INFO,"Point " + pickup.ID + ", cnt = " + cnt);
			if((pre_pick == null || pre_delivery == null) && !rejectPoints.contains(pickup)){
				//LOGGER.log(Level.INFO,"point " + pickup + " is rejected");
				rejectPoints.add(pickup);
				rejectPoints.add(delivery);
				
				if(isPeople){
					rejectPickupPeoples.add(pickup);
				}else{
					rejectPickupGoods.add(pickup);
				}
				//rejectPickup.add(pickup);
				//rejectDelivery.add(delivery);
				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
			}
			else if(pre_pick != null && pre_delivery != null){
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
			}
		}
		LOGGER.log(Level.INFO,"people reject = " + rejectPoints.size()/2 + ", time for inserting reqs = " + (System.currentTimeMillis() - currtime)/1000);
	}
	
	public void greedyInitSolutionUsingBucket(){
		double currtime = System.currentTimeMillis();
		for(int i = 0; i < pickupPoints.size(); i++){
			//printBucket();
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) != Constants.NULL_POINT)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE; 
			boolean isPeople = pickup2DeliveryOfPeople.containsKey(pickup);

			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
//					if(pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
					//cnt++;
					if(isPeople){
						//check constraint
						if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
							//cost improve
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
							if( cost < best_objective || (cost == best_objective && p.ID < pre_pick.ID)){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = p;
							}
						}
					}
					//point is good
					else{
						//int r = XR.route(p);
						for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
							if(pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
								continue;
							if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
								double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
								if( cost < best_objective || (cost == best_objective && p.ID < pre_pick.ID)){
									best_objective = cost;
									pre_pick = p;
									pre_delivery = q;
								}
							}
						}
					}
				}
			}
			//LOGGER.log(Level.INFO,"point : " + pickup + ", mark: " + marks.size());
			//LOGGER.log(Level.INFO,"Point " + pickup.ID + ", cnt = " + cnt);
			if((pre_pick == null || pre_delivery == null) && !rejectPoints.contains(pickup)){
				//LOGGER.log(Level.INFO,"point " + pickup + " is rejected");
				rejectPoints.add(pickup);
				rejectPoints.add(delivery);
				
				if(isPeople){
					rejectPickupPeoples.add(pickup);
				}else{
					rejectPickupGoods.add(pickup);
				}
				//rejectPickup.add(pickup);
				//rejectDelivery.add(delivery);
				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
			}
			else if(pre_pick != null && pre_delivery != null){
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);				
			}
		}
		LOGGER.log(Level.INFO,"people reject = " + rejectPoints.size()/2 + ", time for inserting reqs = " + (System.currentTimeMillis() - currtime)/1000);
	}
    
//	public void InitSolutionByInsertGoodFirst(){
//		
//		/*
//		 * Insert good first
//		 * 		find best route and best position in route to insert
//		 */
//		double currtime = System.currentTimeMillis();
//		LOGGER.log(Level.INFO,"Insert good to route");
//		Iterator<Map.Entry<Point, Point>> it = pickup2DeliveryOfGood.entrySet().iterator();
//		
//		while(it.hasNext()){
//			Map.Entry<Point,Point> requestOfGood = it.next();
//			Point pickup = requestOfGood.getKey();
//			Point delivery = requestOfGood.getValue();
//			
//			Point pre_pick = null;
//			Point pre_delivery = null;
//			double best_objective = Double.MAX_VALUE; 
//			
//			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minRelatedBucketId <= maxRelatedBucketId){
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minRelatedBucketId));
//				//System.out.println("minRB = " + minRelatedBucketId + ", bk size = " + bk.size());
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					int r = XR.route(p);
//					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//						if(S.evaluateAddOnePoint(delivery, q) > 0)
//							continue;
//						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
//							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
//							if(cost < best_objective){
//								best_objective = cost;
//								pre_pick = p;
//								pre_delivery = q;
//							}
//						}
//					}
//				}
//				minRelatedBucketId++;
//			}
//			if(pre_pick == null || pre_delivery == null){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupGoods.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//			else if(pre_pick != null && pre_delivery != null){
//				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
//			}
//		}
//		
//		LOGGER.log(Level.INFO,"good reject = " + rejectPoints.size()/2 + ", time for inserting good reqs = " + (System.currentTimeMillis() - currtime)/1000);
//		currtime = System.currentTimeMillis();
//		
//		/*
//		 * Insert people
//		 * 		find best route and best position in route to insert
//		 */
//		
//		LOGGER.log(Level.INFO,"Insert people to route");
//		Iterator<Map.Entry<Point, Point>> it2 = pickup2DeliveryOfPeople.entrySet().iterator();
//
//		while(it2.hasNext()){
//			Map.Entry<Point,Point> requestOfPeople = it2.next();
//			Point pickup = requestOfPeople.getKey();
//			Point delivery = requestOfPeople.getValue();
//			
//			Point pre_pick = null;
//			Point pre_delivery = null;
//			double best_objective = Double.MAX_VALUE; 
//			
//			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minRelatedBucketId <= maxRelatedBucketId){
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minRelatedBucketId));
//				//System.out.println("minRB = " + minRelatedBucketId + ", bk size = " + bk.size());
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					
//					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
//						//cost improve
//						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
//						if( cost < best_objective){
//							best_objective = cost;
//							pre_pick = p;
//							pre_delivery = p;
//						}
//					}
//				}
//				minRelatedBucketId++;
//			}
//			if(pre_pick == null || pre_delivery == null){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupPeoples.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//			else if(pre_pick != null && pre_delivery != null){
//				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
//			}
//		}
//		LOGGER.log(Level.INFO,"time for inserting people reqs = " + (System.currentTimeMillis() - currtime)/1000);
//	}
//	
//	public void InitSolutionByInsertPeopleFirst(){
//		/*
//		 * Insert people
//		 * 		find best route and best position in route to insert
//		 */
//		double currtime = System.currentTimeMillis();
//		LOGGER.log(Level.INFO,"Insert people to route");
//		Iterator<Map.Entry<Point, Point>> it2 = pickup2DeliveryOfPeople.entrySet().iterator();
//
//		while(it2.hasNext()){
//			Map.Entry<Point,Point> requestOfPeople = it2.next();
//			Point pickup = requestOfPeople.getKey();
//			Point delivery = requestOfPeople.getValue();
//			
//			Point pre_pick = null;
//			Point pre_delivery = null;
//			double best_objective = Double.MAX_VALUE;
//			
//			int ear = earliestAllowedArrivalTime.get(pickup);
//			int late = lastestAllowedArrivalTime.get(pickup);
//			int i = (int)(ear / buckets.getDelta());
//			int maxRelatedBucketId = (int)(late / buckets.getDelta() + 1);
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(i <= maxRelatedBucketId){
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(i));
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					
//					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
//						//cost improve
//						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
//						if( cost < best_objective){
//							best_objective = cost;
//							pre_pick = p;
//							pre_delivery = p;
//						}
//					}
//				}
//				i++;
//			}
//			if(pre_pick == null || pre_delivery == null){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupPeoples.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//			else if(pre_pick != null && pre_delivery != null){
//				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
//			}
//		}
//		LOGGER.log(Level.INFO,"people reject = " + rejectPoints.size()/2 + ", time for inserting people reqs = " + (System.currentTimeMillis() - currtime)/1000);
//		currtime = System.currentTimeMillis();
//		/*
//		 * Insert good
//		 * 		find best route and best position in route to insert
//		 */
//		LOGGER.log(Level.INFO,"Insert good to route");
//		Iterator<Map.Entry<Point, Point>> it = pickup2DeliveryOfGood.entrySet().iterator();
//		
//		while(it.hasNext()){
//			Map.Entry<Point,Point> requestOfGood = it.next();
//			Point pickup = requestOfGood.getKey();
//			Point delivery = requestOfGood.getValue();
//			
//			Point pre_pick = null;
//			Point pre_delivery = null;
//			double best_objective = Double.MAX_VALUE;
//			int i = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()  + 1);
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(i <= maxRelatedBucketId){
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(i));
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					int r = XR.route(p);
//					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//						if(S.evaluateAddOnePoint(delivery, q) > 0)
//							continue;
//						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
//							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
//							if(cost < best_objective){
//								best_objective = cost;
//								pre_pick = p;
//								pre_delivery = q;
//							}
//						}
//					}
//				}
//				i++;
//			}
//			if(pre_pick == null || pre_delivery == null){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupGoods.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//			else if(pre_pick != null && pre_delivery != null){
//				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
//			}
//		}
//		LOGGER.log(Level.INFO,"time for inserting good reqs = " + (System.currentTimeMillis() - currtime)/1000);
//	}
//
//	public void firstPossibleInit(){
//		double currtime = System.currentTimeMillis();
//		for(int i = 0; i < pickupPoints.size(); i++){
//			Point pickup = pickupPoints.get(i);
//			if(XR.route(pickup) != Constants.NULL_POINT)
//				continue;
//			Point delivery = deliveryPoints.get(i);
//			//add the request to route
//			
//			boolean isPeople = pickup2DeliveryOfPeople.containsKey(pickup);
//			boolean finded = false;
//			
//			int minbkId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minbkId <= maxRelatedBucketId){
//				if(finded)
//					break;
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minbkId));
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(finded)
//						break;
//					
//					if(pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					
//					if(isPeople){
//						//check constraint
//						if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
//							//cost improve
//							mgr.performAddTwoPoints(pickup, p, delivery, p);
//							finded = true;
//						}
//					}
//					//point is good
//					else{
//						int r = XR.route(p);
//						for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//							if(pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
//								continue;
//							if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
//								mgr.performAddTwoPoints(pickup, p, delivery, q);
//								finded = true;
//								break;
//							}
//						}
//					}
//				}
//				minbkId++;
//			}
//			
//			if(!finded){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				
//				if(isPeople){
//					rejectPickupPeoples.add(pickup);
//				}else{
//					rejectPickupGoods.add(pickup);
//				}
//				//rejectPickup.add(pickup);
//				//rejectDelivery.add(delivery);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//		}
//		LOGGER.log(Level.INFO,"time for inserting reqs = " + (System.currentTimeMillis() - currtime)/1000);
//	}
//	
//	public void firstPossible_insertGoodFirst_init(){
//		/*
//		 * Insert good first
//		 * 		find best route and best position in route to insert
//		 */
//		double currtime = System.currentTimeMillis();
//		LOGGER.log(Level.INFO,"Insert good to route");
//		Iterator<Map.Entry<Point, Point>> it = pickup2DeliveryOfGood.entrySet().iterator();
//		
//		while(it.hasNext()){
//			Map.Entry<Point,Point> requestOfGood = it.next();
//			Point pickup = requestOfGood.getKey();
//			Point delivery = requestOfGood.getValue();
//			boolean finded = false;
//			
//			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minRelatedBucketId <= maxRelatedBucketId){
//				if(finded)
//					break;
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minRelatedBucketId));
//				//System.out.println("minRB = " + minRelatedBucketId + ", bk size = " + bk.size());
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(finded)
//						break;
//					
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					int r = XR.route(p);
//					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//						if(S.evaluateAddOnePoint(delivery, q) > 0)
//							continue;
//						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
//							mgr.performAddTwoPoints(pickup, p, delivery, q);
//							finded = true;
//							break;
//						}
//					}
//				}
//				minRelatedBucketId++;
//			}
//			if(!finded){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupGoods.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//		}
//		LOGGER.log(Level.INFO,"good reject = " + rejectPoints.size()/2 + ", time for inserting good reqs = " + (System.currentTimeMillis() - currtime)/1000);
//		currtime = System.currentTimeMillis();
//		/*
//		 * Insert people
//		 * 		find best route and best position in route to insert
//		 */
//
//		LOGGER.log(Level.INFO,"Insert people to route");
//		Iterator<Map.Entry<Point, Point>> it2 = pickup2DeliveryOfPeople.entrySet().iterator();
//
//		while(it2.hasNext()){
//			Map.Entry<Point,Point> requestOfPeople = it2.next();
//			Point pickup = requestOfPeople.getKey();
//			Point delivery = requestOfPeople.getValue();
//			
//			boolean finded = false;
//			
//			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minRelatedBucketId <= maxRelatedBucketId){
//				if(finded)
//					break;
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minRelatedBucketId));
//				//System.out.println("minRB = " + minRelatedBucketId + ", bk size = " + bk.size());
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					
//					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
//						mgr.performAddTwoPoints(pickup, p, delivery, p);
//						finded = true;
//						break;
//					}
//				}
//				minRelatedBucketId++;
//			}
//			if(!finded){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupPeoples.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//		}
//		LOGGER.log(Level.INFO,"time for inserting people reqs = " + (System.currentTimeMillis() - currtime)/1000);
//	}
//	
//	public void firstPossible_insertPeopleFirst_init(){
//		
//		/*
//		 * Insert people
//		 * 		find best route and best position in route to insert
//		 */
//		double currtime = System.currentTimeMillis();
//		LOGGER.log(Level.INFO,"Insert people to route");
//		Iterator<Map.Entry<Point, Point>> it2 = pickup2DeliveryOfPeople.entrySet().iterator();
//
//		while(it2.hasNext()){
//			Map.Entry<Point,Point> requestOfPeople = it2.next();
//			Point pickup = requestOfPeople.getKey();
//			Point delivery = requestOfPeople.getValue();
//			
//			boolean finded = false;
//			
//			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minRelatedBucketId <= maxRelatedBucketId){
//				if(finded)
//					break;
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minRelatedBucketId));
//				//System.out.println("minRB = " + minRelatedBucketId + ", bk size = " + bk.size());
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					
//					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
//						mgr.performAddTwoPoints(pickup, p, delivery, p);
//						finded = true;
//						break;
//					}
//				}
//				minRelatedBucketId++;
//			}
//			if(!finded){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupPeoples.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//		}
//		LOGGER.log(Level.INFO,"people reject = " + rejectPoints.size()/2 + ", time for inserting people reqs = " + (System.currentTimeMillis() - currtime)/1000);
//		currtime = System.currentTimeMillis();
//		/*
//		 * Insert good
//		 * 		find best route and best position in route to insert
//		 */
//		LOGGER.log(Level.INFO,"Insert good to route");
//		Iterator<Map.Entry<Point, Point>> it = pickup2DeliveryOfGood.entrySet().iterator();
//		
//		while(it.hasNext()){
//			Map.Entry<Point,Point> requestOfGood = it.next();
//			Point pickup = requestOfGood.getKey();
//			Point delivery = requestOfGood.getValue();
//			boolean finded = false;
//			
//			int minRelatedBucketId = (int)(earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
//			int maxRelatedBucketId = (int)(lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
//			ArrayList<Integer> marks = new ArrayList<Integer>();
//			if(maxRelatedBucketId >= buckets.nbBuckets)
//				maxRelatedBucketId = buckets.nbBuckets - 1;
//			while(minRelatedBucketId <= maxRelatedBucketId){
//				if(finded)
//					break;
//				ArrayList<Point> bk = new ArrayList<Point>(buckets.getBucketWithIndex(minRelatedBucketId));
//				//System.out.println("minRB = " + minRelatedBucketId + ", bk size = " + bk.size());
//				for(Point p : bk){
//					if(marks.contains(p.ID))
//						continue;
//					marks.add(p.ID);
//					if(finded)
//						break;
//					
//					if(S.evaluateAddOnePoint(pickup, p) > 0)
//						continue;
//					int r = XR.route(p);
//					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//						if(S.evaluateAddOnePoint(delivery, q) > 0)
//							continue;
//						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
//							mgr.performAddTwoPoints(pickup, p, delivery, q);
//							finded = true;
//							break;
//						}
//					}
//				}
//				minRelatedBucketId++;
//			}
//			if(!finded){
//				rejectPoints.add(pickup);
//				rejectPoints.add(delivery);
//				rejectPickupGoods.add(pickup);
//				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
//			}
//		}
//		LOGGER.log(Level.INFO,"time for inserting good reqs = " + (System.currentTimeMillis() - currtime)/1000);
//	}
	
	public SolutionShareARide search(int maxIter, int timeLimit, SearchInput si){
		ALNSwithSA alns = new ALNSwithSA(buckets, mgr, objective, S, eat, awm, si);
		return alns.search(maxIter, timeLimit);
	}
	
	public static void main(String []args){		
		try {		
			//for(int i=0; i<20; i++){
			//	for(int j=2; j<14; j++){
		    		String inData = "data/SARP-offline/n1027r10_1.txt";
		        	
		        	int timeLimit = 36000000;
		        	int nIter = 300;
		        	
		        	Handler fileHandler;
		        	Formatter simpleFormater;
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
					Date date = new Date();
					//System.out.println(dateFormat.format(date));
					
					fileHandler = new FileHandler("output/SARP-offline/n1027r10/"+ dateFormat.format(date) + "_NobucketTestStartEndBucket_greedyInitSolution_Removal_14_Insertion_14.txt");
					simpleFormater = new SimpleFormatter();
					
					LOGGER.addHandler(fileHandler);
			    	
					fileHandler.setFormatter(simpleFormater);
					
//					String description = "\n\n\t RUN WITH 13 REMOVAL AND 14 INSERTION (3,1,-5,1) \n\n";
//					LOGGER.log(Level.INFO, description);
					
					LOGGER.log(Level.INFO, "Read data");
					Info info = new Info(inData);
					ShareARide sar = new ShareARide(info);
						
					LOGGER.log(Level.INFO, "Read data done --> Create model");
					sar.stateModel();

					LOGGER.log(Level.INFO, "Create model done --> Init solution");
					double currTime = System.currentTimeMillis();
					//sar.InitSolutionByInsertGoodFirst();
					sar.greedyInitSolution();
					//sar.firstPossibleInit();
					//sar.firstPossible_insertGoodFirst_init();
					//sar.InitSolutionByInsertPeopleFirst();
					//sar.firstPossible_insertPeopleFirst_init();
					//sar.InitSolutionByInsertPeopleFirst();
					//sar.InitSolutionByInsertPeopleFirstGreedyLoopAll();
					
					LOGGER.log(Level.INFO,"Init solution done. At start search number of reject points = "+sar.rejectPoints.size()/2
							+"    violations = "+sar.S.violations()+"   cost = "+sar.objective.getValue() + ", init time = " + (System.currentTimeMillis() - currTime)/1000);
					SearchInput si = new SearchInput(sar.pickupPoints, sar.deliveryPoints, sar.rejectPoints, 
							sar.rejectPickupGoods, sar.rejectPickupPeoples, sar.earliestAllowedArrivalTime, sar.serviceDuration, 
							sar.lastestAllowedArrivalTime, sar.pickup2DeliveryOfGood, sar.pickup2DeliveryOfPeople, sar.pickup2Delivery, sar.delivery2Pickup);
					SolutionShareARide best_solution   = sar.search(nIter, timeLimit,si);
						
					LOGGER.log(Level.INFO,"Search done. At end search number of reject points = "+best_solution.get_rejectPoints().size()/2+"   cost = "+best_solution.get_cost());
					
					LOGGER.log(Level.INFO,best_solution.toString());
					
					fileHandler.close();
				//}
			//}
		} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
    }
}

