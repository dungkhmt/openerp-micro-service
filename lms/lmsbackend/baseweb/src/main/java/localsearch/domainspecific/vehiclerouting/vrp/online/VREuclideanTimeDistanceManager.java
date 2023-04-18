package localsearch.domainspecific.vehiclerouting.vrp.online;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.utils.Utility;

/*
 * authors: Pham Quang Dung
 * start date: 12/1/2016
 */
public class VREuclideanTimeDistanceManager implements TimeDistanceManager {
	private VarRoutesVROnline XR;
	//private int[] time2NextPoint;
	private Point[] position;
	//private Point[] old_position;
	private double[][] distance;
	private double[] traveledDistance;// traveledDistance[k] is the distance that the vehicle k has passed from its depot
	private double[] backTraveledDistance;// backTravledDistance[k] is the reverse distance that the vehicle k come back from current position to the depot
	
	private HashMap<Integer, Double> speed;// speed[k] is the speed of vehicle k
	
	private int[] arrivalTime;
	private int[] departureTime;
	private int[] serviceDuration;
	private ArrayList<Point> points;
	private HashMap<Point, Integer> mPoint;
	private int maxSz = 1000;
	private static final int scaleSz = 1000;
	public VREuclideanTimeDistanceManager(VarRoutesVROnline XR){
		this.XR = XR;
		XR.getVRManagerOnline().setTimeDistanceManager(this);
		points = new ArrayList<Point>();
		mPoint = new HashMap<Point, Integer>();
		distance = new double[maxSz][maxSz];
		arrivalTime = new int[maxSz];
		departureTime = new int[maxSz];
		serviceDuration = new int[maxSz];
		traveledDistance = new double[maxSz];
		backTraveledDistance = new double[maxSz];
		speed = new HashMap<Integer, Double>();
		position = new Point[maxSz];
		//old_position = new Point[maxSz];
	}
	private void scaleUp(){
		maxSz += scaleSz;
		position = Utility.scaleUp(position, scaleSz);
		distance = Utility.scaleUp(distance, scaleSz);
		traveledDistance = Utility.scaleUp(traveledDistance, scaleSz);
		backTraveledDistance = Utility.scaleUp(backTraveledDistance, scaleSz);
		arrivalTime = Utility.scaleUp(arrivalTime, scaleSz);
		departureTime = Utility.scaleUp(departureTime, scaleSz);
		serviceDuration = Utility.scaleUp(serviceDuration, scaleSz);
	}
	public void setSpeed(int k , double sp){
		speed.put(k, sp);
	}
	private double getSpeed(int k){
		return speed.get(k);
	}
	public void addPoint(Point x){
		if(points.size() + 1 >= maxSz)
			scaleUp();
		points.add(x);
		mPoint.put(x, points.size()-1);
		position[mPoint.get(x)] = x;
		setArrivalTime(x,-1);
		setDepartureTime(x,-1);
		for(Point p: points){
			setDistance(x,p,x.distance(p));
			setDistance(p,x,p.distance(x));
		}
	}
	public void setArrivalTime(Point x, int t){
		arrivalTime[mPoint.get(x)] = t;
	}
	public void setDepartureTime(Point x, int t){
		departureTime[mPoint.get(x)] = t;
	}
	public void setDistance(Point x, Point y, double d){
		distance[mPoint.get(x)][mPoint.get(y)] = d;
	}
	@Override
	public void updateWhenReachingTimePoint(int t) {
		// TODO Auto-generated method stub
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			Point np = getNextPoint(k, t);
			if(np == null){//Constants.NULL_POINT){
				update(null, np, k, t);
				
				XR.update(k, XR.getTerminatingPointOfRoute(k));// the vehicle
																// will stop at
																// terminating
																// point
				setDepartureTime(XR.getStartingPointOfRoute(k), -1);// do not
																		// know
																		// when
																		// the
																		// vehicle
																		// restarts
				XR.setMoving(k, false);
			}else{
				Point pp = XR.prev(np);
				update(pp, np, k, t);
				XR.update(k, np);
				setDepartureTime(XR.getStartingPointOfRoute(k), t);
			}
		}
		
