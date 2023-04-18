

/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 05/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public interface IConstraintVR extends InvariantVR {
	/*
	 * return the value of the function
	 */
    public int violations();

    /*
     * query the evaluation of different moves
	 */

    // move of type a [Groer et al., 2010]
    // move customer x to from route of x to route of y; insert x into the position between y and next[y]
    // x and y are not depot
    // remove (prev[x],x), (x, next[x]), (y,next[y])
    // insert (prev[x], next[x]), (y,x), (x, next[y])
    public int evaluateOnePointMove(Point x, Point y);

    // move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depot, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next[y])
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
    public int evaluateTwoPointsMove(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next[y])
    public int evaluateTwoOptMove1(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[x],next[y])
    public int evaluateTwoOptMove2(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[y],next[x])
    public int evaluateTwoOptMove3(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[y],next[x])
    public int evaluateTwoOptMove4(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (y,next[x])
    public int evaluateTwoOptMove5(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (y,next[x])
    public int evaluateTwoOptMove6(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
    public int evaluateTwoOptMove7(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (next[x],y)
    public int evaluateTwoOptMove8(Point x, Point y);

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
    public int evaluateOrOptMove1(Point x1, Point x2, Point y);

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
    public int evaluateOrOptMove2(Point x1, Point x2, Point y);


    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,z) and (next[y], next[x]) and(y, next[z])
    public int evaluateThreeOptMove1(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (z,x) and (next[x], next[y]) and(next[z],y)
    public int evaluateThreeOptMove2(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,y) and (next[x], z) and(next[y], next[z])
    public int evaluateThreeOptMove3(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (y,x) and (z,next[x]) and(next[z], next[y])
    public int evaluateThreeOptMove4(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,next[x]) and(y, next[z])
    public int evaluateThreeOptMove5(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (next[x],z) and(next[z],y)
    public int evaluateThreeOptMove6(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,y) and(next[x], next[z])
    public int evaluateThreeOptMove7(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (y,z) and(next[z], next[x])
    public int evaluateThreeOptMove8(Point x, Point y, Point z);


    // move of type g [Groer et al., 2010]
    // x1 and y1 are on the same route, x1 is before y1
    // x2 and y2 are on the same route, x2 is before y2
    // remove (x1,next[x1]) and (y1, next[y1])
    // remove (x2, next[x2]) and (y2, next[y2])
    // insert (x1, next[x2]) and (y2, next[y1])
    // insert (x2, next[x1]) and (y1, next[y2])
    public int evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2);
/*
	public int evaluateTwoPointsDifferentRouteMove(int x1, int y1, int x2, int y2);
	
    public int evaluateTwoPointsExchangeMove(int x1, int y1, int x2, int y2, int z1, int t1, int z2, int t2);
*/
	// remove x1, x2 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
    public int evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2);
    	
	// remove x1, x2, x3 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
    public int evaluateThreePointsMove (Point x1, Point x2, Point x3, Point y1, Point y2, Point y3);
    
    
	// remove x1, x2, x3, x4 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	// re-insert x4 between y4 and next[y4]
    public int evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1, Point y2, Point y3, Point y4);
    
    // remove x[0...x.size()-1] from current routes
 	// re-insert x[i] right-after y[i], forall i = 0,...,x.size()-1
 	// application: Large Neighborhood Search
 	// if y[i] = CBLSVR.NULL_POINT, then x[i] is removed from current routes
    int evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y);
     
 // add the point x between y and next[y]
    int evaluateAddOnePoint(Point x, Point y);
    
    // remove the point x from its current route
    int evaluateRemoveOnePoint(Point x);
    
    // add the point x1 between y1 and next[y1]
    // add the point x2 between y2 and next[y2]
    // y1 and y2 are on the same route and index[y1] < index[y2]
    // if y1 == y2, the Point x2 is added right-after the Point x1.
    public int evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2);
    
    // remove two points x1 and x2 from its current route
    // x1 and x2 are on the same route and index[x1] < index[x2]
    public int evaluateRemoveTwoPoints(Point x1, Point x2);
    
    int evaluateAddRemovePoints(Point x, Point y, Point z);
}
