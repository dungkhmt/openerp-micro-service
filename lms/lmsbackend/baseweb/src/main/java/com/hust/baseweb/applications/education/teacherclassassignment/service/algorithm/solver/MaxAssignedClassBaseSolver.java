package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver;

import com.hust.baseweb.applications.education.teacherclassassignment.service.MapDataInput;

import java.util.HashSet;

/**
 * OK
 */
public abstract class MaxAssignedClassBaseSolver {

    // Input
    protected final int n; // number of classes

    protected final int m; // number of teachers

    protected final HashSet<Integer>[] D; // D[i] is the set of teachers that can be assigned to class i

    protected final boolean[][] conflict;

    protected final double[] hourClass;

    protected final double[] maxHourTeacher;

    protected final int[][] preAssignments;

    // Data structures for solution
    protected int[] assignments; // assignment[i] is the teacher assigned to class i

    protected final HashSet<Integer> notAssignedClasses = new HashSet<>();

    public MaxAssignedClassBaseSolver(MapDataInput input) {
        this.n = input.getN();
        this.m = input.getM();
        this.D = input.getD();
        this.conflict = input.getConflict();
        this.hourClass = input.getHourClass();
        this.maxHourTeacher = input.getMaxHourTeacher();
        this.preAssignments = input.getPreAssignment();
    }

    /**
     * OK
     *
     * @return
     */
    public HashSet<Integer> getNotAssignedClasses() {
        return notAssignedClasses;
    }

    /**
     * OK
     *
     * @return
     */
    public int[] getAssignments() {
        return assignments;
    }


    /**
     * OK
     *
     * @return
     */
    public abstract double getObjectiveValue();

    public abstract boolean solve();
}