		updateDistanceFromStartingPoint();
		
	}
	/*
	private void updateArrivalDepartureTime(){
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			if(XR.isMoving(k) == false) continue;
			
			for(int v = XR.getStartingPointOfRoute(k); v != XR.getTerminatingPointOfRoute(k); v = XR.next(v)){
				int nv = XR.next(v);
				Point pv = XR.getPoint(v);
				Point pnv = XR.getPoint(nv);
				double d = pv.distance(pnv);//DM.getDistance(pv,pnv);//DM.getDistance(v,nv);
				int t = (int)Math.ceil(d/getSpeed(k));
				//System.out.println(name() + "::update, d(" + v + pv.toString() + "," + nv + pnv.toString() + ") = " + d + ", t = " + t + 
				//		", position[" + v + "] = " + DM.getPosition(v).toString() + ", position[" + nv + "] = " + DM.getPosition(nv).toString());
				arrivalTime[nv] = departureTime[v] + t;
				if(nv != XR.getTerminatingPointOfRoute(k))
					departureTime[nv] = arrivalTime[nv] + serviceDuration[nv];
				else departureTime[nv] = -1;// unknown, not specified yet
				//System.out.println(name() + "::update, departureTime[" + v + "] = " + departureTime[v] + 
				//		", arrivalTime[" + nv + "] = " + arrivalTime[nv] + ", departureTime[" + nv + "] = " + departureTime[nv]);
				
			}
		}
	}
	*/
	private void updateDistanceFromStartingPoint() {
		for(int k = 1; k <= XR.getNbRoutes(); k++)
			updateDistanceFromStartingPoint(k);
	}
	private void updateDistanceFromStartingPoint(int k) {
		Point s = XR.getStartingPointOfRoute(k);
		Point ps = s;//XR.getPoint(s);
		for(Point v : XR.getActivePoints()){
			Point pv = v;//XR.getPoint(v);//XR.getAllPoints().get(v-1);
			//distance[s][v] = Utility.euclideanDistance(position[s], position[v]);
			setDistance(ps,pv,ps.distance(pv));
			
			//if(Math.abs(distance[s][v]) < 0.01 && s != v){
				//System.out.println(name() + "::update(" + k + "), position[" + s + "] = " + position[s].toString() +
					//	", position["+ v + "] = " + position[v].toString() + ", distance[" + s + "," + v + "] = " + distance[s][v]);
				//System.exit(-1);
			//}
		}		
	}
	protected int index(Point p){
		return mPoint.get(p);
	}
	public String name(){
		return "VREuclideanTimeDistanceManager";
	}
	private void update(Point s, Point ns, int k, int t) {
		// update position of last point (considered as the starting point of the
		// current route) 
		//System.out.println(name() + "::updatePosition(s = " + s + ", ns = " + ns + ", k = " + k + ", t = " + t + ", moving = " + XR.isMoving(k));
		if(XR.isMoving(k) == false) return;
		if(ns == null){//Constants.NULL_POINT){// normalize the next status when the next point is Constants.NULL_POINT
			t = arrivalTime[index(XR.getTerminatingPointOfRoute(k))];
			ns = XR.getTerminatingPointOfRoute(k);
			s = XR.prev(ns);
		}
		Point start = XR.getStartingPointOfRoute(k);
		double x = position[index(start)].getX();
		double y = position[index(start)].getY();
		if(arrivalTime[index(ns)] == departureTime[index(s)]){
			//System.out.println(name() + "::updatePosition(route[" + k + "], t = " + t + ", s = " + s + XR.getPoint(s).toString()  + 
		//", ns = " + ns +  XR.getPoint(ns).toString() + ", arrivalTime[ns] = departure[s] = " + arrivalTime[ns]);
			//System.exit(-1);
			XR.setMoving(k, false);
			return;
		}
		double dx = position[index(ns)].getX() - position[index(s)].getX();
		dx = (dx * (t - departureTime[index(s)]) * 1.0)
				/ (arrivalTime[index(ns)] - departureTime[index(s)]);
		double dy = position[index(ns)].getY() - position[index(s)].getY();
		dy = (dy * (t - departureTime[index(s)]) * 1.0)
				/ (arrivalTime[index(ns)] - departureTime[index(s)]);
		
		//old_position[start].setX(position[start].getX());
		//old_position[start].setY(position[start].getY());
		
		position[index(start)].setX(position[index(s)].getX() + dx);
		position[index(start)].setY(position[index(s)].getY() + dy);
	
		//System.out.println(name() + "::updatePositiom(route[" + k + "], t = " + t + ", s = " + s + ", ns = " + ns + 
		//		", begin x = " + x + ", y = " + y + ", position[start].x = " + position[start].getX() + ", position[start].y = " + position[start].getY());
		// update traveled distance
		double d = 0;
		Point v = start;//XR.getStartingPointOfRoute(k);
		boolean ok = false;
		while(v != s){
			Point nv = XR.next(v);
			d += Utility.euclideanDistance(x, y,nv.getX(),nv.getY());//position[v].distance(XR.getPoint(XR.next(v))); 
			//if(k==2)System.out.println(name() + "::updatePosition(route[" + k + "], x = " + x + ", y = " + y + ", v = " + v + ", nv = " + nv + ", s = " + s + ", d = " + d);
			v = nv;
			x = v.getX();
			y = v.getY();
			//System.out.println(name() + "::updatePosition(route[" + k + "], endwhile v = nv = " + v + ", x = " + x + ", y = " + y + ", v = " + v + ", nv = " + nv + ", s = " + s + ", d = " + d);
			ok = true;
		}
		d += Utility.euclideanDistance(x,y,position[index(start)].getX(), position[index(start)].getY());
		//System.out.println(name() + "::updatePosition(route[" + k + "], position[s].x = " + position[s].getX() + ", position[s].y = " + position[s].getY() + 
			//	", x = " + x + ", y = " + y + ", v = " + v + ", s = " + s + ", d = " + d);
		//System.out.println(name() + "::udate, start = " + start.ID + ", index = " + index(start) + ", maxSz = " + maxSz + ", travledDistance.sz = " + traveledDistance.length);
		traveledDistance[index(start)] += d;//TM.getSpeed(k) * dt;
		backTraveledDistance[index(start)] += d;//TM.getSpeed(k) * dt;
	}

	@Override
	public double getTraversedDistance(int k){
		return traveledDistance[index(XR.getStartingPointOfRoute(k))];
	}
	@Override
	public double getBackwardTraversedDistance(int k){
		return backTraveledDistance[index(XR.getStartingPointOfRoute(k))];
	}
	
	@Override
	public double getDistance(Point x, Point y) {
		// TODO Auto-generated method stub
		return distance[mPoint.get(x)][mPoint.get(y)];
	}

	@Override
	public int getArrivalTime(Point x) {
		// TODO Auto-generated method stub
		return arrivalTime[mPoint.get(x)];
	}

	@Override
	public int getDepartureTime(Point x) {
		// TODO Auto-generated method stub
		return departureTime[mPoint.get(x)];
	}
	private void setDepartureTime(int x, int t){
		departureTime[x] = t;
	}
	private Point getNextPoint(int k, int t) {
		// return the point the is the next point of starting point of the route
		Point s = XR.getStartingPointOfRoute(k);
		Point ns = XR.next(s);
		//System.out.println(name() + "::getNextPoint(" + k + "," + t + "), s = " + s + ", ns = " + ns);
		if (ns == null)//Constants.NULL_POINT)
			return null;//Constants.NULL_POINT;// vehicle k is not moving
		while (true) {
			ns = XR.next(s);
			if (ns == XR.getTerminatingPointOfRoute(k)){
				if(arrivalTime[index(ns)] <= t) ns = null;//Constants.NULL_POINT;
				break;
			}
			if (arrivalTime[index(ns)] > t)
				break;
			s = ns;
		}
		return ns;
	}
	public void updateArrivalDepartureTimes(){
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			if(XR.isMoving(k) == false) continue;
			
			for(Point v = XR.getStartingPointOfRoute(k); v != XR.getTerminatingPointOfRoute(k); v = XR.next(v)){
				Point nv = XR.next(v);
				Point pv = v;//XR.getPoint(v);
				Point pnv = nv;//XR.getPoint(nv);
				double d = pv.distance(pnv);//DM.getDistance(pv,pnv);//DM.getDistance(v,nv);
				int t = (int)Math.ceil(d/getSpeed(k));
				//System.out.println(name() + "::update, d(" + v + pv.toString() + "," + nv + pnv.toString() + ") = " + d + ", t = " + t + 
				//		", position[" + v + "] = " + DM.getPosition(v).toString() + ", position[" + nv + "] = " + DM.getPosition(nv).toString());
				arrivalTime[index(nv)] = departureTime[index(v)] + t;
				if(nv != XR.getTerminatingPointOfRoute(k))
					departureTime[index(nv)] = arrivalTime[index(nv)] + serviceDuration[index(nv)];
				else departureTime[index(nv)] = -1;// unknown, not specified yet
				//System.out.println(name() + "::update, departureTime[" + v + "] = " + departureTime[v] + 
				//		", arrivalTime[" + nv + "] = " + arrivalTime[nv] + ", departureTime[" + nv + "] = " + departureTime[nv]);
				
			}
		}
	}

}
