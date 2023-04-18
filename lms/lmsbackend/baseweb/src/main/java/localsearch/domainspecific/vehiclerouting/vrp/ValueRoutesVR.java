
/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 13/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp;
import java.util.ArrayList;
import java.util.HashMap;
import localsearch.domainspecific.vehiclerouting.vrp.entities.*;

public class ValueRoutesVR {
	private HashMap<Point, Point> next;
	private HashMap<Point, Point> prev;
	private HashMap<Point, Integer> route;
	private VarRoutesVR XR;
	private ArrayList<Point> allPoints;
	
	public ValueRoutesVR(VarRoutesVR XR){
		this.XR = XR;
		this.next = new HashMap<Point, Point>();
		this.prev = new HashMap<Point, Point>();
		this.route = new HashMap<Point, Integer>();
		
		allPoints = XR.getAllPoints();
		for(Point p : allPoints){
			next.put(p, XR.next(p));
			prev.put(p, XR.prev(p));
			route.put(p, XR.route(p));
		}
	}
	public Point next(Point p){
		return next.get(p);
	}
	public Point prev(Point p){
		return prev.get(p);
	}
	public int route(Point p){
		return route.get(p);
	}
	public void store(){
		for(Point p : allPoints){
			next.put(p, XR.next(p));
			prev.put(p, XR.prev(p));
			route.put(p, XR.route(p));
		}
	}
	public String toString() {
		String s = "";
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			s += "route[" + k + "] = ";
			Point x = XR.getStartingPointOfRoute(k);
			while(x != XR.getTerminatingPointOfRoute(k)){
				s = s + x.getID() + " " + " -> ";
				x = next.get(x);
			}
			s = s + x.getID() + "\n";
		}
		return s;
	}
}
