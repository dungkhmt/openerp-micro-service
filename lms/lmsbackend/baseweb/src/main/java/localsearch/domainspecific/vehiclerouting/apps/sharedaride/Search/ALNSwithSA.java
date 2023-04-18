package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.SearchInput;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.ShareARide;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.SolutionShareARide;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.RelatedPointBuckets;

public class ALNSwithSA {
	
	private VRManager mgr;
	private VarRoutesVR XR;
	private ConstraintSystemVR S;
	private IFunctionVR objective;
	private EarliestArrivalTimeVR eat;
	private ArcWeightsManager awm;
	
	HashMap<Point, Integer> nChosed;
	HashMap<Point, Boolean> removeAllowed;
	
	private int nRemovalOperators = 13;
	private int nInsertionOperators = 14;
	
	//parameters
	private int lower_removal = (int) 1*(ShareARide.nRequest)/100;
	private int upper_removal = (int) 15*(ShareARide.nRequest)/100;
	private int sigma1 = 20;
	private int sigma2 = 10;
	private int sigma3 = -10;
	private double rp = 0.1;
	private int nw = 1;
	private double shaw1st = 0.5;
	private double shaw2nd = 0.2;
	private double shaw3rd = 0.1;
	//private double temperature = 200;
	//private double cooling_rate = 0.9995;
	private int nTabu = 5;
	//private double shaw4th = 0.2;
	private SearchInput search_input;
	public RelatedPointBuckets buckets;
	
	public ALNSwithSA(RelatedPointBuckets buckets, VRManager mgr, IFunctionVR objective, ConstraintSystemVR S, EarliestArrivalTimeVR eat, ArcWeightsManager awm, SearchInput si){
		this.buckets = buckets;
		this.mgr = mgr;
		this.objective = objective;
		this.XR = mgr.getVarRoutesVR();
		this.S = S;
		this.eat = eat;
		this.awm = awm;
		this.search_input = si;
		nChosed = new HashMap<Point, Integer>();
		removeAllowed = new HashMap<Point, Boolean>();
		//ArrayList<Point> clientPoints = XR.getClientPoints();
		for(int i=0; i<search_input.pickupPoints.size(); i++){
			Point pi = search_input.pickupPoints.get(i);
			nChosed.put(pi, 0);
			removeAllowed.put(pi, true);
			
			Point pj = search_input.pickup2Delivery.get(pi);
			nChosed.put(pj, 0);
			removeAllowed.put(pj, true);
		}
	}
	
