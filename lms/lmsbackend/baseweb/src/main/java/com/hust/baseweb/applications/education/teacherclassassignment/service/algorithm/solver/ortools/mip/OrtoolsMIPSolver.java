package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip;

import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;
import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OrtoolsMIPSolver extends BaseSolver {

    @Override
    public boolean solve(SolverConfig config) {
        long start, end;
        MaxAssignedClassOrtoolsMIPSolver maxAssignedClassSolver = new MaxAssignedClassOrtoolsMIPSolver(
            input);

        log.info("solve, solver " + config.getSolver() +
                 " with model " + config.getModel() +
                 " target " + config.getObjective());

        start = System.nanoTime();
        boolean isOptimal = maxAssignedClassSolver.solve();
        end = System.nanoTime();
        System.out.println("OrtoolsMIPSolver Phase 1, time = " +
                           ((double) (end - start) / 1_000_000_000));
        assignment = maxAssignedClassSolver.getAssignments();
        notAssignedClasses = maxAssignedClassSolver.getNotAssignedClasses();
        int numAssignedClasses = input.n - notAssignedClasses.size();

        log.info("solve, PHASE 1: MaxAssignedClassOrtoolsMIPSolver" +
                 ", numAssignedClass = " + maxAssignedClassSolver.getObjectiveValue()
        );

        if (config.getObjective().equals(SolverConfig.Objective.LOAD_BALANCING_DURATION_CONSIDERATION)) {
            LoadBalancingDurationConsiderationOrtoolsMIPSolver loadBalancingSolver = new LoadBalancingDurationConsiderationOrtoolsMIPSolver(
                input);
            loadBalancingSolver.setNumAssignedClasses(numAssignedClasses);

            start = end;
            loadBalancingSolver.solve();
            end = System.nanoTime();
            System.out.println("OrtoolsMIPSolver Phase 2, time = " +
                               ((double) (end - start) / 1_000_000_000));
            assignment = loadBalancingSolver.getAssignments();
            notAssignedClasses = loadBalancingSolver.getNotAssignedClasses();

            log.info("solve, PHASE 2: LoadBalancingDurationConsiderationOrtoolsMIPSolver"
                     + ", F_min = " + loadBalancingSolver.getObjectiveValue());

            MaxAssignedTeacherOrtoolsMIPSolver maxAssignedTeacherSolver = new MaxAssignedTeacherOrtoolsMIPSolver(input);
            maxAssignedTeacherSolver.setNumAssignedClasses(numAssignedClasses);
            maxAssignedTeacherSolver.setLoadBalancingObjValue(loadBalancingSolver.getObjectiveValue());

            start = end;
            isOptimal = maxAssignedTeacherSolver.solve();
            end = System.nanoTime();
            System.out.println("OrtoolsMIPSolver Phase 3, time = " +
                               ((double) (end - start) / 1_000_000_000));
            assignment = maxAssignedTeacherSolver.getAssignments();
            notAssignedClasses = maxAssignedTeacherSolver.getNotAssignedClasses();

            log.info("solve, PHASE 3: MaxAssignedTeacherOrtoolsMIPSolver"
                     + ", numAssignedTeacher = " + maxAssignedTeacherSolver.getObjectiveValue());
        } else {
            // ...
        }

        return isOptimal;
    }
}
