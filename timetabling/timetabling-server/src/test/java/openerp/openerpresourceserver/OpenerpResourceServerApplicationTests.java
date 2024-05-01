package openerp.openerpresourceserver;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.google.ortools.util.Domain;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.algorithms.V2ClassScheduler;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassroomRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GeneralClassOpenedRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@SpringBootTest
class OpenerpResourceServerApplicationTests {

    private final GeneralClassOpenedRepository gcoRepo;
    private final ClassroomRepo classroomRepo;
    private final int minPeriod = 2;
    private final int maxPeriod = 4;

    @Autowired
    OpenerpResourceServerApplicationTests(GeneralClassOpenedRepository gcoRepo, ClassroomRepo classroomRepo) {
        this.gcoRepo = gcoRepo;
        this.classroomRepo = classroomRepo;
    }

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

    @Test
    void v1SchedulerTest() {
        List<GeneralClassOpened> classes = gcoRepo.findAllBySemester("20221")
                .stream().filter(c -> (c.getGroupName() != null && c.getGroupName()
                        .startsWith("TAKHMT"))).toList();

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

        for (int i = 0 ; i < n; i++) {
            System.out.println( classes.get(i) + ", Duration: " + durations[i] + ", Domain: " + xc[i].getDomain());
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

    }


    @Test
    void v2SchedulerTimeSlotTest() {
        List<GeneralClassOpened> classes = gcoRepo.findAllBySemester("20232")
                .stream().filter(c -> (c.getGroupName() != null && c.getGroupName()
                        .startsWith("TA KHKT"))).toList();
        List<Classroom> rooms = classroomRepo.findAll().stream().filter(classroom -> !classroom.equals("")).toList();
        V2ClassScheduler.autoScheduleTimeSlot(classes);
    }
    @Test
    void v2SchedulerRoomTest() {
        List<GeneralClassOpened> classes = gcoRepo.findAllBySemester("20232")
                .stream().filter(c -> (
                        c.getGroupName() != null
                        && c.getGroupName().startsWith("TA KHKT"))
                        && !c.getQuantity().isEmpty()
                ).toList();
        List<Classroom> rooms = classroomRepo.findAll().stream().filter(classroom -> classroom.getQuantityMax() != 0).toList();
        V2ClassScheduler.autoScheduleRoom(classes, rooms);
    }


}