	public SolutionShareARide search(int maxIter, int timeLimit){
		//insertion operators selection probabilities
		double[] pti = new double[nInsertionOperators];
		//removal operators selection probabilities
		double[] ptd = new double[nRemovalOperators];
		
		//wi - number of times used during last iteration
		int[] wi = new int[nInsertionOperators];
		int[] wd = new int[nRemovalOperators];
		
		//pi_i - score of operator
		int[] si = new int[nInsertionOperators];
		int[] sd = new int[nRemovalOperators];
		
		
		//init probabilites
		for(int i=0; i<nInsertionOperators; i++){
			pti[i] = 1.0/nInsertionOperators;
			wi[i] = 1;
			si[i] = 0;
		}
		for(int i=0; i<nRemovalOperators; i++){
			ptd[i] = 1.0/nRemovalOperators;
			wd[i] = 1;
			sd[i] = 0;
		}
		
		int it = 0;
		
		double best_cost = objective.getValue();
		SolutionShareARide best_solution = new SolutionShareARide(buckets, XR, search_input.rejectPoints, search_input.rejectPickupGoods, search_input.rejectPickupPeoples, best_cost);
		ShareARide.LOGGER.log(Level.INFO, "start search best_solution has cost = "+best_solution.get_cost()+"  number of rejected request of goods = "+best_solution.get_rejectPickupGoods().size()+"  number of rejected request of peoples = "+best_solution.get_rejectPickupPeoples().size());
		
		double start_search_time = System.currentTimeMillis();

		while( (System.currentTimeMillis()-start_search_time) < timeLimit && it++ < maxIter){
			
			double current_cost = objective.getValue();
			SolutionShareARide current_solution = new SolutionShareARide(buckets, XR, search_input.rejectPoints, search_input.rejectPickupGoods, search_input.rejectPickupPeoples, current_cost);
			ShareARide.LOGGER.log(Level.INFO, "Iter "+it+" current_solution has cost = "+current_solution.get_cost()+"  number of rejected request of goods = "+current_solution.get_rejectPickupGoods().size()+"  number of rejected request of peoples = "+current_solution.get_rejectPickupPeoples().size());
			
			int i_selected_removal = get_operator(ptd);
			//int i_selected_removal = 1;
			wd[i_selected_removal]++;
			/*
			 * Select remove operator
			 */
			ShareARide.LOGGER.log(Level.INFO,"selected removal operator = "+i_selected_removal);
			//long timeRemoveStart = System.currentTimeMillis();
			switch(i_selected_removal){
			
				case 0: random_removal(); break;
				case 1: route_removal(); break;
				case 2: late_arrival_removal(); break;
				case 3: shaw_removal(); break;
				case 4: proximity_based_removal(); break;
				case 5: time_based_removal(); break;
				case 6: worst_removal(); break;
				case 7: forbidden_removal(0); break;
				case 8: forbidden_removal(1); break;
				case 9: forbidden_removal(2); break;
				case 10: forbidden_removal(3); break;
				case 11: forbidden_removal(4); break;
				case 12: forbidden_removal(5); break;
			}
			//long timeRemoveEnd = System.currentTimeMillis();
			//long timeRemove = timeRemoveEnd - timeRemoveStart;
			
			int i_selected_insertion = get_operator(pti);
			//int i_selected_insertion = 3;
			wi[i_selected_insertion]++;
			ShareARide.LOGGER.log(Level.INFO,"selected insertion operator = "+i_selected_insertion);
			/*
			 * Select insertion operator
			 */
			//long timeInsertStart = Sy
			switch(i_selected_insertion){
				
				case 0: greedy_insertion(); break;
				case 1: greedy_insertion_noise_function(); break;
				case 2: second_best_insertion(); break;
				case 3: second_best_insertion_noise_function(); break;
				case 4: regret_n_insertion(2); break;
				case 5: regret_n_insertion(3); break;
				case 6: first_possible_insertion();break;
				case 7: sort_before_insertion(0); break;
				case 8: sort_before_insertion(1); break;
				case 9: sort_before_insertion(2); break;
				case 10: sort_before_insertion(3); break;
				case 11: sort_before_insertion(4); break;
				case 12: sort_before_insertion(5); break;
				case 13: sort_before_insertion(6); break;
			}
			double new_cost = objective.getValue();
			ShareARide.LOGGER.log(Level.INFO,"Iter "+it+" new_solution: has cost = "+new_cost+"  number of rejected request of goods = "+search_input.rejectPickupGoods.size()+"  number of rejected request of peoples = "+search_input.rejectPickupPeoples.size());
			
			
			/*
			 * if new solution has cost better than current solution
			 * 		update current solution =  new solution
			 * 		if new solution has best cost
			 * 			update best cost
			 */
			int new_nb_reject_points = search_input.rejectPickupGoods.size()+search_input.rejectPickupPeoples.size();
			int current_nb_reject_points = current_solution.get_rejectPickupGoods().size() + current_solution.get_rejectPickupPeoples().size();
			if( new_nb_reject_points < current_nb_reject_points
					|| (new_nb_reject_points == current_nb_reject_points && new_cost < current_cost)){
				
				int best_nb_reject_points = best_solution.get_rejectPickupGoods().size()+best_solution.get_rejectPickupPeoples().size();
				
				if(new_nb_reject_points < best_nb_reject_points
						|| (new_nb_reject_points == best_nb_reject_points && new_cost < best_cost)){
					
					best_cost = new_cost;
					best_solution = new SolutionShareARide(buckets, XR, search_input.rejectPoints, search_input.rejectPickupGoods, search_input.rejectPickupPeoples, best_cost);
					
					ShareARide.LOGGER.log(Level.INFO,"Iter "+it+" find the best solution with number of rejected of goods  = "+best_solution.get_rejectPickupGoods().size()+" number of rejected of peoples  = "+best_solution.get_rejectPickupPeoples().size()+"  cost = "+best_solution.get_cost());
					if(new_nb_reject_points < best_nb_reject_points) {
						si[i_selected_insertion] += sigma1*(best_nb_reject_points - new_nb_reject_points)/best_nb_reject_points;
						sd[i_selected_removal] += sigma1*(best_nb_reject_points - new_nb_reject_points)/best_nb_reject_points;
					}
					else {
						si[i_selected_insertion] += sigma1*100*(best_cost - new_cost)/best_cost;
						sd[i_selected_removal] += sigma1*100*(best_cost - new_cost)/best_cost;
					}
				}else{
					if(new_nb_reject_points < current_nb_reject_points) {
						si[i_selected_insertion] += sigma2*(current_nb_reject_points - new_nb_reject_points)/current_nb_reject_points;
						sd[i_selected_removal] += sigma2*(current_nb_reject_points - new_nb_reject_points)/current_nb_reject_points;
					}
					else {
						si[i_selected_insertion] += sigma2*100*(current_cost - new_cost)/current_cost;
						sd[i_selected_removal] += sigma2*100*(current_cost - new_cost)/current_cost;
					}
				}
			}
			/*
			 * if new solution has cost worst than current solution
			 * 		because XR is new solution
			 * 			copy current current solution to new solution if don't change solution
			 */
			else{
				if(new_nb_reject_points > current_nb_reject_points) {
					si[i_selected_insertion] += sigma3*(new_nb_reject_points - current_nb_reject_points )/new_nb_reject_points;
					sd[i_selected_removal] += sigma3*(new_nb_reject_points - current_nb_reject_points )/new_nb_reject_points;
				}
				else {
					si[i_selected_insertion] += sigma3*100*(new_cost - current_cost)/new_cost;
					sd[i_selected_removal] += sigma3*100*(new_cost - current_cost)/new_cost;
				}
				//double v = Math.exp(-(new_cost-current_cost)/temperature);
				//double r = Math.random();
				//if(r >= v){
					ShareARide.LOGGER.log(Level.INFO,"The cost did not improve and reverse solution back to current solution");
					current_solution.copy2XR(XR);
					search_input.rejectPoints = current_solution.get_rejectPoints();
					search_input.rejectPickupGoods = current_solution.get_rejectPickupGoods();
					search_input.rejectPickupPeoples = current_solution.get_rejectPickupPeoples();
					buckets.setBuckets(current_solution.get_buckets());
				//}
			}
			
			//temperature = cooling_rate*temperature;
			
			//update probabilities
			if(it % nw == 0){
				for(int i=0; i<nInsertionOperators; i++){
					pti[i] = Math.max(0, pti[i]*(1-rp) + rp*si[i]/wi[i]);
					//wi[i] = 1;
					//si[i] = 0;
				}
				
				for(int i=0; i<nRemovalOperators; i++){
					ptd[i] = Math.max(0, ptd[i]*(1-rp) + rp*sd[i]/wd[i]);
					//wd[i] = 1;
					//sd[i] = 0;
				}
			}
		}
		return best_solution;
	}
	
