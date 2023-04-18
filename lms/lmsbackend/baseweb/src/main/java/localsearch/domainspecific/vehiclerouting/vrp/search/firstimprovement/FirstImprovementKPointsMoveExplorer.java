
/*
 * authors: Pham Quang Dung, dungkhmt@gmail.com
 * start date: 12/02/2016
 */
package localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.AddOnePoint;
import localsearch.domainspecific.vehiclerouting.vrp.moves.CrossExchangeMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.KPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

import java.io.PrintWriter;
import java.util.*;
public class FirstImprovementKPointsMoveExplorer implements INeighborhoodExplorer {

	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	private int K;// number of points included in the move
	private ArrayList<Point> points;
	private ArrayList<Point> allPoints;// include null points
	
	// data structures for enumeration
	private int N;// number of elements = allPoints.size()
	private int m;// number of starting, terminating points
	//private int[] x;
	//private int[] y;
	private int[] z;// variables are z[1..2K] = x[0],..x[K-1], y[0],..y[K-1]
					//if(1 <= i <= K): z[i] = m+1,...,N-1 (0 is null point, 1,2,...,m are starting and terminating points)
					// if K=1 <= i <= 2K: z[i] = 0,1,...,N-1 z[i] \neq z[1..K], 
	private boolean[] used;// used[v] = T if value v is used in z[0..2K-1]
	private Neighborhood neighborhood;
	private LexMultiValues bestEval;
	private ArrayList<Point> x;
	private ArrayList<Point> y;
	private PrintWriter log = null;
	private boolean found;
	private LexMultiValues zeros;
	private HashSet<Point> mandatory;
	
	public FirstImprovementKPointsMoveExplorer(VarRoutesVR XR, LexMultiFunctions F, int K, HashSet<Point> mandatory) {
		this.XR = XR;
		this.F = F;
		this.mandatory = mandatory;
		this.mgr = XR.getVRManager();
		this.K = K;
		z = new int[2*K+1];
		//N = XR.getAllPoints().size()+1;
		
		
		allPoints = new ArrayList<Point>();
		allPoints.add(CBLSVR.NULL_POINT);
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			allPoints.add(XR.getStartingPointOfRoute(k));
			allPoints.add(XR.getTerminatingPointOfRoute(k));
		}
		m = allPoints.size()-1;
		for(int i = 0; i < XR.getClientPoints().size(); i++)
			allPoints.add(XR.getClientPoints().get(i));
		N = allPoints.size();
		used = new boolean[N];
		
		System.out.println(name() + "::constructor, N = " + N + ", m = " + m);
		x = new ArrayList<Point>();
		y = new ArrayList<Point>();
		zeros = new LexMultiValues(0,0);
		try{
			log = new PrintWriter(name() + "-log.txt");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void finalize(){
		log.close();
	}
	private ArrayList<Point> copy(ArrayList<Point> z){
		ArrayList<Point> r = new ArrayList<Point>();
		for(int i = 0; i < z.size(); i++)
			r.add(z.get(i));
		return r;	
	}
	private boolean checkMandatory(ArrayList<Point> x, ArrayList<Point> y){
		for(int i= 0; i < x.size(); i++){
			Point p = x.get(i);
			if(mandatory.contains(p) && y.get(i) == CBLSVR.NULL_POINT) return false;
		}
		return true;
	}
	private void explore(){
		x.clear();
		y.clear();
		for(int i = 1; i <= K; i++){
			x.add(allPoints.get(z[i]));
		}
		for(int i = K+1; i <= 2*K; i++){
			y.add(allPoints.get(z[i]));
		}
		/*
		String s = "(";
		for(int i = 0; i < x.size(); i++) if(x.get(i) == CBLSVR.NULL_POINT) s += "N, "; else s += XR.getIndex(x.get(i)) + ",";
		s += "), (";
		for(int i = 0; i < y.size(); i++) if(y.get(i) == CBLSVR.NULL_POINT) s += "N, "; else  s += XR.getIndex(y.get(i)) + ",";
		s += ")";
		log.println(name() + "::explore " + s);
		*/
		if (XR.checkPerformKPointsMove(x, y) && checkMandatory(x,y)) {
			//log.println(name() + "::explore LEGAL move " + s);
			LexMultiValues eval = F.evaluateKPointsMove(x, y);
			if (eval.lt(bestEval)) {
				mgr.performKPointsMove(x, y);
				System.out.println(name() + "::exploreNeighborhood, F = " + F.getValues().toString());
			}
			//if(eval.lt(zeros)) found = true;// first improvement
		}
	}
	private void genAndExplore(int k){
		if(found) return;
		if(k <= K){
			for(int v = z[k-1]+1; v < N; v++){
				z[k] = v;
				used[v] = true;
				if(k == 2*K){
					explore();
				}else{
					genAndExplore(k+1);
				}
				used[v] = false;
			}
		}else{
			for(int v = 0; v < N; v++) if(!used[v]){
				z[k] = v;
				if(k == 2*K){
					explore();
				}else{
					genAndExplore(k+1);
				}
			}
		}
	}
	
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		neighborhood = N;
		this.bestEval = bestEval;
		z[0] = m;
		for(int i = 0; i < used.length; i++) used[i] = false;
		found = false;
		genAndExplore(1);
		//finalize();
		//System.exit(-1);
	}

	
	public void performMove(IVRMove m) {
		// TODO Auto-generated method stub

	}

	public String name(){
		return "GreedyKPointsMoveExplorer";
	}
}
