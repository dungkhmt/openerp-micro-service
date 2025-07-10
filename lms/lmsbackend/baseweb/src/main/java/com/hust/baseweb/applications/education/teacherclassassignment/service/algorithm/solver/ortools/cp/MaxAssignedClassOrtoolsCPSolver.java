package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.cp;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 500 lop chi phan duoc 460 --> co gang phan nhieu lop nhat co the dươc. Sau do giu ham muc tieu nay va chay them 3 ham toi uu ben duoi
 */
@Log4j2
public class MaxAssignedClassOrtoolsCPSolver {

    // Input
    final int n;// number of classes

    final int m;// number of teachers

    final HashSet<Integer>[] D; // D[i] is the set of teachers that can be assigned to class i

    final boolean[][] conflict;

    final long[] hourClass;

    final long[] maxHourTeacher;

    final int[][] preAssignments;

    long timeLimit = 900;

    //
    IntVar[][] x; // x[j,i] = 1 indicates that class i is assigned to teacher j, = 0 otherwise
    IntVar[] z; // Z[i] = 1 indicates that class i is assigned, = 0 otherwise
    CpModel model;
    CpSolver solver;

    // Data structures for solution
    int[] assignment; // assignment[i] is the teacher assigned to class i
    final HashSet<Integer> notAssignedClasses = new HashSet<>();

    public MaxAssignedClassOrtoolsCPSolver(MapDataInput input) {
        this.n = input.getN();
        this.m = input.getM();
        this.D = input.getD();
        this.conflict = input.getConflict();
        this.preAssignments = input.getPreAssignment();

        this.hourClass = new long[n];
        this.maxHourTeacher = new long[m];

        double[] maxHourTeacher = input.getMaxHourTeacher();
        for (int j = 0; j < m; j++) {
            this.maxHourTeacher[j] = Double.valueOf(maxHourTeacher[j]).longValue();
        }

        double[] hourClass = input.getHourClass();
        for (int i = 0; i < n; i++) {
            this.hourClass[i] = Double.valueOf(hourClass[i]).longValue();
        }
    }

    /**
     * OK
     *
     * @param timeLimit
     */
    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * OK
     *
     * @return
     */
    public HashSet<Integer> getNotAssignedClasses() {
        return notAssignedClasses;
    }

    /**
     * OK
     *
     * @return
     */
    public int[] getAssignment() {
        return assignment;
    }

    /**
     * OK
     *
     * @return
     */
    public double getObjectiveValue() {
        return solver.objectiveValue();
    }

