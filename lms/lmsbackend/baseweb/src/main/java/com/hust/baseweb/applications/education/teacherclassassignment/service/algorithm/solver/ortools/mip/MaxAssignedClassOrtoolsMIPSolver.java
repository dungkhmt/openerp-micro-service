package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.MaxAssignedClassBaseSolver;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

/**
 * OK. 500 lop chi phan duoc 460 --> co gang phan nhieu lop nhat co the dươc. Sau do giu ham muc tieu nay va chay them 3 ham toi uu ben duoi
 */
@Log4j2
public class MaxAssignedClassOrtoolsMIPSolver extends MaxAssignedClassBaseSolver {

//    private final int[][] priority;

    // intermediate data structure
//    private double totalHourClass;
//    private int INF;
//    private int maxP;// max value of priority
//    private int minP;// min value of priority

    // parameters
//    private int alpha;
//    private int[] beta;
//    private long timeLimit = 10000;

    // MIP modelling
    MPVariable[][] x; // x[j,i] = 1 indicates that class i is assigned to teacher j, = 0 otherwise
    //private MPVariable[][] y; // Y[i,k] = 1 indicates that class i is assigned to a teacher with priority k
    MPVariable[] z; // Z[i] = 1 indicates that class i is assigned, = 0 otherwise
    //    private MPVariable[] u; // u[j] is the hour_load of teacher j
    MPSolver solver;
    //    private MPVariable obj;
    private MPVariable objectiveMaxNumClassAssigned; // redundant variable
//    private MPVariable objectiveMaxPriority;

    public MaxAssignedClassOrtoolsMIPSolver(MapDataInput input) {
        super(input);
    }

    /**
     * OK
     *
     * @return
     */
//    public double getObjectivePriority() {
//        return objectiveMaxPriority.solutionValue();
//    }

    /**
     * OK
     *
     * @return
     */
    public double getObjectiveValue() {
        return solver.objective().value();
    }

    /**
     * Con xem xet
     */
//    private void initDatastructures() {
//        totalHourClass = 0;
//        for (int i = 0; i < n; i++) {
//            totalHourClass += hourClass[i];
//        }
//
//        INF = (int) totalHourClass + 1;
//        maxP = 0;
//        minP = Integer.MAX_VALUE;
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                if (priority[i][j] < Integer.MAX_VALUE) {
//                    if (priority[i][j] > maxP) {
//                        maxP = priority[i][j];
//                    }
//                    if (priority[i][j] < minP) {
//                        minP = priority[i][j];
//                    }
//                }
//            }
//        }

    // init parameters, chua hieu anpha va beta
//        alpha = 10000;
//        beta = new int[maxP + 1];
//        for (int k = minP; k <= maxP; k++) {
//            beta[k] = 1;
//        }
//        //beta[minP] = 10000; beta[minP+1] = 100;
//        for (int k = maxP - 1; k >= minP; k--) {
//            if (beta[k + 1] < 10000000) {
//                beta[k] = beta[k + 1] * 10; // gia tri uu tien cang thap, muc do uu tien cang cao
//            } else {
//                beta[k] = beta[k + 1];
//            }
//        }
//    }

    /**
     * Con xem xet. Access modifier OK
     */
    void createSolverAndVariables() {
        Loader.loadNativeLibraries();
        // Create the linear solver with the SCIP backend.
        solver = MPSolver.createSolver("SCIP");
        if (null == solver) {
            log.error("Could not create solver SCIP");
            return;
        }

        log.info("createSolverAndVariables, n = " + n + " m = " + m);

        x = new MPVariable[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!D[i].contains(j)) { // teacher j cannot be assigned to class i
                    x[j][i] = solver.makeIntVar(0, 0, "x[" + j + "," + i + "]");
//                    log.info(name() + "::solve, FORCE var x[" + j + "," + i + "] = 0");
                } else {
                    x[j][i] = solver.makeIntVar(0, 1, "x[" + j + "," + i + "]");
                }
            }
        }

        // Cac lop ngoai preAssignments, D[i].size() == 1 --> z[i] = {0, 1]
        z = new MPVariable[n];
        for (int i = 0; i < n; i++) {
            z[i] = solver.makeIntVar(0, 1, "z[" + i + "]");
        }

        // Toi uu khoi tao z: lop i trong preAssignments --> z[i] = 1
        if (null != preAssignments && 0 != preAssignments.length) {
            for (int[] assignment : preAssignments) {
                int i = assignment[0]; // class index
                z[i] = solver.makeIntVar(1, 1, "z[" + i + "]");
            }
        }

        // de lam gi?
//        obj = solver.makeIntVar(0, totalHourClass, "minOfMaxLoad");

//        int sumBeta = 0;
//        for (int p = minP; p <= maxP; p++) {
//            sumBeta += beta[p];
//        }
//        sumBeta *= n;

//        objectiveMaxPriority = solver.makeIntVar(0, sumBeta, "objectiveMaxPriority");

