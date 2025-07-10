//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.cp;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import ilog.concert.IloException;
//import ilog.concert.IloIntVar;
//import ilog.concert.IloLinearIntExpr;
//import lombok.extern.log4j.Log4j2;
//
//import java.util.Arrays;
//import java.util.HashSet;
//
//import ilog.cp.*;
//
//
//@Log4j2
//public class MaxAssignedClassCplexCPSolver {
//
//    // Input
//    final int n; // number of classes
//
//    final int m; // number of teachers
//
//    final HashSet<Integer>[] D; // D[i] is the set of teachers that can be assigned to class i
//
//    final boolean[][] conflict;
//
//    final int[] hourClass;
//
//    final int[] maxHourTeacher;
//
//    final int[][] preAssignments;
//
//    private long timeLimit = 10000;
//
//    // MIP modelling
//    IloIntVar[][] x; // x[j,i] = 1 indicates that class i is assigned to teacher j, = 0 otherwise
//    IloIntVar[] z; // Z[i] = 1 indicates that class i is assigned, = 0 otherwise
//    IloCP cp;
//
//    // Data structures for solution
//    int[] assignment; // assignment[i] is the teacher assigned to class i
//    final HashSet<Integer> notAssignedClasses = new HashSet<>();
//
//    public MaxAssignedClassCplexCPSolver(MapDataInput input) {
//        this.n = input.getN();
//        this.m = input.getM();
//        this.D = input.getD();
//        this.conflict = input.getConflict();
//        this.preAssignments = input.getPreAssignment();
//
//        this.hourClass = new int[n];
//        this.maxHourTeacher = new int[m];
//
//        double[] maxHourTeacher = input.getMaxHourTeacher();
//        for (int j = 0; j < m; j++) {
//            this.maxHourTeacher[j] = Double.valueOf(maxHourTeacher[j]).intValue();
//        }
//
//        double[] hourClass = input.getHourClass();
//        for (int i = 0; i < n; i++) {
//            this.hourClass[i] = Double.valueOf(hourClass[i]).intValue();
//        }
//    }
//
//    /**
//     * OK
//     *
//     * @param timeLimit
//     */
//    public void setTimeLimit(long timeLimit) {
//        this.timeLimit = timeLimit;
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    public HashSet<Integer> getNotAssignedClasses() {
//        return notAssignedClasses;
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    public int[] getAssignment() {
//        return assignment;
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    public double getObjectiveValue() {
//        try {
//            return cp.getObjValue();
//        } catch (IloException e) {
//            e.printStackTrace();
//        }
//
//        return -1;
//    }
//
//    /**
//     * Con xem xet. Access modifier OK
//     */
//    void createSolverAndVariables() throws IloException {
//        cp = new IloCP();
//
//        log.info("createSolverAndVariables, n = " + n + " m = " + m);
//
//        x = new IloIntVar[m][n];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                if (!D[i].contains(j)) { // teacher j cannot be assigned to class i
//                    x[j][i] = cp.intVar(0, 0, "x[" + j + "," + i + "]");
////                    log.info(name() + "::solve, FORCE var x[" + j + "," + i + "] = 0");
//                } else {
//                    x[j][i] = cp.intVar(0, 1, "x[" + j + "," + i + "]");
//                }
//            }
//        }
//
//        // Todo: xem xet viec toi uu khoi tao z: lop i trong preAssignments --> z[i] = 1
//        // Cac lop ngoai preAssignments, D[i].size() == 1 --> z[i] = {0, 1]
//        z = new IloIntVar[n];
//        for (int i = 0; i < n; i++) {
//            z[i] = cp.intVar(0, 1, "z[" + i + "]");
//        }
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    private void createConstraintsPreAssignment() throws IloException {
//        if (null == preAssignments || 0 == preAssignments.length) {
//            return;
//        }
//
//        for (int[] assignment : preAssignments) {
//            int i = assignment[0]; // class
//            int j = assignment[1]; // teacher
//
//            createInstantiationConstraint(j, i);
//        }
//    }
//
//    /**
//     * OK. Access modifier OK
//     *
//     * @param j teacher
//     * @param i class
//     */
//    private void createInstantiationConstraint(int j, int i) throws IloException {
//        // create constraint saying that teacher j is assigned to class i: x[j][i] = 1
//        cp.addEq(x[j][i], 1);
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    private void createConstraintMaxHourLoadTeacher() throws IloException {
//        // Hour load of each teacher cannot exceed the maximum allowed valueOf
//        for (int j = 0; j < m; j++) {
//            cp.addLe(cp.scalProd(x[j], hourClass), maxHourTeacher[j]);
//        }
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    private void createConstraintConflictClasses() throws IloException {
//        // conflict constraint
//        for (int j = 0; j < m; j++) {
//            for (int c1 = 0; c1 < n; c1++) {
//                for (int c2 = c1 + 1; c2 < n; c2++) {
//                    if (conflict[c1][c2] && D[c1].contains(j) && D[c2].contains(j)) {
//                        cp.addLe(cp.sum(cp.prod(1, x[j][c1]), cp.prod(1, x[j][c2])), 1);
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    private void createConstraintChannelXZ() throws IloException {
//        // constraint between x and z: {0, 1} = z[i] = sum_{j=1..m} x[j,i]
//        // also constraint each class is assigned to at most one teacher
//        for (int i = 0; i < n; i++) {
//            IloLinearIntExpr column = cp.linearIntExpr();
//            for (int j = 0; j < m; j++) {
//                column.addTerm(1, x[j][i]);
//            }
//            cp.addEq(column, z[i]);
//        }
//    }
//
//    /**
//     * Con xem xet. Access modifier OK
//     */
//    void createConstraints() throws IloException {
//        createConstraintsPreAssignment(); // OK
//        createConstraintMaxHourLoadTeacher(); // OK
//        createConstraintConflictClasses(); // OK
//        createConstraintChannelXZ(); // OK
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    void createObjective() throws IloException {
//        cp.add(cp.maximize(cp.sum(z)));
//    }
//
//    /**
//     * Only maximize the number of class assigned. OK. Access modifier OK
//     *
//     * @return
//     */
//    public boolean solve() {
//        try {
//            createSolverAndVariables();
//            createConstraints();
//            createObjective();
//
//            // Solves the model and display the solution if one was found.
//            log.info("Model created, start solving");
//            cp.setParameter(IloCP.DoubleParam.TimeLimit, 900);
//            if (cp.solve()) {
//                cp.output().println("Solution status = " + cp.getStatus());
//
//                // Analyse solution.
//                extractSolution();
//
//                return true;
//            }
//        } catch (IloException e) {
//            System.err.println("Concert exception '" + e + "' caught");
//        }
//
//        log.info("solved, NOT FOUND OPTIMAL??");
//        return false;
//    }
//
//    void extractSolution() throws IloException {
//        assignment = new int[n];
//        Arrays.fill(assignment, -1);
//
//        log.info("solved, n = " + n + " m = " + m + ", objective = " + cp.getObjValue());
//
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                if (cp.getValue(x[j][i]) > 0.9) {
////                        log.info("solver, x[" + i + "," + j + "] = " + model.getValue(x[j][i]));
//                    assignment[i] = j;
//                    break;
//                }
//            }
//        }
//
//        for (int i = 0; i < n; i++) {
////                log.info("solver z[" + i + "] = " + z[i].solutionValue());
//            if (cp.getValue(z[i]) < 0.1) {
//                notAssignedClasses.add(i);
//            }
//        }
//    }
//
//    /**
//     * Con xem xet. Access modifier OK
//     */
//    void printSolutionVariables() throws IloException {
//        for (int j = 0; j < m; j++) {
//            for (int i = 0; i < n; i++) {
//                if (cp.getValue(x[j][i]) > 0.9) {
//                    System.out.println("x[" + j + "," + i + "] = " + cp.getValue(x[j][i]));
//                }
//            }
//        }
//
//        for (int i = 0; i < n; i++) {
//            if (cp.getValue(z[i]) > 0.9) {
//                System.out.println("z[" + i + "]= " + cp.getValue(z[i]));
//            }
//        }
//    }
//}