    /**
     * Con xem xet. Access modifier OK
     */
    void createSolverAndVariables() {
        Loader.loadNativeLibraries();
        // Create the model.
        model = new CpModel();

        log.info("createSolverAndVariables, n = " + n + " m = " + m);

        x = new IntVar[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!D[i].contains(j)) { // teacher j cannot be assigned to class i
                    x[j][i] = model.newIntVar(0, 0, "x[" + j + "," + i + "]");
//                    log.info(name() + "::solve, FORCE var x[" + j + "," + i + "] = 0");
                } else {
                    x[j][i] = model.newIntVar(0, 1, "x[" + j + "," + i + "]");
                }
            }
        }

        // Todo: xem xet viec toi uu khoi tao z: lop i trong preAssignments --> z[i] = 1
        // Cac lop ngoai preAssignments, D[i].size() == 1 --> z[i] = {0, 1}
        z = new IntVar[n];
        for (int i = 0; i < n; i++) {
            z[i] = model.newIntVar(0, 1, "z[" + i + "]");
        }
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintsPreAssignment() {
        if (null == preAssignments || 0 == preAssignments.length) {
            return;
        }

        for (int[] assignment : preAssignments) {
            int i = assignment[0]; // class
            int j = assignment[1]; // teacher

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
        // create constraint saying that teacher j is assigned to class i: x[j][i] = 1
        model.addEquality(x[j][i], 1);
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintMaxHourLoadTeacher() {
//        IntVar[][] coefficient = new IntVar[n][m];
//        for (int i = 0; i < m; i++) {
//            for (int j = 0; j < n; j++) {
//                if (D[j].contains(i)) {
//                    coefficient[i][j] = model.newIntVar(0, 1, "coefficient[" + i + ", " + j + "]");
//
//                    model.addEquality(coefficient[i][j], 1).onlyEnforceIf(u[j]);
//                    model.addEquality(coefficient[i][j], 0).onlyEnforceIf(u[j].not());
//                } else {
//                    coefficient[i][j] = model.newIntVar(0, 0, "coefficient[" + i + ", " + j + "]");
//                }
//            }
//        }

        // Hour load of each teacher cannot exceed the maximum allowed valueOf
        for (int j = 0; j < m; j++) {
            model.addLessOrEqual(LinearExpr.weightedSum(x[j], hourClass), maxHourTeacher[j]);
        }
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintConflictClasses() {
        // conflict constraint
        for (int j = 0; j < m; j++) {
            for (int c1 = 0; c1 < n; c1++) {
                for (int c2 = c1 + 1; c2 < n; c2++) {
                    if (conflict[c1][c2] && D[c1].contains(j) && D[c2].contains(j)) {
                        model.addLessThan(LinearExpr.sum(new IntVar[]{x[j][c1], x[j][c2]}), 2);
                    }
                }
            }
        }
    }

    /**
     * OK. Access modifier OK
     */
    void createConstraintChannelXZ() {
        // if (x[i] >= 0) then z[i] = 1 else z[i] = 0
//        for (int i = 0; i < n; i++) {
//            model.addGreaterOrEqual(x[i], 0).onlyEnforceIf(u[i]);
//            model.addLessOrEqual(x[i], -1).onlyEnforceIf(u[i].not());
//
//            model.addEquality(z[i], 1).onlyEnforceIf(u[i]);
//            model.addEquality(z[i], 0).onlyEnforceIf(u[i].not());
//        }

        // constraint between x and z: {0, 1} = z[i] = sum_{j=1..m} x[j,i]
        // also constraint each class is assigned to at most one teacher
        for (int i = 0; i < n; i++) {
            IntVar[] column = new IntVar[m];
            for (int j = 0; j < m; j++) {
                column[j] = x[j][i];
            }
            model.addEquality(LinearExpr.sum(column), z[i]);
        }
    }

    /**
     * Con xem xet. Access modifier OK
     */
    void createConstraints() {
        createConstraintsPreAssignment(); // OK
        createConstraintMaxHourLoadTeacher(); // OK
        createConstraintConflictClasses(); // OK
        createConstraintChannelXZ(); // OK
    }

    /**
     * OK. Access modifier OK
     */
    void createObjective() {
        model.maximize(LinearExpr.sum(z));
    }

    /**
     * Only maximize the number of class assigned. OK. Access modifier OK
     *
     * @return
     */
    public boolean solve() {
        createSolverAndVariables();
        createConstraints();
        createObjective();

        // Solves.
        log.info("Model created, start solving with time limit = " + timeLimit + "s");

        // Create a solver and solve the model.
        solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timeLimit);
        CpSolverStatus status = solver.solve(model);

        // Statistics.
        System.out.println("Statistics");
        System.out.printf("  conflicts: %d%n", solver.numConflicts());
        System.out.printf("  branches : %d%n", solver.numBranches());
        System.out.printf("  wall time: %f s%n", solver.wallTime());

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.printf("Maximum of objective function: %f%n", solver.objectiveValue());

            // Analyse solution.
            extractSolution();
            return true;
        } else {
            System.out.println("No solution found.");
        }

        return false;
    }

    void extractSolution() {
        assignment = new int[n];
        Arrays.fill(assignment, -1);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (solver.value(x[j][i]) > 0.9) {
//                        log.info("solver, x[" + i + "," + j + "] = " + solver.value(x[j][i]));
                    assignment[i] = j;
                    break;
                }
            }
        }

        for (int i = 0; i < n; i++) {
//                log.info("solver z[" + i + "] = " + solver.value(z[i]));
            if (solver.value(z[i]) < 0.1) {
                notAssignedClasses.add(i);
            }
        }
    }

    /**
     * Con xem xet. Access modifier OK
     */
    void printSolutionVariables() {
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                if (solver.value(x[j][i]) > 0.9) { // Todo: consider change to 0.9 instead of 0
                    System.out.println("x[" + j + "," + i + "] = " + solver.value(x[j][i]));
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (solver.value(z[i]) > 0.9) {
                System.out.println("z[" + i + "]= " + solver.value(z[i]));
            }
        }
    }
}
