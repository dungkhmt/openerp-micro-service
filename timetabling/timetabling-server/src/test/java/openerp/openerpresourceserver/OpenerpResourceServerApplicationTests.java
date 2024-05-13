package openerp.openerpresourceserver;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.google.ortools.util.Domain;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.algorithms.V2ClassScheduler;
import openerp.openerpresourceserver.generaltimetabling.helper.ClassTimeComparator;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.V2UpdateClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassroomRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GeneralClassRepository;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.RoomOccupationRepo;
import openerp.openerpresourceserver.generaltimetabling.service.GeneralClassService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@SpringBootTest
class OpenerpResourceServerApplicationTests {

    private final GeneralClassRepository gcoRepo;
    private final ClassroomRepo classroomRepo;
    private final RoomOccupationRepo roomOccupationRepo;
    private final GroupRepo groupRepo;

    private final GeneralClassService gService;
    private final int minPeriod = 2;
    private final int maxPeriod = 4;

    @Autowired
    OpenerpResourceServerApplicationTests(GeneralClassRepository gcoRepo, ClassroomRepo classroomRepo, RoomOccupationRepo roomOccupationRepo, GroupRepo groupRepo, GeneralClassService gService) {
        this.gcoRepo = gcoRepo;
        this.classroomRepo = classroomRepo;
        this.roomOccupationRepo = roomOccupationRepo;
        this.groupRepo = groupRepo;
        this.gService = gService;
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
        List<GeneralClass> classes = gcoRepo.findAllBySemester("20221")
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
                GeneralClass classA = classes.get(i);
                GeneralClass classB = classes.get(j);
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
        List<GeneralClass> classes = gcoRepo.findAllBySemester("20232")
                .stream().filter(c -> (c.getGroupName() != null && c.getGroupName()
                        .startsWith("TA KHKT"))).toList();
        Group group = groupRepo.findByGroupName("TA KHKT").orElse(null);
        List<Classroom> rooms = classroomRepo
                .getClassRoomByBuildingIn(Arrays.stream(group.getPriorityBuilding().split(",")).toList())
                .stream().filter(classroom -> !classroom.equals("")).toList();
        V2ClassScheduler.autoScheduleTimeSlot(classes, 2);
    }
    @Test
    void v2SchedulerRoomTest() {
        List<GeneralClass> classes = gcoRepo.findAllBySemester("20232")
                .stream().filter(c -> (
                        c.getGroupName() != null
                        && c.getGroupName().startsWith("TA KHKT"))
                        && !c.getQuantityMax().isEmpty()
                ).toList();
        Group group = groupRepo.findByGroupName("TA KHKT").orElse(null);
        List<Classroom> rooms = classroomRepo
                .getClassRoomByBuildingIn(Arrays.stream(group.getPriorityBuilding().split(",")).toList())
                .stream().filter(classroom -> !classroom.equals("")).toList();
        List<RoomOccupation> roomOccupations = roomOccupationRepo.findAllBySemester("20232");
        V2ClassScheduler.autoScheduleRoom(classes, rooms, 2, roomOccupations);
    }

    @Test
    void testFetchRoomOccuptionByClassCodes() {
        List<String> classCodes = List.of(new String[]{"136649","136650","136651"});
        List<RoomOccupation> fetchRoomOccupations =  roomOccupationRepo.findAllByClassCodeIn(classCodes);
        fetchRoomOccupations.forEach(System.out::println);
    }

    @Test
    void testRoomConflict() {
        List<GeneralClass> classes = gcoRepo.findAllBySemester("20232");
        GeneralClass conflictClass = gcoRepo.findById(Long.valueOf(31838)).orElse(null);
        RoomReservation rr = conflictClass.getTimeSlots().get(0);
        System.out.println(ClassTimeComparator.isClassConflict(rr, conflictClass, classes));
    }

    @Test
    void testV2ScheduleTime() {
        V2UpdateClassScheduleRequest request1 = new V2UpdateClassScheduleRequest(33208L, 1, 2, 4, "D3-101");
        V2UpdateClassScheduleRequest request2 = new V2UpdateClassScheduleRequest(30878L, 1, 2, 5, "D3-101");
        gService.v2UpdateClassSchedule("20232", List.of(request1, request2)).forEach(System.out::println);
    }

    @Test
    void testGetClassRoomByBuildings() {
        Group group = groupRepo.findByGroupName("TA KHKT").orElse(null);
        List<Classroom> rooms = classroomRepo.getClassRoomByBuildingIn(Arrays.stream(group.getPriorityBuilding().split(",")).toList());
        rooms.forEach(System.out::println);
    }
    @Test
    void testStringUtil() {
        System.out.println(StringUtils.isNumeric("40.0"));
    }
}
