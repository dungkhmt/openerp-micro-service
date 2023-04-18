package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 27/12/2015
 */

public class MoveTwoPointsMove implements IVRMove {
	
	private VRManager mgr;
	private Point x1;
	private Point x2;
	private Point y1;
	private Point y2;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	
	public MoveTwoPointsMove(VRManager mgr, LexMultiValues eval, Point x1, Point x2, Point y1, Point y2, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.NE = NE;
	}
	public MoveTwoPointsMove(VRManager mgr, LexMultiValues eval, Point x1, Point x2, Point y1, Point y2){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.NE = null;
	}
	public MoveTwoPointsMove(VRManager mgr, double eval, Point x1, Point x2, Point y1, Point y2){
		this.mgr = mgr;
		this.eval = new LexMultiValues(); this.eval.add(eval);
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.NE = null;
	}
	
	public String name(){
		return "MoveTwoPointMove";
	}
	
	
	public void move() {
		System.out.println(name() + "::move(" + x1 + "," + x2 + "," + y1 + "," + y2 + ") " + eval);
		mgr.performTwoPointsMove(x1, x2, y1, y2);
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		return eval;
	}
	
	public INeighborhoodExplorer getNeighborhoodExplorer(){
		return NE;
	}
	
	public Point getX1(){ return x1;}
	public Point getX2(){ return x2;}
	public Point getY1(){ return y1;}
	public Point getY2(){ return y2;}
}
