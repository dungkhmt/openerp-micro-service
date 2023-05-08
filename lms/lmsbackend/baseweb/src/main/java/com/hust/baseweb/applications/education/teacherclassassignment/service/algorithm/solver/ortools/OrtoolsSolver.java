//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.cp.OrtoolsCPSolver;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip.OrtoolsMIPSolver;
//
//public class OrtoolsSolver extends BaseSolver {
//
//    @Override
//    public String name() {
//        return "OrtoolsSolver";
//    }
//
//    @Override
//    public boolean solve(SolverConfig config) {
//        OrtoolsSolver solver;
//        if (config.getModel().equals(SolverConfig.Model.MIP)) {
//            solver = new OrtoolsMIPSolver();
//        } else {
//            solver = new OrtoolsCPSolver();
//        }
//
//        solver.setInput(input);
//        boolean status = solver.solve(config);
//        this.assignment = solver.getAssignments();
//        this.notAssignedClasses = solver.getNotAssignedClasses();
//
//        return status;
//    }
//}
