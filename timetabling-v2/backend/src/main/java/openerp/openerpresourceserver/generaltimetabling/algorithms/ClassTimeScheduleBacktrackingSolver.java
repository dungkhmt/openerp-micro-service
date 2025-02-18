package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Log4j2
public class ClassTimeScheduleBacktrackingSolver {
    private int n;// number of sessions
    private int p; //number of classes
    private int[] durations;
    private List<Integer>[] domains;
    private boolean[][] conflicts;
    private int[] x;// x[i] is the time-slot for class i
    private int[] sol;
    private boolean found;
    private double timeLimit;// time limit in seconds
    private double t_start;
    private HashMap<Integer, Integer> scheduleMap; // session to class map
    private int[] dayPeriods; // store the remain periods of a day(default is 6)
    public
    ClassTimeScheduleBacktrackingSolver(int n, int[] durations, List<Integer>[] domains, List<int[]> C, double timeLimit) {
        this.n = n;
        this.durations = durations;
        this.domains = domains;
        this.timeLimit = timeLimit;
        t_start = System.currentTimeMillis();
        this.conflicts = new boolean[n][n];
        for(int i = 0;i < n; i++)
            for(int j = 0; j < n; j++)
                this.conflicts[i][j] = false;
        for(int[] p: C){
            this.conflicts[p[0]][p[1]] = true;
            this.conflicts[p[1]][p[0]] = true;
        }
    }
    private boolean check(int v, int k){
        for(int i = 0; i < k; i++){
            if(conflicts[i][k]){
                boolean c = x[i] + durations[i] <= v || v + durations[k] <= x[i];
                if(!c) return false;
            }
        }
        return true;
    }
    private void solution(){
        for(int i = 0; i < n; i++) sol[i] = x[i];
        found = true;
        for(int i = 0; i< n; i++){
            log.info("BacktrackingSolver solution x[" + i + "] = " + x[i]);
        }

    }
    private void Try(int k){
        double t= System.currentTimeMillis() - t_start;
        log.info("time = " + t + " -> Try(" + k + "/" + n + ") domain = " + domains[k].size());
        if(t > timeLimit) return;
        if(found) return;

        for(int v : domains[k]){
            if(check(v,k)){
                x[k] = v;
                if(k == n-1) solution();
                else{
                    Try(k+1);
                }
            }
        }
    }


    public void solve(){
        //this.timeLimit = timeLimit;
        log.info("solve...timeLimit = " + timeLimit);
        found = false;
        x = new int[n];
        sol = new int[n];
        t_start = System.currentTimeMillis();
        Try(0);
    }
    public int[] getSolution(){
        return sol;
    }
    public boolean hasSolution(){
        return found;
    }
    public static void main(String[] args){
        int n = 10;
        List<int[]> C = new ArrayList<int[]>();
        //C.add(new int[]{1,0});
        //C.add(new int[]{0,3});
        //C.add(new int[]{1,4});
        //C.add(new int[]{1,7});
        //C.add(new int[]{1,9});
        //C.add(new int[]{2,4});
        //C.add(new int[]{2,5});
        int[] durations = {4,2,2,4,3,3,2};
        //int[] durations = {4,2,2,4,3,3,2,4,4,3};
        //int[] durations = {4,2,2,4,3,3,2,4,4,3,4,2,2,4,3,3,2,4,4,3};
        n = durations.length;
        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++)
                C.add(new int[]{i,j});
        }
        List<Integer> D = new ArrayList<>();
        int KIP = 0;// KIP = 0 (sang); KIP = 1 (chieu)
        for(int s = 0; s < 60; s++) D.add(s);
        List<Integer>[] domains = new List[n];
        for(int i = 0; i< n; i++){
            domains[i] = new ArrayList<>();
            for(int day = 0; day < 5; day++){
                for(int start = 0; start <= 6 - durations[i];start++){
                    int s = 12*day+6*KIP + start;
                    domains[i].add(s);
                }
            }
        }
        ClassTimeScheduleBacktrackingSolver app = new ClassTimeScheduleBacktrackingSolver(n,durations,domains,C, 2);
        app.solve();
        if(!app.hasSolution()){
            log.info("NO SOLUTION");
        }else {
            int[] solution = app.getSolution();
            log.info("FOUND SOLUTION");
            for (int i = 0; i < n; i++) {
                int day = solution[i] / 12;
                int t1 = solution[i] - day * 12;
                int K = t1 / 6; // kip
                int tietBD = t1 - 6 * K;
                log.info("class[" + i + "] is assigned to slot " + solution[i] + "(" + day + "," + K + "," + tietBD + ")");
            }
        }
    }
}
