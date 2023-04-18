package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class OrOptMove2 implements IVRMove {

	private VRManager mgr;
	private Point x1;
	private Point x2;
	private Point y;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	public OrOptMove2(VRManager mgr, LexMultiValues eval, Point x1, Point x2, Point y, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.x2 = x2;
		this.y = y;
		this.NE = NE;
	}
	public OrOptMove2(VRManager mgr, LexMultiValues eval, Point x1, Point x2, Point y){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.x2 = x2;
		this.y = y;
		this.NE = null;
	}
	
	public String name(){
		return "OrOptMove2";
	}
	
	
	public void move() {
		System.out.println(name() + "::move(" + x1 + ", " + x2 + ", " + y + ") " + eval);
		mgr.performOrOptMove2(x1, x2, y);
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		return eval;
	}

	
	public INeighborhoodExplorer getNeighborhoodExplorer(){
		return this.NE;
	}
	public Point getX1(){ return x1;}
	public Point getX2(){ return x2;}
	public Point getY(){ return y;}
}
