//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.mip;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import ilog.concert.IloException;
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//public class MaxAssignedTeacherCplexMIPSolver extends LoadBalancingDurationConsiderationCplexMIPSolver {
//
//    // Additional data parameters
//    private double loadBalancingObjValue;
//
//    public MaxAssignedTeacherCplexMIPSolver(MapDataInput input) {
//        super(input);
//    }
//
//    public void setLoadBalancingObjValue(double loadBalancingObjValue) {
//        this.loadBalancingObjValue = loadBalancingObjValue;
//    }
//
//    /**
//     * OK
//     */
//    @Override
//    void createObjective() throws IloException {
//        double delta = 0.00001; // To avoid tolerance for floating point arithmetic
//
//        model.addGe(objectiveLoadBalancing, loadBalancingObjValue - delta);
//        model.addLe(objectiveLoadBalancing, loadBalancingObjValue + delta);
//        model.addMaximize(model.sum(t));
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    @Override
//    public boolean solve() {
//        try {
//            super.createSolverAndVariables();
//            super.createConstraints();
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
