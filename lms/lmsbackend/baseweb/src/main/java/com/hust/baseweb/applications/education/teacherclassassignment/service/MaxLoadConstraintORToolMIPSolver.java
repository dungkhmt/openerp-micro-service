package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.HashSet;

public class MaxLoadConstraintORToolMIPSolver {

    private int n;// number of classes
    private int m;// number of teachers
    private HashSet<Integer>[] D;// D[i] is the set of teachers that can be assigned to class i
    private boolean[][] conflict;
    private int[][] priority;
    private double[] hourClass;
    private double[] maxHourTeacher;

    // intermediate Data structure
    private double totalHourClass;
    private int INF;
    private int maxP;// max value of priority
    private int minP;// min value of priority

    // parameters
    private int alpha;
    private int[] beta;

    // MIP modelling
    private MPVariable[][] x;// x[i,j] = 1 indicates that teacher i is assigned to class j
    private MPSolver solver;
    private MPVariable obj;
    private MPObjective objectiveMaxNbClassAssigned;
    private MPVariable[][] y;// Y[i,k] = 1 indicates that class i is assigned to a teacher with priority k
    private MPVariable[] z;// Z[i] = 1 indicates that class i is assigned to some teacher
    private MPVariable[] u;// u[j] is the hour_load of teacher j

