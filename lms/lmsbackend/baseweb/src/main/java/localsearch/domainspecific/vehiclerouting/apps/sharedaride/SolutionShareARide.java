package localsearch.domainspecific.vehiclerouting.apps.sharedaride;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.RelatedPointBuckets;

public class SolutionShareARide {
	
	private ArrayList<ArrayList<Point>> _route;
	private ArrayList<Point> _rejectPoints;
	private ArrayList<Point> _rejectPickupGoods;
	private ArrayList<Point> _rejectPickupPeoples;
	private double _cost;
	private HashMap<Integer, HashMap<Integer, ArrayList<Point>>> _bks;
	
	public SolutionShareARide(RelatedPointBuckets buckets, VarRoutesVR XR, ArrayList<Point> rejectPoints, ArrayList<Point> rejectPickupGoods, ArrayList<Point> rejectPickupPeoples, double cost){
		_bks = new HashMap<Integer, HashMap<Integer, ArrayList<Point>>>(buckets.getBuckets());
		_rejectPoints = new ArrayList<Point>();
		_rejectPickupGoods = new ArrayList<Point>();
		_rejectPickupPeoples = new ArrayList<Point>();
		
		for(int i=0; i<rejectPoints.size(); i++){
			_rejectPoints.add(rejectPoints.get(i));
		}
		
		for(int i=0; i<rejectPickupGoods.size(); i++){
			_rejectPickupGoods.add(rejectPickupGoods.get(i));
		}
		
		for(int i=0; i<rejectPickupPeoples.size(); i++){
			_rejectPickupPeoples.add(rejectPickupPeoples.get(i));
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
	}
	
	public void set_buckets(HashMap<Integer, HashMap<Integer, ArrayList<Point>>> bks) {
		this._bks = bks;
	}
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Point>>> get_buckets(){
		return _bks;
	}
	
	public ArrayList<ArrayList<Point>> get_route() {
		return _route;
	}

	public void set_route(ArrayList<ArrayList<Point>> _route) {
		this._route = _route;
	}

	public ArrayList<Point> get_rejectPoints() {
		return _rejectPoints;
	}

	public void set_rejectPoints(ArrayList<Point> _rejectPoints) {
		this._rejectPoints = _rejectPoints;
	}

	public ArrayList<Point> get_rejectPickupGoods() {
		return _rejectPickupGoods;
	}

	public void set_rejectPickupGoods(ArrayList<Point> _rejectPickupGoods) {
		this._rejectPickupGoods = _rejectPickupGoods;
	}

	public ArrayList<Point> get_rejectPickupPeoples() {
		return _rejectPickupPeoples;
	}

	public void set_rejectPickupPeoples(ArrayList<Point> _rejectPickupPeoples) {
		this._rejectPickupPeoples = _rejectPickupPeoples;
	}

	public double get_cost() {
		return _cost;
	}

	public void set_cost(double _cost) {
		this._cost = _cost;
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
	
	
	
	public String toString(){
		String s = "";
		int K = _route.size();
		for(int k = 0; k < K; k++){
			s += "route[" + k + "] = ";
			ArrayList<Point> route_k = _route.get(k);
			int i=0;
			Point x;
			for(; i< route_k.size()-1; i++){
				x = route_k.get(i);
				s = s + x.getID() + " " + " -> ";
			}
			x = route_k.get(i);
			s = s + x.getID() + "\n";
		}
		/*
		String r = "rejectPoints = [";
		for(int i = 0; i<_rejectPickupPoints.size(); i++){
			r += (_rejectPickupPoints.get(i).getID()+", ");
		}
		r += "] \n";
		*/
		//s += (r + "cost = "+_cost+"\n");
		
		return s;
	}
	
}
