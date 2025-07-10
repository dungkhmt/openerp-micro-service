package openerp.openerpresourceserver.generaltimetabling.algorithms.classschedulingmaxregistrationopportunity;

import java.util.List;

public class CountRegistrationOpportunitySolver {
    boolean[][] conflict;
    int n;
    int m;
    List<Integer>[] classOfCourse;
    int[] x;// x[i] is on of the class of course i selected
    int cnt;// number of solutions

    public CountRegistrationOpportunitySolver(int n, int m, List<Integer>[] classOfCourse, boolean[][] conflict){
        this.conflict = conflict;
        this.classOfCourse = classOfCourse;
        this.n = n; this.m = m;

    }
    public void solution(){
        cnt++;
    }
    public boolean check(int v, int k){
        for(int i = 1; i <= k-1; i++){
            if(conflict[x[i]][v]) return false;
        }
        return true;
    }
    public void tryX(int k){
        for(int v: classOfCourse[k]){
            if(check(v,k)) {
                x[k] = v;
                if (k == m) solution();
                else {
                    tryX(k + 1);
                }
            }
        }
    }
    public void solve(){
        x = new int[m+1];
        cnt = 0;
        tryX(1);

    }
    public static void main(String[] args){

    }
}
