package localsearch.domainspecific.vehiclerouting.apps.vinamilk.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Customer;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Distance;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.DistributionCenter;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.MDMTPInput;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Order;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Parking;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Product;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.RouteElement;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Utils;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Vehicle;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.VehicleRoute;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.utils.DateTimeUtils;

public class MDMTPSolver {
	public MDMTPInput input;
	public ArrayList<Point> allPoints;
	public HashMap<String, Point> locationId2Point;
	public HashMap<Integer, String> id2locationId;
	public HashMap<Integer, Point> id2point;

	public ArrayList<Point> startPoints;
	public ArrayList<Point> endPoints;
	public ArrayList<Point> depotPoints;
	public ArrayList<Point> customerPoints;
	public ArrayList<Point> rejectedPoints;
	
	public HashMap<Point, Integer> point2mark;
	public HashMap<Point, Integer> point2arrivalTime;
	
	public HashMap<Point, Integer> earliestAllowedArrivalTime;
	public HashMap<Point, Integer> waittingDuration;
	public HashMap<Point, Double> serviceDuration;
	public HashMap<Point, Integer> lastestAllowedArrivalTime;
	
	public HashMap<String, String> parking2startTime;
	public HashMap<String, String> parking2endTime;
	
	public double[][] matrixT;
	public HashMap<String, Double> pair2travelTime;
	
	public HashMap<String, String> productCode2type;
	public HashMap<String, Double> productCode2grssWeight;
	public ArrayList<String> productTypeSet;
	public ArrayList<String> customerLocSet;
	public HashMap<String, Double> customerProductType2weight;
	
	public HashMap<String, Double> cusLocationId2limitedWeight;
	public HashMap<String, String> cusLocationId2startTime;
	public HashMap<String, String> cusLocationId2endTime;
	public HashMap<String, Double> cusLocationId2unloadPerUnit;
	public HashMap<String, Double> cusLocationId2waitingDuration;
	
	public HashMap<String, ArrayList<Point>> depotLocationId2points;
	
	public HashMap<String, Double> vhCode2upperCapacity;
	public HashMap<String, Double> vhCode2lowerCapacity;
	public HashMap<String, String[]> vhCode2typeOfProduct;
	public HashMap<String, Integer> vhCode2nbTrips;
	public HashMap<String, Integer> vhCode2ownership;
	public HashMap<String, String> vhCode2locationId;
	public HashMap<String, Double> vhCode2weight;
	public HashMap<String, Vehicle> vhCode2Vehicle;
	public HashMap<Point, String> startPoint2vhCode;
	public HashMap<Point, Integer> point2ArrivalTime;
	public HashMap<Point, Integer> point2DepartureTime;
	
	public ArrayList<Integer> sortedRoutes;
	public double max_capacity;
	
	public static int nVehicle;
	public static int nRequest;
	double profit = 0;
	
	
	VRManager mgr;
	VarRoutesVR XR;
	ArcWeightsManager awm;
	Constraints ctrs;
	IFunctionVR objective;
	LexMultiValues valueSolution;
	AdaptRoutes adR;
	public static double MAX_TRAVELTIME;
	
	public HashMap<String, ArrayList<Point>> depotLoc2customerPoints;
	
	HashMap<Point, Integer> nChosed;
	HashMap<Point, Boolean> removeAllowed;
	
	private int nRemovalOperators = 8;
	private int nInsertionOperators = 8;
	
	//parameters
	public int lower_removal = 1;
	public int upper_removal = 5;
	public int sigma1 = 10;
	public int sigma2 = 0;
	public int sigma3 = -10;
	public double rp = 0.1;
	public int nw = 1;
	public double shaw1st = 0.5;
	public double shaw2nd = 0.2;
	public double shaw3rd = 0.1;
	public double temperature = 200;
	public double cooling_rate = 0.9995;
	public int nTabu = 1;
	int timeLimit;
	int nIter;
	int maxStable = 1000;

	HashMap<String, Double> d_fromParking;
	HashMap<String, Double> d_toParking;
	HashMap<String, Double> savingList;
	
	ArrayList<Point> onePointMoveList;
	ArrayList<Point> twoPointMoveList;
	ArrayList<Point> threePointMoveList;
	ArrayList<Point> twoOptMoveList;
	ArrayList<Point> orOptMoveList;
	ArrayList<Point> threeOptMoveList;
	ArrayList<Point> crossExchangeList;
	String dir;
	String rate = "40";
	public double rateint;
	
	public MDMTPSolver() {
		
	}
	
	public void init() {
		this.nVehicle = input.getVehicle().length;
		allPoints = new ArrayList<Point>();
		locationId2Point = new HashMap<String, Point>();
		
		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
		serviceDuration = new HashMap<Point, Double>();
		waittingDuration = new HashMap<Point, Integer>();
		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
		
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		depotPoints = new ArrayList<Point>();
		customerPoints = new ArrayList<Point>();
		rejectedPoints = new ArrayList<Point>();
		point2mark = new HashMap<Point, Integer>();
		
		parking2startTime = new HashMap<String, String>();
		parking2endTime = new HashMap<String, String>();
		for(int i = 0; i < input.getParking().length; i++){
			Parking prk = input.getParking()[i];
			parking2startTime.put(prk.getLocationId(), prk.getStartWorkingTime());
			parking2endTime.put(prk.getLocationId(), prk.getEndWorkingTime());
		}
		
		int id = 0;
		id2locationId = new HashMap<Integer, String>();
		id2point = new HashMap<Integer, Point>();
		vhCode2upperCapacity = new HashMap<String, Double>();
		vhCode2lowerCapacity = new HashMap<String, Double>();
		vhCode2typeOfProduct = new HashMap<String, String[]>();//vehicle cannot carry these products
		vhCode2nbTrips = new HashMap<String, Integer>();
		vhCode2ownership = new HashMap<String, Integer>();
		vhCode2locationId = new HashMap<String, String>();
		vhCode2weight = new HashMap<String, Double>();
		vhCode2Vehicle = new HashMap<String, Vehicle>();
		startPoint2vhCode = new HashMap<Point, String>();

		for(int i = 0; i < input.getVehicle().length; i++) {
			Vehicle vh = input.getVehicle()[i];
			String vhCode = vh.getVehicleCode();
			vhCode2Vehicle.put(vhCode, vh);
			vhCode2upperCapacity.put(vhCode, vh.getUpperLoadRate()*vh.getWeight());
			vhCode2lowerCapacity.put(vhCode, (double)(rateint*vh.getWeight()));
			vhCode2typeOfProduct.put(vhCode, vh.getRestrictedProducts());
			vhCode2nbTrips.put(vhCode, vh.getNbTrips());
			vhCode2ownership.put(vhCode, vh.getOwnership());
			vhCode2locationId.put(vhCode, vh.getLocaionId());
			vhCode2weight.put(vhCode, vh.getWeight());
			
			Point p = new Point(id, vh.getLocaionId(),
					Utils.PARKING, "", 0, Utils.INF);
			startPoints.add(p);
			startPoint2vhCode.put(p, vhCode);
			allPoints.add(p);
			locationId2Point.put(p.getLocationId(), p);
			id2locationId.put(id, p.locationId);
			id2point.put(id, p);
			
			earliestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(parking2startTime.get(vh.getLocaionId())));
			serviceDuration.put(p, 0.0);
			waittingDuration.put(p, 0);
			lastestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(parking2endTime.get(vh.getLocaionId())));
			id++;
			
			Point q = new Point(id, vh.getLocaionId(),
					Utils.PARKING, "", 0, Utils.INF);
			endPoints.add(q);
			allPoints.add(q);
			locationId2Point.put(q.getLocationId(), q);
			id2locationId.put(id, q.locationId);
			id2point.put(id, q);
			
			earliestAllowedArrivalTime.put(q, 
					(int)DateTimeUtils.dateTime2Int(parking2startTime.get(vh.getLocaionId())));
			serviceDuration.put(q, 0.0);
			waittingDuration.put(q, 0);
			lastestAllowedArrivalTime.put(q, 
					(int)DateTimeUtils.dateTime2Int(parking2endTime.get(vh.getLocaionId())));
			id++;
		}
		
		depotLocationId2points = new HashMap<String, ArrayList<Point>>();
		for(int i = 0; i < input.getDistributionCenter().length; i++) {
			DistributionCenter depot = input.getDistributionCenter()[i];
			ArrayList<Point> depotLogicalPoints = new ArrayList<Point>();
			for(int j = 0; j < nVehicle; j++) {
				for(int k = 0; k < input.getVehicle()[j].getNbTrips(); k++) {
					Point p = new Point(id, depot.getLocationId(),
							Utils.DEPOT, "", 0, Utils.INF);
					depotLogicalPoints.add(p);
					depotPoints.add(p);
					allPoints.add(p);
					locationId2Point.put(p.getLocationId(), p);
					id2locationId.put(id, p.locationId);
					id2point.put(id, p);
					
					earliestAllowedArrivalTime.put(p, 
							(int)DateTimeUtils.dateTime2Int(depot.getStartWorkingTime()));
					serviceDuration.put(p, 0.0);
					waittingDuration.put(p, (int)(depot.getWaittingDuration()));
					lastestAllowedArrivalTime.put(p, 
							(int)DateTimeUtils.dateTime2Int(depot.getEndWorkingTime()));
					id++;
				}
			}
			depotLocationId2points.put(depot.getLocationId(), depotLogicalPoints);
		}
		
		cusLocationId2limitedWeight = new HashMap<String, Double>();
		cusLocationId2startTime = new HashMap<String, String>();
		cusLocationId2endTime = new HashMap<String, String>();
		cusLocationId2unloadPerUnit = new HashMap<String, Double>();
		cusLocationId2waitingDuration = new HashMap<String, Double>();
		for(int i = 0; i < input.getCustomer().length; i++) {
			Customer c = input.getCustomer()[i];
			cusLocationId2startTime.put(c.getLocationId(), c.getStartWorkingTime());
			cusLocationId2endTime.put(c.getLocationId(), c.getEndWorkingTime());
			cusLocationId2limitedWeight.put(c.getLocationId(), c.getLimitedWeight());
			cusLocationId2unloadPerUnit.put(c.getLocationId(), c.getUnloadDurationPerUnit());
			cusLocationId2waitingDuration.put(c.getLocationId(), c.getWaittingDuration());
		}
		
		productCode2type = new HashMap<String, String>();
		productCode2grssWeight = new HashMap<String, Double>();
		for(int i = 0; i < input.getProduct().length; i++) {
			Product p = input.getProduct()[i];
			double w = p.getGrssWeight();
			String s = w + "";
			if(s.equals("NaN"))
				w = 0.0001;
			productCode2grssWeight.put(p.getProductCode(), p.getGrssWeight());
			productCode2type.put(p.getProductCode(), p.getType());
		}
		
		customerProductType2weight = new HashMap<String, Double>();
		productTypeSet = new ArrayList<String>();
		customerLocSet = new ArrayList<String>();
		
		for(int i = 0; i < input.getOrder().length; i++){
			Order ord = input.getOrder()[i];
			String cusLocationId = ord.getShiptoCode();
			String productType = productCode2type.get(ord.getOrderItem());
			String key = cusLocationId + "-" + productType;
			
			if(customerProductType2weight.get(key) == null) {
				customerProductType2weight.put(key, 
					ord.getQuantity()*productCode2grssWeight.get(ord.getOrderItem()));
				customerLocSet.add(cusLocationId);
				if(!productTypeSet.contains(productType))
					productTypeSet.add(productType);
			}
			else {
				double w = customerProductType2weight.get(key);
				w += ord.getQuantity() * productCode2grssWeight.get(ord.getOrderItem());
				customerProductType2weight.put(key, w);
			}
		}
		
