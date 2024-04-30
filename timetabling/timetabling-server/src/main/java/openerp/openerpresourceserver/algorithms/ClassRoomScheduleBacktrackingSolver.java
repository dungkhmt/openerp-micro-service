package openerp.openerpresourceserver.algorithms;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
@Log4j2

public class ClassRoomScheduleBacktrackingSolver {
    int n;// number of classes
    int m;// number of rooms
    boolean[][] conflict; // conflict[i][j] = true: class i and j are scheduled at overlap time-slot
    // -> must be assigned to different rooms
    int[] c;// c[i] is the number of places
    List<Integer>[] D;// D[i] is the list of rooms can be assigned to class i
                      // rooms having more places than students of class

    int[] x;// x[k] is the room assigned to class k
    int[] y;// y[k] is the number of classes assigned to room k
    int load;// load = sum_{k in rooms} y[k]*c[k]
    int[] sol;// store best solution found

    int f;
    int f_best;
    double timeLimit; // in milli seconds
    double t0;// start time-point
    public ClassRoomScheduleBacktrackingSolver(int n, int m, boolean[][] conflict, int[] c, List[] D) {
        this.n = n;
        this.m = m;
        this.conflict = conflict;
        this.c = c;
        this.D = D;
    }

    public void setTimeLimit(double timeLimit){
        this.timeLimit = timeLimit;
    }

    private boolean check(int v, int k){
        for(int i = 0; i < k; i++){
            if(conflict[i][k] && x[i] == v) return false;
        }
        return true;
    }
    private void solution(){
        if(load < f_best){
            f_best = load;
            for(int i = 0; i < n; i++) sol[i] = x[i];
            log.info("update best " + f_best);
        }
    }
    private void Try(int k){// try values of x[k]
        double t = System.currentTimeMillis() - t0;
        if(t > timeLimit) return;
        for(int v: D[k]){
            if(check(v,k)){
                x[k] = v;
                y[v]++;
                load += c[v]; // room v is used one more time
                if(k == n-1) solution();
                else{
                    Try(k+1);
                }
                load -= c[v];
                y[v]--;
            }
        }
    }
    public void solve(){
        x = new int[n];
        y = new int[m];
        sol = new int[n];
        load = 0;
        f_best = 10000000;
        t0 = System.currentTimeMillis();
        Try(0);
    }
    public int[] getSolution(){
        return sol;
    }
    public void printSolution(){
        for(int i = 0; i < n; i++) x[i] = sol[i];
        for(int i = 0; i < n; i++) log.info("x[" + i + "] = " + x[i]);
        for(int r = 0; r < m; r++) y[r] = 0;
        for(int i = 0; i < n; i++) y[x[i]]++;
        for(int r = 0; r < m; r++){
            String C = "";
            for(int i = 0; i < n; i++) if(x[i] == r) C = C + i + ",";
            log.info("room " + r + " used " + y[r] + " cap = " + c[r] + ": " + C);
        }
    }
    public static void main(String[] args ){
        int[] c = {50,50,50,80,80,80,120,120,120};// capacity of rooms
        int[] d = {45,45,49,50,70,70,100,110,115,120,120,120,120};// nbr student of classes
        int n = d.length;// number of classes;
        int m = c.length;// number of rooms
        List[] D = new List[n];
        for(int i = 0; i < n; i++){
            D[i] = new ArrayList<Integer>();
            for(int r = 0;r < m; r++)
                if(c[r] >= d[i]) D[i].add(r);
        }
        List<int[]> C = new ArrayList();
        C.add(new int[]{0,1});
        C.add(new int[]{0,2});
        C.add(new int[]{6,7});

        C.add(new int[]{0,4});
        C.add(new int[]{1,6});
        boolean[][] conflict = new boolean[n][n];
        for(int[] p: C){
            conflict[p[0]][p[1]] = true;
        }
        ClassRoomScheduleBacktrackingSolver app =
                new ClassRoomScheduleBacktrackingSolver(n,m,conflict,c,D);
        app.setTimeLimit(2000);// time limit 2 seconds
        app.solve();
        app.printSolution();
    }
}
