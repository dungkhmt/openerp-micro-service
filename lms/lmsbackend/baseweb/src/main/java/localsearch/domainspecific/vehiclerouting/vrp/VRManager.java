package localsearch.domainspecific.vehiclerouting.vrp;

import java.util.ArrayList;
import java.util.Iterator;

import localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com), Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 10/08/2015
 */
public class VRManager {

	private ArrayList<InvariantVR> invariants;
	private VarRoutesVR X;
	private ArrayList<ConstraintSystemVR> constraintSystem;

	public VRManager() {
		invariants = new ArrayList<InvariantVR>();
		X = null;
		constraintSystem = new ArrayList<ConstraintSystemVR>();
	}

	public void postConstraintSystemVR(ConstraintSystemVR S) {
		constraintSystem.add(S);
	}

	public void printInvariants() {
		for (InvariantVR I : invariants) {
			System.out.println(I.name());
		}
	}

	public VarRoutesVR getVarRoutesVR() {
		return X;
	}

	public void post(InvariantVR f) {
		// System.out.println("***** " + f.name());
		invariants.add(f);
	}

	public void post(VarRoutesVR XR) {
		if (X != null) {
			System.out
					.println(name()
							+ "::post(VarRoutesVR X),  EXCEPTION, another VarRoutesVR is already posted");
			System.out.println(X.toString());
			exit(-1);
		}
		// System.out.println(name() + "::post(VarRoutesVR)");
		this.X = XR;
	}

	public void initPropagation() {
		for (InvariantVR f : invariants) {
			// System.out.println(f.name());
			f.initPropagation();
		}
	}

	public void close() {
		for (ConstraintSystemVR S : constraintSystem)
			invariants.add(S);

		// System.out.println(name() + "::close, invariants = ");
		// printInvariants();

		initPropagation();

	}
    public void performTwoOptMoveOneRoute(Point x, Point y){
    	// x and y are in the same route, x is before y
    	// remove (x,next[x]) and (y,next[y])
    	// add (x,y) and (next[x],next[y]), reverse path from y to next[x]
		X.performTwoOptMoveOneRoute(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMoveOneRoute(x, y);
		}
    
    		
    }

	public void performRemoveAllClientPoints() {
		for (int k = 1; k <= X.getNbRoutes(); k++) {
			Point p = X.next(X.startPoint(k));
			while (p != X.endPoint(k)) {
				performRemoveOnePoint(p);
				//System.out.println(name() + "::performRemoveOnePoint(" + p.ID
				//		+ " on route " + k + "), XR = " + X.toString());
				p = X.next(X.startPoint(k));
			}
		}
	}

	// move of type a [Groer et al., 2010]
	// move customer x to from route of x to route of y; insert x into the
	// position between y and next[y]
	// x and y are not the depot
	public void performOnePointMove(Point x, Point y) {
		X.performOnePointMove(x, y);
		for (InvariantVR f : invariants) {
			f.propagateOnePointMove(x, y);
		}
	}

