package localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.moves.KPointsMove;
public class CEarliestArrivalTimeVR implements IConstraintVR {
	
	private HashMap<Point, Integer> latestAllowedArrivalTime;
	private VarRoutesVR XR;
	private EarliestArrivalTimeVR eat;
	private int violations;
	private HashMap<Point, Double> earliestArrivalTime;
	private HashMap<Point, Integer> vio;

	// temporary data structure
	private HashMap<Point, Point> t_next;
	public CEarliestArrivalTimeVR(EarliestArrivalTimeVR eat, HashMap<Point, Integer> latestAllowedArrivalTime){
		this.eat = eat;
		this.latestAllowedArrivalTime = latestAllowedArrivalTime;
		
		earliestArrivalTime = eat.getEarliestArrivalTime();
		XR = eat.getVarRouteVR();
		t_next = new HashMap<Point,Point>();
		vio = new HashMap<Point,Integer>();
		getVRManager().post(this);
	}
	
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return XR.getVRManager();
	}

	private int computeViolations(Point i) {
		double v = earliestArrivalTime.get(i) <= latestAllowedArrivalTime.get(i) ? 0
					: earliestArrivalTime.get(i) - latestAllowedArrivalTime.get(i);
		return (int) Math.ceil(v);
	}

	private int computeViolations(Point i, double arrivalTime) {
		double v = arrivalTime <= latestAllowedArrivalTime.get(i) ? 0 : arrivalTime
				- latestAllowedArrivalTime.get(i);
		return (int) Math.ceil(v);
	}
	
	
	public void initPropagation() {
		// TODO Auto-generated method stub
		violations = 0;
		vio = new HashMap<Point, Integer>();
		t_next = new HashMap<Point, Point>();
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
	}
	
	private void propagateAddPoint(int k)
	{
		for(Point v = XR.getStartingPointOfRoute(k); v!= XR.getTerminatingPointOfRoute(k); v = XR.next(v)){
			violations -= vio.get(v);
			vio.put(v,computeViolations(v));
			violations += vio.get(v);
		}
	}
	
	private void propagateRemovePoint(int k)
	{
		for(Point v = XR.getStartingPointOfRoute(k); v!= XR.getTerminatingPointOfRoute(k); v = XR.oldNext(v)){
			if(XR.route(v) == Constants.NULL_POINT)
				violations -= vio.get(v);
			else{
				violations -= vio.get(v);
				vio.put(v,computeViolations(v));
				violations += vio.get(v);
			}
		}
	}
	
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

	
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		propagate(kx);
	}

	
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int ky = XR.oldRoute(y);
		
		propagate(kx);
		propagate(ky);
	}

	
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x));
		st.add(XR.oldRoute(y));
		st.add(XR.oldRoute(y));
		for(Integer k : st)
			propagate(k);
	}

	
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(x2));
		for(Integer k : st)
			propagate(k);
	}

	
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

	
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		TreeSet<Integer>st = new TreeSet<Integer>();
		st.add(XR.oldRoute(x1));
		st.add(XR.oldRoute(x2));
		st.add(XR.oldRoute(x3));
		st.add(XR.oldRoute(y1));
		st.add(XR.oldRoute(y2));
		st.add(XR.oldRoute(y3));
		for(Integer k : st)
			propagate(k);
	}

	
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

	
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int k = XR.oldRoute(y);
		vio.put(x, 0);
		propagateAddPoint(k);
	}

	
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int k = XR.oldRoute(x);
		propagateRemovePoint(k);
	}
	
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2){
		int k = XR.oldRoute(y1);
		vio.put(x1, 0);
		vio.put(x2, 0);
		propagateAddPoint(k);
	}

	public void propagateRemoveTwoPoints(Point x1, Point x2){
		int k = XR.oldRoute(x1);
		propagateRemovePoint(k);
	}
	
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	
	public String name() {
		// TODO Auto-generated method stub
		return "CEarliestArrivalTimeVR";
	}

	
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
			//System.out.println(v);
			t_next.put(v , XR.prev(v));
			v = XR.prev(v);
		}
	}
	
	int calDeltaSegment(Point begin,Point end)
	{
		//System.out.println("start cal delta");
		Point v = begin;
		int delta = 0;
		double dt = eat.getEarliestArrivalTime(v)
				+ eat.getServiceDuration(v);

		while (v != end) {
			//System.out.println(v);
			Point nv = t_next.get(v);
			double at = dt + eat.getTravelTime(v,nv);
			delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv, at);
			//System.out.println(vio.get(nv)+"  -   "+computeViolations(nv,at));
			dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);

			v = nv;
		}
		return delta;
	}
	
	public int evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		//System.out.println(x+"  "+y);
		//System.out.println(XR.index(x)+"  "+XR.index(y));
		int kx = XR.route(x);
		int ky = XR.route(y);
		int delta = 0;
		if (kx == ky) {
			Point v,nv;
			if (XR.index(x) < XR.index(y)) {
				v = XR.prev(x);
				nv = XR.next(x);
				t_next.put(v,nv);
				v = nv;
				while (v != y) {
					t_next.put(v,XR.next(v));
					v = XR.next(v);
				}
				t_next.put(y,x);
				t_next.put(x,XR.next(y));
				v = XR.next(y);
				while (v != XR.getTerminatingPointOfRoute(kx)) {
					t_next.put(v,XR.next(v));
					v = XR.next(v);
				}
				v = XR.prev(x);
				
			} else {
				v = y;
				if(XR.next(v)!=x)
				{
					t_next.put(v, x);
					t_next.put(x, XR.next(v));
					v = XR.next(y);
					while(v != XR.prev(x)){
						t_next.put(v, XR.next(v));
						v = XR.next(v);
					}
					t_next.put(XR.prev(x),XR.next(x));
					getSegment(XR.next(x), XR.endPoint(kx));
					v = y;
				}
				else{
					return 0;
				}
			}
			double dt = eat.getEarliestArrivalTime(v)
					+ eat.getServiceDuration(v);
			while (v != XR.getTerminatingPointOfRoute(kx)) {
				nv = t_next.get(v);
				double at = dt + eat.getTravelTime(v,nv);
				delta = delta - vio.get(nv);
				delta = delta + computeViolations(nv, at);
				dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
						.getEarliestAllowedArrivalTime(nv) : at )
						+ eat.getServiceDuration(nv);

				v = nv;
			}

		} else {
			Point v,nv;
			// process route kx
			v = XR.prev(x);
			double dt = eat.getEarliestArrivalTime(v)
					+ eat.getServiceDuration(v);
			nv = XR.next(x);
			double at = dt + eat.getTravelTime(v,nv);
			delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv,at);
			v = nv;
			dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);

			while (v != XR.getTerminatingPointOfRoute(kx)) {
				nv = XR.next(v);
				at = dt + eat.getTravelTime(v,nv);
				delta = delta - vio.get(nv);
				delta = delta + computeViolations(nv, at);
				dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
						.getEarliestAllowedArrivalTime(nv) : at )
						+ eat.getServiceDuration(nv);

				v = nv;
			}
			
			// process route ky
			v = y;
			dt = eat.getEarliestArrivalTime(v)
					+ eat.getServiceDuration(v);
			nv = x;
			at = dt + eat.getTravelTime(v,nv);
			delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv,at);
			
			v = x;
			dt = ( at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);
			nv = XR.next(y);
			at = dt + eat.getTravelTime(v,nv);
			delta = delta - vio.get(nv);
			delta = delta + computeViolations(nv,at);
			
			v = nv;
			dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);
			while (v != XR.getTerminatingPointOfRoute(ky)) {
				nv = XR.next(v);
				at = dt + eat.getTravelTime(v,nv);
				delta = delta - vio.get(nv);
				delta = delta + computeViolations(nv, at);
				dt = ( at < eat.getEarliestAllowedArrivalTime(nv) ? eat
						.getEarliestAllowedArrivalTime(nv) : at )
						+ eat.getServiceDuration(nv);

				v = nv;
			}
			
		}
		return delta;
	}

	// move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	
	public int evaluateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.oldRoute(x);
		int delta = 0;
		Point v,nv;
		if (XR.index(x) < XR.index(y)) {
			v = XR.prev(x);
			nv = XR.next(x);
			t_next.put(v,nv);
			v = nv;
			while (v != y) {
				t_next.put(v,XR.next(v));
				v = XR.next(v);
			}
			t_next.put(y,x);
			t_next.put(x,XR.next(y));
			v = XR.next(y);
			while (v != XR.getTerminatingPointOfRoute(kx)) {
				t_next.put(v,XR.next(v));
				v = XR.next(v);
			}
			v = XR.prev(x);
			
		} else {
			v = y;
			if(XR.next(v)!=x)
			{
				t_next.put(v, x);
				t_next.put(x, XR.next(v));
				v = XR.next(y);
				while(v != XR.prev(x)){
					t_next.put(v, XR.next(v));
					v = XR.next(v);
				}
				t_next.put(XR.prev(x),XR.next(x));
				getSegment(XR.next(x), XR.endPoint(kx));
				v = y;
			}
			else{
				return 0;
			}
		}
		double dt = eat.getEarliestArrivalTime(v)
				+ eat.getServiceDuration(v);
		while (v != XR.getTerminatingPointOfRoute(kx)) {
			nv = t_next.get(v);
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

	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next(y))
	
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
		
		int delta  = 0;
		
		delta += calDeltaSegment(x, XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx), XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}

	
	public int evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		t_next.put(y, x);
		
		getRevSegment(x,XR.getStartingPointOfRoute(kx));
		
		
		t_next.put(nx,ny);
		getRevSegment(XR.getTerminatingPointOfRoute(kx),nx);
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		int delta  = 0;
		
		delta += calDeltaSegment(y, XR.getStartingPointOfRoute(kx));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx), XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}

	
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
		
		int delta  = 0;
		
		delta += calDeltaSegment(x, XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky), XR.getTerminatingPointOfRoute(kx));
		
		return delta;
	}

	
	public int evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);
		
		t_next.put(y, x);
		
		getRevSegment(x,XR.getStartingPointOfRoute(kx));
		
		
		t_next.put(ny,nx);
		getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));

		int delta  = 0;
		delta += calDeltaSegment(y, XR.getStartingPointOfRoute(kx));
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky), XR.getTerminatingPointOfRoute(kx));
		
		return delta;
	}

	
	public int evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		Point nx = XR.next(x);
		Point ny = XR.next(y);

		t_next.put(x, ny);
		
		getSegment(ny,XR.getTerminatingPointOfRoute(ky));
		
		
		t_next.put(y, nx);
		//getRevSegment(XR.getTerminatingPointOfRoute(ky),ny);
		getSegment(nx,XR.getTerminatingPointOfRoute(kx));

		int delta  = 0;
		delta += calDeltaSegment(y, XR.getTerminatingPointOfRoute(kx));
		delta += calDeltaSegment(x, XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}

	
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
		
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(ky), XR.getStartingPointOfRoute(kx));
		delta += calDeltaSegment(nx,XR.getTerminatingPointOfRoute(kx));

		return delta;
	}

	
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
		
		int delta  = 0;
		delta += calDeltaSegment(XR.getTerminatingPointOfRoute(kx), XR.getStartingPointOfRoute(ky));
		delta += calDeltaSegment(x, XR.getTerminatingPointOfRoute(ky));
		
		return delta;
	}

	
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

	
	public int evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
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

		return delta;
	}

	
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

	
	public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
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
	
		return delta;
	}

	    // remove x1, x2 from their current routes
		// x1 , x2 in same route , index x1 < index x2
		// y1, y2 in same route , index y1 < index y2
		// route of x1 != route of y1
		// re-insert x1 between y1 and next[y1]
		// re-insert x2 between y2 and next[y2]	
		
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

	
	public int evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int k = XR.route(y);
		Point ny = XR.next(y);
		t_next.put(y, x);
		t_next.put(x, ny);
		vio.put(x, 0);
		getSegment(ny, XR.getTerminatingPointOfRoute(k));
		int delta = 0;
		//System.out.println("tnexxt");
		//for(int i  =0; i < t_next.size(); i++)
			//System.out.println(t_next.get(i));
		delta += calDeltaSegment(y, XR.getTerminatingPointOfRoute(k));
		return delta;
	}

	
	public int evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		Point px = XR.prev(x);
		Point nx = XR.next(x);
		t_next.put(px,nx);
		getSegment(nx, XR.getTerminatingPointOfRoute(k));
		
		return calDeltaSegment(px, XR.getTerminatingPointOfRoute(k));
	}

	public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2){
		int k = XR.route(y1);
		Point ny1 = XR.next(y1);
		t_next.put(y1, x1);
		vio.put(x1, 0);
		vio.put(x2, 0);
		if(y1 != y2){
			t_next.put(x1, ny1);
			getSegment(ny1, y2);
			Point ny2 = XR.next(y2);
			t_next.put(y2, x2);
			t_next.put(x2, ny2);
			getSegment(ny2, XR.getTerminatingPointOfRoute(k));
		}
		else{
			t_next.put(x1, x2);
			t_next.put(x2, ny1);
			getSegment(ny1, XR.getTerminatingPointOfRoute(k));
		}
		return calDeltaSegment(y1, XR.getTerminatingPointOfRoute(k));
	}
	
	public int evaluateRemoveTwoPoints(Point x1, Point x2){
		int k = XR.route(x1);
		Point px1 = XR.prev(x1);
		Point nx1 = XR.next(x1);
		Point px2 = XR.prev(x2);
		Point nx2 = XR.next(x2);
		if(x2 == nx1){
			t_next.put(px1, nx2);
			getSegment(nx2, XR.getTerminatingPointOfRoute(k));
		}
		else{
			t_next.put(px1, nx1);
			getSegment(nx1, px2);
			t_next.put(px2, nx2);
			getSegment(nx2, XR.getTerminatingPointOfRoute(k));
		}
		
		return calDeltaSegment(px1, XR.getTerminatingPointOfRoute(k));
	}
	
	public int evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		return 0;
	}
	
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
