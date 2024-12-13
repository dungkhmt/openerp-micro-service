package openerp.openerpresourceserver.tarecruitment.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class OrToolsTaRecruitment {

    int numClasses;
    int numStudents;
    final int maxClassesPerStudent = 8;
    int maxDurations = 120;
    int maxClassesGotAssign = 0;
    int[] durations;// number of session slots
    int[][] request;
    int[][] conflictBetweenClasses;
    int[][] result;


    public OrToolsTaRecruitment(int numClasses, int numStudents, int[][] request, int[][] conflictBetweenClasses, int[] durations) {
        this.numClasses = numClasses;
        this.numStudents = numStudents;
        this.request = request;
        this.conflictBetweenClasses = conflictBetweenClasses;
        this.durations = durations;
        result = new int[numStudents][numClasses];
    }

    public int[][] solving() {
        Loader.loadNativeLibraries();

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            throw new RuntimeException("Cant create solver");
        }

        // Khởi tạo biến
        MPVariable[][] x = new MPVariable[numStudents][numClasses];
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }

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

        // Mỗi sinh viên được nhiều nhất maxDurations tiet
        for(int i = 0; i < numStudents; i++) {
            MPConstraint c1 = solver.makeConstraint(0, maxDurations, "c1");
            for(int j = 0; j < numClasses; j++) {
                c1.setCoefficient(x[i][j], durations[j]);
            }
        }


        // Conflict về mặt thời gian các lớp
        for(int i = 0; i < numClasses; i++) {
            for(int j = i + 1; j < numClasses; j++) {
                if(conflictBetweenClasses[i][j] == 1) {
                    for(int k = 0; k < numStudents; k++) {
                        MPConstraint c2 = solver.makeConstraint(0, 1, "c2");
                        c2.setCoefficient(x[k][i], 1);
                        c2.setCoefficient(x[k][j], 1);
                    }
                }
            }
        }

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

            // Gán số lớp tối đa có thể được assign
            maxClassesGotAssign = (int) objective.value();

            //////////////////////////////////////////// PHASE 2 ///////////////////////////////////////
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
                for(int i = 0; i < numStudents; i++) {
                    for(int j = 0; j < numClasses; j++) {
                        result[i][j] = (int) x[i][j].solutionValue();
                    }
                }
            }

            else {
                throw new RuntimeException("Phase 2 don't have solution");
            }

        }

        else {
            throw new RuntimeException("Phase 1 don't have solution");
        }

        return result;
    }

    // by PQD 2024-09-03
    public int solvePhase1MaximizeNbrClassAssigned(){
        Loader.loadNativeLibraries();

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            throw new RuntimeException("Cant create solver");
        }

        // Khởi tạo biến
        MPVariable[][] x = new MPVariable[numStudents][numClasses];
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }

        // Mỗi lớp chỉ được có 1 sinh viên
        for(int j = 0; j < numClasses; j++) {
            MPConstraint c0 = solver.makeConstraint(0, 1, "c0");
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

        // Mỗi sinh viên được nhiều nhất maxDurations tiet
        for(int i = 0; i < numStudents; i++) {
            MPConstraint c1 = solver.makeConstraint(0, maxDurations, "c1");
            for(int j = 0; j < numClasses; j++) {
                c1.setCoefficient(x[i][j], durations[j]);
            }
        }

        // Conflict về mặt thời gian các lớp
        for(int i = 0; i < numClasses; i++) {
            for(int j = i + 1; j < numClasses; j++) {
                if(conflictBetweenClasses[i][j] == 1) {
                    for(int k = 0; k < numStudents; k++) {
                        MPConstraint c2 = solver.makeConstraint(0, 1, "c2");
                        c2.setCoefficient(x[k][i], 1);
                        c2.setCoefficient(x[k][j], 1);
                    }
                }
            }
        }

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

            // Gán số lớp tối đa có thể được assign
            int res = (int) objective.value();
            System.out.println("solvePhase1MaximizeNbrClassAssigned, res = " + res);
            return res; // number of class assigned
        }else{
            return -1; // not found
        }
    }
    public int[][] solvePhase2MaxNbStudentAssigned(int nbC){
        Loader.loadNativeLibraries();

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            throw new RuntimeException("Cant create solver");
        }

        // Khởi tạo biến
        MPVariable[][] x = new MPVariable[numStudents][numClasses];
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }
        MPVariable[] y = new MPVariable[numStudents];
        for(int i = 0; i < numStudents; i++)
            y[i] = solver.makeIntVar(0,1,"");// y[i] = 1 means that student i is assigned

        // Mỗi lớp chỉ được có 1 sinh viên
        for(int j = 0; j < numClasses; j++) {
            MPConstraint c0 = solver.makeConstraint(0, 1, "c0");
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

        // Mỗi sinh viên được nhiều nhất maxDurations tiet
        for(int i = 0; i < numStudents; i++) {
            MPConstraint c1 = solver.makeConstraint(0, maxDurations, "c1");
            for(int j = 0; j < numClasses; j++) {
                c1.setCoefficient(x[i][j], durations[j]);
            }
        }

        // Conflict về mặt thời gian các lớp
        for(int i = 0; i < numClasses; i++) {
            for(int j = i + 1; j < numClasses; j++) {
                if(conflictBetweenClasses[i][j] == 1) {
                    for(int k = 0; k < numStudents; k++) {
                        MPConstraint c2 = solver.makeConstraint(0, 1, "c2");
                        c2.setCoefficient(x[k][i], 1);
                        c2.setCoefficient(x[k][j], 1);
                    }
                }
            }
        }
        // constraint over the number of classes assigned
        MPConstraint c2= solver.makeConstraint(nbC,nbC,"c2");
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                c2.setCoefficient(x[i][j], request[i][j]);
            }
        }


        double inf = solver.infinity();
        for(int i = 0; i < numStudents; i++) {
            MPConstraint c3 = solver.makeConstraint(-(int)inf, 0);
            for(int j = 0; j < numClasses; j++){
                c3.setCoefficient(x[i][j],1);
            }
            c3.setCoefficient(y[i],-numClasses);

            MPConstraint c4 = solver.makeConstraint(0,(int)inf);
            for(int j = 0; j < numClasses; j++){
                c4.setCoefficient(x[i][j],1);
            }
            c4.setCoefficient(y[i],-1);
        }


        // Hàm max
        MPObjective objective = solver.objective();
        for(int i = 0; i < numStudents; i++) {
            objective.setCoefficient(y[i],1);
            //for(int j = 0; j < numClasses; j++) {
            //    objective.setCoefficient(x[i][j], request[i][j]);
           // }
        }
        objective.setMaximization();

        final MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Phase 2 finished, objective = " + (int)objective.value());
            for(int i = 0; i < numStudents; i++) {
                for(int j = 0; j < numClasses; j++) {
                    result[i][j] = (int) x[i][j].solutionValue();
                }
            }
            return result;
        }
        System.out.println("Phase 2 finished, no solution fonud");
        return null;
    }
    public int[][] newSolve() {
        int nbClassAssigned = solvePhase1MaximizeNbrClassAssigned();
        System.out.println("Phase 1 FINISHED nb classes assigned = " +  nbClassAssigned);
        result = solvePhase2MaxNbStudentAssigned(nbClassAssigned);
        return result;
        /*
        if(true) return null;
        Loader.loadNativeLibraries();

        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            throw new RuntimeException("Cant create solver");
        }

        // Khởi tạo biến
        MPVariable[][] x = new MPVariable[numStudents][numClasses];
        for(int i = 0; i < numStudents; i++) {
            for(int j = 0; j < numClasses; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }

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
        for(int i = 0; i < numClasses; i++) {
            for(int j = i + 1; j < numClasses; j++) {
                if(conflictBetweenClasses[i][j] == 1) {
                    for(int k = 0; k < numStudents; k++) {
                        MPConstraint c2 = solver.makeConstraint(0, 1, "c2");
                        c2.setCoefficient(x[k][i], 1);
                        c2.setCoefficient(x[k][j], 1);
                    }
                }
            }
        }

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

            // Gán số lớp tối đa có thể được assign
            maxClassesGotAssign = (int) objective.value();

            //////////////////////////////////////////// PHASE 2 ///////////////////////////////////////
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
                for(int i = 0; i < numStudents; i++) {
                    for(int j = 0; j < numClasses; j++) {
                        result[i][j] = (int) x[i][j].solutionValue();
                    }
                }
            }

            else {
                throw new RuntimeException("Phase 2 don't have solution");
            }

        }

        else {
            throw new RuntimeException("Phase 1 don't have solution");
        }

        return result;

         */
    }

}
