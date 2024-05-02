package openerp.openerpresourceserver.labtimetabling.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;

import javax.sound.sampled.Line;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class LabTimetablingSolver {
    List<Class> classList;
    List<Room> roomList;
    int N_CLASSES ;
    int N_ROOMS ;
    int N_WEEKS = 20;
    int N_PERIODS = 10; // 2 per day (morning, afternoon) * 5
    int N_LESSONS = 6;  // 6 lessons / period
    public LabTimetablingSolver(List<Class> classList, List<Room> roomList){
        this.classList = classList;
        this.roomList = roomList;
        this.N_CLASSES = classList.size();
        this.N_ROOMS = roomList.size();
    }
    private class VarArraySolutionPrinter extends CpSolverSolutionCallback {
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

        public int getFirstNumber(String str) {
            String[] parts = str.split(",")[0].split("\\[");
            String firstPart = parts[parts.length - 1];
            return Integer.parseInt(firstPart.trim());
        }
    }
    public String solve() {
        Loader.loadNativeLibraries();

        IntVar[][][][][] x = new IntVar[N_CLASSES][N_PERIODS][N_LESSONS][N_ROOMS][N_WEEKS];
        IntVar[] y = new IntVar[N_CLASSES];

        CpModel model = new CpModel();

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
                            for (int k=b;k<b+classList.get(i).getPeriod();k++){
                                sum[k-b] = x[i][d][k][r][w];
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

        // optional condition
//        for (int i = 0; i < N_CLASSES; i++) {
//            for (int r = 0; r < N_ROOMS; r++) {
//                for (int d = 0; d < N_PERIODS; d++) {
//                    for (int k = 0; k < N_LESSONS; k++) {
//                        for (int w = 0; w < N_WEEKS; w++) {
//                            BoolVar c = model.newBoolVar("c3");
//                            model.addEquality(x[i][d][k][r][w], 1).onlyEnforceIf(c);
//                            model.addEquality(x[i][d][k][r][w], 0).onlyEnforceIf(c.not());
//
//                            IntVar[] sum = new IntVar[N_WEEKS];
//                            for (int t=0;t<N_WEEKS;t++) sum[t] = x[i][d][k][r][t];
//                            LinearExpr e = LinearExpr.newBuilder().addSum(sum).build();
//                            model.addEquality(e, 4).onlyEnforceIf(c);
//                        }
//                    }
//                }
//            }
//        }

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
        CpSolverStatus status = solver.solve(model, printer);
        List<String> nameList = xs.stream().filter(var-> solver.value(var)==1).map(IntVar::getName).toList();
        List<List<String>> filteredStrings = filterStrings(nameList);

        StringBuilder res = new StringBuilder();
        res.append(String.format("%d classes - %d rooms", classList.size(), roomList.size()));
        res.append("\n");
        for (List<String> group : filteredStrings) {
            for (String str : group) {
                res.append(str);
                res.append("\n");
            }
            res.append("\n");
        }
        res.append(status);
        return res.toString();
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

    public int getFirstNumber(String str) {
        String[] parts = str.split(",")[0].split("\\[");
        String firstPart = parts[parts.length - 1];
        return Integer.parseInt(firstPart.trim());
    }
}
