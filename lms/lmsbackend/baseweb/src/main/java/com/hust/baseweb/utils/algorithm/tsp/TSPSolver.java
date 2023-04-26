package com.hust.baseweb.utils.algorithm.tsp;

//import com.google.ortools.linearsolver.MPConstraint;
//import com.google.ortools.linearsolver.MPObjective;
//import com.google.ortools.linearsolver.MPSolver;
//import com.google.ortools.linearsolver.MPVariable;

public class TSPSolver {
	/*
	static {
	    System.loadLibrary("jniortools");
	  }

	
	int N = 5;
	double[][] c = {
			{0,4,1,5,7},
			{5,0,9,8,3},
			{1,2,0,5,2},
			{9,1,3,0,4},
			{1,2,3,7,0}
	};
	double inf = java.lang.Double.POSITIVE_INFINITY;
	
	MPSolver solver;
	MPVariable[][] X;
	
	// generate subset of {0,1,...,N-1)
	class SubSetGenerator{
		int N;
		int[] X;// represents binary sequence
		public SubSetGenerator(int N){
			this.N = N;
		}
		
		public HashSet<Integer> first(){
			X = new int[N];
			for(int i = 0; i < N; i++) X[i] = 0;
			HashSet<Integer> S = new HashSet<Integer>();
			for(int i = 0; i < N; i++) if(X[i] == 1) S.add(i);
			return S;
		}
		public HashSet<Integer> next(){
			int j = N-1;
			while(j >= 0 && X[j] == 1){
				X[j] = 0; j--;
			}
			if(j >= 0){
				X[j] = 1;
				HashSet<Integer> S = new HashSet<Integer>();
				for(int i = 0; i < N; i++) if(X[i] == 1) S.add(i);
				return S;
			}else{
				return null;
			}
		}
		
		
	}
	public void readData(String filename){
		try{
			Scanner in = new Scanner(new File(filename));
			N = in.nextInt();
			c = new double[N][N];
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++){
					c[i][j] = in.nextDouble();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void solve(){
		if(N > 10){
			System.out.println("N = 10 is too high to apply this solve method, use solveDynamicAddSubTourConstraint"); 
			return;
		}
		solver = new MPSolver("TSP solver", 
				MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
		
			
		X = new MPVariable[N][N];
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				if(i != j){
					X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
				}
			}
		}
		MPObjective obj = solver.objective();
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				if(i != j){
					obj.setCoefficient(X[i][j], c[i][j]);
				}
			}
		}
		
		// flow constraint
		for(int i = 0; i < N; i++){
			// \sum X[i,j] = 1, \forall j \in {0,...,N-1}\{i}
			MPConstraint fc1 = solver.makeConstraint(1,inf);
			for(int j = 0; j < N; j++) if(j != i){
				fc1.setCoefficient(X[i][j], 1);
			}
			MPConstraint fc2 = solver.makeConstraint(-inf,1);
			for(int j = 0; j < N; j++) if(j != i){
				fc2.setCoefficient(X[i][j], 1);
			}
			
			// \sum X[j][i] = 1, \forall j\in {0,1,...,N-1}\{i}
			MPConstraint fc3 = solver.makeConstraint(1,inf);
			for(int j = 0; j < N; j++) if(j != i){
				fc3.setCoefficient(X[j][i], 1);
			}
			
			MPConstraint fc4 = solver.makeConstraint(-inf,1);
			for(int j = 0; j < N; j++) if(j != i){
				fc4.setCoefficient(X[j][i], 1);
			}			
		}
		
		// sub-tour elimination constraints
		SubSetGenerator generator = new SubSetGenerator(N);
		HashSet<Integer> S = generator.first();
		while(S != null){
			if(S.size() > 1 && S.size() < N){
				//Sfor(int i: S) System.out.print(i + " "); System.out.println();
				MPConstraint sc = solver.makeConstraint(0,S.size() -1);
				for(int i: S){
					for(int j: S)if(i != j){
						sc.setCoefficient(X[i][j], 1);
					}
				}
			}
			S = generator.next();
		}
		
		System.out.println("finish model, start solve....");
		
		final MPSolver.ResultStatus resultStatus = solver.solve();
		 if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
		      System.err.println("The problem does not have an optimal solution!");
		      return;
		    }
		 
		 System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");

		    // The objective value of the solution.
		    System.out.println("Optimal objective value = " + solver.objective().value());

		    // The value of each variable in the solution.
		   for(int i = 0; i < N; i++){
			   for(int j = 0; j < N; j++){
				   if(i != j){
					   if(X[i][j].solutionValue() > 0)
					   System.out.println("X[" + i + "," + j + "] = " + X[i][j].solutionValue() + ", c= " + c[i][j]);
				   }
			   }
		   }

		    System.out.println("Advanced usage:");
		    System.out.println("Problem solved in " + solver.nodes() + " branch-and-bound nodes");
		    
		 ArrayList<Integer> tour = extractCycle(0);
		 for(int i = 0; i < tour.size(); i++) System.out.print(tour.get(i) + " -> "); System.out.println(tour.get(0));
	}
	private int findNext(int s){
		for(int i = 0; i < N; i++) if(i != s && X[s][i].solutionValue() > 0) return i;
		return -1;
	}
	
	public ArrayList<Integer> extractCycle(int s){
		ArrayList<Integer> L = new ArrayList<Integer>();
		int x = s;
		while(true){
			L.add(x);
			x = findNext(x);
			int rep = -1;
			for(int i = 0; i < L.size(); i++)if(L.get(i) == x){
				rep = i;
				break;
			}
			if(rep != -1){
				ArrayList<Integer> rL = new ArrayList<Integer>();
				for(int i = rep; i < L.size(); i++) rL.add(L.get(i));				
				break;
			}
		}
		return L;
	}
	private void createSolverWithSubTourConstraints(HashSet<ArrayList<Integer>> S){
		solver = new MPSolver("TSP solver", 
				MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
		X = new MPVariable[N][N];
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				if(i != j){
					X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
				}
			}
		}
		MPObjective obj = solver.objective();
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				if(i != j){
					obj.setCoefficient(X[i][j], c[i][j]);
				}
			}
		}
		
		// flow constraint
		for(int i = 0; i < N; i++){
			// \sum X[i,j] = 1, \forall j \in {0,...,N-1}\{i}
			MPConstraint fc1 = solver.makeConstraint(1,inf);
			for(int j = 0; j < N; j++) if(j != i){
				fc1.setCoefficient(X[i][j], 1);
			}
			MPConstraint fc2 = solver.makeConstraint(-inf,1);
			for(int j = 0; j < N; j++) if(j != i){
				fc2.setCoefficient(X[i][j], 1);
			}
			
			// \sum X[j][i] = 1, \forall j\in {0,1,...,N-1}\{i}
			MPConstraint fc3 = solver.makeConstraint(1,inf);
			for(int j = 0; j < N; j++) if(j != i){
				fc3.setCoefficient(X[j][i], 1);
			}
			
			MPConstraint fc4 = solver.makeConstraint(-inf,1);
			for(int j = 0; j < N; j++) if(j != i){
				fc4.setCoefficient(X[j][i], 1);
			}			
		}

		for(ArrayList<Integer> C: S){
			MPConstraint sc = solver.makeConstraint(0, C.size() -1);
			for(int i: C){
				for(int j : C) if(i != j){
					sc.setCoefficient(X[i][j], 1);
				}
			}
		}
	}
	public void solveDynamicAddSubTourConstraint(){
		
		HashSet<ArrayList<Integer>> S = new HashSet();
		boolean[] mark = new boolean[N];
		boolean found = false;
		while(!found){
			createSolverWithSubTourConstraints(S);
			final MPSolver.ResultStatus resultStatus = solver.solve();
			if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
			      System.err.println("The problem does not have an optimal solution!");
			      return;
			}
			System.out.println("obj = " + solver.objective().value());
			for(int i = 0; i < N; i++) mark[i] = false;
			
			for(int s = 0; s < N; s++)if(!mark[s]){
				ArrayList<Integer> C = extractCycle(s);
				if(C.size() < N){// sub-tour detected
					System.out.print("SubTour deteted, C = "); for(int i: C) System.out.print(i + " "); System.out.println();
					// add a constraint corresponding to this sub-tour
					S.add(C);
					for(int i: C) mark[i] = true;
				}else{
					System.out.println("Global tour detected, solution found!!!");
					found = true;
					break;
				}
			}
		}
		ArrayList<Integer> tour = extractCycle(0);
		 for(int i = 0; i < tour.size(); i++) System.out.print(tour.get(i) + " -> "); System.out.println(tour.get(0));

	}
	public void genData(String fn, int N, int maxD){
		try{
			Random R = new Random();
			PrintWriter out = new PrintWriter(fn);
			out.println(N);
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++){
					int x = R.nextInt(maxD) + 1;
					out.print(x + " ");
				}
				out.println();
			}
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("TSP...");
		
		TSPSolver app = new TSPSolver();
		//String filename = "data/TSP/sample-20.txt";
		//app.genData(filename, 20, 100);
		//app.readData(filename);
		//app.readData("data/TSP/sample.txt");
		//app.readData("data/TSP/sample-10.txt");
		//app.solve();
		app.solveDynamicAddSubTourConstraint();
	}
	*/
}
