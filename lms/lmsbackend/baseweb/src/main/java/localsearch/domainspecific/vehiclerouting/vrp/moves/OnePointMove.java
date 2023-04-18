package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com)
 * date: 30/08/2015
 */
public class OnePointMove implements IVRMove {
	private VRManager mgr;
	private Point x;
	private Point y;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	public OnePointMove(VRManager mgr, LexMultiValues eval, Point x, Point y, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = NE;
	}
	public OnePointMove(VRManager mgr, LexMultiValues eval, Point x, Point y){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = null;
	}
	public String name(){
		return "OnePointMove";
	}
	
	public void move() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::move(" + x + "," + y + ") " + eval);
		mgr.performOnePointMove(x, y);
		
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		// TODO Auto-generated method stub
		return eval;
	}

	
	public INeighborhoodExplorer getNeighborhoodExplorer(){
		return NE;
	}
	
	public Point getX(){
		return x;
	}
	public Point getY(){
		return y;
	}
}
