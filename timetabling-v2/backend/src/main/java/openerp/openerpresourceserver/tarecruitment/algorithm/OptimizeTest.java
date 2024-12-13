package openerp.openerpresourceserver.tarecruitment.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.Random;

public class OptimizeTest {

    static int[][] graph(int m, int n) {
        Random random = new Random();
        int[][] result = new int[m][n];
        for(int i = 0; i < m; i ++) {
            for(int j = 0; j < n; j++) {
                result[i][j] = random.nextInt(2);
            }
        }
        return result;
    }

    public static void main(String[] args) {

        Loader.loadNativeLibraries();

        int numTest = 100;

        int numClasses = numTest;

        int numStudents = numTest;

        int maxClassesPerStudent = 4;

        int maxClassesGotAssign = 0;

        // Sinh viên yêu cầu vào lớp nào
//        final int[][] request = {
//                {0, 1, 1, 0, 0, 0},
//                {0, 0, 0, 0, 1, 0},
//                {1, 0, 0, 1, 0, 0},
//                {0, 0, 1, 0, 0, 0},
//                {0, 0, 1, 1, 0, 0},
//                {0, 0, 0, 0, 0, 1}
//        };

//        final int[][] request = {
//                {1, 0, 0, 0, 0},
//                {1, 1, 1, 1, 1},
//                {1, 1, 1, 1, 1},
//                {1, 1, 1, 1, 1},
//                {1, 1, 1, 0, 1}
//        };

        int[][] request = graph(numStudents, numClasses);

        // Các lớp conflict về mặt thời gian với nhau
//        final int[][] conflictBetweenClasses = new int[][] {
//                {0, 1, 0, 0, 0, 0},
//                {1, 0, 0, 0, 0, 0},
//                {0, 0, 0, 1, 0, 0},
//                {0, 0, 1, 0, 0, 0},
//                {0, 0, 0, 0, 0, 1},
//                {0, 0, 0, 0, 1, 0}
//        };

//        final int[][] conflictBetweenClasses = new int[][] {
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//        };

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            return;
        }

        // Khởi tạo biến
        MPVariable[][] x = new MPVariable[numStudents][numClasses];
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }
//        System.out.println("Number of variables = " + solver.numVariables());

        // Mỗi lớp chỉ được có 1 sinh viên
        for(int j = 0; j < numClasses; j++) {
            MPConstraint c0 = solver.makeConstraint(1, 1, "c0");
            for(int i = 0; i < numStudents; i++) {
                c0.setCoefficient(x[i][j], 1);
            }
        }

        // Mỗi sinh viên được nhiều nhất maxClassesPerStudent lớp
        for(int i = 0; i < numStudents; i++) {
            MPConstraint c1 = solver.makeConstraint(0, maxClassesPerStudent, "c1");
            for(int j = 0; j < numClasses; j++) {
                c1.setCoefficient(x[i][j], 1);
            }
        }


        // Conflict về mặt thời gian các lớp
//        for(int i = 0; i < numClasses; i++) {
//            for(int j = i + 1; j < numClasses; j++) {
//                if(conflictBetweenClasses[i][j] == 1) {
//                    for(int k = 0; k < numStudents; k++) {
//                        MPConstraint c2 = solver.makeConstraint(0, 1, "c2");
//                        c2.setCoefficient(x[k][i], 1);
//                        c2.setCoefficient(x[k][j], 1);
//                    }
//                }
//            }
//        }

//        System.out.println("Number of constraints = " + solver.numConstraints());

        // Hàm max
        MPObjective objective = solver.objective();
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                objective.setCoefficient(x[i][j], request[i][j]);
            }
        }
        objective.setMaximization();

        final MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Objective value = " + objective.value());
            maxClassesGotAssign = (int) objective.value();
            System.out.println("maxClassesGotAssign: " + maxClassesGotAssign);
            for(int i = 0; i < numStudents; i++) {
                for(int j = 0; j < numClasses; j++) {
//                    System.out.println("x[" + i + "][" + j + "] = " + x[i][j].solutionValue());
                }
            }
            System.out.println();
            System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
            System.out.println("Problem solved in " + solver.iterations() + " iterations");
            System.out.println("Problem solved in " + solver.nodes() + " branch-and-bound nodes");

            // PHASE 2

            solver.objective().delete();

            // Số lớp có sinh viên phải bằng với phase 1
            MPConstraint c3 = solver.makeConstraint(maxClassesGotAssign, maxClassesGotAssign, "c3");
            for(int i = 0; i < numStudents; i++) {
                for(int j = 0; j < numClasses; j++) {
                    c3.setCoefficient(x[i][j], request[i][j]);
                }
            }

            // Xóa constraint liên quan đến giới hạn lớp mỗi sinh viên
            for(int i = numClasses; i < numStudents + numClasses; i++) {
                solver.constraint(i).delete();
            }

            // Min classPerStudent
            MPObjective objective2 = solver.objective();
            MPVariable classPerStudent = solver.makeIntVar(0, maxClassesPerStudent, "classPerStudent");
            objective2.setCoefficient(classPerStudent, 1);
            objective2.setMinimization();

            // Ràng buộc số lớp mỗi sinh viên đăng ký
            for(int i = 0; i < numStudents; i++) {

                // Y = classPerStudent
                // 0 <= sum(x[i][j] <= M - M + Y
                // 0 <= sum(x[i][j] + M - Y <= M
                // -M <= sum(x[i][j]) - Y <= 0

                MPConstraint c4 = solver.makeConstraint(maxClassesPerStudent * -1, 0, "c4");
                for(int j = 0; j < numClasses; j++) {
                    c4.setCoefficient(x[i][j], 1);
                }
                c4.setCoefficient(classPerStudent, -1);
            }

            final MPSolver.ResultStatus resultStatus2 = solver.solve();

            if (resultStatus2 == MPSolver.ResultStatus.OPTIMAL) {
                System.out.println();
                System.out.println("PHASE 2");
                System.out.println("Objective2 value = " + classPerStudent.solutionValue());
                for(int i = 0; i < numStudents; i++) {
                    for(int j = 0; j < numClasses; j++) {
//                        System.out.println("x[" + i + "][" + j + "] = " + x[i][j].solutionValue());
                    }
                }
                System.out.println();
                System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
                System.out.println("Problem solved in " + solver.iterations() + " iterations");
                System.out.println("Problem solved in " + solver.nodes() + " branch-and-bound nodes");
            } else {
                System.err.println("The problem 2 does not have an optimal solution.");
            }


        } else {
            System.err.println("The problem does not have an optimal solution.");
        }

    }
}
