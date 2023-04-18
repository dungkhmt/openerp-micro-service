
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date:27/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.ValueRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class GenericLocalSearch implements ISearch {

	protected VRManager mgr;
	protected VarRoutesVR XR;
	protected LexMultiValues bestValue;
	protected LexMultiValues knownOptimal = null;
	protected ValueRoutesVR bestSolution;
	protected int currentIter;
	protected LexMultiFunctions F;
	//protected ArrayList<INeighborhoodExplorer> neighborhoodExplorer;
	protected NeighbohoodExplorerManager NEM;
	protected int maxStable;
	protected int nic;
	//protected HashMap<INeighborhoodExplorer, Integer> mN2ID;
	protected double time_to_best;
	protected double time;
	protected double t0;
	protected double t;
	private Random R = new Random();
	
	// parameters
	public boolean noMoveBreak = false;
	public boolean verbose = true;
	public boolean pertubationNeighborhood = true;
	public boolean adaptNeighborhood = true;
	public int lenAdaptNeighborhood = 10;
	
	public GenericLocalSearch(VRManager mgr, LexMultiFunctions F, ArrayList<INeighborhoodExplorer> neighborhoodExplorer){
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		//this.neighborhoodExplorer = neighborhoodExplorer;
		NEM = new NeighbohoodExplorerManager(neighborhoodExplorer);
		this.maxStable = 100;
		
		/*
		mN2ID = new HashMap<INeighborhoodExplorer, Integer>();
		for(int i = 0; i < neighborhoodExplorer.size(); i++){
			INeighborhoodExplorer in = neighborhoodExplorer.get(i);
			mN2ID.put(in, i);
		}
		*/
		
	}
	public GenericLocalSearch(VRManager mgr){
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.maxStable = 100;
		NEM = new NeighbohoodExplorerManager();
	}
	public void setKnownOptimal(LexMultiValues o){
		this.knownOptimal = o;
	}
	
	public void perturbNeighborhoodExplorer(){
		NEM.perturb();
		/*
		INeighborhoodExplorer[] a = new INeighborhoodExplorer[neighborhoodExplorer.size()];
		for(int i = 0;i < neighborhoodExplorer.size(); i++)
			a[i] = neighborhoodExplorer.get(i);
		for(int step = 0; step < a.length; step++){
			int i = R.nextInt(a.length);
			int j = R.nextInt(a.length);
			INeighborhoodExplorer tmp = a[i]; a[i] = a[j]; a[j] = tmp;
		}
		neighborhoodExplorer.clear();
		mN2ID.clear();
		for(int i = 0; i < a.length; i++){
			neighborhoodExplorer.add(a[i]);
			mN2ID.put(a[i], i);
		}
		*/
	}
	public void setObjectiveFunction(LexMultiFunctions F){
		this.F = F;
	}
	public void setNeighborhoodExplorer(ArrayList<INeighborhoodExplorer> NE){
		NEM.setNeighborhoodExplorers(NE);
		/*
		this.neighborhoodExplorer = NE;
		
		mN2ID = new HashMap<INeighborhoodExplorer, Integer>();
		for(int i = 0; i < neighborhoodExplorer.size(); i++){
			INeighborhoodExplorer in = neighborhoodExplorer.get(i);
			mN2ID.put(in, i);
		}
		*/
	}
	public void updateBest() {
		bestValue.set(F.getValues());
		bestSolution.store();
		nic = 0;
	}

	public void setMaxStable(int maxStable){
		this.maxStable = maxStable;
	}
	public double getTimeToBest(){
		return time_to_best;
	}
	public void restart(int currentIter){
		if(verbose) System.out.println(name() + "::restart............");
		//XR.setRandom();
		//generateInitialSolution();
		perturb(XR.getNbClients());
		if(F.getValues().lt(bestValue)){
			updateBest();
		}
		nic = 0;
		NEM.restart(currentIter);
	}
	 
	public void generateInitialSolution(){
		XR.setRandom();
	}
	
	public void processNeighbor(){
		if(F.getValues().lt(bestValue)){
			updateBest();
			time_to_best = System.currentTimeMillis() - t0;
			time_to_best = time_to_best * 0.001;
			
			if(knownOptimal != null){
				if(F.getValues().leq(knownOptimal)){
					if(verbose) System.out.println(name() + "::search, step " + currentIter + ", time " + t + ", F = " + F.getValues().toString() + 
							", best = " + bestValue.toString() + ", time_to_best = " + time_to_best + 
							", REACH KNOWN OPTIMAL --> BREAK");
					//break;
				}
			}
		}else{
			nic++;
			if(nic > maxStable){
				restart(currentIter);
			}
		}

	}
	public void endOfAnIteration(){
		// to be overridden in extended classes
	}
	public void search(int maxIter, int timeLimit){
		bestSolution = new ValueRoutesVR(XR);
		currentIter = 0;
		generateInitialSolution();
		if(verbose) System.out.println(name() + "::search, initial solution = " + XR.toString());
		nic = 0;
		Neighborhood N = new Neighborhood(mgr);
		bestValue = new LexMultiValues(F.getValues());
		updateBest();
		t0 = System.currentTimeMillis();
		if(verbose) System.out.println(name() + "::search, init bestValue = " + bestValue.toString());
		//System.exit(-1);
		if(verbose) System.out.println(XR.toString());
		while (currentIter < maxIter) {
			t = System.currentTimeMillis() - t0;
			t = t * 0.001;
			if (t  > timeLimit)
				break;
			N.clear();
			
			LexMultiValues bestEval = new LexMultiValues();
			//bestEval.fill(F.size(), CBLSVR.MAX_INT);
			bestEval.fill(F.size(), 0);
			
			if(pertubationNeighborhood) perturbNeighborhoodExplorer();
			
			NEM.exploreNeighborhoodsFirstImprovement(N, bestEval, currentIter);
			
			//System.out.println("adaptNeighborhood = " + adaptNeighborhood); System.exit(-1);
			if(adaptNeighborhood) NEM.adaptNeighborhoods(lenAdaptNeighborhood,currentIter);
			
			if (N.hasMove()) {
				IVRMove m = N.getAMove();
				m.move();
				
				if(verbose) System.out.println(name() + "::search, step " + currentIter + ", time " + t + ", F = " + F.getValues().toString() + 
						", best = " + bestValue.toString() + ", time_to_best = " + time_to_best + 
						", nic/maxStable = " + nic + "/" + maxStable);
				
				processNeighbor();
				
				if(knownOptimal != null){
					if(F.getValues().leq(knownOptimal)){
						if(verbose) System.out.println(name() + "::search, step " + currentIter + ", time " + t + ", F = " + F.getValues().toString() + 
								", best = " + bestValue.toString() + ", time_to_best = " + time_to_best + 
								", REACH KNOWN OPTIMAL --> BREAK");
						break;
					}
				}
				/*
				if(F.getValues().lt(bestValue)){
					updateBest();
					time_to_best = System.currentTimeMillis() - t0;
					time_to_best = time_to_best * 0.001;
					
					if(knownOptimal != null){
						if(F.getValues().leq(knownOptimal)){
							if(verbose) System.out.println(name() + "::search, step " + currentIter + ", time " + t + ", F = " + F.getValues().toString() + 
									", best = " + bestValue.toString() + ", time_to_best = " + time_to_best + 
									", REACH KNOWN OPTIMAL --> BREAK");
							break;
						}
					}
				}else{
					nic++;
					if(nic > maxStable){
						restart(currentIter);
					}
				}
				*/
			} else {
				if(verbose) System.out.println(name()
						+ "::search --> no move available, break");
				
				if(noMoveBreak) break;
				else restart(currentIter);
			}
			// System.out.println(obj.toString());

			endOfAnIteration();
			
			currentIter++;

		}

		XR.setValue(bestSolution);

		if(verbose) System.out.println("Best = " + F.getValues().toString());
		if(verbose) System.out.println("bestValues = " + bestValue.toString());
		
	}
	public void perturb(int nbSteps){
		for(int k = 1; k <= nbSteps; k++){
			ArrayList<Point> P = XR.collectClientPointsOnRoutes();
			if(P.size() >= 2){
				for(int i = 1; i <= 10; i++){
					Point x = P.get(R.nextInt(P.size()));
					Point y = P.get(R.nextInt(P.size()));
					
					if(x != y && XR.checkPerformOnePointMove(x, y)){
						mgr.performOnePointMove(x, y);
						break;
					}
					
				}
			}
		}
	}
	public void searchImprove(int maxIter, int timeLimit){
		bestSolution = new ValueRoutesVR(XR);
		currentIter = 0;
		//XR.setRandom();
		nic = 0;
		Neighborhood N = new Neighborhood(mgr);
		bestValue = new LexMultiValues(F.getValues());
		updateBest();
		double t0 = System.currentTimeMillis();
		System.out.println(name() + "::search, init bestValue = " + bestValue.toString());
		//System.exit(-1);
		System.out.println(XR.toString());
		while (currentIter < maxIter) {
			double t = System.currentTimeMillis() - t0;
			if (t  > timeLimit)
				break;
			N.clear();
			
			LexMultiValues bestEval = new LexMultiValues();
			bestEval.fill(F.size(), CBLSVR.MAX_INT);
			
			NEM.exploreNeighborhoodsFirstImprovement(N, bestEval,currentIter);
			/*
			for(INeighborhoodExplorer NI: neighborhoodExplorer){
				NI.exploreNeighborhood(N, bestEval);
			}
			*/
			
			if (N.hasMove()) {
				IVRMove m = N.getAMove();
				m.move();
				
				System.out.println(name() + "::search, step " + currentIter + ", F = " + F.getValues().toString() + ", best = " + bestValue.toString());
				if(F.getValues().lt(bestValue)){
					updateBest();
				}else{
					nic++;
					if(nic > maxStable){
						//restart();
						nic = 0;
						perturb(20);
					}
				}
			} else {
				System.out.println(name()
						+ "::search --> no move available, break");
				break;
			}
			// System.out.println(obj.toString());

			currentIter++;

		}

		XR.setValue(bestSolution);

		System.out.println("Best = " + F.getValues().toString());
		System.out.println("bestValues = " + bestValue.toString());
		
	}
	
	public String name(){
		return "GenericLocalSearch";
	}
	
	public LexMultiValues getIncumbentValue() {
		// TODO Auto-generated method stubValueRoutesVR
		return bestValue;
	}

	public LexMultiFunctions getFunction(){
		return F;
	}
	public int getCurrentIteration() {
		// TODO Auto-generated method stub
		return currentIter;
	}

	
	public ValueRoutesVR getIncumbent() {
		// TODO Auto-generated method stub
		return bestSolution;
	}

}