//		try{
//			FileOutputStream write = new FileOutputStream("data\\vinamilk\\summary-orders-VNM-HCM-orders-2019-09-21.txt");
//			PrintWriter fo = new PrintWriter(write);
//			fo.println("CustomerLocationId  ProductType  Weight");
//			
//			fo.close();
//		}catch(Exception e){
//			System.out.println(e);
//		}

		for(String key : customerProductType2weight.keySet()) {
			String[] str = key.split("-");
			Point p = new Point(id, str[0], 
					Utils.CUSTOMER, str[1], 
					customerProductType2weight.get(key), 
					cusLocationId2limitedWeight.get(str[0]));
			customerPoints.add(p);
			allPoints.add(p);
			locationId2Point.put(p.getLocationId(), p);
			id2locationId.put(id, p.locationId);
			id2point.put(id, p);
			
			earliestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(cusLocationId2startTime.get(str[0])));
			serviceDuration.put(p, customerProductType2weight.get(key)*cusLocationId2unloadPerUnit.get(str[0]));
			waittingDuration.put(p, (int)(cusLocationId2waitingDuration.get(str[0])*1));
			lastestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(cusLocationId2endTime.get(str[0])));
			id++;
			
//			try{
//				FileOutputStream write = new FileOutputStream("data\\vinamilk\\summary-orders-VNM-HCM-orders-2019-09-21.txt", true);
//				PrintWriter fo = new PrintWriter(write);
//				fo.println(str[0] + " " + str[1] + " " + customerProductType2weight.get(key));
//				
//				fo.close();
//			}catch(Exception e){
//				System.out.println(e);
//			}
		}
		
		pair2travelTime = new HashMap<String, Double>();
		for(int i = 0; i < input.getDistance().length; i++) {
			Distance e = input.getDistance()[i];
			String key = e.getFrom() + "-" + e.getTo();
			pair2travelTime.put(key, e.getT());
		}
		
		awm = new ArcWeightsManager(allPoints);
		matrixT = new double[allPoints.size()][allPoints.size()];
		double max_time = Double.MIN_VALUE;
		for(int i = 0; i < allPoints.size(); i++) {
			point2mark.put(allPoints.get(i), 0);
			for(int j = 0; j < allPoints.size(); j++) {
				String fromLocation = id2locationId.get(i);
				String toLocation = id2locationId.get(j);
				String pair = fromLocation + "-" + toLocation;
				double w = waittingDuration.get(allPoints.get(j));
				double t = pair2travelTime.get(pair);
				if(!fromLocation.equals(toLocation))
					t += w;
				matrixT[i][j] = t;
				if(max_time < t)
					max_time = t;
				awm.setWeight(allPoints.get(i), allPoints.get(j), t);
			}
		}
		MAX_TRAVELTIME = max_time;
	}
	
	public void initForCreateMIPFile() {
		this.nVehicle = input.getVehicle().length;
		allPoints = new ArrayList<Point>();
		locationId2Point = new HashMap<String, Point>();
		
		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
		serviceDuration = new HashMap<Point, Double>();
		waittingDuration = new HashMap<Point, Integer>();
		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
		
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		depotPoints = new ArrayList<Point>();
		customerPoints = new ArrayList<Point>();
		rejectedPoints = new ArrayList<Point>();
		point2mark = new HashMap<Point, Integer>();
		
		parking2startTime = new HashMap<String, String>();
		parking2endTime = new HashMap<String, String>();
		for(int i = 0; i < input.getParking().length; i++){
			if(i > 1)//sua so parking
				break;
			Parking prk = input.getParking()[i];
			parking2startTime.put(prk.getLocationId(), prk.getStartWorkingTime());
			parking2endTime.put(prk.getLocationId(), prk.getEndWorkingTime());
		}
		
		int id = 0;
		id2locationId = new HashMap<Integer, String>();
		id2point = new HashMap<Integer, Point>();
		vhCode2upperCapacity = new HashMap<String, Double>();
		vhCode2lowerCapacity = new HashMap<String, Double>();
		vhCode2typeOfProduct = new HashMap<String, String[]>();//vehicle cannot carry these products
		vhCode2nbTrips = new HashMap<String, Integer>();
		vhCode2ownership = new HashMap<String, Integer>();
		vhCode2locationId = new HashMap<String, String>();
		vhCode2weight = new HashMap<String, Double>();
		startPoint2vhCode = new HashMap<Point, String>();

		for(int i = 0; i < input.getVehicle().length; i++) {
//			if(i > 1)
//				break;
			Vehicle vh = input.getVehicle()[i];
			String vhCode = vh.getVehicleCode();
			vhCode2upperCapacity.put(vhCode, vh.getUpperLoadRate()*vh.getWeight());
			vhCode2lowerCapacity.put(vhCode, vh.getLowerLoadRate()*vh.getWeight());
			vhCode2typeOfProduct.put(vhCode, vh.getRestrictedProducts());
			vhCode2nbTrips.put(vhCode, vh.getNbTrips());
			vhCode2ownership.put(vhCode, vh.getOwnership());
			vhCode2locationId.put(vhCode, vh.getLocaionId());
			vhCode2weight.put(vhCode, vh.getWeight());
			
			Point p = new Point(id, vh.getLocaionId(),
					Utils.PARKING, "", 0, Utils.INF);
			startPoints.add(p);
			startPoint2vhCode.put(p, vhCode);
			allPoints.add(p);
			locationId2Point.put(p.getLocationId(), p);
			id2locationId.put(id, p.locationId);
			id2point.put(id, p);
			
			earliestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(parking2startTime.get(vh.getLocaionId())));
			serviceDuration.put(p, 0.0);
			waittingDuration.put(p, 0);
			lastestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(parking2endTime.get(vh.getLocaionId())));
			id++;
			
			Point q = new Point(id, vh.getLocaionId(),
					Utils.PARKING, "", 0, Utils.INF);
			endPoints.add(q);
			allPoints.add(q);
			locationId2Point.put(q.getLocationId(), q);
			id2locationId.put(id, q.locationId);
			id2point.put(id, q);
			
			earliestAllowedArrivalTime.put(q, 
					(int)DateTimeUtils.dateTime2Int(parking2startTime.get(vh.getLocaionId())));
			serviceDuration.put(q, 0.0);
			waittingDuration.put(q, 0);
			lastestAllowedArrivalTime.put(q, 
					(int)DateTimeUtils.dateTime2Int(parking2endTime.get(vh.getLocaionId())));
			id++;
		}
		
		depotLocationId2points = new HashMap<String, ArrayList<Point>>();
		for(int i = 0; i < input.getDistributionCenter().length; i++) {
			if(i > 1)//sua so depot
				break;
			DistributionCenter depot = input.getDistributionCenter()[i];
			ArrayList<Point> depotLogicalPoints = new ArrayList<Point>();
			for(int j = 0; j < nVehicle; j++) {
				for(int k = 0; k < input.getVehicle()[j].getNbTrips(); k++) {
					Point p = new Point(id, depot.getLocationId(),
							Utils.DEPOT, "", 0, Utils.INF);
					depotLogicalPoints.add(p);
					depotPoints.add(p);
					allPoints.add(p);
					locationId2Point.put(p.getLocationId(), p);
					id2locationId.put(id, p.locationId);
					id2point.put(id, p);
					
					earliestAllowedArrivalTime.put(p, 
							(int)DateTimeUtils.dateTime2Int(depot.getStartWorkingTime()));
					serviceDuration.put(p, depot.getLoadDurationPerUnit());
					waittingDuration.put(p, (int)(depot.getWaittingDuration()));
					lastestAllowedArrivalTime.put(p, 
							(int)DateTimeUtils.dateTime2Int(depot.getEndWorkingTime()));
					id++;
				}
			}
			depotLocationId2points.put(depot.getLocationId(), depotLogicalPoints);
		}
		
		cusLocationId2limitedWeight = new HashMap<String, Double>();
		cusLocationId2startTime = new HashMap<String, String>();
		cusLocationId2endTime = new HashMap<String, String>();
		cusLocationId2unloadPerUnit = new HashMap<String, Double>();
		cusLocationId2waitingDuration = new HashMap<String, Double>();
		for(int i = 0; i < input.getCustomer().length; i++) {
			Customer c = input.getCustomer()[i];
			cusLocationId2startTime.put(c.getLocationId(), c.getStartWorkingTime());
			cusLocationId2endTime.put(c.getLocationId(), c.getEndWorkingTime());
			cusLocationId2limitedWeight.put(c.getLocationId(), c.getLimitedWeight());
			cusLocationId2unloadPerUnit.put(c.getLocationId(), c.getUnloadDurationPerUnit());
			cusLocationId2waitingDuration.put(c.getLocationId(), c.getWaittingDuration());
		}
		
		productCode2type = new HashMap<String, String>();
		productCode2grssWeight = new HashMap<String, Double>();
		for(int i = 0; i < input.getProduct().length; i++) {
			Product p = input.getProduct()[i];
			double w = p.getGrssWeight();
			String s = w + "";
			if(s.equals("NaN"))
				w = 0.0001;
			productCode2grssWeight.put(p.getProductCode(), p.getGrssWeight());
			productCode2type.put(p.getProductCode(), p.getType());
		}
		
		customerProductType2weight = new HashMap<String, Double>();
		productTypeSet = new ArrayList<String>();
		customerLocSet = new ArrayList<String>();
		
		for(int i = 0; i < input.getOrder().length; i++){
			Order ord = input.getOrder()[i];
			String cusLocationId = ord.getShiptoCode();
			String productType = productCode2type.get(ord.getOrderItem());
			String key = cusLocationId + "-" + productType;
			
			if(customerProductType2weight.get(key) == null) {
				if(customerLocSet.size() > 49)//sua so khach hang
					break;
				customerProductType2weight.put(key, 
					ord.getQuantity()*productCode2grssWeight.get(ord.getOrderItem()));
				customerLocSet.add(cusLocationId);
				if(!productTypeSet.contains(productType))
					productTypeSet.add(productType);
			}
			else {
				double w = customerProductType2weight.get(key);
				w += ord.getQuantity() * productCode2grssWeight.get(ord.getOrderItem());
				customerProductType2weight.put(key, w);
			}
		}
		
