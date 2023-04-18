package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.*;

public class Test {

    private Set<Integer> infeasibleClasses;

    private int numVertices;

    private List<Integer>[] graph;

    public Test() {
        infeasibleClasses = new HashSet<>();
    }

    private int checkClassesAssign2OnlyOneTeacher() {
        System.out.println("Solving by greedy");
        // Greedy Algorithm.
        while (true) {
            // Short is important, if use short, graph[vertex].remove(removedVertex) will not work
            // because removedVertex is an index, not object.
            Integer removedVertex = -1;
            short max = 0;

            for (Integer i = 0; i < graph.length; i++) {
                if (!infeasibleClasses.contains(i)) {
                    if (graph[i].size() > max) {
                        max = (short) graph[i].size();
                        removedVertex = i;
                    }
                }
            }

            if (0 == max) {
                break;
            } else {
                infeasibleClasses.add(removedVertex);
                System.out.println("Remove class: " + removedVertex);

                for (int i = 0; i < graph[removedVertex].size(); i++) {
                    int vertex = graph[removedVertex].get(i);
                    graph[vertex].remove(removedVertex);
                }
            }
        }

        return numVertices - infeasibleClasses.size();
    }

    private int solveBySolver() {
        System.out.println("Solving by solver");

        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("SCIP");
        MPVariable[] x = new MPVariable[numVertices];

        for (int i = 0; i < x.length; i++) {
            x[i] = solver.makeIntVar(0, 1, "x[" + i + "]");
        }

        for (int i = 0; i < graph.length; i++) {
            for (Integer vertex : graph[i]) {
                MPConstraint conflict = solver.makeConstraint(0, 1, "conflict_" + i + "_" + vertex);
                conflict.setCoefficient(x[i], 1);
                conflict.setCoefficient(x[vertex], 1);
            }
        }

        MPVariable sum = solver.makeIntVar(0, 1000, "sum");
        MPConstraint totalVertices = solver.makeConstraint(0, 0);

        for (int i = 0; i < x.length; i++) {
            totalVertices.setCoefficient(x[i], 1);
        }

        totalVertices.setCoefficient(sum, -1);

        MPObjective objective = solver.objective();
        objective.setCoefficient(sum, 1);
        objective.setMaximization();

        final MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.print("Keep: ");
            for (int i = 0; i < x.length; i++) {
                if ((int) (x[i].solutionValue() + 0.25) == 1) {
                    System.out.print(i + ", ");
                }
            }

            return (int) (objective.value() + 0.25);
        } else {
            System.err.println("This problem not have optimal solution");
            return -1;
        }
    }

    private void genGraph() {
        Random generator = new Random();
        numVertices = generator.nextInt(9) + 2; // 2-100

        graph = new ArrayList[numVertices];

        for (int i = 0; i < graph.length; i++) {
            graph[i] = new ArrayList<>();
        }

        for (Integer i = 0; i < graph.length; i++) {
            int m = -1;

            while (m < graph[i].size()) {
                m = generator.nextInt(10); // 0-100
            }

            Set<Integer> exited = new HashSet<>();
            exited.add(i);

            graph[i].forEach(vertex -> {
                exited.add(vertex);
            });

            for (int j = graph[i].size(); j < m; j++) {
                Integer vertex = generator.nextInt(numVertices); // 0-(numVertices-1)

                if (!exited.contains(vertex)) {
                    if (vertex > i) {
                        exited.add(vertex);
                        graph[i].add(vertex);
                        graph[vertex].add(i);
                    }
                }
            }
        }

        for (int j = 0; j < graph.length; j++) {
            System.out.println(graph[j].toString());
        }

        numVertices = 8;
        graph = new ArrayList[numVertices];

        graph[0] = new ArrayList<>(Arrays.asList(1, 6, 2));
        graph[1] = new ArrayList<>(Arrays.asList(0, 4, 6, 2, 3, 7));
        graph[2] = new ArrayList<>(Arrays.asList(0, 1, 4));
        graph[3] = new ArrayList<>(Arrays.asList(1, 5, 6));
        graph[4] = new ArrayList<>(Arrays.asList(1, 2, 6, 5));

        graph[5] = new ArrayList<>(Arrays.asList(3, 4, 7));
        graph[6] = new ArrayList<>(Arrays.asList(0, 1, 3, 4));
        graph[7] = new ArrayList<>(Arrays.asList(1, 5));


        for (Integer i = 0; i < graph.length; i++) {
            for (Integer vertex : graph[i]) {
                if (!graph[vertex].contains(i)) {
                    System.out.println("Conflict: " + i + ", " + vertex);
                }
            }
        }
    }

    public static void main(String[] args) {
        /*for (int i = 0; i < 10000; i++) {
            System.out.println("Iteration: " + i);

            Test t = new Test();
            t.genGraph();

            int result = t.solveBySolver();
            int result2 = t.checkClassesAssign2OnlyOneTeacher();

            if (result != result2) {
                System.out.println("NOT CORRECT");
                System.out.println("SOLVER = " + result);
                System.out.println("GREEDY = " + result2);
                break;
            }

        }*/
        System.out.println((int) 1.99);
    }
}
