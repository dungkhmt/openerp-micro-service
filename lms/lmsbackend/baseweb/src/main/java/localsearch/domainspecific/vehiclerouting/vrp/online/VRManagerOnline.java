package localsearch.domainspecific.vehiclerouting.vrp.online;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.online.invariants.OInvariantVR;
import java.util.*;


public class VRManagerOnline extends VRManager {
	private ArrayList<OInvariantVR> oinvariants;
	private TimeDistanceManager TDM;
	private NodeWeightsManagerOnline nwm;
	
	//private TimeManager TM;

	public VRManagerOnline() {
		super();
		oinvariants = new ArrayList<OInvariantVR>();
	}

	public String name(){
		return "VRPManagerOnline";
	}
	
	public void post(OInvariantVR I) {
		//System.out.println(name() + "::post(OInvaariantVRP");
		oinvariants.add(I);
	}
	public void print(){
		System.out.println(name() + "::print, oinvariants = ");
		for(OInvariantVR o: oinvariants)
			System.out.println(o.name());
	}
	public void update(int t) {
		//System.out.println(name() + "::execute before TDM.update");
		TDM.updateWhenReachingTimePoint(t);
		//System.out.println(name() + "::execute before TDM.update");
		for (OInvariantVR I : oinvariants)
			I.updateWhenReachingTimePoint(t);
	}


	public TimeDistanceManager getTimeDistanceManager() {
		return TDM;
	}

	public void setTimeDistanceManager(TimeDistanceManager TDM) {
		this.TDM = TDM;
	}

	public void setNodeWeightsManager(NodeWeightsManagerOnline nwm){
		this.nwm = nwm;
	}
	public void engage(Point p){
		getVarRoutesVR().addClientPoint(p);
		TDM.addPoint(p);
		nwm.addPoint(p);
		for(OInvariantVR o: oinvariants)
			o.addPoint(p);
	}
	public void performAddOnePoint(Point x, Point y) {
		//System.out.println(name() + "::performAddOnePoint(" + x + "," + y + ")");
		super.getVarRoutesVR().performAddOnePoint(x, y);
		for (OInvariantVR f : oinvariants) {
			//System.out
			//.println(name() + "::performAddOnePoint(" + x + "," + y + "), invariant f = " + f.name() + "::propagateAddOnePoint");
		
			f.propagateAddOnePoint(x, y);
		}
	}
	
	public void performTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		//print();
		super.getVarRoutesVR().performTwoPointsMove(x1, x2, y1, y2);
		for(OInvariantVR f: oinvariants){
			f.propagateTwoPointsMove(x1, x2, y1, y2);
		}
	}
}
