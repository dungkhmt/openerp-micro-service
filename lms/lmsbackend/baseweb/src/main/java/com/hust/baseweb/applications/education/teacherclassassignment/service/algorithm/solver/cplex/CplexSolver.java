//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.cp.CplexCPSolver;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.mip.CplexMIPSolver;
//
//
//public class CplexSolver extends BaseSolver {
//
//    @Override
//    public String name() {
//        return "CplexSolver";
//    }
//
//    @Override
//    public boolean solve(SolverConfig config) {
//        CplexSolver solver;
//        if (config.getModel().equals(SolverConfig.Model.MIP)) {
//            solver = new CplexMIPSolver();
//        } else {
//            solver = new CplexCPSolver();
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
