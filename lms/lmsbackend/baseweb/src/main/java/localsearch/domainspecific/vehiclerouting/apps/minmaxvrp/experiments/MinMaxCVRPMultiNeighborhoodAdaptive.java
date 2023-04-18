package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.experiments;

import java.util.ArrayList;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.MMSearch;
import localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.MinMaxCVRP;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
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

public class MinMaxCVRPMultiNeighborhoodAdaptive extends MinMaxCVRP{

	public MinMaxCVRPMultiNeighborhoodAdaptive(){
		super();
	}
	public void search(int timeLimit) {
		HashSet<Point> mandatory = new HashSet<Point>();
		for(Point p: clientPoints) mandatory.add(p);
		
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		NE.add(new GreedyCrossExchangeMoveExplorer(XR, F));
		
		NE.add(new GreedyOrOptMove1Explorer(XR, F));
		NE.add(new GreedyOrOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove1Explorer(XR, F));
		NE.add(new GreedyThreeOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove3Explorer(XR, F));
		NE.add(new GreedyThreeOptMove4Explorer(XR, F));
		NE.add(new GreedyThreeOptMove5Explorer(XR, F));
		NE.add(new GreedyThreeOptMove6Explorer(XR, F));
		NE.add(new GreedyThreeOptMove7Explorer(XR, F));
		NE.add(new GreedyThreeOptMove8Explorer(XR, F));
		NE.add(new GreedyTwoOptMove1Explorer(XR, F));
		NE.add(new GreedyTwoOptMove2Explorer(XR, F));
		NE.add(new GreedyTwoOptMove3Explorer(XR, F));
		NE.add(new GreedyTwoOptMove4Explorer(XR, F));
		NE.add(new GreedyTwoOptMove5Explorer(XR, F));
		NE.add(new GreedyTwoOptMove6Explorer(XR, F));
		NE.add(new GreedyTwoOptMove7Explorer(XR, F));
		NE.add(new GreedyTwoOptMove8Explorer(XR, F));
		
		
		
		//NE.add(new GreedyKPointsMoveExplorer(XR, F, 2, mandatory));
		
		MMSearch se = new MMSearch(mgr);
		
		//GenericLocalSearch se = new GenericLocalSearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);
		
		
		se.search(150, timeLimit);
		print();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinMaxCVRPMultiNeighborhoodAdaptive vrp = new MinMaxCVRPMultiNeighborhoodAdaptive();
		vrp.readData("data/MinMaxVRP/Christophides/std-all/E-n101-k14.vrp");
		vrp.mapping();
		vrp.stateModel();
		vrp.search(300);

	}

}
