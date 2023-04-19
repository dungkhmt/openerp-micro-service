package openerp.openerpresourceserver.service;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
public class PassBookOptimizerImpl {
    public static void main(String[]args ){
        // FOR TESTING OR-TOOLS
        int m = 3; int n = 3;
        MPVariable[][] x = new MPVariable[m][n];

        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver(String.valueOf(MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING));

        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return;
        }

        System.out.println("createSolverAndVariables, n = " + n + " m = " + m);
    }
}
