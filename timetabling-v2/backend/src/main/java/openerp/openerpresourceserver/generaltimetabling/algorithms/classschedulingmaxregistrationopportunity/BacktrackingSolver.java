package openerp.openerpresourceserver.generaltimetabling.algorithms.classschedulingmaxregistrationopportunity;

import java.util.ArrayList;
import java.util.List;

public class BacktrackingSolver {
    Input I;
    int n;// number of classes
    int m;// number of courses

    int[] d; // duration (so tiet trong 1 tuan)
    int[] c; // c[i] is the course of class i
    List<Integer>[] classOfCourse;
    List<Integer>[] D;// D[i] is the domain (set of time slot for scheduling class i)
    int[] x;// x[i] is the start time-slot for class i, end time-slot = x[i] + d[i] - 1

    boolean[][] conflict; // conflict[i][j] = true if class i and j overlap

    public void computeDomains(){
        D = new ArrayList[n+1];
        for(int i = 1; i <= n; i++){
            D[i] = new ArrayList<Integer>();
            for(int s = 1; s <= I.S - d[i] + 1; s++)
                D[i].add(s);
        }
    }
    public boolean check(int v, int k){
        return true;
    }

    public void solution(){
        for(int i = 1; i <= n; i++){
            for(int j = i+1; j <= n; j++){
                if(x[i] + d[i] <= x[j] || x[j] + d[j] <= x[i]){
                    conflict[i][j] = false; conflict[j][i] = false;
                }else{
                    conflict[i][j] = true; conflict[j][i] = true;
                }
            }
        }
        CountRegistrationOpportunitySolver S = new CountRegistrationOpportunitySolver(n,m,classOfCourse,conflict);
        S.solve();
        for(int i = 1; i <= n; i++) System.out.print(x[i] + " ");
        System.out.println(" obj = " + S.cnt);

    }
    public void tryX(int k){
        for(int v: D[k]){
            if(check(v,k)){
                x[k] = v;
                if(k == n) solution();
                else{
                    tryX(k+1);
                }
            }
        }
    }
    public void solve(){
        x = new int[n+1];
        computeDomains();
        conflict = new boolean[n+1][n+1];
        tryX(1);
    }
    public BacktrackingSolver(Input I){
        this.I = I;
        n = I.n; m = I.m; d = I.d; c = I.c; this.classOfCourse = I.classOfCourse;
    }
    public static void main(String[] args){
        Input I = new Input();
        I.loadData("data/test.txt");
        I.printData();
        BacktrackingSolver solver = new BacktrackingSolver(I);
        solver.solve();
    }
}