    // data structures for solution
    private int[] assignment;// assignment[i] is the teacher assigned to class i
    private HashSet<Integer> notAssigned;
    public MaxLoadConstraintORToolMIPSolver(
        int n,
        int m,
        HashSet[] D,
        int[][] priority,
        boolean[][] conflict,
        double[] hourClass,
        double[] maxHourTeacher
    ) {
        this.n = n;
        this.m = m;
        this.D = D;
        this.priority = priority;
        this.conflict = conflict;
        this.hourClass = hourClass;
        this.maxHourTeacher = maxHourTeacher;


    }
    public HashSet<Integer> getNotAssignedClass(){
        return notAssigned;
    }
    private void initDatastructures() {
        totalHourClass = 0;
        for (int i = 0; i < n; i++) {
            totalHourClass += hourClass[i];
        }
        INF = (int) totalHourClass + 1;
        maxP = 0;
        minP = Integer.MAX_VALUE;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(priority[i][j] < Integer.MAX_VALUE){
                        if(priority[i][j] > maxP) maxP = priority[i][j];
                        if(priority[i][j] < minP) minP = priority[i][j];
                }

            }
        }

        // init parameters
        alpha = 10000;
        beta = new int[maxP + 1];
        for(int k = minP; k <= maxP; k++) beta[k] = 1;
    }

    public void createSolverAndVariables() {
        x = new MPVariable[m][n];

        Loader.loadNativeLibraries();
        solver = MPSolver.createSolver(String.valueOf(MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING));

        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return;
        }

        System.out.println("createSolverAndVariables, n = " + n + " m = " + m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!D[i].contains(j)) {// teacher j cannot be assigned to class i
                    x[j][i] = solver.makeIntVar(0, 0, "x[" + j + "," + i + "]");
                } else {
                    x[j][i] = solver.makeIntVar(0, 1, "x[" + j + "," + i + "]");
                }
            }
        }
        obj = solver.makeIntVar(0, totalHourClass, "minOfMaxLoad");

        // create variables y
        y = new MPVariable[n][maxP+1];
        for(int i = 0; i < n; i++){
            for(int k = minP; k <= maxP; k++){
                y[i][k] = solver.makeIntVar(0,1,"y[" + i + "," + k + "]");
            }
        }

        // create variable z
        z = new MPVariable[n];
        for(int i = 0; i < n; i++)
            z[i] = solver.makeIntVar(0,1,"z[" + i + "]");

        // create variable u
        //u = new MPVariable[n];
        //for(int i = 0; i < n; i++)
        //    u[i] = solver.makeIntVar(0,totalHourClass,"u[" + i + "]");
    }
    private void createConstraintAtMostOneTeacherIsAssignedToEachClass(){
        // each class is assigned to at most one teacher
        for (int i = 0; i < n; i++) {
            //MPConstraint c = solver.makeConstraint(1, 1);
            MPConstraint c = solver.makeConstraint(0, 1);
            for (int j = 0; j < m; j++) {
                c.setCoefficient(x[j][i], 1);
            }
        }

    }
    private void createConstraintMaxHourLoadTeacher(){
        // hour load of each teacher cannot exceed the maximum allowed valueOf
        for (int j = 0; j < m; j++) {
            MPConstraint c = solver.makeConstraint(0, maxHourTeacher[j]);
            for (int i = 0; i < n; i++) {
                c.setCoefficient(x[j][i], hourClass[i]);
            }
        }
    }
    private void createConstraintConflictClasses(){
        // conflict constraint
        for (int j = 0; j < m; j++) {
            for (int i1 = 0; i1 < n; i1++) {
                for (int i2 = i1 + 1; i2 < n; i2++) {
                    if (conflict[i1][i2]) {
                        MPConstraint c = solver.makeConstraint(0, 1);
                        c.setCoefficient(x[j][i1], 1);
                        c.setCoefficient(x[j][i2], 1);
                    }
                }
            }
        }
    }
    private void createConstraintChannelXZ(){
        // constraint between x and z: z[i] = \sum_{j=1..m} x[j,i]
        for(int i = 0; i < n; i++){
            MPConstraint c = solver.makeConstraint(0, 0);
            c.setCoefficient(z[i],1);
            for(int j = 0; j < m; j++)
                c.setCoefficient(x[j][i],-1);

        }
    }
    private void createConstraintChannelXY(){
        // constraint between x and y
        for(int p = minP; p <= maxP; p++){
            for(int i = 0; i < n; i++){
                for(int j = 0; j < m; j++){
                    if(priority[i][j] == p){ // y[i,p] = x[j,i]
                        MPConstraint c = solver.makeConstraint(0,0);
                        c.setCoefficient(y[i][p],1);
                        c.setCoefficient(x[j][i],-1);
                    }
                }
            }
        }

    }
    private void createConstraintObj(){
        // constraint on the objective function
        for (int j = 0; j < m; j++) {
            MPConstraint c = solver.makeConstraint(-INF, 0);
            for (int i = 0; i < n; i++) {
                c.setCoefficient(x[j][i], hourClass[i]);
            }
            c.setCoefficient(obj, -1);
        }
    }
    private void createdConstraints() {
        createConstraintAtMostOneTeacherIsAssignedToEachClass();
        createConstraintMaxHourLoadTeacher();
        createConstraintConflictClasses();
        createConstraintChannelXY();
        createConstraintChannelXZ();
        createConstraintObj();
    }
    private void createObjective1(){
        MPObjective objective = solver.objective();
        for(int i = 0; i < n; i++) {
            objective.setCoefficient(z[i], 1);
        }
        objective.setMaximization();
    }
    private void createObjective() {
        objectiveMaxNbClassAssigned = solver.objective();
        objectiveMaxNbClassAssigned.setCoefficient(obj, 1);
        objectiveMaxNbClassAssigned.setMinimization();

    }

    private boolean solvePhase1(){
        // maximize the number of class assigned
        initDatastructures();
        createSolverAndVariables();
        createdConstraints();
        createObjective1();

        // Solves.
        final MPSolver.ResultStatus resultStatus = solver.solve();

        notAssigned = new HashSet<Integer>();
        // Analyse solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            assignment = new int[n];
            for(int i = 0; i < n; i++) assignment[i] = -1;
            //System.out.println("solve, n = " + n + " m = " + m + ", objective = " + objectiveMaxNbClassAssigned.value());
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) if (x[j][i].solutionValue() > 0){
                    System.out.println("solver, x[" + i + "," + j + "] = " + x[j][i].solutionValue());
                    if (x[j][i].solutionValue() > 0) {
                        assignment[i] = j;
                    }
                }
            }
            for(int i = 0; i < n; i++) {
                System.out.println("solver z[" + i + "] = " + z[i].solutionValue());
                if (z[i].solutionValue() <= 0) {
                    notAssigned.add(i);
                }
            }
            return true;
        }
        return false;

    }

    public boolean solve() {
        if(true)
        return solvePhase1();

        initDatastructures();
        createSolverAndVariables();
        createdConstraints();
        createObjective();

        // Solves.
        final MPSolver.ResultStatus resultStatus = solver.solve();

        // Analyse solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            assignment = new int[n];
            System.out.println("solve, n = " + n + " m = " + m);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.println("solver, x[" + i + "," + j + "] = " + x[j][i].solutionValue());
                    if (x[j][i].solutionValue() > 0) {
                        assignment[i] = j;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public int[] getSolutionAssignment() {
        return assignment;
    }

}
