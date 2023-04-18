package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class TruckContainerSolution {
	private ArrayList<ArrayList<Point>> _route;
	private ArrayList<Point> _rejectPickupPoints;
	private ArrayList<Point> _rejectDeliveryPoints;
	private HashMap<Integer, Integer> _group2marked;
	private HashMap<Point, Integer> _point2Group;

	private double _cost;
	private int _nbTrucks;
	private int _nbReject;
	
	public TruckContainerSolution(VarRoutesVR XR, ArrayList<Point> rejectPickupPoints, 
			ArrayList<Point> rejectDeliveryPoints, double cost, int nbTrucks, int nbReject,
			HashMap<Point, Integer> point2Group, HashMap<Integer, Integer> group2marked){
		this._rejectPickupPoints = new ArrayList<Point>();
		this._rejectDeliveryPoints = new ArrayList<Point>();
		this._group2marked = new HashMap<Integer, Integer>();
		this._point2Group = new HashMap<Point, Integer>();
		
		for(int i=0; i<rejectPickupPoints.size(); i++){
			_rejectPickupPoints.add(rejectPickupPoints.get(i));
		}
		
		for(int i=0; i<rejectDeliveryPoints.size(); i++){
			_rejectDeliveryPoints.add(rejectDeliveryPoints.get(i));
		}
		
		for(int key : group2marked.keySet()){
			_group2marked.put(key, group2marked.get(key));
		}
		
		for(Point key : point2Group.keySet()){
			_point2Group.put(key, point2Group.get(key));
		}
		
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
		
		this._cost = cost;
		this._nbTrucks = nbTrucks;
		this._nbReject = nbReject;
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

	public ArrayList<Point> get_rejectPickupPoints() {
		return _rejectPickupPoints;
	}

	public HashMap<Point, Integer> get_point2Group() {
		return _point2Group;
	}

	public void set_point2Group(HashMap<Point, Integer> _point2Group) {
		this._point2Group = _point2Group;
	}

	public void set_rejectPickupPoints(ArrayList<Point> _rejectPickupPoints) {
		this._rejectPickupPoints = _rejectPickupPoints;
	}

	public ArrayList<Point> get_rejectDeliveryPoints() {
		return _rejectDeliveryPoints;
	}

	public void set_rejectDeliveryPoints(ArrayList<Point> _rejectDeliveryPoints) {
		this._rejectDeliveryPoints = _rejectDeliveryPoints;
	}

	public double get_cost() {
		return _cost;
	}

	public void set_cost(double _cost) {
		this._cost = _cost;
	}

	public int get_nbTrucks() {
		return _nbTrucks;
	}

	public void set_nbTrucks(int _nbTrucks) {
		this._nbTrucks = _nbTrucks;
	}
	
	public int get_nbReject() {
		return _nbReject;
	}

	public void set_nbReject(int _nbReject) {
		this._nbReject = _nbReject;
	}

	public HashMap<Integer, Integer> get_group2marked() {
		return _group2marked;
	}

	public void set_group2marked(HashMap<Integer, Integer> _group2marked) {
		this._group2marked = _group2marked;
	}
	
	public int getNbRejectedRequests(){
		Set<Integer> grs = new HashSet<Integer>();
		for(int i = 0; i < _rejectPickupPoints.size(); i++){
			Point pickup = _rejectPickupPoints.get(i);
			int groupId = _point2Group.get(pickup);
			
			if(_group2marked.get(groupId) == 1)
				continue;
			grs.add(groupId);
		}
		return grs.size();
	}
}
