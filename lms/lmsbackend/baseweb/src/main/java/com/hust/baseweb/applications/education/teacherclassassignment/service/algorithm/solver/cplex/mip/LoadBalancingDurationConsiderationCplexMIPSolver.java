//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.mip;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import ilog.concert.IloException;
//import ilog.concert.IloIntVar;
//import ilog.concert.IloLinearNumExpr;
//import ilog.concert.IloNumVar;
//import lombok.extern.log4j.Log4j2;
//
///**
// * OK
// */
//@Log4j2
//public class LoadBalancingDurationConsiderationCplexMIPSolver extends MaxAssignedClassCplexMIPSolver {
//
//    // Additional data parameters
//    int numAssignedClasses;
//
//    // MIP modelling
//    IloNumVar objectiveLoadBalancing;
//
//    IloIntVar[] t; // use for linearing constraint
////    private MPVariable[] u; // use for linearing constraint
////    private MPVariable min;
//
//    public LoadBalancingDurationConsiderationCplexMIPSolver(MapDataInput input) {
//        super(input);
//    }
//
//    /**
//     * OK
//     *
//     * @param numAssignedClasses
//     */
//    void setNumAssignedClasses(int numAssignedClasses) {
//        this.numAssignedClasses = numAssignedClasses;
//    }
//
//    @Override
//    void createSolverAndVariables() throws IloException {
//        super.createSolverAndVariables();
//
//        //
//        t = new IloIntVar[m];
//        for (int i = 0; i < m; i++) {
//            t[i] = model.intVar(0, 1, "t[" + i + "]");
//        }
//
//        objectiveLoadBalancing = model.numVar(0.0, 1.0, "objectiveLoadBalancing");
//    }
//
//    /**
//     * OK
//     */
//    private void createConstraintNumAssignedClasses() throws IloException {
//        model.addEq(model.sum(z), numAssignedClasses);
//    }
//
//    /**
//     * OK
//     */
//    private void createConstraintObjective() throws IloException {
//        // Constraint on the objective function
//        // Y(j) > 0 --> F >= 1 - Y(j)/q(j) = 1 - sum_{i=1..n}[x(j, i) * hourClass(i) / maxHourTeacher(j)]
//        double M = -1;
//        for (int i = 0; i < m; i++) {
//            if (maxHourTeacher[i] > M) {
//                M = maxHourTeacher[i];
//            }
//        }
//        M = (int) M + 1;
//
//        // Linearing constraint
//        //// Y(j) <= M*t(j) <--> M*t(j) - Y(j) >= 0, j = {1..m}
//        for (int j = 0; j < m; j++) {
//            IloLinearNumExpr totalLoad = model.scalProd(x[j], hourClass);
//            model.addGe(model.prod(M, t[j]), totalLoad);
//        }
//
//        //// F + [1 - t(j)]*M >= 1 - Y(j)/q(j) <--> F - M*t(j) + Y(j)/q(j) >= 1 - M, j = {1..m}
//        for (int j = 0; j < m; j++) {
//            IloLinearNumExpr lhs = model.linearNumExpr();
//            lhs.addTerm(1, objectiveLoadBalancing);
//            lhs.addTerm(-M, t[j]);
//
//            for (int i = 0; i < n; i++) {
//                lhs.addTerm(hourClass[i] / maxHourTeacher[j], x[j][i]);
//            }
//
//            model.addGe(lhs, 1 - M);
//        }
//    }
//
//    /**
//     * OK
//     */
//    @Override
//    void createConstraints() throws IloException {
//        super.createConstraints();
//
//        createConstraintNumAssignedClasses();
//        createConstraintObjective();
//    }
//
//    /**
//     * OK
//     */
//    @Override
//    void createObjective() throws IloException {
//        model.addMinimize(objectiveLoadBalancing);
//    }
//
//    /**
//     * OK. Overriding is required
//     *
//     * @return
//     */
//    @Override
//    public boolean solve() {
//        try {
//            createSolverAndVariables();
//            createConstraints();
//            createObjective();
//
//            // Solves the model.
//            if (model.solve()) {
//                model.output().println("Solution status = " + model.getStatus());
//
//                // Analyse solution.
//                super.extractSolution();
//                return true;
//            }
//        } catch (IloException e) {
//            System.err.println("Concert exception '" + e + "' caught");
//        }
//
//        return false;
//    }
//}
//
