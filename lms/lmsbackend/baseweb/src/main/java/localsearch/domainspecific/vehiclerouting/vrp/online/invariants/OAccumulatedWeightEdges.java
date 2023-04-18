package localsearch.domainspecific.vehiclerouting.vrp.online.invariants;

import java.io.PrintWriter;
import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.IDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.online.TimeDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.online.VarRoutesVROnline;

public class OAccumulatedWeightEdges extends AccumulatedWeightEdgesVR implements
		OInvariantVR {
	
	//private double[] traveledDistance;
	//private double[] backTraveledDistance;
	private TimeDistanceManager TDM;
	protected ArrayList<Point> points;
	private static final int scaleSz = 1000;
	private int maxSz = 0;
	private PrintWriter log = null;
	public OAccumulatedWeightEdges(VarRoutesVROnline XR, TimeDistanceManager TDM){
		super(XR,TDM);
		this.TDM = TDM;
		XR.getVRManagerOnline().post(this);
		//traveledDistance = DM.getTraveledDistance();
		//backTraveledDistance = DM.getBackTraveledDistance();
		//costRight = new double[XR.getMaxNbPoints()];
		//costLeft = new double[XR.getMaxNbPoints()];
		points = new ArrayList<Point>();
	}
	public void setLog(PrintWriter log){
		this.log = log;
	}
	public void scaleUp(){
		maxSz += scaleSz;
		double[] t_costRight = new double[maxSz];
		double[] t_costLeft = new double[maxSz];
		System.arraycopy(costRight, 0, t_costRight, 0, costRight.length);
		System.arraycopy(costLeft, 0, t_costLeft, 0, costLeft.length);
		costRight = t_costRight;
		costLeft = t_costLeft;
	}
	public void addPoint(Point p){
		points.add(p);
		map.put(p, points.size()-1);
		
		if(points.size() + 1 >= maxSz)
			scaleUp();
	}
	@Override
	public void updateWhenReachingTimePoint(int t){
		//System.out.println(name() + "::updateWhenReachingTimePoint(" + t + ") + costRight.length = " + costRight.length);
		VarRoutesVR VR = super.getVarRoutesVR();
		for(int k = 1; k <= VR.getNbRoutes(); k++){
			costRight[getIndex(VR.getStartingPointOfRoute(k))] = TDM.getTraversedDistance(k);//traveledDistance[XR.getStartingPointOfRoute(k)];
			costLeft[getIndex(VR.getStartingPointOfRoute(k))] = TDM.getBackwardTraversedDistance(k);//backTraveledDistance[XR.getStartingPointOfRoute(k)];
			//log.println(name() + "::updateWhenReachingTimePoint(" + t + "), costRight[" + k + "] = " + costRight[getIndex(XR.getStartingPointOfRoute(k))]);
			super.update(k);
		}
	}
	
	/*
	public void print(int k){
		if(k > XR.getNbRoutes()) return;
		//for(int k = 1; k <= XR.getNbRoutes(); k++){
			double f = costRight[XR.getStartingPointOfRoute(k)];
			double f1 = costRight[XR.getStartingPointOfRoute(k)];
			System.out.println(name() + "::print, costRight[" + k + "] = " + costRight[XR.getStartingPointOfRoute(k)]);
			for(int u = XR.getStartingPointOfRoute(k); u != XR.getTerminatingPointOfRoute(k); u = XR.next(u)){
				int nu = XR.next(u);
				f += cost[u][nu];
				
				Point pu = XR.getAllPoints().get(u-1);
				Point pnu = XR.getAllPoints().get(nu-1);
				double c = pu.distance(pnu);
				f1 += c;
				System.out.println(name() + "::print, cost[" + u + "," + nu + "] = " + cost[u][nu] + ", c = " + c + ", f = " + f + ", f1 = " + f1);
			}
		//}
	}
	*/
	public String name(){
		return "OAccumulatedWeightEdges";
	}
	/*
	void update(int k) {
		System.out.println(name() + "::update(" + k + ")");System.exit(-1);
        //costRight[XR.getStartingPointOfRoute(k)] = 0;
        for (int u = XR.getStartingPointOfRoute(k); u != XR.getTerminatingPointOfRoute(k); u = XR.next(u)) {
            costRight[XR.next(u)] = costRight[u] + cost[u][XR.next(u)];
        }
        //costLeft[XR.getTerminatingPointOfRoute(k)] = 0;
        for (int u = XR.getTerminatingPointOfRoute(k); u != XR.getStartingPointOfRoute(k); u = XR.prev(u)) {
            costLeft[XR.prev(u)] = costLeft[u] + cost[u][XR.prev(u)];
        }
    }
    */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
