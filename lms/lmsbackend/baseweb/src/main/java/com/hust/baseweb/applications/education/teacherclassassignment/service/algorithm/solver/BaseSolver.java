package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver;

import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.cp.CplexCPSolver;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.mip.CplexMIPSolver;
import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.cp.OrtoolsCPSolver;
import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip.OrtoolsMIPSolver;

import java.util.HashSet;

public class BaseSolver implements ISolver {

    protected MapDataInput input;

    // Data structures for solution
    protected int[] assignment; // assignment[i] is the teacher assigned to class i

    protected HashSet<Integer> notAssignedClasses;

    public void setInput(MapDataInput input) {
        this.input = input;
    }

    @Override
    public int[] getAssignment() {
        return this.assignment;
    }

    @Override
    public HashSet<Integer> getNotAssignedClasses() {
        return this.notAssignedClasses;
    }

    @Override
    public boolean solve(SolverConfig config) {
        BaseSolver solver;

//        if (config.getSolver().equals(SolverConfig.Solver.CPLEX)) {
//            if (config.getModel().equals(SolverConfig.Model.MIP)) {
//                solver = new CplexMIPSolver();
//            } else {
//                solver = new CplexCPSolver();
//            }
//        } else {
        if (config.getModel().equals(SolverConfig.Model.MIP)) {
            solver = new OrtoolsMIPSolver();
        } else {
            solver = new OrtoolsCPSolver();
        }
//        }

        solver.setInput(input);
        long start, end;
        start = System.nanoTime();
        boolean status = solver.solve(config);
        end = System.nanoTime();
        System.out.println("Total time = " +
                           ((double) (end - start) / 1_000_000_000));
        this.assignment = solver.getAssignment();
        this.notAssignedClasses = solver.getNotAssignedClasses();

        return status;
    }
}
