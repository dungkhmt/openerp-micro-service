package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;

import com.google.gson.Gson;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.ShareARide;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.SolutionShareARide;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.constraints.ContainerCapacityConstraint;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.constraints.ContainerCarriedByTrailerConstraint;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.constraints.MoocCapacityConstraint;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Container;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ContainerTruckMoocInput;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotContainer;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotMooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotTruck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DistanceElement;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Mooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Port;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.RouteElement;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ShipCompany;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.StatisticInformation;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Truck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.TruckContainerSolution;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.TruckMoocContainerOutputJson;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.TruckRoute;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Warehouse;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows.CEarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.CapacityConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.utils.DateTimeUtils;

public class TruckContainerSolver {
	public ContainerTruckMoocInput input;
	
	ArrayList<Point> points;
	public ArrayList<Point> pickupPoints;
	public ArrayList<Point> deliveryPoints;
	public ArrayList<Point> rejectPickupPoints;
	public ArrayList<Point> rejectDeliveryPoints;
	ArrayList<Point> startPoints;
	ArrayList<Point> stopPoints;
	ArrayList<Point> startMoocPoints;
	ArrayList<Point> stopMoocPoints;
	HashMap<Point, String> point2Type;
	
	public HashMap<Point, Integer> earliestAllowedArrivalTime;
	public HashMap<Point, Integer> serviceDuration;
	public HashMap<Point, Integer> lastestAllowedArrivalTime;
	public HashMap<Point,Point> pickup2DeliveryOfGood;
	public HashMap<Point,Point> pickup2DeliveryOfPeople;
	public HashMap<Point, Point> pickup2Delivery;
	public HashMap<Point,Point> delivery2Pickup;
	
	public HashMap<Point, Point> start2stopMoocPoint;
	public HashMap<Point,Point> stop2startMoocPoint;
	
	public HashMap<Point, Truck> startPoint2Truck;
	public HashMap<Point, Mooc> startPoint2Mooc;
	
	public HashMap<Point, Integer> point2Group;
	public HashMap<Integer, Integer> group2marked;
	public HashMap<Integer, ExportEmptyRequests> group2EE;
	public HashMap<Integer, ExportLadenRequests> group2EL;
	public HashMap<Integer, ImportEmptyRequests> group2IE;
	public HashMap<Integer, ImportLadenRequests> group2IL;
	
	public HashMap<Point, Integer> point2moocWeight;
	public HashMap<Point, Integer> point2containerWeight;
	
	public HashMap<Integer, Point> route2DeliveryMooc;
	
	public static int nVehicle;
	public static int nRequest;
	
	
	public String[] locationCodes;
	public HashMap<String, Integer> mLocationCode2Index;
	public double[][] distance;// distance[i][j] is the distance from location
								// index i to location index j
	public double[][] travelTime;// travelTime[i][j] is the travel time from
									// location index i to location index j
	
	public HashMap<String, Truck> mCode2Truck;
	public HashMap<String, Mooc> mCode2Mooc;
	public HashMap<String, Container> mCode2Container;
	public HashMap<String, DepotContainer> mCode2DepotContainer;
	public HashMap<String, DepotTruck> mCode2DepotTruck;
	public HashMap<String, DepotMooc> mCode2DepotMooc;
	public HashMap<String, Warehouse> mCode2Warehouse;
	public HashMap<String, Port> mCode2Port;
	public ArrayList<Container> additionalContainers;
	
	public ExportEmptyRequests[] exEmptyRequests;
	public ExportLadenRequests[] exLadenRequests;
	public ImportEmptyRequests[] imEmptyRequests;
	public ImportLadenRequests[] imLadenRequests;
	
	ArcWeightsManager awm;
	VRManager mgr;
	VarRoutesVR XR;
	ConstraintSystemVR S;
	IFunctionVR objective;
	CEarliestArrivalTimeVR ceat;
	LexMultiValues valueSolution;
	EarliestArrivalTimeVR eat;
	CEarliestArrivalTimeVR cEarliest;
	ContainerCapacityConstraint capContCtr;
	MoocCapacityConstraint capMoocCtr;
	ContainerCarriedByTrailerConstraint contmoocCtr;
	
	NodeWeightsManager nwMooc;
	NodeWeightsManager nwContainer;
	AccumulatedWeightNodesVR accMoocInvr;
	AccumulatedWeightNodesVR accContainerInvr;
	HashMap<Point, IFunctionVR> accDisF;
	
	HashMap<Point, Integer> nChosed;
	HashMap<Point, Boolean> removeAllowed;
	
	public int nRemovalOperators = 8;
	public int nInsertionOperators = 8;
	
	//parameters
	public int lower_removal;
	public int upper_removal;
	public int sigma1 = 3;
	public int sigma2 = 1;
	public int sigma3 = -5;
	public double rp = 0.1;
	public int nw = 1;
	public double shaw1st = 0.5;
	public double shaw2nd = 0.2;
	public double shaw3rd = 0.1;
	public double temperature = 200;
	public double cooling_rate = 0.9995;
	public int nTabu = 5;
	int timeLimit = 36000000;
	int nIter = 30000;
	int maxStable = 1000;
	
	int INF_TIME = Integer.MAX_VALUE;
	public static double MAX_TRAVELTIME;
	public static final String START_TRUCK 	= "START_TRUCK";
	public static final String END_TRUCK 	= "END_TRUCK";
	public static final String START_MOOC 	= "PICKUP_MOOC";
	public static final String END_MOOC 	= "DELIVERY_MOOC";
	public static final String START_CONT 	= "PICKUP_EMPTYCONT";
	public static final String END_CONT 	= "DELIVERY_EMPTYCONT";
	public static final String PORT_PICKUP_EMPTYCONT	= "PORT_PICKUP_EMPTYCONT";
	public static final String PORT_PICKUP_FULLCONT		= "PORT_PICKUP_FULLCONT";
	public static final String PORT_DELIVERY_EMPTYCONT	= "PORT_DELIVERY_EMPTYCONT";
	public static final String PORT_DELIVERY_FULLCONT	= "PORT_DELIVERY_FULLCONT";
	public static final String WH_PICKUP_EMPTYCONT 	= "WH_PICKUP_EMPTYCONT";
	public static final String WH_PICKUP_FULLCONT 	= "WH_PICKUP_FULLCONT";
	public static final String WH_DELIVERY_EMPTYCONT = "WH_DELIVERY_EMPTYCONT";
	public static final String WH_DELIVERY_FULLCONT 	= "WH_DELIVERY_FULLCONT";
	
	public TruckContainerSolver(){
		
	}
	