        // Will be redundant in phase 2 but still declare
        objectiveMaxNumClassAssigned = solver.makeIntVar(0, n, "objectiveMaxNumClassAssigned");

        // create variables y
//        y = new MPVariable[n][maxP + 1];
//        for (int i = 0; i < n; i++) {
//            for (int j = minP; j <= maxP; j++) {
//                y[i][j] = solver.makeIntVar(0, 1, "y[" + i + "," + j + "]");
//            }
//        }

        // create variable u
        //u = new MPVariable[n];
        //for(int i = 0; i < n; i++)
        //    u[i] = solver.makeIntVar(0,totalHourClass,"u[" + i + "]");
    }

    /**
     * OK. Khong can rang buoc nay, createConstraintChannelXZ() da rang buoc dieu nay roi
     */
//    private void createConstraintAtMostOneTeacherIsAssignedToEachClass() {
//        // Todo: avoid constraints on preassigned classes: use preAssignments
//        // each class is assigned to at most one teacher
//        for (int i = 0; i < n; i++) {
//            //MPConstraint c = solver.makeConstraint(1, 1); // bor
//            MPConstraint c = solver.makeConstraint(0, 1);
//
//            for (int j = 0; j < m; j++) {
//                c.setCoefficient(x[j][i], 1);
//            }
//        }
//    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintsPreAssignment() {
        if (null == preAssignments || 0 == preAssignments.length) {
            return;
        }

        for (int[] assignment : preAssignments) {
            int i = assignment[0]; // class index
            int j = assignment[1]; // teacher index

            createInstantiationConstraint(j, i);
        }
    }

    /**
     * OK. Access modifier OK
     *
     * @param j teacher
     * @param i class
     */
    private void createInstantiationConstraint(int j, int i) {
        // Create constraint saying that teacher j is assigned to class i: x[j][i] = 1
        MPConstraint c = solver.makeConstraint(1, 1);
        c.setCoefficient(x[j][i], 1);
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintMaxHourLoadTeacher() {
        // Hour load of each teacher cannot exceed the maximum allowed valueOf
        for (int j = 0; j < m; j++) {
            MPConstraint c = solver.makeConstraint(0, maxHourTeacher[j]);

            for (int i = 0; i < n; i++) {
                c.setCoefficient(x[j][i], hourClass[i]);
            }
        }
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintConflictClasses() {
        // Conflict constraint
        for (int j = 0; j < m; j++) {
            for (int c1 = 0; c1 < n; c1++) {
                for (int c2 = c1 + 1; c2 < n; c2++) {
                    if (conflict[c1][c2] && D[c1].contains(j) && D[c2].contains(j)) {
                        MPConstraint c = solver.makeConstraint(0, 1);
                        c.setCoefficient(x[j][c1], 1);
                        c.setCoefficient(x[j][c2], 1);
                    }
                }
            }
        }
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintChannelXZ() {
        // Constraint between x and z: {0, 1} = z[i] = sum_{j=1..m} x[j,i]
        // Also constraint each class is assigned to at most one teacher
        for (int i = 0; i < n; i++) {
            MPConstraint c = solver.makeConstraint(0, 0);
            c.setCoefficient(z[i], -1);
            for (int j = 0; j < m; j++) {
                c.setCoefficient(x[j][i], 1);
            }
        }
    }

//    private void createConstraintOnY() {
//        for (int i = 0; i < n; i++) {
//            MPConstraint c = solver.makeConstraint(0, 1);
//            for (int p = minP; p <= maxP; p++) {
//                c.setCoefficient(y[i][p], 1);
//            }
//        }
//    }
//
//    private void createConstraintChannelXY() {
//        // constraint between x and y
//        for (int p = minP; p <= maxP; p++) {
//            for (int i = 0; i < n; i++) {
//                MPConstraint c = solver.makeConstraint(0, 0);
//                c.setCoefficient(y[i][p], 1);
//                for (int j = 0; j < m; j++) {
//                    if (priority[i][j] == p) { // y[i,p] = x[j,i]
//                        //MPConstraint c = solver.makeConstraint(0,0);
//                        //c.setCoefficient(y[i][p],1);
//                        c.setCoefficient(x[j][i], -1);
//                        //if(i == 18 && j == 67){
//                        //System.out.println(name() + "::solve, constraintXY, x[" + j + "," + i + "] = y[" + i + "," + p + "]");
//                        //}
//                    }
//                    /*
//                    else{
//                        MPConstraint c = solver.makeConstraint(0,0);
//                        c.setCoefficient(y[i][p],1);
//                    }
//                    */
//                }
//            }
//        }
//
//    }

//    private void createConstraintObj() {
//        // constraint on the objective function
//        for (int j = 0; j < m; j++) {
//            MPConstraint c = solver.makeConstraint(-INF, 0);
//            for (int i = 0; i < n; i++) {
//                c.setCoefficient(x[j][i], hourClass[i]);
//            }
//            c.setCoefficient(obj, -1);
//        }
//    }
//
//    private void createMaxPriorityObjectiveConstraint() {
//        MPConstraint c = solver.makeConstraint(0, 0);
//        for (int p = minP; p <= maxP; p++) {
//            for (int i = 0; i < n; i++) {
//                c.setCoefficient(y[i][p], beta[p]);
//            }
//        }
//        c.setCoefficient(objectiveMaxPriority, -1);
//
//    }
//
//    private void createConstraintChannelYZ() {
//        for (int i = 0; i < n; i++) {
//            MPConstraint c = solver.makeConstraint(0, INF);
//            for (int p = minP; p <= maxP; p++) {
//                c.setCoefficient(y[i][p], -1);
//            }
//            c.setCoefficient(z[i], 1);
//        }
//    }

    /**
     * OK but redundant. Access modifier OK
     */
    private void createMaxNumAssignedClassConstraint() {
        MPConstraint c = solver.makeConstraint(0, 0);

        for (int i = 0; i < n; i++) {
            c.setCoefficient(z[i], 1);
        }

        c.setCoefficient(objectiveMaxNumClassAssigned, -1); // objectiveMaxNumClassAssigned = sum(z)
    }

    /**
     * Con xem xet. Access modifier OK
     */
    void createConstraints() {
//        createConstraintAtMostOneTeacherIsAssignedToEachClass(); // OK but redundant
        createConstraintsPreAssignment(); // OK
        createConstraintMaxHourLoadTeacher(); // OK
        createConstraintConflictClasses(); // OK
        //createConstraintChannelXY(); // ?
        createConstraintChannelXZ(); // OK
        //createConstraintChannelYZ(); // ?
        //createConstraintOnY();

        //createConstraintObj();
        createMaxNumAssignedClassConstraint(); // OK but redundant
        //createMaxPriorityObjectiveConstraint(); // ?
    }

    /**
     * OK. Access modifier OK
     */
    void createObjective() {
        MPObjective objective = solver.objective();
        objective.setCoefficient(objectiveMaxNumClassAssigned, 1);
        objective.setMaximization();
    }

    /**
     * Only maximize the number of class assigned. OK. Access modifier OK
     *
     * @return
     */
    private boolean solvePhase1() {
//        initDatastructures();
        createSolverAndVariables();
        createConstraints();
        createObjective();

        // Solves.
        log.info("Model created, start solving...");
        solver.setTimeLimit(900 * 1000);
        final MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            log.info("Solution status = OPTIMAL");

            // Analyse solution.
            extractSolution();
            return true;
        }

        log.info("solved, NOT FOUND OPTIMAL??");
        return false;
    }

    void extractSolution() {
        assignments = new int[n];
        Arrays.fill(assignments, -1);

//            printSolutionVariables();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (x[j][i].solutionValue() > 0.9) {
//                        log.info("solver, x[" + i + "," + j + "] = " + x[j][i].solutionValue());
                    assignments[i] = j;
                    break;
                }
            }
        }

        for (int i = 0; i < n; i++) {
//                log.info("solver z[" + i + "] = " + z[i].solutionValue());
            if (z[i].solutionValue() < 0.1) {
                notAssignedClasses.add(i);
            }
        }
    }

    /**
     * Chua xem xet. Access modifier OK
     *
     * @return
     */
    public boolean solve() {
        // ??
//        if (true) {
        return solvePhase1();
//        }

//        initDatastructures();
//        createSolverAndVariables();
//        createConstraints();
//        createObjective();
//
//        // Solves.
//        final MPSolver.ResultStatus resultStatus = solver.solve();
//
//        // Analyse solution.
//        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
//            assignment = new int[n];
//            for (int i = 0; i < n; i++) {
//                assignment[i] = -1;
//            }
//            System.out.println("solve, n = " + n + " m = " + m);
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < m; j++) {
//                    System.out.println("solver, x[" + i + "," + j + "] = " + x[j][i].solutionValue());
//                    if (x[j][i].solutionValue() > 0) {
//                        assignment[i] = j;
//                    }
//                }
//            }
//            return true;
//        }
//        return false;
    }

    /**
     * Con xem xet. Access modifier OK
     */
    void printSolutionVariables() {
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                if (x[j][i].solutionValue() > 0.9) {
                    System.out.println("x[" + j + "," + i + "] = " + x[j][i].solutionValue());
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (z[i].solutionValue() > 0.9) {
                System.out.println("z[" + i + "]= " + z[i].solutionValue());
            }
        }

        // chua xem xet
//        for (int i = 0; i < n; i++) {
//            for (int p = minP; p <= maxP; p++) {
//                if (y[i][p].solutionValue() > 0) {
//                    System.out.println("y[" + i + "," + p + "] = " + y[i][p].solutionValue());
//                }
//            }
//        }
    }
}
