//package com.hust.baseweb.applications.education.teacherclassassignment.service;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip.LoadBalancingDurationConsiderationOrtoolsMIPSolver;
//import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.ortools.mip.MaxAssignedClassOrtoolsMIPSolver;
//import lombok.extern.log4j.Log4j2;
//
//import java.io.PrintWriter;
//import java.util.HashSet;
//
///**
// * Day la class phai lam viec
// */
//@Log4j2
//public class ORToolMIPSolver {
//
//    private final MapDataInput input;
//
//    // Solvers
//    private final MaxAssignedClassOrtoolsMIPSolver maxAssignedClassOrtoolsMIPSolver;
//
//    private final MaxPriorityClassAssignmentORToolMIPSolver maxPriorityClassAssignmentORToolMIPSolver;
//
//    private final MinWorkingDaysClassAssignmentORToolMIPSolver minWorkingDaysClassAssignmentORToolMIPSolver;
//
//    private final LoadBalancingDurationConsiderationOrtoolsMIPSolver loadBalancingDurationConsiderationSolver;
//
//    private int[] assignment;
//
//    private HashSet<Integer> notAssignedClasses;
//
//    public ORToolMIPSolver(MapDataInput input) {
//        this.input = input;
//        maxAssignedClassOrtoolsMIPSolver = new MaxAssignedClassOrtoolsMIPSolver(input);
//        maxPriorityClassAssignmentORToolMIPSolver = new MaxPriorityClassAssignmentORToolMIPSolver(input);
//        minWorkingDaysClassAssignmentORToolMIPSolver = new MinWorkingDaysClassAssignmentORToolMIPSolver(input);
//        loadBalancingDurationConsiderationSolver = new LoadBalancingDurationConsiderationOrtoolsMIPSolver(input);
//    }
//
//    public String name() {
//        return "ORToolMIPSolver";
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    public int[] getAssignment() {
//        return assignment;
//    }
//
//    /**
//     * OK
//     *
//     * @return
//     */
//    public HashSet<Integer> getNotAssignedClasses() {
//        return notAssignedClasses;
//    }
//
//    /**
//     * Chua quan tam
//     *
//     * @param fo
//     * @param timeLimit
//     * @return
//     */
//    public boolean testSolveOffline(String fo, int timeLimit) {
//        System.out.println(name() + "::testSolveOffline, fo = " + fo);
//        boolean ok = maxAssignedClassOrtoolsMIPSolver.solve();
//        assignment = maxAssignedClassOrtoolsMIPSolver.getAssignments();
//        notAssignedClasses = maxAssignedClassOrtoolsMIPSolver.getNotAssignedClasses();
//        try {
//            PrintWriter out = new PrintWriter(fo);
//            int ans = 0;
//            for (int i = 0; i < assignment.length; i++) {
//                out.println(i + " " + assignment[i]);
//                if (assignment[i] > -1) {
//                    ans += 1;
//                }
//            }
//            out.println(ans);
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ok;
//    }
//
//    public boolean solve(String solver) {
//        // Only for logging
//        String thisMethodName = "solve, ";
//
//        log.info(thisMethodName + "solver = " + solver);
//
//        boolean isOptimal = maxAssignedClassOrtoolsMIPSolver.solve();
//        assignment = maxAssignedClassOrtoolsMIPSolver.getAssignments();
//        notAssignedClasses = maxAssignedClassOrtoolsMIPSolver.getNotAssignedClasses();
//
//        log.info(thisMethodName + "PHASE 1: maxAssignedClassOrtoolsMIPSolver"
////                           + ", priority = " + maxAssignedClassOrtoolsMIPSolver.getObjectivePriority()
//                 +
//                 ", numAssignedClass = " +
//                 maxAssignedClassOrtoolsMIPSolver.getObjectiveValue()
//        );
//
//        int numAssignedClasses = input.n - notAssignedClasses.size();
//
//        //if(true) return isOptimal;
//        if (solver.equals("PRIORITY")) {
//            System.out.println(name() + "::solve, solver = " + solver + " start priority solver");
//
//            maxPriorityClassAssignmentORToolMIPSolver.setNbAssignedClasses(numAssignedClasses);
//
//            isOptimal = maxPriorityClassAssignmentORToolMIPSolver.solve();
//            assignment = maxPriorityClassAssignmentORToolMIPSolver.getSolutionAssignment();
//            notAssignedClasses = maxPriorityClassAssignmentORToolMIPSolver.getNotAssignedClass();
//
//            System.out.println("PHASE 2: maxPriorityClassAssignmentORToolMIPSolver, priority = "
//                               +
//                               maxPriorityClassAssignmentORToolMIPSolver.getObjectivePriority()
//                               +
//                               " nbAssignedClass = " +
//                               maxPriorityClassAssignmentORToolMIPSolver.getObjectiveNumberAssignedClass()
//            );
//        } else if (solver.equals("WORKDAYS")) {
//            System.out.println(name() + "::solve, solver = " + solver + " start work days solver");
//            minWorkingDaysClassAssignmentORToolMIPSolver.setNbAssignedClasses(numAssignedClasses);
//            isOptimal = minWorkingDaysClassAssignmentORToolMIPSolver.solve();
//            assignment = minWorkingDaysClassAssignmentORToolMIPSolver.getSolutionAssignment();
//            notAssignedClasses = minWorkingDaysClassAssignmentORToolMIPSolver.getNotAssignedClass();
//
//            System.out.println("PHASE 3: minWorkingDaysClassAssignmentORToolMIPSolver, objectiveMinWorkingDays = " +
//                               minWorkingDaysClassAssignmentORToolMIPSolver.getObjectiveMinWorkingDays());
//        } else if ("LOAD_BALANCING_DURATION_CONSIDERATION".equalsIgnoreCase(solver)) {
//            loadBalancingDurationConsiderationSolver.setNumAssignedClasses(numAssignedClasses);
//
//            isOptimal = loadBalancingDurationConsiderationSolver.solve();
//            assignment = loadBalancingDurationConsiderationSolver.getAssignments();
//            notAssignedClasses = loadBalancingDurationConsiderationSolver.getNotAssignedClasses();
//
//            log.info(thisMethodName + "PHASE 2: loadBalancingDurationConsiderationSolver"
//                     + ", F_min = " + loadBalancingDurationConsiderationSolver.getObjectiveValue());
//        }
//
//        return isOptimal;
//    }
//
//    public static void main(String[] args) {
//        System.out.println("ORToolMIPSolver start....");
//        MapDataInput input = new MapDataInput();
//        String fi = "D:/tmp/data-bca/3.txt";
//        String fo = "D:/tmp/data-bca/3-out.txt";
//        //String fi = "D:/tmp/data-bca/input/bca-1.txt";
//        //input.genRandom(fi,500,50);
//        input.loadDataFromPlanFile(fi);
//        ORToolMIPSolver solver = new ORToolMIPSolver(input);
//        //solver.solve("MAXCLASS");
//        boolean ok = solver.testSolveOffline(fo, 1000);
//
//        input.checkSolution(fi, fo);
//    }
//}
