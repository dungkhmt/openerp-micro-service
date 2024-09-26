package com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver;

import com.hust.baseweb.applications.education.teacherclassassignment.model.SolverConfig;

import java.util.HashSet;

public interface ISolver {

    /**
     * OK
     *
     * @return
     */
    int[] getAssignment();

    /**
     * OK
     *
     * @return
     */
    HashSet<Integer> getNotAssignedClasses();

    boolean solve(SolverConfig config);

}
