
package localsearch.domainspecific.vehiclerouting.vrp;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

/* 
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 20/08/2015
 */
public interface IFunctionVR extends InvariantVR{

	/*
	 * return the value of the function
	 */
    double getValue();

    /*
     * query the evaluation of different moves
	 */

    // x is before y on the same route
 	// remove (x, next[x]) and (y,next[y])
 	// add (x,y) and (next[x],next[y])
 	double evaluateTwoOptMoveOneRoute(Point x, Point y);
 	
 	
    // move of type a [Groer et al., 2010]
    // move customer x to from route of x to route of y; insert x into the position between y and next[y]
    // x and y are not depot
    // remove (prev[x],x), (x, next[x]), (y,next[y])
    // insert (prev[x], next[x]), (y,x), (x, next[y])
    double evaluateOnePointMove(Point x, Point y);

    // move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depot, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next[y])
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
    double evaluateTwoPointsMove(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next[y])
    double evaluateTwoOptMove1(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[x],next[y])
    double evaluateTwoOptMove2(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[y],next[x])
    double evaluateTwoOptMove3(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[y],next[x])
    double evaluateTwoOptMove4(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (y,next[x])
    double evaluateTwoOptMove5(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (y,next[x])
    double evaluateTwoOptMove6(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
    double evaluateTwoOptMove7(Point x, Point y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not the depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (next[x],y)
    double evaluateTwoOptMove8(Point x, Point y);

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
    double evaluateOrOptMove1(Point x1, Point x2, Point y);

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
    double evaluateOrOptMove2(Point x1, Point x2, Point y);


    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,z) and (next[y], next[x]) and(y, next[z])
    double evaluateThreeOptMove1(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (z,x) and (next[x], next[y]) and(next[z],y)
    double evaluateThreeOptMove2(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,y) and (next[x], z) and(next[y], next[z])
    double evaluateThreeOptMove3(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (y,x) and (z,next[x]) and(next[z], next[y])
    double evaluateThreeOptMove4(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,next[x]) and(y, next[z])
    double evaluateThreeOptMove5(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (next[x],z) and(next[z],y)
    double evaluateThreeOptMove6(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,y) and(next[x], next[z])
    double evaluateThreeOptMove7(Point x, Point y, Point z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (y,z) and(next[z], next[x])
    double evaluateThreeOptMove8(Point x, Point y, Point z);


    // move of type g [Groer et al., 2010]
    // x1 and y1 are on the same route, x1 is before y1
    // x2 and y2 are on the same route, x2 is before y2
    // remove (x1,next[x1]) and (y1, next[y1])
    // remove (x2, next[x2]) and (y2, next[y2])
    // insert (x1, next[x2]) and (y2, next[y1])
    // insert (x2, next[x1]) and (y1, next[y2])
    double evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2);
/*
    double evaluateTwoPointsDifferentRouteMove(int x1, int y1, int x2, int y2);
	
    double evaluateTwoPointsExchangeMove(int x1, int y1, int x2, int y2, int z1, int t1, int z2, int t2);
*/
	// remove x1, x2 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
    double evaluateTwoPointsMove(Point x1, Point x2, Point y1, Point y2);
    	
	// remove x1, x2, x3 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
    double evaluateThreePointsMove (Point x1, Point x2, Point x3, Point y1, Point y2, Point y3);
    
    
	// remove x1, x2, x3, x4 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	// re-insert x4 between y4 and next[y4]
    double evaluateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1, Point y2, Point y3, Point y4);

    // remove x[0...x.size()-1] from current routes
 	// re-insert x[i] right-after y[i], forall i = 0,...,x.size()-1
 	// application: Large Neighborhood Search
 	// if y[i] = CBLSVR.NULL_POINT, then x[i] is removed from current routes
    double evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y);
     
 // add the point x between y and next[y]
    double evaluateAddOnePoint(Point x, Point y);
    
    // remove the point x from its current route
    double evaluateRemoveOnePoint(Point x);
    
    // add the point x1 between y1 and next[y1]
    // add the point x2 between y2 and next[y2]
    // y1 and y2 are on the same route and index[y1] < index[y2]
    // if y1 == y2, the Point x2 is added right-after the Point x1.
    double evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2);
    
    // remove two points x1 and x2 from its current route
    // x1 and x2 are on the same route and index[x1] < index[x2]
    double evaluateRemoveTwoPoints(Point x1, Point x2);
    
    double evaluateAddRemovePoints(Point x, Point y, Point z);
    
    /*
	 * Perform moves and propagate impact
	 */

	/*
	 * this method is called when the manager is closed
	 * implementing classes implement this method for initializing the data structure maintained
	 */
    /*
	public void initPropagation();

	
    // move of type a [Groer et al., 2010]
    // move customer x to from route of x to route of y; insert x into the position between y and next[y]
    // x and y are not the depot
    void propagateOnePointMove(int x, int y);

    // move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
    void propagateTwoPointsMove(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next(y))
    void propagateTwoOptMove1(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[x],next(y))
    void propagateTwoOptMove2(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[y],next(x))
    void propagateTwoOptMove3(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[y],next(x))
    void propagateTwoOptMove4(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (y,next[x])
    void propagateTwoOptMove5(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (y,next[x])
    void propagateTwoOptMove6(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
    void propagateTwoOptMove7(int x, int y);

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (next[x],y)
    void propagateTwoOptMove8(int x, int y);

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
    void propagateOrOptMove1(int x1, int x2, int y);

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
    void propagateOrOptMove2(int x1, int x2, int y);


    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,z) and (next[y], next[x]) and(y, next[z])
    void propagateThreeOptMove1(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (z,x) and (next[x], next[y]) and(next[z],y)
    void propagateThreeOptMove2(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,y) and (next[x], z) and(next[y], next[z])
    void propagateThreeOptMove3(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (y,x) and (z,next[x]) and(next[z], next[y])
    void propagateThreeOptMove4(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,next[x]) and(y, next[z])
    void propagateThreeOptMove5(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (next[x],z) and(next[z],y)
    void propagateThreeOptMove6(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,y) and(next[x], next[z])
    void propagateThreeOptMove7(int x, int y, int z);

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (y,z) and(next[z], next[x])
    void propagateThreeOptMove8(int x, int y, int z);


    // move of type g [Groer et al., 2010]
    // x1 and y1 are on the same route, x1 is before y1
    // x2 and y2 are on the same route, x2 is before y2
    // remove (x1,next[x1]) and (y1, next[y1])
    // remove (x2, next[x2]) and (y2, next[y2])
    // insert (x1, next[x2]) and (y2, next[y1])
    // insert (x2, next[x1]) and (y1, next[y2])
    void propagateCrossExchangeMove(int x1, int y1, int x2, int y2);
	*/
}
