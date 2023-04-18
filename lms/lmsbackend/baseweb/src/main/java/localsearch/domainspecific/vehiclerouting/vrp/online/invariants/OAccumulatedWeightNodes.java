package localsearch.domainspecific.vehiclerouting.vrp.online.invariants;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.online.VarRoutesVROnline;


public class OAccumulatedWeightNodes extends AccumulatedWeightNodesVR implements
		OInvariantVR {
	protected VarRoutesVROnline XRO;
	private static final int scaleSz = 1000;
	private int maxSz = 0;
	protected ArrayList<Point> points;
	private PrintWriter log = null;
	public OAccumulatedWeightNodes(VarRoutesVROnline XR, NodeWeightsManager nodeWeights){
		super(XR,nodeWeights);
		XRO = XR;
		points = new ArrayList<Point>();
		XR.getVRManagerOnline().post(this);
		//TODO
		//System.out.println(name() + "::constructor, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		//System.exit(-1);
	}
	public void scaleUp(){
		//TODO
		//System.out.println(name() + "::scaleUp, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		//System.exit(-1);
		
		maxSz += scaleSz;
		double[] t_sumWeights = new double[maxSz];
		System.arraycopy(sumWeights, 0, t_sumWeights, 0, sumWeights.length);
		sumWeights = t_sumWeights;
	}
	public void setLog(PrintWriter log){
		this.log = log;
	}
	public void addPoint(Point p){
		//TODO
		//System.out.println(name() + "::addPoint(p  = " + p.ID + "), NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		//System.exit(-1);
	
		points.add(p);
		map.put(p, points.size()-1);
		
		if(points.size() + 1 >= maxSz)
			scaleUp();
	}
	public String name(){
		return "OAccumulatedWeightNodes";
	}
	public void init(){
		for(int k = 1; k <= XR.getNbRoutes(); k++)
			update(k);
	}
	@Override
	public void updateWhenReachingTimePoint(int t) {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::updateWhenReachingTimePoint, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		//System.exit(-1);
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			Point s = XR.startPoint(k);
			double w = nwm.getWeight(s);
			for(Point p: XRO.getPassedPoints(k)){
				w += nwm.getWeight(p);
			}
			sumWeights[getIndex(s)] = w;
		}
		
	}

}
