package openerp.openerpresourceserver.labtimetabling.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Solver;
import com.google.ortools.sat.*;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.AutoSchedulingVar;
import org.springframework.security.core.parameters.P;

import javax.sound.sampled.Line;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class LabTimetablingSolver {
    private static List<Class> classList;
    private static List<Room> roomList;
    private static int N_CLASSES ;
    private static int N_ROOMS ;
    private static int N_WEEKS = 20;
    private static int N_PERIODS = 10; // 2 per day (morning, afternoon) * 5
    private static int N_LESSONS = 6;  // 6 lessons / period

    private static CpModel model;
    private static IntVar[][][][][] x;
    private static IntVar[] y;
    private static IntVar[][] z;

    private Long solvingTimeLimit;

    public LabTimetablingSolver(List<Class> classList, List<Room> roomList, Long solvingTimeLimit){
        LabTimetablingSolver.classList = classList;
        LabTimetablingSolver.roomList = roomList;
        N_CLASSES = classList.size();
        N_ROOMS = roomList.size();
        this.solvingTimeLimit = solvingTimeLimit;

        Loader.loadNativeLibraries();

        x = new IntVar[N_CLASSES][N_PERIODS][N_LESSONS][N_ROOMS][N_WEEKS];
        y = new IntVar[N_CLASSES];
        z = new IntVar[N_CLASSES][N_WEEKS];
        model = new CpModel();
    }
    private static class VarArraySolutionPrinter extends CpSolverSolutionCallback {
        private List<IntVar> xs = new ArrayList<>();
        public VarArraySolutionPrinter(List<IntVar> xs){
            this.xs=xs;
        }
        @Override
        public void onSolutionCallback() {
            System.out.println("onSolutionCallback "+wallTime());
            List<String> nameList = xs.stream().filter(var->value(var)==1).map(IntVar::getName).toList();
            List<List<String>> filteredStrings = filterStrings(nameList);
            for (List<String> group : filteredStrings) {
                for (String str : group) {
                    System.out.println(str);
                }
                System.out.println();
            }
        }
        public List<List<String>> filterStrings(List<String> strings) {
            Map<Integer, List<String>> groups = new HashMap<>();
            for (String str : strings) {
                int firstNumber = getFirstNumber(str);
                groups.computeIfAbsent(firstNumber, k -> new ArrayList<>()).add(str);
            }

            return groups.values().stream()
                    .map(stringsInGroup -> {
                        int minThirdElement = stringsInGroup.stream()
                                .mapToInt(this::getThirdElement)
                                .min().orElse(Integer.MAX_VALUE);

                        return stringsInGroup.stream()
                                .filter(str -> getThirdElement(str) == minThirdElement)
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());
        }

        private int getLastNumber(String str) {
            String[] parts = str.split(",");
            String lastPart = parts[parts.length - 1];
            return Integer.parseInt(lastPart.substring(0, lastPart.length() - 1).trim());
        }

        private int getThirdElement(String str) {
            String[] parts = str.split(",");
            String thirdPart = parts[2];
            return Integer.parseInt(thirdPart.trim());
        }

        private int getFirstNumber(String str) {
            String[] parts = str.split(",")[0].split("\\[");
            String firstPart = parts[parts.length - 1];
            return Integer.parseInt(firstPart.trim());
        }
    }
    public abstract static class OptionalConstraint {
        List<Long> classes_id;
        List<List<Integer>> weeks;
        public OptionalConstraint(List<Long> classes_id, List<List<Integer>> weeks){
            this.classes_id = classes_id;
            this.weeks = weeks;
        }
        abstract void applyConstraint();
    }
    public static class ConsistentWeeklyScheduleConstraint extends OptionalConstraint{
        public ConsistentWeeklyScheduleConstraint(List<Long> classes_id) {
            super(classes_id, null);
        }

        @Override
        public void applyConstraint() {
            for (Long aLong : classes_id) {
                for (int r = 0; r < N_ROOMS; r++) {
                    for (int d = 0; d < N_PERIODS; d++) {
                        for (int k = 0; k < N_LESSONS; k++) {
                            for (int w = 0; w < N_WEEKS; w++) {
                                BoolVar c = model.newBoolVar("c3");
                                OptionalInt class_index = IntStream.range(0, N_CLASSES)
                                        .filter(item -> Objects.equals(classList.get(item).getId(), aLong))
                                        .findFirst();
                                int i = class_index.getAsInt();
                                model.addEquality(x[i][d][k][r][w], 1).onlyEnforceIf(c);
                                model.addEquality(x[i][d][k][r][w], 0).onlyEnforceIf(c.not());

                                IntVar[] sum = new IntVar[N_WEEKS];
                                for (int t = 0; t < N_WEEKS; t++) sum[t] = x[i][d][k][r][t];
                                LinearExpr e = LinearExpr.newBuilder().addSum(sum).build();
                                model.addEquality(e, 4).onlyEnforceIf(c);
                            }
                        }
                    }
                }
            }
        }
    }
    public static class EvenWeekScheduleConstraint extends OptionalConstraint{
        public EvenWeekScheduleConstraint(List<Long> classes_id) {
            super(classes_id, null);
        }

        @Override
        public void applyConstraint() {
            for (int cl = 0; cl < classes_id.size(); cl++) {
                for (int r = 0; r < N_ROOMS; r++) {
                    for (int d = 0; d < N_PERIODS; d++) {
                        for (int k = 0; k < N_LESSONS; k++) {
                            for (int w= 0; w < N_WEEKS; w++) {
                                BoolVar c1 = model.newBoolVar("week_assign");
                                Long class_id = classes_id.get(cl);
                                OptionalInt class_index = IntStream.range(0, N_CLASSES)
                                        .filter(item -> Objects.equals(classList.get(item).getId(), class_id))
                                        .findFirst();
                                int i = class_index.getAsInt();
                                model.addEquality(x[i][d][k][r][w], 1).onlyEnforceIf(c1);
                                model.addEquality(x[i][d][k][r][w], 0).onlyEnforceIf(c1.not());
                                model.addEquality(z[i][w], 1).onlyEnforceIf(c1);

                                ArrayList<IntVar> hs = new ArrayList<>();
                                for(int h=0;h<N_WEEKS;h+=2){
                                    hs.add(z[i][h]);
                                }
                                IntVar[] sum = new IntVar[hs.size()];
                                LinearExpr e = LinearExpr.newBuilder().addSum(hs.toArray(sum)).build();
                                model.addEquality(e, 0).onlyEnforceIf(c1);
                            }
                        }
                    }
                }
            }
        }
    }
    public static class OddWeekScheduleConstraint extends OptionalConstraint{
        public OddWeekScheduleConstraint(List<Long> classes_id) {
            super(classes_id, null);
        }

        @Override
        public void applyConstraint() {
            for (int cl = 0; cl < classes_id.size(); cl++) {
                for (int r = 0; r < N_ROOMS; r++) {
                    for (int d = 0; d < N_PERIODS; d++) {
                        for (int k = 0; k < N_LESSONS; k++) {
                            for (int w= 0; w < N_WEEKS; w++) {
                                BoolVar c1 = model.newBoolVar("week_assign");
                                Long class_id = classes_id.get(cl);
                                OptionalInt class_index = IntStream.range(0, N_CLASSES)
                                        .filter(item -> Objects.equals(classList.get(item).getId(), class_id))
                                        .findFirst();
                                int i = class_index.getAsInt();
                                model.addEquality(x[i][d][k][r][w], 1).onlyEnforceIf(c1);
                                model.addEquality(x[i][d][k][r][w], 0).onlyEnforceIf(c1.not());
                                model.addEquality(z[i][w], 1).onlyEnforceIf(c1);

                                ArrayList<IntVar> hs = new ArrayList<>();
                                for(int h=1;h<N_WEEKS;h+=2){
                                    hs.add(z[i][h]);
                                }
                                IntVar[] sum = new IntVar[hs.size()];
                                LinearExpr e = LinearExpr.newBuilder().addSum(hs.toArray(sum)).build();
                                model.addEquality(e, 0).onlyEnforceIf(c1);
                            }
                        }
                    }
                }
            }
        }
    }
    public static class AvoidWeekScheduleConstraint extends OptionalConstraint{
        public AvoidWeekScheduleConstraint(List<Long> classes_id, List<List<Integer>> weeks ){
            super(classes_id, weeks);
        }

        @Override
        void applyConstraint() {
            for (int cl = 0; cl < classes_id.size(); cl++) {
                for (int r = 0; r < N_ROOMS; r++) {
                    for (int d = 0; d < N_PERIODS; d++) {
                        for (int k = 0; k < N_LESSONS; k++) {
                            for (int w= 0; w < N_WEEKS; w++) {
                                BoolVar c1 = model.newBoolVar("week_assign");
                                Long class_id = classes_id.get(cl);
                                OptionalInt class_index = IntStream.range(0, N_CLASSES)
                                        .filter(item -> Objects.equals(classList.get(item).getId(), class_id))
                                        .findFirst();
                                int i = class_index.getAsInt();
                                model.addEquality(x[i][d][k][r][w], 1).onlyEnforceIf(c1);
                                model.addEquality(x[i][d][k][r][w], 0).onlyEnforceIf(c1.not());
                                model.addEquality(z[i][w], 1).onlyEnforceIf(c1);

                                for(int v=0;v<weeks.get(cl).size();v++) {
                                    LinearExpr e = LinearExpr.newBuilder().add(z[i][weeks.get(cl).get(v)]).build();
                                    model.addEquality(e, 0).onlyEnforceIf(c1);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    public List<List<AutoSchedulingVar>> solve(List<OptionalConstraint> optionalConstraints) {
        // set_model
        for (int i = 0; i < N_CLASSES; i++) {
            for (int r = 0; r < N_ROOMS; r++) {
                for (int d = 0; d < N_PERIODS; d++) {
                    for (int k = 0; k < N_LESSONS; k++) {
                        for (int w = 0; w < N_WEEKS; w++) {
                            x[i][d][k][r][w] = model.newIntVar(0, 1, String.format("x[%d, %d, %d, %d, %d]", i, d, k, r, w));
                        }
                    }
                }
            }
        }
        for (int i=0;i<N_CLASSES;i++){
            for(int _w=0;_w<N_WEEKS;_w++){
                z[i][_w] = model.newIntVar(0, 1, String.format("z[%d, %d]", i, _w));
            }
        }
        for (int i = 0; i < N_CLASSES; i++) {
            y[i] = model.newIntVar(0, 1, "y[" + i + "]");
        }

        // set_constraint

        // Number of students in each class <= room capacity
        for (int i = 0; i < N_CLASSES; i++) {
            for (int r = 0; r < N_ROOMS; r++) {
                for (int d = 0; d < N_PERIODS; d++) {
                    for (int k = 0; k < N_LESSONS; k++) {
                        for (int w = 0; w < N_WEEKS; w++) {
                            LinearExpr e = LinearExpr.newBuilder().addTerm(x[i][d][k][r][w], classList.get(i).getQuantity()).build();
                            model.addLessOrEqual(e, roomList.get(r).getCapacity());
                        }
                    }
                }
            }
        }
        // Only one class in one room at a time
        for (int r = 0; r < N_ROOMS; r++) {
            for (int d = 0; d < N_PERIODS; d++) {
                for (int k = 0; k < N_LESSONS; k++) {
                    for (int w = 0; w < N_WEEKS; w++) {
                        IntVar[] sum = new IntVar[N_CLASSES];
                        for (int i = 0; i < N_CLASSES; i++) sum[i] = x[i][d][k][r][w];
                        LinearExpr e = LinearExpr.newBuilder().addSum(sum).build();
                        model.addLessOrEqual(e, 1);
                    }
                }
            }
        }
//        // A room is assigned to only one class at a time
        for (int i = 0; i < N_CLASSES; i++) {
            for (int d = 0; d < N_PERIODS; d++) {
                for (int k = 0; k < N_LESSONS; k++) {
                    for (int w = 0; w < N_WEEKS; w++) {
                        IntVar[] sum = new IntVar[N_ROOMS];
                        for (int r = 0; r < N_ROOMS; r++) sum[r] = x[i][d][k][r][w];
                        LinearExpr e = LinearExpr.newBuilder().addSum(sum).build();
                        model.addLessOrEqual(e, 1);
                    }
                }
            }
        }
//        // Lessons must be completed in one session
        for (int i = 0; i < N_CLASSES; i++) {
            for (int r = 0; r < N_ROOMS; r++) {
                for (int d = 0; d < N_PERIODS; d++) {
                    for (int w = 0; w < N_WEEKS; w++) {
                        BoolVar c = model.newBoolVar("c");
                        IntVar[] sum = new IntVar[N_LESSONS];
                        for(int k=0;k<N_LESSONS;k++) sum[k] = x[i][d][k][r][w];
                        LinearExpr e = LinearExpr.newBuilder().addSum(sum).build();
                        model.addEquality(e, classList.get(i).getPeriod()).onlyEnforceIf(c);
                        model.addEquality(e, 0).onlyEnforceIf(c.not());
                    }
                }
            }
        }
        // Lessons of each class must be continuous
        for (int i = 0; i < N_CLASSES; i++) {
            for (int r = 0; r < N_ROOMS; r++) {
                for (int d = 0; d < N_PERIODS; d++) {
                    for (int b = 0; b < N_LESSONS - classList.get(i).getPeriod(); b++) {
                        for (int w = 0; w < N_WEEKS; w++) {
                            IntVar[] sum = new IntVar[classList.get(i).getPeriod()];
                            for (int k = b; k < b + classList.get(i).getPeriod(); k++) {
                                sum[k - b] = x[i][d][k][r][w];
                            }
                            LinearExpr el = LinearExpr.newBuilder().addSum(sum).build();
                            LinearExpr er = LinearExpr.newBuilder().add(x[i][d][b][r][w]).add(classList.get(i).getPeriod()).build();

                            BoolVar c = model.newBoolVar("bool_var");
                            model.addEquality(el, er).onlyEnforceIf(c);
                            model.addEquality(x[i][d][b][r][w], 1).onlyEnforceIf(c);
                            model.addEquality(x[i][d][b][r][w], 0).onlyEnforceIf(c.not());
                        }
                    }
                }
            }
        }

        //
        for(int i=0;i<N_CLASSES;i++){
            BoolVar c = model.newBoolVar(" ");
            model.addEquality(y[i], 1).onlyEnforceIf(c);
            model.addEquality(y[i], 0).onlyEnforceIf(c.not());

            ArrayList<IntVar> sum = new ArrayList<>();
            for (int r = 0; r < N_ROOMS; r++) {
                for (int d = 0; d < N_PERIODS; d++) {
                    for (int k = 0; k < N_LESSONS; k++) {
                        for (int w = 0; w < N_WEEKS; w++) {
                            sum.add(x[i][d][k][r][w]);
                        }
                    }
                }
            }
            IntVar[] list_constraint = new IntVar[sum.size()];
            LinearExpr el = LinearExpr.newBuilder().addSum(sum.toArray(list_constraint)).build();
            model.addEquality(el, classList.get(i).getPeriod()*4).onlyEnforceIf(c);
            model.addEquality(el, 0).onlyEnforceIf(c.not());
        }

        for(OptionalConstraint constraint: optionalConstraints){
            constraint.applyConstraint();
        }

        LinearExpr obj = LinearExpr.newBuilder().addSum(y).build();
        model.maximize(obj);

        CpSolver solver = new CpSolver();
        ArrayList<IntVar> xs = new ArrayList<>();
        for (int i=0;i<N_CLASSES;i++) {
            for (int r = 0; r < N_ROOMS; r++) {
                for (int d = 0; d < N_PERIODS; d++) {
                    for (int k = 0; k < N_LESSONS; k++) {
                        for (int w = 0; w < N_WEEKS; w++) {
                            xs.add(x[i][d][k][r][w]);
                        }
                    }
                }
            }
        }
        VarArraySolutionPrinter printer = new VarArraySolutionPrinter(xs);
        if (this.getSolvingTimeLimit() >0) {
            solver.getParameters().setMaxTimeInSeconds(this.getSolvingTimeLimit());
            System.out.println(this.getSolvingTimeLimit());
        }
        CpSolverStatus status = solver.solve(model, printer);
        if (status == CpSolverStatus.OPTIMAL) {
            List<String> nameList = xs.stream().filter(var-> solver.value(var)==1).map(IntVar::getName).toList();
            List<List<String>> filteredStrings = filterStrings(nameList);
            List<List<AutoSchedulingVar>> autoSchedulingVars = filteredStrings.stream().map(group -> {
                List<AutoSchedulingVar> vars = new ArrayList<>();
                for (String var : group) {
                    List<Long> nums = varToNumberList(var);
//              class_id period lesson room_id week
                    AutoSchedulingVar autoSchedulingVar = new AutoSchedulingVar.Builder()
                            .setClassId(nums.get(0))
                            .setPeriod(nums.get(1))
                            .setLesson(nums.get(2))
                            .setRoomId(nums.get(3))
                            .setWeek(nums.get(4))
                            .build();
                    vars.add(autoSchedulingVar);
                }
                return vars;
            }).toList();
            return autoSchedulingVars;
        }
        return null;
    }
    public List<List<String>> filterStrings(List<String> strings) {
        Map<Integer, List<String>> groups = new HashMap<>();
        for (String str : strings) {
            int firstNumber = getFirstNumber(str);
            groups.computeIfAbsent(firstNumber, k -> new ArrayList<>()).add(str);
        }

        return groups.values().stream()
                .map(stringsInGroup -> {
                    int minThirdElement = stringsInGroup.stream()
                            .mapToInt(this::getThirdElement)
                            .min().orElse(Integer.MAX_VALUE);

                    return stringsInGroup.stream()
                            .filter(str -> getThirdElement(str) == minThirdElement)
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
    }
    private int getThirdElement(String str) {
        String[] parts = str.split(",");
        String thirdPart = parts[2];
        return Integer.parseInt(thirdPart.trim());
    }

    public int getFirstNumber(String str) {
        String[] parts = str.split(",")[0].split("\\[");
        String firstPart = parts[parts.length - 1];
        return Integer.parseInt(firstPart.trim());
    }

    public List<Long> varToNumberList(String var){
        ArrayList<Long> nums = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(var);
        while (matcher.find()) {
            nums.add(Long.parseLong(matcher.group()));
        }
        nums.set(0, classList.get(Math.toIntExact(nums.get(0))).getId());
        nums.set(nums.size()-2, roomList.get(Math.toIntExact(nums.get(nums.size() - 2))).getId());
        return nums;
    }
}
