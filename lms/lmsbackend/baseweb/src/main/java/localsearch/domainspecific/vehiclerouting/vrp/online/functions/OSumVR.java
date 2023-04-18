package localsearch.domainspecific.vehiclerouting.vrp.online.functions;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.online.VRManagerOnline;

import java.util.*;

public class OSumVR implements OFunctionVR {

	private VRManager mgr;
	private ArrayList<OFunctionVR> _functions;
	private double _value;

	public OSumVR(VRManagerOnline mgr) {
		this.mgr = mgr;
		mgr.post(this);
		_functions = new ArrayList<OFunctionVR>();
	}
	public void addPoint(Point p){
		// DO NOTHING, this was done by _functions
	}
	public void add(OFunctionVR f) {
		_functions.add(f);
		//System.out.println(name() + "::add, begin initPropagaton _value = " + _value);
		initPropagation();
		//System.out.println(name() + "::add, after initPropagation, _value = " + _value);
	}
	public OFunctionVR get(int i){
		return _functions.get(i);
	}
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}
	public double getValue(){
		return _value;
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public double evaluateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name()
				+ "::evaluateTwoOptMoveOneRoute NOT IMPEMENTED YET");
		System.exit(-1);
		return 0;
	}

	public double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y){
		double delta = 0;
		for (OFunctionVR f : _functions)
			delta += f.evaluateKPointsMove(x, y);
		return delta;
	}
	// move of type a [Groer et al., 2010]
	// move customer x to from route of x to route of y; insert x into the
	// position between y and next[y]
	// x and y are not depot
	// remove (prev[x],x), (x, next[x]), (y,next[y])
	// insert (prev[x], next[x]), (y,x), (x, next[y])
	public double evaluateOnePointMove(Point x, Point y) {
		double delta = 0;
		for (OFunctionVR f : _functions)
			delta += f.evaluateOnePointMove(x, y);
		return delta;
	}

	// move of type b [Groer et al., 2010]
	// x and y are on the same route and are not the depot, y locates before x
	// on the route
	// remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next[y])
	// insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	public double evaluateTwoPointsMove(Point x, Point y) {
		double delta = 0;
		for (OFunctionVR f : _functions)
			delta += f.evaluateTwoPointsMove(x, y);
		return delta;
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,y) and (next[x],next[y])
	public double evaluateTwoOptMove1(Point x, Point y) {
		double delta = 0;
		for (OFunctionVR f : _functions)
			delta += f.evaluateTwoOptMove1(x, y);
		return delta;
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (y,x) and (next[x],next[y])
	public double evaluateTwoOptMove2(Point x, Point y) {
		double delta = 0;
		for (OFunctionVR f : _functions)
			delta += f.evaluateTwoOptMove2(x, y);
		return delta;
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,y) and (next[y],next[x])
	public double evaluateTwoOptMove3(Point x, Point y) {
		double delta = 0;
		for (OFunctionVR f : _functions)
			delta += f.evaluateTwoOptMove3(x, y);
		return delta;
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (y,x) and (next[y],next[x])
	public double evaluateTwoOptMove4(Point x, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateTwoOptMove4(x, y);
    	return delta;

	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,next[y]) and (y,next[x])
	public double evaluateTwoOptMove5(Point x, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateTwoOptMove5(x, y);
    	return delta;

	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (next[y],x) and (y,next[x])
	public double evaluateTwoOptMove6(Point x, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateTwoOptMove6(x, y);
    	return delta;

	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,next[y]) and (next[x],y)
	public double evaluateTwoOptMove7(Point x, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateTwoOptMove7(x, y);
    	return delta;

	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not the depots
	// remove (x,next[x]) and (y,next[y])
	// insert (next[y],x) and (next[x],y)
	public double evaluateTwoOptMove8(Point x, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateTwoOptMove8(x, y);
    	return delta;

	}

	// move of type d [Groer et al., 2010]
	// move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the
	// route containing y
	// remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
	// add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
	public double evaluateOrOptMove1(Point x1, Point x2, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateOrOptMove1(x1, x2, y);
    	return delta;

	}

	// move of type d [Groer et al., 2010]
	// move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the
	// route containing y
	// remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
	// add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
	public double evaluateOrOptMove2(Point x1, Point x2, Point y){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateOrOptMove2(x1, x2, y);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,z) and (next[y], next[x]) and(y, next[z])
	public double evaluateThreeOptMove1(Point x, Point y, Point z){
    	double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove1(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (z,x) and (next[x], next[y]) and(next[z],y)
	public double evaluateThreeOptMove2(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove2(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,y) and (next[x], z) and(next[y], next[z])
	public double evaluateThreeOptMove3(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove3(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (y,x) and (z,next[x]) and(next[z], next[y])
	public double evaluateThreeOptMove4(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove4(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,next[y]) and (z,next[x]) and(y, next[z])
	public double evaluateThreeOptMove5(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove5(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (next[y],x) and (next[x],z) and(next[z],y)
	public double evaluateThreeOptMove6(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove6(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,next[y]) and (z,y) and(next[x], next[z])
	public double evaluateThreeOptMove7(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove7(x, y, z);
    	return delta;

	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (next[y],x) and (y,z) and(next[z], next[x])
	public double evaluateThreeOptMove8(Point x, Point y, Point z){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreeOptMove8(x, y, z);
    	return delta;

	}

	// move of type g [Groer et al., 2010]
	// x1 and y1 are on the same route, x1 is before y1
	// x2 and y2 are on the same route, x2 is before y2
	// remove (x1,next[x1]) and (y1, next[y1])
	// remove (x2, next[x2]) and (y2, next[y2])
	// insert (x1, next[x2]) and (y2, next[y1])
	// insert (x2, next[x1]) and (y1, next[y2])
	public double evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateCrossExchangeMove(x1, y1, x2, y2);
    	return delta;

	}

	/*
	 * double evaluateTwoPointsDifferentRouteMove(int x1, int y1, int x2, int
	 * y2);
	 * 
	 * double evaluateTwoPointsExchangeMove(int x1, int y1, int x2, int y2, int
	 * z1, int t1, int z2, int t2);
	 */
	// remove x1, x2 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	public double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateTwoPointsMove(x1, x2, y1, y2);
    	return delta;

	}

	// remove x1, x2, x3 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	public double evaluateThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2,
			Point y3){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateThreePointsMove(x1, x2, x3, y1, y2, y3);
    	return delta;

	}

	// remove x1, x2, x3, x4 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	// re-insert x4 between y4 and next[y4]
	public double evaluateFourPointsMove(Point x1, Point x2,Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
    	return delta;

	}

	// add the point x between y and next[y]
	public double evaluateAddOnePoint(Point x, Point y){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateAddOnePoint(x, y);
    	return delta;

	}

	// remove the point x from its current route
	public double evaluateRemoveOnePoint(Point x){
		double delta = 0;
    	for(OFunctionVR f: _functions)
    		delta += f.evaluateRemoveOnePoint(x);
    		return delta;
	
	}

	public double evaluateAddRemovePoints(Point x, Point y, Point z) {
		double delta = 0;
		for(OFunctionVR f: _functions){
			delta += f.evaluateAddRemovePoints(x, y, z);
		}
		return delta;
	};
	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		_value = 0;
		for (OFunctionVR f : _functions){
			_value += f.getValue();
			//System.out.println(name() + "::initPropagation, f = " + f.name() + " = " + f.getValue());
		}
		//System.out.println(name() + "::initPropagation, _value = " + _value);
	}
	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	public void propagateTwoOptMoveOneRoute(Point x, Point y) {
		System.out.println(name() + "::propagateTwoOptMoveOneRoute NOT IMPLEMENTED YET");
		System.exit(-1);
	}

	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y){
		initPropagation();
	}
	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::propagateTwoPointsMove(" + x1 + "," + x2 + "," + y1 + "," + y2 + ")");
		initPropagation();
	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1,
			Point y2, Point y3, Point y4) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		initPropagation();
		//System.out.println(name() + "::propagateAddOnePoint("+ x + "," + y + "), after initPropagation, _functions.sz = " + _functions.size() + ", _value = " + _value);
	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		initPropagation();
	}

	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		initPropagation();
	};
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "OSumVR";
	}

	@Override
	public void updateWhenReachingTimePoint(int t) {
		// TODO Auto-generated method stub
		
		initPropagation();
		//System.out.println(name() + "::updateWhenReacingTimePoint(" + t + "), _value = " + _value);
		//for(int i = 1; i <= _functions.size(); i++)	System.out.println("f["+ i + "] = " +_functions.get(i-1).getValue());
		//if(_functions.size() > 0)System.out.println(_functions.get(0).getVRPManager().getVarRoutesVRP().toString());
	}
	@Override
	public double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double evaluateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		
	}

}
