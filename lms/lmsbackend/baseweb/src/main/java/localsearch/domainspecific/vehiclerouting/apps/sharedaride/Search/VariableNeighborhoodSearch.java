package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.ValueRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
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

public class VariableNeighborhoodSearch implements ISearch {

	private VRManager mgr;
	private VarRoutesVR XR;
	private LexMultiValues bestValue;
	private ValueRoutesVR bestSolution;
	private int currentIter;
	private LexMultiFunctions F;
	private ArrayList<ArrayList<INeighborhoodExplorer>> listNeighborhoodExplorer;
	private int maxStable;
	private int nic;
	private HashMap<INeighborhoodExplorer, Integer> mN2ID;
	LexMultiValues preRound;
	int limit;
	int cntTimeRestart;
	ArrayList<Point>pickup;
	ArrayList<Point>delivery;
	int numRouteReset = 0;
	boolean isImprove = false;
	int nRDontImprove = 0;
	public VariableNeighborhoodSearch(VRManager mgr, LexMultiFunctions F, ArrayList<ArrayList<INeighborhoodExplorer>> listNeighborhoodExplorer, ArrayList<Point>pk,ArrayList<Point>dl){
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.maxStable = 40;
		this.listNeighborhoodExplorer = listNeighborhoodExplorer;
		this.pickup = pk;
		this.delivery = dl;
	}
	
	private void updateBest() {
		bestValue.set(F.getValues());
		bestSolution.store();
		nic = 0;
		isImprove = true;
	}

	public void setMaxStable(int maxStable){
		this.maxStable = maxStable;
	}
	