	public void adaptiveSearchOperators(String outputfile){	
		int it = 0;
		
    	int iS = 0;
    	
    	
    	//insertion operators selection probabilities
		double[] pti = new double[nInsertionOperators];
		//removal operators selection probabilities
		double[] ptd = new double[nRemovalOperators];
		
		//wi - number of times used during last iteration
		int[] wi = new int[nInsertionOperators];
		int[] wd = new int[nRemovalOperators];
		
		//pi_i - score of operator
		int[] si = new int[nInsertionOperators];
		int[] sd = new int[nRemovalOperators];
		
		
		//init probabilites
		for(int i=0; i<nInsertionOperators; i++){
			pti[i] = 1.0/nInsertionOperators;
			wi[i] = 1;
			si[i] = 0;
		}
		for(int i=0; i<nRemovalOperators; i++){
			ptd[i] = 1.0/nRemovalOperators;
			wd[i] = 1;
			sd[i] = 0;
		}
    	
    	SearchOptimumSolution opt = new SearchOptimumSolution(this);
    	
		double best_cost = objective.getValue();

		TruckContainerSolution best_solution = new TruckContainerSolution(XR, rejectPickupPoints,
				rejectDeliveryPoints, best_cost, getNbUsedTrucks(), getNbRejectedRequests(), point2Group, group2marked);

		double start_search_time = System.currentTimeMillis();
		try{
			
			FileOutputStream write = new FileOutputStream(outputfile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println("time limit = " + timeLimit + ", nbIters = " + nIter + ", maxStable = " + maxStable);
			fo.println("#Request = " + nRequest);
			fo.println("iter=====insertion=====removal=====time=====cost=====nbReject=====nbTrucks");
			fo.println("0 -1 -1 " + " " + System.currentTimeMillis()/1000 + " " + best_cost + " " + getNbRejectedRequests() + " " + getNbUsedTrucks());
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		while( (System.currentTimeMillis()-start_search_time) < timeLimit && it++ < nIter){
			System.out.println("nb of iterator: " + it);
			double current_cost = objective.getValue();
			int current_nbTrucks = getNbUsedTrucks();
			TruckContainerSolution current_solution = new TruckContainerSolution(XR, rejectPickupPoints, 
				rejectDeliveryPoints, current_cost, current_nbTrucks, getNbRejectedRequests(), point2Group, group2marked);
			
			removeAllMoocFromRoutes();
			
			int i_selected_removal = -1;
			if(iS >= maxStable){
				opt.allRemoval();
				iS = 0;
			}
			else{
				i_selected_removal = get_operator(ptd);
				//i_selected_removal = idxRemoval;
				wd[i_selected_removal]++;
				switch(i_selected_removal){
					case 0: opt.routeRemoval(); break;
					case 1: opt.randomRequestRemoval(); break;
					case 2: opt.shaw_removal(); break;
					case 3: opt.worst_removal(); break;
					case 4: opt.forbidden_removal(0); break;
					case 5: opt.forbidden_removal(1); break;
					case 6: opt.forbidden_removal(2); break;
					case 7: opt.forbidden_removal(3); break;
				}
			}
			
			
			int i_selected_insertion = get_operator(pti);
			//int i_selected_insertion =idxRemoval ;
			wi[i_selected_insertion]++;
			switch(i_selected_insertion){
				case 0: opt.greedyInsertion(); break;
				case 1: opt.greedyInsertionWithNoise(); break;
				case 2: opt.regret_n_insertion(2); break;
				case 3: opt.first_possible_insertion(); break;
				case 4: opt.sort_before_insertion(0); break;
				case 5: opt.sort_before_insertion(1); break;
				case 6: opt.sort_before_insertion(2); break;
				case 7: opt.sort_before_insertion(3); break;
			}
			
			//insertMoocToRoutes();
			//System.out.println("s.vio = " + S.violations());
			int new_nb_reject_points = rejectPickupPoints.size();
			double new_cost = objective.getValue();
			int new_nbTrucks = getNbUsedTrucks();
			int current_nb_reject_points = current_solution.get_rejectPickupPoints().size();

			if( new_nb_reject_points < current_nb_reject_points
					|| (new_nb_reject_points == current_nb_reject_points && new_cost < current_cost)){
				int best_nb_reject_points = best_solution.get_rejectPickupPoints().size();
				int best_nbTrucks = best_solution.get_nbTrucks();
				
				if(new_nb_reject_points < best_nb_reject_points
						|| (new_nb_reject_points == best_nb_reject_points && new_cost < best_cost)){
					
					best_cost = new_cost;
					best_solution = new TruckContainerSolution(XR, rejectPickupPoints, rejectDeliveryPoints,
							new_cost, new_nbTrucks, new_nb_reject_points, point2Group, group2marked);
					try{
						FileOutputStream write = new FileOutputStream(outputfile, true);
						PrintWriter fo = new PrintWriter(write);
						fo.println(it + " " + i_selected_insertion 
							+ " " + i_selected_removal + " "
							+ System.currentTimeMillis()/1000 + " "
							+ best_cost + " " + getNbRejectedRequests() + " " + getNbUsedTrucks());
						fo.close();
					}catch(Exception e){
						System.out.println(e);
					}
					si[i_selected_insertion] += sigma1;
					if(i_selected_removal >= 0)
						sd[i_selected_removal] += sigma1;
				}
				else{
					si[i_selected_insertion] += sigma2;
					if(i_selected_removal >= 0)
						sd[i_selected_removal] += sigma2;
				}
			}
			/*
			 * if new solution has cost worst than current solution
			 * 		because XR is new solution
			 * 			copy current current solution to new solution if don't change solution
			 */
			else{
				si[i_selected_insertion] += sigma3;
				if(i_selected_removal >= 0)
					sd[i_selected_removal] += sigma3;
				double v = Math.exp(-(new_cost-current_cost)/temperature);
				double e = Math.random();
				if(e >= v){
					current_solution.copy2XR(XR);
					group2marked = current_solution.get_group2marked();
					rejectPickupPoints = current_solution.get_rejectPickupPoints();
					rejectDeliveryPoints = current_solution.get_rejectDeliveryPoints();
				}
				iS++;
			}
			
			temperature = cooling_rate*temperature;
			
			//update probabilities
			if(it % nw == 0){
				for(int i=0; i<nInsertionOperators; i++){
					pti[i] = Math.max(0.0001, pti[i]*(1-rp) + rp*si[i]/wi[i]);
					//wi[i] = 1;
					//si[i] = 0;
				}
				
				for(int i=0; i<nRemovalOperators; i++){
					ptd[i] = Math.max(0.0001, ptd[i]*(1-rp) + rp*sd[i]/wd[i]);
					//wd[i] = 1;
					//sd[i] = 0;
				}
			}
		}
		best_solution.copy2XR(XR);
		group2marked = best_solution.get_group2marked();
		
		rejectPickupPoints = best_solution.get_rejectPickupPoints();
		rejectDeliveryPoints = best_solution.get_rejectDeliveryPoints();
		try{
			FileOutputStream write = new FileOutputStream(outputfile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(it + " -1 -1 "
					+ System.currentTimeMillis()/1000 + " "
					+ best_cost + " " + getNbRejectedRequests() + " " + getNbUsedTrucks());
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	public boolean checkCapacityContainerConstraint(Point x, Point y){
		int k = XR.route(y);
		if(accContainerInvr.getSumWeights(y) + accContainerInvr.getWeights(x) > 2)
			return false;
		for(Point p = XR.next(y); p != XR.getTerminatingPointOfRoute(k); p = XR.next(p)){
			if(accContainerInvr.getSumWeights(p) + accContainerInvr.getWeights(x) > 2)
				return false;
		}
		return true;
	}
	
	public void checkRejectedRequestsInfo(){
		ArrayList<Integer> grs = new ArrayList<Integer>();
		for(int i = 0; i < rejectPickupPoints.size(); i++){
			Point pickup = rejectPickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			
			if(group2marked.get(groupId) == 1 && grs.contains(groupId))
				continue;
			grs.add(groupId);
			System.out.println("p = " + pickup.getID());
			Point delivery = pickup2Delivery.get(pickup);
			Truck truck = input.getTrucks()[11];//getNearestTruck(pickup.getLocationCode());
			Mooc mooc = getNearestMooc(truck.getDepotTruckLocationCode());
			long d = DateTimeUtils.dateTime2Int(truck.getStartWorkingTime())
					+ getTravelTime(truck.getDepotTruckLocationCode(), mooc.getDepotMoocLocationCode())
					+ input.getParams().getLinkMoocDuration()
					+ getTravelTime(mooc.getDepotMoocLocationCode(), pickup.getLocationCode());
			if(d >= lastestAllowedArrivalTime.get(pickup))
				System.out.println("pick = " + pickup.getLocationCode()
						+ ", d = " + DateTimeUtils.unixTimeStamp2DateTime(d)
						+ ", late = " + DateTimeUtils.unixTimeStamp2DateTime(lastestAllowedArrivalTime.get(pickup)) );
			d += getTravelTime(pickup.getLocationCode(), delivery.getLocationCode());
			if(d >= lastestAllowedArrivalTime.get(delivery))
				System.out.println("del = " + delivery.getLocationCode()
						+ ", d = " + DateTimeUtils.unixTimeStamp2DateTime(d)
						+ ", late = " + DateTimeUtils.unixTimeStamp2DateTime(lastestAllowedArrivalTime.get(delivery)) );
		}
	}
	
	public void init(){
		this.exEmptyRequests = input.getExEmptyRequests();
		this.exLadenRequests = input.getExLadenRequests();
		this.imEmptyRequests = input.getImEmptyRequests();
		this.imLadenRequests = input.getImLadenRequests();
		this.nRequest = exEmptyRequests.length
			+ exLadenRequests.length
			+ imEmptyRequests.length
			+ imLadenRequests.length;
		this.nVehicle = input.getTrucks().length;
		points = new ArrayList<Point>();
		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
		serviceDuration = new HashMap<Point, Integer>();
		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
		
		pickupPoints = new ArrayList<Point>();
		deliveryPoints = new ArrayList<Point>();
		rejectPickupPoints = new ArrayList<Point>();
		rejectDeliveryPoints = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		stopPoints = new ArrayList<Point>();
		startMoocPoints = new ArrayList<Point>();
		stopMoocPoints = new ArrayList<Point>();
		point2Type = new HashMap<Point, String>();
		
		pickup2Delivery = new HashMap<Point, Point>();
		delivery2Pickup = new HashMap<Point, Point>();
		
		start2stopMoocPoint = new HashMap<Point, Point>();
		stop2startMoocPoint = new HashMap<Point, Point>();
		
		startPoint2Truck = new HashMap<Point, Truck>();
		startPoint2Mooc = new HashMap<Point, Mooc>();
		
		point2Group = new HashMap<Point, Integer>();
		group2marked = new HashMap<Integer, Integer>();
		
		group2EE = new HashMap<Integer, ExportEmptyRequests>();
		group2EL = new HashMap<Integer, ExportLadenRequests>();
		group2IE = new HashMap<Integer, ImportEmptyRequests>();
		group2IL = new HashMap<Integer, ImportLadenRequests>();
		
		point2moocWeight = new HashMap<Point, Integer>();
		point2containerWeight = new HashMap<Point, Integer>();
		
		route2DeliveryMooc = new HashMap<Integer, Point>();
		
		int id = 0;
		int groupId = 0;
		for(int i = 0; i < nVehicle; i++){
			Truck truck = input.getTrucks()[i];
			groupId++;
			group2marked.put(groupId, 0);
			for(int j = 0; j < truck.getReturnDepotCodes().length; j++){
				id++;
				Point sp = new Point(id, truck.
						getDepotTruckLocationCode());
				
				points.add(sp);
				startPoints.add(sp);
				point2Type.put(sp, START_TRUCK);
				startPoint2Truck.put(sp, truck);
				
				point2Group.put(sp, groupId);
				
				earliestAllowedArrivalTime.put(sp,(int)(DateTimeUtils.dateTime2Int(
						truck.getStartWorkingTime())));
				serviceDuration.put(sp, 0);
				lastestAllowedArrivalTime.put(sp,INF_TIME);
				
				id++;
				DepotTruck depotTruck = mCode2DepotTruck.get(truck.getReturnDepotCodes()[j]);
				Point tp = new Point(id, depotTruck.getLocationCode());
				points.add(tp);
				stopPoints.add(tp);
				point2Type.put(tp, END_TRUCK);
				
				point2Group.put(tp, groupId);
				
				earliestAllowedArrivalTime.put(tp,(int)(DateTimeUtils.dateTime2Int(
						input.getTrucks()[i].getStartWorkingTime())));
				serviceDuration.put(tp, 0);
				lastestAllowedArrivalTime.put(tp, INF_TIME);
				
				//pickup2Delivery.put(sp, tp);
				//delivery2Pickup.put(tp, sp);
				
				point2moocWeight.put(sp, 0);
				point2moocWeight.put(tp, 0);
				
				point2containerWeight.put(sp, 0);
				point2containerWeight.put(tp, 0);
			}
		}		
	
		for(int i = 0; i < input.getMoocs().length; i++){
			Mooc mooc = input.getMoocs()[i];
			groupId++;
			group2marked.put(groupId, 0);
			for(int j = 0; j < mooc.getReturnDepotCodes().length; j++){
				id++;
				Point sp = new Point(id, mooc
						.getDepotMoocLocationCode());
				points.add(sp);
				startMoocPoints.add(sp);
				point2Type.put(sp, START_MOOC);
				startPoint2Mooc.put(sp, mooc);
				
				point2Group.put(sp, groupId);
				
				earliestAllowedArrivalTime.put(sp, 0);
				serviceDuration.put(sp, input.getParams().getLinkMoocDuration());
				lastestAllowedArrivalTime.put(sp,INF_TIME);
				
				id++;
				String moocCode = mooc.getReturnDepotCodes()[j];
				DepotMooc depotMooc = mCode2DepotMooc.get(moocCode);
				Point tp = new Point(id, depotMooc.getLocationCode());
				points.add(tp);
				stopMoocPoints.add(tp);
				point2Type.put(tp, END_MOOC);
				point2Group.put(tp, groupId);
				
				earliestAllowedArrivalTime.put(tp, 0);
				serviceDuration.put(tp, 0);
				lastestAllowedArrivalTime.put(tp, INF_TIME);
				
				start2stopMoocPoint.put(sp, tp);
				stop2startMoocPoint.put(tp, sp);
				
				point2moocWeight.put(sp, 2);
				point2moocWeight.put(tp, -2);
				
				point2containerWeight.put(sp, 0);
				point2containerWeight.put(tp, 0);
			}
		}
		
		for(int i = 0; i < exEmptyRequests.length; i++){
			groupId++;
			group2marked.put(groupId, 0);
			group2EE.put(groupId, exEmptyRequests[i]);
			for(int j = 0; j < input.getContainers().length; j++){
				Container c = input.getContainers()[j];
				if(c.isImportedContainer())
					continue;
				
				DepotContainer depotCont = mCode2DepotContainer.get(c.getDepotContainerCode());
//				if(!exEmptyRequests[i].getContainerType().equals(depotCont.getContainerType()))
//					continue;
				id++;
				Point pickup = new Point(id, c.getDepotContainerCode());
				id++;
				Warehouse wh = mCode2Warehouse.get(
						exEmptyRequests[i].getWareHouseCode());
				Point delivery = new Point(id, wh.getLocationCode());
	
				points.add(pickup);
				points.add(delivery);
				
				pickupPoints.add(pickup);
				deliveryPoints.add(delivery);
	
				pickup2Delivery.put(pickup, delivery);
				delivery2Pickup.put(delivery, pickup);
				
				point2moocWeight.put(pickup, 0);
				if(exEmptyRequests[i].getIsBreakRomooc())
					point2moocWeight.put(delivery, -2);
				else
					point2moocWeight.put(delivery, 0);
				
				point2containerWeight.put(pickup, 1);
				point2containerWeight.put(delivery, -1);
				if(exEmptyRequests[i].getContainerType() != null
						&& exEmptyRequests[i].getContainerType().equals("40")){
					point2containerWeight.put(pickup, 2);
					point2containerWeight.put(delivery, -2);
				}
				
				point2Type.put(pickup, START_CONT);
				point2Type.put(delivery, WH_DELIVERY_EMPTYCONT);
				
				point2Group.put(pickup, groupId);
				point2Group.put(delivery, groupId);
				
				int early = 0;
				int latest = INF_TIME;
				if(exEmptyRequests[i].getEarlyDateTimePickupAtDepot() != null)
					early = (int)(DateTimeUtils.dateTime2Int(
							exEmptyRequests[i].getEarlyDateTimePickupAtDepot()));
				if(exEmptyRequests[i].getLateDateTimePickupAtDepot() != null)
					latest = (int)(DateTimeUtils.dateTime2Int(
							exEmptyRequests[i].getLateDateTimePickupAtDepot()));
				earliestAllowedArrivalTime.put(pickup, early);
				serviceDuration.put(pickup, input.getParams().getLinkEmptyContainerDuration());
				lastestAllowedArrivalTime.put(pickup, latest);
				
				early = 0;
				latest = INF_TIME;
				if(exEmptyRequests[i].getEarlyDateTimeLoadAtWarehouse() != null)
					early = (int)(DateTimeUtils.dateTime2Int(
							exEmptyRequests[i].getEarlyDateTimeLoadAtWarehouse()));
				if(exEmptyRequests[i].getLateDateTimeLoadAtWarehouse() != null)
					latest = (int)(DateTimeUtils.dateTime2Int(
							exEmptyRequests[i].getLateDateTimeLoadAtWarehouse()));
				earliestAllowedArrivalTime.put(delivery, early);
				serviceDuration.put(delivery, (int)(input.getParams().getUnlinkEmptyContainerDuration()));
				lastestAllowedArrivalTime.put(delivery, latest);
			}
		}
		
		for(int i = 0; i < exLadenRequests.length; i++)
		{
			groupId++;
			group2marked.put(groupId, 0);
			group2EL.put(groupId, exLadenRequests[i]);
			id++;
			Warehouse wh = mCode2Warehouse.get(
					exLadenRequests[i].getWareHouseCode());
			Point pickup = new Point(id, wh.getLocationCode());
			id++;
			Port port = mCode2Port.get(
					exLadenRequests[i].getPortCode());
			Point delivery = new Point(id, port.getLocationCode());

			points.add(pickup);
			points.add(delivery);
			
			pickupPoints.add(pickup);
			deliveryPoints.add(delivery);
			
			pickup2Delivery.put(pickup, delivery);
			delivery2Pickup.put(delivery, pickup);
			
			point2Type.put(pickup, WH_PICKUP_FULLCONT);
			point2Type.put(delivery, PORT_DELIVERY_FULLCONT);
			
			point2Group.put(pickup, groupId);
			point2Group.put(delivery, groupId);
			
			point2moocWeight.put(pickup, 0);
			if(exLadenRequests[i].getIsBreakRomooc())
				point2moocWeight.put(delivery, -2);
			else
				point2moocWeight.put(delivery, 0);
			
			point2containerWeight.put(pickup, 1);
			point2containerWeight.put(delivery, -1);
			if(exLadenRequests[i].getContainerType() != null
					&& exLadenRequests[i].getContainerType().equals("40")){
				point2containerWeight.put(pickup, 2);
				point2containerWeight.put(delivery, -2);
			}
			
			int early = 0;
			int latest = INF_TIME;
			if(exLadenRequests[i].getEarlyDateTimeAttachAtWarehouse() != null)
				early = (int)(DateTimeUtils.dateTime2Int(
						exLadenRequests[i].getEarlyDateTimeAttachAtWarehouse()));
			
			earliestAllowedArrivalTime.put(pickup,  early);
			serviceDuration.put(pickup, input.getParams().getLinkLoadedContainerDuration());
			lastestAllowedArrivalTime.put(pickup, latest);
			
			early = 0;
			latest = INF_TIME;
			if(exLadenRequests[i].getLateDateTimeUnloadAtPort() != null)
				latest = (int)(DateTimeUtils.dateTime2Int(
						exLadenRequests[i].getLateDateTimeUnloadAtPort()));
			earliestAllowedArrivalTime.put(delivery, early);
			serviceDuration.put(delivery, (int)(input.getParams().getUnlinkLoadedContainerDuration()));
			lastestAllowedArrivalTime.put(delivery, latest);
			
		}

		for(int i = 0; i < imEmptyRequests.length; i++){
			groupId++;
			group2marked.put(groupId, 0);
			group2IE.put(groupId, imEmptyRequests[i]);
			for(int j = 0; j < input.getDepotContainers().length; j++){
				DepotContainer depotCont = input.getDepotContainers()[j];
				id++;
				Warehouse wh = mCode2Warehouse.get(
						imEmptyRequests[i].getWareHouseCode());
				Point pickup = new Point(id, wh.getLocationCode());
				id++;
				
				Point delivery = new Point(id, depotCont.getLocationCode());
	
				points.add(pickup);
				points.add(delivery);
				
				pickupPoints.add(pickup);
				deliveryPoints.add(delivery);
				
				pickup2Delivery.put(pickup, delivery);
				delivery2Pickup.put(delivery, pickup);
				
				point2moocWeight.put(pickup, 0);
				point2moocWeight.put(delivery, 0);
				
				point2containerWeight.put(pickup, 1);
				point2containerWeight.put(delivery, -1);
				if(imEmptyRequests[i].getContainerType() != null
						&& imEmptyRequests[i].getContainerType().equals("40")){
					point2containerWeight.put(pickup, 2);
					point2containerWeight.put(delivery, -2);
				}
				
				point2Type.put(pickup, WH_PICKUP_EMPTYCONT);
				point2Type.put(delivery, END_CONT);
				
				point2Group.put(pickup, groupId);
				point2Group.put(delivery, groupId);
				
				int early = 0;
				int latest = INF_TIME;
				if(imEmptyRequests[i].getEarlyDateTimeAttachAtWarehouse() != null)
					early = (int)(DateTimeUtils.dateTime2Int(
							imEmptyRequests[i].getEarlyDateTimeAttachAtWarehouse()));
				earliestAllowedArrivalTime.put(pickup, early);
				serviceDuration.put(pickup, input.getParams().getLinkEmptyContainerDuration());
				lastestAllowedArrivalTime.put(pickup, latest);
				
				early = 0;
				latest = INF_TIME;
	
				if(imEmptyRequests[i].getLateDateTimeReturnEmptyAtDepot() != null)
					latest = (int)(DateTimeUtils.dateTime2Int(
							imEmptyRequests[i].getLateDateTimeReturnEmptyAtDepot()));
				earliestAllowedArrivalTime.put(delivery, early);
				serviceDuration.put(delivery, (int)(input.getParams().getUnlinkEmptyContainerDuration()));
				lastestAllowedArrivalTime.put(delivery, latest);
			}
		}
		
		for(int i = 0; i < imLadenRequests.length; i++)
		{
			groupId++;
			group2marked.put(groupId, 0);
			group2IL.put(groupId, imLadenRequests[i]);
			id++;
			Port port = mCode2Port.get(
					imLadenRequests[i].getPortCode());
			Point pickup = new Point(id, port.getLocationCode());
			
			id++;
			Warehouse wh = mCode2Warehouse.get(
					imLadenRequests[i].getWareHouseCode());
			Point delivery = new Point(id, wh.getLocationCode());

			points.add(pickup);
			points.add(delivery);
			
			pickupPoints.add(pickup);
			deliveryPoints.add(delivery);
			
			pickup2Delivery.put(pickup, delivery);
			delivery2Pickup.put(delivery, pickup);
			
			point2moocWeight.put(pickup, 0);
			if(imLadenRequests[i].getIsBreakRomooc())
				point2moocWeight.put(delivery, -2);
			else
				point2moocWeight.put(delivery, 0);
			
			point2containerWeight.put(pickup, 1);
			point2containerWeight.put(delivery, -1);
			if(imLadenRequests[i].getContainerType() != null
				&& imLadenRequests[i].getContainerType().equals("40")){
				point2containerWeight.put(pickup, 2);
				point2containerWeight.put(delivery, -2);
			}
			
			point2Type.put(pickup, PORT_PICKUP_FULLCONT);
			point2Type.put(delivery, WH_DELIVERY_FULLCONT);
			
			point2Group.put(pickup, groupId);
			point2Group.put(delivery, groupId);
			
			int early = 0;
			int latest = INF_TIME;
			if(imLadenRequests[i].getEarlyDateTimePickupAtPort() != null)
				early = (int)(DateTimeUtils.dateTime2Int(
						imLadenRequests[i].getEarlyDateTimePickupAtPort()));
			if(imLadenRequests[i].getLateDateTimePickupAtPort() != null)
				latest = (int)(DateTimeUtils.dateTime2Int(
						imLadenRequests[i].getLateDateTimePickupAtPort()));
			earliestAllowedArrivalTime.put(pickup, early);
			serviceDuration.put(pickup, input.getParams().getLinkLoadedContainerDuration());
			lastestAllowedArrivalTime.put(pickup, latest);
			
			early = 0;
			latest = INF_TIME;
			if(imLadenRequests[i].getEarlyDateTimeUnloadAtWarehouse() != null)
				early = (int)(DateTimeUtils.dateTime2Int(
						imLadenRequests[i].getEarlyDateTimeUnloadAtWarehouse()));
			if(imLadenRequests[i].getLateDateTimeUnloadAtWarehouse() != null)
				latest = (int)(DateTimeUtils.dateTime2Int(
						imLadenRequests[i].getLateDateTimeUnloadAtWarehouse()));

			earliestAllowedArrivalTime.put(delivery, early);
			serviceDuration.put(delivery, (int)(input.getParams().getUnlinkLoadedContainerDuration()));
			lastestAllowedArrivalTime.put(delivery, latest);
			
		}
		
		nwMooc = new NodeWeightsManager(points);
		nwContainer = new NodeWeightsManager(points);
		awm = new ArcWeightsManager(points);
		double max_time = Double.MIN_VALUE;
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {
				double tmp_cost = getTravelTime(points.get(i).getLocationCode(),
						points.get(j).getLocationCode());
				awm.setWeight(points.get(i), points.get(j), tmp_cost);
				max_time = tmp_cost > max_time ? tmp_cost : max_time;
			}
			nwMooc.setWeight(points.get(i), point2moocWeight.get(points.get(i)));
			nwContainer.setWeight(points.get(i), point2containerWeight.get(points.get(i)));
		}
		MAX_TRAVELTIME = max_time;
	}
	
	public void initParamsForALNS(){
		nChosed = new HashMap<Point, Integer>();
		removeAllowed = new HashMap<Point, Boolean>();
		for(int i=0; i<pickupPoints.size(); i++){
			Point pi = pickupPoints.get(i);
			nChosed.put(pi, 0);
			removeAllowed.put(pi, true);
			
			Point pj = pickup2Delivery.get(pi);
			nChosed.put(pj, 0);
			removeAllowed.put(pj, true);
		}
	}
	
	public void insertMoocForAllRoutes(){
		removeAllMoocFromRoutes();
		for(int r = 1; r <= XR.getNbRoutes(); r++){
			Point st = XR.getStartingPointOfRoute(r);
			Point stMooc = null;
			Point preP = null;
			Point nextP = null;
			Point enMooc = null;
			for(Point p = XR.next(st); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
				if(accMoocInvr.getSumWeights(XR.prev(p)) <= 0){
					stMooc = getBestStartMoocForRequest(r, XR.prev(p), p);
					if(stMooc == null)
						continue;
					preP = XR.prev(p);
					nextP = p;
					mgr.performAddOnePoint(stMooc, XR.prev(p));
					int groupMooc= point2Group.get(stMooc);
					group2marked.put(groupMooc, 1);					
				}
			}
			if(accMoocInvr.getSumWeights(XR.getTerminatingPointOfRoute(r)) > 0){
				Point enPoint = XR.prev(XR.getTerminatingPointOfRoute(r));
				Point newStMooc = getBestMoocForRequest(stMooc, preP, nextP, enPoint, XR.getTerminatingPointOfRoute(r));
				if(newStMooc != stMooc){
					mgr.performRemoveOnePoint(stMooc);
					int groupMooc= point2Group.get(stMooc);
					group2marked.put(groupMooc, 0);
					mgr.performAddOnePoint(newStMooc, preP);
					groupMooc= point2Group.get(newStMooc);
					group2marked.put(groupMooc, 1);	
				}
				enMooc = start2stopMoocPoint.get(newStMooc);
				mgr.performAddOnePoint(enMooc, enPoint);
			}
		}
	}
	
	public void insertMoocToRoutes(int r){
		Point st = XR.getStartingPointOfRoute(r);
		Point stMooc = null;
		Point enMooc = null;
		for(Point p = XR.next(st); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
			if(accMoocInvr.getSumWeights(XR.prev(p)) <= 0){
				stMooc = getBestStartMoocForRequest(r, XR.prev(p), p);
				if(stMooc == null)
					continue;
				mgr.performAddOnePoint(stMooc, XR.prev(p));
				int groupMooc= point2Group.get(stMooc);
				group2marked.put(groupMooc, 1);
				enMooc = start2stopMoocPoint.get(stMooc);
			}
		}
		if(accMoocInvr.getSumWeights(XR.getTerminatingPointOfRoute(r)) > 0 && enMooc != null){
			mgr.performAddOnePoint(enMooc, XR.prev(XR.getTerminatingPointOfRoute(r)));
		}
	}
	
//	public void init_compare(){
//		this.exEmptyRequests = input.getExEmptyRequests();
//		this.exLadenRequests = input.getExLadenRequests();
//		this.imEmptyRequests = input.getImEmptyRequests();
//		this.imLadenRequests = input.getImLadenRequests();
//		this.nRequest = exEmptyRequests.length
//			+ exLadenRequests.length
//			+ imEmptyRequests.length
//			+ imLadenRequests.length;
//		this.nVehicle = input.getTrucks().length;
//		points = new ArrayList<Point>();
//		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
//		serviceDuration = new HashMap<Point, Integer>();
//		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
//		
//		pickupPoints = new ArrayList<Point>();
//		deliveryPoints = new ArrayList<Point>();
//		rejectPickupPoints = new ArrayList<Point>();
//		rejectDeliveryPoints = new ArrayList<Point>();
//		startPoints = new ArrayList<Point>();
//		stopPoints = new ArrayList<Point>();
//		startMoocPoints = new ArrayList<Point>();
//		stopMoocPoints = new ArrayList<Point>();
//		point2Type = new HashMap<Point, String>();
//		
//		pickup2Delivery = new HashMap<Point, Point>();
//		delivery2Pickup = new HashMap<Point, Point>();
//		
//		startPoint2Truck = new HashMap<Point, Truck>();
//		startPoint2Mooc = new HashMap<Point, Mooc>();
//		
//		point2Group = new HashMap<Point, Integer>();
//		group2marked = new HashMap<Integer, Integer>();
//		
//		point2moocWeight = new HashMap<Point, Integer>();
//		point2containerWeight = new HashMap<Point, Integer>();
//		
//		int id = 0;
//		int groupId = 0;
//		for(int i = 0; i < nVehicle; i++){
//			Truck truck = input.getTrucks()[i];
//			groupId++;
//			group2marked.put(groupId, 0);
//			for(int j = 0; j < truck.getReturnDepotCodes().length; j++){
//				id++;
//				Point sp = new Point(Integer.parseInt(truck.getDepotTruckCode()), truck.
//						getDepotTruckLocationCode());
//				
//				points.add(sp);
//				startPoints.add(sp);
//				point2Type.put(sp, START_TRUCK);
//				startPoint2Truck.put(sp, truck);
//				
//				point2Group.put(sp, groupId);
//				
//				earliestAllowedArrivalTime.put(sp,(int)(DateTimeUtils.dateTime2Int(
//						truck.getStartWorkingTime())));
//				serviceDuration.put(sp, 0);
//				lastestAllowedArrivalTime.put(sp,INF_TIME);
//				
//				id++;
//				DepotTruck depotTruck = mCode2DepotTruck.get(truck.getReturnDepotCodes()[j]);
//				Point tp = new Point(Integer.parseInt(depotTruck.getCode()), depotTruck.getLocationCode());
//				points.add(tp);
//				stopPoints.add(tp);
//				point2Type.put(tp, END_TRUCK);
//				
//				point2Group.put(tp, groupId);
//				
//				earliestAllowedArrivalTime.put(tp,(int)(DateTimeUtils.dateTime2Int(
//						input.getTrucks()[i].getStartWorkingTime())));
//				serviceDuration.put(tp, 0);
//				lastestAllowedArrivalTime.put(tp, INF_TIME);
//				
//				//pickup2Delivery.put(sp, tp);
//				//delivery2Pickup.put(tp, sp);
//				
//				point2moocWeight.put(sp, 0);
//				point2moocWeight.put(tp, 0);
//				
//				point2containerWeight.put(sp, 0);
//				point2containerWeight.put(tp, 0);
//			}
//		}		
//	
//		for(int i = 0; i < input.getMoocs().length; i++){
//			Mooc mooc = input.getMoocs()[i];
//			groupId++;
//			group2marked.put(groupId, 0);
//			for(int j = 0; j < mooc.getReturnDepotCodes().length; j++){
//				id++;
//				Point sp = new Point(Integer.parseInt(mooc.getDepotMoocCode()), mooc
//						.getDepotMoocLocationCode());
//				points.add(sp);
//				startMoocPoints.add(sp);
//				point2Type.put(sp, START_MOOC);
//				startPoint2Mooc.put(sp, mooc);
//				
//				point2Group.put(sp, groupId);
//				
//				earliestAllowedArrivalTime.put(sp, 0);
//				serviceDuration.put(sp, input.getParams().getLinkMoocDuration());
//				lastestAllowedArrivalTime.put(sp,INF_TIME);
//				
//				id++;
//				String moocCode = mooc.getReturnDepotCodes()[j];
//				DepotMooc depotMooc = mCode2DepotMooc.get(moocCode);
//				Point tp = new Point(Integer.parseInt(depotMooc.getCode()), depotMooc.getLocationCode());
//				points.add(tp);
//				stopMoocPoints.add(tp);
//				point2Type.put(tp, END_MOOC);
//				point2Group.put(tp, groupId);
//				
//				earliestAllowedArrivalTime.put(tp, 0);
//				serviceDuration.put(tp, 0);
//				lastestAllowedArrivalTime.put(tp, INF_TIME);
//				
//				//pickup2Delivery.put(sp, tp);
//				//delivery2Pickup.put(tp, sp);
//				
//				point2moocWeight.put(sp, 1);
//				point2moocWeight.put(tp, -1);
//				
//				point2containerWeight.put(sp, 0);
//				point2containerWeight.put(tp, 0);
//			}
//		}
//		
//		for(int i = 0; i < exEmptyRequests.length; i++){
//			groupId++;
//			group2marked.put(groupId, 0);
//			for(int j = 0; j < input.getDepotContainers().length; j++){
//				id++;
//				DepotContainer depotCont = input.getDepotContainers()[j];
//				Point pickup = new Point(Integer.parseInt(depotCont.getCode()), depotCont.getLocationCode());
//				id++;
//				Warehouse wh = mCode2Warehouse.get(
//						exEmptyRequests[i].getWareHouseCode());
//				Point delivery = new Point(Integer.parseInt(wh.getCode()), wh.getLocationCode());
//	
//				points.add(pickup);
//				points.add(delivery);
//				
//				pickupPoints.add(pickup);
//				deliveryPoints.add(delivery);
//	
//				pickup2Delivery.put(pickup, delivery);
//				delivery2Pickup.put(delivery, pickup);
//				
//				point2moocWeight.put(pickup, 0);
//				point2moocWeight.put(delivery, 0);
//				
//				point2containerWeight.put(pickup, 1);
//				point2containerWeight.put(delivery, -1);
//				
//				point2Type.put(pickup, START_CONT);
//				point2Type.put(delivery, WAREHOUSE);
//				
//				point2Group.put(pickup, groupId);
//				point2Group.put(delivery, groupId);
//				
//				int early = 0;
//				int latest = INF_TIME;
//				if(exEmptyRequests[i].getEarlyDateTimePickupAtDepot() != null)
//					early = (int)(DateTimeUtils.dateTime2Int(
//							exEmptyRequests[i].getEarlyDateTimePickupAtDepot()));
//				if(exEmptyRequests[i].getLateDateTimePickupAtDepot() != null)
//					latest = (int)(DateTimeUtils.dateTime2Int(
//							exEmptyRequests[i].getLateDateTimePickupAtDepot()));
//				earliestAllowedArrivalTime.put(pickup, early);
//				serviceDuration.put(pickup, input.getParams().getLinkEmptyContainerDuration());
//				lastestAllowedArrivalTime.put(pickup, latest);
//				
//				early = 0;
//				latest = INF_TIME;
//				if(exEmptyRequests[i].getEarlyDateTimeLoadAtWarehouse() != null)
//					early = (int)(DateTimeUtils.dateTime2Int(
//							exEmptyRequests[i].getEarlyDateTimeLoadAtWarehouse()));
//				if(exEmptyRequests[i].getLateDateTimeLoadAtWarehouse() != null)
//					latest = (int)(DateTimeUtils.dateTime2Int(
//							exEmptyRequests[i].getLateDateTimeLoadAtWarehouse()));
//				earliestAllowedArrivalTime.put(delivery, early);
//				serviceDuration.put(delivery, (int)(input.getParams().getUnlinkEmptyContainerDuration()));
//				lastestAllowedArrivalTime.put(delivery, latest);
//			}
//		}
//		
//		for(int i = 0; i < exLadenRequests.length; i++)
//		{
//			groupId++;
//			group2marked.put(groupId, 0);
//			id++;
//			Warehouse wh = mCode2Warehouse.get(
//					exLadenRequests[i].getWareHouseCode());
//			Point pickup = new Point(Integer.parseInt(wh.getCode()), wh.getLocationCode());
//			id++;
//			Port port = mCode2Port.get(
//					exLadenRequests[i].getPortCode());
//			Point delivery = new Point(Integer.parseInt(port.getCode()), port.getLocationCode());
//
//			points.add(pickup);
//			points.add(delivery);
//			
//			pickupPoints.add(pickup);
//			deliveryPoints.add(delivery);
//			
//			pickup2Delivery.put(pickup, delivery);
//			delivery2Pickup.put(delivery, pickup);
//			
//			point2Type.put(pickup, WAREHOUSE);
//			point2Type.put(delivery, PORT);
//			
//			point2Group.put(pickup, groupId);
//			point2Group.put(delivery, groupId);
//			
//			point2moocWeight.put(pickup, 0);
//			point2moocWeight.put(delivery, 0);
//			
//			point2containerWeight.put(pickup, 1);
//			point2containerWeight.put(delivery, -1);
//			
//			int early = 0;
//			int latest = INF_TIME;
//			if(exLadenRequests[i].getEarlyDateTimeAttachAtWarehouse() != null)
//				early = (int)(DateTimeUtils.dateTime2Int(
//						exLadenRequests[i].getEarlyDateTimeAttachAtWarehouse()));
//			
//			earliestAllowedArrivalTime.put(pickup,  early);
//			serviceDuration.put(pickup, input.getParams().getLinkLoadedContainerDuration());
//			lastestAllowedArrivalTime.put(pickup, latest);
//			
//			early = 0;
//			latest = INF_TIME;
//			if(exLadenRequests[i].getLateDateTimeUnloadAtPort() != null)
//				latest = (int)(DateTimeUtils.dateTime2Int(
//						exLadenRequests[i].getLateDateTimeUnloadAtPort()));
//			earliestAllowedArrivalTime.put(delivery, early);
//			serviceDuration.put(delivery, (int)(input.getParams().getUnlinkLoadedContainerDuration()));
//			lastestAllowedArrivalTime.put(delivery, latest);
//			
//		}
//
//		for(int i = 0; i < imEmptyRequests.length; i++){
//			groupId++;
//			group2marked.put(groupId, 0);
//			for(int j = 0; j < input.getDepotContainers().length; j++){
//				id++;
//				Warehouse wh = mCode2Warehouse.get(
//						imEmptyRequests[i].getWareHouseCode());
//				Point pickup = new Point(Integer.parseInt(wh.getCode()), wh.getLocationCode());
//				id++;
//				DepotContainer depotCont = input.getDepotContainers()[j];
//				Point delivery = new Point(Integer.parseInt(depotCont.getCode()), depotCont.getLocationCode());
//	
//				points.add(pickup);
//				points.add(delivery);
//				
//				pickupPoints.add(pickup);
//				deliveryPoints.add(delivery);
//				
//				pickup2Delivery.put(pickup, delivery);
//				delivery2Pickup.put(delivery, pickup);
//				
//				point2moocWeight.put(pickup, 0);
//				point2moocWeight.put(delivery, 0);
//				
//				point2containerWeight.put(pickup, 1);
//				point2containerWeight.put(delivery, -1);
//				
//				point2Type.put(pickup, WAREHOUSE);
//				point2Type.put(delivery, END_CONT);
//				
//				point2Group.put(pickup, groupId);
//				point2Group.put(delivery, groupId);
//				
//				int early = 0;
//				int latest = INF_TIME;
//				if(imEmptyRequests[i].getEarlyDateTimeAttachAtWarehouse() != null)
//					early = (int)(DateTimeUtils.dateTime2Int(
//							imEmptyRequests[i].getEarlyDateTimeAttachAtWarehouse()));
//				earliestAllowedArrivalTime.put(pickup, early);
//				serviceDuration.put(pickup, input.getParams().getLinkEmptyContainerDuration());
//				lastestAllowedArrivalTime.put(pickup, latest);
//				
//				early = 0;
//				latest = INF_TIME;
//	
//				if(imEmptyRequests[i].getLateDateTimeReturnEmptyAtDepot() != null)
//					latest = (int)(DateTimeUtils.dateTime2Int(
//							imEmptyRequests[i].getLateDateTimeReturnEmptyAtDepot()));
//				earliestAllowedArrivalTime.put(delivery, early);
//				serviceDuration.put(delivery, (int)(input.getParams().getUnlinkEmptyContainerDuration()));
//				lastestAllowedArrivalTime.put(delivery, latest);
//			}
//		}
//		
//		for(int i = 0; i < imLadenRequests.length; i++)
//		{
//			groupId++;
//			group2marked.put(groupId, 0);
//			id++;
//			Port port = mCode2Port.get(
//					imLadenRequests[i].getPortCode());
//			Point pickup = new Point(Integer.parseInt(port.getCode()), port.getLocationCode());
//			
//			id++;
//			Warehouse wh = mCode2Warehouse.get(
//					imLadenRequests[i].getWareHouseCode());
//			Point delivery = new Point(Integer.parseInt(wh.getCode()), wh.getLocationCode());
//
//			points.add(pickup);
//			points.add(delivery);
//			
//			pickupPoints.add(pickup);
//			deliveryPoints.add(delivery);
//			
//			pickup2Delivery.put(pickup, delivery);
//			delivery2Pickup.put(delivery, pickup);
//			
//			point2moocWeight.put(pickup, 0);
//			point2moocWeight.put(delivery, 0);
//			
//			point2containerWeight.put(pickup, 1);
//			point2containerWeight.put(delivery, -1);
//			
//			point2Type.put(pickup, PORT);
//			point2Type.put(delivery, WAREHOUSE);
//			
//			point2Group.put(pickup, groupId);
//			point2Group.put(delivery, groupId);
//			
//			int early = 0;
//			int latest = INF_TIME;
//			if(imLadenRequests[i].getEarlyDateTimePickupAtPort() != null)
//				early = (int)(DateTimeUtils.dateTime2Int(
//						imLadenRequests[i].getEarlyDateTimePickupAtPort()));
//			if(imLadenRequests[i].getLateDateTimePickupAtPort() != null)
//				latest = (int)(DateTimeUtils.dateTime2Int(
//						imLadenRequests[i].getLateDateTimePickupAtPort()));
//			earliestAllowedArrivalTime.put(pickup, early);
//			serviceDuration.put(pickup, input.getParams().getLinkLoadedContainerDuration());
//			lastestAllowedArrivalTime.put(pickup, latest);
//			
//			early = 0;
//			latest = INF_TIME;
//			if(imLadenRequests[i].getEarlyDateTimeUnloadAtWarehouse() != null)
//				early = (int)(DateTimeUtils.dateTime2Int(
//						imLadenRequests[i].getEarlyDateTimeUnloadAtWarehouse()));
//			if(imLadenRequests[i].getLateDateTimeUnloadAtWarehouse() != null)
//				latest = (int)(DateTimeUtils.dateTime2Int(
//						imLadenRequests[i].getLateDateTimeUnloadAtWarehouse()));
//			earliestAllowedArrivalTime.put(delivery, early);
//			serviceDuration.put(delivery, (int)(input.getParams().getUnlinkLoadedContainerDuration()));
//			lastestAllowedArrivalTime.put(delivery, latest);
//			
//		}
//		
//		nwMooc = new NodeWeightsManager(points);
//		nwContainer = new NodeWeightsManager(points);
//		awm = new ArcWeightsManager(points);
//		double max_time = Double.MIN_VALUE;
//		for (int i = 0; i < points.size(); i++) {
//			for (int j = 0; j < points.size(); j++) {
//				double tmp_cost = getTravelTime(points.get(i).getLocationCode(),
//						points.get(j).getLocationCode());
//				awm.setWeight(points.get(i), points.get(j), tmp_cost);
//				max_time = tmp_cost > max_time ? tmp_cost : max_time;
//			}
//			nwMooc.setWeight(points.get(i), point2moocWeight.get(points.get(i)));
//			nwContainer.setWeight(points.get(i), point2containerWeight.get(points.get(i)));
//		}
//		MAX_TRAVELTIME = max_time;
//	}
	
	public void readData(String fileName){
		try{
			Gson g = new Gson();
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			input = g.fromJson(in, ContainerTruckMoocInput.class);
			in.close();
			InputAnalyzer IA = new InputAnalyzer();
			IA.standardize(input);
			mapData();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	public void input(String inputJson){
        try{
            Gson g = new Gson();
            input = g.fromJson(inputJson, ContainerTruckMoocInput.class);
            InputAnalyzer IA = new InputAnalyzer();
            IA.standardize(input);
            mapData();
        }
        catch(Exception e){
            System.out.println(e);
        }

    }
    public void mapData(){
	//public void mapData(String dataFileName) {

		// create artificial containers based on import request
		//Random ran = new Random();
		additionalContainers = new ArrayList<Container>();
		int idxCode = -1;
//		if(input.getImRequests() != null){
//			for (int i = 0; i < input.getImRequests().length; i++) {
//				ImportContainerTruckMoocRequest R = input.getImRequests()[i];
//				for (int j = 0; j < R.getContainerRequest().length; j++) {
//					ImportContainerRequest r = R.getContainerRequest()[j];
//
//					idxCode++;
//					String code = "A-" + idxCode;
//					String depotContainerCode = null;
//					if(r.getDepotContainerCode() != null)
//						depotContainerCode = r.getDepotContainerCode()[0];
//					else{
//						int idx = 0;//ran.nextInt(input.getDepotContainers().length);
//						depotContainerCode = input.getDepotContainers()[idx].getCode();
//					}
//					Container c = new Container(code, (int) r.getWeight(),
//							r.getContainerCategory(), depotContainerCode,
//							r.getDepotContainerCode());
//					additionalContainers.add(c);
//					r.setContainerCode(code);
//				}
//			}
//		}
		if(input.getImEmptyRequests() != null){
			for (int i = 0; i < input.getImEmptyRequests().length; i++) {
				ImportEmptyRequests R = input.getImEmptyRequests()[i];
				idxCode++;
				String code = "A-" + idxCode;
				String depotContainerCode = null;
				if(R.getDepotContainerCode() != null)
					depotContainerCode = R.getDepotContainerCode();
				else{
					int idx = 0;//ran.nextInt(input.getDepotContainers().length);
					depotContainerCode = input.getDepotContainers()[idx].getCode();
				}
//				DepotContainer[] dpc = input.getDepotContainers();
//				for(int k = 0; k < dpc.length; k++){
//					if(dpc[k].getCode().equals(depotContainerCode))
//						dpc[k].setReturnedContainer(true);
//				}
//				input.setDepotContainers(dpc);
				
				String[] returnDepot = new String[1];
				returnDepot[0] = new String();
				returnDepot[0] = depotContainerCode;
				Container c = new Container(code, (int) R.getWeight(),
						R.getContainerCategory(), depotContainerCode,
						returnDepot);
				additionalContainers.add(c);
				R.setContainerCode(code);
			}
		}
		ArrayList<String> containerCodes = new ArrayList<String>();
		
		Container[] temp = input.getContainers();
		ArrayList<Container> cL = new ArrayList<Container>();
		for (int i = 0; i < temp.length; i++) {
			if(!containerCodes.contains(temp[i].getCode())){
				containerCodes.add(temp[i].getCode());
				cL.add(temp[i]);
			}
		}
		Container[] L = new Container[cL.size()
				+ additionalContainers.size()];
		for (int i = 0; i < cL.size(); i++) {
			L[i] = cL.get(i);
			L[i].setImportedContainer(false);
		}
		for (int i = 0; i < additionalContainers.size(); i++) {
			L[i + cL.size()] = additionalContainers.get(i);
			L[i + cL.size()].setImportedContainer(true);
		}
		input.setContainers(L);

		HashSet<String> s_locationCode = new HashSet<String>();
		for (int i = 0; i < input.getDistance().length; i++) {
			DistanceElement e = input.getDistance()[i];
			String src = e.getSrcCode();
			String dest = e.getDestCode();
			s_locationCode.add(src);
			s_locationCode.add(dest);
		}
		locationCodes = new String[s_locationCode.size()];
		mLocationCode2Index = new HashMap<String, Integer>();
		int idx = -1;
		for (String lc : s_locationCode) {
			idx++;
			locationCodes[idx] = lc;
			mLocationCode2Index.put(lc, idx);
		}
		distance = new double[s_locationCode.size()][s_locationCode.size()];
		travelTime = new double[s_locationCode.size()][s_locationCode.size()];
		for (int i = 0; i < input.getDistance().length; i++) {
			DistanceElement e = input.getDistance()[i];
			String src = e.getSrcCode();
			String dest = e.getDestCode();
			double d = e.getDistance();
			int is = mLocationCode2Index.get(src);
			int id = mLocationCode2Index.get(dest);
			distance[is][id] = d;

			travelTime[is][id] = e.getTravelTime();
		}
		ArrayList<DepotContainer> dcL = new ArrayList<DepotContainer>();
		ArrayList<String> codes = new ArrayList<String>();
		for (int i = 0; i < input.getDepotContainers().length; i++) {
			if(!codes.contains(input.getDepotContainers()[i].getCode())){
				codes.add(input.getDepotContainers()[i].getCode());
				dcL.add(input.getDepotContainers()[i]);
			}
		}
		DepotContainer[] dpc = new DepotContainer[dcL.size()];
		for(int i = 0; i < dcL.size(); i++)
			dpc[i] = dcL.get(i);
		input.setDepotContainers(dpc);
		
		mCode2DepotContainer = new HashMap<String, DepotContainer>();
		for (int i = 0; i < input.getDepotContainers().length; i++) {
			mCode2DepotContainer.put(input.getDepotContainers()[i].getCode(),
					input.getDepotContainers()[i]);
		}

		ArrayList<DepotMooc> depotMoocList = new ArrayList<DepotMooc>();
		codes = new ArrayList<String>();
		for (int i = 0; i < input.getDepotMoocs().length; i++) {
			if(!codes.contains(input.getDepotMoocs()[i].getCode())){
				codes.add(input.getDepotMoocs()[i].getCode());
				depotMoocList.add(input.getDepotMoocs()[i]);
			}
		}
		DepotMooc[] dpm = new DepotMooc[depotMoocList.size()];
		for(int i = 0; i < depotMoocList.size(); i++)
			dpm[i] = depotMoocList.get(i);
		input.setDepotMoocs(dpm);
		
		mCode2DepotMooc = new HashMap<String, DepotMooc>();
		for (int i = 0; i < input.getDepotMoocs().length; i++) {
			mCode2DepotMooc.put(input.getDepotMoocs()[i].getCode(),
					input.getDepotMoocs()[i]);
		}
		
		ArrayList<DepotTruck> depotTruckList = new ArrayList<DepotTruck>();
		codes = new ArrayList<String>();
		for (int i = 0; i < input.getDepotTrucks().length; i++) {
			if(!codes.contains(input.getDepotTrucks()[i].getCode())){
				codes.add(input.getDepotTrucks()[i].getCode());
				depotTruckList.add(input.getDepotTrucks()[i]);
			}
		}
		DepotTruck[] dpt = new DepotTruck[depotTruckList.size()];
		for(int i = 0; i < depotTruckList.size(); i++)
			dpt[i] = depotTruckList.get(i);
		input.setDepotTrucks(dpt);
		
		mCode2DepotTruck = new HashMap<String, DepotTruck>();
		for (int i = 0; i < input.getDepotTrucks().length; i++) {
			mCode2DepotTruck.put(input.getDepotTrucks()[i].getCode(),
					input.getDepotTrucks()[i]);
		}
		
		ArrayList<Warehouse> whList = new ArrayList<Warehouse>();
		codes = new ArrayList<String>();
		for (int i = 0; i < input.getWarehouses().length; i++) {
			if(!codes.contains(input.getWarehouses()[i].getCode())){
				codes.add(input.getWarehouses()[i].getCode());
				whList.add(input.getWarehouses()[i]);
			}
		}
		Warehouse[] whs = new Warehouse[whList.size()];
		for(int i = 0; i < whList.size(); i++)
			whs[i] = whList.get(i);
		input.setWarehouses(whs);
		
		mCode2Warehouse = new HashMap<String, Warehouse>();
		for (int i = 0; i < input.getWarehouses().length; i++) {
			mCode2Warehouse.put(input.getWarehouses()[i].getCode(),
					input.getWarehouses()[i]);
		}
		
		ArrayList<Mooc> moocList = new ArrayList<Mooc>();
		codes = new ArrayList<String>();
		for (int i = 0; i < input.getMoocs().length; i++) {
			if(!codes.contains(input.getMoocs()[i].getCode())){
				codes.add(input.getMoocs()[i].getCode());
				moocList.add(input.getMoocs()[i]);
			}
		}
		Mooc[] ms = new Mooc[moocList.size()];
		for(int i = 0; i < moocList.size(); i++)
			ms[i] = moocList.get(i);
		input.setMoocs(ms);
		
		mCode2Mooc = new HashMap<String, Mooc>();
		for (int i = 0; i < input.getMoocs().length; i++) {
			Mooc mooc = input.getMoocs()[i];
			mCode2Mooc.put(mooc.getCode(), mooc);
		}
		
		ArrayList<Truck> truckList = new ArrayList<Truck>();
		codes = new ArrayList<String>();
		for (int i = 0; i < input.getTrucks().length; i++) {
			if(!codes.contains(input.getTrucks()[i].getCode())){
				codes.add(input.getTrucks()[i].getCode());
				truckList.add(input.getTrucks()[i]);
			}
		}
		Truck[] ts = new Truck[truckList.size()];
		for(int i = 0; i < truckList.size(); i++)
			ts[i] = truckList.get(i);
		input.setTrucks(ts);

		mCode2Truck = new HashMap<String, Truck>();
		for (int i = 0; i < input.getTrucks().length; i++) {
			Truck truck = input.getTrucks()[i];
			mCode2Truck.put(truck.getCode(),
					truck);
		}

		mCode2Container = new HashMap<String, Container>();
		for (int i = 0; i < input.getContainers().length; i++) {
			Container c = input.getContainers()[i];
			mCode2Container.put(c.getCode(), c);
		}

		ArrayList<Port> portList = new ArrayList<Port>();
		codes = new ArrayList<String>();
		for (int i = 0; i < input.getPorts().length; i++) {
			if(!codes.contains(input.getPorts()[i].getCode())){
				codes.add(input.getPorts()[i].getCode());
				portList.add(input.getPorts()[i]);
			}
		}
		Port[] ps = new Port[portList.size()];
		for(int i = 0; i < portList.size(); i++)
			ps[i] = portList.get(i);
		input.setPorts(ps);
		
		mCode2Port = new HashMap<String, Port>();
		for (int i = 0; i < input.getPorts().length; i++) {
			mCode2Port.put(input.getPorts()[i].getCode(), input.getPorts()[i]);
		}
		
//		try{
//			Gson gson = new Gson();
//			File fo = new File(dataFileName);
//			FileWriter fw = new FileWriter(fo);
//			gson.toJson(input, fw);
//			fw.close();
//		}
//		catch(Exception e){
//			System.out.println(e);
//		}
	}
	
	//roulette-wheel mechanism
 	private int get_operator(double[] p){
 		//String message = "probabilities input \n";
 		
 		int n = p.length;
		double[] s = new double[n];
		s[0] = 0+p[0];

		
		for(int i=1; i<n; i++)
			s[i] = s[i-1]+p[i]; 
		
		double r = s[n-1]*Math.random();
		
		if(r>=0 && r <= s[0])
			return 0;
		
		for(int i=1; i<n; i++){
			if(r>s[i-1] && r<=s[i])
				return i;
		}
		return -1;
	}
	
	public Point getBestStartMoocForRequest(int r, Point p, Point pickup){
		Point bestMooc = null;
		double min_d = Double.MAX_VALUE;
		for(int i = 0; i < startMoocPoints.size(); i++){
			Point stMooc = startMoocPoints.get(i);
			int groupMooc = point2Group.get(stMooc);
			if(group2marked.get(groupMooc) == 1
				|| XR.route(stMooc) != Constants.NULL_POINT)
				continue;
			double d = getTravelTime(p.getLocationCode(), stMooc.getLocationCode())
				+ getTravelTime(stMooc.getLocationCode(), pickup.getLocationCode());
			if(d < min_d){
				min_d = d;
				bestMooc = stMooc;
			}
		}
		return bestMooc;
	}
	
	public Point getBestMoocForRequest(Point curStMooc, Point p, Point np, Point q, Point nq){
		Point bestMooc = curStMooc;
		double min_d = Double.MAX_VALUE;
		for(int i = 0; i < startMoocPoints.size(); i++){
			Point stMooc = startMoocPoints.get(i);
			Point enMooc = start2stopMoocPoint.get(stMooc);
			int groupMooc = point2Group.get(stMooc);
			if((group2marked.get(groupMooc) == 1
				|| XR.route(stMooc) != Constants.NULL_POINT) && stMooc != curStMooc)
				continue;
			double d = getTravelTime(p.getLocationCode(), stMooc.getLocationCode())
				+ getTravelTime(stMooc.getLocationCode(), np.getLocationCode())
				+ getTravelTime(q.getLocationCode(), enMooc.getLocationCode())
				+ getTravelTime(enMooc.getLocationCode(), nq.getLocationCode());
			if(d < min_d){
				min_d = d;
				bestMooc = stMooc;
			}
		}
		return bestMooc;
	}
	
	public Point getBestDepotMoocForRoute(int r){
//		HashMap<Mooc, Integer> mooc2marked = new HashMap<Mooc, Integer>();
		Mooc[] moocs = input.getMoocs();
//		for(int i = 0; i < moocs.length; i++)
//			mooc2marked.put(moocs[i], 0);
//		for(int i = 0; i < startMoocPoints.size(); i++){
//			Point stMooc = startMoocPoints.get(i);
//			if(XR.route(stMooc) != Constants.NULL_POINT){
//				Mooc mooc = startPoint2Mooc.get(stMooc);
//				mooc2marked.put(mooc, 1);
//			}
//		}
		
		Point st = XR.getStartingPointOfRoute(r);
		Point bestMooc = null;
		double min_d = Double.MAX_VALUE;
		for(int i = 0; i < startMoocPoints.size(); i++){
			Point stMooc = startMoocPoints.get(i);

			int groupMooc = point2Group.get(stMooc);
			if(group2marked.get(groupMooc) == 1)
				continue;
			double d = objective.evaluateAddOnePoint(stMooc, st);
			if(d < min_d){
				min_d = d;
				bestMooc = stMooc;
			}
		}
		return bestMooc;
	}
	
	public int getNbRejectedRequests(){
		Set<Integer> grs = new HashSet<Integer>();
		for(int i = 0; i < rejectPickupPoints.size(); i++){
			Point pickup = rejectPickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			
			if(group2marked.get(groupId) == 1)
				continue;
			grs.add(groupId);
		}
		return grs.size();
	}
	
	public int getNbUsedTrucks(){
		int nb = 0;
		for(int r = 1; r <= XR.getNbRoutes(); r++){
			if(XR.index(XR.getTerminatingPointOfRoute(r)) > 3)
				nb++;
		}
		return nb;
	}
	
	public Truck getNearestTruck(String locationCode){
		int minTime = Integer.MAX_VALUE;
		Truck truck = null;
		for(int i = 0; i < input.getTrucks().length; i++){
			int d = getTravelTime(input.getTrucks()[i].getDepotTruckLocationCode(),
					locationCode);
			if(d < minTime){
				minTime = d;
				truck = input.getTrucks()[i];
			}
		}
		return truck;
	}
	
	public Mooc getNearestMooc(String locationCode){
		int minTime = Integer.MAX_VALUE;
		Mooc mooc = null;
		for(int i = 0; i < input.getMoocs().length; i++){
			int d = getTravelTime(input.getMoocs()[i].getDepotMoocLocationCode(),
					locationCode);
			if(d < minTime){
				minTime = d;
				mooc = input.getMoocs()[i];
			}
		}
		return mooc;
	}
	
	public int getTravelTime(String src, String dest) {
		if (mLocationCode2Index.get(src) == null
				|| mLocationCode2Index.get(dest) == null) {
			 System.out.println("::getTravelTime, src " + src +
			 " OR dest " + dest + " NOT COMPLETE, INPUT ERROR??????");
			//return 1000;

		}

		int is = mLocationCode2Index.get(src);
		int id = mLocationCode2Index.get(dest);
		return (int) travelTime[is][id];
	}

	public void greedyInitSolution(){
		double currtime = System.currentTimeMillis();

		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			Point beststMooc1 = null;
			Point bestenMooc1 = null;
			Point beststMooc2 = null;
			Point best_pre = null;
			double best_objective = Double.MAX_VALUE;
			
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						Point pp = p;
						Point qq = q;
						Point stMooc1 = null;
						Point stMooc2 = null;
						Point enMooc1 = null;
						Point enMooc2 = null;
						Point pre = null;
						if(accMoocInvr.getSumWeights(p) == 0){
							stMooc1 = getBestStartMoocForRequest(r, p, pickup);
							mgr.performAddOnePoint(stMooc1, p);
							pp = stMooc1;
							if(p == q)
								qq = pp;
							enMooc1 = start2stopMoocPoint.get(stMooc1);
						}

						if(accMoocInvr.getWeights(delivery) < 0){
							enMooc1 = null;
							System.out.println("accContainerInvr.getWeights("
									+ XR.next(q).getID() + ") = "
									+ accContainerInvr.getWeights(XR.next(q)));
							if(accContainerInvr.getWeights(XR.next(q)) > 0){
								stMooc2 = getBestStartMoocForRequest(r, q, delivery);
								mgr.performAddOnePoint(stMooc2, q);
								enMooc1 = start2stopMoocPoint.get(stMooc2);
							}
						}
						for(Point h = XR.next(q); h != XR.getTerminatingPointOfRoute(r); h = XR.next(h)){
							if(enMooc1 != null
								&& stopMoocPoints.contains(h)){
									enMooc2 = h;
									break;
								}
						}
						if(enMooc1 != null){
							if(enMooc2 != null){
								pre = XR.prev(enMooc2);
								mgr.performAddOnePoint(enMooc1, pre);
								mgr.performRemoveOnePoint(enMooc2);
							}
							else{
								pre = XR.prev(XR.getTerminatingPointOfRoute(r));
								mgr.performAddOnePoint(enMooc1, pre);
							}
						}
						
						mgr.performAddTwoPoints(pickup, pp, delivery, qq);
						if(S.violations() == 0){
							double cost = objective.getValue();
							if( cost < best_objective){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
								beststMooc1 = stMooc1;
								bestenMooc1 = enMooc1;
								beststMooc2 = stMooc2;
								best_pre = pre;
							}
						}
						mgr.performRemoveTwoPoints(pickup, delivery);
						if(stMooc1 != null)
							mgr.performRemoveOnePoint(stMooc1);
						if(stMooc2 != null)
							mgr.performRemoveOnePoint(stMooc2);
						if(enMooc2 != null && enMooc1 != null)
							mgr.performAddOnePoint(enMooc2, pre);
						if(enMooc1 != null)
							mgr.performRemoveOnePoint(enMooc1);
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				if(accMoocInvr.getSumWeights(pre_pick) == 0 && beststMooc1 != null){
					mgr.performAddOnePoint(beststMooc1, pre_pick);
					if(pre_pick == pre_delivery)
						pre_delivery = beststMooc1;
					pre_pick = beststMooc1;
					
					int groupMooc= point2Group.get(beststMooc1);
					group2marked.put(groupMooc, 1);
				}
				if(accMoocInvr.getWeights(delivery) < 0)
					if(accContainerInvr.getWeights(XR.next(pre_delivery)) > 0 && beststMooc2 != null){
						mgr.performAddOnePoint(beststMooc2, pre_delivery);
						int groupMooc= point2Group.get(beststMooc2);
						group2marked.put(groupMooc, 1);
					}
				if(bestenMooc1 != null){
					Point enM = XR.next(best_pre);
					mgr.performAddOnePoint(bestenMooc1, best_pre);
					if(enM != XR.getTerminatingPointOfRoute(XR.route(best_pre)))
						mgr.performRemoveOnePoint(enM);
				}
				
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				Point st = XR.getStartingPointOfRoute(XR.route(pre_pick));
				int groupTruck = point2Group.get(st);
				group2marked.put(groupTruck, 1);
				group2marked.put(groupId, 1);
			}
		}
		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
//				int groupId = point2Group.get(pickup);
//				if(group2marked.get(groupId) == 0)
//					testTimeWindow(pickup);
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		
		//insertMoocToRoutes();
		
		int nRp = getNbRejectedRequests();
		int nB = getNbUsedTrucks();
		System.out.println("nb rejected requests = " + nRp
				+ ", nb trucks = " + nB
				+ ", cost = " + objective.getValue()
				+ ", time for inserting reqs = " + (System.currentTimeMillis() - currtime)/1000);
	}
	
	//chen req roi chen mooc
	public void greedyInitSolution2(){
		for(int i = 0; i < pickup2Delivery.size(); i++){
			System.out.println("req " + i + "/" + pickup2Delivery.size());
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE;
			int best_idx = 10000;
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
//				if(pickup.getID() == 985 && (r == 23 || r == 27))
//					System.out.println("ds");
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//						if(pickup.getID() == 1271 && p.getID() == 121 && q.getID() == 1222)
//							System.out.println("f");
						mgr.performAddTwoPoints(pickup, p, delivery, q);
						insertMoocToRoutes(r);
						if(S.violations() == 0){
							double cost = objective.getValue();
							if( cost < best_objective 
									|| (cost == best_objective && best_idx > r)){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
								best_idx = r;
							}
						}
						mgr.performRemoveTwoPoints(pickup, delivery);
						removeMoocOnRoutes(r);
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				//insertMoocToRoutes(XR.route(pre_pick));
				Point st = XR.getStartingPointOfRoute(XR.route(pre_pick));
				int groupTruck = point2Group.get(st);
				group2marked.put(groupTruck, 1);
				group2marked.put(groupId, 1);
			}
		}

		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
//				int groupId = point2Group.get(pickup);
//				if(group2marked.get(groupId) == 0)
//					testTimeWindow(pickup);
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		
		insertMoocForAllRoutes();
		
//		int nRp = getNbRejectedRequests();
//		int nB = getNbUsedTrucks();
//		System.out.println("nb rejected requests = " + nRp
//				+ ", nb trucks = " + nB
//				+ ", cost = " + objective.getValue());
		//checkRejectedRequestsInfo();
	}
	
	//chen req roi chen mooc
	public void greedyInitSolutionWithAcceptanceBPI(){
		Random ran = new Random();
		double T = 200;
		double k = 0.995;
		for(int i = 0; i < pickup2Delivery.size(); i++){
			System.out.println("req " + i + "/" + pickup2Delivery.size());
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE;
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
//					if(pickup.getID() == 985 && (r == 23 || r == 27))
//						System.out.println("ds");
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//							if(pickup.getID() == 1271 && p.getID() == 121 && q.getID() == 1222)
//								System.out.println("f");
						mgr.performAddTwoPoints(pickup, p, delivery, q);
						insertMoocToRoutes(r);
						if(S.violations() == 0){
							double cost = objective.getValue();
							if( cost < best_objective ){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
							}
							else{
								double v = Math.exp(-(cost - best_objective)/T);
								double e = ran.nextDouble();
								if( e < v){
									best_objective = cost;
									pre_pick = p;
									pre_delivery = q;
								}
							}
						}
						mgr.performRemoveTwoPoints(pickup, delivery);
						removeMoocOnRoutes(r);
					}
				}
			}
			T = k * T;
			if(pre_pick != null && pre_delivery != null){
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				//insertMoocToRoutes(XR.route(pre_pick));
				Point st = XR.getStartingPointOfRoute(XR.route(pre_pick));
				int groupTruck = point2Group.get(st);
				group2marked.put(groupTruck, 1);
				group2marked.put(groupId, 1);
			}
		}

		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
//					int groupId = point2Group.get(pickup);
//					if(group2marked.get(groupId) == 0)
//						testTimeWindow(pickup);
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		
		insertMoocForAllRoutes();
		
//			int nRp = getNbRejectedRequests();
//			int nB = getNbUsedTrucks();
//			System.out.println("nb rejected requests = " + nRp
//					+ ", nb trucks = " + nB
//					+ ", cost = " + objective.getValue());
		//checkRejectedRequestsInfo();
	}
	/***
	 * insert into scheduled points by stack
	 */
	public void firstPossibleInitFPI(){
		for(int i = 0; i < pickup2Delivery.size(); i++){
			System.out.println("req " + i + "/" + pickup2Delivery.size());
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			boolean isAdded = false;
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				if(isAdded)
					break;
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					if(isAdded)
						break;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						mgr.performAddTwoPoints(pickup, p, delivery, q);
						insertMoocToRoutes(r);
						if(S.violations() == 0){
							group2marked.put(groupTruck, 1);
							group2marked.put(groupId, 1);
							isAdded = true;
							removeMoocOnRoutes(r);
							break;
						}
						mgr.performRemoveTwoPoints(pickup, delivery);
						removeMoocOnRoutes(r);
					}
				}
			}
		}
		
		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		insertMoocForAllRoutes();
		
