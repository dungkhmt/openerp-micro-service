//package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.cplex.cp;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;
//import ilog.concert.IloException;
//import ilog.concert.IloIntVar;
//import ilog.cp.IloCP;
//
//public class MaxAssignedTeacherCplexCPSolver extends LoadBalancingDurationConsiderationCplexCPSolver {
//
//    private IloIntVar[] t;
//
//    // Additional data parameters
//    private double loadBalancingObjValue;
//
//    public void setLoadBalancingObjValue(double loadBalancingObjValue) {
//        this.loadBalancingObjValue = loadBalancingObjValue;
//    }
//
//    public MaxAssignedTeacherCplexCPSolver(MapDataInput input) {
//        super(input);
//    }
//
//    @Override
//    void createSolverAndVariables() throws IloException {
//        super.createSolverAndVariables();
//
//        //
//        t = new IloIntVar[m];
//        for (int j = 0; j < m; j++) {
//            t[j] = cp.intVar(0, 1, "t[" + j + "]");
//        }
//    }
//
//    private void createConstraintAssignedTeacher() throws IloException {
//        for (int j = 0; j < m; j++) {
//            cp.add(cp.ifThen(cp.eq(cp.sum(x[j]), 0), cp.eq(t[j], 0)));
//        }
//    }
//
//    @Override
//    void createConstraints() throws IloException {
//        super.createConstraints();
//        createConstraintAssignedTeacher();
//    }
//
//    /**
//     * OK
//     */
//    @Override
//    void createObjective() throws IloException {
//        cp.addEq(objectiveLoadBalancing, loadBalancingObjValue);
//        cp.add(cp.maximize(cp.sum(t)));
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
//
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
