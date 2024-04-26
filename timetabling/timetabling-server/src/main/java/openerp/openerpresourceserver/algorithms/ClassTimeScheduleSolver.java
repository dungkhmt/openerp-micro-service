package openerp.openerpresourceserver.algorithms;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.google.ortools.util.Domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.helper.MassExtractor;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ClassTimeScheduleSolver {

    private List<GeneralClassOpened> classes;
    private class VarArraySolutionPrinter extends CpSolverSolutionCallback {
        public VarArraySolutionPrinter(IntVar[] variables) {
            variableArray = variables;
        }

        @Override
        public void onSolutionCallback() {
            System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
            for (IntVar v : variableArray) {
                GeneralClassOpened gClass = classes.get(v.getIndex());
                gClass.getTimeSlots().clear();
                gClass.getTimeSlots().add(new RoomReservation(
                        (int)value(v)%6,
                        (int)value(v)%6 + MassExtractor.extract(gClass.getMass())-1,
                        (int)value(v)/6 +1,
                        null));
                System.out.printf("  %s = %d%n", v.getName(), value(v));
            }
            solutionCount++;
        }

        @Getter
        private int solutionCount;
        private final IntVar[] variableArray;
    }
    public List<GeneralClassOpened> solve() {

        Loader.loadNativeLibraries();
        CpModel model = new CpModel();

        /*Init the constraint*/
        int[] durations = classes.stream()
                .filter(c -> c.getMass() != null)
                .mapToInt(c -> MassExtractor.extract(c.getMass()))
                .toArray();

        int n = classes.size();
        int[][] conflict = new int[n][n];

        // Create the variables.

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                GeneralClassOpened classA = classes.get(i);
                GeneralClassOpened classB = classes.get(j);
                if (!classA.getModuleCode().equals(classB.getModuleCode())) {
                    conflict[i][j] = 1;
                } else {
                    conflict[i][j] = 0;
                }
            }
        }
        /*Add the domain constraints*/
        IntVar[] xc = new IntVar[n];

        for (int i = 0; i < n; i++) {
            List<Integer> allowedValues = new ArrayList<>();
            for (int startPeriod = 0; startPeriod < 42 - durations[i] ; startPeriod++) {
                if ((startPeriod / 6) == ((startPeriod + durations[i]) / 6)) allowedValues.add(startPeriod);
            }
            if (durations[i] == 1) {
                allowedValues.forEach(System.out::println);
            }
            xc[i] = model.newIntVarFromDomain(
                    Domain.fromValues(
                            allowedValues.stream().mapToLong(z -> z + 1).toArray()
                    ),
                    "c[" + i + "]");
        }


        // Create the constraints.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (conflict[i][j] == 1) {
                    BoolVar b = model.newBoolVar("b");
                    LinearExpr ei = LinearExpr.newBuilder().add(xc[i]).add(durations[i]).build();
                    LinearExpr ej = LinearExpr.newBuilder().add(xc[j]).add(durations[j]).build();
                    model.addLessOrEqual(ei, xc[j]).onlyEnforceIf(b);
                    model.addGreaterThan(ei, xc[i]).onlyEnforceIf(b.not());
                    model.addLessOrEqual(ej, xc[i]).onlyEnforceIf(b.not());
                }
            }
        }

        // Create a solver and solve the model.
        CpSolver solver = new CpSolver();
        VarArraySolutionPrinter cb = new VarArraySolutionPrinter(xc);

        // Tell the solver to enumerate all solutions.
//        solver.getParameters().setEnumerateAllSolutions(true);
        solver.getParameters().setEnumerateAllSolutions(false);


        // And solve.
        solver.solve(model, cb);
        System.out.println(cb.getSolutionCount() + " solutions found.");

        return classes;
    }
}
