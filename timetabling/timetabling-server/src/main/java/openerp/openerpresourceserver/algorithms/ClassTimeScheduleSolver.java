package openerp.openerpresourceserver.algorithms;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.google.ortools.util.Domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.helper.MassExtractor;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Log4j2
public class ClassTimeScheduleSolver {

    private static class VarArraySolutionPrinter extends CpSolverSolutionCallback {
        private static List<GeneralClassOpened> classes;
        public VarArraySolutionPrinter(IntVar[] variables, List<GeneralClassOpened> classes) {
            variableArray = variables;
            VarArraySolutionPrinter.classes = classes;
        }

        @Override
        public void onSolutionCallback() {
            if (solutionCount > 0) return;
            System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
            for (IntVar v : variableArray) {
                GeneralClassOpened gClass = classes.get(v.getIndex());
                gClass.getTimeSlots().forEach(rr -> rr.setGeneralClassOpened(null));
                gClass.getTimeSlots().clear();
                RoomReservation newRoomReservation = new RoomReservation(
                        (int)value(v)%6,
                        (int)value(v)%6 + MassExtractor.extract(gClass.getMass())-1,
                        (int)value(v)/6 +1,
                        null) ;
                newRoomReservation.setGeneralClassOpened(gClass);
                gClass.getTimeSlots().add(newRoomReservation);
                System.out.printf("  %s = %d%n", v.getName(), value(v));
            }
            solutionCount++;
        }

        @Getter
        private int solutionCount;
        private final IntVar[] variableArray;
    }
    public static List<GeneralClassOpened> solve(List<GeneralClassOpened> classes) {
        log.info("solve... by or-tools, start to loadNativeLibraries");
        Loader.loadNativeLibraries();
        log.info("solve... by or-tools, loadNativeLibraries -> OK ");

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
        VarArraySolutionPrinter cb = new VarArraySolutionPrinter(xc, classes);

        log.info("solve... by or-tools created model OK, start to search...");

        // Tell the solver to enumerate all solutions.
//        solver.getParameters().setEnumerateAllSolutions(true);
        solver.getParameters().setEnumerateAllSolutions(false);


        // And solve.
        log.info("solve, start to call solver.solve...");
        solver.solve(model, cb);
        log.info("solve, finished to call solver.solve...");
        System.out.println(cb.getSolutionCount() + " solutions found.");

        return classes;
    }

    public void autoScheduleRoom(){
        log.info("autoScheduleRoom... by or-tools");
        Loader.loadNativeLibraries();
        CpModel model = new CpModel();
        CpSolver solver = new CpSolver();
        solver.getParameters().setEnumerateAllSolutions(false);
        //VarArraySolutionPrinter cb = new VarArraySolutionPrinter(xc, classes);


        // And solve.
        //solver.solve(model, cb);
    }
}
