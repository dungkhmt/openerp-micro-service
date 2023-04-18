package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class AddRemovePoints implements IVRMove {
	
	private VRManager mgr;
	private Point x;
	private Point y;
	private Point z;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	
	public AddRemovePoints(VRManager mgr, LexMultiValues eval, Point x, Point y, Point z, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.z = z;
		this.NE = NE;
	}
	public AddRemovePoints(VRManager mgr, LexMultiValues eval, Point x, Point y, Point z){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.z = z;
		this.NE = null;
	}
	
	
	public void move() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::move(" + x + "," + y + "," + z + ") " + eval);
		mgr.performAddRemovePoints(x, y, z);
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
		return "AddRemovePoints";
	}
	
	public Point getX() {
		return x;
	}
	public Point getY() {
		return y;
	}
	public Point getZ() {
		return z;
	}
}
