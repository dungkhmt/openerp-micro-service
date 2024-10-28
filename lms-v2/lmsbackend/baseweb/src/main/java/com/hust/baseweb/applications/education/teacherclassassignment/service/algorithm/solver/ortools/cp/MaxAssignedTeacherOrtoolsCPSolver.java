package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.cp;

import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MaxAssignedTeacherOrtoolsCPSolver extends LoadBalancingDurationConsiderationOrtoolsCPSolver {

    // Additional data parameters
    private long loadBalancingObjValue;

    public MaxAssignedTeacherOrtoolsCPSolver(MapDataInput input) {
        super(input);
    }

    public void setLoadBalancingObjValue(long loadBalancingObjValue) {
        this.loadBalancingObjValue = loadBalancingObjValue;
    }

    /**
     * OK
     */
    @Override
    void createObjective() {
//        log.info("START PHASE 3 with " + loadBalancingObjValue);
        model.addEquality(objectiveLoadBalancing, loadBalancingObjValue);
        model.maximize(LinearExpr.sum(b));
    }

    /**
     * OK
     *
     * @return
     */
    @Override
    public boolean solve() {
        super.createSolverAndVariables();
        super.createConstraints();
        createObjective();

        // Solves.
        log.info("Model created, start solving with time limit = " + timeLimit + "s");

        // Create a solver and solve the model.
        solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timeLimit);
        CpSolverStatus status = solver.solve(model);

        // Statistics.
        System.out.println("Statistics");
        System.out.printf("  conflicts: %d%n", solver.numConflicts());
        System.out.printf("  branches : %d%n", solver.numBranches());
        System.out.printf("  wall time: %f s%n", solver.wallTime());

        if (status == CpSolverStatus.OPTIMAL) {
            System.out.printf("Maximum of objective function: %f%n", solver.objectiveValue());

            // Analyse solution.
            super.extractSolution();
            return true;
        } else {
            System.out.println("No solution found.");
        }

        return false;
    }
}
