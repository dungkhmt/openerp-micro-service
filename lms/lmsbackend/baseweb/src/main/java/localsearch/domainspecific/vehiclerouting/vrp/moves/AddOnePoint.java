package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class AddOnePoint implements IVRMove {
	
	private VRManager mgr;
	private Point x;
	private Point y;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	
	public AddOnePoint(VRManager mgr, LexMultiValues eval, Point x, Point y, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = NE;
	}
	public AddOnePoint(VRManager mgr, LexMultiValues eval, Point x, Point y){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = null;
	}
	
	
	
	public void move() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::move(" + x + "," + y + ") " + eval);
		mgr.performAddOnePoint(x, y);
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		// TODO Auto-generated method stub
		return eval;
	}

	
	public INeighborhoodExplorer getNeighborhoodExplorer() {
		// TODO Auto-generated method stub
		return NE;
	}

	
	public String name() {
		// TODO Auto-generated method stub
		return "AddOnePoint";
	}

	public Point getX() {
		return x;
	}
	public Point getY() {
		return y;
	}
}
