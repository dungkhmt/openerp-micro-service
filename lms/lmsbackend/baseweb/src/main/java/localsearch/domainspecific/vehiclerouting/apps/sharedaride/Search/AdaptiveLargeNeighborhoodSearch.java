package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.ValueRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.largeneighborhoodexploration.ILargeNeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.moves.CrossExchangeMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.KPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OnePointMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.OrOptMove1;
import localsearch.domainspecific.vehiclerouting.vrp.moves.ThreeOptMove5;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove5;
import localsearch.domainspecific.vehiclerouting.vrp.moves.TwoPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class AdaptiveLargeNeighborhoodSearch implements ISearch {

	private VRManager mgr;
	private VarRoutesVR XR;
	private LexMultiValues bestValue;
	private ValueRoutesVR bestSolution;
	private int currentIter;
	private LexMultiFunctions F;
	private ArrayList<ArrayList<ILargeNeighborhoodExplorer>> listLargeNeighborhoodExplorer;

	public AdaptiveLargeNeighborhoodSearch(VRManager mgr, LexMultiFunctions F,
					ArrayList<ArrayList<ILargeNeighborhoodExplorer>> listLargeNeighborhoodExplorer){
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.listLargeNeighborhoodExplorer = listLargeNeighborhoodExplorer;
	}
	
	private void updateBest() {
		bestValue.set(F.getValues());
		bestSolution.store();
	}
	
@Override 
public void search(int maxIter, int timeLimit){
	
}
public void search(int maxIter, int timeLimit, String outDir){
	bestSolution = new ValueRoutesVR(XR);
	currentIter = 0;
	//XR.setRandom();
	bestSolution.store();
	Neighborhood N = new Neighborhood(mgr);
	bestValue = new LexMultiValues(F.getValues());
	double t0 = System.currentTimeMillis();
	System.out.println(name() + "::search, init bestValue = " + bestValue.toString());
	System.out.println(XR.toString());
	
	while (currentIter < maxIter) {
		double curTime = System.currentTimeMillis();
		double t = (curTime - t0)/1000;
		if (t  > timeLimit)
		{
			//System.out.println(t+"  -  "+timeLimit);
			break;
		}
		
		N.clear();

		for(int turn = 0; turn < listLargeNeighborhoodExplorer.size(); ++ turn)
		{
			System.out.println("turn : "+turn);
			ArrayList<ILargeNeighborhoodExplorer> largeNeighborhoodExplorer = listLargeNeighborhoodExplorer.get(turn);
			for(ILargeNeighborhoodExplorer NI : largeNeighborhoodExplorer)
			{
				//NI.exploreNeighborhood(N, bestEval); 
				NI.exploreLargeNeighborhood(N); 
			}
			if(F.getValues().lt(bestValue)){
				updateBest();
				System.out.println("ALNS:: better: " + F.getValues().toString());
				try{
					PrintWriter out = new PrintWriter(new FileOutputStream(outDir, true));
					out.println("AdaptiveLargeNeighborhoodSearch::search vio = " + F.getValues().get(0) + ", obj = " + F.getValues().get(1) + ", iter = " + currentIter + ", time = " + t);
					out.close();
				}catch(Exception e){
					
				}
			}
			else{
				XR.setValue(bestSolution);
			}
		}
		currentIter++;
	}

	XR.setValue(bestSolution);
	System.out.println(XR.toString());
	
	System.out.println("Best = " + F.getValues().toString());
	System.out.println("bestValues = " + bestValue.toString());
}
	
	public String name(){
		return "AdaptiveLargeNeighborhoodSearch";
	}
	@Override
	public LexMultiValues getIncumbentValue() {
		// TODO Auto-generated method stubValueRoutesVR
		return bestValue;
	}

	@Override
	public int getCurrentIteration() {
		// TODO Auto-generated method stub
		return currentIter;
	}
	
	@Override
	public ValueRoutesVR getIncumbent() {
		// TODO Auto-generated method stub
		return bestSolution;
	}

}
