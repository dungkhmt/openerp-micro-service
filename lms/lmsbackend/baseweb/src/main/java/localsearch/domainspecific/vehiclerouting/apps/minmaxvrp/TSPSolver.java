package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;

import java.util.*;

public class TSPSolver {
	public MinMaxCVRP vrp;
	public ArcWeightsManager awm;
	public ArrayList<Point> points;
	public Point start;
	public Point end;
	
	// modelling
	public VRManager mgr;
	public VarRoutesVR XR;
	public IFunctionVR obj;
	public LexMultiFunctions F;
	public TSPSolver(MinMaxCVRP vrp){
		this.awm = vrp.awm;
	}
	public void stateModel(){
		//ArrayList<Point> points = route.clientPoints;
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		XR.addRoute(start, end);
		for(Point p: points) XR.addClientPoint(p);
		
		AccumulatedWeightEdgesVR awe = new AccumulatedWeightEdgesVR(XR, awm);
		obj = new AccumulatedEdgeWeightsOnPathVR(awe, end);
		F = new LexMultiFunctions();
		F.add(obj);		
		mgr.close();
	}
	public void search(int maxIter, int maxTime){
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyThreeOptMove1Explorer(XR, F));
		NE.add(new GreedyThreeOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove3Explorer(XR, F));
		NE.add(new GreedyThreeOptMove4Explorer(XR, F));
		NE.add(new GreedyThreeOptMove5Explorer(XR, F));
		NE.add(new GreedyThreeOptMove6Explorer(XR, F));
		NE.add(new GreedyThreeOptMove7Explorer(XR, F));
		NE.add(new GreedyThreeOptMove8Explorer(XR, F));
		NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
		
		GenericLocalSearch se = new GenericLocalSearch(mgr, F, NE);
		//se.verbose = false;
		se.noMoveBreak = true;
		se.verbose = false;
		se.search(maxIter, maxTime);
	}
	
	public void optimize(Cluster route, int maxIter, int maxTime){
		points = route.clientPoints;
		this.start = route.startPoint;
		this.end = route.endPoint;
		
		stateModel();
		
		search(maxIter, maxTime);
		
		route.clientPoints = new ArrayList<Point>();
		for(Point p = XR.next(XR.startPoint(1)); p != XR.endPoint(1); p = XR.next(p))
			route.clientPoints.add(p);
		route.length = obj.getValue();
	}
	public ArrayList<Point> getClientSequence(){
		ArrayList<Point> s = new ArrayList<Point>();
		for(Point p = XR.next(XR.startPoint(1)); p != XR.endPoint(1); p = XR.next(p))
			s.add(p);
		
		return s;
	}
	public Cluster evaluateAdd(Cluster route, Point p, int maxIter, int maxTime){
		points = new ArrayList<Point>();
		for(Point pi: route.clientPoints)
			points.add(pi);
		points.add(p);
		this.start = route.startPoint;
		this.end = route.endPoint;
		
		stateModel();
		
		search(maxIter, maxTime);
		
		Cluster c = new Cluster(start, end, getClientSequence());
		c.length = obj.getValue(); 
		return c;
		
	}
	public Cluster evaluateRemove(Cluster route, Point p, int maxIter, int maxTime){
		points = new ArrayList<Point>();
		for(Point pi: route.clientPoints)
			points.add(pi);
		
		int idx = points.indexOf(p);
		if(idx >= 0)
		points.remove(idx);
		
		this.start = route.startPoint;
		this.end = route.endPoint;
		
		stateModel();
		
		search(maxIter, maxTime);
		
		Cluster c = new Cluster(start, end, getClientSequence());
		c.length = obj.getValue(); 
		return c;
	}
	
	public Cluster evaluateAddRemove(Cluster route, Point pIn, Point pOut, int maxIter, int maxTime){
		points = new ArrayList<Point>();
		for(Point pi: route.clientPoints)
			points.add(pi);
		points.add(pIn);
		
		int idx = points.indexOf(pOut);
		if(idx >= 0)
			points.remove(pOut);
		
		this.start = route.startPoint;
		this.end = route.endPoint;
		
		stateModel();
		
		search(maxIter, maxTime);
		
		Cluster c = new Cluster(start, end, getClientSequence());
		c.length = obj.getValue(); 
		return c;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
