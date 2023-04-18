package localsearch.domainspecific.vehiclerouting.vrp.search;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

import java.util.HashMap;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;

public class NeighbohoodExplorerManager {
	public ArrayList<INeighborhoodExplorer> neighborhoodExplorers;
	private HashMap<INeighborhoodExplorer, Integer> mNeighborhood2Index;
	private HashMap<INeighborhoodExplorer, Integer> lastIterationUsed;// lastIterationUsed.get(ne) is the last iteration that
											// neighborhoodExplorer ne has been used
	private HashMap<INeighborhoodExplorer, Boolean> active;// active[i] = true, INeighborhoodExplorer[i] is active
	//private ArrayList<INeighborhoodExplorer> selected;// selected[i] = J means that neighborhood J is selected at iteration i 
	private Random R = new Random();
	
	public NeighbohoodExplorerManager(){
		neighborhoodExplorers = new ArrayList<INeighborhoodExplorer>();
		//selected = new ArrayList<INeighborhoodExplorer>();
		active = new HashMap<INeighborhoodExplorer, Boolean>();
		mNeighborhood2Index =new HashMap<INeighborhoodExplorer, Integer>();
		lastIterationUsed = new HashMap<INeighborhoodExplorer, Integer>();
		for(int i = 0; i < neighborhoodExplorers.size(); i++){
			mNeighborhood2Index.put(neighborhoodExplorers.get(i), i);
			lastIterationUsed.put(neighborhoodExplorers.get(i), 0);
		}
	}
	public NeighbohoodExplorerManager(ArrayList<INeighborhoodExplorer> neighborhoodExplorers){
		this.neighborhoodExplorers = neighborhoodExplorers;
		//selected = new ArrayList<INeighborhoodExplorer>();
		active = new HashMap<INeighborhoodExplorer, Boolean>();
		for(INeighborhoodExplorer NE: neighborhoodExplorers)
			active.put(NE, true);
		mNeighborhood2Index = new HashMap<INeighborhoodExplorer, Integer>();
		lastIterationUsed = new HashMap<INeighborhoodExplorer, Integer>();
		
		for(int i = 0; i < neighborhoodExplorers.size(); i++){
			mNeighborhood2Index.put(neighborhoodExplorers.get(i), i);
			lastIterationUsed.put(neighborhoodExplorers.get(i), 0);
		}
		
		
	}
	public void setNeighborhoodExplorers(ArrayList<INeighborhoodExplorer> neighborhoodExplorers){
			this.neighborhoodExplorers = neighborhoodExplorers;
			//selected = new ArrayList<INeighborhoodExplorer>();
			active = new HashMap<INeighborhoodExplorer, Boolean>();
			for(INeighborhoodExplorer NE: neighborhoodExplorers)
				active.put(NE, true);
			mNeighborhood2Index = new HashMap<INeighborhoodExplorer, Integer>();
			for(int i = 0; i < neighborhoodExplorers.size(); i++){
				mNeighborhood2Index.put(neighborhoodExplorers.get(i), i);
				lastIterationUsed.put(neighborhoodExplorers.get(i), 0);
			}
	}
	
	public void restart(int currentIteration){
		int actives = getNbActiveNeighborhoods();
		//System.out.println(name() + "::restart, #active neighborhoods = " + getNbActiveNeighborhoods());
		for(INeighborhoodExplorer ne: neighborhoodExplorers){
			enable(ne);
			lastIterationUsed.put(ne, currentIteration);
		}
		//selected.clear();
		//System.out.println(name() + "::restart, #active neighborhoods = " + getNbActiveNeighborhoods());
		//if(getNbActiveNeighborhoods() > actives) System.exit(-1);
	}
	
	public void perturb(){
		INeighborhoodExplorer[] a = new INeighborhoodExplorer[neighborhoodExplorers.size()];
		for(int i = 0;i < neighborhoodExplorers.size(); i++)
			a[i] = neighborhoodExplorers.get(i);
		for(int step = 0; step < a.length; step++){
			int i = R.nextInt(a.length);
			int j = R.nextInt(a.length);
			INeighborhoodExplorer tmp = a[i]; a[i] = a[j]; a[j] = tmp;
		}
		neighborhoodExplorers.clear();
		mNeighborhood2Index.clear();
		for(int i = 0; i < a.length; i++){
			neighborhoodExplorers.add(a[i]);
			mNeighborhood2Index.put(a[i], i);
		}
		
	}
	public void add(INeighborhoodExplorer ne){
		neighborhoodExplorers.add(ne);
		active.put(ne, true);
		
		mNeighborhood2Index.put(ne, neighborhoodExplorers.size()-1);
	}
	public void disable(INeighborhoodExplorer ne){
		active.put(ne, false);
	}
	public void enable(INeighborhoodExplorer ne){
		active.put(ne, true);
	}
	public int getNbActiveNeighborhoods(){
		int countActives = 0;
		for(INeighborhoodExplorer NI: neighborhoodExplorers)
			if(active.get(NI)){
				countActives++;	
			}
		return countActives;
	}
	public void exploreNeighborhoodsFirstImprovement(Neighborhood N, LexMultiValues bestEval, int currentIteration){
		int countActives = getNbActiveNeighborhoods();
		
		
		for(INeighborhoodExplorer NI: neighborhoodExplorers)
			if(active.get(NI)){
			NI.exploreNeighborhood(N, bestEval);
			if(N.hasImprovement()){
				//selected.add(NI);
				break;
			}
		}
		System.out.println(name() + "::exploreNeighborhoodFirstImprovement, countActives = " + countActives + ", moves.sz = " + N.getMoves().size());
		for(IVRMove m: N.getMoves()){
			INeighborhoodExplorer ne = m.getNeighborhoodExplorer();
			lastIterationUsed.put(ne, currentIteration);
		}
	}
	public String name(){
		return "NeighborhoodExplorerManager";
	}
	public void adaptNeighborhoods(int len, int currentIteration){
		//int[] lastVisited = new int[neighborhoodExplorers.size()];
		String lastVisited = "";
		for(INeighborhoodExplorer ne: neighborhoodExplorers){
			int lastIter = lastIterationUsed.get(ne);
			//lastVisited[mNeighborhood2Index.get(ne)] = lastIter;
			lastVisited += lastIter + " ";
			if(lastIter + len < currentIteration){
				disable(ne);
			}
		}
		
		System.out.println(name() + "::adaptNeighborhoods, len = " + len + ", curentIter = " + currentIteration + ", actives = " + 
		getNbActiveNeighborhoods() + ", lastVisited = " + lastVisited);
		
		/*
		System.out.println(name() + "::adaptNeighborhoods, selected.sz = " + selected.size() + ", len = " + len);
		if(selected.size() <= len) return;
		
		int[] count = new int[neighborhoodExplorers.size()];
		int sz = selected.size();
		Arrays.fill(count, 0);
		for(int i = sz-1; i >= sz - len; i--){
			INeighborhoodExplorer ne = selected.get(i);
			count[mNeighborhood2Index.get(ne)]++;
		}
		for(int i = 0; i < count.length; i++){
			if(count[i] == 0){
				disable(neighborhoodExplorers.get(i));
				System.out.println(name() + "::adaptNeighborhoods, disable " + neighborhoodExplorers.get(i).name());
			}
		}
		
		System.out.println(name() + "::adaptNeighborhoods, selected.sz = " + selected.size() + ", actives = " + getNbActiveNeighborhoods());
		*/
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		
	}

}
