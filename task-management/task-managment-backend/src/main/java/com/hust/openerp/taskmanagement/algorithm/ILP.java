package com.hust.openerp.taskmanagement.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ILP {
	static {
        Loader.loadNativeLibraries();
    }
	
    private final int numUsers;
    private final int numSessions;
    private final List<List<Integer>> userChoices;
    private final UserSessionMapper mapper;

    public ILP(Map<String, List<UUID>> userChoicesMap) {
        this.mapper = new UserSessionMapper(userChoicesMap);
        this.numUsers = mapper.getNumUsers();
        this.numSessions = mapper.getNumSessions();
        this.userChoices = mapper.getUserChoices();
    }

    public static class ILPResult {
        private final Map<String, UUID> assignment;
        private final int minimalMaxLoad;

        public ILPResult(Map<String, UUID> assignment, int minimalMaxLoad) {
            this.assignment = assignment;
            this.minimalMaxLoad = minimalMaxLoad;
        }

        public Map<String, UUID> getAssignment() { return assignment; }
        public int getMinimalMaxLoad() { return minimalMaxLoad; }
    }

    public ILPResult schedule() {
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) throw new RuntimeException("Could not create SCIP solver");

        MPVariable[][] x = new MPVariable[numUsers][numSessions];
        for (int u = 0; u < numUsers; u++) {
            for (int s : userChoices.get(u)) {
                x[u][s] = solver.makeBoolVar("x_" + u + "_" + s);
            }
        }

        MPVariable C = solver.makeIntVar(1, numUsers, "C");

        // Constraint: Each user assigned to exactly one session
        for (int u = 0; u < numUsers; u++) {
            MPConstraint ct = solver.makeConstraint(1, 1, "user_" + u);
            for (int s : userChoices.get(u)) ct.setCoefficient(x[u][s], 1);
        }

        // Constraint: Load per session <= C
        for (int s = 0; s < numSessions; s++) {
            MPConstraint ct = solver.makeConstraint(Double.NEGATIVE_INFINITY, 0, "session_" + s);
            for (int u = 0; u < numUsers; u++) {
                if (userChoices.get(u).contains(s)) ct.setCoefficient(x[u][s], 1);
            }
            ct.setCoefficient(C, -1); // sum(x[u][s]) - C <= 0
        }

        // Objective: Minimize C
        MPObjective objective = solver.objective();
        objective.setCoefficient(C, 1);
        objective.setMinimization();

        MPSolver.ResultStatus status = solver.solve();
        if (status != MPSolver.ResultStatus.OPTIMAL) {
            throw new IllegalStateException("No optimal solution found");
        }

        Map<String, UUID> assignment = new HashMap<>();
        for (int u = 0; u < numUsers; u++) {
            for (int s : userChoices.get(u)) {
                if (x[u][s].solutionValue() > 0.5) {
                    assignment.put(mapper.getUserFromIndex(u), mapper.getSessionFromIndex(s));
                    break;
                }
            }
        }

        int minimalMaxLoad = (int) C.solutionValue();
        return new ILPResult(assignment, minimalMaxLoad);
    }
}