//		int nRp = getNbRejectedRequests();
//		int nB = getNbUsedTrucks();
//		System.out.println("nb rejected requests = " + nRp
//				+ ", nb trucks = " + nB
//				+ ", cost = " + objective.getValue());
		//checkRejectedRequestsInfo();
	}
	
	/***
	 * first possible insert into scheduled vehicles by stack
	 */
	public void firstPossibleInitFPIUS(){
		Stack<String> stack = new Stack<String>();
		for(int r = XR.getNbRoutes(); r >= 1; r--){
			String s = "" + r;
			stack.push(s);
		}
		
		for(int i = 0; i < pickup2Delivery.size(); i++){
			System.out.println("req " + i + "/" + pickup2Delivery.size());
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			boolean isAdded = false;
			for(int k = stack.size() - 1; k >= 0; k--){
				if(isAdded)
					break;
				int r = Integer.parseInt(stack.get(k));
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					if(isAdded)
						break;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						mgr.performAddTwoPoints(pickup, p, delivery, q);
						insertMoocToRoutes(r);
						if(S.violations() == 0){
							group2marked.put(groupTruck, 1);
							group2marked.put(groupId, 1);
							stack.remove(stack.get(k));
							String s = "" + r;
							stack.push(s);
							isAdded = true;
							removeMoocOnRoutes(r);
							break;
						}
						mgr.performRemoveTwoPoints(pickup, delivery);
						removeMoocOnRoutes(r);
					}
				}
			}
		}
		
		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		insertMoocForAllRoutes();
	}
	
	public void insertOneReq2oneTruck(){
		for(int i = 0; i < pickup2Delivery.size(); i++){
			System.out.println("req " + i + "/" + pickup2Delivery.size());
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				if(XR.index(XR.getTerminatingPointOfRoute(r)) > 2)
					continue;
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				mgr.performAddTwoPoints(pickup, st, delivery, st);
				insertMoocToRoutes(r);
				if(S.violations() == 0){
					group2marked.put(groupTruck, 1);
					group2marked.put(groupId, 1);
					removeMoocOnRoutes(r);
					break;
				}
			}
		}
		
		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		insertMoocForAllRoutes();
	}
	
	public void allRemoval(){
		System.out.println("all removal");
		mgr.performRemoveAllClientPoints();
		for(int i = 0; i < pickupPoints.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(!rejectPickupPoints.contains(pickup)){
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		
		for(int k : group2marked.keySet())
			group2marked.put(k, 0);
	}
	
	
	/***
	 * first possible insert into scheduled vehicles by stack
	 */
	public void heuristicFPIUS(String outputfile){
		int it = 0;
		Random ran = new Random();
		double T = 200;
		double kap = 0.995;
		
		TruckContainerSolution best_solution = null;
		double best_cost = Double.MAX_VALUE;
		int best_usedTruck = this.nVehicle;
		int best_nbReject = Integer.MAX_VALUE;
		
		TruckContainerSolution curr_solution = null;
		double curr_cost = Double.MAX_VALUE;
		int curr_usedTruck = this.nVehicle;
		int curr_nbReject = Integer.MAX_VALUE;

		double start_search_time = System.currentTimeMillis();
		
		while(it++ <= 1000 && (System.currentTimeMillis() - start_search_time)/1000 < 36000){
			allRemoval();
			
			Stack<String> stack = new Stack<String>();
			for(int r = XR.getNbRoutes(); r >= 1; r--){
				String s = "" + r;
				stack.push(s);
			}
			Collections.shuffle(rejectPickupPoints);
			for(int i = 0; i < rejectPickupPoints.size(); i++){
				System.out.println("req " + i + "/" + pickup2Delivery.size());
				Point pickup = rejectPickupPoints.get(i);
				int groupId = point2Group.get(pickup);
				if(XR.route(pickup) != Constants.NULL_POINT
					|| group2marked.get(groupId) == 1)
					continue;
				Point delivery = pickup2Delivery.get(pickup);
				//add the request to route
				boolean isAdded = false;
				for(int k = stack.size() - 1; k >= 0; k--){
					if(isAdded)
						break;
					int r = Integer.parseInt(stack.get(k));
					Point st = XR.getStartingPointOfRoute(r);
	
					int groupTruck = point2Group.get(st);
					if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
						continue;
					for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
						if(isAdded)
							break;
						for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
							mgr.performAddTwoPoints(pickup, p, delivery, q);
							insertMoocToRoutes(r);
							if(S.violations() == 0){
								group2marked.put(groupTruck, 1);
								group2marked.put(groupId, 1);
								stack.remove(stack.get(k));
								String s = "" + r;
								stack.push(s);
								isAdded = true;
								removeMoocOnRoutes(r);
								rejectPickupPoints.remove(pickup);
								rejectDeliveryPoints.remove(delivery);
								i--;
								break;
							}
							mgr.performRemoveTwoPoints(pickup, delivery);
							removeMoocOnRoutes(r);
						}
					}
				}
			}
			
			insertMoocForAllRoutes();
			
			
			System.out.println("nb of iterator: " + it);
			double new_cost = objective.getValue();
			int new_nbTrucks = getNbUsedTrucks();
			int new_rejected = getNbRejectedRequests();

			if(best_solution == null){
				best_cost = curr_cost = new_cost;
				best_nbReject = curr_nbReject = new_rejected;
				best_usedTruck = curr_usedTruck = new_nbTrucks;
				best_solution = new TruckContainerSolution(XR, rejectPickupPoints,
						rejectDeliveryPoints, new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
				curr_solution = new TruckContainerSolution(XR, rejectPickupPoints,
						rejectDeliveryPoints, new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
			}
			else{
				if( new_rejected < curr_nbReject
						|| (new_rejected == curr_nbReject && new_nbTrucks < curr_usedTruck)
						|| (new_rejected == curr_nbReject && new_nbTrucks == curr_usedTruck && new_cost < curr_cost)){
					
					if(new_rejected < best_nbReject
							|| (new_rejected == best_nbReject && new_nbTrucks < best_usedTruck)
							|| (new_rejected == best_nbReject && new_nbTrucks == best_usedTruck && new_cost < best_cost)){
						
						best_cost = new_cost;
						best_nbReject = new_rejected;
						best_usedTruck = new_nbTrucks;
						
						best_solution = new TruckContainerSolution(XR, rejectPickupPoints, rejectDeliveryPoints,
								new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
						try{
							FileOutputStream write = new FileOutputStream(outputfile, true);
							PrintWriter fo = new PrintWriter(write);
							fo.println(it + " " + 
								+ System.currentTimeMillis()/1000 + " "
								+ best_cost + " " + best_nbReject + " " + best_usedTruck);
							fo.close();
						}catch(Exception e){
							System.out.println(e);
						}
					}
				}
				else{
					double v = Math.exp(-(new_cost - best_cost)/T);
					double e = ran.nextDouble();
					if( e < v){
						best_cost = new_cost;
						best_cost = new_cost;
						best_nbReject = new_rejected;
						best_usedTruck = new_nbTrucks;
						
						best_solution = new TruckContainerSolution(XR, rejectPickupPoints, rejectDeliveryPoints,
								new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
						try{
							FileOutputStream write = new FileOutputStream(outputfile, true);
							PrintWriter fo = new PrintWriter(write);
							fo.println(it + " " + 
								+ System.currentTimeMillis()/1000 + " "
								+ best_cost + " " + best_nbReject + " " + best_usedTruck);
							fo.close();
						}catch(Exception ex){
							System.out.println(ex);
						}
					}
				}
			}
			T = T * kap;
		}
		best_solution.copy2XR(XR);
		group2marked = best_solution.get_group2marked();
		
		rejectPickupPoints = best_solution.get_rejectPickupPoints();
		rejectDeliveryPoints = best_solution.get_rejectDeliveryPoints();
		
		try{
			FileOutputStream write = new FileOutputStream(outputfile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(it + " " + 
				+ System.currentTimeMillis()/1000 + " "
				+ best_cost + " " + best_nbReject + " " + best_usedTruck);
			fo.close();
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	
	public void heuristicBPIUS(String outputfile){
		int it = 0;
		Random ran = new Random();
		double T = 200;
		double kap = 0.995;
		
		
		TruckContainerSolution best_solution = null;
		double best_cost = Double.MAX_VALUE;
		int best_usedTruck = this.nVehicle;
		int best_nbReject = Integer.MAX_VALUE;
		
		TruckContainerSolution curr_solution = null;
		double curr_cost = Double.MAX_VALUE;
		int curr_usedTruck = this.nVehicle;
		int curr_nbReject = Integer.MAX_VALUE;

		double start_search_time = System.currentTimeMillis();
		
		while(it++ <= nIter && (System.currentTimeMillis() - start_search_time) < timeLimit){
			allRemoval();
			
			Stack<String> stack = new Stack<String>();
			for(int r = XR.getNbRoutes(); r >= 1; r--){
				String s = "" + r;
				stack.push(s);
			}
			Collections.shuffle(rejectPickupPoints);
			for(int i = 0; i < rejectPickupPoints.size(); i++){
				//System.out.println("req " + i + "/" + pickup2Delivery.size());
				Point pickup = rejectPickupPoints.get(i);
				int groupId = point2Group.get(pickup);
				if(XR.route(pickup) != Constants.NULL_POINT
					|| group2marked.get(groupId) == 1)
					continue;
				Point delivery = pickup2Delivery.get(pickup);
				
				Point pre_pick = null;
				Point pre_delivery = null;
				double best_objective = Double.MAX_VALUE;
				int best_idx = -1;
				
				//add the request to route
				for(int k = stack.size() - 1; k >= 0; k--){
					int r = Integer.parseInt(stack.get(k));
					Point st = XR.getStartingPointOfRoute(r);

					int groupTruck = point2Group.get(st);
					if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
						continue;
//					if(pickup.getID() == 985 && (r == 23 || r == 27))
//						System.out.println("ds");
					for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
						for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//							if(pickup.getID() == 1271 && p.getID() == 121 && q.getID() == 1222)
//								System.out.println("f");
							mgr.performAddTwoPoints(pickup, p, delivery, q);
							insertMoocToRoutes(r);
							if(S.violations() == 0){
								double cost = objective.getValue();
								if( cost < best_objective ){
									best_objective = cost;
									pre_pick = p;
									pre_delivery = q;
									best_idx = k;
								}
								else{
									double v = Math.exp(-(cost - best_objective)/T);
									double e = ran.nextDouble();
									if( e < v){
										best_objective = cost;
										pre_pick = p;
										pre_delivery = q;
									}
								}
							}
							mgr.performRemoveTwoPoints(pickup, delivery);
							removeMoocOnRoutes(r);
						}
					}
				}
				T = kap * T;
				if(pre_pick != null && pre_delivery != null){
					mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
					//insertMoocToRoutes(XR.route(pre_pick));
					Point st = XR.getStartingPointOfRoute(XR.route(pre_pick));
					int groupTruck = point2Group.get(st);
					group2marked.put(groupTruck, 1);
					group2marked.put(groupId, 1);
					String s = stack.get(best_idx);
					stack.remove(best_idx);
					stack.push(s);
					rejectPickupPoints.remove(pickup);
					rejectDeliveryPoints.remove(delivery);
					i--;
				}
			}
			
			insertMoocForAllRoutes();
			
			
			System.out.println("nb of iterator: " + it);
			double new_cost = objective.getValue();
			int new_nbTrucks = getNbUsedTrucks();
			int new_rejected = getNbRejectedRequests();

			if(best_solution == null){
				best_cost = curr_cost = new_cost;
				best_nbReject = curr_nbReject = new_rejected;
				best_usedTruck = curr_usedTruck = new_nbTrucks;
				best_solution = new TruckContainerSolution(XR, rejectPickupPoints,
						rejectDeliveryPoints, new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
				curr_solution = new TruckContainerSolution(XR, rejectPickupPoints,
						rejectDeliveryPoints, new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
			}
			else{
//				if( new_rejected < curr_nbReject
//						|| (new_rejected == curr_nbReject && new_nbTrucks < curr_usedTruck)
//						|| (new_rejected == curr_nbReject && new_nbTrucks == curr_usedTruck && new_cost < curr_cost)){
				if(new_cost < curr_cost){
					curr_solution = new TruckContainerSolution(XR, rejectPickupPoints,
							rejectDeliveryPoints, new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
					curr_cost = objective.getValue();
					curr_nbReject = new_rejected;
					curr_usedTruck = new_nbTrucks;
//					if(new_rejected < best_nbReject
//							|| (new_rejected == best_nbReject && new_nbTrucks < best_usedTruck)
//							|| (new_rejected == best_nbReject && new_nbTrucks == best_usedTruck && new_cost < best_cost)){
					if(new_cost < best_cost){
						
						best_cost = new_cost;
						best_nbReject = new_rejected;
						best_usedTruck = new_nbTrucks;
						
						best_solution = new TruckContainerSolution(XR, rejectPickupPoints, rejectDeliveryPoints,
								new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
						try{
							FileOutputStream write = new FileOutputStream(outputfile, true);
							PrintWriter fo = new PrintWriter(write);
							fo.println(it + " " + 
								+ System.currentTimeMillis()/1000 + " "
								+ best_cost + " " + best_nbReject + " " + best_usedTruck);
							fo.close();
						}catch(Exception e){
							System.out.println(e);
						}
					}
				}
				else{
					double v = Math.exp(-(new_cost - best_cost)/T);
					double e = ran.nextDouble();
					if( e < v){
						curr_cost = new_cost;
						curr_nbReject = new_rejected;
						curr_usedTruck = new_nbTrucks;
						
						curr_solution = new TruckContainerSolution(XR, rejectPickupPoints, rejectDeliveryPoints,
								new_cost, new_nbTrucks, new_rejected, point2Group, group2marked);
//						try{
//							FileOutputStream write = new FileOutputStream(outputfile, true);
//							PrintWriter fo = new PrintWriter(write);
//							fo.println(it + " " + 
//								+ System.currentTimeMillis()/1000 + " "
//								+ best_cost + " " + best_nbReject + " " + best_usedTruck);
//							fo.close();
//						}catch(Exception ex){
//							System.out.println(ex);
//						}
					}
					else{
						curr_solution.copy2XR(XR);
					}
				}
			}
			T = T * kap;
		}
		best_solution.copy2XR(XR);
		group2marked = best_solution.get_group2marked();
		
		rejectPickupPoints = best_solution.get_rejectPickupPoints();
		rejectDeliveryPoints = best_solution.get_rejectDeliveryPoints();
		
		try{
			FileOutputStream write = new FileOutputStream(outputfile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(it + " " + 
				+ System.currentTimeMillis()/1000 + " "
				+ best_cost + " " + best_nbReject + " " + best_usedTruck);
			fo.close();
		}catch(Exception ex){
			System.out.println(ex);
		}
	}

	/***
	 * best possible insert into scheduled vehicles by stack
	 */
	public void bestPossibleInitBPIUS(){
		Random ran = new Random();
		double T = 200;
		double kap = 0.995;
		Stack<String> stack = new Stack<String>();
		for(int r = XR.getNbRoutes(); r >= 1; r--){
			String s = "" + r;
			stack.push(s);
		}
		
		for(int i = 0; i < pickup2Delivery.size(); i++){
			System.out.println("req " + i + "/" + pickup2Delivery.size());
			Point pickup = pickupPoints.get(i);
			int groupId = point2Group.get(pickup);
			if(XR.route(pickup) != Constants.NULL_POINT
				|| group2marked.get(groupId) == 1)
				continue;
			Point delivery = deliveryPoints.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE;
			int best_idx = -1;
			
			for(int k = stack.size() - 1; k >= 0; k--){
				int r = Integer.parseInt(stack.get(k));
				Point st = XR.getStartingPointOfRoute(r);

				int groupTruck = point2Group.get(st);
				if(group2marked.get(groupTruck) == 1 && XR.index(XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
//				if(pickup.getID() == 985 && (r == 23 || r == 27))
//					System.out.println("ds");
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
//						if(pickup.getID() == 1271 && p.getID() == 121 && q.getID() == 1222)
//							System.out.println("f");
						mgr.performAddTwoPoints(pickup, p, delivery, q);
						insertMoocToRoutes(r);
						if(S.violations() == 0){
							double cost = objective.getValue();
							if( cost < best_objective ){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
								best_idx = k;
							}
							else{
								double v = Math.exp(-(cost - best_objective)/T);
								double e = ran.nextDouble();
								if( e < v){
									best_objective = cost;
									pre_pick = p;
									pre_delivery = q;
								}
							}
						}
						mgr.performRemoveTwoPoints(pickup, delivery);
						removeMoocOnRoutes(r);
					}
				}
			}
			T = kap * T;
			if(pre_pick != null && pre_delivery != null){
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				//insertMoocToRoutes(XR.route(pre_pick));
				Point st = XR.getStartingPointOfRoute(XR.route(pre_pick));
				int groupTruck = point2Group.get(st);
				group2marked.put(groupTruck, 1);
				group2marked.put(groupId, 1);
				String s = stack.get(best_idx);
				stack.remove(best_idx);
				stack.push(s);
			}
		}
		
		for(int i = 0; i < pickup2Delivery.size(); i++){
			Point pickup = pickupPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectPickupPoints.contains(pickup)){
				rejectPickupPoints.add(pickup);
				rejectDeliveryPoints.add(pickup2Delivery.get(pickup));
			}
		}
		insertMoocForAllRoutes();
	}

	public void removeAllMoocFromRoutes(){
		for(int i = 0; i < startMoocPoints.size(); i++){
			Point st = startMoocPoints.get(i);
			Point tp = start2stopMoocPoint.get(st);
			if(XR.route(st) != Constants.NULL_POINT){
				mgr.performRemoveOnePoint(st);
				int groupMooc = point2Group.get(st);
				group2marked.put(groupMooc, 0);
			}
			if(XR.route(tp) != Constants.NULL_POINT){
				mgr.performRemoveOnePoint(tp);
				int groupMooc = point2Group.get(tp);
				group2marked.put(groupMooc, 0);
			}
		}
	}
	
	public void removeMoocOnRoutes(int r){
		Point x = XR.getStartingPointOfRoute(r);
		Point next_x = XR.next(x);
		while(next_x != XR.getTerminatingPointOfRoute(r)){
			x = next_x;
			next_x = XR.next(x);
			if(startMoocPoints.contains(x) || stopMoocPoints.contains(x)){
				mgr.performRemoveOnePoint(x);
				int groupMooc = point2Group.get(x);
				group2marked.put(groupMooc, 0);
			}
		}
	}
	
	public void stateModel(){
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		S = new ConstraintSystemVR(mgr);
		for(int i = 0; i < startPoints.size(); ++i)
			XR.addRoute(startPoints.get(i), stopPoints.get(i));
		
		for(int i = 0; i < pickupPoints.size(); ++i)
		{
			Point pickup = pickupPoints.get(i);
			Point delivery = deliveryPoints.get(i);
			XR.addClientPoint(pickup);
			XR.addClientPoint(delivery);
		}
		for(int i = 0; i < startMoocPoints.size(); ++i){
			XR.addClientPoint(startMoocPoints.get(i));
			XR.addClientPoint(stopMoocPoints.get(i));
		}
		
		//time windows
		eat = new EarliestArrivalTimeVR(XR,awm,earliestAllowedArrivalTime,serviceDuration);
		cEarliest = new CEarliestArrivalTimeVR(eat, lastestAllowedArrivalTime);
		
		//accumulated mooc
		accMoocInvr = new AccumulatedWeightNodesVR(XR, nwMooc);
		
		//accumulated container
		accContainerInvr = new AccumulatedWeightNodesVR(XR, nwContainer);
		
		//container capacity constraint
		capContCtr = new ContainerCapacityConstraint(XR, accContainerInvr);
		
		//mooc capacity constraint
		capMoocCtr = new MoocCapacityConstraint(XR, accMoocInvr);
		
		//container carried by trailer constraint
		contmoocCtr = new ContainerCarriedByTrailerConstraint(XR, accContainerInvr, accMoocInvr);
		
		S.post(cEarliest);
		S.post(capContCtr);
		S.post(capMoocCtr);
		S.post(contmoocCtr);
		objective = new TotalCostVR(XR,awm);
		valueSolution = new LexMultiValues();
		valueSolution.add(S.violations());
		valueSolution.add(objective.getValue());
		mgr.close();
	}
	
	public void testTimeWindow(Point pickup){
		int groupId = point2Group.get(pickup);
		if(XR.route(pickup) != Constants.NULL_POINT
			|| group2marked.get(groupId) == 1)
			System.out.println("conflict");
		Point delivery = pickup2Delivery.get(pickup);
		//add the request to route
		Point pre_pick = null;
		Point pre_delivery = null;
		mgr.performRemoveAllClientPoints();
		
		double best_objective = Double.MAX_VALUE;
		for(int r = 1; r <= XR.getNbRoutes(); r++){
			Point st = XR.getStartingPointOfRoute(r);
			Truck truck = startPoint2Truck.get(st);

			for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)){
				for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
					System.out.println("pick = " + pickup.getID()
							+ ", p = " + p.getID()
							+ ", del = " + delivery.getID()
							+ ", q = " + q.getID());
					System.out.println("arrTime p = " + DateTimeUtils.unixTimeStamp2DateTime((long)eat.getEarliestAllowedArrivalTime(p)));
					System.out.println("travelTime p ->pick = " + eat.getTravelTime(p, pickup));
					System.out.println("late = " + DateTimeUtils.unixTimeStamp2DateTime((long)lastestAllowedArrivalTime.get(pickup)));
					System.out.println("arrTime q = " + DateTimeUtils.unixTimeStamp2DateTime((long)eat.getEarliestAllowedArrivalTime(pickup)));
					System.out.println("travelTime pick ->del = " + eat.getTravelTime(pickup, delivery));
					System.out.println("arrTime del = " + DateTimeUtils.unixTimeStamp2DateTime((long)(eat.getEarliestAllowedArrivalTime(pickup)
							+ eat.getTravelTime(pickup, delivery))));
					System.out.println("late del = " + DateTimeUtils.unixTimeStamp2DateTime((long)lastestAllowedArrivalTime.get(delivery)));
					
					mgr.performAddTwoPoints(pickup, p, delivery, q);
					insertMoocToRoutes(r);
					if(S.violations() == 0){
						System.out.println("route = " + r + " is ok");
					}
					else
						System.out.println("pick = " + pickup.getID() + " not ok");
					mgr.performRemoveTwoPoints(pickup, delivery);
					removeMoocOnRoutes(r);
				}
			}
		}
		System.out.println("stop");
	}
	
	public void printSolution(String outputfile){
		String s = "";

		int K = XR.getNbRoutes();
		int cost = 0;
		for(int k=1; k<=K; k++){
			s += "route[" + k + "] = ";
			Point x = XR.getStartingPointOfRoute(k);
			for(; x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
				s = s + x.getLocationCode() + " (" + point2Type.get(x) + ") -> ";
			}
			x = XR.getTerminatingPointOfRoute(k);
			s = s + x.getLocationCode()  + " (" + point2Type.get(x) + ")" + "\n";
		}		
		System.out.println(s);
		
		
		int nbR = getNbRejectedRequests();
		int nB = getNbUsedTrucks();
		System.out.println("Search done. At end search number of reject points = " + nbR
				+ ", nb Trucks = " + nB
				+ ",  cost = " + objective.getValue());
		long t = System.currentTimeMillis();
		try{
			FileOutputStream write = new FileOutputStream(outputfile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(s);
			fo.println("end time = " + DateTimeUtils.unixTimeStamp2DateTime(t/1000)
					+ ", #RejectedReqs = " + nbR
					+ ", nb Trucks = " + nB
					+ ", cost = " + objective.getValue());
			
			fo.close();
		}catch(Exception e){
			
		}
		
	}
	
	public TruckMoocContainerOutputJson createFormatedSolution() {
		ArrayList<TruckRoute> brArr = new ArrayList<TruckRoute>();

		int nbTrucks = 0;
		for (int r = 1; r <= XR.getNbRoutes(); r++) {
			int nb = XR.index(XR.getTerminatingPointOfRoute(r)) + 1;
			Truck truck = startPoint2Truck.get(XR.getStartingPointOfRoute(r));
			
			if(nb <= 2)
				continue;
			
			double d = 0;
			int nbPers = 0;
			Point st = XR.getStartingPointOfRoute(r);
			Point en = XR.getTerminatingPointOfRoute(r);
			
			int g = 0;
			RouteElement[] nodes = new RouteElement[nb];

			for(Point p = st; p != XR
					.getTerminatingPointOfRoute(r); p = XR.next(p)) {			

				nodes[g] = new RouteElement(p.getLocationCode(), point2Type.get(p),
						DateTimeUtils.unixTimeStamp2DateTime((long)(eat.getEarliestArrivalTime(p))),
						DateTimeUtils.unixTimeStamp2DateTime((long)(eat.getEarliestArrivalTime(p) + serviceDuration.get(p))), 
						(int)awm.getWeight(p, XR.next(p)));
				g++;
			}
			
			

			nodes[g] = new RouteElement(XR.getTerminatingPointOfRoute(r).getLocationCode(),
					point2Type.get(XR.getTerminatingPointOfRoute(r)),
					DateTimeUtils.unixTimeStamp2DateTime((long)eat.getEarliestArrivalTime(en)),
					DateTimeUtils.unixTimeStamp2DateTime((long)(eat.getEarliestArrivalTime(en) + serviceDuration.get(en))), 0);
			
			TruckRoute br = new TruckRoute(r, truck, nb, (int)objective.getValue(), nodes);
			brArr.add(br);
			br.setIndex(brArr.size()); // reset the index so that 1, 2, 3, ...
			nbTrucks++;
		}
		
		TruckRoute[] truckRoutes = new TruckRoute[brArr.size()];
		for(int i = 0; i < brArr.size(); i++)
			truckRoutes[i] = brArr.get(i);
		
		HashSet<ExportEmptyRequests> unscheduledEE = new HashSet<ExportEmptyRequests>();
		HashSet<ExportLadenRequests> unscheduledEL = new HashSet<ExportLadenRequests>();
		HashSet<ImportEmptyRequests> unscheduledIE = new HashSet<ImportEmptyRequests>();
		HashSet<ImportLadenRequests> unscheduledIL = new HashSet<ImportLadenRequests>();
		int nbRejects = 0;
		for(int i = 0; i < rejectPickupPoints.size(); i++){
			int groupId = point2Group.get(rejectPickupPoints.get(i));
			if(group2marked.get(groupId) == 0
					&& group2EE.get(groupId) != null)
				unscheduledEE.add(group2EE.get(groupId));
			else if(group2marked.get(groupId) == 0
					&& group2EL.get(groupId) != null)
				unscheduledEL.add(group2EL.get(groupId));
			else if(group2marked.get(groupId) == 0
					&& group2IE.get(groupId) != null)
				unscheduledIE.add(group2IE.get(groupId));
			else if(group2marked.get(groupId) == 0
					&& group2IL.get(groupId) != null)
				unscheduledIL.add(group2IL.get(groupId));
		}
		ExportEmptyRequests[] ee = new ExportEmptyRequests[unscheduledEE.size()];
		ExportLadenRequests[] el = new ExportLadenRequests[unscheduledEL.size()];
		ImportEmptyRequests[] ie = new ImportEmptyRequests[unscheduledIE.size()];
		ImportLadenRequests[] il = new ImportLadenRequests[unscheduledIL.size()];

		int i = 0;
		for(ExportEmptyRequests r : unscheduledEE){
			ee[i] = r;
			i++;
		}
		i = 0;
		for(ImportEmptyRequests r : unscheduledIE){
			ie[i] = r;
			i++;
		}
		i = 0;
		for(ExportLadenRequests r : unscheduledEL){
			el[i] = r;
			i++;
		}
		i=0;
		for(ImportLadenRequests r : unscheduledIL){
			il[i] = r;
			i++;
		}
		int totalRejectReqs = unscheduledEE.size() + unscheduledEL.size()
				+ unscheduledIE.size() + unscheduledIL.size();
		
		StatisticInformation statisticInformation = new StatisticInformation(
				this.nRequest,totalRejectReqs, objective.getValue(), nbTrucks);
		
		return new TruckMoocContainerOutputJson(truckRoutes, 
				ee, el, ie, il, statisticInformation);
	}
	
	//run some init algorithm for comparison
//	public static void main(String[] args){
//		String dir = "data/truck-container/";
//		int[] nbReqs = new int[]{8, 70, 100, 150, 200};
//		String[] initType = new String[]{"greedyInit", "firstPossibleInit1", "firstPossibleInit2"};
//		for(int i = 0; i < nbReqs.length; i++){
//			for(int j = 0; j < initType.length; j++){
//				String fileName = "random_big_data-"+ nbReqs[i] + "reqs-1.json";
//				String outputfile = dir + "output/result-" + fileName + "-" + initType[j] + ".txt"; 
//				String dataFileName = dir + fileName;
//				
//				TruckContainerSolver solver = new TruckContainerSolver();
//				solver.readData(dataFileName);
//				solver.init();
//				solver.stateModel();
//				
//				double t = System.currentTimeMillis();
//				try{
//					FileOutputStream write = new FileOutputStream(outputfile);
//					PrintWriter fo = new PrintWriter(write);
//					fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()) 
//							+ ", total reqs = " + nRequest
//							+ ", total truck = " + nVehicle);
//					
//					fo.close();
//				}catch(Exception e){
//					
//				}
//				switch(initType[j]){
//					case "greedyInit": solver.greedyInitSolution2(); break;
//					case "firstPossibleInit1": solver.firstPossibleInit1(); break;
//					case "firstPossibleInit2": solver.firstPossibleInit2(); break;
//				}			
//		
//				//solver.adaptiveSearchOperators(outputfile);
//				solver.printSolution(outputfile, t);
//			}
//		}
//	}
	
	
	//efficient of operators
//	public static void main(String[] args){
//		int[] nbReq = new int[]{4, 8, 70, 100, 150, 200};
//		double[] lower_removal_list = new double[]{0.05, 0.1};
//		double[] upper_removal_list = new double[]{0.2, 0.25};
//		int[] sigma1_list = new int[]{3, 5, 10};
//		int[] sigma2_list = new int[]{1, 0, -1};
//		int[] sigma3_list = new int[]{-3, -5, -10};
//		for(int i1 = 0; i1 < lower_removal_list.length; i1++){
//			for(int i2 = 0; i2 < upper_removal_list.length; i2++){
//				for(int i3 = 0; i3 < sigma1_list.length; i3++){
//					for(int i4 = 0; i4 < sigma2_list.length; i4++){
//						for(int i5 = 0; i5 < sigma3_list.length; i5++){
//							String dir = "data/truck-container/";
//							
//							String fileName = "random-20reqs-RealLoc-0";
//							String dataFileName = dir + "input/" + fileName + ".txt";
//							
//							String outputALNSfileTxt = dir + "output/newTuning/ALNS-" + i1 + "-" + i2 + "-" + i3 + "-" + i4 + "-" + i5 + ".txt";
//							String outputALNSfileJson = dir + "output/newTuning/ALNS-" + i1 + "-" + i2 + "-" + i3 + "-" + i4 + "-" + i5 + ".json";
//							
//							try
//							{
//								File temp = new File(outputALNSfileTxt);
//								if(temp.exists())
//									continue;
//							} catch (Exception e){
//								e.printStackTrace();
//							}
//							
//							TruckContainerSolver solver = new TruckContainerSolver();
//							solver.readData(dataFileName);
//							solver.init();
//							solver.stateModel();
//				
//							try{
//								FileOutputStream write = new FileOutputStream(outputALNSfileTxt);
//								PrintWriter fo = new PrintWriter(write);
//								fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) 
//										+ ", total reqs = " + nRequest
//										+ ", total truck = " + nVehicle);
//								
//								fo.close();
//							}catch(Exception e){
//								System.out.println(e);
//							}
//							solver.firstPossibleInitFPIUS();
//							
//							solver.timeLimit = 3600000;
//							solver.nIter = 10000;
//							
//							solver.nRemovalOperators = 8;
//							solver.nInsertionOperators = 8;
//							
//							solver.lower_removal = (int) lower_removal_list[i1]*nRequest;
//							solver.upper_removal = (int) upper_removal_list[i2]*nRequest;
//							solver.sigma1 = sigma1_list[i3];
//							solver.sigma2 = sigma2_list[i4];
//							solver.sigma3 = sigma3_list[i5];
//							
//							solver.rp = 0.1;
//							solver.nw = 1;
//							solver.shaw1st = 0.5;
//							solver.shaw2nd = 0.2;
//							solver.	shaw3rd = 0.1;
//					
//							solver.temperature = 200;
//							solver.cooling_rate = 0.9995;
//							solver.nTabu = 5;
//					
//							solver.initParamsForALNS();
//							solver.adaptiveSearchOperators(outputALNSfileTxt);
//							solver.printSolution(outputALNSfileTxt);
//							
//							Gson g = new Gson();
//							TruckMoocContainerOutputJson solution = solver.createFormatedSolution();
//							try{
//								String out = g.toJson(solution);
//								BufferedWriter writer = new BufferedWriter(new FileWriter(outputALNSfileJson));
//							    writer.write(out);
//							     
//							    writer.close();
//							}catch(Exception e){
//								System.out.println(e);
//							}
//						}
//					}
//				}
//			}
//		}
//			
//	}
	public static void main(String[] args){
		TruckContainerSolver solver = new TruckContainerSolver();
		solver.timeLimit = 360000; //gioi han thoi gian chay
		solver.nIter = 100000; //gioi han so lan lap
		
		String dir = "C:/Temp/";
		
		String fileName = "ttcrp";
		String inputFile = dir + "input/" + fileName + ".json.txt";
		
		String outputALNSfileTxt = dir + "output/Result-" + fileName + ".txt";
		String outputALNSfileJson = dir + "output/Result-" + fileName + ".json";
		
		
		solver.readData(inputFile);
		solver.init();
		solver.stateModel();

		try{
			FileOutputStream write = new FileOutputStream(outputALNSfileTxt);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) 
					+ ", total reqs = " + nRequest
					+ ", total truck = " + nVehicle);
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		solver.firstPossibleInitFPIUS();
		
		
		
		solver.nRemovalOperators = 8;
		solver.nInsertionOperators = 8;
		
		solver.lower_removal = (int) (0.1*nRequest);
		solver.upper_removal = (int) (0.25*nRequest);
		solver.sigma1 = 10;
		solver.sigma2 = -1;
		solver.sigma3 = -3;
		solver.rp = 0.1;
		solver.nw = 1;
		solver.shaw1st = 0.5;
		solver.shaw2nd = 0.2;
		solver.	shaw3rd = 0.1;
		solver.temperature = 200;
		solver.cooling_rate = 0.9995;
		solver.nTabu = 5;

		solver.initParamsForALNS();
		solver.adaptiveSearchOperators(outputALNSfileTxt);
		solver.printSolution(outputALNSfileTxt);
		
		Gson g = new Gson();
		TruckMoocContainerOutputJson solution = solver.createFormatedSolution();
		try{
			String out = g.toJson(solution);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputALNSfileJson));
		    writer.write(out);
		     
		    writer.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}
