package localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows;

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
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;

public class CEarliestArrivalTimeVRWithBuffer implements IConstraintVR {
	
	private HashMap<Point, Integer> latestAllowedArrivalTime;
	private VarRoutesVR XR;
	private EarliestArrivalTimeVR eat;
	private int violations;
	private HashMap<Point, Double> earliestArrivalTime;
	private HashMap<Point, Integer> vio;
	Map<Long,Integer> buffer;
	ArrayList<ArrayList<Long>> routeBuffer;
	// temporary data structure
	private HashMap<Point, Point> t_next;
	
	public static int ONE_POINT_MOVE = 0;
	public static int TWO_POINT_MOVE = 1;
	public static int OR_OPT_MOVE1 = 2;
	public static int CROSS_EXCHANGE = 3;
	long BASE ;
	public static int MAX_POINT = 1000;
	public static int MAX_MOVE_NUM = 100;
	
	public CEarliestArrivalTimeVRWithBuffer(EarliestArrivalTimeVR eat, HashMap<Point, Integer> latestAllowedArrivalTime){
		this.eat = eat;
		this.latestAllowedArrivalTime = latestAllowedArrivalTime;
		
		earliestArrivalTime = eat.getEarliestArrivalTime();
		XR = eat.getVarRouteVR();
		t_next = new HashMap<Point,Point>();
		vio = new HashMap<Point,Integer>();
		
		getVRManager().post(this);
	}
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return XR.getVRManager();
	}

	private int computeViolations(Point i) {
		double v = earliestArrivalTime.get(i) <= latestAllowedArrivalTime.get(i) ? 0
				: (earliestArrivalTime.get(i) - latestAllowedArrivalTime.get(i));
		return (int) Math.ceil(v);
	}

	private int computeViolations(Point i, double arrivalTime) {
		double v = arrivalTime <= latestAllowedArrivalTime.get(i) ? 0 : 
			(arrivalTime - latestAllowedArrivalTime.get(i));
		return (int) Math.ceil(v);
	}
	
	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		violations = 0;
		vio = new HashMap<Point, Integer>();
		t_next = new HashMap<Point, Point>();
		buffer = new TreeMap<Long,Integer>();
		routeBuffer = new ArrayList<>();
		for(int i=0;i<=XR.getNbRoutes();++i)
			routeBuffer.add(new ArrayList<Long>());
		
		BASE = MAX_POINT *2;
		int nr = XR.getNbRoutes();
		for(int k = 1; k <= nr; ++k)
		{
			Point s = XR.getStartingPointOfRoute(k);
			do{		
				int svio  = computeViolations(s);
				violations += svio;
				vio.put(s, svio);
				//System.out.println(s+"("+svio+","+eat.getEarliestArrivalTime(s)+") ");
				if(XR.isTerminatingPoint(s))
					break;
				s  = XR.next(s);
			}while(true);
			//System.out.println();
		}
	
	}

	private void propagate(int k)
	{
		for(Point v = XR.getStartingPointOfRoute(k); v!= XR.getTerminatingPointOfRoute(k); v = XR.oldNext(v))
		{
			violations -= vio.get(v);
			vio.put(v,computeViolations(v));
			violations += vio.get(v);
		}
		for(Long h : routeBuffer.get(k))
			buffer.remove(h);
		routeBuffer.get(k).clear();
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
		System.out.println("CEarliestArrivalTimeVRWithBuffer::propagateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("CEarliestArrivalTimeVRWithBuffer::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
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
		Point v = begin;
		int delta = 0;
		double dt = eat.getEarliestArrivalTime(v)
				+ eat.getServiceDuration(v);

		while (v != end) {
			Point nv = t_next.get(v);
			double at = dt + eat.getTravelTime(v,nv);
			delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv, at);
			dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);
			v = nv;
		}
		return delta;
	}
	int calDeltaRoute(Point beginRoute,Point endRoute)
	{
		Point v = beginRoute;
		int delta = 0;
		double dt = eat.getEarliestAllowedArrivalTime(v)
				+ eat.getServiceDuration(v);

		while (v != endRoute) {
			Point nv = t_next.get(v);
			double at = dt + eat.getTravelTime(v,nv);
			delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv, at);
			dt = Math.max(at, eat.getEarliestAllowedArrivalTime(nv)) + eat.getServiceDuration(nv);
			v = nv;
		}
		return delta;
	}
	long calHash(ArrayList<Integer>var, int moveIndex)
	{
		long h= 0;
		for(int i=0;i<var.size(); ++i)
			h = h*BASE + var.get(i);
		h = h*MAX_MOVE_NUM + moveIndex;
		return h;
	}
	@Override
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		ArrayList<Integer> var = new ArrayList<>();
		var.add(x.getID());
		var.add(y.getID());
		long h = calHash(var, ONE_POINT_MOVE);
		if(buffer.containsKey(h))
			return buffer.get(h);
		int kx = XR.route(x);
		int ky = XR.route(y);
		int delta = 0;
		if(kx==ky)
		{
			if(XR.index(x) < XR.index(y))
			{
				Point px = XR.prev(x);
				Point nx = XR.next(x);
				getSegment(XR.startPoint(kx), px);
				t_next.put(px, nx);
				getSegment(nx, y);
				t_next.put(y, x);
				Point ny = XR.next(y);
				t_next.put(x,ny);
				getSegment(ny, XR.endPoint(kx));
				delta = calDeltaRoute(XR.startPoint(kx), XR.endPoint(kx));
			}
			else{
				Point px = XR.prev(x);
				Point nx = XR.next(x);
				Point ny = XR.next(y);
				if(px==y)
				{
					delta =  0;
				}
				else{
					getSegment(XR.startPoint(kx),y);
					t_next.put(y,x);
					t_next.put(x, ny);
					getSegment(ny, px);
					t_next.put(px,nx);
					getSegment(nx, XR.endPoint(kx));
					delta = calDeltaRoute(XR.startPoint(kx), XR.endPoint(kx));
				}
			}
		}
		else{
			Point px = XR.prev(x);
			Point nx = XR.next(x);
			Point ny = XR.next(y);
			getSegment(XR.startPoint(kx), px);
			t_next.put(px,nx);
			getSegment(nx, XR.endPoint(kx));
			getSegment(XR.startPoint(ky), y);
			t_next.put(y,x);
			t_next.put(x,ny);
			getSegment(ny, XR.endPoint(ky));
			delta = calDeltaRoute(XR.startPoint(kx), XR.endPoint(kx));
			delta += calDeltaRoute(XR.startPoint(ky), XR.endPoint(ky));
		}
		buffer.put(h, delta);
		routeBuffer.get(kx).add(h);
		if(ky!=kx)
			routeBuffer.get(ky).add(h);
		return delta;
	}

	// move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	@Override
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		
		ArrayList<Integer> var = new ArrayList<>();
		var.add(x.getID());
		var.add(y.getID());
		long h = calHash(var, TWO_POINT_MOVE);
		if(buffer.containsKey(h))
			return buffer.get(h);
		int k = XR.route(x);

		Point px = XR.prev(x);
		Point nx = XR.next(x);
		Point py = XR.prev(y);
		Point ny = XR.next(y);
		int delta = 0;
		if(ny==x)
		{
			getSegment(XR.startPoint(k), py);
			t_next.put(py, x);
			t_next.put(x,y);
			t_next.put(y,nx);
			getSegment(nx, XR.endPoint(k));
			delta = calDeltaSegment(XR.startPoint(k), XR.endPoint(k));
		}
		else{
			getSegment(XR.startPoint(k), py);
			t_next.put(py, x);
			t_next.put(x,ny);
			getSegment(ny, px);
			t_next.put(px,y);
			t_next.put(y, nx);
			getSegment(nx, XR.endPoint(k));
			delta = calDeltaSegment(XR.startPoint(k), XR.endPoint(k));
		}
		buffer.put(h, delta);
		routeBuffer.get(k).add(h);
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
		
		t_next.put(x,y);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		
		t_next.put(nx,ny);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		t_next.put(XR.startPoint(ky), t_next.get(XR.endPoint(kx)));
		t_next.put(XR.next(XR.startPoint(ky)), XR.endPoint(kx));
		int delta  = 0;
		delta += calDeltaSegment(x, XR.endPoint(kx));
		delta += calDeltaRoute(XR.startPoint(ky), XR.endPoint(ky));
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
		
		Point startX = XR.startPoint(kx);
		Point endX  = XR.endPoint(kx);
		Point startY = XR.startPoint(ky);
		Point endY = XR.endPoint(ky);
		
		t_next.put(startX, t_next.get(startY));
		t_next.put(XR.next(startX), endX);
		t_next.put(startY, t_next.get(endX));
		
		int delta  = 0;
		delta += calDeltaRoute(startX,endX);
		delta += calDeltaRoute(startY,endY);
		
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
		
		t_next.put(x,y);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		
		t_next.put(ny, nx);
		getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));
		
		Point startX = XR.startPoint(kx);
		Point endX  = XR.endPoint(kx);
		Point startY = XR.startPoint(ky);
		Point endY = XR.endPoint(ky);
		
		t_next.put(startY, t_next.get(endY));
		
		
		t_next.put(XR.next(startY), endX);
		if(ny==endY)
		{
			if(endX == nx)
			{
				t_next.put(startY,endY);
			}
			else{
				t_next.put(startY, nx);
				t_next.put(XR.prev(endX), endY);
			}
		}
		else{
			t_next.put(startY, t_next.get(endY));
			if(endX == nx)
			{
				t_next.put(ny, endY);
			}
			else{
				t_next.put(XR.prev(endX), endY);
			}
		}
		
		
		int delta  = 0;
		
		delta += calDeltaSegment(x, endX);
		delta += calDeltaRoute(startY,endY);
		
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

		Point startX = XR.startPoint(kx);
		Point endX  = XR.endPoint(kx);
		Point startY = XR.startPoint(ky);
		Point endY = XR.endPoint(ky);
		
		t_next.put(startX, t_next.get(startY));
		t_next.put(XR.next(startX),endX);
		
		if(ny==endY)
		{
			if(endX == nx)
			{
				t_next.put(startY,endY);
			}
			else{
				t_next.put(startY, nx);
				t_next.put(XR.prev(endX), endY);
			}
		}
		else{
			t_next.put(startY, t_next.get(endY));
			if(endX == nx)
			{
				t_next.put(ny, endY);
			}
			else{
				t_next.put(XR.prev(endX), endY);
			}
		}
		int delta  = 0;
		delta += calDeltaRoute(startX,endX);
		delta += calDeltaRoute(startY, endY);
		
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

		t_next.put(x, ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		
		t_next.put(y, nx);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));

		Point endX = XR.endPoint(kx);
		Point endY = XR.endPoint(ky);
		
		if(endX == nx)
		{
			t_next.put(y,endY);
		}
		else{
			t_next.put(XR.prev(endX),endY);
		}
		if(endY == ny)
		{
			t_next.put(x,endX);
		}
		else{
			t_next.put(XR.prev(endY),endX);
		}
		int delta  = 0;
		delta += calDeltaSegment(y, endY);
		delta += calDeltaSegment(x, endX);
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
		
		t_next.put(y, nx);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));
		
		Point startX = XR.startPoint(kx);
		Point endX = XR.endPoint(kx);
		Point startY = XR.startPoint(ky);
		Point endY = XR.endPoint(ky);
		
		if(ny==endY)
		{
			t_next.put(startX, x);
		}
		else{
			t_next.put(startX, XR.prev(endY));
		}
		t_next.put(XR.next(startX), endX);
		
		if(nx==endX)
		{
			t_next.put(y,endY);
		}
		else{
			t_next.put(XR.prev(endX),endY);
		}
		int delta  = 0;
		delta += calDeltaRoute(startX,endX);
		delta += calDeltaSegment(y,endY);

		return delta;
	}

	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
	public int evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		t_next.put(x, ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		t_next.put(nx, y);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getRevSegment(y,XR.getStartingPointOfRoute(ky));
		
		Point startX = XR.startPoint(kx);
		Point endX = XR.endPoint(kx);
		Point startY = XR.startPoint(ky);
		Point endY = XR.endPoint(ky);
		
		if(ny == endY)
		{
			t_next.put(x, endX);
		}
		else{
			t_next.put(XR.prev(endY), endX);
		}
		if(nx == endX)
		{
			t_next.put(startY, y);
		}
		else{
			t_next.put(startY, XR.prev(endX));
		}
		t_next.put(XR.next(startY), endY);
		int delta  = 0;
		delta += calDeltaRoute(startY,endY);
		delta += calDeltaSegment(x, endX);
		
		return delta;
	}

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (next[x],y)
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
		
		Point startX = XR.startPoint(kx);
		Point endX = XR.endPoint(kx);
		Point startY = XR.startPoint(ky);
		Point endY = XR.endPoint(ky);
		
		if(ny == endY)
		{
			t_next.put(startX, x);
		}
		else{
			t_next.put(startX, XR.prev(endY));
		}
		t_next.put(XR.next(startX), endX);
		
		if(nx == endX)
		{
			t_next.put(startY, y);
		}
		else{
			t_next.put(startY, XR.prev(endX));
		}
		t_next.put(XR.next(startY), endY);
		int delta  = 0;
		delta += calDeltaRoute(startX,endX);
		delta += calDeltaRoute(startY,endY);
		
		return delta;
	}
	 // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
	@Override
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		ArrayList<Integer> var = new ArrayList<>();
		var.add(x1.getID());
		var.add(x2.getID());
		var.add(y.getID());
		long h = calHash(var, OR_OPT_MOVE1);
		if(buffer.containsKey(h))
			return buffer.get(h);
		int kx = XR.route(x1);
		int ky = XR.route(y);
		Point px1 = XR.prev(x1);
		Point nx2 = XR.next(x2);
		Point ny = XR.next(y);
		
		t_next.put(px1,nx2);
		getSegment(nx2, XR.getTerminatingPointOfRoute(kx));
		
		t_next.put(y, x1);
		getSegment(x1,x2);
		t_next.put(x2,ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		int delta  = 0;
		delta += calDeltaSegment(px1, XR.getTerminatingPointOfRoute(kx));
		delta += calDeltaSegment(y, XR.getTerminatingPointOfRoute(ky));
		buffer.put(h, delta);
		routeBuffer.get(kx).add(h);
		routeBuffer.get(ky).add(h);
		return delta;
	}

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
	public int evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x1);
		int ky = XR.route(y);
		Point px1 = XR.prev(x1);
		Point nx2 = XR.next(x2);
		Point ny = XR.next(y);
		
		t_next.put(px1, nx2);
		getSegment(nx2, XR.getTerminatingPointOfRoute(kx));
		
		t_next.put(y, x2);
		getRevSegment(x2,x1);
		t_next.put(x1, ny);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		int delta  = 0;
		delta += calDeltaSegment(px1, XR.getTerminatingPointOfRoute(kx));
		delta += calDeltaSegment(y, XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}

	 // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,z) and (next[y], next[x]) and(y, next[z])
 
	public int evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		
		t_next.put(x,z);
		getRevSegment(z,ny);
		t_next.put(ny,nx);
		getSegment(nx,y);
		t_next.put(y,nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(x, XR.getTerminatingPointOfRoute(k));
		return delta;
	}

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
		delta += calDeltaRoute(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));
		return delta;
	}

	@Override
	public int evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

	
		t_next.put(x,y);
		getRevSegment(y,nx);
		t_next.put(nx, z);
		getRevSegment(z,ny);
		t_next.put(ny,nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(x, XR.getTerminatingPointOfRoute(k));
		
		return delta;
	}

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
		delta += calDeltaRoute(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));

		return delta;
	}

	@Override
	public int evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

		t_next.put(x, ny);
		getSegment(ny,z);
		t_next.put(z,nx);
		getSegment(nx,y);
		t_next.put(y,nz);
		getSegment(nz,XR.getTerminatingPointOfRoute(k));
		
		int delta  = 0;
		delta += calDeltaSegment(x, XR.getTerminatingPointOfRoute(k));
		
		return delta;
	}

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
		delta += calDeltaRoute(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));

		return delta;
	}

	@Override
	public int evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		Point nz = XR.next(z);

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
		delta += calDeltaRoute(XR.getTerminatingPointOfRoute(k), XR.getStartingPointOfRoute(k));
		
		return delta;
	}

	@Override
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		ArrayList<Integer> var = new ArrayList<>();
		var.add(x1.getID());
		var.add(x2.getID());
		var.add(y1.getID());
		var.add(y2.getID());
		long h = calHash(var, CROSS_EXCHANGE);
		if(buffer.containsKey(h))
			return buffer.get(h);
		
		Point nx1 = XR.next(x1);
		Point ny1 = XR.next(y1);
		Point nx2 = XR.next(x2);
		Point ny2 = XR.next(y2);
		int k1 = XR.route(x1);
		int k2 = XR.route(x2);
		
		t_next.put(x1, nx2);
		getSegment(nx2, y2);
		t_next.put(y2,ny1);
		getSegment(ny1, XR.getTerminatingPointOfRoute(k1));
		
		t_next.put(x2, nx1);
		getSegment(nx1, y1);
		t_next.put(y1, ny2);
		getSegment(ny2, XR.getTerminatingPointOfRoute(k2));
		
		int delta  = 0;
		delta += calDeltaSegment(x1, XR.getTerminatingPointOfRoute(k1));
		delta += calDeltaSegment(x2, XR.getTerminatingPointOfRoute(k2));
		buffer.put(h, delta);
		routeBuffer.get(k1).add(h);
		routeBuffer.get(k2).add(h);
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
		// TODO Auto-generated method stub
		int k = XR.route(y);
		Point ny = XR.next(y);
		t_next.put(y, x);
		t_next.put(x, ny);
		getSegment(ny, XR.getTerminatingPointOfRoute(k));
		int delta = 0;
		
		delta += calDeltaSegment(y, XR.getTerminatingPointOfRoute(k));
		return delta;
	}

	@Override
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point px = XR.prev(x);
		Point nx = XR.next(x);
		t_next.put(px,nx);
		getSegment(nx, XR.getTerminatingPointOfRoute(k));
		
		return calDeltaSegment(px, XR.getTerminatingPointOfRoute(k));
	}

	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		System.out.println("CEarliestArrivalTimeVRWithBuffer::evaluateAddTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		return 0;
	}

	public int evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		System.out.println("CEarliestArrivalTimeVRWithBuffer::evaluateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
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
		//System.out.println(k+","+KPointsMove.array2String(in)+","+KPointsMove.array2String(preIn));
		//System.out.print("Out : ");
		//for(Point p : out)
			//System.out.print(p.getID()+" ");
		//System.out.println();
		int delta = 0;
		Point s = XR.getStartingPointOfRoute(k);
		Point pre = s;
		delta -= vio.get(s);
		while(!XR.isTerminatingPoint(s))
		{
			Point ns = XR.next(s);
			delta -= vio.get(ns);
			if(!out.contains(ns))
			{
				t_next.put(pre, ns);
				pre = ns;
			}
			s = ns;
		}
		for(int i = in.size()-1; i >= 0; --i)
		{
			Point inp = in.get(i);
			Point preP = preIn.get(i);
			Point nex = t_next.get(preP);
			t_next.put(preP, inp);
			t_next.put(inp, nex);
		}
		Point v = XR.getStartingPointOfRoute(k);
		double dt = eat.getEarliestArrivalTime(v)
				+ eat.getServiceDuration(v);

		while (v != XR.endPoint(k)) {
			//System.out.println(v);
			Point nv = t_next.get(v);
			double at = dt + eat.getTravelTime(v,nv);
			//delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv, at);
			dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);
			v = nv;
		}
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
		//System.out.println(delta);
		return delta;
	}
	@Override
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		// TODO Auto-generated method stub
		
	}

}
