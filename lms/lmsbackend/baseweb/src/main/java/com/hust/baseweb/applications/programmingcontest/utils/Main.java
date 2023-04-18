package com.hust.baseweb.applications.programmingcontest.utils;
import java.util.*;
public class Main {
    public int C(int k, int n){
        if(k == 0 || k == n) return 1;
        return C(k-1,n-1) + C(k,n-1);
    }
    public static void main(String[] args){
        Main app= new Main();

        try{
            int k,n;
            Scanner in = new Scanner(System.in);
            k = in.nextInt();
            n = in.nextInt();
            System.out.print(app.C(k,n));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