	private void restart(){
		System.out.println(name() + "::restart............");
		
		XR.setRandom();
		if(F.getValues().lt(bestValue))
			updateBest();
		cntTimeRestart++;
	}
	
	
	boolean isBetter(LexMultiValues bestEval)
	{
		boolean flag = false;
		for(int i=0;i<bestEval.size();++i)
		{
			double v = bestEval.get(i);
			if(v < 0)
			{
				flag = true;
				break;
			}
			if(v > 0)
			{
				flag = false;
				break;
			}
		}
		return flag;
	}
@Override 
public void search(int maxIter, int timeLimit){
	
}
public void search(int maxIter, int timeLimit, String outDir){
	bestSolution = new ValueRoutesVR(XR);
	currentIter = 0;
	//XR.setRandom();
	bestSolution.store();
	nic = 0;
	Neighborhood N = new Neighborhood(mgr);
	bestValue = new LexMultiValues(F.getValues());
	preRound = F.getValues();
	double t0 = System.currentTimeMillis();
	System.out.println(name() + "::search, init bestValue = " + bestValue.toString());
	System.out.println(XR.toString());
	double previous = t0;
	LexMultiValues bestCurTurn = F.getValues();
	cntTimeRestart = 0;
	int km4 = 0;
	int km2 = 0;
	int onePoint = 0;
	int twoPoint = 0;
	int orOpt = 0;
	int cross = 0;
	int twoOpt5 = 0;
	int threeOpt5 = 0;
	LexMultiValues zero = new LexMultiValues();
	zero.fill(F.size(), 0);
	ArrayList<Point> allPoint = XR.getClientPoints();
	Random rand = new Random();
	while (currentIter < maxIter) {
		double curTime = System.currentTimeMillis();
		double t = curTime - t0;
		if (t  > timeLimit)
		{
			//System.out.println(t+"  -  "+timeLimit);
			break;
		}
		//System.out.println(t/1000+"    "+(curTime-previous)/1000+"   "+t/(currentIter*1000));
		previous = curTime;
		
		N.clear();
		LexMultiValues bestEval = new LexMultiValues();
		//bestEval.fill(F.size(), Double.MAX_VALUE);//search min violation.
		bestEval.fill(F.size(), 0);//vio = 0,cost = current cost.
		
		LexMultiValues curValue = F.getValues();
		for(int turn = 0; turn < listNeighborhoodExplorer.size(); ++ turn)
		{
			System.out.println("turn : "+turn);
			ArrayList<INeighborhoodExplorer> neighborhoodExplorer = listNeighborhoodExplorer.get(turn);
			for(INeighborhoodExplorer NI : neighborhoodExplorer)
			{
				//NI.exploreNeighborhood(N, bestEval); 
				NI.exploreNeighborhood(N, bestEval); 
			}
			
			if(isBetter(bestEval))
				break;
		}
		
		if (N.hasMove()) {
			LexMultiValues oldV = F.getValues();
			
				IVRMove m = N.getAMove();
				
				
				m.move();
				if(m instanceof KPointsMove)
				{
					KPointsMove km = (KPointsMove)m;
					if(km.getX().size() == 4)
						km4++;
					if(km.getX().size() == 2)
						km2++;					
				}
				if(m instanceof OnePointMove)
					onePoint++;
				if(m instanceof TwoPointsMove)
					twoPoint ++;
				if(m instanceof OrOptMove1)
				{
					int i1 = XR.index(((OrOptMove1) m).getX1());
					int i2 = XR.index(((OrOptMove1) m).getX2());
					System.out.println(i2-i1);
					orOpt++;
				}
				if(m instanceof CrossExchangeMove)
					cross++;
				if(m instanceof TwoOptMove5)
					twoOpt5++;
				if(m instanceof ThreeOptMove5)
					threeOpt5++;
				System.out.println("km4 = "+km4);
				System.out.println("km2 = "+km2);
				System.out.println("one = "+onePoint);
				System.out.println("two = "+twoPoint);
				System.out.println("orOpt = "+orOpt);
				System.out.println("cross = "+cross);
				System.out.println("two opt 5 = "+twoOpt5);
				System.out.println("three opt 5 = "+threeOpt5);
				LexMultiValues newV = F.getValues();
				if(Math.abs(newV.get(0)- oldV.get(0) - bestEval.get(0)) > 1e-5 || Math.abs(newV.get(1)- oldV.get(1) - bestEval.get(1)) > 1e-5  )
				{
					System.out.println("error "+newV.toString()+"  "+oldV.toString()+"  "+bestEval.toString()+"  "+(newV.get(0) - oldV.get(0)));
					System.out.println(XR.toString());
					System.exit(0);
				}
			
			
			System.out.println(name() + "::search, step " + currentIter + ", F = " + F.getValues().toString() + ", best = " + bestValue.toString());
			if(F.getValues().lt(bestValue)){
				updateBest();
			}
			if(F.getValues().lt(bestCurTurn))
			{
				nic = 0;
				bestCurTurn = F.getValues();
			}
			else{
				nic++;
//				if(nic > maxStable){
//					restart();
//					nic = 0;
//					bestCurTurn = F.getValues();
//				}
			}
			try{
				PrintWriter out = new PrintWriter(new FileOutputStream(outDir, true));
				out.println("ShareARide::search vio = " + F.getValues().get(0) + ", obj = " + F.getValues().get(1) + ", iter = " + currentIter + ", time = " + t);
				out.close();
			}catch(Exception e){
				
			}
		} else {
			System.out.println(name()
					+ "::search --> no move available, break");
			break;
		}
//		if(currentIter%40==0)
//			System.out.println(XR.toString());
		//System.out.println("currentIter = " + currentIter);
		currentIter++;
	}

	XR.setValue(bestSolution);
	System.out.println(XR.toString());
	
	System.out.println("Best = " + F.getValues().toString());
	System.out.println("bestValues = " + bestValue.toString());
}
	
	public String name(){
		return "VariableNeighborhoodSearch";
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
	public int getCntRestart()
	{
		return cntTimeRestart;
	}
	
	@Override
	public ValueRoutesVR getIncumbent() {
		// TODO Auto-generated method stub
		return bestSolution;
	}

}
