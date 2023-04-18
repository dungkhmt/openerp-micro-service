package localsearch.domainspecific.vehiclerouting.apps.vinamilk.service;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class MDMTPsolution {
	private ArrayList<ArrayList<Point>> _route;
	private HashMap<Point, Integer> _point2arrivalTime;
	private ArrayList<Point> _rejectPoints;
	private double _cost;
	private int _nbVehicles;
	private int _nbServedReqs;
	
	public MDMTPsolution(VarRoutesVR XR, HashMap<Point, Integer> point2arrivalTime, 
			double cost, int nbVehicles, int nbServedReqs,
			ArrayList<Point> rejectPoints){		
		_route = new ArrayList<ArrayList<Point>>();
		
		int K = XR.getNbRoutes();
		
		for(int k=1; k<=K; k++){
			ArrayList<Point> route_k = new ArrayList<Point>();
			Point x = XR.getStartingPointOfRoute(k);
			for(; x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
				route_k.add(x);
			}
			route_k.add(x);
			_route.add(route_k);
		}
		
		this._point2arrivalTime = new HashMap<Point, Integer>();
		for(Point x : point2arrivalTime.keySet())
			_point2arrivalTime.put(x, point2arrivalTime.get(x));
		
		this._cost = cost;
		this._nbVehicles = nbVehicles;
		this._nbServedReqs = nbServedReqs;
		
		this._rejectPoints = new ArrayList<Point>();
		for(int i = 0; i < rejectPoints.size(); i++)
			_rejectPoints.add(rejectPoints.get(i));
	}
	
	public void copy2XR(VarRoutesVR XR){
		int K = XR.getNbRoutes();
		VRManager mgr = XR.getVRManager();
		mgr.performRemoveAllClientPoints();
		
		for(int k=1; k<=K; k++){
			ArrayList<Point> route_k = _route.get(k-1);
			for(int i=0; i<route_k.size()-2; i++){
				mgr.performAddOnePoint(route_k.get(i+1),route_k.get(i));
			}
		}
	}

	public ArrayList<ArrayList<Point>> get_route() {
		return _route;
	}

	public void set_route(ArrayList<ArrayList<Point>> _route) {
		this._route = _route;
	}

	public HashMap<Point, Integer> get_point2arrivalTime() {
		return _point2arrivalTime;
	}

	public void set_point2arrivalTime(HashMap<Point, Integer> _point2arrivalTime) {
		this._point2arrivalTime = _point2arrivalTime;
	}

	public double get_cost() {
		return _cost;
	}

	public void set_cost(double _cost) {
		this._cost = _cost;
	}

	public int get_nbVehicles() {
		return _nbVehicles;
	}

	public void set_nbVehicles(int _nbVehicles) {
		this._nbVehicles = _nbVehicles;
	}

	public int get_nbServedReqs() {
		return _nbServedReqs;
	}

	public void set_nbServedReqs(int _nbServedReqs) {
		this._nbServedReqs = _nbServedReqs;
	}

	public ArrayList<Point> get_rejectPoints() {
		return _rejectPoints;
	}

	public void set_rejectPoints(ArrayList<Point> _rejectPoints) {
		this._rejectPoints = _rejectPoints;
	}
	
}
