/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 11/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.search;

import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;

public class Neighborhood {
	private VRManager mgr;
	private ArrayList<IVRMove> moves;
	private Random R;
	private LexMultiValues eval;
	public Neighborhood(VRManager mgr){
		this.mgr = mgr;
		moves = new ArrayList<IVRMove>();
		R = new Random();
	}
	public void add(IVRMove m){
		moves.add(m);
		if(eval == null) eval = m.evaluation();
		else if(m.evaluation().lt(eval)){
			eval = m.evaluation();
		}
	}
	public ArrayList<IVRMove> getMoves(){
		return moves;
	}
	public void clear(){
		moves.clear();
		eval = null;
	}
	public boolean hasImprovement(){
		if(eval == null) return false;
		return eval.lt(0);
	}
	public int size(){
		return moves.size();
	}
	public boolean hasMove(){
		return moves.size() > 0;
	}
	public IVRMove getAMove(){
		return moves.get(R.nextInt(moves.size()));
	}
}
