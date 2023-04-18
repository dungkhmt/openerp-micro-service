package localsearch.domainspecific.vehiclerouting.apps.vinamilk.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.TruckContainerSolver;
import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.Utils;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class AdaptRoutes {
	MDMTPSolver solver;

	public AdaptRoutes(MDMTPSolver solver) {
		super();
		this.solver = solver;
	}

	public ArrayList<Point> addOnePoint(Point x, Point y){
		if(x == null
			|| y == null) {
			System.out.println("Null point is added!");
			System.exit(1);
		}
		ArrayList<Point> newRoute = new ArrayList<Point>();
		int r = solver.XR.route(y);
		for(Point p = solver.XR.getStartingPointOfRoute(r); 
				p != null; p = solver.XR.next(p)) {
			if(p.getTypeOfPoint().equals(Utils.DEPOT))
				continue;
			newRoute.add(p);
			if(p == y)
				newRoute.add(x);
		}

		return newRoute;
	}
	
	public ArrayList<Point> removeOnePoint(Point x){
		if(x == null) {
			System.out.println("Null point is removed!");
			System.exit(1);
		}
		ArrayList<Point> newRoute = new ArrayList<Point>();
		int r = solver.XR.route(x);
		for(Point p = solver.XR.getStartingPointOfRoute(r); 
				p != null; p = solver.XR.next(p)) {
			if(p != x)
				newRoute.add(p);
		}

		return newRoute;
	}
	
	//z->y->x
	public ArrayList<Point> addTwoPoint(Point x, Point y, Point z){
		if(x == null
			|| y == null
			|| z == null) {
			System.out.println("Null point is added!");
			System.exit(1);
		}
		ArrayList<Point> newRoute = new ArrayList<Point>();
		int r = solver.XR.route(z);
		for(Point p = solver.XR.getStartingPointOfRoute(r); 
				p != null; p = solver.XR.next(p)) {
			newRoute.add(p);
			if(p == z) {
				newRoute.add(y);
				newRoute.add(x);
			}
		}

		return newRoute;
	}
	
	public void allRemoval(){
		System.out.println("all removal");
		solver.mgr.performRemoveAllClientPoints();
		for(int i = 0; i < solver.customerPoints.size(); i++){
			Point c = solver.customerPoints.get(i);
			solver.rejectedPoints.add(c);
			solver.point2mark.put(c, 0);
		}
	}
	
	public void routeRemovalOperator() {
		Random r = new Random();
		ArrayList<Integer> usedVehicles = getUsedVehicles();
		if(usedVehicles.size() == 0)
			return;
		int id = r.nextInt(usedVehicles.size());
		int k = usedVehicles.get(id);
		System.out.println("routeRemoval: index of removed route = " + k);
		Point x = solver.XR.getStartingPointOfRoute(k);
		Point next_x = solver.XR.next(x);
		while(next_x != solver.XR.getTerminatingPointOfRoute(k)){
			x = next_x;
			next_x = solver.XR.next(x);
			solver.mgr.performRemoveOnePoint(x);
			solver.point2mark.put(x, 0);
			if(x.getTypeOfPoint().equals(Utils.CUSTOMER))
				solver.rejectedPoints.add(x);

			solver.nChosed.put(x, solver.nChosed.get(x)+1);
		}
	}
	
	public void subRouteRemovalOperator() {
		Random r = new Random();
		ArrayList<Integer> usedVehicles = getUsedVehicles();
		if(usedVehicles.size() == 0)
			return;
		int id = r.nextInt(usedVehicles.size());
		int k = usedVehicles.get(id);
		boolean c = r.nextBoolean();
		if(c) {
			System.out.println("subRouteRemoval prefix: index of removed route = " + k);
			Point en = solver.XR.getTerminatingPointOfRoute(k);
			int l = solver.XR.index(en);
			int nbRm = 0;
			Point x = solver.XR.getStartingPointOfRoute(k);
			Point next_x = solver.XR.next(x);
			while(nbRm < l/2){
				x = next_x;
				next_x = solver.XR.next(x);
				solver.mgr.performRemoveOnePoint(x);
				solver.point2mark.put(x, 0);
				if(x.getTypeOfPoint().equals(Utils.CUSTOMER))
					solver.rejectedPoints.add(x);
				nbRm++;
				solver.nChosed.put(x, solver.nChosed.get(x)+1);
			}
		}
		else {
			System.out.println("subRouteRemoval postfix: index of removed route = " + k);
			Point st = solver.XR.getStartingPointOfRoute(k);
			Point en = solver.XR.getTerminatingPointOfRoute(k);
			int nbRm = 0;
			int l = solver.XR.index(en);
			Point x = en;
			Point prev_x = solver.XR.prev(x);
			while(nbRm < l/2){
				x = prev_x;
				prev_x = solver.XR.prev(x);
				solver.mgr.performRemoveOnePoint(x);
				solver.point2mark.put(x, 0);
				if(x.getTypeOfPoint().equals(Utils.CUSTOMER))
					solver.rejectedPoints.add(x);
				nbRm++;
				solver.nChosed.put(x, solver.nChosed.get(x)+1);
			}
		}
	}
	
	public void randomRequestRemovalOperator(){
		Random R = new Random();
		int n = R.nextInt(solver.upper_removal-solver.lower_removal+1) + solver.lower_removal;
		System.out.println("randomReqRemoval:number of removed request = " + n);
		if(n >= solver.customerPoints.size())
			allRemoval();
		else{
			int i = 0;
			ArrayList<Point> usedPoints = getAssignedPoints();
			if(n >= usedPoints.size())
				allRemoval();
			else{
				while(i < n){
					if(solver.rejectedPoints.size() == solver.customerPoints.size())
						break;
					
					int rand = R.nextInt(usedPoints.size());
					Point pickup = usedPoints.get(rand);
					if(!solver.removeAllowed.get(pickup))
						continue;
					
					solver.mgr.performRemoveOnePoint(pickup);
					solver.rejectedPoints.add(pickup);
					usedPoints.remove(pickup);
					solver.point2mark.put(pickup, 0);
					i++;
					solver.nChosed.put(pickup, solver.nChosed.get(pickup)+1);
				}
			}
		}
	}
	
	public void shawRemovalOperator(){
		Random R = new Random();
		int nRemove = R.nextInt(solver.upper_removal-solver.lower_removal+1) + solver.lower_removal;
		
		System.out.println("Shaw removal : number of request removed = " + nRemove);
		
		int ipRemove;
		ArrayList<Point> assignedPoints = getAssignedPoints();
		if(assignedPoints.size() <= 0 || nRemove >= assignedPoints.size())
			return;
		/*
		 * select randomly request r1
		 */
		Point r1 = null;
		int c = 0;
		do{
			ipRemove = R.nextInt(assignedPoints.size());
			r1 = assignedPoints.get(ipRemove);	
		}while(nRemove > 0 && !solver.removeAllowed.get(r1));
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove && r1 != null){
			
			Point removedPickup = null;
			double relatedMin =  Double.MAX_VALUE;
			int routeOfR1 = solver.XR.route(r1);
			/*
			 * Compute arrival time at request r1 and its delivery dr1
			 */
			
			double arrivalTimeR1 = solver.point2arrivalTime.get(solver.XR.prev(r1))+
					solver.serviceDuration.get(solver.XR.prev(r1))+
					solver.awm.getDistance(solver.XR.prev(r1), r1);

			double serviceTimeR1 = 1.0*solver.earliestAllowedArrivalTime.get(r1);
			serviceTimeR1 = arrivalTimeR1 > serviceTimeR1 ? arrivalTimeR1 : serviceTimeR1;
			
			double depatureTimeR1 = serviceTimeR1 + solver.serviceDuration.get(r1);
			
			solver.nChosed.put(r1, solver.nChosed.get(r1)+1);

			solver.mgr.performRemoveOnePoint(r1);
			solver.point2mark.put(r1, 0);
			solver.rejectedPoints.add(r1);

			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=solver.XR.getNbRoutes(); k++){
				Point x = solver.XR.getStartingPointOfRoute(k);
				for(x = solver.XR.next(x); x != solver.XR.getTerminatingPointOfRoute(k); x = solver.XR.next(x)){
					if(!solver.removeAllowed.get(x))
						continue;
					/*
					 * Compute arrival time of x and
					 */
					double arrivalTimeX = solver.point2arrivalTime.get(solver.XR.prev(x))+
							solver.serviceDuration.get(solver.XR.prev(x))+
							solver.awm.getDistance(solver.XR.prev(x), x);
					double serviceTimeX = 1.0*solver.earliestAllowedArrivalTime.get(x);
					serviceTimeX = arrivalTimeX > serviceTimeX ? arrivalTimeX : serviceTimeX;
					
					double depatureTimeX = serviceTimeX + solver.serviceDuration.get(x);
					/*
					 * Compute related between r1 and x
					 */
					int lr1x;
					if(routeOfR1 == k){
						lr1x = 1;
					}else{
						lr1x = -1;
					}
					
					double related = solver.shaw1st*(solver.awm.getDistance(r1, x))+
							solver.shaw2nd*(Math.abs(depatureTimeR1-depatureTimeX))+
							solver.shaw3rd*lr1x;
					if(related < relatedMin){
						relatedMin = related;
						removedPickup = x;
					}
				}
			}
			
			r1 = removedPickup;
			if(r1 != null)
				solver.nChosed.put(r1, solver.nChosed.get(r1)+1);
		}
		
	}
	
	public void worstRemovalOperator(){
		Random R = new Random();
		int nRemove = R.nextInt(solver.upper_removal-solver.lower_removal+1) + solver.lower_removal;
		System.out.println("worstRemoval: nRemove = " + nRemove);
		
		int inRemove = 0;
		int c = 0;
		while(inRemove++ != nRemove && c++ < solver.customerPoints.size()){
			if(solver.rejectedPoints.size() == solver.customerPoints.size())
				break;
			double maxCost = Double.MIN_VALUE;
			Point removedPickup = null;
			
			for(int k=1; k<=solver.XR.getNbRoutes(); k++){
				Point x = solver.XR.getStartingPointOfRoute(k);
				for(x = solver.XR.next(x); x != solver.XR.getTerminatingPointOfRoute(k); x = solver.XR.next(x)){
					if(!solver.removeAllowed.get(x))
						continue;
					
					double cost = solver.objective.evaluateRemoveOnePoint(x);
					if(cost > maxCost){
						maxCost = cost;
						removedPickup = x;
					}
				}
			}
			
			if(removedPickup == null)
				break;
			solver.mgr.performRemoveOnePoint(removedPickup);
			solver.rejectedPoints.add(removedPickup);
			solver.nChosed.put(removedPickup, solver.nChosed.get(removedPickup)+1);	
			solver.point2mark.put(removedPickup, 0);
		}
	}
	
	public void forbidden_removal(int nRemoval){
		
		System.out.println("forbidden_removal");
		
		for(int i=0; i < solver.customerPoints.size(); i++){
			Point pi = solver.customerPoints.get(i);
			
			if(solver.nChosed.get(pi) > solver.nTabu){
				solver.removeAllowed.put(pi, true);
			}
		}
		
		switch(nRemoval){
			case 0: routeRemovalOperator(); break;
			case 1: randomRequestRemovalOperator(); break;
			case 2: shawRemovalOperator(); break;
			case 3: worstRemovalOperator(); break;
		}
		
		for(int i=0; i < solver.customerPoints.size(); i++){
			Point pi = solver.customerPoints.get(i);
			solver.removeAllowed.put(pi, true);
		}
	}
	
	public void greedyInsertion(){
		System.out.println("greedyInsertion");
		int c = 0;
		for(int i = 0; i < solver.rejectedPoints.size(); i++){
			Point pickup = solver.rejectedPoints.get(i);
			if(solver.XR.route(pickup) != Constants.NULL_POINT)
				continue;
			//add the request to route
			Point pre_pick = null;
			double best_objective = Double.MAX_VALUE;
			for(int r = 1; r <= solver.XR.getNbRoutes(); r++){
				Point st = solver.XR.getStartingPointOfRoute(r);
				String vhCode = solver.startPoint2vhCode.get(st);
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, pickup.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, pickup.getTypeOfProduct()) == false)
					continue;
				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)){
					for(Point q = p; q != solver.XR.getTerminatingPointOfRoute(r); q = solver.XR.next(q)){
						solver.mgr.performAddOnePoint(pickup, p);
						solver.addDepotToOneRoute(r);
						if(solver.ctrs.timeWindowConstraint(r)
								&& solver.ctrs.upperCapacityConstraints(r)){
							double cost = solver.objective.getValue();
							if( cost < best_objective){
								best_objective = cost;
								pre_pick = p;
							}
						}
						solver.mgr.performRemoveOnePoint(pickup);
						solver.removeDepotPointFromRoute(r);
					}
				}
			}
			if(pre_pick != null){
				solver.mgr.performAddOnePoint(pickup, pre_pick);
				solver.rejectedPoints.remove(pickup);
				solver.point2mark.put(pickup, 1);
				i--;
			}
		}
	}
	
	public void greedyInitSolutionProposed() {
		System.out.println("greedyInitSolutionProposed");
		
		ArrayList<Integer> marks = new ArrayList<Integer>();
		for(int i = 0; i < solver.sortedRoutes.size(); i++)
			marks.add(0);
		int idx = 0;
		int r = -1;
		boolean exceedCap = false;
		while(true) {
			if(exceedCap == false) {
				idx = solver.getUnmarkedVehicle(marks);
				if(idx < 0)
					break;
				r = solver.sortedRoutes.get(idx);
				marks.set(idx, 1);
			}
			exceedCap = false;
			
			Point st = solver.XR.getStartingPointOfRoute(r);
			String vhCode = solver.startPoint2vhCode.get(st);
			for(int j = 0; j < solver.rejectedPoints.size(); j++) {
				Point c = solver.rejectedPoints.get(j);
				if(solver.XR.route(c) != Constants.NULL_POINT) {
					solver.rejectedPoints.remove(c);
					j--;
					continue;
				}
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, c.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, c.getTypeOfProduct()) == false)
					continue;
				
				double bestObj = Utils.INF;
				Point bestP = null;
				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)) {
					solver.mgr.performAddOnePoint(c, p);
					solver.addDepotToOneRoute(r);
					if(solver.ctrs.timeWindowConstraint(r) == false) {
						solver.mgr.performRemoveOnePoint(c);
						solver.removeDepotPointFromRoute(r);
						continue;
					}
					if(solver.ctrs.upperCapacityConstraints(r) == false) {
						exceedCap = true;
						solver.mgr.performRemoveOnePoint(c);
						solver.removeDepotPointFromRoute(r);
						break;
					}

					if(solver.objective.getValue() < bestObj) {
						bestP = p;
						bestObj = solver.objective.getValue();
					}
					solver.mgr.performRemoveOnePoint(c);
					solver.removeDepotPointFromRoute(r);
				}
				if(bestP != null) {
					solver.mgr.performAddOnePoint(c, bestP);
					solver.point2mark.put(c, 1);
					solver.rejectedPoints.remove(c);
					j--;
				}
				if(exceedCap == true) {
					int newIdx = solver.getBestVehicleForExchange(idx, solver.sortedRoutes, marks);
					if(newIdx <= idx) {
						exceedCap = false;
						continue;
					}
					else {
						solver.moveToBetterVehicle(r, solver.sortedRoutes.get(newIdx));
						marks.set(idx, 0);
						marks.set(newIdx, 1);
						r = solver.sortedRoutes.get(newIdx);
						idx = newIdx;
						break;
					}
				}
			}
		}
	}
	
	public void greedyInitSolutionProposedWithNoise() {
		System.out.println("greedyInitSolutionProposedWithNoise");
		ArrayList<Integer> sortedRoutes = solver.sortVehiclesByLowerLoadRate();
		
		ArrayList<Integer> marks = new ArrayList<Integer>();
		for(int i = 0; i < sortedRoutes.size(); i++)
			marks.add(0);
		int idx = 0;
		int r = -1;
		boolean exceedCap = false;
		while(true) {
			if(exceedCap == false) {
				idx = solver.getUnmarkedVehicle(marks);
				if(idx < 0)
					break;
				r = sortedRoutes.get(idx);
				marks.set(idx, 1);
			}
			exceedCap = false;
			
			Point st = solver.XR.getStartingPointOfRoute(r);
			String vhCode = solver.startPoint2vhCode.get(st);
			for(int j = 0; j < solver.rejectedPoints.size(); j++) {
				Point c = solver.rejectedPoints.get(j);
				if(solver.XR.route(c) != Constants.NULL_POINT) {
					solver.rejectedPoints.remove(c);
					j--;
					continue;
				}
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, c.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, c.getTypeOfProduct()) == false)
					continue;
				
				double bestObj = Utils.INF;
				Point bestP = null;
				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)) {
					solver.mgr.performAddOnePoint(c, p);
					solver.addDepotToOneRoute(r);
					if(solver.ctrs.timeWindowConstraint(r) == false) {
						solver.mgr.performRemoveOnePoint(c);
						solver.removeDepotPointFromRoute(r);
						continue;
					}
					if(solver.ctrs.upperCapacityConstraints(r) == false) {
						exceedCap = true;
						solver.mgr.performRemoveOnePoint(c);
						solver.removeDepotPointFromRoute(r);
						break;
					}

					double ran = Math.random()*2-1;
					double cost = solver.objective.getValue();
					cost += solver.MAX_TRAVELTIME*0.1*ran;
					if( cost < bestObj){
						bestP = p;
						bestObj = solver.objective.getValue();
					}
					
					solver.mgr.performRemoveOnePoint(c);
					solver.removeDepotPointFromRoute(r);
				}
				if(bestP != null) {
					solver.mgr.performAddOnePoint(c, bestP);
					solver.point2mark.put(c, 1);
					solver.rejectedPoints.remove(c);
					j--;
				}
				if(exceedCap == true) {
					int newIdx = solver.getBestVehicleForExchange(idx, sortedRoutes, marks);
					if(newIdx <= idx) {
						exceedCap = false;
						continue;
					}
					else {
						solver.moveToBetterVehicle(r, sortedRoutes.get(newIdx));
						marks.set(idx, 0);
						marks.set(newIdx, 1);
						r = sortedRoutes.get(newIdx);
						idx = newIdx;
						break;
					}
				}
			}
		}
	}
	
	public void greedyInsertionWithNoise(){
		System.out.println("greedyInsertionWithNoise");
		
		for(int i = 0; i < solver.rejectedPoints.size(); i++){
			Point pickup = solver.rejectedPoints.get(i);
			if(solver.XR.route(pickup) != Constants.NULL_POINT) {
				solver.rejectedPoints.remove(pickup);
				i--;
				continue;
			}
			//add the request to route
			Point pre_pick = null;
			double best_objective = Double.MAX_VALUE;
			for(int r = 1; r <= solver.XR.getNbRoutes(); r++){
				Point st = solver.XR.getStartingPointOfRoute(r);
				String vhCode = solver.startPoint2vhCode.get(st);
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, pickup.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, pickup.getTypeOfProduct()) == false)
					continue;

				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)){
					solver.mgr.performAddOnePoint(pickup, p);
					solver.addDepotToOneRoute(r);
					
					if(solver.ctrs.timeWindowConstraint(r)
						&& solver.ctrs.upperCapacityConstraints(r)){
						double cost = solver.objective.getValue();
						double ran = Math.random()*2-1;
						cost += TruckContainerSolver.MAX_TRAVELTIME*0.1*ran;
						if( cost < best_objective){
							best_objective = cost;
							pre_pick = p;
						}
					}
					solver.mgr.performRemoveOnePoint(pickup);
					solver.removeDepotPointFromRoute(r);
				}
			}
			if(pre_pick != null){
				solver.mgr.performAddOnePoint(pickup, pre_pick);
				solver.rejectedPoints.remove(pickup);
				solver.point2mark.put(pickup, 1);
				i--;
			}
		}
	}
	
	public void bestRateCapacityInsertion() {
		System.out.println("bestRateCapacityInsertion");
		
		ArrayList<Integer> usedVehicles = getUsedVehicles();
		
		for(int i = 0; i < solver.rejectedPoints.size(); i++) {
			double bestRate = Double.MAX_VALUE;
			Point bestP = null;
			double bestObj = Double.MAX_VALUE;
			
			Point c = solver.rejectedPoints.get(i);
			if(solver.XR.route(c) != Constants.NULL_POINT) {
				solver.rejectedPoints.remove(c);
				i--;
				continue;
			}
			for(int j = 0; j < usedVehicles.size(); j++) {
				int r = usedVehicles.get(j);
				Point st = solver.XR.getStartingPointOfRoute(r);
				String vhCode = solver.startPoint2vhCode.get(st);
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, c.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, c.getTypeOfProduct()) == false)
					continue;
				
				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)) {
					solver.mgr.performAddOnePoint(c, p);
					solver.addDepotToOneRoute(r);
					if(solver.ctrs.timeWindowConstraint(r) == false
						|| solver.ctrs.upperCapacityConstraints(r) == false) {
						solver.mgr.performRemoveOnePoint(c);
						solver.removeDepotPointFromRoute(r);
						continue;
					}
					double vio = solver.ctrs.lowerCapacityConstraints(r);
					if(vio == 0) {
						if(solver.objective.getValue() < bestObj) {
							bestP = p;
							bestObj = solver.objective.getValue();
						}
					}
					else {
						if(vio < bestRate) {
							bestRate = vio;
							bestP = p;
						}
					}
					solver.mgr.performRemoveOnePoint(c);
					solver.removeDepotPointFromRoute(r);
				}
			}
			if(bestP != null) {
				solver.mgr.performAddOnePoint(c, bestP);
				solver.point2mark.put(c, 1);
				solver.rejectedPoints.remove(c);
				i--;
			}
		}
	}
	
	public void regret_n_insertion(int n){
		System.out.println("regret insertion n = " + n);
		
		for(int i = 0; i < solver.rejectedPoints.size(); i++){
			Point pickup = solver.rejectedPoints.get(i);

			if(solver.XR.route(pickup) != Constants.NULL_POINT)
				continue;

			//add the request to route
			Point pre_pick = null;
			double n_best_objective[] = new double[n];
			double best_regret_value = Double.MIN_VALUE;
			
			for(int it=0; it<n; it++){
				n_best_objective[it] = Double.MAX_VALUE;
			}

			for(int r = 1; r <= solver.XR.getNbRoutes(); r++){
				Point st = solver.XR.getStartingPointOfRoute(r);
				String vhCode = solver.startPoint2vhCode.get(st);
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, pickup.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, pickup.getTypeOfProduct()) == false)
					continue;
				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)){
					solver.mgr.performAddOnePoint(pickup, p);
					solver.addDepotToOneRoute(r);
					if(solver.ctrs.timeWindowConstraint(r)
							&& solver.ctrs.upperCapacityConstraints(r)){
						double cost = solver.objective.getValue();
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
							pre_pick = p;
						}
					}
					solver.mgr.performRemoveOnePoint(pickup);
					solver.removeDepotPointFromRoute(r);
				}
			}
			if(pre_pick != null){
				solver.mgr.performAddOnePoint(pickup, pre_pick);
				solver.rejectedPoints.remove(pickup);
				solver.point2mark.put(pickup, 1);
				i--;
			}
		}
	}
	
	public void first_possible_insertion(){
		System.out.println("first_possible_insertion");
		
		for(int i = 0; i < solver.rejectedPoints.size(); i++){
			Point pickup = solver.rejectedPoints.get(i);
			
			if(solver.XR.route(pickup) != Constants.NULL_POINT) {
				solver.rejectedPoints.remove(pickup);
				i--;
				continue;
			}
			//add the request to route
			double best_objective = Double.MAX_VALUE;
			boolean finded = false;
			for(int r = 1; r <= solver.XR.getNbRoutes(); r++){
				if(finded)
					break;
				Point st = solver.XR.getStartingPointOfRoute(r);
				String vhCode = solver.startPoint2vhCode.get(st);
				if(solver.ctrs.customerCanVisitedByVehicle(vhCode, pickup.getLocationId()) == false
					|| solver.ctrs.typeOfProductsCanCarriedByVehicle(vhCode, pickup.getTypeOfProduct()) == false)
					continue;
				
				for(Point p = st; p != solver.XR.getTerminatingPointOfRoute(r); p = solver.XR.next(p)){
					if(finded)
						break;
					solver.mgr.performAddOnePoint(pickup, p);
					solver.addDepotToOneRoute(r);
					if(solver.ctrs.timeWindowConstraint(r)
							&& solver.ctrs.upperCapacityConstraints(r)){
						double cost = solver.objective.getValue();
						if( cost < best_objective){
							solver.removeDepotPointFromRoute(r);
							solver.rejectedPoints.remove(pickup);
							solver.point2mark.put(pickup, 1);
							finded = true;
							i--;
							break;
						}
					}
					solver.mgr.performRemoveOnePoint(pickup);
					solver.removeDepotPointFromRoute(r);
				}
			}
		}
	}
	
	public void sort_before_insertion(int iInsertion){
		System.out.println("sort_before_insertion");
		
		sort_reject_pickup();
		
		switch(iInsertion){
			case 0: greedyInitSolutionProposed(); break;
			case 1: greedyInitSolutionProposedWithNoise(); break;
			case 2: bestRateCapacityInsertion(); break;
			case 3: first_possible_insertion(); break;
		}
		
		Collections.shuffle(solver.rejectedPoints);
	}
	
	private void sort_reject_pickup(){
		HashMap<Point, Double> time_flexibility = new HashMap<Point, Double>();
		
		for(int i = 0; i < solver.rejectedPoints.size(); i++){
			Point pickup = solver.rejectedPoints.get(i);
			
			Point dp = solver.getNearestPointToParking(pickup, solver.depotPoints);
			//int ud = solver.earliestAllowedArrivalTime.get(pickup);
			
			time_flexibility.put(pickup, solver.matrixT[dp.getID()][pickup.getID()]);
		}
		
		List<Point> keys = new ArrayList<Point>(time_flexibility.keySet());
		List<Double> values = new ArrayList<Double>(time_flexibility.values());
		Collections.sort(values);
		
		ArrayList<Point> rejectPointSorted = new ArrayList<Point>();
		for(int i = 0; i < values.size(); i++){
			double v = values.get(i);
			for(int j = 0; j < keys.size(); j++){
				Point p = keys.get(j);
				double vs = time_flexibility.get(p);
				if(vs == v){
					rejectPointSorted.add(p);
					keys.remove(p);
					break;
				}
			}
		}
		solver.rejectedPoints = rejectPointSorted;
	}
	
	private ArrayList<Integer> getUsedVehicles(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int r = 1; r <= solver.XR.getNbRoutes(); r++)
			if(solver.XR.next(solver.XR.getStartingPointOfRoute(r)) != solver.XR.getTerminatingPointOfRoute(r))
				result.add(r);
		return result;
	}
	
	private ArrayList<Point> getAssignedPoints(){
		ArrayList<Point> result = new ArrayList<Point>();
		for(Point p : solver.customerPoints)
			if(solver.XR.route(p) != Constants.NULL_POINT)
				result.add(p);
		return result;
	}
}
