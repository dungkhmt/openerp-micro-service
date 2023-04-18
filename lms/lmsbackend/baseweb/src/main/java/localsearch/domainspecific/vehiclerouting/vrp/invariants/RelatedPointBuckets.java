package localsearch.domainspecific.vehiclerouting.vrp.invariants;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class RelatedPointBuckets implements InvariantVR {

	protected VarRoutesVR XR;
	protected VRManager mgr;
	HashMap<Point, Double> eat;
	HashMap<Point, Integer> latestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	private HashMap<Integer, HashMap<Integer, ArrayList<Point>>> bks;
	private HashMap<Point,Point> pickup2DeliveryOfPeople;
	private int delta;
	public int nbBuckets;
	
	public RelatedPointBuckets(VarRoutesVR XR, HashMap<Point,Point> pickup2DeliveryOfPeople, HashMap<Point, Double> eat, HashMap<Point, Integer> latestAllowedArrivalTime, HashMap<Point, Integer> serviceDuration, int delta){
		this.pickup2DeliveryOfPeople = pickup2DeliveryOfPeople;
		this.eat = eat;
		this.latestAllowedArrivalTime = latestAllowedArrivalTime;
		this.serviceDuration = serviceDuration;
		this.XR = XR;
		this.mgr = XR.getVRManager();
		this.delta = delta;
		bks = new HashMap<Integer, HashMap<Integer, ArrayList<Point>>>();
		this.nbBuckets = 86400/delta;
		this.mgr.post(this);
	}
	
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Point>>> getBuckets(){
		return this.bks;
	}
	
	public void setBuckets(HashMap<Integer, HashMap<Integer, ArrayList<Point>>> bks) {
		this.bks = bks;
	}
	public HashMap<Integer, ArrayList<Point>> getBucketOfRoute(int idx){
		return this.bks.get(idx);
	}
	
	public int getDelta(){
		return delta;
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		
		for(int i = 1; i <= XR.getNbRoutes(); i++){
			HashMap<Integer, ArrayList<Point>> routeBK = new HashMap<Integer, ArrayList<Point>>();
			Point stP = XR.getStartingPointOfRoute(i);
			for(int j = 0; j < nbBuckets; j++){
				ArrayList<Point> b0 = new ArrayList<Point>();
				b0.add(stP);
				b0.add(stP);
				routeBK.put(j, b0);
			}
			bks.put(i, routeBK);
		}
	}
	/***
	 * Update bucket of some points on route k
	 * Point x is the last point on route k in which it was not affected by moving.
	 */
	private void propagate(Point x){
		int k = XR.route(x);
		HashMap<Integer, ArrayList<Point>> routeBK = bks.get(k);
		for(Point p = x; p != XR.getTerminatingPointOfRoute(k); p = XR.next(p)){
			//get bucket list include point p
			if(pickup2DeliveryOfPeople.containsKey(p))
				continue;
			
			//update new bucket
			double eatX = eat.get(p);
			int stIdx = (int)eatX/delta;
			int endIdx = stIdx;
			//double flexTime = latestAllowedArrivalTime.get(XR.next(p)) - eat.get(XR.next(p));
			//endIdx = (int)(eatX + serviceDuration.get(p) + flexTime)/delta;
			endIdx = latestAllowedArrivalTime.get(XR.next(p))/delta + 1;
			if(endIdx > 86400/delta - 1)
				endIdx = 86400/delta - 1;
			for(int i = stIdx; i <= endIdx; i++){	
				ArrayList<Point> bk = routeBK.get(i);
				Point st = bk.get(0);

				if(XR.index(st) == Constants.NULL_POINT || XR.index(st) > XR.index(p))
					bk.set(0, p);
				else{
					Point en = bk.get(1);
					if(XR.index(en) < XR.index(p))
						bk.set(1, p);
				}
				routeBK.put(i, bk);
			}
			//p.setBucketIDs(BucketIDsOfPointP);
		}
		bks.put(k, routeBK);
	}

	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		Point preX = XR.oldPrev(x);
		propagate(preX);
		propagate(y);
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		Point preY = XR.oldPrev(y);
		int k = XR.route(x);
		propagate(preY);
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1, Point y2, Point y3,
			Point y4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		propagate(x);

	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		propagate(XR.oldPrev(x));
	}

	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		propagate(x1);
	}

	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		propagate(XR.oldPrev(x1));
	}

	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "RelatedPointBuckets";
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

}
