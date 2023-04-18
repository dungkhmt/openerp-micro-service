package localsearch.domainspecific.vehiclerouting.vrp.moves;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import java.util.ArrayList;
public class KPointsMove implements IVRMove {
	private VRManager mgr;
	private ArrayList<Point> x;
	private ArrayList<Point> y;
	private LexMultiValues eval;
	private INeighborhoodExplorer NE;
	public KPointsMove(VRManager mgr, LexMultiValues eval, ArrayList<Point> x, ArrayList<Point> y, INeighborhoodExplorer NE){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = NE;
	}
	public KPointsMove(VRManager mgr, LexMultiValues eval, ArrayList<Point> x, ArrayList<Point> y){
		this.mgr = mgr;
		this.eval = eval;
		this.x = x;
		this.y = y;
		this.NE = null;
	}
	
	public KPointsMove(VRManager mgr, ArrayList<Point> x, ArrayList<Point> y){
		this.mgr = mgr;
		this.eval = null;
		this.x = x;
		this.y = y;
		this.NE = null;
	}
	public String name(){
		return "KPointsMove";
	}
	
	public void move() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::move(" + x + "," + y + ") " + eval);
		for(int i = 0; i < x.size(); i++){
			if(!mgr.getVarRoutesVR().contains(x.get(i)) && !mgr.getVarRoutesVR().contains(y.get(i))){
				System.out.println(name() + "::move failed"); System.exit(-1);
			}
		}
		mgr.performKPointsMove(x, y);
		if(NE != null) NE.performMove(this);
	}

	
	public LexMultiValues evaluation() {
		// TODO Auto-generated method stub
		return eval;
	}

	
	public INeighborhoodExplorer getNeighborhoodExplorer(){
		return NE;
	}
	
	public ArrayList<Point> getX(){
		return x;
	}
	public ArrayList<Point> getY(){
		return y;
	}
}
