package localsearch.domainspecific.vehiclerouting.apps.vinamilk.service;

import java.util.ArrayList;
import java.util.HashMap;


import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Utils;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class Constraints {
	public HashMap<Point, Integer> earliestAllowedArrivalTime;
	public HashMap<Point, Double> serviceDuration;
	public HashMap<Point, Integer> waittingDuration;
	public HashMap<Point, Integer> lastestAllowedArrivalTime;
	public double[][] matrixT;
	public HashMap<String, Double> vhCode2upperCapacity;
	public HashMap<String, Double> vhCode2lowerCapacity;
	public HashMap<String, Double> vhCode2weight;
	public HashMap<String, String[]> vhCode2typeOfProduct;
	public HashMap<String, Integer> vhCode2nbTrips;
	
	public HashMap<String, Double> cusLocationId2limitedWeight;
	
	public HashMap<Point, String> startPoint2vhCode;
	
	public VRManager mgr;
	public VarRoutesVR XR;
	
	public Constraints(HashMap<Point, Integer> earliestAllowedArrivalTime, 
			HashMap<Point, Double> serviceDuration,
			HashMap<Point, Integer> waittingDuration, 
			HashMap<Point, Integer> lastestAllowedArrivalTime,
			double[][] matrixT, HashMap<String, Double> vhCode2upperCapacity,
			HashMap<String, Double> vhCode2lowerCapacity, 
			HashMap<String, Double> vhCode2weight,
			HashMap<String, String[]> vhCode2typeOfProduct,
			HashMap<String, Integer> vhCode2nbTrips,
			HashMap<Point, String> startPoint2vhCode,
			HashMap<String, Double> cusLocationId2limitedWeight,
			VRManager mgr, VarRoutesVR XR) {
		super();
		this.earliestAllowedArrivalTime = earliestAllowedArrivalTime;
		this.serviceDuration = serviceDuration;
		this.waittingDuration = waittingDuration;
		this.lastestAllowedArrivalTime = lastestAllowedArrivalTime;
		this.matrixT = matrixT;
		this.vhCode2upperCapacity = vhCode2upperCapacity;
		this.vhCode2lowerCapacity = vhCode2lowerCapacity;
		this.vhCode2weight = vhCode2weight;
		this.vhCode2typeOfProduct = vhCode2typeOfProduct;
		this.vhCode2nbTrips = vhCode2nbTrips;
		this.startPoint2vhCode = startPoint2vhCode;
		this.cusLocationId2limitedWeight = cusLocationId2limitedWeight;
		this.mgr = mgr;
		this.XR = XR;
	}

	public boolean timeWindowConstraint(ArrayList<Point> route) {
		HashMap<Point, Integer> loadWeights = new HashMap<Point, Integer>();
		int acmLoadWeight = 0;
		Point depot = null;
		for(int i = 1; i < route.size(); i++) {
			Point p = route.get(i);
			acmLoadWeight += p.getOrderWeight();
			if(p.getTypeOfPoint().equals(Utils.DEPOT)) {
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
		int startServiceTime = earliestAllowedArrivalTime.get(route.get(0));
		for(int i = 1; i < route.size(); i++) {
			Point p = route.get(i);
			startServiceTime += matrixT[route.get(i-1).ID][p.ID];
			if(startServiceTime > lastestAllowedArrivalTime.get(route.get(i)))
				return false;
			if(startServiceTime < earliestAllowedArrivalTime.get(p))
				startServiceTime = earliestAllowedArrivalTime.get(p);
			startServiceTime += 0;//waittingDuration.get(p);
			startServiceTime += serviceDuration.get(p);
			if(p.getTypeOfPoint().equals(Utils.DEPOT))
				startServiceTime += loadWeights.get(p)*serviceDuration.get(p);
		}
		return true;
	}
	
	public boolean timeWindowConstraint(int r) {
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
		for(Point p = XR.next(st); p != null; p = XR.next(p)) {
			startServiceTime += matrixT[XR.prev(p).getID()][p.getID()];
			if(startServiceTime > lastestAllowedArrivalTime.get(p))
				return false;
//			if(startServiceTime < earliestAllowedArrivalTime.get(p))
//				return false;
			startServiceTime += 0;//waittingDuration.get(p);
			startServiceTime += serviceDuration.get(p);
			if(p.getTypeOfPoint().equals(Utils.DEPOT))
				startServiceTime += loadWeights.get(p);
		}
		return true;
	}
	
	public int upperCapacityConstraints(ArrayList<Point> route) {
		if(route.size() <= 2)
			return 1;
		
		double acmWeight = 0;
		int nbDepots = 0;
		String vhCode = startPoint2vhCode.get(route.get(0));
		for(int i = 1; i < route.size(); i++) {
			Point p = route.get(i);
			acmWeight += p.getOrderWeight();
			if(acmWeight > vhCode2upperCapacity.get(vhCode)) {
//				System.out.println("acmW = " + acmWeight 
//						+ ", cap = " + vhCode2upperCapacity.get(vhCode)
//						+ ", nbDepots = " + nbDepots);
//				if(acmWeight >= vhCode2lowerCapacity.get(vhCode))
//					return -1;
				return 0;
			}
			if(p.getTypeOfPoint().equals(Utils.DEPOT) 
				|| p.getTypeOfPoint().equals(Utils.PARKING)) {
//				if(acmWeight < vhCode2lowerCapacity.get(vhCode)
//					&& nbDepots > 0)
//					return false;
				acmWeight = 0;
				nbDepots++;
			}
		}
		if(nbDepots - 1 > vhCode2nbTrips.get(vhCode)
			|| nbDepots == 1) {
//			System.out.println("nbDepots = " + nbDepots);
			return -1;
		}
		
		return 1;
	}
	
	public boolean upperCapacityConstraints(int r) {
		Point st = XR.getStartingPointOfRoute(r);
		Point tp = XR.getTerminatingPointOfRoute(r);
		if(XR.next(st) == tp)
			return true;
		if(!XR.next(st).getTypeOfPoint().equals(Utils.DEPOT))
			return false;
		
		double acmWeight = 0;
		int nbDepots = 0;
		String vhCode = startPoint2vhCode.get(st);
		for(Point p = XR.next(st); p != null; p = XR.next(p)) {
			acmWeight += p.getOrderWeight();
			if(acmWeight > vhCode2upperCapacity.get(vhCode)) {
//				System.out.println("acmWeight = " + acmWeight + ", nbDepots = " + nbDepots
//						+ ", upperLimited = " + vhCode2upperCapacity.get(vhCode));
				return false;
			}
			
			if(p.getTypeOfPoint().equals(Utils.DEPOT) 
				|| p.getTypeOfPoint().equals(Utils.PARKING)) {
				acmWeight = 0;
				nbDepots++;
			}
		}
		if(nbDepots - 1 > vhCode2nbTrips.get(vhCode)
			|| nbDepots == 1) {
//			System.out.println("nbDepots = " + nbDepots);
			return false;
		}
		
		return true;
	}
	
	public double lowerCapacityConstraints(int r) {
		Point st = XR.getStartingPointOfRoute(r);
		String vhCode = startPoint2vhCode.get(st);
		double acmWeight = 0;
		for(Point p = XR.next(st);
				p != null; p = XR.next(p)) {
			acmWeight += p.getOrderWeight();
			if(p.getTypeOfPoint().equals(Utils.DEPOT) 
					|| p.getTypeOfPoint().equals(Utils.PARKING)) {
				double lw = vhCode2lowerCapacity.get(vhCode);
				if(acmWeight < lw
					&& acmWeight > 0) {
					double vio = acmWeight > lw/2 ? lw - acmWeight : acmWeight;
					return vio;
				}
				acmWeight = 0;
			}
		}
		return 0;
	}
	
	public boolean typeOfProductsCanCarriedByVehicle(String vhCode, String productType) {
		if(vhCode2typeOfProduct.get(vhCode) == null)
			return true;
		String[] restrictedProducts = vhCode2typeOfProduct.get(vhCode);
		for(int i = 0; i< restrictedProducts.length; i++) {
			if(restrictedProducts[i].equals(productType))
				return false;
		}
		return true;
	}
	
	public boolean customerCanVisitedByVehicle(String vhCode, String cusLocationId) {
		if(vhCode2weight.get(vhCode) > cusLocationId2limitedWeight.get(cusLocationId))
			return false;
		else
			return true;
	}
	
	public boolean checkAllConstraints(ArrayList<Point> route) {
		if(route.size() <= 2)
			return true;
		
		if(timeWindowConstraint(route) == false
				|| upperCapacityConstraints(route) != 1)
			return false;
		String vhCode = startPoint2vhCode.get(route.get(0));
		for(int i = 1; i < route.size(); i++) {
			Point p = route.get(i);
			if(p.getTypeOfPoint().equals(Utils.CUSTOMER)
					&& (customerCanVisitedByVehicle(vhCode, p.getLocationId())== false
					|| typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false))
				return false;			
		}
		return true;
	}
	
	public boolean checkAllConstraints(int r) {
		Point st = XR.getStartingPointOfRoute(r);
		Point tp = XR.getTerminatingPointOfRoute(r);
		if(XR.next(st) == tp)
			return true;
		
		if(timeWindowConstraint(r) == false
				|| upperCapacityConstraints(r) == false)
			return false;
		String vhCode = startPoint2vhCode.get(st);
		for(Point p = XR.next(st); p != null; p = XR.next(p)) {
			if(p.getTypeOfPoint().equals(Utils.CUSTOMER)
					&& (customerCanVisitedByVehicle(vhCode, p.getLocationId())== false
					|| typeOfProductsCanCarriedByVehicle(vhCode, p.getTypeOfProduct()) == false))
				return false;			
		}
		return true;
	}
	
}
