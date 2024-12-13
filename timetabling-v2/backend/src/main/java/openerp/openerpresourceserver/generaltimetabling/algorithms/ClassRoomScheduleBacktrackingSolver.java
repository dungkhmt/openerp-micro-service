package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Log4j2
@Getter
public class ClassRoomScheduleBacktrackingSolver {
    int n;// number of sessions
    int p; // number of classes
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
    HashMap<Integer,Integer> scheduleMap; // mapping from session to class idx
    int[] roomAssigns; // rA[k] = i: class k is assign room i


    int f;
    int f_best;
    double timeLimit; // in milli seconds
    double t0;// start time-point
    public ClassRoomScheduleBacktrackingSolver(int n, int m, int p, HashMap<Integer, Integer> scheduleMap, boolean[][] conflict, int[] c, List[] D, int timeLimit) {
        this.n = n;
        this.m = m;
        this.p = p;
        this.conflict = conflict;
        this.scheduleMap = scheduleMap;
        this.c = c;
        this.D = D;
        this.timeLimit = timeLimit;
    }

    public void setTimeLimit(double timeLimit){
        this.timeLimit = timeLimit;
    }

    private boolean check(int v, int k){
        for(int i = 0; i < k; i++){
            if((conflict[i][k] && x[i] == v) && ( roomAssigns[scheduleMap.get(k)] != -1 && roomAssigns[scheduleMap.get(k)] != v)) return false;
        }
        return true;
    }
    private void solution(){
        if(load < f_best){
            f_best = load;
            for(int i = 0; i < n; i++) sol[i] = x[i];
            log.info("update best " + f_best);
            for (int i = 0; i < p; i++) {
                log.info("class " + i + " use room " + roomAssigns[i]);
            }
        }
    }
    private void Try(int k){// try values of x[k]
        double t = System.currentTimeMillis() - t0;
        if(t > timeLimit) return;
        if (roomAssigns[scheduleMap.get(k)] == -1) {
            for(int v: D[k]){
                if(check(v,k)){
                    x[k] = v;
                    roomAssigns[scheduleMap.get(k)] = v;
                    y[v]++;
                    load += c[v]; // room v is used one more time
                    if(k == n-1) solution();
                    else{
                        Try(k+1);
                    }
                    roomAssigns[scheduleMap.get(k)] = -1;
                    load -= c[v];
                    y[v]--;
                }
            }
        } else {
            int v = roomAssigns[scheduleMap.get(k)];
            if (check(v,k)) {
                x[k] = v;
                y[v]++;
                load += c[v]; // room v is used one more time
                if(k == n-1) solution();
                else{
                    Try(k+1);
                }
                roomAssigns[scheduleMap.get(k)] = -1;
                load -= c[v];
                y[v]--;
            }
        }
    }
    public void solve(){
        x = new int[n];
        y = new int[m];
        sol = new int[n];
        roomAssigns = new int [p];
        for (int i = 0; i < p; i++) {
            roomAssigns[i] = -1;
        }
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
        HashMap<Integer, Integer> scheduleMap = new HashMap<>();

        ClassRoomScheduleBacktrackingSolver app =
                new ClassRoomScheduleBacktrackingSolver(n,m, n, scheduleMap, conflict,c,D, 2000);

        app.solve();
        app.printSolution();
    }
}
