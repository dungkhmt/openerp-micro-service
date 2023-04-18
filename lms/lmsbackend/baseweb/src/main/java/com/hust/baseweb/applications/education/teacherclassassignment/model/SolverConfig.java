package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SolverConfig {

    public enum Solver {
        ORTOOLS, CPLEX
    }

    public enum Model {
        MIP, CP
    }

    public enum Objective {
        SCORES, PRIORITY, WORKDAYS, LOAD_BALANCING_DURATION_CONSIDERATION
    }

    private Solver solver;

    private Model model;

    private Objective objective;

}
