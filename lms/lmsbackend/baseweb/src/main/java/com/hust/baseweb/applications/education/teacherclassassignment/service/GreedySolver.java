package com.hust.baseweb.applications.education.teacherclassassignment.service;

import java.io.PrintWriter;
import java.util.HashSet;

public class GreedySolver {
    private MapDataInput I;
    private long timeLimit;

    private int n;// number of classes
    private int m;// number of teachers
    private HashSet<Integer>[] D;// D[i] is the set of teachers that can be assigned to class i
    private boolean[][] conflict;
    private int[][] priority;
    private double[] hourClass;
    private double[] maxHourTeacher;
    private int[][] preAssignment;

    private int[] assignment;

    public GreedySolver(MapDataInput I){
        this.I = I;
        this.n = I.n;
        this.m = I.m;
        this.D = I.D;
        this.priority = I.priority;
        this.conflict = I.conflict;
        this.hourClass = I.hourClass;
        this.maxHourTeacher = I.maxHourTeacher;
        this.preAssignment = I.getPreAssignment();

    }
    public int[] getAssignment(){
        return assignment;
    }
    private void greedy1(){
        double[] load = new double[m];
        assignment = new int[n];
        for(int i = 0; i < n; i++)
            assignment[i] = -1;

        for(int j = 0; j < m; j++) load[j] = 0;
        HashSet<Integer>[] C = new HashSet[m];// C[j] is set of assigned class to teacher j
        for(int j = 0; j < m; j++) C[j] = new HashSet();
        HashSet<Integer> cand = new HashSet();
        for(int i = 0; i < n; i++) cand.add(i);

        while(cand.size() > 0){
            int sel_i = -1; int sel_j = -1;
            int minD = Integer.MAX_VALUE;
            for(int i: cand){
                for(int j: D[i]){
                    boolean ok = true;
                    for(int k: C[j]){
                        if(conflict[i][k]){
                            ok = false; break;
                        }
                    }
                    if(!ok)continue;
                    if(hourClass[i] + load[j] > maxHourTeacher[j]) continue;

                    // accept (i <- j)
                    if(minD > D[i].size()){
                        minD = D[i].size(); sel_i = i; sel_j = j;
                    }
                }
            }
            if(sel_i != -1){
                System.out.println("Assign " + sel_i + " <- " + sel_j);
                assignment[sel_i] = sel_j;
                load[sel_j] += hourClass[sel_i];
                C[sel_j].add(sel_i);
                cand.remove(sel_i);
            }else{
                System.out.println("BREAK");
                break;

            }
        }
    }
    public boolean solve(String fo, long timeLimit){
        this.timeLimit = timeLimit;
        try{
            greedy1();
            PrintWriter out = new PrintWriter(fo);
            for(int i = 0; i < n; i++)
                out.println(i + " " + assignment[i]);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args){
        MapDataInput input = new MapDataInput();
        String fi = "D:/tmp/data-bca/3.txt";
        String fo = "D:/tmp/data-bca/3-out-greedy.txt";
        //String fi = "D:/tmp/data-bca/input/bca-1.txt";
        //input.genRandom(fi,500,50);
        input.loadDataFromPlanFile(fi);
        GreedySolver solver= new GreedySolver(input);
        //solver.solve("MAXCLASS");
        boolean ok = solver.solve(fo,1000);

        input.checkSolution(fi,fo);

    }
}

