package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class RemoveTwoPoints implements IVRMove {

	private VRManager mgr;
	private Point x1;
	private Point x2;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	
	public RemoveTwoPoints(VRManager mgr, LexMultiValues eval, Point x1, Point x2, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.x2 = x2;
		this.NE = NE;
	}
	public RemoveTwoPoints(VRManager mgr, LexMultiValues eval, Point x1, Point x2){
		this.mgr = mgr;
		this.eval = eval;
		this.x1 = x1;
		this.x2 = x2;
		this.NE = null;
	}
	
	
	@Override
	public void move() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::move(" + x1 + ", " + x2 +  ") " + eval);
		mgr.performRemoveTwoPoints(x1, x2);
		if(NE != null) NE.performMove(this);
	}

	@Override
	public LexMultiValues evaluation() {
		// TODO Auto-generated method stub
		return eval;
	}

	@Override
	public INeighborhoodExplorer getNeighborhoodExplorer() {
		// TODO Auto-generated method stub
		return NE;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "RemoveTwoPoints";
	}

	public Point getX1() {
		return x1;
	}
	public Point getX2() {
		return x2;
	}
}
