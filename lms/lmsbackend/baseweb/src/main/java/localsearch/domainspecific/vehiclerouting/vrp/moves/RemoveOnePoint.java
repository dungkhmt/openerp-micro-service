package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class RemoveOnePoint implements IVRMove {

	private VRManager mgr;
	private Point x;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	
	public RemoveOnePoint(VRManager mgr, LexMultiValues eval, Point x, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.NE = NE;
	}
	public RemoveOnePoint(VRManager mgr, LexMultiValues eval, Point x){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.NE = null;
	}
	
	
	@Override
	public void move() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::move(" + x +  ") " + eval);
		mgr.performRemoveOnePoint(x);
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
		return "RemoveOnePoint";
	}

	public Point getX() {
		return x;
	}
}