	// move of type b [Groer et al., 2010]
	// x and y are on the same route and are not the depots, y locates before x
	// on the route
	// remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
	// insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
	public void performTwoPointsMove(Point x, Point y) {
		X.performTwoPointsMove(x, y);
		Iterator<InvariantVR> it = invariants.iterator();
		for (InvariantVR f : invariants) {
			f.propagateTwoPointsMove(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,y) and (next[x],next(y))
	public void performTwoOptMove1(Point x, Point y) {
		X.performTwoOptMove1(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove1(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (y,x) and (next[x],next(y))
	public void performTwoOptMove2(Point x, Point y) {
		X.performTwoOptMove2(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove2(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,y) and (next[y],next(x))
	public void performTwoOptMove3(Point x, Point y) {
		X.performTwoOptMove3(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove3(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (y,x) and (next[y],next(x))
	public void performTwoOptMove4(Point x, Point y) {
		X.performTwoOptMove4(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove4(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,next[y]) and (y,next[x])
	public void performTwoOptMove5(Point x, Point y) {
		X.performTwoOptMove5(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove5(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (next[y],x) and (y,next[x])
	public void performTwoOptMove6(Point x, Point y) {
		X.performTwoOptMove6(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove6(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (x,next[y]) and (next[x],y)
	public void performTwoOptMove7(Point x, Point y) {
		X.performTwoOptMove7(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove7(x, y);
		}
	}

	// move of type c [Groer et al., 2010]
	// x and y are on different routes and are not depots
	// remove (x,next[x]) and (y,next[y])
	// insert (next[y],x) and (next[x],y)
	public void performTwoOptMove8(Point x, Point y) {
		X.performTwoOptMove8(x, y);
		for (InvariantVR f : invariants) {
			f.propagateTwoOptMove8(x, y);
		}
	}

	// move of type d [Groer et al., 2010]
	// move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the
	// route containing y
	// remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
	// add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
	public void performOrOptMove1(Point x1, Point x2, Point y) {
		X.performOrOptMove1(x1, x2, y);
		for (InvariantVR f : invariants) {
			f.propagateOrOptMove1(x1, x2, y);
		}
	}

	// move of type d [Groer et al., 2010]
	// move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the
	// route containing y
	// remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
	// add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
	public void performOrOptMove2(Point x1, Point x2, Point y) {
		X.performOrOptMove2(x1, x2, y);
		for (InvariantVR f : invariants) {
			f.propagateOrOptMove2(x1, x2, y);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,z) and (next[y], next[x]) and(y, next[z])
	public void performThreeOptMove1(Point x, Point y, Point z) {
		X.performThreeOptMove1(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove1(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (z,x) and (next[x], next[y]) and(next[z],y)
	public void performThreeOptMove2(Point x, Point y, Point z) {
		X.performThreeOptMove2(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove2(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,y) and (next[x], z) and(next[y], next[z])
	public void performThreeOptMove3(Point x, Point y, Point z) {
		X.performThreeOptMove3(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove3(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (y,x) and (z,next[x]) and(next[z], next[y])
	public void performThreeOptMove4(Point x, Point y, Point z) {
		X.performThreeOptMove4(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove4(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,next[y]) and (z,next[x]) and(y, next[z])
	public void performThreeOptMove5(Point x, Point y, Point z) {
		X.performThreeOptMove5(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove5(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (next[y],x) and (next[x],z) and(next[z],y)
	public void performThreeOptMove6(Point x, Point y, Point z) {
		X.performThreeOptMove6(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove6(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (x,next[y]) and (z,y) and(next[x], next[z])
	public void performThreeOptMove7(Point x, Point y, Point z) {
		X.performThreeOptMove7(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove7(x, y, z);
		}
	}

	// move of type e [Groer et al., 2010]
	// x, y, z are on the same route in that order (x is before y, y is before
	// z)
	// remove (x, next[x]), (y, next[y]), and (z, next[z])
	// insert (next[y],x) and (y,z) and(next[z], next[x])
	public void performThreeOptMove8(Point x, Point y, Point z) {
		X.performThreeOptMove8(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateThreeOptMove8(x, y, z);
		}
	}

	// move of type g [Groer et al., 2010]
	// x1 and y1 are on the same route, x1 is before y1
	// x2 and y2 are on the same route, x2 is before y2
	// remove (x1,next[x1]) and (y1, next[y1])
	// remove (x2, next[x2]) and (y2, next[y2])
	// insert (x1, next[x2]) and (y2, next[y1])
	// insert (x2, next[x1]) and (y1, next[y2])
	public void performCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		X.performCrossExchangeMove(x1, y1, x2, y2);
		for (InvariantVR f : invariants) {
			f.propagateCrossExchangeMove(x1, y1, x2, y2);
		}
	}

	public void performTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		X.performTwoPointsMove(x1, x2, y1, y2);
		;
		for (InvariantVR f : invariants) {
			f.propagateTwoPointsMove(x1, x2, y1, y2);
			;
		}
	}

	public void performThreePointsMove(Point x1, Point x2, Point x3, Point y1,
			Point y2, Point y3) {
		X.performThreePointsMove(x1, x2, x3, y1, y2, y3);
		;
		for (InvariantVR f : invariants) {
			f.propagateThreePointsMove(x1, x2, x3, y1, y2, y3);
		}
	}

	public void performFourPointsMove(Point x1, Point x2, Point x3, Point x4,
			Point y1, Point y2, Point y3, Point y4) {
		X.performFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
		for (InvariantVR f : invariants) {
			f.propagateFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4);
		}
	}

	public void performAddOnePoint(Point x, Point y) {
		X.performAddOnePoint(x, y);
		for (InvariantVR f : invariants) {
			f.propagateAddOnePoint(x, y);
		}
	}

	public void performRemoveOnePoint(Point x) {
		X.performRemoveOnePoint(x);
		for (InvariantVR f : invariants) {
			f.propagateRemoveOnePoint(x);
		}
	}
	
	public void performAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		X.performAddTwoPoints(x1, y1, x2, y2);
		for (InvariantVR f : invariants) {
			f.propagateAddTwoPoints(x1, y1, x2, y2);
		}
	}

	public void performRemoveTwoPoints(Point x1, Point x2) {
		X.performRemoveTwoPoints(x1, x2);
		for (InvariantVR f : invariants) {
			f.propagateRemoveTwoPoints(x1, x2);
		}
	}

	public void performAddRemovePoints(Point x, Point y, Point z) {
		X.performAddRemovePoints(x, y, z);
		for (InvariantVR f : invariants) {
			f.propagateAddRemovePoints(x, y, z);
		}
	}

	public void performKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		X.performKPointsMove(x, y);
		for (InvariantVR f : invariants) {
			f.propagateKPointsMove(x, y);
		}
	}

	public String name() {
		return "VRManager";
	}

	public void exit(int code) {
		System.out.println(name() + "::exit(" + code + ")");
		System.exit(code);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
