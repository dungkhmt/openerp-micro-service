/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 05/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public interface InvariantVR {
	/*
	 * return the VRPManager
	 */
	public VRManager getVRManager();

	/*
	 * Perform moves and propagate impact
	 */

	/*
	 * this method is called when the manager is closed implementing classes
	 * implement this method for initializing the data structure maPointained
	 */
	public void initPropagation();

	// x is before y on the same route
	// remove (x, next[x]) and (y,next[y])
	// add (x,y) and (next[x],next[y])
	void propagateTwoOptMoveOneRoute(Point x, Point y);

	// move of type a [Groer et al., 2010]
	// move customer x to from route of x to route of y; insert x Pointo the
	// position between y and next[y]
	// x and y are not the depot
	void propagateOnePointMove(Point x, Point y);

	// move of type b [Groer et al., 2010]
	// x and y are on the same route and are not the depots, y locates before x
	// on the route
	// remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
	// insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	void propagateTwoPointsMove(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,y) and (next[x],next(y))
	void propagateTwoOptMove1(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (y,x) and (next[x],next(y))
	void propagateTwoOptMove2(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,y) and (next[y],next(x))
	void propagateTwoOptMove3(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (y,x) and (next[y],next(x))
	void propagateTwoOptMove4(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,next[y]) and (y,next[x])
	void propagateTwoOptMove5(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (next[y],x) and (y,next[x])
	void propagateTwoOptMove6(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,next[y]) and (next[x],y)
	void propagateTwoOptMove7(Point x, Point y);

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (next[y],x) and (next[x],y)
	void propagateTwoOptMove8(Point x, Point y);

	// move of type d [Groer et al., 2010]
	// move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the
	// route containing y
	// remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
	// add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
	void propagateOrOptMove1(Point x1, Point x2, Point y);

	// move of type d [Groer et al., 2010]
	// move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the
	// route containing y
	// remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
	// add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
	void propagateOrOptMove2(Point x1, Point x2, Point y);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,z) and (next[y], next[x]) and(y, next[z])
	void propagateThreeOptMove1(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (z,x) and (next[x], next[y]) and(next[z],y)
	void propagateThreeOptMove2(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,y) and (next[x], z) and(next[y], next[z])
	void propagateThreeOptMove3(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (y,x) and (z,next[x]) and(next[z], next[y])
	void propagateThreeOptMove4(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,next[y]) and (z,next[x]) and(y, next[z])
	void propagateThreeOptMove5(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (next[y],x) and (next[x],z) and(next[z],y)
	void propagateThreeOptMove6(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,next[y]) and (z,y) and(next[x], next[z])
	void propagateThreeOptMove7(Point x, Point y, Point z);

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (next[y],x) and (y,z) and(next[z], next[x])
	void propagateThreeOptMove8(Point x, Point y, Point z);

	// move of type g [Groer et al., 2010]
	// x1 and y1 are on the same route, x1 is before y1
	// x2 and y2 are on the same route, x2 is before y2
	// remove (x1,next[x1]) and (y1, next[y1])
	// remove (x2, next[x2]) and (y2, next[y2])
	// insert (x1, next[x2]) and (y2, next[y1])
	// insert (x2, next[x1]) and (y1, next[y2])
	void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2);

	/*
	 * // new proposed moves // x1, y1 are on route k1, x1 is before y1 // x2,
	 * y2 are on route k2 (k2 != k1), x2 is before y2 // remove x1, y1 from
	 * route k1 // insert x1 between x2 and next[x2] // insert y1 between y2 and
	 * next[y2]
	 * 
	 * void propagateTwoPoPointsDifferentRouteMove(Point x1, Point y1, Point x2,
	 * Point y2);
	 * 
	 * // new proposed moves // x1, y1, z1, t1 are on route k1 // x2, y2, z2, t2
	 * are on route k2 // k1 != k2 // x1, y1, x2, y2, z1, t1, z2, t2 are
	 * alldifferent // remove x1, y1 from route k1 // remove x2, y2 from route
	 * k2 // insert x1 between z2 and next[z2] // insert y1 between t2 and
	 * next[t2] // insert x2 between z1 and next[z1] // insert y2 between t1 and
	 * next[t1]
	 * 
	 * void propagateTwoPoPointsExchangeMove(Point x1, Point y1, Point x2, Point
	 * y2, Point z1, Point t1, Point z2, Point t2);
	 */
	// remove x1, x2 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2);

	// remove x1, x2, x3 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	public void propagateThreePointsMove(Point x1, Point x2, Point x3,
			Point y1, Point y2, Point y3);

	// remove x1, x2, x3, x4 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	// re-insert x4 between y4 and next[y4]
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4);

	// remove x[0...x.size()-1] from current routes
	// re-insert x[i] right-after y[i], forall i = 0,...,x.size()-1
	// application: Large Neighborhood Search
	// if y[i] = CBLSVR.NULL_POINT, then x[i] is removed from current routes
    void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y);
    
    // add the Point x between y and next[y]
    public void propagateAddOnePoint(Point x, Point y);
    
    // remove the Point x from its current route
    public void propagateRemoveOnePoint(Point x);
    
    // add the Point x1 between y1 and next[y1]
    // add the Point x2 between y2 and next[y2]
    // y1 and y2 are on the same route and index[y1] < index[y2]
    // if y1 == y2, the Point x2 is added right-after the Point x1.
    public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2);
    
    // remove two points x1 and x2 from its current route
    // x1 and x2 are on the same route and index[x1] < index[x2]
    public void propagateRemoveTwoPoints(Point x1, Point x2);
    
    public void propagateAddRemovePoints(Point x, Point y, Point z);
    
    
    
    public String name();
}
