package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;

import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class TwoOptMove8 implements IVRMove {

	private VRManager mgr;
	private Point x;
	private Point y;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	public TwoOptMove8(VRManager mgr, LexMultiValues eval, Point x, Point y, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = NE;
	}
	public TwoOptMove8(VRManager mgr, LexMultiValues eval, Point x, Point y){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = null;
	}
	
	public String name(){
		return "TwoOptMove8";
	}
	
	
	public void move() {
		System.out.println(name() + "::move(" + x + "," + y + ") " + eval);
		mgr.performTwoOptMove8(x, y);
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		return eval;
	}
	
	public INeighborhoodExplorer getNeighborhoodExplorer(){
		return NE;
	}
	public Point getX(){ return x;}
	public Point getY(){ return y;}
}
