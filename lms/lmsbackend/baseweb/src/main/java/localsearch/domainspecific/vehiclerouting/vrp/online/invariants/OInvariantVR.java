package localsearch.domainspecific.vehiclerouting.vrp.online.invariants;

import localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
//import localsearch.domainspecific.vehiclerouting.vrp.InvariantVRP;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public interface OInvariantVR extends InvariantVR {
	public void updateWhenReachingTimePoint(int t);
	public void addPoint(Point p);
}