//		try{
//			FileOutputStream write = new FileOutputStream("data\\vinamilk\\summary-orders-VNM-HCM-orders-2019-09-21.txt");
//			PrintWriter fo = new PrintWriter(write);
//			fo.println("CustomerLocationId  ProductType  Weight");
//			
//			fo.close();
//		}catch(Exception e){
//			System.out.println(e);
//		}
		
		for(String key : customerProductType2weight.keySet()) {
			String[] str = key.split("-");
			Point p = new Point(id, str[0], 
					Utils.CUSTOMER, str[1], 
					customerProductType2weight.get(key), 
					cusLocationId2limitedWeight.get(str[0]));
			customerPoints.add(p);
			allPoints.add(p);
			locationId2Point.put(p.getLocationId(), p);
			id2locationId.put(id, p.locationId);
			id2point.put(id, p);
			
			earliestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(cusLocationId2startTime.get(str[0])));
			serviceDuration.put(p, customerProductType2weight.get(key)*cusLocationId2unloadPerUnit.get(str[0]));
			waittingDuration.put(p, (int)(cusLocationId2waitingDuration.get(str[0])*1));
			lastestAllowedArrivalTime.put(p, 
					(int)DateTimeUtils.dateTime2Int(cusLocationId2endTime.get(str[0])));
			id++;
			
//			try{
//				FileOutputStream write = new FileOutputStream("data\\vinamilk\\summary-orders-VNM-HCM-orders-2019-09-21.txt", true);
//				PrintWriter fo = new PrintWriter(write);
//				fo.println(str[0] + " " + str[1] + " " + customerProductType2weight.get(key));
//				
//				fo.close();
//			}catch(Exception e){
//				System.out.println(e);
//			}
		}
		
		pair2travelTime = new HashMap<String, Double>();
		for(int i = 0; i < input.getDistance().length; i++) {
			Distance e = input.getDistance()[i];
			String key = e.getFrom() + "-" + e.getTo();
			pair2travelTime.put(key, e.getT());
		}
		
		awm = new ArcWeightsManager(allPoints);
		matrixT = new double[allPoints.size()][allPoints.size()];
		double max_time = Double.MIN_VALUE;
		for(int i = 0; i < allPoints.size(); i++) {
			point2mark.put(allPoints.get(i), 0);
			for(int j = 0; j < allPoints.size(); j++) {
				String fromLocation = id2locationId.get(i);
				String toLocation = id2locationId.get(j);
				String pair = fromLocation + "-" + toLocation;
				double w = waittingDuration.get(allPoints.get(i));
				double t = pair2travelTime.get(pair);
				if(!fromLocation.equals(toLocation))
					t += w;
				matrixT[i][j] = t;
				if(max_time < t)
					max_time = t;
				awm.setWeight(allPoints.get(i), allPoints.get(j), t);
			}
		}
		MAX_TRAVELTIME = max_time;
	}
	
	public void stateModel() {
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		
		ctrs = new Constraints(earliestAllowedArrivalTime, serviceDuration, 
				waittingDuration, lastestAllowedArrivalTime, 
				matrixT, vhCode2upperCapacity, vhCode2lowerCapacity,vhCode2weight, 
				vhCode2typeOfProduct, vhCode2nbTrips, startPoint2vhCode,
				cusLocationId2limitedWeight, mgr, XR);
		adR = new AdaptRoutes(this);
		for(int i = 0; i < startPoints.size(); ++i)
			XR.addRoute(startPoints.get(i), endPoints.get(i));
		
		for(int i = 0; i < depotPoints.size(); ++i)
			XR.addClientPoint(depotPoints.get(i));
		for(int i = 0; i < customerPoints.size(); ++i)
			XR.addClientPoint(customerPoints.get(i));
		objective = new TotalCostVR(XR, awm, vhCode2lowerCapacity, 
				vhCode2upperCapacity, startPoint2vhCode);
		
		sortedRoutes = sortVehiclesByLowerLoadRate(); 
		
		mgr.close();
		
	}
	
	//lay diem gan nhat tu danh sach point di den point x