	private void random_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		ArrayList<Point> clientPoints = XR.getClientPoints();
		Collections.shuffle(clientPoints);
		
		int inRemove = 0 ;
		for(int i=0; i<clientPoints.size(); i++){
			if(inRemove == nRemove)
				break;
			Point pr1 = clientPoints.get(i);
			if(!removeAllowed.get(pr1))
				continue;
			if(search_input.rejectPoints.contains(pr1))
				continue;
			//out.println("pr1 = "+pr1.getID());
			Point pr2 = search_input.pickup2Delivery.get(pr1);
			boolean pr2IsDelivery = true;
			if(pr2 == null){
				//out.println("pr2 null");
				pr2 = search_input.delivery2Pickup.get(pr1);
				pr2IsDelivery = false;
			}
			//System.out.println("pr2 = "+pr2.getID());
			/*if(S.evaluateRemoveTwoPoints(pr1,pr2) != 0){
				System.out.println("iter "+i+"  invalid   "+pr1.getID()+"  "+pr2.getID());
				continue;
			}*/
			//System.out.println("iter "+i+"  Remove "+pr1.getID()+"  "+pr2.getID());
			inRemove++;

			if(pr2IsDelivery){
				mgr.performRemoveTwoPoints(pr1, pr2);
			}else{
				mgr.performRemoveTwoPoints(pr2, pr1);
			}
		
			search_input.rejectPoints.add(pr1);
			search_input.rejectPoints.add(pr2);
			nChosed.put(pr1, nChosed.get(pr1)+1);
			nChosed.put(pr2, nChosed.get(pr2)+1);
			
			if(pr2IsDelivery){
				if(search_input.pickup2DeliveryOfPeople.containsKey(pr1)){
					search_input.rejectPickupPeoples.add(pr1);
				}else if(search_input.pickup2DeliveryOfGood.containsKey(pr1)){
					search_input.rejectPickupGoods.add(pr1);
				}else{
					ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
					System.exit(-1);
				}
			}else{
				if(search_input.pickup2DeliveryOfPeople.containsKey(pr2)){
					search_input.rejectPickupPeoples.add(pr2);
				}else if(search_input.pickup2DeliveryOfGood.containsKey(pr2)){
					search_input.rejectPickupGoods.add(pr2);
				}else{
					ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
					System.exit(-1);
				}
			}
		}
		if(inRemove == 0){
			ShareARide.LOGGER.log(Level.INFO,"Can't remove any client points");
		}
		//System.out.println("random_removal done");
	}
	
	private void route_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		int cnt = 0;
		while(cnt/2 < nRemove){
			int K = XR.getNbRoutes();
			R = new Random();
			int iRouteRemoval = R.nextInt(K)+1;
			ShareARide.LOGGER.log(Level.INFO,"index of route removed = "+iRouteRemoval);
			
			Point x = XR.getStartingPointOfRoute(iRouteRemoval);
			Point next_x = XR.next(x);
			while(next_x != XR.getTerminatingPointOfRoute(iRouteRemoval)){
				x = next_x;
				next_x = XR.next(x);
				if(!removeAllowed.get(x))
					continue;
				search_input.rejectPoints.add(x);
				if(search_input.pickup2Delivery.containsKey(x)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(x)){
						search_input.rejectPickupPeoples.add(x);
					}else if(search_input.pickup2DeliveryOfGood.containsKey(x)){
						search_input.rejectPickupGoods.add(x);
					}else{
						ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
						System.exit(-1);
					}
				}
				mgr.performRemoveOnePoint(x);
				nChosed.put(x, nChosed.get(x)+1);
				cnt++;
			}
		}
	}
	
	private void late_arrival_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		int iRemove = 0;
		while(iRemove++ != nRemove){
			
			double deviationMax = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					if(!removeAllowed.get(x))
						continue;
					Point dX = search_input.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double arrivalTime = eat.getEarliestArrivalTime(XR.prev(x))+ 
							search_input.serviceDuration.get(XR.prev(x))+
							awm.getDistance(XR.prev(x), x);
					
					
					double serviceTime = 1.0*search_input.earliestAllowedArrivalTime.get(x);
					serviceTime = arrivalTime > serviceTime ? arrivalTime : serviceTime;
					
					double depatureTime = serviceTime + search_input.serviceDuration.get(x);
					
					double arrivalTimeD =  eat.getEarliestArrivalTime(XR.prev(dX))+ 
							search_input.serviceDuration.get(XR.prev(dX))+
							awm.getDistance(XR.prev(dX), dX);
					
					double serviceTimeD = 1.0*search_input.earliestAllowedArrivalTime.get(dX);
					serviceTime = arrivalTimeD > serviceTimeD ? arrivalTimeD : serviceTimeD;
					
					double depatureTimeD = serviceTimeD + search_input.serviceDuration.get(dX);
					
					double deviation = depatureTime - arrivalTime + depatureTimeD - arrivalTimeD;
					if(deviation > deviationMax){
						deviationMax = deviation;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			if(removedPickup == null || removedDelivery == null)
				break;
			
			search_input.rejectPoints.add(removedDelivery);
			search_input.rejectPoints.add(removedPickup);
			nChosed.put(removedDelivery, nChosed.get(removedDelivery)+1);
			nChosed.put(removedPickup, nChosed.get(removedPickup)+1);
			
			if(search_input.pickup2DeliveryOfPeople.containsKey(removedPickup)){
				search_input.rejectPickupPeoples.add(removedPickup);
			}else if(search_input.pickup2DeliveryOfGood.containsKey(removedPickup)){
				search_input.rejectPickupGoods.add(removedPickup);
			}else{
				ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
				System.exit(-1);
			}
			mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
	
	/*
	private void worst_distance_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		int iRemove = 0;
		while(iRemove++ != nRemove){
			double distanceMax = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double distance = awm.getDistance(x, XR.prev(x)) + awm.getDistance(x, XR.next(x)) 
							+ awm.getDistance(dX, XR.prev(dX)) + awm.getDistance(dX, XR.next(dX));
					
					if(distance > distanceMax){
						distanceMax = distance;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			ShareARide.rejectPoints.add(removedDelivery);
			ShareARide.rejectPoints.add(removedPickup);
			ShareARide.rejectPickup.add(removedPickup);
			ShareARide.rejectDelivery.add(removedDelivery);
			
			mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
	*/

	private void shaw_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1;
		do{
			ipRemove = R.nextInt(clientPoints.size());
			r1 = clientPoints.get(ipRemove);	
		}while(search_input.rejectPoints.contains(r1) || !removeAllowed.get(r1));
		
		Point dr1;
		boolean isPickup = search_input.pickup2Delivery.containsKey(r1);
		if(isPickup){
			dr1 = search_input.pickup2Delivery.get(r1);
		}else{
			Point tmp = search_input.delivery2Pickup.get(r1);
			dr1 = r1;
			r1 = tmp;
		}
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove && r1 !=null && dr1 != null){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			int routeOfR1 = XR.route(r1);
			/*
			 * Compute arrival time at request r1 and its delivery dr1
			 */
			double arrivalTimeR1 = eat.getEarliestArrivalTime(XR.prev(r1))+
					search_input.serviceDuration.get(XR.prev(r1))+
					awm.getDistance(XR.prev(r1), r1);
			
			double serviceTimeR1 = 1.0*search_input.earliestAllowedArrivalTime.get(r1);
			serviceTimeR1 = arrivalTimeR1 > serviceTimeR1 ? arrivalTimeR1 : serviceTimeR1;
			
			double depatureTimeR1 = serviceTimeR1 + search_input.serviceDuration.get(r1);
			
			double arrivalTimeDR1 = eat.getEarliestArrivalTime(XR.prev(dr1))+
					search_input.serviceDuration.get(XR.prev(dr1))+
					awm.getDistance(XR.prev(dr1), dr1);
			
			double serviceTimeDR1 = 1.0*search_input.earliestAllowedArrivalTime.get(dr1);
			serviceTimeDR1 = arrivalTimeDR1 > serviceTimeDR1 ? arrivalTimeDR1 : serviceTimeDR1;
			
			double depatureTimeDR1 = serviceTimeDR1 + search_input.serviceDuration.get(dr1);
			
			search_input.rejectPoints.add(r1);
			search_input.rejectPoints.add(dr1);
			nChosed.put(r1, nChosed.get(r1)+1);
			nChosed.put(dr1, nChosed.get(dr1)+1);
			
			if(search_input.pickup2DeliveryOfPeople.containsKey(r1)){
				search_input.rejectPickupPeoples.add(r1);
			}else if(search_input.pickup2DeliveryOfGood.containsKey(r1)){
				search_input.rejectPickupGoods.add(r1);
			}else{
				ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
				System.exit(-1);
			}
			
			mgr.performRemoveTwoPoints(r1, dr1);
			
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					if(!removeAllowed.get(x))
						continue;
					
					Point dX = search_input.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute arrival time of x and its delivery dX
					 */
					double arrivalTimeX =  eat.getEarliestArrivalTime(XR.prev(x))+
							search_input.serviceDuration.get(XR.prev(x))+
							awm.getDistance(XR.prev(x), x);
					
					double serviceTimeX = 1.0*search_input.earliestAllowedArrivalTime.get(x);
					serviceTimeX = arrivalTimeX > serviceTimeX ? arrivalTimeX : serviceTimeX;
					
					double depatureTimeX = serviceTimeX + search_input.serviceDuration.get(x);
					
					double arrivalTimeDX =  eat.getEarliestArrivalTime(XR.prev(dX))+
							search_input.serviceDuration.get(XR.prev(dX))+
							awm.getDistance(XR.prev(dX), dX);
					
					double serviceTimeDX = 1.0*search_input.earliestAllowedArrivalTime.get(dX);
					serviceTimeDX = arrivalTimeDX > serviceTimeDX ? arrivalTimeDX : serviceTimeDX;
					
					double depatureTimeDX = serviceTimeDX + search_input.serviceDuration.get(dX);
					
					/*
					 * Compute related between r1 and x
					 */
					int lr1x;
					if(routeOfR1 == k){
						lr1x = 1;
					}else{
						lr1x = -1;
					}
					
					double related = shaw1st*(awm.getDistance(r1, x) + awm.getDistance(dX, dr1))+
							shaw2nd*(Math.abs(depatureTimeR1-depatureTimeX) + Math.abs(depatureTimeDX-depatureTimeDR1))+
							shaw3rd*lr1x;
					if(related < relatedMin){
						relatedMin = related;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			r1 = removedPickup;
			dr1 = removedDelivery;
		}
		
	}
	
	private void proximity_based_removal(){
		
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1;
		do{
			ipRemove = R.nextInt(clientPoints.size());
			r1 = clientPoints.get(ipRemove);	
		}while(search_input.rejectPoints.contains(r1) || !removeAllowed.get(r1));
		
		Point dr1;
		boolean isPickup = search_input.pickup2Delivery.containsKey(r1);
		if(isPickup){
			dr1 = search_input.pickup2Delivery.get(r1);
		}else{
			Point tmp = search_input.delivery2Pickup.get(r1);
			dr1 = r1;
			r1 = tmp;
		}
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove && r1 != null && dr1 != null){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			search_input.rejectPoints.add(r1);
			search_input.rejectPoints.add(dr1);
			nChosed.put(r1, nChosed.get(r1)+1);
			nChosed.put(dr1, nChosed.get(dr1)+1);
			
			if(search_input.pickup2DeliveryOfPeople.containsKey(r1)){
				search_input.rejectPickupPeoples.add(r1);
			}else if(search_input.pickup2DeliveryOfGood.containsKey(r1)){
				search_input.rejectPickupGoods.add(r1);
			}else{
				ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
				System.exit(-1);
			}
			
			mgr.performRemoveTwoPoints(r1, dr1);
			
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					if(!removeAllowed.get(x))
						continue;
					
					Point dX = search_input.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute related between r1 and x
					 */
					
					double related = shaw1st*(awm.getDistance(r1, x) + awm.getDistance(dX, dr1));
					
					if(related < relatedMin){
						relatedMin = related;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			r1 = removedPickup;
			dr1 = removedDelivery;
		}
	}
	
	private void time_based_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1;
		do{
			ipRemove = R.nextInt(clientPoints.size());
			r1 = clientPoints.get(ipRemove);	
		}while(search_input.rejectPoints.contains(r1) || !removeAllowed.get(r1));
		
		Point dr1;
		boolean isPickup = search_input.pickup2Delivery.containsKey(r1);
		if(isPickup){
			dr1 = search_input.pickup2Delivery.get(r1);
		}else{
			Point tmp = search_input.delivery2Pickup.get(r1);
			dr1 = r1;
			r1 = tmp;
		}
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove && r1 != null && dr1 != null){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			/*
			 * Compute arrival time at request r1 and its delivery dr1
			 */
			double arrivalTimeR1 =  eat.getEarliestArrivalTime(XR.prev(r1))+
					search_input.serviceDuration.get(XR.prev(r1))+
					awm.getDistance(XR.prev(r1), r1);
			
			double serviceTimeR1 = 1.0*search_input.earliestAllowedArrivalTime.get(r1);
			serviceTimeR1 = arrivalTimeR1 > serviceTimeR1 ? arrivalTimeR1 : serviceTimeR1;
			
			double depatureTimeR1 = serviceTimeR1 + search_input.serviceDuration.get(r1);
			
			double arrivalTimeDR1 =  eat.getEarliestArrivalTime(XR.prev(dr1))+
					search_input.serviceDuration.get(XR.prev(dr1))+
					awm.getDistance(XR.prev(dr1), dr1);
			
			double serviceTimeDR1 = 1.0*search_input.earliestAllowedArrivalTime.get(dr1);
			serviceTimeDR1 = arrivalTimeDR1 > serviceTimeDR1 ? arrivalTimeDR1 : serviceTimeDR1;
			
			double depatureTimeDR1 = serviceTimeDR1 + search_input.serviceDuration.get(dr1);
			
			search_input.rejectPoints.add(r1);
			search_input.rejectPoints.add(dr1);
			nChosed.put(r1, nChosed.get(r1));
			nChosed.put(dr1, nChosed.get(dr1));
			
			if(search_input.pickup2DeliveryOfPeople.containsKey(r1)){
				search_input.rejectPickupPeoples.add(r1);
			}else if(search_input.pickup2DeliveryOfGood.containsKey(r1)){
				search_input.rejectPickupGoods.add(r1);
			}else{
				ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
				System.exit(-1);
			}
			
			mgr.performRemoveTwoPoints(r1, dr1);
			
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					if(!removeAllowed.get(x))
						continue;
					
					Point dX = search_input.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute arrival time of x and its delivery dX
					 */
					double arrivalTimeX =  eat.getEarliestArrivalTime(XR.prev(x))+
							search_input.serviceDuration.get(XR.prev(x))+
							awm.getDistance(XR.prev(x), x);
					
					double serviceTimeX = 1.0*search_input.earliestAllowedArrivalTime.get(x);
					serviceTimeX = arrivalTimeX > serviceTimeX ? arrivalTimeX : serviceTimeX;
					
					double depatureTimeX = serviceTimeX + search_input.serviceDuration.get(x);
					
					double arrivalTimeDX =  eat.getEarliestArrivalTime(XR.prev(dX))+
							search_input.serviceDuration.get(XR.prev(dX))+
							awm.getDistance(XR.prev(dX), dX);
					
					double serviceTimeDX = 1.0*search_input.earliestAllowedArrivalTime.get(dX);
					serviceTimeDX = arrivalTimeDX > serviceTimeDX ? arrivalTimeDX : serviceTimeDX;
					
					double depatureTimeDX = serviceTimeDX + search_input.serviceDuration.get(dX);
					
					/*
					 * Compute related between r1 and x
					 */
					
					double related = shaw2nd*(Math.abs(depatureTimeR1-depatureTimeX) + Math.abs(depatureTimeDX-depatureTimeDR1));
					
					if(related < relatedMin){
						relatedMin = related;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			r1 = removedPickup;
			dr1 = removedDelivery;
		}
	}
	
	private void worst_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		int inRemove = 0;
		while(inRemove++ != nRemove){
			
			double maxCost = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					if(!removeAllowed.get(x))
						continue;
					
					Point dX = search_input.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double cost = objective.evaluateRemoveTwoPoints(x, dX);
					if(cost > maxCost){
						maxCost = cost;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			if(removedDelivery == null || removedPickup == null)
				break;
			
			search_input.rejectPoints.add(removedDelivery);
			search_input.rejectPoints.add(removedPickup);
			nChosed.put(removedDelivery, nChosed.get(removedDelivery)+1);
			nChosed.put(removedPickup, nChosed.get(removedPickup)+1);
			
			if(search_input.pickup2DeliveryOfPeople.containsKey(removedPickup)){
				search_input.rejectPickupPeoples.add(removedPickup);
			}else if(search_input.pickup2DeliveryOfGood.containsKey(removedPickup)){
				search_input.rejectPickupGoods.add(removedPickup);
			}else{
				ShareARide.LOGGER.log(Level.INFO,"Exception point removed do not in pickup Good and people");
				System.exit(-1);
			}
			
			mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
		
	private void forbidden_removal(int nRemoval){
		
		//ShareARide.LOGGER.log(Level.INFO,nChosed.toString());
		
		for(int i=0; i < search_input.pickupPoints.size(); i++){
			Point pi = search_input.pickupPoints.get(i);
			Point pj = search_input.pickup2Delivery.get(pi);
			
			if(nChosed.get(pi) > nTabu){
				removeAllowed.put(pi, false);
				removeAllowed.put(pj, false);
			}
		}
		
		switch(nRemoval){
			case 0: random_removal(); break;
			case 1: route_removal(); break;
			case 2: late_arrival_removal(); break;
			case 3: shaw_removal(); break;
			case 4: proximity_based_removal(); break;
			case 5: time_based_removal(); break;
			case 6: worst_removal(); break;
		}
		
		for(int i=0; i < search_input.pickupPoints.size(); i++){
			Point pi = search_input.pickupPoints.get(i);
			removeAllowed.put(pi, true);
			Point pj = search_input.pickup2Delivery.get(pi);
			removeAllowed.put(pj, true);
		}
	}
	
 	private void greedy_insertion(){
 		
 		ShareARide.LOGGER.log(Level.INFO,"Inserting peoples to route");		
 		
		for(int i=0; i<search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
						//cost improve
						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
						if( cost < best_objective){
							best_objective = cost;
							best_insertion_pickup = p;
							best_insertion_delivery = p;
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				search_input.rejectPickupPeoples.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
		
		ShareARide.LOGGER.log(Level.INFO,"Inserting goods to route");
		
		for(int i=0; i<search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						if(search_input.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							if(cost < best_objective){
								best_objective = cost;
								best_insertion_pickup = p;
								best_insertion_delivery = q;
							}
						}
					}
				}
				minRelatedBucketId++;
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				search_input.rejectPickupGoods.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
	}
	
 	private void greedy_insertion_noise_function(){
 		
 		ShareARide.LOGGER.log(Level.INFO,"Inserting peoples to route");
 		
 		for(int i=0; i<search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					//check constraint
					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
						//cost improve
						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
						double ran = Math.random()*2-1;
						cost += ShareARide.MAX_DISTANCE*0.1*ran;
						if( cost < best_objective){
							best_objective = cost;
							best_insertion_pickup = p;
							best_insertion_delivery = p;
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				search_input.rejectPickupPeoples.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
 		
 		ShareARide.LOGGER.log(Level.INFO,"Inserting goods to route");
		
 		for(int i=0; i<search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						if(search_input.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						
						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							double ran = Math.random()*2-1;
							cost += ShareARide.MAX_DISTANCE*0.1*ran;
							if(cost < best_objective){
								best_objective = cost;
								best_insertion_pickup = p;
								best_insertion_delivery = q;
							}
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				search_input.rejectPickupGoods.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
 	}
 	
 	private void second_best_insertion(){
 		ShareARide.LOGGER.log(Level.INFO,"Inserting peoples to route");
 		
		for(int i=0; i<search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point second_best_insertion_pickup = null;
			Point second_best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			double second_best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
						//cost improve
						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
						if( cost <= best_objective){
							second_best_objective = best_objective;
							best_objective = cost;
						}else{
							if(cost < second_best_objective){
								second_best_objective = cost;
								second_best_insertion_pickup = p;
								second_best_insertion_delivery = p;
							}
						}
					}
				}
			}
			
			if(second_best_insertion_pickup != null && second_best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, second_best_insertion_pickup, delivery, second_best_insertion_delivery);
				search_input.rejectPickupPeoples.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
		
		ShareARide.LOGGER.log(Level.INFO,"Inserting goods to route");
		
		for(int i=0; i<search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point second_best_insertion_pickup = null;
			Point second_best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			double second_best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						if(search_input.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							if(cost <= best_objective){
								second_best_objective = best_objective;
								best_objective = cost;
							}else{
								if(cost < second_best_objective){
									second_best_objective = cost;
									second_best_insertion_pickup = p;
									second_best_insertion_delivery = q;
								}
							}
						}
					}
				}
			}
			
			if(second_best_insertion_pickup != null && second_best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, second_best_insertion_pickup, delivery, second_best_insertion_delivery);
				search_input.rejectPickupGoods.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
 	}

 	private void second_best_insertion_noise_function(){
 		ShareARide.LOGGER.log(Level.INFO,"Inserting peoples to route");
 		
 		for(int i=0; i<search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point second_best_insertion_pickup = null;
			Point second_best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			double second_best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					
					//check constraint
					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
						//cost improve
						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
						double ran = Math.random()*2-1;
						cost += ShareARide.MAX_DISTANCE*0.1*ran;
						if( cost <= best_objective){
							second_best_objective = best_objective;
							best_objective = cost;
						}else{
							if(cost < second_best_objective){
								second_best_objective = cost;
								second_best_insertion_pickup = p;
								second_best_insertion_delivery = p;
							}
						}
					}
				}
			}
			
			if(second_best_insertion_pickup != null && second_best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, second_best_insertion_pickup, delivery, second_best_insertion_delivery);
				search_input.rejectPickupPeoples.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
 		
 		ShareARide.LOGGER.log(Level.INFO,"Inserting goods to route");
		
 		for(int i=0; i<search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point second_best_insertion_pickup = null;
			Point second_best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			double second_best_objective = Double.MAX_VALUE;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						if(search_input.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						
						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							double ran = Math.random()*2-1;
							cost += ShareARide.MAX_DISTANCE*0.1*ran;
							if(cost <= best_objective){
								second_best_objective = best_objective;
								best_objective = cost;
							}else{
								if(cost < second_best_objective){
									second_best_objective = cost;
									second_best_insertion_pickup = p;
									second_best_insertion_delivery = q;
								}
							}
						}
					}
				}
			}
			
			if(second_best_insertion_pickup != null && second_best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, second_best_insertion_pickup, delivery, second_best_insertion_delivery);
				search_input.rejectPickupGoods.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
 	}

 	private void regret_n_insertion(int n){
 		ShareARide.LOGGER.log(Level.INFO,"n = "+n+" ,Inserting peoples to route");
 		
		for(int i=0; i<search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double n_best_objective[] = new double[n];
			double best_regret_value = Double.MIN_VALUE;
			
			for(int it=0; it<n; it++){
				n_best_objective[it] = Double.MAX_VALUE;
			}
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
						//cost improve
						double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
						for(int it=0; it<n; it++){
							if(n_best_objective[it] > cost){
								for(int it2 = n-1; it2 > it; it2--){
									n_best_objective[it2] = n_best_objective[it2-1];
								}
								n_best_objective[it] = cost;
								break;
							}
						}
						double regret_value = 0;
						for(int it=1; it<n; it++){
							regret_value += Math.abs(n_best_objective[it] - n_best_objective[0]);
						}
						if(regret_value > best_regret_value){
							best_regret_value = regret_value;
							best_insertion_pickup = p;
							best_insertion_delivery = p;
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				search_input.rejectPickupPeoples.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
		
		ShareARide.LOGGER.log(Level.INFO,"Inserting goods to route");
		
		for(int i=0; i<search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double n_best_objective[] = new double[n];
			double best_regret_value = Double.MIN_VALUE;
			
			for(int it=0; it<n; it++){
				n_best_objective[it] = Double.MAX_VALUE;
			}
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						if(search_input.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							for(int it=0; it<n; it++){
								if(n_best_objective[it] > cost){
									for(int it2 = n-1; it2 > it; it2--){
										n_best_objective[it2] = n_best_objective[it2-1];
									}
									n_best_objective[it] = cost;
									break;
								}
							}
							double regret_value = 0;
							for(int it=1; it<n; it++){
								regret_value += Math.abs(n_best_objective[it] - n_best_objective[0]);
							}
							if(regret_value > best_regret_value){
								best_regret_value = regret_value;
								best_insertion_pickup = p;
								best_insertion_delivery = q;
							}
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				search_input.rejectPickupGoods.remove(pickup);
				search_input.rejectPoints.remove(pickup);
				search_input.rejectPoints.remove(delivery);
				i--;
			}
		}
 	}
 	
	private void first_possible_insertion(){
		ShareARide.LOGGER.log(Level.INFO,"Inserting peoples to route");
 		
		for(int i=0; i<search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			boolean finded = false;
			
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				if(finded)
					break;
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
						mgr.performAddTwoPoints(pickup, p, delivery, p);
						search_input.rejectPickupPeoples.remove(pickup);
						search_input.rejectPoints.remove(pickup);
						search_input.rejectPoints.remove(delivery);
						i--;
						finded = true;
						break;
					}
				}
			}
		}
		
		ShareARide.LOGGER.log(Level.INFO,"Inserting goods to route");
		for(int i=0; i<search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			boolean finded = false;
			int minRelatedBucketId = (int)(search_input.earliestAllowedArrivalTime.get(pickup) / buckets.getDelta());
			int maxRelatedBucketId = (int)(search_input.lastestAllowedArrivalTime.get(pickup) / buckets.getDelta()) + 1;
			if(maxRelatedBucketId >= buckets.nbBuckets)
				maxRelatedBucketId = buckets.nbBuckets - 1;;
					
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				if(finded)
					break;
				HashMap<Integer, ArrayList<Point>> routeBK = buckets.getBucketOfRoute(r);
				Point st = routeBK.get(minRelatedBucketId).get(0);
				Point en = routeBK.get(maxRelatedBucketId).get(1);
				for(Point p = st; p!= XR.next(en); p = XR.next(p)){
					if(finded)
						break;
					if(search_input.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != XR.getTerminatingPointOfRoute(r); q = XR.next(q)){
						if(search_input.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							mgr.performAddTwoPoints(pickup, p, delivery, q);
							search_input.rejectPickupGoods.remove(pickup);
							search_input.rejectPoints.remove(pickup);
							search_input.rejectPoints.remove(delivery);
							i--;
							finded = true;
							break;
						}
					}
				}
			}
		}
	}
 	
	private void sort_before_insertion(int iInsertion){
		sort_reject_people();
		sort_reject_good();
		
		switch(iInsertion){
			case 0: greedy_insertion(); break;
			case 1: greedy_insertion_noise_function(); break;
			case 2: second_best_insertion(); break;
			case 3: second_best_insertion_noise_function(); break;
			case 4: regret_n_insertion(2); break;
			case 5: regret_n_insertion(3); break;
			case 6: first_possible_insertion(); break;
		}
		
		Collections.shuffle(search_input.rejectPickupPeoples);
		Collections.shuffle(search_input.rejectPickupGoods);
	}
	
	private void sort_reject_people(){
		HashMap<Point, Integer> time_flexibility_people = new HashMap<Point, Integer>();
		
		for(int i = 0; i < search_input.rejectPickupPeoples.size(); i++){
			Point pickup = search_input.rejectPickupPeoples.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			int lp = search_input.lastestAllowedArrivalTime.get(pickup);
			int ud = search_input.earliestAllowedArrivalTime.get(delivery);
			
			time_flexibility_people.put(pickup, ud-lp);
		}
		
		List<Point> keys_people = new ArrayList<Point>(time_flexibility_people.keySet());
		List<Integer> values_people = new ArrayList<Integer>(time_flexibility_people.values());
		Collections.sort(values_people);
		
		ArrayList<Point> rejectPeopleSorted = new ArrayList<Point>();
		for(int i = 0; i < values_people.size(); i++){
			int v = values_people.get(i);
			for(int j = 0; j < keys_people.size(); j++){
				Point p = keys_people.get(j);
				int vs = time_flexibility_people.get(p);
				if(vs == v){
					rejectPeopleSorted.add(p);
					keys_people.remove(p);
					break;
				}
			}
		}
		search_input.rejectPickupPeoples = rejectPeopleSorted;
	}
	
	private void sort_reject_good(){
		HashMap<Point, Integer> time_flexibility_good = new HashMap<Point, Integer>();
		for(int i = 0; i < search_input.rejectPickupGoods.size(); i++){
			Point pickup = search_input.rejectPickupGoods.get(i);
			Point delivery = search_input.pickup2Delivery.get(pickup);
			
			int lp = search_input.lastestAllowedArrivalTime.get(pickup);
			int ud = search_input.earliestAllowedArrivalTime.get(delivery);
			
			time_flexibility_good.put(pickup, ud-lp);
		}
		
		List<Point> key_good = new ArrayList<Point>(time_flexibility_good.keySet());
		List<Integer> value_good = new ArrayList<Integer>(time_flexibility_good.values());
		Collections.sort(value_good);
		
		ArrayList<Point> rejectGoodSorted = new ArrayList<Point>();
		for(int i = 0; i < value_good.size(); i++){
			int v = value_good.get(i);
			for(int j = 0; j < key_good.size(); j++){
				Point p = key_good.get(j);
				int vs = time_flexibility_good.get(p);
				if(vs == v){
					rejectGoodSorted.add(p);
					key_good.remove(p);
					break;
				}
			}
		}
		search_input.rejectPickupGoods = rejectGoodSorted;
	}
	
	//roulette-wheel mechanism
 	private int get_operator(double[] p){
 		//String message = "probabilities input \n";
 		
 		int n = p.length;
		double[] s = new double[n];
		s[0] = 0+p[0];
		
		//String messagep = ("p = ["+p[0]+", ");
		//String messages = ("s = ["+s[0]+", ");
		
		for(int i=1; i<n; i++){
			//messagep += (p[i]+", ");
			s[i] = s[i-1]+p[i]; 
			//messages += (s[i]+", ");
		}
		//messagep += ("]");
		//messages += ("]");
		
		double r = s[n-1]*Math.random();
		//String messr = ("radom value = " + r);
		
		//message += (messagep +"\n" + messages + "\n" + messr);
		//ShareARide.LOGGER.log(Level.INFO,message);
		
		if(r>=0 && r <= s[0])
			return 0;
		
		for(int i=1; i<n; i++){
			if(r>s[i-1] && r<=s[i])
				return i;
		}
		return -1;
	}
	
}
