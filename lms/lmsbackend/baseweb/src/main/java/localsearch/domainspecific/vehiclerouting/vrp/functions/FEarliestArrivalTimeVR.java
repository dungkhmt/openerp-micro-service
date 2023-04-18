package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;

public class FEarliestArrivalTimeVR implements IFunctionVR {
	VarRoutesVR XR;
	EarliestArrivalTimeVR eat;
	HashMap<Point, Integer> earliestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	double earliestArrivalTime;
	HashMap<Point,Point> t_next;
	Point calPoint;
	public FEarliestArrivalTimeVR(EarliestArrivalTimeVR eat, Point v){
		this.XR = eat.getVarRouteVR();
		this.eat = eat;
		earliestAllowedArrivalTime = eat.getEarliestAllowedArrivalTime();
		serviceDuration = eat.getServiceDuration();
		earliestArrivalTime = 0;
		t_next = new HashMap<Point,Point>();
		this.calPoint = v;
		getVRManager().post(this);
	}
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return XR.getVRManager();
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
		t_next = new HashMap<Point,Point>();
	}

	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2,
			Point y2) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}
	
	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		//System.out.println("FEarliestArrivalTimeVR::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		//System.out.println("FEarliestArrivalTimeVR::propagateRemoveTwoPoints HAS NOT BEEN IMPLEMENTED YET");
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "FEaliestArrivalTimeVR";
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return earliestArrivalTime;
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
	
	
	double calDeltaSegment(Point begin,Point end)
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
			if(at < eat.getEarliestAllowedArrivalTime(nv))
				at = eat.getEarliestAllowedArrivalTime(nv);
			if(nv==calPoint)
				return at - earliestArrivalTime;
			dt = at + eat.getServiceDuration(nv);
			v = nv;
		}
		return 0;
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	
	// move of type a [Groer et al., 2010]
    // move customer x to from route of x to route of y; insert x into the position between y and next[y]
    // x and y are not the depot
	@Override
	public double evaluateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kv!=kx&&kv!=ky)
			return 0;
		if(kx==ky)
		{
			Point v,nv;
			if (XR.getIndex(x) < XR.getIndex(y)) {
				v = XR.prev(x);
				nv = XR.next(x);
				t_next.put(v,nv);
				getSegment(nv, y);
				t_next.put(y,x);
				t_next.put(x,XR.next(y));
				getSegment(XR.next(y), XR.getTerminatingPointOfRoute(kx));
				v = XR.prev(x);
			} else {
				v = y;
				t_next.put(v, x);
				t_next.put(x, XR.next(v));
				if(y!= XR.prev(x))
					getSegment(XR.next(y), XR.prev(x));
				t_next.put(XR.prev(x),XR.next(x));
				getSegment(XR.next(v),XR.getTerminatingPointOfRoute(kx));
				v = XR.prev(y);
			}
			double dt = eat.getEarliestArrivalTime(v)
					+ eat.getServiceDuration(v);
			while (v != XR.getTerminatingPointOfRoute(kx)) {
				nv = t_next.get(v);
				double at = dt + eat.getTravelTime(v,nv);
				if(at < eat.getEarliestAllowedArrivalTime(nv))
				{
					at = eat.getEarliestAllowedArrivalTime(nv);
				}
				if(nv == calPoint)
					return at - earliestArrivalTime;
				dt = at + eat.getServiceDuration(nv);
				v = nv;
			}
			return 0;
		}
		else{
			Point v,nv;
			if(kx == kv)
			{
				v = XR.prev(x);
				double dt = eat.getEarliestArrivalTime(v)
						+ eat.getServiceDuration(v);
				nv = XR.next(x);
				double at = dt + eat.getTravelTime(v, nv);
				if(at < eat.getEarliestAllowedArrivalTime(nv))
					at = eat.getEarliestAllowedArrivalTime(nv);
				if(nv == calPoint)
					return at - earliestArrivalTime;
				v = nv;
				while(v!=XR.getTerminatingPointOfRoute(kx))
				{
					nv = XR.next(v);
					at = at + eat.getServiceDuration(v) + eat.getTravelTime(v, nv);
					if(at < eat.getEarliestAllowedArrivalTime(nv))
						at = eat.getEarliestAllowedArrivalTime(nv);
					if(nv == calPoint)
						return at - earliestArrivalTime;
					v = nv;
				}
				return 0;
			}
			else{
				v = y;
				double dt = eat.getEarliestArrivalTime(v)
						+ eat.getServiceDuration(v);
				nv = x;
				double at = dt + eat.getTravelTime(v, nv);
				if(at < eat.getEarliestAllowedArrivalTime(nv))
					at = eat.getEarliestAllowedArrivalTime(nv);
				if(x == calPoint)
					return at - earliestArrivalTime;
				v = nv;
				nv = XR.next(y);
				at = dt + eat.getTravelTime(v, nv);
				if(at < eat.getEarliestAllowedArrivalTime(nv))
					at = eat.getEarliestAllowedArrivalTime(nv);
				if(x == calPoint)
					return at - earliestArrivalTime;
				v = nv;
				while(v!=XR.getTerminatingPointOfRoute(ky))
				{
					nv = XR.next(v);
					at = at + eat.getServiceDuration(v) + eat.getTravelTime(v, nv);
					if(at < eat.getEarliestAllowedArrivalTime(nv))
						at = eat.getEarliestAllowedArrivalTime(nv);
					if(nv == calPoint)
						return at - earliestArrivalTime;
					v = nv;
				}
				return 0;
			}
			
		}
	}
	// move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	@Override
	public double evaluateTwoPointsMove(Point x, Point y) {
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kv!=kx || kv != ky)
			return 0;
		if(XR.index(x) > XR.index(y))
		{
			Point tmp = x;
			x = y;
			y = tmp;
		}
		if(XR.next(x) == y)
		{
			t_next.put(y, x);
			t_next.put(x, XR.next(y));
			getSegment(XR.next(y), XR.endPoint(kx));
			return calDeltaSegment(y, XR.endPoint(kx));
		}
		else{
			Point px = XR.prev(x);
			Point nx = XR.next(x);
			Point py = XR.prev(y);
			Point ny = XR.next(y);
			t_next.put(px, y);
			t_next.put(y, nx);
			t_next.put(py, x);
			t_next.put(x, ny);
			getSegment(nx, py);
			getSegment(ny, XR.endPoint(kx));
			return calDeltaSegment(px, XR.endPoint(kx));
		}
	}

	// move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next(y))
	@Override
	public double evaluateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub

		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x1);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		int kx = XR.route(x1);
		int ky = XR.route(y);
		int kv = XR.route(calPoint);
		if(kx!=kv&&ky!=kv)
			return 0;
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

	@Override
	public double evaluateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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
	public double evaluateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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

	@Override
	public double evaluateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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
	public double evaluateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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

	@Override
	public double evaluateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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
	public double evaluateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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

	@Override
	public double evaluateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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
	public double evaluateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
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

	@Override
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		Point nx1 = XR.next(x1);
		Point ny1 = XR.next(y1);
		Point nx2 = XR.next(x2);
		Point ny2 = XR.next(y2);
		int k1 = XR.route(x1);
		int k2 = XR.route(x2);
		int kv = XR.route(calPoint);
		if(k1!=kv&&k2!=kv)
			return 0;
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
		@Override
		public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
			// TODO Auto-generated method stub
			int k1 = XR.route(x1);
			int k2 = XR.route(y1);
			int kv = XR.route(calPoint);
			if(k1!=kv&&k2!=kv)
				return 0;
			Point px1 = XR.prev(x1);
			Point nx1 = XR.next(x1);
			Point px2 = XR.prev(x2);
			Point nx2 = XR.next(x2);
			Point ny1 = XR.next(y1);
			Point ny2 = XR.next(y2);
			
			//System.out.println(x1.getID()+" "+x2.getID()+" "+y1.getID()+" "+y2.getID());
			if(nx1 == x2)
			{
				t_next.put(px1, nx2);
				getSegment(nx2, XR.getTerminatingPointOfRoute(k1));
			}
			else{
				t_next.put(px1, nx1);
				getSegment(nx1, px2);
				t_next.put(px2, nx2);
				getSegment(nx2, XR.getTerminatingPointOfRoute(k1));
			}
			t_next.put(y1, x1);
			t_next.put(x1,ny1);
			getSegment(ny1, y2);
			t_next.put(y2, x2);
			t_next.put(x2, ny2);
			getSegment(ny2, XR.getTerminatingPointOfRoute(k2));
			int delta = 0;
			
			delta += calDeltaSegment(px1, XR.getTerminatingPointOfRoute(k1));
			delta += calDeltaSegment(y1, XR.getTerminatingPointOfRoute(k2));
			//System.out.println("delta = "+delta);
			return delta;
		}

	@Override
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
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
	public double evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
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
	public double evaluateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		int k = XR.route(y);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
		Point ny = XR.next(y);
		t_next.put(y, x);
		t_next.put(x, ny);
		getSegment(ny, XR.getTerminatingPointOfRoute(k));
		int delta = 0;
		
		delta += calDeltaSegment(y, XR.getTerminatingPointOfRoute(k));
		return delta;
	}

	@Override
	public double evaluateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		int k = XR.route(x);
		int kv = XR.route(calPoint);
		if(k!=kv)
			return 0;
		Point px = XR.prev(x);
		Point nx = XR.next(x);
		t_next.put(px,nx);
		getSegment(nx, XR.getTerminatingPointOfRoute(k));
		
		return calDeltaSegment(px, XR.getTerminatingPointOfRoute(k));
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
	}

	@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		int k = XR.route(y1);
		int kv = XR.route(calPoint);
		if(k!=kv){
			System.out.println("FEarliestArrivalTimeVR::evaluateAddTwoPoints calPoint and y1 and y2 are not on the same route");
			return 0;
		}
		Point ny1 = XR.next(y1);
		t_next.put(y1, x1);
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
	
	@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		int k = XR.route(x1);
		int kv = XR.route(calPoint);
		if(k!=kv){
			System.out.println("FEarliestArrivalTimeVR::evaluateAddTwoPoints calPoint and y1 and y2 are not on the same route");
			return 0;
		}
		
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
	
	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		earliestArrivalTime = eat.getEarliestArrivalTime(calPoint);
	}
	private double evaluateDeltaRoute(int k,Set<Point>out,ArrayList<Point>in,ArrayList<Point>preIn)
	{
		Point s = XR.getStartingPointOfRoute(k);
		Point pre = s;
		while(!XR.isTerminatingPoint(s))
		{
			Point ns = XR.next(s);
			if(!out.contains(ns))
			{
				t_next.put(pre, ns);
				pre = ns;
			}
			s = ns;
		}
		for(int i = 0; i < in.size(); ++i)
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
			dt = (at < eat.getEarliestAllowedArrivalTime(nv) ? eat
					.getEarliestAllowedArrivalTime(nv) : at )
					+ eat.getServiceDuration(nv);
			if(nv == calPoint )
				return dt - earliestArrivalTime;
			v = nv;
		}
		return 0;
	}
	@Override
	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub
		for(int i = 0; i < x.size(); ++i)
		{
			if(x.get(i) == calPoint)
				if(y.get(i)==CBLSVR.NULL_POINT)
					return -earliestArrivalTime;
		}
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
			delta += evaluateDeltaRoute(k, out, in, prein);
		}
		return delta;
	}
	@Override
	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		int kx = XR.route(x);
		int kz = XR.route(z);
		int kv = XR.route(calPoint);
		if(kx!=kv&&kz!=kv)
			return 0;
		if(kx!=kz)
		{
			if(kx==kv)
			{
				Point px = XR.prev(x);
				t_next.put(px, XR.next(x));
				getSegment(XR.next(x), XR.endPoint(kx));
				return calDeltaSegment(px, XR.endPoint(kx));
			}
			else{
				t_next.put(z, y);
				Point nz = XR.next(z);
				t_next.put(y, nz);
				getSegment(nz, XR.endPoint(XR.route(z)));
				return calDeltaSegment(z, XR.endPoint(kz));
			}
		}
		else{
			if(XR.getIndex(x) < XR.getIndex(z))
			{
				Point px = XR.prev(x);
				t_next.put(px, XR.next(x));
				getSegment(XR.next(x), XR.endPoint(kx));

				t_next.put(z, y);
				Point nz = XR.next(z);
				t_next.put(y, nz);
				getSegment(nz, XR.endPoint(XR.route(z)));
				return calDeltaSegment(px, XR.endPoint(kx));
			}
			else{
				

				t_next.put(z, y);
				Point nz = XR.next(z);
				t_next.put(y, nz);
				getSegment(nz, XR.endPoint(XR.route(z)));
				Point px = XR.prev(x);
				t_next.put(px, XR.next(x));
				getSegment(XR.next(x), XR.endPoint(kx));
				return calDeltaSegment(z, XR.endPoint(kx));
			}
		}
	}

}
