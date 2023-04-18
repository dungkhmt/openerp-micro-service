package localsearch.domainspecific.vehiclerouting.vrp.largeneighborhoodexploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class RandomRouteRemovalsAndGreedyInsertions  implements ILargeNeighborhoodExplorer {

	private VRManager mgr;
	private VarRoutesVR XR;
	private LexMultiFunctions F;
	private int nbIters = 1;
	private HashMap<Point, Point> pickup2delivery;
	private HashMap<Point, Point> delivery2pickup;
	private HashMap<Point, Point> pickup2deliveryOfPeople;
	private HashMap<Point, Point> pickup2deliveryOfParcels;
	private ArrayList<Point> rejectPoints;
	private ArrayList<Point> rejectPickup;
	private ArrayList<Point> rejectDelivery;
	
	public RandomRouteRemovalsAndGreedyInsertions(VarRoutesVR XR, LexMultiFunctions F, int nbIters, 
							HashMap<Point, Point> pickup2delivery, HashMap<Point, Point> delivery2pickup,
							HashMap<Point, Point> pickup2deliveryOfPeople, HashMap<Point, Point> pickup2deliveryOfParcels,
							ArrayList<Point> rejectPoints, ArrayList<Point> rejectPickup, ArrayList<Point> rejectDelivery) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.nbIters = nbIters;
		this.pickup2delivery = pickup2delivery;
		this.delivery2pickup = delivery2pickup;
		this.pickup2deliveryOfPeople = pickup2deliveryOfPeople;
		this.pickup2deliveryOfParcels = pickup2deliveryOfParcels;
		this.rejectPoints = rejectPoints;
		this.rejectPickup = rejectPickup;
		this.rejectDelivery = rejectDelivery;
	}
	
	public void randomRouteRemoval(Neighborhood N){
		int k = 0;
		int n = XR.getNbRoutes();
		Random r = new Random();
		
		while(k < nbIters){
			int idx = r.nextInt(n-1);
			idx++;
			Point v = XR.prev(XR.getTerminatingPointOfRoute(idx));
			while(v != XR.getStartingPointOfRoute(idx)){
				Point pick = v;
				Point delivery = null;
				delivery = pickup2delivery.get(v);
				if(delivery == null){
					pick = delivery2pickup.get(v);
					delivery = v;
				}
				if(XR.checkPerformRemoveTwoPoints(pick, delivery)){
					XR.performRemoveTwoPoints(pick, delivery);
					rejectPoints.add(pick);
					rejectPoints.add(delivery);
					rejectPickup.add(pick);
					rejectDelivery.add(delivery);
					k++;
					System.out.println("RandomRouteRemoval:: remove request: " + k + " in route " + idx);
				}
				if(k >= nbIters)
					break;
				v = XR.prev(XR.getTerminatingPointOfRoute(idx));
			}
		}
	}
	
	public void greedyInsertion(Neighborhood N){
		LexMultiValues bestEval = new LexMultiValues();
		bestEval.fill(F.size(), 0);
		Set<Point> pickPeoplePoints = pickup2deliveryOfPeople.keySet();
		ArrayList<Point> rjP = new ArrayList<Point>(rejectPickup);
		ArrayList<Point> rjD = new ArrayList<Point>(rejectDelivery);
		for(int i = 0; i < rjP.size(); i++){
			Point pickup = rjP.get(i);
			Point delivery = rjD.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				for(Point v = XR.getStartingPointOfRoute(r); v!= XR.getTerminatingPointOfRoute(r); v = XR.next(v)){
					if(pickPeoplePoints.contains(pickup)){
						LexMultiValues cur_value = F.evaluateAddTwoPoints(pickup, v, delivery, v);
						if(cur_value.lt(bestEval)){
							bestEval = cur_value;
							pre_pick = v;
							pre_delivery = v;
						}
					}
					else{
						for(Point u = v; u != XR.getTerminatingPointOfRoute(r); u = XR.next(u)){
							if(pickPeoplePoints.contains(u)){
								continue;
							}
							else{
								LexMultiValues cur_value = F.evaluateAddTwoPoints(pickup, v, delivery, v);
								if(cur_value.lt(bestEval)){
									bestEval = cur_value;
									pre_pick = v;
									pre_delivery = u;
								}
							}
						}
					}	
				}
			}
			if((pre_pick != null && pre_delivery != null)){
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				rejectPoints.remove(pickup);
				rejectPoints.remove(delivery);
				rejectPickup.remove(pickup);
				rejectDelivery.remove(delivery);
				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
			}
			System.out.println("i = " + i);
		}
		System.out.println("reject size = " + rejectPickup.size());
	}
	public void exploreLargeNeighborhood(Neighborhood N) {
		// TODO Auto-generated method stub
		randomRouteRemoval(N);
		greedyInsertion(N);
	}

	public String name(){
		return "RandomRouteRemovalsAndGreedyInsertions";
	}
	public void performMove(IVRMove m) {
		// TODO Auto-generated method stub

	}
}