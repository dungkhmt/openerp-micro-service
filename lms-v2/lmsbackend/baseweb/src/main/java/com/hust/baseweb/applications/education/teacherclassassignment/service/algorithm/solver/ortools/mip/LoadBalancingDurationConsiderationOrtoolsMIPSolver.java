package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
import lombok.extern.log4j.Log4j2;

/**
 * OK
 */
@Log4j2
public class LoadBalancingDurationConsiderationOrtoolsMIPSolver extends MaxAssignedClassOrtoolsMIPSolver {

    // Additional data parameters
    int numAssignedClasses;

    // MIP modelling
    MPVariable objectiveLoadBalancing;

    MPVariable[] t; // use for linearing constraint
//    private MPVariable[] u; // use for linearing constraint
//    private MPVariable min;

    public LoadBalancingDurationConsiderationOrtoolsMIPSolver(MapDataInput input) {
        super(input);
    }

    /**
     * OK
     *
     * @param numAssignedClasses
     */
    public void setNumAssignedClasses(int numAssignedClasses) {
        this.numAssignedClasses = numAssignedClasses;
    }

    @Override
    void createSolverAndVariables() {
        super.createSolverAndVariables();

        //
        t = new MPVariable[m];
        for (int i = 0; i < m; i++) {
            t[i] = solver.makeIntVar(0, 1, "t[" + i + "]");
        }

//        u = new MPVariable[m];
//        for (int i = 0; i < m; i++) {
//            u[i] = solver.makeIntVar(0, 1, "u[" + i + "]");
//        }

        // Consider
        objectiveLoadBalancing = solver.makeNumVar(0.0, 1.0, "objectiveLoadBalancing");
//        min = solver.makeNumVar(0.0, 1.0, "min");
    }

    /**
     * OK
     */
    private void createConstraintNumAssignedClasses() {
        MPConstraint c = solver.makeConstraint(numAssignedClasses, numAssignedClasses);
        for (int i = 0; i < n; i++) {
            c.setCoefficient(z[i], 1);
        }
    }

    /**
     * OK
     */
    private void createConstraintObjective() {
        // Constraint on the objective function
        // Y(j) > 0 --> F >= 1 - Y(j)/q(j) = 1 - sum_{i=1..n}[x(j, i) * hourClass(i) / maxHourTeacher(j)]
        double infinity = java.lang.Double.POSITIVE_INFINITY;
        double M = -1;
        for (int i = 0; i < m; i++) {
            if (maxHourTeacher[i] > M) {
                M = maxHourTeacher[i];
            }
        }
        M = (int) M + 1;

        // Linearing constraint
        //// Y(j) <= M*t(j) <--> M*t(j) - Y(j) >= 0, j = {1..m}
        for (int j = 0; j < m; j++) {
            MPConstraint c = solver.makeConstraint(0, infinity);
            c.setCoefficient(t[j], M);

            for (int i = 0; i < n; i++) {
                c.setCoefficient(x[j][i], -hourClass[i]);
            }
        }

        //// F + [1 - t(j)]*M >= 1 - Y(j)/q(j) <--> F - M*t(j) + Y(j)/q(j) >= 1 - M, j = {1..m}
        for (int j = 0; j < m; j++) {
            MPConstraint c = solver.makeConstraint(1 - M, infinity);
            c.setCoefficient(objectiveLoadBalancing, 1);
            c.setCoefficient(t[j], -M);

            for (int i = 0; i < n; i++) {
                c.setCoefficient(x[j][i], hourClass[i] / maxHourTeacher[j]);
            }
        }

        // Test obj func: min(min-max)
//        for (int j = 0; j < m; j++) {
//            MPConstraint c = solver.makeConstraint(0, infinity);
//            c.setCoefficient(u[j], M);
//
//            for (int i = 0; i < n; i++) {
//                c.setCoefficient(x[j][i], -hourClass[i]);
//            }
//        }
//
//        //// F + [1 - t(j)]*M >= 1 - Y(j)/q(j) <--> F - M*t(j) + Y(j)/q(j) >= 1 - M, j = {1..m}
//        for (int j = 0; j < m; j++) {
//            MPConstraint c = solver.makeConstraint(0, 1 + M);
//            c.setCoefficient(min, 1);
//            c.setCoefficient(u[j], M);
//
//            for (int i = 0; i < n; i++) {
//                c.setCoefficient(x[j][i], hourClass[i] / maxHourTeacher[j]);
//            }
//        }
    }

    /**
     * OK
     */
    @Override
    void createConstraints() {
        super.createConstraintsPreAssignment();
        super.createConstraintMaxHourLoadTeacher();
        super.createConstraintConflictClasses();
        super.createConstraintChannelXZ();

        createConstraintNumAssignedClasses();
        createConstraintObjective();
    }

    /**
     * OK
     */
    @Override
    void createObjective() {
        MPObjective objective = solver.objective();
        objective.setCoefficient(objectiveLoadBalancing, 1);
//        objective.setCoefficient(min, -1);
        objective.setMinimization();
    }

    /**
     * OK. Overriding is required
     *
     * @return
     */
    @Override
    public boolean solve() {
        createSolverAndVariables();
        createConstraints();
        createObjective();
        solver.setTimeLimit(900 * 1000);
        // Solves.
        final MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            log.info("Solution status = OPTIMAL");

            // Analyse solution.
            super.extractSolution();
            return true;
        }

        return false;
    }
}
