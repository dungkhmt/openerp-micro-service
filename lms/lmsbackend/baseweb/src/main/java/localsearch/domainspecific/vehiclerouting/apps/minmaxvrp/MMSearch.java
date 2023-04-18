package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;

public class MMSearch extends GenericLocalSearch {
	private Random R;
	PrintWriter log = null;
	public MMSearch(VRManager mgr) {
		super(mgr);
		R = new Random();
		maxStable = 20;
	}
	public void initLog(String fn){
		try{
			log = new PrintWriter(fn);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void finalizeLog(){
		log.close();
	}
	public void endOfAnIteration(){
		log.println(getCurrentIteration() + "\t" + getFunction().getValues().get(1) + "\t" + getIncumbentValue().get(1));
	}

	public String name(){
		return "MMSearch";
	}
	public void processNeighbor(){
		//System.exit(-1);
		if(F.getValues().lt(bestValue)){
		//if(F.getValues().get(0) < bestValue.get(0)){
			boolean ok = (F.getValues().get(1) < bestValue.get(1));
				
			updateBest();
			
			if(ok){
				time_to_best = System.currentTimeMillis() - t0;
				time_to_best = time_to_best * 0.001;
				if(verbose) System.out.println(name() + "::processNeighbor, UPDATE time_to_best = " + time_to_best);
				
			}
			
			if(knownOptimal != null){
				if(F.getValues().leq(knownOptimal)){
					if(verbose) System.out.println(name() + "::processNeighbor, step " + currentIter + ", time " + t + ", F = " + F.getValues().toString() + 
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

	public void print() {
		System.out.println(XR.toString());
		// for(int k = 1; k <= XR.getNbRoutes(); k++)
		// System.out.println("cost[" + k + "] = " + costRoute[k-1].getValue());
		System.out.println("Cost = " + F.getValues().toString());
	}
	public void restart(int currentIter){
		System.out.println("RESTART----------------------------------------");
		generateInitialSolution();
		NEM.restart(currentIter);
		System.out.println("RESTART----------------------------------------F = " + F.getValues().toString());
		if(F.getValues().lt(bestValue)){
			updateBest();
		}
		nic = 0;
	}
	public void generateInitialSolutionOld() {
		for (Point p : XR.getClientPoints()) {
			ArrayList<Point> L = XR.collectCurrentClientAndStartPointsOnRoute();
			LexMultiValues bestEval = new LexMultiValues();
			bestEval.fill(1, CBLSVR.MAX_INT);
			Point sel_q = null;
			for (Point q : L) {
				LexMultiValues D = F.evaluateAddOnePoint(p, q);
				if (D.lt(bestEval)) {
					sel_q = q;
					bestEval = D;
				}
			}
			mgr.performAddOnePoint(p, sel_q);
			// print();

		}
		System.out.println("generateInitialSolution finished, cost = "
				+ F.getValues().toString());
		print();
	}

	
	public void generateInitialSolution() {
		mgr.performRemoveAllClientPoints();
		System.out.println("After removing all client points, XR = " + XR.toString());
		HashSet<Point> S = new HashSet<Point>();
		for (Point p : XR.getClientPoints()) {
			S.add(p);
		}
		while (S.size() > 0) {
			LexMultiValues eval = new LexMultiValues();
			eval.fill(1, CBLSVR.MAX_INT);
			Point sel_q = null;
			Point sel_p = null;
			ArrayList<PairPoints> Cand = new ArrayList<PairPoints>();
			for (Point p : S) {
				ArrayList<Point> L = XR
						.collectCurrentClientAndStartPointsOnRoute();
				for (Point q : L) {
					LexMultiValues e = F.evaluateAddOnePoint(p, q);
					if (e.lt(eval)) {
						Cand.clear();
						Cand.add(new PairPoints(p,q));
						eval = e;
					}else if(e.eq(eval)){
						Cand.add(new PairPoints(p,q));
					}
				}
			}
			PairPoints PP = Cand.get(R.nextInt(Cand.size()));
			sel_p = PP.x; sel_q = PP.y;
			mgr.performAddOnePoint(sel_p, sel_q);
			S.remove(sel_p);
		}
		System.out.println("generateInitialSolution finished, cost = "
				+ F.getValues().toString());
		print();
		
		//mgr.performRemoveAllClientPoints();
		//System.exit(-1);
	}

}