//	public Point getNearestParkingToPoint(Point x, ArrayList<Point> points) {
//		double d = Utils.INF;
//		Point nearestPoint = null;
//		ArrayList<String> checkedLocationId = new ArrayList<String>();
//		for(int i = 0; i < points.size(); i++) {
//			Point p = points.get(i);
//			if(nearestPoint != null && checkedLocationId.contains(p.getLocationId()))
//				continue;
//			if(matrixT[p.getID()][x.getID()] < d) {
//				d = matrixT[p.getID()][x.getID()];
//				nearestPoint = p;
//			}
//			checkedLocationId.add(p.getLocationId());
//		}
//		return nearestPoint;
//	}
	
	//lay diem gan nhat trong set locationList di den point x
	public String getNearestParkingToPoint(Point x, Set<String> locationList) {
		double min_d = Double.MIN_VALUE;
		String nearestLoc = "";
		for(String loc : locationList) {
			String key = loc + "-" + x.getLocationId();
			double d = pair2travelTime.get(key);
			if(min_d < d) {
				min_d = d;
				nearestLoc = loc;
			}
		}
		return nearestLoc;
	}
	
	//lay diem gan nhat tu point x di den trong danh sach points
	public Point getNearestPointToParking(Point x, ArrayList<Point> points) {
		double d = Utils.INF;
		Point nearestPoint = null;
		ArrayList<String> checkedLocationId = new ArrayList<String>();
		for(int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			if(nearestPoint != null && checkedLocationId.contains(p.getLocationId()))
				continue;
			if(matrixT[x.getID()][p.getID()] < d) {
				d = matrixT[x.getID()][p.getID()];
				nearestPoint = p;
			}
			checkedLocationId.add(p.getLocationId());
		}
		return nearestPoint;
	}
	
	public String getNearestPointToParking(Point x, Set<String> locationList) {
		double min_d = Double.MIN_VALUE;
		String nearestLoc = "";
		for(String loc : locationList) {
			String key = x.getLocationId() + "-" + loc;
			double d = pair2travelTime.get(key);
			if(min_d < d) {
				min_d = d;
				nearestLoc = loc;
			}
		}
		return nearestLoc;
	}
	
	public Point getNearestAvailablePointFromPoint(Point x, ArrayList<Point> points, ArrayList<Point> retrictedPoints) {
		double min_d = Double.MAX_VALUE;
		Point nearestPoint = null;
		ArrayList<String> checkedLocationId = new ArrayList<String>();
		for(int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			if((nearestPoint != null && checkedLocationId.contains(p.getLocationId()))
				|| XR.route(p) != Constants.NULL_POINT
//				|| point2mark.get(p) == 1
				|| retrictedPoints.contains(p))
				continue;
			double d = matrixT[x.getID()][p.getID()] + matrixT[p.getID()][XR.next(x).getID()];
			if(d < min_d) {
				min_d = d;
				nearestPoint = p;
			}
			checkedLocationId.add(p.getLocationId());
		}
		if(nearestPoint == null) {
			System.out.println("cannot find the depot!");
			System.exit(-1);
		}
		return nearestPoint;
	}
	
	//lay customer point xa nhat tu depot dp den do. thoa man cac rang buoc de chen vao xe r
	public Point getNearestPointFromDepot(Point dp, Point st, String vhCode, int r) {
		double max_d = Double.MAX_VALUE;
		Point max_p = null;
		for(int i = 0; i < customerPoints.size(); i++) {
			Point p = customerPoints.get(i);
			if(XR.route(p) == Constants.NULL_POINT) {
				if(ctrs.customerCanVisitedByVehicle(vhCode, p.getLocationId()) == false
						|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false
						|| ctrs.customerCanVisitedByVehicle(vhCode, p.getLocationId()) == false
						|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false)
						continue;
				mgr.performAddOnePoint(p, st);
				addDepotToOneRoute(r);
				if(!ctrs.timeWindowConstraint(r)
					|| !ctrs.upperCapacityConstraints(r)) {
					mgr.performRemoveOnePoint(p);
					removeDepotPointFromRoute(r);
					continue;
				}
				mgr.performRemoveOnePoint(p);
				removeDepotPointFromRoute(r);
				
				double d = matrixT[dp.getID()][p.getID()];
				if(d < max_d) {
					max_d = d;
					max_p = p;
				}
			}
		}
		return max_p;
	}
	
	public int addPointHaveSmallestAngleToRoute(Point dp, Point st, String vhCode, int r, Point zero) {
		double min_angle = Double.MAX_VALUE;
		Point min_p = null;
		Point pre_min_p = null;
		for(int i = 0; i < customerPoints.size(); i++) {
			Point p = customerPoints.get(i);
			if(XR.route(p) == Constants.NULL_POINT) {
				if(ctrs.customerCanVisitedByVehicle(vhCode, p.getLocationId()) == false
						|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false
						|| ctrs.customerCanVisitedByVehicle(vhCode, p.getLocationId()) == false
						|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false)
						continue;
				double min_d = Double.MAX_VALUE;
				for(Point q = st; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)) {
					mgr.performAddOnePoint(p, q);
					addDepotToOneRoute(r);
					if(ctrs.timeWindowConstraint(r)
						&& ctrs.upperCapacityConstraints(r)) {
						double AB = matrixT[dp.getID()][zero.getID()];
						double AC = matrixT[dp.getID()][p.getID()];
						double BC = matrixT[zero.getID()][p.getID()];
						double angle = Math.toDegrees(Math.acos((AB*AB + AC*AC - BC*BC)/(2*AB*AC)));
						
//						double CD = matrixT[p.getID()][dp.getID()];
//						double DC = matrixT[dp.getID()][p.getID()];
//						double CB = matrixT[p.getID()][zero.getID()];
//						double BA = matrixT[zero.getID()][dp.getID()];
//						double min = AB + BC + CD < DC + CB + BA ? AB + BC + CD : DC + CB + BA;
//						double angle = min - AB - BA;
						if(angle < min_angle) {
							min_angle = angle;
							min_p = p;
							if(objective.getValue() < min_d) {
								min_d = objective.getValue();
								pre_min_p = q;
							}
						}
					}
					mgr.performRemoveOnePoint(p);
					removeDepotPointFromRoute(r);
				}
			}
		}
		if(min_p != null && pre_min_p != null) {
			mgr.performAddOnePoint(min_p, pre_min_p);
			point2mark.put(min_p, 1);
			return 1;
		}
		return 0;
	}
	
	
	//clustering customer points to depots with small travel time
	//sap xep cac diem thuoc ve moi depot theo thu tu giam dan khoang cach
	public void customerClusters() {
		depotLoc2customerPoints = new HashMap<String, ArrayList<Point>>();
		for(int i = 0; i < customerPoints.size(); i++) {
			Point nearestPoint = getNearestAvailablePointFromPoint(customerPoints.get(i), depotPoints, new ArrayList<Point>());
			if(depotLoc2customerPoints.get(nearestPoint.getLocationId()) == null) {
				ArrayList<Point> points = new ArrayList<Point>();
				points.add(customerPoints.get(i));
				depotLoc2customerPoints.put(nearestPoint.getLocationId(), points);
			}
			else {
				ArrayList<Point> points = depotLoc2customerPoints.get(nearestPoint.getLocationId());
				points.add(customerPoints.get(i));
				depotLoc2customerPoints.put(nearestPoint.getLocationId(), points);
			}
		}
		
		//sort by distance from point to depot
		for(String depotLoc : depotLoc2customerPoints.keySet()) {
			ArrayList<Point> c = depotLoc2customerPoints.get(depotLoc);
			ArrayList<Point> newCustomerPoints = new ArrayList<Point>();
			int n = c.size();
			for(int i = 0; i < n; i++) {
				double d = -1;
				Point max = null;
				for(Point p : c) {
					Point dp = locationId2Point.get(depotLoc);
					if(matrixT[p.getID()][dp.getID()] > d) {
						d = matrixT[p.getID()][dp.getID()];
						max = p;
					}
				}
				newCustomerPoints.add(max);
				c.remove(max);
			}
			depotLoc2customerPoints.put(depotLoc, newCustomerPoints);
		}
		
		
//		//sort by weight
//		for(String depotLoc : depotLoc2customerPoints.keySet()) {
//			ArrayList<Point> c = depotLoc2customerPoints.get(depotLoc);
//			ArrayList<Point> newCustomerPoints = new ArrayList<Point>();
//			int n = c.size();
//			for(int i = 0; i < n; i++) {
//				double w = -1;
//				Point max = null;
//				for(Point p : c) {
//					if(p.getOrderWeight() > w) {
//						w = p.getOrderWeight();
//						max = p;
//					}
//				}
//				newCustomerPoints.add(max);
//				c.remove(max);
//			}
//			depotLoc2customerPoints.put(depotLoc, newCustomerPoints);
//		}
	}
	
	
	//sap xep danh sach khach hang theo thu tu khoang cach gan den xa so voi moi depot.
	//moi depot se co danh sach tat ca cac khach hang nhung da duoc sap xep theo thu tu khoang cach
	//duong tron dong tam
	public void customerClusters2() {
		depotLoc2customerPoints = new HashMap<String, ArrayList<Point>>();
		
		for(int i = 0; i < depotPoints.size(); i++) {
			Point dp = depotPoints.get(i);
			if(depotLoc2customerPoints.get(dp.getLocationId()) != null)
				continue;
			ArrayList<Point> temp_customers = new ArrayList<Point>(customerPoints);
			ArrayList<Point> newCustomers = new ArrayList<Point>();
			while(newCustomers.size() < customerPoints.size()) {
				double min_d = Double.MAX_VALUE;
				Point min_p = null;
				for(int j = 0; j < temp_customers.size(); j++) {
					Point p = temp_customers.get(j);
					if(matrixT[dp.getID()][p.getID()] < min_d) {
						min_d = matrixT[dp.getID()][p.getID()];
						min_p = p;
					}
				}
				temp_customers.remove(min_p);
				newCustomers.add(min_p);
			}
			depotLoc2customerPoints.put(dp.getLocationId(), newCustomers);
		}
	}
	
	public void addDepotToOneRoute(int r) {
		double acmWeight = 0;
		Point st = XR.getStartingPointOfRoute(r);
		String vhCode = startPoint2vhCode.get(st);
		if(XR.next(st) == XR.getTerminatingPointOfRoute(r))
			return;
		if(!XR.next(st).getTypeOfPoint().equals(Utils.DEPOT)) {
			Point dpAdded = getNearestAvailablePointFromPoint(st, depotPoints, new ArrayList<Point>());
			mgr.performAddOnePoint(dpAdded, st);
			point2mark.put(dpAdded, 1);
		}
		int nbDepots = 0;
		Point pre_x = st;
		for(Point x = XR.next(st); x != XR.getTerminatingPointOfRoute(r); x = XR.next(x)) {
			if(acmWeight + x.getOrderWeight() > vhCode2upperCapacity.get(vhCode)) {
				Point dpAdded = getNearestAvailablePointFromPoint(pre_x, depotPoints, new ArrayList<Point>());
				point2mark.put(dpAdded, 1);
				mgr.performAddOnePoint(dpAdded, pre_x);
				acmWeight = 0;
			}
			else
				acmWeight += x.getOrderWeight();
			if(x.getTypeOfPoint().equals(Utils.DEPOT) 
					|| x.getTypeOfPoint().equals(Utils.PARKING)) {
				acmWeight = 0;
				nbDepots++;
				if(nbDepots+1 > vhCode2nbTrips.get(vhCode))
					return;
			}
			pre_x = x;
		}
	}
	
	//get the nearest depot point from point x and add it into right-after point x
	public void addDepotToAllRoutes() {
		for(int r = 1; r <= XR.getNbRoutes(); r++) {
			addDepotToOneRoute(r);
		}
	}
	

	
	public ArrayList<Integer> sortVehiclesByLowerLoadRate() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int nbRoutes = XR.getNbRoutes();
		ArrayList<Integer> routes = new ArrayList<Integer>();
		for(int r = 1; r <= nbRoutes; r++)
			routes.add(r);
		while(result.size() < nbRoutes) {
			double minWeight = Double.MAX_VALUE;
			int mr = -1;
			for(int idx = 0; idx < routes.size(); idx++) {
				Point st = XR.getStartingPointOfRoute(routes.get(idx));
				String vhCode = startPoint2vhCode.get(st);
				double w = vhCode2lowerCapacity.get(vhCode);
				if(w < minWeight) {
					minWeight = w;
					mr = idx;
				}
			}
			result.add(routes.get(mr));
			routes.remove(mr);
		}
		Point st = XR.getStartingPointOfRoute(result.get(result.size()-1));
		String vhCode = startPoint2vhCode.get(st);
		max_capacity = vhCode2upperCapacity.get(vhCode);
		return result;
	}
	
	public int getBestVehicleForExchange(int oldRoute, ArrayList<Integer> sortedRoutes,
			ArrayList<Integer> marks) {
		int r_old = sortedRoutes.get(oldRoute);
		Point st = XR.getStartingPointOfRoute(r_old);
		
		for(int i = oldRoute+1; i < sortedRoutes.size(); i++) {
			if(marks.get(i) == 1)
				continue;
			boolean isBreak = false;
			int r_new = sortedRoutes.get(i);
			Point newSt = XR.getStartingPointOfRoute(r_new);
			String vhCode = startPoint2vhCode.get(newSt);
			for(Point p = XR.next(st); p != XR.getTerminatingPointOfRoute(r_old); p = XR.next(p)) {
				if(ctrs.customerCanVisitedByVehicle(vhCode, p.getLocationId()) == false
					|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false) {
					isBreak = true;
					break;
				}
			}
			if(isBreak == false)
				return i;
		}
		return -1;
	}
	
	public void moveToBetterVehicle(int oldRoute, int newRoute) {
		Point st = XR.getStartingPointOfRoute(oldRoute);
		Point newSt = XR.getStartingPointOfRoute(newRoute);
		Point prev = st;
		for(Point p = XR.next(st); p != XR.getTerminatingPointOfRoute(oldRoute); p = XR.next(p)) {
			prev = XR.prev(p);
			mgr.performRemoveOnePoint(p);
			double cost = Double.MAX_VALUE;
			Point addedPosPoint = null;
			for(Point q = newSt; q != XR.getTerminatingPointOfRoute(newRoute); q = XR.next(q)) {
				mgr.performAddOnePoint(p, q);
				if(objective.getValue() < cost) {
					cost = objective.getValue();
					addedPosPoint = q;
				}
				mgr.performRemoveOnePoint(p);
			}
			mgr.performAddOnePoint(p, addedPosPoint);
			addDepotToOneRoute(newRoute);
			if(ctrs.timeWindowConstraint(newRoute) == false
				|| ctrs.upperCapacityConstraints(newRoute) == false) {
				mgr.performRemoveOnePoint(p);
				mgr.performAddOnePoint(p, prev);
			}
			else
				p = prev;
			removeDepotPointFromRoute(newRoute);
		}
	}
	
	public int getUnmarkedVehicle(ArrayList<Integer> marks) {
		for(int i = 0; i < marks.size(); i++)
			if(marks.get(i) == 0)
				return i;
		return -1;
	}
	
	public void calculateDistanceCustomerDepot(){
		d_fromParking = new HashMap<String, Double>();
		d_toParking = new HashMap<String, Double>();
		for(String loc1 : parking2startTime.keySet()) {
			for(int j = 0; j < customerPoints.size(); j++) {
				Point c = customerPoints.get(j);
				double minDfromParking = Double.MAX_VALUE;//kc tu depot den c
				String nearestLoc_f = getNearestParkingToPoint(c, parking2startTime.keySet());//parking gan nhat di den c
				double minDtoParking= Double.MAX_VALUE;//kc tu c quay ve depot
				String nearestLoc_t = getNearestPointToParking(c, parking2endTime.keySet());//parking gan nhat tu c quay ve
				for(String loc2 : parking2startTime.keySet()) {

					String k1 = loc1 + "-" + c.getLocationId();
					String k2 = loc2 + "-" + c.getLocationId();
					String k3 = nearestLoc_f + "-" + c.getLocationId();
					double d1 = pair2travelTime.get(k2)
							- pair2travelTime.get(k1)
							+ pair2travelTime.get(k3);
					
					k1 = c.getLocationId() + "-" + loc1;
					k2 = c.getLocationId() + "-" + loc2;
					k3 = c.getLocationId() + "-" + nearestLoc_t;

					double d2 = pair2travelTime.get(k2)
							- pair2travelTime.get(k1)
							+ pair2travelTime.get(k3);
					if(d1 < minDfromParking) {
						minDfromParking = d1;
					}
					if(d2 < minDtoParking) {
						minDtoParking = d2;
					}
				}

				String str = loc1 + "-" + c.getID();
				d_fromParking.put(str, minDfromParking);
				str = c.getID() + "-" + loc1;
				d_toParking.put(str, minDtoParking);
			}
		}
	}
	
	public HashMap<String, Double> sortSavingList(HashMap<String, Double> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Double> > list = 
               new LinkedList<Map.Entry<String, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
            public int compare(Map.Entry<String, Double> o1,  
                               Map.Entry<String, Double> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>(); 
        for (Map.Entry<String, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
	
	public void calculateSavingList(){
		calculateDistanceCustomerDepot();
		savingList = new HashMap<String, Double>();
		
		for(String loc : parking2startTime.keySet()) {
			for(int j = 0; j < customerPoints.size(); j++) {
				for(int k = 0; k < customerPoints.size(); k++) {
					if(j == k)
						continue;
					String ijk = loc + "-" 
						+ j + "-" + k;
					String ik = loc + "-" + customerPoints.get(k).getID();
					String ji = customerPoints.get(j).getID() + "-" + loc;
					double Sijk = d_fromParking.get(ik) + d_toParking.get(ji)
							- matrixT[customerPoints.get(j).getID()][customerPoints.get(k).getID()];
					savingList.put(ijk, Sijk);
				}
			}
		}
		savingList = sortSavingList(savingList);
	}
	
	public void greedyInitSolutionSavingAlgorithmNoExchangeRoute() {
		System.out.println("greedyInitSolutionSavingAlgorithm");
		calculateSavingList();

		ArrayList<Integer> marks = new ArrayList<Integer>();
		for(int i = 0; i < sortedRoutes.size(); i++)
			marks.add(0);

		int tt = 0;
		int nn = savingList.size();
		for (Map.Entry<String, Double> en : savingList.entrySet()) {
			System.out.println(tt + "/" + nn);
			tt++;
			String[] ijk = en.getKey().split("-");
			int j = Integer.parseInt(ijk[1]);
			int k = Integer.parseInt(ijk[2]);

			Point pj = customerPoints.get(j);
			Point pk = customerPoints.get(k);
			
			if(XR.route(pj) == Constants.NULL_POINT
				&& XR.route(pk) == Constants.NULL_POINT) {
				for(int i = 0; i < sortedRoutes.size(); i++) {
					int r = sortedRoutes.get(i);
					Point st = XR.getStartingPointOfRoute(r);
					if(!st.getLocationId().equals(ijk[0]))
						continue;
					String vhCode = startPoint2vhCode.get(st);
					if(ctrs.customerCanVisitedByVehicle(vhCode, pj.getLocationId()) == false
						|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, pj.getTypeOfProduct()) == false
						|| ctrs.customerCanVisitedByVehicle(vhCode, pk.getLocationId()) == false
						|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, pk.getTypeOfProduct()) == false)
							continue;
					
					Point p = XR.prev(XR.getTerminatingPointOfRoute(r));
					mgr.performAddOnePoint(pj, p);
					mgr.performAddOnePoint(pk, pj);
					addDepotToOneRoute(r);
					if(ctrs.timeWindowConstraint(r) == false
						|| ctrs.upperCapacityConstraints(r) == false) {
						mgr.performRemoveOnePoint(pj);
						mgr.performRemoveOnePoint(pk);
						removeDepotPointFromRoute(r);
					}
					else {
						point2mark.put(pj, 1);
						point2mark.put(pk, 1);
						removeDepotPointFromRoute(r);
						break;
					}
				}
			}
			else if(XR.route(pj) != Constants.NULL_POINT
				&& XR.route(pk) == Constants.NULL_POINT) {
				int r = XR.route(pj);
				Point st = XR.getStartingPointOfRoute(r);
				if(!st.getLocationId().equals(ijk[0])
					|| XR.next(pj) != XR.getTerminatingPointOfRoute(r))
					continue;
				mgr.performAddOnePoint(pk, pj);
				addDepotToOneRoute(r);
				if(ctrs.timeWindowConstraint(r) == false
					|| ctrs.upperCapacityConstraints(r) == false) {
					mgr.performRemoveOnePoint(pk);
					removeDepotPointFromRoute(r);
				}
				else {
					point2mark.put(pk, 1);
					removeDepotPointFromRoute(r);
				}
			}
			else if(XR.route(pj) == Constants.NULL_POINT
					&& XR.route(pk) != Constants.NULL_POINT) {
				int r = XR.route(pk);
				Point st = XR.getStartingPointOfRoute(r);
				if(!st.getLocationId().equals(ijk[0])
					|| XR.prev(pk) != st)
					continue;
				mgr.performAddOnePoint(pj, st);
				addDepotToOneRoute(r);
				if(ctrs.timeWindowConstraint(r) == false
					|| ctrs.upperCapacityConstraints(r) == false) {
					mgr.performRemoveOnePoint(pj);
					removeDepotPointFromRoute(r);
				}
				else {
					point2mark.put(pj, 1);
					removeDepotPointFromRoute(r);
				}
			}
		}
		
		System.out.println("insert done:  " + getNbServedCustomers());
		
		removeViolationPoints();
		System.out.println("remove done:  " + getNbServedCustomers());
		calculateArrivalTimeAtEachPoint();
		//removeDepotPointAllRoutes();
		rejectedPoints.clear();
		for(int i = 0; i < customerPoints.size(); i++){
			Point pickup = customerPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectedPoints.contains(pickup)){
				rejectedPoints.add(pickup);
				point2mark.put(pickup, 0);
			}
		}
	}
	
	public void greedyInitSolutionRouteFirstClusterSecond() {
		//customers are clusted by distance from depot
		customerClusters2();
		
		for(int idx = 0; idx < sortedRoutes.size(); idx++) {
			int r = sortedRoutes.get(idx);
			Point st = XR.getStartingPointOfRoute(r);
			String vhCode = startPoint2vhCode.get(st);
			Point dp = getNearestAvailablePointFromPoint(st, depotPoints, new ArrayList<Point>());
			ArrayList<Point> cluster = depotLoc2customerPoints.get(dp.getLocationId());
			for(int i = 0; i < cluster.size(); i++) {
				Point c = cluster.get(i);
				if(XR.route(c) != Constants.NULL_POINT)
					continue;
				if(ctrs.customerCanVisitedByVehicle(vhCode, c.getLocationId()) == false
					|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, c.getTypeOfProduct()) == false
					|| ctrs.customerCanVisitedByVehicle(vhCode, c.getLocationId()) == false
					|| ctrs.typeOfProductsCanCarriedByVehicle(vhCode, c.getTypeOfProduct()) == false)
					continue;
				
				Point bestP = null;
				double bestObj = Utils.INF;
				for(Point p = st; p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)) {
					mgr.performAddOnePoint(c, p);
					addDepotToOneRoute(r);
					if(ctrs.timeWindowConstraint(r)
						&& ctrs.upperCapacityConstraints(r)) {
						if(objective.getValue() < bestObj) {
							bestP = p;
							bestObj = objective.getValue();
						}
					}
					mgr.performRemoveOnePoint(c);
					removeDepotPointFromRoute(r);
				}
				if(bestP != null) {
					mgr.performAddOnePoint(c, bestP);
					point2mark.put(c, 1);
				}
			}
		}
		
		System.out.println("insert done");
		
		removeViolationPoints();
		calculateArrivalTimeAtEachPoint();
		//removeDepotPointAllRoutes();
		rejectedPoints.clear();
		for(int i = 0; i < customerPoints.size(); i++){
			Point pickup = customerPoints.get(i);
			if(XR.route(pickup) == Constants.NULL_POINT && !rejectedPoints.contains(pickup)){
				rejectedPoints.add(pickup);
				point2mark.put(pickup, 0);
			}
		}
	}
	
	public Point getPointDebug(int id) {
		for(int i = 0; i < allPoints.size(); i++)
			if(allPoints.get(i).getID() == id)
				return allPoints.get(i);
		return null;
	}
	
	public Point getPointByLocationId(String locationId) {
		for(int i = 0; i < allPoints.size(); i++)
			if(allPoints.get(i).getLocationId().equals(locationId))
				return allPoints.get(i);
		return null;
	}
	
	
	public Point getWorsePointinRoute(int r, Point cur_p) {
		Point rs = null;
		double max_d = Double.MIN_VALUE;
		for(Point p = XR.next(XR.getStartingPointOfRoute(r)); p != cur_p; p = XR.next(p)) {
			double d = matrixT[XR.prev(p).getID()][p.getID()] + matrixT[p.getID()][XR.next(p).getID()];
			if(d > max_d) {
				max_d = d;
				rs = p;
			}
		}
		return rs;
	}
	
	public void removeViolationPoints() {
		int nbRemove = 0;
		for(int r = 1; r <= XR.getNbRoutes(); r++) {
			addDepotToOneRoute(r);
			HashMap<Point, Integer> loadWeights = new HashMap<Point, Integer>();
			int acmLoadWeight = 0;
			Point depot = null;
			Point st = XR.getStartingPointOfRoute(r);
			for(Point p = XR.next(st); p != null; p = XR.next(p)) {
				acmLoadWeight += p.getOrderWeight();
				if(p.getTypeOfPoint().equals(Utils.DEPOT)) {
					acmLoadWeight -= serviceDuration.get(p);
					if(depot == null)
						depot = p;
					else {
						loadWeights.put(depot, acmLoadWeight);
						depot = p;
						acmLoadWeight = 0;
					}
				}
				else if(p.getTypeOfPoint().equals(Utils.PARKING)) {
					if(depot != null)
						loadWeights.put(depot, acmLoadWeight);
				}
			}
			double startServiceTime = earliestAllowedArrivalTime.get(st);
			for(Point p = XR.next(st); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)) {
				startServiceTime += matrixT[XR.prev(p).getID()][p.getID()];
				if(startServiceTime > lastestAllowedArrivalTime.get(p)
					) {
					Point q = XR.prev(p);
					startServiceTime -= matrixT[XR.prev(p).getID()][p.getID()];
					mgr.performRemoveOnePoint(p);
					p=q;
				}
				startServiceTime += 0;//waittingDuration.get(p);
				startServiceTime += serviceDuration.get(p);
				if(p.getTypeOfPoint().equals(Utils.DEPOT))
					startServiceTime += loadWeights.get(p);
			}
			//removeDepotPointFromRoute(r);
		}
		
		//System.out.println("remove");
		for(int r = 1; r <= XR.getNbRoutes(); r++) {
			addDepotToOneRoute(r);
			Point st = XR.getStartingPointOfRoute(r);
			String vhCode = startPoint2vhCode.get(st);
			double acmWeight = 0;
			ArrayList<Point> removedPoints = new ArrayList<Point>();
//			removedPoints.add(XR.next(st));
			Point pre_p = st;
			for(Point p = XR.next(st);
					p != null; p = XR.next(p)) {
				acmWeight += p.getOrderWeight();
				if(p.getTypeOfPoint().equals(Utils.DEPOT) 
						|| p.getTypeOfPoint().equals(Utils.PARKING)) {
					if(acmWeight < vhCode2lowerCapacity.get(vhCode)
						&& acmWeight > 0) {
//						System.out.println("acmWeight = " + acmWeight + ", lower = " + vhCode2lowerCapacity.get(vhCode) + ", upper = " + vhCode2upperCapacity.get(vhCode));
//						System.out.println("arrival time = " + point2arrivalTime.get(p) + ", latest = " + lastestAllowedArrivalTime.get(p));
						mgr.performRemoveOnePoint(pre_p);
						point2mark.put(pre_p, 0);
						acmWeight = 0;
						removedPoints.clear();
						removeDepotPointFromRoute(r);
						nbRemove++;
						r--;
						break;
//						for(Point rp : removedPoints) {
//							mgr.performRemoveOnePoint(rp);
//							point2mark.put(rp, 0);
//							if(rp.getTypeOfPoint().equals(Utils.CUSTOMER))
//								rejectedPoints.add(rp);
//						}
					}
				}
				pre_p = p;
				removedPoints.add(p);
			}
		}
		if(nbRemove != 0) {
			try{
				String outputFile = dir + "E21-lowerbound-" + rate + "-ALNS.txt";
				FileOutputStream write = new FileOutputStream(outputFile, true);
				PrintWriter fo = new PrintWriter(write);
				fo.println(nbRemove);
				fo.close();                                                                   
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
	
	public void calculateArrivalTimeAtEachPoint() {
		point2arrivalTime = new HashMap<Point, Integer>();
		for(int r = 1; r <= XR.getNbRoutes(); r++) {
			HashMap<Point, Integer> loadDurations = new HashMap<Point, Integer>();
			int acmLoadDuration = 0;
			Point depot = null;
			Point st = XR.getStartingPointOfRoute(r);
			for(Point p = st; p != null; p = XR.next(p)) {
				acmLoadDuration += serviceDuration.get(p);
				if(p.getTypeOfPoint().equals(Utils.DEPOT)) {
					acmLoadDuration -= serviceDuration.get(p);
					if(depot == null)
						depot = p;
					else {
						loadDurations.put(depot, acmLoadDuration);
						depot = p;
						acmLoadDuration = 0;
					}
				}
				else if(p.getTypeOfPoint().equals(Utils.PARKING)) {
					if(depot != null)
						loadDurations.put(depot, acmLoadDuration);
				}
			}
			int startServiceTime = earliestAllowedArrivalTime.get(st);
			point2arrivalTime.put(st, startServiceTime);
			for(Point p = XR.next(st); p != null; p = XR.next(p)) {
				startServiceTime += matrixT[XR.prev(p).ID][p.ID];
//				if(startServiceTime < earliestAllowedArrivalTime.get(p))
//					startServiceTime = earliestAllowedArrivalTime.get(p);
				startServiceTime += 0;//waittingDuration.get(p);
				startServiceTime += serviceDuration.get(p);
				if(p.getTypeOfPoint().equals(Utils.DEPOT))
					startServiceTime += loadDurations.get(p);
				point2arrivalTime.put(p, startServiceTime);
			}
		}
	}
	
	public int getNbUsedVehicles() {
		int nb = 0;
		for(int r = 1; r <= XR.getNbRoutes(); r++)
			for(Point p = XR.next(XR.getStartingPointOfRoute(r)); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p))
				if(p.getTypeOfPoint().equals(Utils.CUSTOMER)) {
					nb++;
					break;
				}
		return nb;
	}

	
	public void printSolution(double t0, String outputFile) {
		calculateArrivalTimeAtEachPoint();
		
		//calculateProfit();
		try{	
			FileOutputStream write = new FileOutputStream(outputFile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println("======Searching done======");
			fo.println("ALNS-RunTime-Objective-NbServedCustomers-NbUsedVehicles");
			fo.println((System.currentTimeMillis() - t0)/1000
					+ "-" + totalDistance()
					+ "-" + getNbServedCustomers()
					+ "-" + getNbUsedVehicles());
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		int K = XR.getNbRoutes();
		String s = "";
		for(int k=1; k<=K; k++){
			double acmWeight = 0;
			Point x = XR.getStartingPointOfRoute(k);
			if(XR.next(x) == XR.getTerminatingPointOfRoute(k))
				continue;
			s += "route[" + k + "] = ";
			for(; x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
				acmWeight += x.getOrderWeight();
				s = s + x.getLocationId() + "(" + x.getTypeOfPoint() + "-" + acmWeight 
					+ "-" + point2arrivalTime.get(x) + " -> ";
				if(x.getTypeOfPoint().equals(Utils.DEPOT))
					acmWeight = 0;
			}
			x = XR.getTerminatingPointOfRoute(k);
			s = s + x.getLocationId() + "(" + x.getTypeOfPoint() + "-" + acmWeight 
					+ "-" + point2arrivalTime.get(x) + "\n";
			
		}		
		System.out.println(s);
		try{
			
			FileOutputStream write = new FileOutputStream(outputFile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(s);
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void removeDepotPointFromRoute(int r) {
		for(Point p = XR.getStartingPointOfRoute(r); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)) {
			if(p.getTypeOfPoint().equals(Utils.DEPOT)) {
				Point preP = XR.prev(p);
				mgr.performRemoveOnePoint(p);
				point2mark.put(p, 0);
				p = preP;
			}
		}
	}
	
	public void removeDepotPointAllRoutes() {
		for(int r = 1; r <= XR.getNbRoutes(); r++)
			removeDepotPointFromRoute(r);
	}
	
	
	public void ALNSsearch(String outputFile) {
		initParamsForALNS();
		
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
    	
		double best_cost = totalDistance();
		int best_nbUsedVehicles = getNbUsedVehicles();
		int best_nbServedReqs = getNbServedCustomers();

		MDMTPsolution best_solution = new MDMTPsolution(XR, point2arrivalTime, best_cost, 
				best_nbUsedVehicles, best_nbServedReqs, rejectedPoints);

		double start_search_time = System.currentTimeMillis();
		try{
			
			FileOutputStream write = new FileOutputStream(outputFile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println("time limit = " + timeLimit + ", nbIters = " + nIter + ", maxStable = " + maxStable);
			fo.println("#Request = " + nRequest);
			fo.println("iter=====insertion=====removal=====time=====cost=====nbServedReqs=====nbVehicles");
			fo.println("0 -1 -1 " + " " + System.currentTimeMillis() + " " 
			+ best_cost + " " + best_nbServedReqs + " " + best_nbUsedVehicles);
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}

		while( (System.currentTimeMillis()-start_search_time) < timeLimit && it++ < nIter){
			System.out.println("nb of iterator: " + it);
			double current_cost = totalDistance();
			int current_nbTrucks = getNbUsedVehicles();
			int current_nbServedReqs = getNbServedCustomers();
			MDMTPsolution current_solution = new MDMTPsolution(XR, point2arrivalTime, 
					current_cost, current_nbTrucks,
					current_nbServedReqs, rejectedPoints);
			
			removeDepotPointAllRoutes();
			
			int i_selected_removal = -1;
			if(iS >= maxStable){
				adR.allRemoval();
				iS = 0;
//				if(it > 200)
//					insertDebug();
			}
			else{
				i_selected_removal = get_operator(ptd);
				//i_selected_removal = idxRemoval;
				wd[i_selected_removal]++;
				switch(i_selected_removal){
					case 0: adR.routeRemovalOperator(); break;
					case 1: adR.randomRequestRemovalOperator(); break;
					case 2: adR.shawRemovalOperator(); break;
					case 3: adR.worstRemovalOperator(); break;
					case 4: adR.forbidden_removal(0); break;
					case 5: adR.forbidden_removal(1); break;
					case 6: adR.forbidden_removal(2); break;
					case 7: adR.forbidden_removal(3); break;
				}
			}
			
			
			int i_selected_insertion = get_operator(pti);
			System.out.println(i_selected_insertion);
			//int i_selected_insertion =idxRemoval ;
			wi[i_selected_insertion]++;
			switch(i_selected_insertion){
				case 0: adR.greedyInitSolutionProposed(); break;
				case 1: adR.greedyInitSolutionProposedWithNoise(); break;
				case 2: adR.bestRateCapacityInsertion(); break;
				case 3: adR.first_possible_insertion(); break;
				case 4: adR.sort_before_insertion(0); break;
				case 5: adR.sort_before_insertion(1); break;
				case 6: adR.sort_before_insertion(2); break;
				case 7: adR.sort_before_insertion(3); break;
			}
			System.out.println("1");
			addDepotToAllRoutes();
			System.out.println("2");
			removeViolationPoints();
			System.out.println("3");
			calculateArrivalTimeAtEachPoint();
			System.out.println("4");
			int new_nb_served_points = getNbServedCustomers();
			double new_cost = totalDistance();

			int new_nbTrucks = getNbUsedVehicles();

			if( new_nb_served_points > current_nbServedReqs
					|| (new_nb_served_points == current_nbServedReqs && new_nbTrucks < current_nbTrucks)
				|| (new_nb_served_points == current_nbServedReqs && new_nbTrucks == current_nbTrucks
						&& new_cost < current_cost)){
				best_nbServedReqs = best_solution.get_nbServedReqs();
				best_nbUsedVehicles = best_solution.get_nbVehicles();
				best_cost = best_solution.get_cost();
				
				if(new_nb_served_points > best_nbServedReqs
						|| (new_nb_served_points == best_nbServedReqs && new_nbTrucks < best_nbUsedVehicles)
						|| (new_nb_served_points == best_nbServedReqs && new_nbTrucks == best_nbUsedVehicles
								&& new_cost < best_cost)){
					best_solution = new MDMTPsolution(XR, point2arrivalTime, new_cost, 
							new_nbTrucks, new_nb_served_points, rejectedPoints);
					try{
						FileOutputStream write = new FileOutputStream(outputFile, true);
						PrintWriter fo = new PrintWriter(write);
						fo.println(it + " " + i_selected_insertion 
							+ " " + i_selected_removal + " "
							+ System.currentTimeMillis() + " "
							+ new_cost + " " + new_nb_served_points + " " + new_nbTrucks);
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
					calculateArrivalTimeAtEachPoint();
					rejectedPoints = current_solution.get_rejectPoints();
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
		calculateArrivalTimeAtEachPoint();
		rejectedPoints = best_solution.get_rejectPoints();
		try{
			FileOutputStream write = new FileOutputStream(outputFile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(it + " -1 -1 "
					+ System.currentTimeMillis() + " "
					+ best_cost + " " + getNbServedCustomers() + " " + getNbUsedVehicles());
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public int getNbServedCustomers() {
		int nb = 0;
		for(int i = 0; i < customerPoints.size(); i++) {
			if(XR.route(customerPoints.get(i)) != Constants.NULL_POINT)
				nb++;
		}
		return nb;
	}
	
	public void initParamsForALNS(){
//		lower_removal = (int)(customerPoints.size() * 0.2);
//		upper_removal = (int)(customerPoints.size() * 0.4);
		nChosed = new HashMap<Point, Integer>();
		removeAllowed = new HashMap<Point, Boolean>();
		for(int i=0; i<customerPoints.size(); i++){
			Point pi = customerPoints.get(i);
			nChosed.put(pi, 0);
			removeAllowed.put(pi, true);
		}
	}
	
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
	
	public void createFileToMIP(String dir) {
		String fileName = dir + "-2parkings-2depots-3vehicles-50customers-MILP.txt";
		ArrayList<String> locOrders = new ArrayList<String>();
		try {
			PrintWriter f = new PrintWriter(new File(fileName));
			f.println("#nbCustomers");
			f.println(customerLocSet.size());
			
			f.println("#nbParkings");
			f.println(parking2startTime.size());
			
			f.println("#nbCentralDepots");
			f.println(depotLocationId2points.size());
			
			HashMap<String, ArrayList<String>> prk2vhCodes = new HashMap<String, ArrayList<String>>();
			ArrayList<String> order2prkCode = new ArrayList<String>();
			int nbVhs = 0;
			int id = 0;
			for(String prkCode : parking2startTime.keySet()) {
				ArrayList<String> vhCodes = new ArrayList<String>();
				for(String vh : vhCode2locationId.keySet()) {
					if(vhCode2locationId.get(vh).equals(prkCode))
						vhCodes.add(vh);
				}
				List<String> newVhCodes;
				if(id == 0)
					newVhCodes = vhCodes.subList(0, 2);//sua so xe
				else
					newVhCodes = vhCodes.subList(0, 1);//sua so xe
				id++;
				vhCodes = new ArrayList<String>(newVhCodes);
				prk2vhCodes.put(prkCode, vhCodes);
				order2prkCode.add(prkCode);
				nbVhs += vhCodes.size();
			}
			
			f.println("#nbVehicles");
			f.println(nbVhs);
			
			f.println("#nbProducts");
			f.println(productTypeSet.size());
			
			f.println("#parking info (nk ep lp)");
			for(String prkCode : parking2startTime.keySet()) {
				ArrayList<String> vhCodes = prk2vhCodes.get(prkCode);
				f.println(vhCodes.size() + " " + (int)DateTimeUtils.dateTime2Int(parking2startTime.get(prkCode)) 
					+ " " + (int)DateTimeUtils.dateTime2Int(parking2endTime.get(prkCode)));
				
				locOrders.add(prkCode);
			}

			f.println("#central depot info(ed, ld, waittingTime, loadingTimeperUnit)");
			for(String dp : depotLocationId2points.keySet()) {
				ArrayList<Point> depotPoints = depotLocationId2points.get(dp);
				f.println(earliestAllowedArrivalTime.get(depotPoints.get(0))
					+ " " + lastestAllowedArrivalTime.get(depotPoints.get(0))
					+ " " + waittingDuration.get(depotPoints.get(0))
					+ " " + serviceDuration.get(depotPoints.get(0)));
				locOrders.add(dp);
			}
			f.println("#vehicle info(ek, lk, ck lower, ck upper, qk, costRate)");
			for(int i = 0; i < order2prkCode.size(); i++) {
				ArrayList<String> vhCodes = prk2vhCodes.get(order2prkCode.get(i));
				for(int j = 0; j < vhCodes.size(); j++) {
					f.println((int)DateTimeUtils.dateTime2Int(parking2startTime.get(vhCode2locationId.get(vhCodes.get(j))))
						+ " " + (int)DateTimeUtils.dateTime2Int(parking2endTime.get(vhCode2locationId.get(vhCodes.get(j))))
						+ " " + Math.round(vhCode2lowerCapacity.get(vhCodes.get(j))*0.6/0.75)
						+ " " + Math.round(vhCode2upperCapacity.get(vhCodes.get(j)))
						+ " " + "2"//vhCode2nbTrips.get(vhCodes.get(j))//sua so trip
						+ " 1");
						//+ " " + vhCode2ownership.get(vhCodes.get(j)));
				}
			}
			
			f.println("#weight of products (wp)");
			for(int i = 0; i < productTypeSet.size(); i++)
				f.println("1");
			
			f.println("#customer demand (quantity, quantity, quantity,... nbProducts)");
			for(int i = 0; i < customerLocSet.size(); i++) {
				String s = "";
				for(int j = 0; j < productTypeSet.size() - 1; j++) {
					String key = customerLocSet.get(i) + "-" + productTypeSet.get(j);
					if(customerProductType2weight.get(key) != null)
						s += Math.round(customerProductType2weight.get(key)) + " ";
					else
						s += "0 ";
				}
				String key = customerLocSet.get(i) + "-" + productTypeSet.get(productTypeSet.size() - 1);
				if(customerProductType2weight.get(key) != null)
					s += Math.round(customerProductType2weight.get(key));
				else
					s += "0";
				f.println(s);
			}
			
			f.println("#customer info(ei, li, waittingTime, unloadingTimePerUnit)");
			for(int i = 0; i < customerLocSet.size(); i++) {
				String cusLoc = customerLocSet.get(i);
				f.println((int)DateTimeUtils.dateTime2Int(cusLocationId2startTime.get(cusLoc))
					+ " " + (int)DateTimeUtils.dateTime2Int(cusLocationId2endTime.get(cusLoc))
					+ " " + Math.round(cusLocationId2waitingDuration.get(cusLoc))
					+ " " + cusLocationId2unloadPerUnit.get(cusLoc));
				locOrders.add(cusLoc);
			}
			
			f.println("#vehicle - product (restrictly bkp) = 1: vh can carry");
			for(int i = 0; i < order2prkCode.size(); i++) {
				ArrayList<String> vhCodes = prk2vhCodes.get(order2prkCode.get(i));
				for(int k = 0; k < vhCodes.size(); k++) {
					String[] restrictedProduct = vhCode2typeOfProduct.get(vhCodes.get(k));
					String s = "";
					ArrayList<String> arr = new ArrayList<String>(Arrays.asList(restrictedProduct));
					for(int j = 0; j < productTypeSet.size() - 1; j++) {
//						String prType= productTypeSet.get(j);
//						if(arr.contains(prType))
//							s += "0 ";
//						else
							s+= "1 ";
					}
					String prType= productTypeSet.get(productTypeSet.size()-1);
//					if(arr.contains(prType))
//						s += "0";
//					else
						s+= "1";
					f.println(s);
				}
			}
			f.println("#vehicles - customer (restrictly rki) = 1: vh can go to cus");
			for(int i = 0; i < order2prkCode.size(); i++) {
				ArrayList<String> vhCodes = prk2vhCodes.get(order2prkCode.get(i));
				for(int k = 0; k < vhCodes.size(); k++) {
					String s = "";
					for(int j = 0; j < customerLocSet.size() - 1; j++) {
						s += "1 ";
					}
					s += "1";
					f.println(s);
				}
			}
			
			f.println("#vehicle - remain customers = 1: remain");
			for(int i = 0; i < order2prkCode.size(); i++) {
				ArrayList<String> vhCodes = prk2vhCodes.get(order2prkCode.get(i));
				for(int k = 0; k < vhCodes.size(); k++) {
					String s = "";
					for(int j = 0; j < customerLocSet.size() - 1; j++) {
						s += "0 ";
					}
					s += "0";
					f.println(s);
				}
			}
			
			f.println("#travel time matrix:[from to travelTime]");
			f.println(locOrders.size() * (locOrders.size()));
			for(int i = 0; i < locOrders.size(); i++)
				for(int j = 0; j < locOrders.size(); j++) {
					String key = locOrders.get(i) + "-" + locOrders.get(j);
					if(pair2travelTime.get(key) != null)
						f.println(i + " " + j + " " + Math.round(pair2travelTime.get(key)));
					else
						f.println(i + " " + j + " 0");
				}
			f.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void computeArrivalTimeandDepartureTime(int r) {
		point2ArrivalTime = new HashMap<Point, Integer>();
		point2DepartureTime = new HashMap<Point, Integer>();
		HashMap<Point, Integer> loadWeights = new HashMap<Point, Integer>();
		int acmLoadWeight = 0;
		Point depot = null;
		Point st = XR.getStartingPointOfRoute(r);
		for(Point p = XR.next(st); p != null; p = XR.next(p)) {
			acmLoadWeight += p.getOrderWeight();
			if(p.getTypeOfPoint().equals(Utils.DEPOT)) {
				acmLoadWeight -= serviceDuration.get(p);
				if(depot == null)
					depot = p;
				else {
					loadWeights.put(depot, acmLoadWeight);
					depot = p;
					acmLoadWeight = 0;
				}
			}
			else if(p.getTypeOfPoint().equals(Utils.PARKING)) {
				if(depot != null)
					loadWeights.put(depot, acmLoadWeight);
			}
		}
		int startServiceTime = earliestAllowedArrivalTime.get(st);
		point2ArrivalTime.put(st, startServiceTime);
		point2DepartureTime.put(st, startServiceTime);
		for(Point p = XR.next(st); p != null; p = XR.next(p)) {
			startServiceTime += matrixT[XR.prev(p).getID()][p.getID()];
			point2ArrivalTime.put(p, startServiceTime);

			startServiceTime += 0;//waittingDuration.get(p);
			startServiceTime += serviceDuration.get(p);
			if(p.getTypeOfPoint().equals(Utils.DEPOT))
				startServiceTime += loadWeights.get(p);
			point2DepartureTime.put(p, startServiceTime);
		}
	}
	
	//chay tuning parameters
//	public static void main(String[] args) {
//		Gson g = new Gson();
//		double[] lowerRemovals = {0.1, 0.2};
//		double[] upperRemovals = {0.3, 0.4};
//		int[] sig1_list = {3, 5, 10};
//		int[] sig2_list = {0, 1};
//		int[] sig3_list = {-1, -3, -10};
//		
//		for(int lb = 0; lb < lowerRemovals.length; lb++){
//			for(int ub = 0; ub < upperRemovals.length; ub++){
//				for(int s1 = 0; s1 < sig1_list.length; s1++){
//					for(int s2 = 0; s2 < sig2_list.length; s2++){
//						for(int s3 = 0; s3 < sig3_list.length; s3++){
//							MDMTPSolver solver = new MDMTPSolver();
//							String dir = "data\\vinamilk\\";
//							String fileName = "VNM-HCM-orders-2019-09-21";
//							String jsonInFileName = dir + fileName + ".json";
//							try{
//								BufferedReader in = new BufferedReader(new InputStreamReader(
//						                new FileInputStream(jsonInFileName), "UTF8"));
//								solver.input = g.fromJson(in, MDMTPInput.class);
//								
//								solver.init();
//								
//								//init parameters for alns
//								solver.lower_removal = (int)(solver.customerPoints.size() * lowerRemovals[lb]);
//								solver.upper_removal = (int)(solver.customerPoints.size() * upperRemovals[ub]);
//								solver.sigma1 = sig1_list[s1];
//								solver.sigma2 = sig2_list[s2];
//								solver.sigma3 = sig3_list[s3];
//								
//								String outputFile = dir + "output\\tunningParameters\\"
//										+ fileName + "_" + solver.lower_removal
//										+ "_" + solver.upper_removal 
//										+ "_" + solver.sigma1
//										+ "_" + solver.sigma2
//										+ "_" + solver.sigma3 + ".txt";
//								try{
//									FileOutputStream write = new FileOutputStream(outputFile);
//									PrintWriter fo = new PrintWriter(write);
//									fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) 
//											+ ", total reqs = " + solver.customerPoints.size()
//											+ ", total vehicles = " + solver.nVehicle);
//									
//									fo.close();
//								}catch(Exception e){
//									System.out.println(e);
//								}
//								
//								System.out.println("====Create Model====");
//								solver.stateModel();
//								
//								System.out.println("====Greedy Init Solution====");
//								double t0 = System.currentTimeMillis();
//								solver.greedyInitSolution3();
//								try{
//									
//									FileOutputStream write = new FileOutputStream(outputFile, true);
//									PrintWriter fo = new PrintWriter(write);
//									fo.println("Greedy init solution");
//									fo.println("Init-RunTime-Objective-NbServedCustomers-NbUsedVehicles");
//									fo.println((System.currentTimeMillis() - t0)/1000 
//											+ "-" + solver.objective.getValue()
//											+ "-" + solver.getNbServedCustomers()
//											+ "-" + solver.getNbUsedVehicles());
//									fo.close();
//								}catch(Exception e){
//									System.out.println(e);
//								}
//								//solver.initParamsForALNS();
//								//solver.localSearch(outputFile, 10000, 180000);
//								solver.search(outputFile);
//								
//								solver.printSolution(t0, outputFile);
//								
//								System.out.println("Done!");
//							}catch(Exception e){
//								System.out.println(e);
//							}
//						}
//					}
//				}
//			}
//		}
//	}

	
	//thu local search de so sanh
//	public static void main(String[] args) {
//		MDMTPSolver solver = new MDMTPSolver();
//		Gson g = new Gson();
//		try{
//			String dir = "data\\vinamilk\\";
//			String dataFile = "VNM-HCM-orders-2019-09-21";
//			String jsonInFileName = dir + dataFile + ".json";
//			String outputFile = dir + dataFile + "-output.txt";
//			
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//	                new FileInputStream(jsonInFileName), "UTF8"));
//			solver.input = g.fromJson(in, MDMTPInput.class);
//			
//			solver.init();
////			solver.initForCreateMIPFile();
////			solver.createFileToMIP(dir + dataFile);
//			
//			try{
//				FileOutputStream write = new FileOutputStream(outputFile);
//				PrintWriter fo = new PrintWriter(write);
//				fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) 
//						+ ", total reqs = " + solver.customerPoints.size()
//						+ ", total vehicles = " + solver.nVehicle);
//				
//				fo.close();
//			}catch(Exception e){
//				System.out.println(e);
//			}
//			
//			System.out.println("====Create Model====");
//			solver.stateModel();
//			
//			System.out.println("====Greedy Init Solution====");
//			double t0 = System.currentTimeMillis();
//			solver.greedyInitSolutionRouteFirstClusterSecond();
//			System.out.println("Runtime = " + (System.currentTimeMillis() - t0)/1000 
//					+ ", objective = " + solver.objective.getValue()
//					+ ", nbServedRequests = " + solver.getNbServedCustomers()
//					+ ", nbUsedVehicles = " + solver.getNbUsedVehicles());
//			try{
//				
//				FileOutputStream write = new FileOutputStream(outputFile, true);
//				PrintWriter fo = new PrintWriter(write);
//				fo.println("Greedy init solution");
//				fo.println("RunTime-Objective-NbServedCustomers-NbUsedVehicles");
//				fo.println((System.currentTimeMillis() - t0)/1000 
//						+ "-" + solver.objective.getValue()
//						+ "-" + solver.getNbServedCustomers()
//						+ "-" + solver.getNbUsedVehicles());
//				fo.close();
//			}catch(Exception e){
//				System.out.println(e);
//			}
////			//solver.localSearch(outputFile, 10000, 180000);
//			solver.localSearch(outputFile);
//			
//			solver.printSolution(t0, outputFile);
////			
//			System.out.println("Runtime = " + (System.currentTimeMillis() - t0)/1000 
//					+ ", objective = " + solver.objective.getValue()
//					+ ", nbServedRequests = " + solver.getNbServedCustomers()
//					+ ", nbUsedVehicles = " + solver.getNbUsedVehicles());
//			System.out.println("Done!");
//			
//		}catch(Exception e){
//			System.out.println(e);
//		}
//	}
	
	public double totalDistance() {
		double d = 0;
		
		for(int r = 1; r <= XR.getNbRoutes(); r++) {			
			for(Point p = XR.getStartingPointOfRoute(r); p != XR.getTerminatingPointOfRoute(r); p = XR.next(p)) {
				String pair = p.getLocationId() + "-" + XR.next(p).getLocationId();
				d += pair2travelTime.get(pair);
			}
		}
		return d;
	}
	
	public MDMTPSolutionJson createFormatedSolution() {
		ArrayList<VehicleRoute> brArr = new ArrayList<VehicleRoute>();

		int nbVehicles = 0;
		for (int r = 1; r <= XR.getNbRoutes(); r++) {
			int nb = XR.index(XR.getTerminatingPointOfRoute(r)) + 1;
			Point st = XR.getStartingPointOfRoute(r);
			Point en = XR.getTerminatingPointOfRoute(r);
			Vehicle vehicle = vhCode2Vehicle.get(startPoint2vhCode.get(st));
			
			if(nb <= 2)
				continue;
			
			double d = 0;
			int nbPers = 0;
			
			
			int g = 0;
			RouteElement[] nodes = new RouteElement[nb];
			computeArrivalTimeandDepartureTime(r);
			for(Point p = st; p != en; p = XR.next(p)) {			

				nodes[g] = new RouteElement(p.getLocationCode(), p.getTypeOfPoint(),
						DateTimeUtils.unixTimeStamp2DateTime((long)(point2ArrivalTime.get(p))),
						DateTimeUtils.unixTimeStamp2DateTime((long)(point2DepartureTime.get(p))), 
						(int)awm.getWeight(p, XR.next(p)));
				g++;
			}
			
			

			nodes[g] = new RouteElement(en.getLocationCode(),
					en.getTypeOfPoint(),
					DateTimeUtils.unixTimeStamp2DateTime((long)point2ArrivalTime.get(en)),
					DateTimeUtils.unixTimeStamp2DateTime((long)(point2DepartureTime.get(en))), 0);
			
			VehicleRoute br = new VehicleRoute(vehicle, nb, (int)objective.getValue(), nodes);
			brArr.add(br);
			nbVehicles++;
		}
		
		VehicleRoute[] vehicleRoutes = new VehicleRoute[brArr.size()];
		for(int i = 0; i < brArr.size(); i++)
			vehicleRoutes[i] = brArr.get(i);
		
		
		int totalRejectReqs = customerPoints.size() - getNbServedCustomers();
		
		StatisticInformation statisticInformation = new StatisticInformation(
				customerPoints.size(),totalRejectReqs, objective.getValue(), nbVehicles);
		
		return new MDMTPSolutionJson(vehicleRoutes, statisticInformation);
	}
	
	//evaluate rate parameter
//	public static void main(String[] args) {
//		
//		ArrayList<Integer> rates = new ArrayList<Integer>();
////		rates.add(20);
////		rates.add(30);
//		rates.add(40);
//		rates.add(50);
//		rates.add(60);
//		rates.add(70);
//		rates.add(80);
//		rates.add(90);
//		for(int i = 0; i < rates.size(); i++) {
//			MDMTPSolver solver = new MDMTPSolver();
//			solver.rate = "" + rates.get(i);
//			double b = (double)(rates.get(i))/100;
//			solver.rateint = b;
//			Gson g = new Gson();
//			try{
//				solver.dir = "data\\vinamilk\\";
//				String dataFile = "VNM-HCM-orders-2019-09-21";
//				String jsonInFileName = solver.dir + dataFile + ".json";
//				String outputFile = solver.dir + dataFile + "-ALNS-output.txt";
//				
//				BufferedReader in = new BufferedReader(new InputStreamReader(
//		                new FileInputStream(jsonInFileName), "UTF8"));
//				solver.input = g.fromJson(in, MDMTPInput.class);
//				
//				solver.init();
//	//			solver.initForCreateMIPFile();
//	//			solver.createFileToMIP(dir + dataFile);
//				
//				try{
//					FileOutputStream write = new FileOutputStream(outputFile);
//					PrintWriter fo = new PrintWriter(write);
//					fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) 
//							+ ", total reqs = " + solver.customerPoints.size()
//							+ ", total vehicles = " + solver.nVehicle);
//					
//					fo.close();
//				}catch(Exception e){
//					System.out.println(e);
//				}
//				
//				try{
//					String outputFile2 = solver.dir + "E21-lowerbound-"+ solver.rate + "-ALNS.txt";
//					FileOutputStream write = new FileOutputStream(outputFile2);
//					PrintWriter fo = new PrintWriter(write);
//					fo.println("=======number of violation requests=========");
//					fo.close();
//				}catch(Exception e){
//					System.out.println(e);
//				}
//				
//				System.out.println("====Create Model====");
//				solver.stateModel();
//				
//				System.out.println("====RouteFirstClusterSecond====");
//				double t0 = System.currentTimeMillis();
//				solver.greedyInitSolutionRouteFirstClusterSecond();
//				System.out.println("Runtime = " + (System.currentTimeMillis() - t0)/1000 
//						+ ", objective = " + solver.totalDistance()
//						+ ", nbServedRequests = " + solver.getNbServedCustomers()
//						+ ", nbUsedVehicles = " + solver.getNbUsedVehicles());
//				try{
//					
//					FileOutputStream write = new FileOutputStream(outputFile);
//					PrintWriter fo = new PrintWriter(write);
//					fo.println("vSA");
//					fo.println("RunTime-Objective-NbServedCustomers-NbUsedVehicles");
//					fo.println((System.currentTimeMillis() - t0)/1000 
//							+ "-" + solver.totalDistance()
//							+ "-" + solver.getNbServedCustomers()
//							+ "-" + solver.getNbUsedVehicles());
//					fo.close();
//				}catch(Exception e){
//					System.out.println(e);
//				}
//	//			//solver.localSearch(outputFile, 10000, 180000);
//				solver.ALNSsearch(outputFile);
//				
//				double d = solver.totalDistance();
//				solver.printSolution(t0, outputFile);
//	//			
//				System.out.println("Runtime = " + (System.currentTimeMillis() - t0) 
//						+ ", objective = " + solver.totalDistance()
//						+ ", d = " + d
//						+ ", nbServedRequests = " + solver.getNbServedCustomers()
//						+ ", nbUsedVehicles = " + solver.getNbUsedVehicles());
//				System.out.println("Done!");
//				
//			}catch(Exception e){
//				System.out.println(e);
//			}
//		}
//	}
	
	//chay cac file du lieu 10 lan
	public static void main(String[] args) {
		Gson g = new Gson();
		MDMTPSolver solver = new MDMTPSolver();
		solver.timeLimit = 360000;//gioi han thoi gian chay
		solver.nIter = 10000;//gioi han so lan lap
		String dir = "F:\\Project\\OpenERP\\data\\VNM\\";
		String fileName = "VNM-HCM-orders-2019-09-21";
		String inputFile = dir + "input\\" + fileName + ".json";
		String outputFileTxT = dir + "output\\" + fileName + "-IT-0.txt";
		String outputFileJson = dir + "output\\" + fileName + "-IT-0.json";
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(
	                new FileInputStream(inputFile), "UTF8"));
			solver.input = g.fromJson(in, MDMTPInput.class);
			
			solver.init();
			
			//init parameters for alns
			solver.lower_removal = (int)(solver.customerPoints.size() * 0.2);
			solver.upper_removal = (int)(solver.customerPoints.size() * 0.3);
			solver.sigma1 = 10;
			solver.sigma2 = 0;
			solver.sigma3 = -10;
			
			try{
				FileOutputStream write = new FileOutputStream(outputFileTxT);
				PrintWriter fo = new PrintWriter(write);
				fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) 
						+ ", total reqs = " + solver.customerPoints.size()
						+ ", total vehicles = " + solver.nVehicle);
				
				fo.close();
			}catch(Exception e){
				System.out.println(e);
			}
			
			System.out.println("====Create Model====");
			solver.stateModel();
			
			System.out.println("====Greedy Init Solution====");
			double t0 = System.currentTimeMillis();
			solver.greedyInitSolutionRouteFirstClusterSecond();
			try{
				
				FileOutputStream write = new FileOutputStream(outputFileTxT, true);
				PrintWriter fo = new PrintWriter(write);
				fo.println("Greedy init solution");
				fo.println("Init-RunTime-Objective-NbServedCustomers-NbUsedVehicles");
				fo.println((System.currentTimeMillis() - t0)/1000 
						+ "-" + solver.objective.getValue()
						+ "-" + solver.getNbServedCustomers()
						+ "-" + solver.getNbUsedVehicles());
				fo.close();
			}catch(Exception e){
				System.out.println(e);
			}
			//solver.initParamsForALNS();
			//solver.localSearch(outputFile, 10000, 180000);
			solver.ALNSsearch(outputFileTxT);
			
			solver.printSolution(t0, outputFileTxT);
			
			MDMTPSolutionJson solution = solver.createFormatedSolution();
			try{
				String out = g.toJson(solution);
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileJson));
			    writer.write(out);
			     
			    writer.close();
			}catch(Exception e){
				System.out.println(e);
			}
			
			System.out.println("Done!");
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}
