
package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class CrossExchangeMove implements IVRMove {

	private VRManager mgr;
	private Point x1;
	private Point y1;
	private Point x2;
	private Point y2;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	public CrossExchangeMove(VRManager mgr, LexMultiValues eval, Point x1, Point y1, Point x2, Point y2, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.NE = NE;
	}
	public CrossExchangeMove(VRManager mgr, LexMultiValues eval, Point x1, Point y1, Point x2, Point y2){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.NE = null;
	}
	
	public String name(){
		return "CrossExchangeMove";
	}
	
	
	public void move() {
		System.out.println(name() + "::move(" + x1 + "," + y1 + "," + x2 + "," + y2 + ") " + eval);
		mgr.performCrossExchangeMove(x1, y1, x2, y2);
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		return eval;
	}
	
	public INeighborhoodExplorer getNeighborhoodExplorer(){
		return NE;
	}
	
	public Point getX1(){ return x1;}
	public Point getY1(){ return y1;}
	public Point getX2(){ return x2;}
	public Point getY2(){ return y2;}
}
