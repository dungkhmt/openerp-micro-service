package com.hust.baseweb.utils;

import java.io.PrintWriter;

public class RandomUserLoginGenerator {
    public static String std(String prefix, int n, int len){
        String res = n + "";
        while(res.length() < len - prefix.length()) {
            res = "0" + res;
        }
        res = prefix + res;
        return res;
    }
    public void gen(String filename, String prefix, int n, int len){
        try{
            PrintWriter out = new PrintWriter(filename);
            for(int i = 1; i <= n; i++){
                String uid = std(prefix,i,len);
                out.println(uid + "\t" + uid);
            }
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        System.out.println("start");
        RandomUserLoginGenerator app = new RandomUserLoginGenerator();
        app.gen("C:\\DungPQ\\project\\openerp-microservices\\tmp\\users.txt",
                                                                    "e",1000,10);
    }
}
