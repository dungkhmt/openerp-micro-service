package openerp.openerpresourceserver.generaltimetabling.algorithms;

import java.util.ArrayList;
import java.util.List;

public class ClassTimeScheduleBacktrackingMaxScheduledClassSolver {
    int n;// number of classes;
    int[] durations;// durations[i] is the number of slots of class i
    List<Integer>[] domains;
    boolean[][] conflict;
    int[] x;// solution reprentation
    int best;
    private boolean check(int v, int k){
        for(int i = 0; i <= k-1; i++) if(conflict[i][k]){
            if(!(x[i] + durations[i] <= v || v + durations[k] <= x[i]))
                return false;
        }
        return true;
    }
    private void Try(int k){
        for(int v: domains[k]){
            if(check(v,k)){
                x[k] = v;
                if(best < k) best = k;
                if(k < n) Try(k+1);
            }
        }
    }
    public static void main(String[] args){
        int n = 6;
        List<int[]> conflict = new ArrayList();
        conflict.add(new int[]{0,1});
        conflict.add(new int[]{0,2});
        conflict.add(new int[]{0,4});
        conflict.add(new int[]{1,5});
        int[] durations = {2,4,4,4,2,3};
        List<Integer>[] domains = new List[n];
        for(int i = 0; i < n; i++){
            domains[i] = new ArrayList<Integer>();
            for(int j = 0; j < 12; j++) domains[i].add(j);
        }

    }
}
