//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.cp;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
//import lombok.extern.log4j.Log4j2;
//
//import java.util.concurrent.TimeUnit;
//
//@Log4j2
//public class CplexCPSolver extends BaseSolver {
//
//    @Override
//    public boolean solve(SolverConfig config) {
//        long start, end;
//        MaxAssignedClassCplexCPSolver maxAssignedClassSolver = new MaxAssignedClassCplexCPSolver(input);
//
//        log.info("solve, solver " + config.getSolver() +
//                 " with model " + config.getModel() +
//                 " target " + config.getObjective());
//
//        start = System.nanoTime();
//        boolean isOptimal = maxAssignedClassSolver.solve();
//        end = System.nanoTime();
//        System.out.println("CplexCPSolver Phase 1, time = " +
//                           ((double) (end - start) / 1_000_000_000));
//        assignment = maxAssignedClassSolver.getAssignment();
//        notAssignedClasses = maxAssignedClassSolver.getNotAssignedClasses();
//        int numAssignedClasses = input.n - notAssignedClasses.size();
//
//        log.info("solve, PHASE 1: MaxAssignedClassCplexCPSolver" +
//                 ", numAssignedClass = " + maxAssignedClassSolver.getObjectiveValue()
//        );
//
//        if (config.getObjective().equals(SolverConfig.Objective.LOAD_BALANCING_DURATION_CONSIDERATION)) {
//            LoadBalancingDurationConsiderationCplexCPSolver loadBalancingSolver = new LoadBalancingDurationConsiderationCplexCPSolver(
//                input);
//            loadBalancingSolver.setNumAssignedClasses(numAssignedClasses);
//
//            start = end;
//            loadBalancingSolver.solve();
//            end = System.nanoTime();
//            System.out.println("CplexCPSolver Phase 2, time = " +
//                               ((double) (end - start) / 1_000_000_000));
//
//            assignment = loadBalancingSolver.getAssignment();
//            notAssignedClasses = loadBalancingSolver.getNotAssignedClasses();
//
//            log.info("solve, PHASE 2: LoadBalancingDurationConsiderationCplexCPSolver"
//                     + ", F_min = " + loadBalancingSolver.getObjectiveValue());
//
//            MaxAssignedTeacherCplexCPSolver maxAssignedTeacherSolver = new MaxAssignedTeacherCplexCPSolver(input);
//            maxAssignedTeacherSolver.setNumAssignedClasses(numAssignedClasses);
//            maxAssignedTeacherSolver.setLoadBalancingObjValue(loadBalancingSolver.getObjectiveValue());
//
//            start = end;
//            isOptimal = maxAssignedTeacherSolver.solve();
//            end = System.nanoTime();
//            System.out.println("CplexCPSolver Phase 3, time = " +
//                               ((double) (end - start) / 1_000_000_000));
//            assignment = maxAssignedTeacherSolver.getAssignment();
//            notAssignedClasses = maxAssignedTeacherSolver.getNotAssignedClasses();
//
//            log.info("solve, PHASE 3: MaxAssignedTeacherCplexCPSolver"
//                     + ", numAssignedTeacher = " + maxAssignedTeacherSolver.getObjectiveValue());
//        } else {
//            // ...
//        }
//
//        return isOptimal;
//    }
//}
