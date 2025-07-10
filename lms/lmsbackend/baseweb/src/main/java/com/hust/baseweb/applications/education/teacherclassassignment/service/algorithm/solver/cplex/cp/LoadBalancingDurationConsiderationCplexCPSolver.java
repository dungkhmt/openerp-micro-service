//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.cp;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import com.hust.baseweb.applications.education.teacherclassassignment.utils.MathUtils;
//import ilog.concert.*;
//import ilog.cp.IloCP;
//
//public class LoadBalancingDurationConsiderationCplexCPSolver extends MaxAssignedClassCplexCPSolver {
//
//    // Additional data parameters
//    int numAssignedClasses;
//
//    // With a problem that has constraints with non-integer terms,
//    // you need to first multiply those constraints by a sufficiently large integer so that all terms are integers
//    int factor;
//
//    // CP modelling
//    IloNumVar objectiveLoadBalancing;
//
//    public LoadBalancingDurationConsiderationCplexCPSolver(MapDataInput input) {
//        super(input);
//    }
//
//    /**
//     * OK
//     *
//     * @param numAssignedClasses
//     */
//    void setNumAssignedClasses(int numAssignedClasses) {
//        this.numAssignedClasses = numAssignedClasses;
//    }
//
//
//    @Override
//    void createSolverAndVariables() throws IloException {
//        super.createSolverAndVariables();
//        factor = MathUtils.lcm(maxHourTeacher, 0);
//        objectiveLoadBalancing = cp.intVar(0, factor, "objectiveLoadBalancing");
//    }
//
//    /**
//     * OK
//     */
//    private void createConstraintNumAssignedClassesAtLeast() throws IloException {
//        cp.addEq(cp.sum(z), numAssignedClasses);
//    }
//
//    /**
//     * OK
//     */
//    private void createConstraintObjective() throws IloException {
//        // constraint on the objective function
//        // Y(j) > 0 --> F >= 1 - Y(j)/q(j) = 1 - sum_{i=1..n}[x(j, i) * hourClass(i) / maxHourTeacher(j)]
//        for (int j = 0; j < m; j++) {
//            IloLinearIntExpr totalLoad = cp.scalProd(x[j], hourClass);
//            cp.add(cp.ifThen(
//                cp.gt(totalLoad, 0),
//                cp.ge(
//                    cp.sum(objectiveLoadBalancing, cp.prod(totalLoad, factor / maxHourTeacher[j])),
//                    factor)));
//        }
//    }
//
//    /**
//     * OK
//     */
//    @Override
//    void createConstraints() throws IloException {
//        super.createConstraints();
//
//        createConstraintNumAssignedClassesAtLeast();
//        createConstraintObjective();
//    }
//
//    /**
//     * OK
//     */
//    @Override
//    void createObjective() throws IloException {
//        cp.add(cp.minimize(objectiveLoadBalancing));
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    @Override
//    public boolean solve() {
//        try {
//            createSolverAndVariables();
//            createConstraints();
//            createObjective();
//            cp.setParameter(IloCP.DoubleParam.TimeLimit, 900);
//            // Solves the model and display the solution if one was found.
//            if (cp.solve()) {
//                cp.output().println("Solution status = " + cp.getStatus());
//                System.out.println("factor * objectiveLoadBalancing = " +
//                                   factor + " * " + (cp.getObjValue() / factor) +
//                                   " = " + cp.getObjValue());
//                // Analyse solution.
//                super.extractSolution();
//                return true;
//            }
//        } catch (IloException e) {
//            System.err.println("Concert exception '" + e + "' caught");
//        }
//
//        return false;
//    }
//}
