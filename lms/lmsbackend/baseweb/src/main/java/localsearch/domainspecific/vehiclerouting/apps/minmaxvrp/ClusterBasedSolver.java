package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class ClusterBasedSolver {
	public MinMaxCVRP vrp;
	public Cluster[] routes;
	public TSPSolver tsp;
	public MultiKnapsackSolver knapsack;
	
	public ClusterBasedSolver(MinMaxCVRP vrp){
		this.vrp = vrp;
		tsp = new TSPSolver(vrp);
		knapsack = new MultiKnapsackSolver(vrp);
	}
	public String name(){
		return "ClusterBasedSolver";
	}
	
	public int selectMaxLengthRoute(){
		int sel_r = -1;
		double maxLength = -1;
		for(int i = 0; i < routes.length; i++){
			if(routes[i].length > maxLength){
				maxLength = routes[i].length;
				sel_r = i;
			}
		}
		return sel_r;
	}
	public void search(int timeLimit){
		double t0 = System.currentTimeMillis();
		
		knapsack.solve(60);
		
		double maxLength = 0;
		double maxDemand = 0;
		routes = new Cluster[vrp.nbVehicles];
		for(int k = 1; k <= vrp.nbVehicles; k++){
			Point s = vrp.startPoints.get(k-1);
			Point t = vrp.endPoints.get(k-1);
			ArrayList<Point> clientPoints = knapsack.getPointsOfRoute(k);
			routes[k-1] = new Cluster(s,t,clientPoints);
			routes[k-1].demand = 0;
			for(Point p: clientPoints){
				routes[k-1].demand += vrp.nwm.getWeight(p);
			}
			tsp.optimize(routes[k-1], 10000, 1);
			
			if(maxLength < routes[k-1].length)
				maxLength = routes[k-1].length;
			
			if(maxDemand < routes[k-1].demand)
				maxDemand = routes[k-1].demand;
		}
		for(int k = 1; k <= vrp.nbVehicles; k++){
			System.out.println(name() + "::search, INIT route[" + k + "] = " + routes[k-1].toString());
		}
		System.out.println(name() + "::search INIT, MaxLength = " + maxLength + ", maxDemand = " + maxDemand + ", capacity = " + vrp.capacity);
		
		while(true){
			double t = System.currentTimeMillis() - t0;
			t = t * 0.001;
			if(t > timeLimit) break;
			
			double maxL = -1;
			int sel_r = selectMaxLengthRoute();
			Cluster newR = null;
			int sel_i = -1;
			Cluster newCi = null;
			boolean hasMove = false;
			for(Point p: routes[sel_r].clientPoints){
				for(int i = 0; i < vrp.nbVehicles; i++)if(i != sel_r){
					for(Point pi: routes[i].clientPoints){
					if(routes[i].demand + vrp.nwm.getWeight(p) - vrp.nwm.getWeight(pi) <= vrp.capacity &&
							routes[sel_r].demand + vrp.nwm.getWeight(pi) - vrp.nwm.getWeight(p) <= vrp.capacity){
						Cluster ci = tsp.evaluateAddRemove(routes[i], p, pi, 100, 1);
						Cluster cr = tsp.evaluateAddRemove(routes[sel_r], pi, p, 100, 1);
						maxL = ci.length;
						if(maxL < cr.length) maxL = cr.length;
						for(int j = 0; j < vrp.nbVehicles; j++) if(j != sel_r && j != i){
							if(maxL < routes[j].length) maxL = routes[j].length;
						}
						System.out.println(name() + "::search, longest route = " + routes[sel_r].length + ", maxL = " + maxL + ", maxLength = " + maxLength);
						if(maxL < maxLength){
							maxL = maxLength;
							newR = cr;
							newCi = ci;
							sel_i = i;
							hasMove = true;
						}
					}
					}
				}
			}
			
			if(hasMove){
				routes[sel_r] = newR;
				routes[sel_i] = newCi;
				maxLength = maxL;
				System.out.println(name() + "::search, IMPROVE MaxL = " + maxL);
			}else{
				break;
			}
		}
		
		
		for(int k = 1; k <= vrp.nbVehicles; k++){
			System.out.println(name() + "::search, route[" + k + "] = " + routes[k-1].toString());
		}
		System.out.println("MaxLength = " + maxLength + ", maxDemand = " + maxDemand + ", capacity = " + vrp.capacity);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinMaxCVRP vrp = new MinMaxCVRP();
		vrp.readData("data/MinMaxVRP/Christophides/std-all/E-n101-k14.vrp");
		vrp.mapping();
		ClusterBasedSolver solver = new ClusterBasedSolver(vrp);
		solver.search(120);
			
	}

}
