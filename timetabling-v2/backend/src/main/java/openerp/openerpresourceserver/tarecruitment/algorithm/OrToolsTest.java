package openerp.openerpresourceserver.tarecruitment.algorithm;

import com.google.ortools.sat.*;
import com.google.ortools.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
public class OrToolsTest {

    public static void main(String[] args){
        Loader.loadNativeLibraries();
        final int numStudents = 6;
        final int numClasses = 6;

        final int[] allStudents = IntStream.range(0, numStudents).toArray();
        final int[] allClasses = IntStream.range(0, numClasses).toArray();

        // Yêu cầu lớp học
        final int [][] classRequests = new int[][] {
                {0, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {1, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 0},
                {0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1}
        };

        CpModel model = new CpModel();

        Literal[][] result = new Literal[numStudents][numClasses];


        // Lưu biến bằng 1 nếu lấy cặp này, không lấy sẽ là 0
        for(int i : allStudents) {
            for(int j : allClasses) {
                result[i][j] = model.newBoolVar("student number " + i + " in class number " + j);
            }
        }

        // Mỗi lớp chỉ có 1 sinh viên giảng dạy
        for(int j : allClasses) {
            List<Literal> students = new ArrayList<>();
            for(int i : allStudents) {
                students.add(result[i][j]);
            }
            model.addExactlyOne(students);
        }

        // Tối đa mỗi sinh viên dạy 1 lớp (sẽ cải tiến sau)
        for(int i : allStudents) {
            List<Literal> classes = new ArrayList<>();
            for(int j : allClasses) {
                classes.add(result[i][j]);
            }
            model.addAtMostOne(classes); // Có hàm atLeast
        }

        LinearExprBuilder obj = LinearExpr.newBuilder();
        for(int i : allStudents) {
            for(int j : allClasses) {
                obj.addTerm(result[i][j], classRequests[i][j]);
            }
        }
        model.maximize(obj);

        CpSolver solver = new CpSolver();
//        solver.getParameters().setLinearizationLevel(0);
//        // Tell the solver to enumerate all solutions.
//        solver.getParameters().setEnumerateAllSolutions(true);

//        final int solutionLimit = 5;
//
//        class VarArraySolutionPrinterWithLimit extends CpSolverSolutionCallback {
//            public VarArraySolutionPrinterWithLimit(
//                    int[] allStudents, int[] allClasses, Literal[][] result, int limit) {
//                solutionCount = 0;
//                this.allStudents = allStudents;
//                this.allClasses = allClasses;
//                this.result = result;
//                solutionLimit = limit;
//            }
//
//            @Override
//            public void onSolutionCallback() {
//                System.out.printf("Solution #%d:%n", solutionCount);
//                for (int i : allStudents) {
//                    System.out.printf("Student %d%n", i);
//                    boolean isWorking = false;
//                    for(int j : allClasses) {
//                        if(booleanValue(result[i][j])) {
//                            isWorking = true;
//                            System.out.printf(" Student %d work in class %d%n", i, j);
//                        }
//                    }
//                    if(!isWorking) {
//                        System.out.printf("  Student %d dont have class%n", i);
//                    }
//                }
//                solutionCount++;
//                if (solutionCount >= solutionLimit) {
//                    System.out.printf("Stop search after %d solutions%n", solutionLimit);
//                    stopSearch();
//                }
//            }
//
//            public int getSolutionCount() {
//                return solutionCount;
//            }
//
//            private int solutionCount;
//            private final int[] allStudents;
//            private final int[] allClasses;
//            private final Literal[][] result;
//            private final int solutionLimit;
//        }
//
//        VarArraySolutionPrinterWithLimit cb = new VarArraySolutionPrinterWithLimit(allStudents, allClasses, result, solutionLimit);

        CpSolverStatus status = solver.solve(model);
//        System.out.println("Status: " + status);
//        System.out.println(cb.getSolutionCount() + " solutions found.");

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.printf("Solution:%n");
            for (int i : allStudents) {
                System.out.printf("Student %d%n", i);

                for(int j : allClasses) {
                    if (solver.booleanValue(result[i][j])) {
                        if(classRequests[i][j] == 1) {
                            System.out.printf("  Student %d works class %d (requested).%n", i, j);
                        }
                        else {
                            System.out.printf("  Student %d works class %d (not requested).%n", i, j);
                        }
                    }
                }
            }
            System.out.printf("Number of class requests met = %f (out of %d)%n", solver.objectiveValue(),
                    numClasses);
        } else {
            System.out.printf("No optimal solution found !");
        }

        // Statistics.
        System.out.println("Statistics");
        System.out.printf("  conflicts: %d%n", solver.numConflicts());
        System.out.printf("  branches : %d%n", solver.numBranches());
        System.out.printf("  wall time: %f s%n", solver.wallTime());

    }
}
