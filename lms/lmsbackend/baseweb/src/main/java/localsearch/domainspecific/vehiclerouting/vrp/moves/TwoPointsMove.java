package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;

import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

/*
 * authors: Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 04/09/2015
 */

public class TwoPointsMove implements IVRMove {
	
	private VRManager mgr;
	private Point x;
	private Point y;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	public TwoPointsMove(VRManager mgr, LexMultiValues eval, Point x, Point y, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = NE;
	}
	public TwoPointsMove(VRManager mgr, LexMultiValues eval, Point x, Point y){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = null; 
	}
	
	public String name(){
		return "TwoPointMove";
	}
	
	
	public void move() {
		System.out.println(name() + "::move(" + x + "," + y + ") " + eval);
		mgr.performTwoPointsMove(x, y);
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
