package localsearch.domainspecific.vehiclerouting.vrp.invariants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
public class EarliestArrivalTimeVR implements InvariantVR {
	VarRoutesVR XR;
	ArcWeightsManager awm;
	HashMap<Point, Integer> earliestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	private HashMap<Point, Double> earliestArrivalTime;
	public EarliestArrivalTimeVR(VarRoutesVR XR, ArcWeightsManager awm, 
			HashMap<Point, Integer> earliestAllowedArrivalTime, HashMap<Point, Integer> serviceDuration){
		this.XR = XR;
		this.awm = awm;
		this.earliestAllowedArrivalTime = earliestAllowedArrivalTime;
		this.serviceDuration = serviceDuration;
		earliestArrivalTime = new HashMap<Point,Double>();
		getVRManager().post(this);
	}
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return XR.getVRManager();
	}

	
	
	public void initPropagation() {
		// TODO Auto-generated method stub
		int nr = XR.getNbRoutes();
		for(int i=1;i<=nr;++i)
			update(i);
	}

	private void update(int k){
		Point s = XR.getStartingPointOfRoute(k);
		earliestArrivalTime.put( s ,1.0*earliestAllowedArrivalTime.get(s));

		for(Point x = s; x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
			Point nx = XR.next(x);
			//System.out.println(earliestArrivalTime.get(x) + "  "+serviceDuration.get(x) + "   "+ awm.getDistance(x, nx));
			double tnx = earliestArrivalTime.get(x) + serviceDuration.get(x) + awm.getDistance(x, nx);
			double tmp = tnx > earliestAllowedArrivalTime.get(nx) ? 
					tnx : earliestAllowedArrivalTime.get(nx);
				earliestArrivalTime.put(nx, tmp);
			//System.out.println(x+"  "+nx+"  "+awm.getDistance(x, nx)+"   "+tmp);
		}
	}
	public HashMap<Point,Double> getEarliestArrivalTime(){
		return earliestArrivalTime;
	}
	public double getEarliestArrivalTime(Point v){
		return earliestArrivalTime.get(v);
	}
	
	public HashMap<Point,Integer> getEarliestAllowedArrivalTime(){
		return earliestAllowedArrivalTime;
	}
	public VarRoutesVR getVarRouteVR()
	{
		return XR;
	}
	public double getServiceDuration(Point v)
	{
		return serviceDuration.get(v);
	}
	public HashMap<Point,Integer> getServiceDuration()
	{
		return serviceDuration;
	}
	public double getTravelTime(Point x,Point y)
	{
		return awm.getDistance(x, y);
	}
	public double getEarliestAllowedArrivalTime(Point v)
	{
		return earliestAllowedArrivalTime.get(v);
	}
	void updateFromPoint(Point x)
	{
		//System.out.println(name() + "::updateFromPoint(" + x.ID + "), earliestArrivalTime = " + earliestArrivalTime.get(x));
		Point pX = XR.prev(x);
		Point p = pX;
		Point nP = x;
		if(XR.isStartingPoint(x))
		{
			p = x;
			nP = XR.next(x);
		}
		//System.out.println(name() + "::updateFromPoint(" + x.ID + "), earliestArrivalTime = " + earliestArrivalTime.get(x) + ", p = " + p.ID + ", nP = " + nP.ID);
		do{
			double curTime = earliestArrivalTime.get(p) + serviceDuration.get(p);
			if(earliestAllowedArrivalTime.get(nP) > curTime + awm.getDistance(p, nP))
			{
				earliestArrivalTime.put(nP, 1.0*earliestAllowedArrivalTime.get(nP));
			}
			else{
				earliestArrivalTime.put(nP,curTime + awm.getDistance(p, nP)) ;
			}
			//System.out.println(name() + "::updateFromPoint(" + x.ID + "), set earliestArrivalTime(" + nP.ID + ") = " + earliestArrivalTime.get(nP));
			p = nP;
			nP = XR.next(p);
		}while(!XR.isTerminatingPoint(p));
		/*
		System.out.println(name() + "::updateFromPoint(" + x.ID + "), after propagate: ");
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			for(Point z = XR.startPoint(k); z != XR.endPoint(k); z = XR.next(z)){
				System.out.println("earliestArrivalTime(" + z.ID + ") = " + earliestArrivalTime.get(z));
			}
			Point z = XR.endPoint(k);
			System.out.println("earliestArrivalTime(" + z.ID + ") = " + earliestArrivalTime.get(z));
		}
		*/
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
	}

	
	// move of type a [Groer et al., 2010]
    // move customer x to from route of x to route of y; insert x into the position between y and next[y]
    // x and y are not the depot
	
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		Point oldNexX = XR.oldNext(x);
		updateFromPoint(oldNexX);
		updateFromPoint(x);
	}

	// move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		update(XR.route(x));
	}

	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next(y))
	
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(XR.oldNext(x));
	}

	
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(y);
		updateFromPoint(XR.oldNext(x));
	}

	
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(XR.oldNext(y));
	}

	
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(y);
		updateFromPoint(XR.oldNext(y));
	}

	
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(y);
	}

	
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(y));
		updateFromPoint(y);
	}

	
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(XR.oldNext(x));
	}

	
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(y));
		updateFromPoint(XR.oldNext(x));
	}

	
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldPrev(x1));
		updateFromPoint(y);
		updateFromPoint(x2);
	}

	
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		update(XR.oldRoute(x1));
		update(XR.oldRoute(y));
	}

	
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(XR.oldNext(y));
		updateFromPoint(y);
	}

	
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(z));
		updateFromPoint(XR.oldNext(x));
		updateFromPoint(z);
	}

	
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(XR.oldNext(x));
		updateFromPoint(XR.oldNext(y));
	}

	
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(z));
		updateFromPoint(z);
		updateFromPoint(y);
	}

	
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(z);
		updateFromPoint(y);
	}

	
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(z));
		updateFromPoint(XR.oldNext(x));
		updateFromPoint(XR.oldNext(y));
	}

	
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
		updateFromPoint(z);
		updateFromPoint(XR.oldNext(x));
	}

	
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(z));
		updateFromPoint(y);
		updateFromPoint(XR.oldNext(y));
	}

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		updateFromPoint(x1);
		updateFromPoint(x2);
		updateFromPoint(y1);
		updateFromPoint(y2);
	}

	
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
    	oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	for (int r : oldR) {
    		update(r);
    	}
	}

	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
    	oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	oldR.add(XR.oldRoute(x3)); 
    	oldR.add(XR.oldRoute(y3));
    	for (int r : oldR) {
    		update(r);
    	}
	}

	
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		HashSet<Integer> oldR = new HashSet<Integer>();
		oldR.add(XR.oldRoute(x1)); 
    	oldR.add(XR.oldRoute(y1));
    	oldR.add(XR.oldRoute(x2)); 
    	oldR.add(XR.oldRoute(y2));
    	oldR.add(XR.oldRoute(x3)); 
    	oldR.add(XR.oldRoute(y3));
    	oldR.add(XR.oldRoute(x4)); 
    	oldR.add(XR.oldRoute(y4));
    	for (int r : oldR) {
    		update(r);
    	}
	}

	
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		updateFromPoint(x);
	}

	
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		updateFromPoint(XR.oldNext(x));
	}

	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		updateFromPoint(x1);
	}

	
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		if(x2 != XR.oldNext(x1))
			updateFromPoint(XR.oldNext(x1));
		else
			updateFromPoint(XR.oldNext(x2));
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		
	}

	
	public String name() {
		// TODO Auto-generated method stub
		return "EarliestArrivalTimeVR";
	}
	
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		/*
		HashMap<Integer,Point> mp = new HashMap<Integer,Point>();
		int i;
		for(i=0;i<x.size();++i)
		{
			Point p = x.get(i);
			if(y.get(i) == CBLSVR.NULL_POINT)
				continue;
			int k = XR.route(p);
			int ind = XR.index(p);
			if(mp.containsKey(k))
			{
				if(XR.index(mp.get(k)) > ind)
					mp.put(k, p);
			}
			else{
				mp.put(k, p);
			}
		}
		Set<Integer> keyset = mp.keySet();
		for(int k : keyset)
		{
			updateFromPoint(mp.get(k));
		}
		*/
		//System.out.print(name() + "::propagateKPointsMove, XR = " + XR.toString() + ", x = ");
		//for(Point p: x) System.out.print(p.ID + ", ");
		//System.out.print(", y = "); for(Point p: y) System.out.print(p.ID + ", ");
		//System.out.println();
		
		Set<Integer> st = new HashSet<Integer>();
		for(Point p : x)
			st.add(XR.oldRoute(p));
		for(Point p : y){
			st.add(XR.oldRoute(p));
		}
		for(int k : st){
			if(k != Constants.NULL_POINT)
				update(k);
		}
	}

}
