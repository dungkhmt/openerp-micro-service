package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.cp;

import com.google.ortools.sat.*;
import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoadBalancingDurationConsiderationOrtoolsCPSolver extends MaxAssignedClassOrtoolsCPSolver {

    // Additional data parameters
    int numAssignedClasses;

    // With a problem that has constraints with non-integer terms,
    // you need to first multiply those constraints by a sufficiently large integer so that all terms are integers
    long factor;

    // CP modelling
    IntVar objectiveLoadBalancing;
    BoolVar[] b;

    public LoadBalancingDurationConsiderationOrtoolsCPSolver(MapDataInput input) {
        super(input);
    }

    /**
     * To find LCM
     *
     * @param num1
     * @param num2
     * @return
     */
    private long gcd(long num1, long num2) {
        if (num1 == 0 || num2 == 0) {
            return num1 + num2;
        } else {
            long absNumber1 = Math.abs(num1);
            long absNumber2 = Math.abs(num2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    /**
     * Find LCM of array. lcm(a,b) = (a*b/gcd(a,b))
     *
     * @return
     */
    private long lcm(long[] array, int index) {
        if (index == array.length - 1) {
            return array[index];
        }

        long a = array[index];
        long b = lcm(array, index + 1);
        return (a * b / gcd(a, b)); //
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
        factor = lcm(maxHourTeacher, 0);

        // From range [0, 1] to [0, factor]
        objectiveLoadBalancing = model.newIntVar(0, factor, "objectiveLoadBalancing");
    }

    /**
     * OK
     */
    private void createConstraintNumAssignedClassesAtLeast() {
        model.addEquality(LinearExpr.sum(z), numAssignedClasses);
    }

    /**
     * OK
     */
    private void createConstraintObjective() {
        // Todo: consider when hourClass is real number, with tolerance for floating point arithmetic
        // constraint on the objective function
        // Y(j) > 0 --> F >= 1 - Y(j)/q(j) = 1 - sum_{i=1..n}[x(j, i) * hourClass(i) / maxHourTeacher(j)]
        long[][] coeffs = new long[m][n];
        b = new BoolVar[m]; // To model if-then constraint
        for (int j = 0; j < m; j++) {
            b[j] = model.newBoolVar("b[" + j + "]");
            for (int i = 0; i < n; i++) {
                coeffs[j][i] = factor * hourClass[i] / maxHourTeacher[j]; // factor / maxHourTeacher[j] = int
            }
        }

        LinearExpr[] loadFactor = new LinearExpr[m];
        for (int j = 0; j < m; j++) {
            loadFactor[j] = LinearExpr.weightedSum(x[j], coeffs[j]);
        }

        for (int j = 0; j < m; j++) {
            model.addGreaterThan(loadFactor[j], 0).onlyEnforceIf(b[j]);
            model.addEquality(loadFactor[j], 0).onlyEnforceIf(b[j].not()); // required
            model.addGreaterOrEqual(LinearExpr.sum(new LinearArgument[]{objectiveLoadBalancing, loadFactor[j]}), factor)
                 .onlyEnforceIf(b[j]);
        }
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

        createConstraintNumAssignedClassesAtLeast();
        createConstraintObjective();
    }

    /**
     * OK
     */
    @Override
    void createObjective() {
        model.minimize(objectiveLoadBalancing);
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
            System.out.printf("Maximum of objective function: %f%n", solver.objectiveValue() / factor);
            System.out.println("factor * objectiveLoadBalancing = " +
                               factor + " * " + (solver.objectiveValue() / factor) +
                               " = " + solver.objectiveValue());

            // Analyse solution.
            super.extractSolution();
            return true;
        } else {
            System.out.println("No solution found.");
        }

        return false;
    }
}
