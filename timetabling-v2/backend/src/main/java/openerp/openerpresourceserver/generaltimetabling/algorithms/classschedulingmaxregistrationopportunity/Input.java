package openerp.openerpresourceserver.generaltimetabling.algorithms.classschedulingmaxregistrationopportunity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Input {
    int S = 6;// time-slot are 1, 2, . . ., S
    int n;// number of classes
    int m;// number of courses

    int[] d; // duration (so tiet trong 1 tuan)
    int[] c; // c[i] is the course of class i
    List<Integer>[] classOfCourse;
    List<Integer>[] D;// D[i] is the domain (set of time slot for scheduling class i)

    public void loadData(String filename){
        try{
            Scanner in = new Scanner(new File(filename));
            n = in.nextInt(); m = in.nextInt();
            d = new int[n+1];
            c = new int[n+1];
            for(int i = 1; i <= n; i++) d[i] = in.nextInt();
            classOfCourse = new ArrayList[m+1];
            for(int j = 1; j <= m; j++) classOfCourse[j] = new ArrayList<Integer>();
            for(int k = 1; k <= m; k++){
                int sz = in.nextInt();
                for(int j = 1; j <= sz; j++){
                    int cls = in.nextInt(); c[cls] = k;
                    classOfCourse[k].add(cls);
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void printData(){
        for(int i = 1; i <= n; i++) System.out.println("d[" + i + "] = " + d[i]);

        for(int i = 1; i <= m; i++){
            System.out.print("Course " + i + ": ");
            for(int j: classOfCourse[i]) System.out.print(j + " ");
            System.out.println();
        }
    }
    public static void main(String[] args){
        try{

        }catch (Exception e){

        }
    }
}
