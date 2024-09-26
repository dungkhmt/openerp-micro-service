package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.cp;

import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OrtoolsCPSolver extends BaseSolver {

    @Override
    public boolean solve(SolverConfig config) {
        long start, end;
        MaxAssignedClassOrtoolsCPSolver maxAssignedClassSolver = new MaxAssignedClassOrtoolsCPSolver(input);
//        maxAssignedClassSolver.setTimeLimit(900);

        log.info("solve, solver " + config.getSolver() +
                 " with model " + config.getModel() +
                 " target " + config.getObjective());

        start = System.nanoTime();
        boolean isOptimal = maxAssignedClassSolver.solve();
        end = System.nanoTime();
        System.out.println("OrtoolsCPSolver Phase 1, time = " +
                           ((double) (end - start) / 1_000_000_000));
        assignment = maxAssignedClassSolver.getAssignment();
        notAssignedClasses = maxAssignedClassSolver.getNotAssignedClasses();
        int numAssignedClasses = input.n - notAssignedClasses.size();

        log.info("solve, PHASE 1: MaxAssignedClassOrtoolsCPSolver" +
                 ", numAssignedClass = " + maxAssignedClassSolver.getObjectiveValue()
        );

        if (config.getObjective().equals(SolverConfig.Objective.LOAD_BALANCING_DURATION_CONSIDERATION)) {
            LoadBalancingDurationConsiderationOrtoolsCPSolver loadBalancingSolver = new LoadBalancingDurationConsiderationOrtoolsCPSolver(
                input);
            loadBalancingSolver.setNumAssignedClasses(numAssignedClasses);

            start = end;
            loadBalancingSolver.solve();
            end = System.nanoTime();
            System.out.println("OrtoolsCPSolver Phase 2, time = " +
                               ((double) (end - start) / 1_000_000_000));
            assignment = loadBalancingSolver.getAssignment();
            notAssignedClasses = loadBalancingSolver.getNotAssignedClasses();

            log.info("solve, PHASE 2: LoadBalancingDurationConsiderationOrtoolsCPSolver"
                     + ", F_min = " + loadBalancingSolver.getObjectiveValue());

            MaxAssignedTeacherOrtoolsCPSolver maxAssignedTeacherSolver = new MaxAssignedTeacherOrtoolsCPSolver(input);
            maxAssignedTeacherSolver.setNumAssignedClasses(numAssignedClasses);
            maxAssignedTeacherSolver.setLoadBalancingObjValue((long) loadBalancingSolver.getObjectiveValue());

            start = end;
            isOptimal = maxAssignedTeacherSolver.solve();
            end = System.nanoTime();
            System.out.println("OrtoolsCPSolver Phase 3, time = " +
                               ((double) (end - start) / 1_000_000_000));
            assignment = maxAssignedTeacherSolver.getAssignment();
            notAssignedClasses = maxAssignedTeacherSolver.getNotAssignedClasses();

            log.info("solve, PHASE 3: MaxAssignedTeacherOrtoolsCPSolver"
                     + ", numAssignedTeacher = " + maxAssignedTeacherSolver.getObjectiveValue());
        } else {
            // ...
        }

        return isOptimal;
    }
}
