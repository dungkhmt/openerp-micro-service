package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;

/**
 * OK
 */
public class MaxAssignedTeacherOrtoolsMIPSolver extends LoadBalancingDurationConsiderationOrtoolsMIPSolver {

    public MaxAssignedTeacherOrtoolsMIPSolver(MapDataInput input) {
        super(input);
    }

    // Additional data parameters
    private double loadBalancingObjValue;

    public void setLoadBalancingObjValue(double loadBalancingObjValue) {
        this.loadBalancingObjValue = loadBalancingObjValue;
    }

    /**
     * OK
     */
    @Override
    void createObjective() {
        double delta = 0.00001; // To avoid tolerance for floating point arithmetic
        MPConstraint c = solver.makeConstraint(loadBalancingObjValue - delta, loadBalancingObjValue + delta);
        c.setCoefficient(objectiveLoadBalancing, 1);

        //
        MPObjective objective = solver.objective();
        for (int j = 0; j < m; j++) {
            objective.setCoefficient(t[j], 1);
        }
        objective.setMaximization();
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
        solver.setTimeLimit(900 * 1000);
        // Solves the model.
        final MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            // Analyse solution.
            super.extractSolution();
            return true;
        }

        return false;
    }
}
