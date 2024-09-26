//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.mip;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
//import lombok.extern.log4j.Log4j2;
//
//import java.util.concurrent.TimeUnit;
//
//@Log4j2
//public class CplexMIPSolver extends BaseSolver {
//
//    @Override
//    public boolean solve(SolverConfig config) {
//        long start, end;
//        MaxAssignedClassCplexMIPSolver maxAssignedClassSolver = new MaxAssignedClassCplexMIPSolver(input);
//        log.info("solve, solver " + config.getSolver() +
//                 " with model " + config.getModel() +
//                 " target " + config.getObjective());
//
//        start = System.nanoTime();
//        boolean isOptimal = maxAssignedClassSolver.solve();
//        end = System.nanoTime();
//        System.out.println("CplexMIPSolver Phase 1, time = " +
//                           ((double) (end - start) / 1_000_000_000));
//        assignment = maxAssignedClassSolver.getAssignments();
//        notAssignedClasses = maxAssignedClassSolver.getNotAssignedClasses();
//        int numAssignedClasses = input.n - notAssignedClasses.size();
//
//        log.info("solve, PHASE 1: MaxAssignedClassCplexMIPSolver" +
//                 ", numAssignedClass = " + maxAssignedClassSolver.getObjectiveValue()
//        );
//
//        if (config.getObjective().equals(SolverConfig.Objective.LOAD_BALANCING_DURATION_CONSIDERATION)) {
//            LoadBalancingDurationConsiderationCplexMIPSolver loadBalancingSolver = new LoadBalancingDurationConsiderationCplexMIPSolver(
//                input);
//            loadBalancingSolver.setNumAssignedClasses(numAssignedClasses);
//
//            start = end;
//            loadBalancingSolver.solve();
//            end = System.nanoTime();
//            System.out.println("CplexMIPSolver Phase 2, time = " +
//                               ((double) (end - start) / 1_000_000_000));
//
//            assignment = loadBalancingSolver.getAssignments();
//            notAssignedClasses = loadBalancingSolver.getNotAssignedClasses();
//
//            log.info("solve, PHASE 2: LoadBalancingDurationConsiderationCplexMIPSolver"
//                     + ", F_min = " + loadBalancingSolver.getObjectiveValue());
//
//            MaxAssignedTeacherCplexMIPSolver maxAssignedTeacherSolver = new MaxAssignedTeacherCplexMIPSolver(input);
//            maxAssignedTeacherSolver.setNumAssignedClasses(numAssignedClasses);
//            maxAssignedTeacherSolver.setLoadBalancingObjValue(loadBalancingSolver.getObjectiveValue());
//
//            start = end;
//            isOptimal = maxAssignedTeacherSolver.solve();
//            end = System.nanoTime();
//            System.out.println("CplexMIPSolver Phase 3, time = " +
//                               ((double) (end - start) / 1_000_000_000));
//            assignment = maxAssignedTeacherSolver.getAssignments();
//            notAssignedClasses = maxAssignedTeacherSolver.getNotAssignedClasses();
//
//            log.info("solve, PHASE 3: MaxAssignedTeacherCplexMIPSolver"
//                     + ", numAssignedTeacher = " + maxAssignedTeacherSolver.getObjectiveValue());
//        } else {
//            // ...
//        }
//
//        return isOptimal;
//    }
//}
