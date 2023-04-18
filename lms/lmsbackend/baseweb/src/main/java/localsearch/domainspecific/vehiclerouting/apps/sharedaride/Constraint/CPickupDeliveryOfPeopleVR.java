package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class CPickupDeliveryOfPeopleVR implements IConstraintVR{
	VarRoutesVR XR;
	Map<Point,Point>pickup2Delivery;
	Map<Point,Point>delivery2Pickup;
	
	HashMap<Point,Point> t_next;
	HashMap<Point,Integer> vio;
	int violations;
	public CPickupDeliveryOfPeopleVR(VarRoutesVR XR, Map<Point,Point> pickup2Delivery) {
		// TODO Auto-generated constructor stub
		this.XR = XR;
		this.pickup2Delivery = pickup2Delivery;
		this.delivery2Pickup = new HashMap<Point,Point>();
		Set<Point>stPoint = pickup2Delivery.keySet();
		for(Point p : stPoint)
		{
			Point dp = pickup2Delivery.get(p);
			delivery2Pickup.put(dp, p);
		}
		getVRManager().post(this);
		
	}
	
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return XR.getVRManager();
	}

	int computeViolations(Point pickup)
	{
		Point delivery = pickup2Delivery.get(pickup);
		int pickupRoute = XR.route(pickup);
		int deliveryRoute = XR.route(delivery);
		int pickupIndex = XR.index(pickup);
		int deliveryIndex = XR.index(delivery);
		if((pickupRoute == deliveryRoute && pickupIndex + 1 == deliveryIndex))
		{
			return 0;
		}
		else
			return 1;
	}
	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		t_next = new HashMap<Point,Point>();
		vio = new HashMap<Point,Integer>();
		Set<Point> pickups = pickup2Delivery.keySet();
		violations = 0;
		ArrayList<Point>allP = XR.getAllPoints();
		for(Point p : allP)
			vio.put(p, 0);
		for(Point pickup : pickups)
		{
			int v = computeViolations(pickup);
			vio.put(pickup, v);
			violations += v;
		}
	}

	private void propagate(int k)
	{
		Set<Point> pickups = pickup2Delivery.keySet();
		for(Point v = XR.getStartingPointOfRoute(k); v!= XR.getTerminatingPointOfRoute(k); v = XR.oldNext(v))
		{
			if(pickups.contains(v))
			{
				violations -= vio.get(v);
				vio.put(v,computeViolations(v));
				violations += vio.get(v);
			}
		}
	}

	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		if (kx == ky) {
			propagate(kx);
		} else {
			propagate(kx);
			propagate(ky);
		}
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		propagate(kx);
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}
	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(x2));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(x2));
		st.add(XR.oldRoute(y1));
		st.add(XR.oldRoute(y2));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(x2));
		st.add(XR.oldRoute(y1));
		st.add(XR.oldRoute(y2));
		st.add(XR.oldRoute(x3));
		st.add(XR.oldRoute(y3));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(x2));
		st.add(XR.oldRoute(y1));
		st.add(XR.oldRoute(y2));
		st.add(XR.oldRoute(x3));
		st.add(XR.oldRoute(y3));
		st.add(XR.oldRoute(x4));
		st.add(XR.oldRoute(y4));
		for(Integer k : st)
			propagate(k);
	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int k = XR.oldRoute(y);
		propagate(k);
	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int k = XR.oldRoute(x);
		propagate(k);
	}

	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("CPickupDeliveryOfPeople::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("CPickupDeliveryOfPeople::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}
	
	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "CEarliestArrivalTimeVR";
	}

	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return violations;
	}

	void getSegment(Point begin,Point end)
	{
		Point v = begin;
		while(v!=end)
		{
			t_next.put(v , XR.next(v));
			v = XR.next(v);
		}
	}
	void getRevSegment(Point begin,Point end)
	{
		Point v = begin;
		while(v!=end)
		{
			t_next.put(v , XR.prev(v));
			v = XR.prev(v);
		}
	}
	
	int calDeltaSegment(Point begin,Point end)
	{
		int delta = 0;
		Set<Point>pickups = pickup2Delivery.keySet();
		HashMap<Point,Integer> indexs = new HashMap<Point,Integer>();
		Point v = begin;
		int ind = 0;
		do{
			ind++;
			indexs.put(v, ind);
			if(v==end)
				break;
			v = t_next.get(v);
		}while(true);
		v = begin;
		do{
			if(pickups.contains(v))
			{
				delta -= vio.get(v);
				int indPickup = indexs.get(v);
				Point delivery = pickup2Delivery.get(v);
				if(indexs.containsKey(delivery))
				{
					int indDelivery = indexs.get(delivery);
					if(indDelivery != indPickup + 1)
						delta++;
				}
				else{
					delta++;
				}
			}
			if(v==end)
				break;
			v = t_next.get(v);
		}while(true);
		return delta;
	}
	@Override
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub

		int kx = XR.route(x);
		int ky = XR.route(y);
		int delta = 0;
		if(x == XR.next(y))
		{
			return 0;
		}
		if(pickup2Delivery.containsKey(x)&&pickup2Delivery.get(x)==XR.next(x))
			delta ++;
		if(delivery2Pickup.containsKey(x)&&delivery2Pickup.get(x)==XR.prev(x))
			delta++;
		if(pickup2Delivery.containsKey(y)&&pickup2Delivery.get(y)==XR.next(y))
			delta++;
		if(pickup2Delivery.containsKey(y)&&pickup2Delivery.get(y)==x)
			delta--;
		if(pickup2Delivery.containsKey(x)&&pickup2Delivery.get(x)==XR.next(y))
			delta--;
		if(pickup2Delivery.containsKey(XR.prev(x))&&pickup2Delivery.get(XR.prev(x))==XR.next(x))
			delta--;
		return delta;
	}

	// move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	@Override
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int k = XR.route(x);

		Point px = XR.prev(x);
		Point nx = XR.next(x);
		Point py = XR.prev(y);
		Point ny = XR.next(y);
		HashMap<Point,Point> before = new HashMap<>();
		HashMap<Point,Point> after = new HashMap<>();
		if(ny==x)
		{
			before.put(py, y);
			before.put(y, x);
			before.put(x, nx);
			after.put(py, x);
			after.put(x, y);
			after.put(y, nx);
		}
		else{
			before.put(py, y);
			before.put(y, ny);
			before.put(px, x);
			before.put(x, nx);
			
			after.put(py, x);
			after.put(x, ny);
			after.put(y, nx);
			after.put(px, y);
		}
		int delta = 0;
		Set<Point>key = before.keySet();
		for(Point p : key)
		{
			if(pickup2Delivery.containsKey(p)&&pickup2Delivery.get(p)==before.get(p))
				delta++;
		}
		key = after.keySet();
		for(Point p : key)
		{
			if(pickup2Delivery.containsKey(p)&&pickup2Delivery.get(p)==after.get(p))
				delta--;
		}
		return delta;
	}

	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next(y))
	@Override
	public int evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub

		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		getSegment(XR.startPoint(kx), x);
		t_next.put(x,y);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		t_next.put(nx,ny);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		int delta  = 0;
		
		delta += calDeltaSegment(XR.startPoint(kx), XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx), XR.getTerminatingPointOfRoute(ky));
		return delta;
	}
	 // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[x],next(y))
	@Override
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		getSegment(XR.startPoint(ky), y);
		t_next.put(y, x);
		getRevSegment(x,XR.getStartingPointOfRoute(kx));
		
		
		t_next.put(nx,ny);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		int delta  = 0;
		
		delta += calDeltaSegment(XR.startPoint(ky), XR.getStartingPointOfRoute(kx));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx), XR.getTerminatingPointOfRoute(ky));
		return delta;
	}
    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[y],next(x))
	@Override
	public int evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		getSegment(XR.startPoint(kx), x);
		t_next.put(x,y);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		t_next.put(ny, nx);
		getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));
		
		int delta  = 0;
		
		delta += calDeltaSegment(XR.startPoint(kx), XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky), XR.getTerminatingPointOfRoute(kx));
		return delta;
	}
	 // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[y],next(x))
	@Override
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		getSegment(XR.startPoint(ky), y);
		t_next.put(y, x);
		getRevSegment(x,XR.getStartingPointOfRoute(kx));
		
		
		t_next.put(ny,nx);
		getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));

		int delta  = 0;
		delta += calDeltaSegment(XR.startPoint(ky), XR.getStartingPointOfRoute(kx));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky), XR.getTerminatingPointOfRoute(kx));
		return delta;
	}
	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (y,next[x])
	@Override
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);

		getSegment(XR.startPoint(kx), x);
		t_next.put(x, ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		getSegment(XR.startPoint(ky), y);
		t_next.put(y, nx);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));

		int delta  = 0;
		delta += calDeltaSegment(XR.startPoint(ky), XR.getTerminatingPointOfRoute(kx));
		delta += calDeltaSegment(XR.startPoint(kx), XR.getTerminatingPointOfRoute(ky));
		return delta;
	}
	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (y,next[x])
	@Override
	public int evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		t_next.put(ny, x);
		getRevSegment(x,XR.getStartingPointOfRoute(kx));
		getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		
		getSegment(XR.startPoint(ky), y);
		t_next.put(y, nx);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky), XR.getStartingPointOfRoute(kx));
		delta += calDeltaSegment(XR.startPoint(ky),XR.getTerminatingPointOfRoute(kx));
		return delta;
	}
	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
	@Override
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		getSegment(XR.startPoint(kx), x);
		t_next.put(x, ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		t_next.put(nx, y);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx), XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(XR.startPoint(kx), XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}
	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (next[x],y)
	@Override
	public int evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		t_next.put(ny, x);
		getRevSegment(x,XR.getStartingPointOfRoute(kx));
		getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		
		t_next.put(nx, y);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx),XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky),XR.getStartingPointOfRoute(kx));
		return delta;
	}
    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
	@Override
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x1);
		int ky = XR.route(y);
		Point px1 = XR.prev(x1);
		Point nx2 = XR.next(x2);
		Point ny = XR.next(y);
		
		int delta = 0;
		if(pickup2Delivery.containsKey(px1))
		{
			Point delivery = pickup2Delivery.get(px1);
			if(delivery == x1)
				delta++;
			if(delivery == nx2)
				delta--;
		}
		if(pickup2Delivery.containsKey(y))
		{
			Point delivery = pickup2Delivery.get(y);
			if(delivery == x1)
				delta--;
			if(delivery == ny)
				delta++;
		}
		if(pickup2Delivery.containsKey(x2))
		{
			Point delivery = pickup2Delivery.get(x2);
			if(delivery == ny)
				delta--;
			if(delivery == nx2)
				delta++;
		}

		return delta;
	}
	// move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
	@Override
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x1);
		int ky = XR.route(y);
		Point px1 = XR.prev(x1);
		Point nx2 = XR.next(x2);
		Point ny = XR.next(y);
		
		getSegment(XR.startPoint(kx), px1);
		t_next.put(px1, nx2);
		getSegment(nx2, XR.getTerminatingPointOfRoute(kx));
		
		getSegment(XR.startPoint(ky), y);
		t_next.put(y, x2);
		getRevSegment(x2,x1);
		t_next.put(x1, ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.startPoint(kx), XR.getTerminatingPointOfRoute(kx));
		delta += calDeltaSegment(XR.startPoint(ky), XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}
    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,z) and (next[y], next[x]) and(y, next[z])
	@Override
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		
		getSegment(XR.startPoint(k), x);
		t_next.put(x,z);
		getRevSegment(z,ny);
		t_next.put(ny,nx);
		getSegment(nx,y);
		t_next.put(y,nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.startPoint(k), XR.getTerminatingPointOfRoute(k));
		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (z,x) and (next[x], next[y]) and(next[z],y)
	@Override
	public int evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		
		getRevSegment(XR.getTerminatingPointOfRoute(k),nz);
		t_next.put(nz, y);
		getRevSegment(y,nx);
		t_next.put(nx,ny);
		getSegment(ny,z);
		t_next.put(z, x);
		getRevSegment(x,XR.getStartingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));
		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,y) and (next[x], z) and(next[y], next[z])
	@Override
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		getSegment(XR.startPoint(k), x);
		t_next.put(x,y);
		getRevSegment(y,nx);
		t_next.put(nx, z);
		getRevSegment(z,ny);
		t_next.put(ny,nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.startPoint(k), XR.getTerminatingPointOfRoute(k));
		
		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (y,x) and (z,next[x]) and(next[z], next[y])
	@Override
	public int evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		
		getRevSegment(XR.getTerminatingPointOfRoute(k),nz);
		t_next.put(nz,ny);
		getSegment(ny,z);
		t_next.put(z, nx);
		getSegment(nx,y);
		t_next.put(y,x);
		getRevSegment(x,XR.getStartingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));

		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,next[x]) and(y, next[z])
	@Override
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		getSegment(XR.startPoint(k), x);
		t_next.put(x, ny);
		getSegment(ny,z);
		t_next.put(z,nx);
		getSegment(nx,y);
		t_next.put(y,nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.startPoint(k), XR.getTerminatingPointOfRoute(k));
		
		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (next[x],z) and(next[z],y)
	@Override
	public int evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		getRevSegment(XR.getTerminatingPointOfRoute(k),nz);
		t_next.put(nz, y);
		getRevSegment(y,nx);
		t_next.put(nx, z);
		getRevSegment(z,ny);
		t_next.put(ny, x);
		getRevSegment(x,XR.getStartingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));

		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,y) and(next[x], next[z])
	@Override
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		getSegment(XR.startPoint(k), x);
		t_next.put(x, ny);
		getSegment(ny,z);
		t_next.put(z,y);
		getRevSegment(y,nx);
		t_next.put(nx, nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(x, XR.getTerminatingPointOfRoute(k));
		
		return delta;
	}
	// move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (y,z) and(next[z], next[x])
	@Override
	public int evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		getRevSegment(XR.getTerminatingPointOfRoute(k),nz);
		t_next.put(nz, nx);
		getSegment(nx,y);
		t_next.put(y, z);
		getRevSegment(z,ny);
		t_next.put(ny, x);
		getRevSegment(x,XR.getStartingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));
		
		return delta;
	}
	 // move of type g [Groer et al., 2010]
    // x1 and y1 are on the same route, x1 is before y1
    // x2 and y2 are on the same route, x2 is before y2
    // remove (x1,next[x1]) and (y1, next[y1])
    // remove (x2, next[x2]) and (y2, next[y2])
    // insert (x1, next[x2]) and (y2, next[y1])
    // insert (x2, next[x1]) and (y1, next[y2])
	@Override
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		Point nx1 = XR.next(x1);
		Point ny1 = XR.next(y1);
		Point nx2 = XR.next(x2);
		Point ny2 = XR.next(y2);
		int k1 = XR.route(x1);
		int k2 = XR.route(x2);
		int delta = 0;
		if(pickup2Delivery.containsKey(x1))
		{
			Point delivery = pickup2Delivery.get(x1);
			if(delivery == nx1)
				delta++;
			if(delivery == nx2)
				delta--;
		}
		if(pickup2Delivery.containsKey(x2))
		{
			Point delivery = pickup2Delivery.get(x2);
			if(delivery == nx2)
				delta++;
			if(delivery == nx1)
				delta--;
		}
		if(pickup2Delivery.containsKey(y1))
		{
			Point delivery = pickup2Delivery.get(y1);
			if(delivery == ny1)
				delta++;
			if(delivery == ny2)
				delta--;
		}
		if(pickup2Delivery.containsKey(y2))
		{
			Point delivery = pickup2Delivery.get(y2);
			if(delivery == ny2)
				delta++;
			if(delivery == ny1)
				delta--;
		}
		return delta;
	}

	    // remove x1, x2 from their current routes
		// x1 , x2 in same route , index x1 < index x2
		// y1, y2 in same route , index y1 < index y2
		// route of x1 != route of y1
		// re-insert x1 between y1 and next[y1]
		// re-insert x2 between y2 and next[y2]	
		@Override
		public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
			// TODO Auto-generated method stub
			ArrayList<Point> x = new ArrayList<Point>();
			ArrayList<Point> y = new ArrayList<Point>();
			x.add(x1);
			x.add(x2);
			y.add(y1);
			y.add(y2);
			return evaluateKPointsMove(x, y);
		}

	@Override
	public int evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		ArrayList<Point> x = new ArrayList<Point>();
		ArrayList<Point> y = new ArrayList<Point>();
		x.add(x1);
		x.add(x2);
		x.add(x3);
		y.add(y1);
		y.add(y2);
		y.add(y3);
		return evaluateKPointsMove(x, y);
	}

	@Override
	public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		ArrayList<Point> x = new ArrayList<Point>();
		ArrayList<Point> y = new ArrayList<Point>();
		x.add(x1);
		x.add(x2);
		x.add(x3);
		y.add(y1);
		y.add(y2);
		y.add(y3);
		return evaluateKPointsMove(x, y);
	}

	@Override
	public int evaluateAddOnePoint(Point x, Point y) {
		return 0;
	}

	@Override
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		return vio.get(x);
	}
	
	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("CPickupDeliveryOfPeople::evaluateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		return 0;
	}

	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("CPickupDeliveryOfPeople::evaluateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		return 0;
	}

	@Override
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		HashSet<Integer> st = new HashSet<Integer>();
		for(Point p : x)
			st.add(XR.oldRoute(p));
		for(Point p : y)
			if( p != CBLSVR.NULL_POINT)
				st.add(XR.oldRoute(p));
		for(int k : st)
		{
			propagate(k);
		}
	}
	private int evaluateVioRoute(int k,Set<Point>out,ArrayList<Point>in,ArrayList<Point>preIn)
	{
		int delta = 0;
		
		Set<Point> before = new HashSet<Point>();
		Set<Point> after = new HashSet<Point>();
		for(Point p : out)
		{
			Point pre = XR.prev(p);
			before.add(p);
			before.add(pre);
			if(!out.contains(pre))
				after.add(pre);
			t_next.put(pre, p);
			t_next.put(p, XR.next(p));
		}
		for(int i=0;i< preIn.size(); ++i)
		{
			Point p = in.get(i);
			Point pre = preIn.get(i);
			
			before.add(pre);
			after.add(pre);
			after.add(p);
			t_next.put(pre, XR.next(pre));
		}
		for(Point p : before)
			if(pickup2Delivery.containsKey(p)&& pickup2Delivery.get(p) == t_next.get(p))
				delta ++;
		Map<Integer,Point> st = new TreeMap<Integer,Point>();
		for(Point p : out)
			st.put(-XR.index(p), p);
		Set<Integer> key = st.keySet();
		for(int ke : key)
		{
			Point p = st.get(ke);
			t_next.put(XR.prev(p), t_next.get(p));
		}
		for(int i=in.size()-1;i>=0;--i)
		{
			t_next.put(in.get(i),t_next.get(preIn.get(i)));
			t_next.put(preIn.get(i), in.get(i));
		}
		for(Point p : after)
			if(pickup2Delivery.containsKey(p)&&pickup2Delivery.get(p)== t_next.get(p))
				delta--;
		return delta;
	}
	@Override
	public int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		HashMap<Integer,Set<Point>> mout= new HashMap<Integer,Set<Point>>();
		HashMap<Integer,ArrayList<Point>> min = new HashMap<Integer,ArrayList<Point>>();
		HashMap<Integer,ArrayList<Point>> mPrein = new HashMap<Integer,ArrayList<Point>>();
		Set<Integer> sk = new HashSet<Integer>();
		for(int i=0;i<x.size();++i)
		{
			Point px = x.get(i);
			Point py = y.get(i);
			int kx = 0;
			if(XR.contains(px))
			{
				kx = XR.route(px);
				sk.add(kx);
				if(mout.containsKey(kx))
				{
					mout.get(kx).add(px);
				}
				else{
					Set<Point> o = new HashSet<Point>();
					o.add(px);
					mout.put(kx, o);
				}
			}
			if(XR.contains(py))
			{
				int ky = XR.route(py);
				sk.add(ky);
				if(min.containsKey(ky))
				{
					min.get(ky).add(px);
					mPrein.get(ky).add(py);
				}
				else{
					ArrayList<Point>tin = new ArrayList<Point>();
					tin.add(px);
					ArrayList<Point>tprein = new ArrayList<Point>();
					tprein.add(py);
					min.put(ky, tin);
					mPrein.put(ky, tprein);
				}
			}
		}
		int delta = 0;
		for(int k : sk)
		{
			Set<Point>out = new HashSet<Point>();
			ArrayList<Point> in = new ArrayList<Point>();
			ArrayList<Point> prein = new ArrayList<Point>();
			if(mout.containsKey(k))
				out = mout.get(k);
			if(min.containsKey(k))
			{
				in = min.get(k);
				prein = mPrein.get(k);
			}
			delta += evaluateVioRoute(k, out, in, prein);
		}
		return delta;
	}

	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

}
