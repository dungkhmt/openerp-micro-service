//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.mip;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.MaxAssignedClassBaseSolver;
//import ilog.concert.*;
//import ilog.cplex.IloCplex;
//import lombok.extern.log4j.Log4j2;
//
//import java.util.Arrays;
//
///**
// * OK
// */
//@Log4j2
//public class MaxAssignedClassCplexMIPSolver extends MaxAssignedClassBaseSolver {
//
//    // MIP modelling
//    IloIntVar[][] x; // x[j,i] = 1 indicates that class i is assigned to teacher j, = 0 otherwise
//
//    IloIntVar[] z; // Z[i] = 1 indicates that class i is assigned, = 0 otherwise
//
//    IloCplex model;
//
//    public MaxAssignedClassCplexMIPSolver(MapDataInput input) {
//        super(input);
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    public double getObjectiveValue() {
//        try {
//            return model.getObjValue();
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
//        model = new IloCplex();
//
//        log.info("createSolverAndVariables, n = " + n + " m = " + m);
//
//        x = new IloIntVar[m][n];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                if (!D[i].contains(j)) { // teacher j cannot be assigned to class i
//                    x[j][i] = model.intVar(0, 0, "x[" + j + "," + i + "]");
////                    log.info(name() + "::solve, FORCE var x[" + j + "," + i + "] = 0");
//                } else {
//                    x[j][i] = model.intVar(0, 1, "x[" + j + "," + i + "]");
//                }
//            }
//        }
//
//        // Cac lop ngoai preAssignments, D[i].size() == 1 --> z[i] = {0, 1]
//        z = new IloIntVar[n];
//        for (int i = 0; i < n; i++) {
//            z[i] = model.intVar(0, 1, "z[" + i + "]");
//        }
//
//        // Toi uu khoi tao z: lop i trong preAssignments --> z[i] = 1
//        if (null != preAssignments && 0 != preAssignments.length) {
//            for (int[] assignment : preAssignments) {
//                int i = assignment[0]; // class index
//                z[i] = model.intVar(1, 1, "z[" + i + "]");
//            }
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
//        model.addEq(x[j][i], 1);
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    private void createConstraintMaxHourLoadTeacher() throws IloException {
//        // Hour load of each teacher cannot exceed the maximum allowed valueOf
//        for (int j = 0; j < m; j++) {
//            model.addLe(model.scalProd(x[j], hourClass), maxHourTeacher[j]);
//        }
//    }
//
//    /**
//     * OK. Access modifier OK
//     */
//    private void createConstraintConflictClasses() throws IloException {
//        // Conflict constraint
//        for (int j = 0; j < m; j++) {
//            for (int c1 = 0; c1 < n; c1++) {
//                for (int c2 = c1 + 1; c2 < n; c2++) {
//                    if (conflict[c1][c2] && D[c1].contains(j) && D[c2].contains(j)) {
//                        model.addLe(model.sum(model.prod(1, x[j][c1]), model.prod(1, x[j][c2])), 1);
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
//        // Constraint between x and z: {0, 1} = z[i] = sum_{j=1..m} x[j,i]
//        // Also constraint each class is assigned to at most one teacher
//        for (int i = 0; i < n; i++) {
//            IloLinearIntExpr column = model.linearIntExpr();
//            for (int j = 0; j < m; j++) {
//                column.addTerm(1, x[j][i]);
//            }
//            model.addEq(column, z[i]);
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
//        model.addMaximize(model.sum(z), "objectiveMaxNumClassAssigned");
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
//            // Solves the model.
//            log.info("Model created, start solving...");
//
//            if (model.solve()) {
//                model.output().println("Solution status = " + model.getStatus());
//
//                // Analyse solution.
//                extractSolution();
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
//        assignments = new int[n];
//        Arrays.fill(assignments, -1);
//
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                if (model.getValue(x[j][i]) > 0.9) {
////                        log.info("solver, x[" + i + "," + j + "] = " + model.getValue(x[j][i]));
//                    assignments[i] = j;
//                    break;
//                }
//            }
//        }
//
//        for (int i = 0; i < n; i++) {
////                log.info("solver z[" + i + "] = " + z[i].solutionValue());
//            if (model.getValue(z[i]) < 0.1) {
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
//                if (model.getValue(x[j][i]) > 0.9) {
//                    System.out.println("x[" + j + "," + i + "] = " + model.getValue(x[j][i]));
//                }
//            }
//        }
//
//        for (int i = 0; i < n; i++) {
//            if (model.getValue(z[i]) > 0.9) {
//                System.out.println("z[" + i + "]= " + model.getValue(z[i]));
//            }
//        }
//    }
//}
//
