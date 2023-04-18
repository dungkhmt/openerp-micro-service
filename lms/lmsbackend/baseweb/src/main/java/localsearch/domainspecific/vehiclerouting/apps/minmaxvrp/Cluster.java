package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;
import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
public class Cluster {
	public Point startPoint;
	public Point endPoint;
	public ArrayList<Point> clientPoints;
	public double length;
	public int demand;
	
	public Cluster(Point s, Point t, ArrayList<Point> clientPoints){
		this.startPoint = s;
		this.endPoint = t;
		this.clientPoints = clientPoints;
	}
	public Cluster clone(){
		ArrayList<Point> P = new ArrayList<Point>();
		for(Point p: clientPoints) P.add(p);
		
		Cluster c = new Cluster(startPoint,endPoint,P);
		c.demand = demand;
		c.length = length;
		return c;
	}
	public void addClientPoint(Point p){
		clientPoints.add(p);
	}
	public void removeClientPoint(Point p){
		int idx = clientPoints.indexOf(p);
		if(idx >= 0) clientPoints.remove(idx);
	}
	public String toString(){
		String s = startPoint.ID + "";
		for(Point p: clientPoints)
			s += " -> " + p.ID;
		s += " -> " + endPoint.ID;
		s += " length = " + length + ", demand = " + demand;
		return s;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
