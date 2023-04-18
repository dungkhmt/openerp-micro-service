package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.MaxVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove2Explorer;
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
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;

public class MultiKnapsackSolver {
	public MinMaxCVRP vrp;
	
	public ArrayList<Point> startPoints;
	public ArrayList<Point> endPoints;
	public ArrayList<Point> clientPoints;
	//public ArrayList<Point> allPoints;
	public NodeWeightsManager nwm;
	
	
	// modelling
	public VRManager mgr;
	public VarRoutesVR XR;
	public IFunctionVR[] accDemand;
	public ConstraintSystemVR CS;
	public LexMultiFunctions F;

	public MultiKnapsackSolver(MinMaxCVRP vrp){
		this.vrp = vrp;
		this.startPoints = vrp.startPoints;
		this.endPoints = vrp.endPoints;
		this.clientPoints = vrp.clientPoints;
		this.nwm = vrp.nwm;
		 
	}
	
	public void stateModel(){
		int capacity = vrp.capacity;
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		for(int i = 0; i < startPoints.size(); i++){
			Point s = startPoints.get(i);
			Point t = endPoints.get(i);
			XR.addRoute(s, t);
		}
		for(Point p: clientPoints)
			XR.addClientPoint(p);
		
		CS = new ConstraintSystemVR(mgr);
		AccumulatedWeightNodesVR awn = new AccumulatedWeightNodesVR(XR, nwm);
		accDemand = new IFunctionVR[XR.getNbRoutes()];
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			accDemand[k-1] = new AccumulatedNodeWeightsOnPathVR(awn, XR.endPoint(k));
			CS.post(new Leq(accDemand[k-1], capacity));
		}
		
		
		F = new LexMultiFunctions();
		F.add(new ConstraintViolationsVR(CS));
		mgr.close();
		
	}
	
	public void search(int timeLimit) {
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		NE.add(new GreedyCrossExchangeMoveExplorer(XR, F));
		
		GenericLocalSearch se = new GenericLocalSearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);
		LexMultiValues opt = new LexMultiValues(0);
		
		se.setKnownOptimal(opt);
		se.search(100000, timeLimit);

	}
	
	public void solve(int timeLimit){
		stateModel();
		search(timeLimit);
	}
	public ArrayList<Point> getPointsOfRoute(int k){
		ArrayList<Point> P = new ArrayList<Point>();
		for(Point p = XR.next(XR.startPoint(k)); p != XR.endPoint(k); p = XR.next(p))
			P.add(p);
		return P;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
