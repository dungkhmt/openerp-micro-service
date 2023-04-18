package localsearch.domainspecific.vehiclerouting.vrp.online;

import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

import java.util.ArrayList;

public class NodeWeightsManagerOnline extends NodeWeightsManager {
	
	public NodeWeightsManagerOnline(VarRoutesVROnline XR){
		super(new ArrayList<Point>());
		XR.getVRManagerOnline().setNodeWeightsManager(this);
	}
	
	private void scaleUp(){
		double[] t_w = new double[2*weights.length];
		System.arraycopy(weights, 0, t_w, 0, weights.length);
		weights = t_w;
	}
	public void addPoint(Point p){
		if(weights.length == points.size()) scaleUp();
		points.add(p);
		map.put(p, points.size()-1);
	}
	
	public String name(){
		return "NodeWeightsManagerOnline";
	}
	
}
