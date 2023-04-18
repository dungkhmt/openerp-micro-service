package localsearch.domainspecific.vehiclerouting.vrp.online;

/**
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * start date: 12/1/2016
 */
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.IDistanceManager;
public interface TimeDistanceManager extends IDistanceManager{
	public void updateWhenReachingTimePoint(int t);
	public int getArrivalTime(Point x);
	public int getDepartureTime(Point x);
	public double getTraversedDistance(int k);// return the distance already traversed by the vehicle k
	public double getBackwardTraversedDistance(int k);
	public void addPoint(Point p);
}
