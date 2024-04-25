package openerp.openerpresourceserver.algorithms;


import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;

public class Coloring {
    static class VarArraySolutionPrinter extends CpSolverSolutionCallback {
        public VarArraySolutionPrinter(IntVar[] variables) {
            variableArray = variables;
        }

        @Override
        public void onSolutionCallback() {
            System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
            for (IntVar v : variableArray) {
                System.out.printf("  %s = %d%n", v.getName(), value(v));
            }
            solutionCount++;
        }

        public int getSolutionCount() {
            return solutionCount;
        }

        private int solutionCount;
        private final IntVar[] variableArray;
    }
    public static void main(String[] args){
        System.out.println("start");
        Loader.loadNativeLibraries();

        // Create the model.
        CpModel model = new CpModel();
        int[][] conflict = {
                {0,0,1,0,1},
                {0,0,1,1,0},
                {1,1,0,1,0},
                {0,1,1,0,1},
                {1,0,0,1,0}
        };
        int n = 5;
        // Create the variables.
        long numVals = 3;
        IntVar[] xc = new IntVar[n];
        for(int i = 0; i < n; i++)
            xc[i] = model.newIntVar(0,numVals-1,"c[" + i + "]");

        //IntVar x = model.newIntVar(1, numVals , "x");
        //IntVar y = model.newIntVar(1, numVals , "y");
        //IntVar z = model.newIntVar(1, numVals, "z");

        // Create the constraints.
        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++){
                if(conflict[i][j] == 1){
                    model.addDifferent(xc[i],xc[j]);
                }
            }
        }
        //model.addDifferent(x, y);
        //model.addDifferent(y,z);
        //model.addDifferent(x,z);

        // Create a solver and solve the model.
        CpSolver solver = new CpSolver();
        //VarArraySolutionPrinter cb = new VarArraySolutionPrinter(new IntVar[] {x, y, z});
        VarArraySolutionPrinter cb = new VarArraySolutionPrinter(xc);

        // Tell the solver to enumerate all solutions.
        solver.getParameters().setEnumerateAllSolutions(true);
        //solver.getParameters().setEnumerateAllSolutions(false);


        // And solve.
        solver.solve(model, cb);

        System.out.println(cb.getSolutionCount() + " solutions found.");
    }

}
