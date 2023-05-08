package com.hust.baseweb.applications.education.quiztest.utils;

import java.util.Random;

public class Utils {
    public static String stdCode(int i, int len){
        String s = "" + i;
        while(s.length() < len){
            s = "0" + s;
        }
        return s ;
    }
    public static String genRandomPermutation(int n){
        Random R = new Random();
        int[] a=  new int[n];
        for(int i = 0; i < n; i++) a[i] = i;
        for(int k = 1; k <= n; k++){
            int i = R.nextInt(n);
            int j = R.nextInt(n);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        String s = "";
        for(int i = 0; i < n; i++)
            s = s + a[i];
        return s;
    }
    public static boolean checkPermutation(int[] p){
        // return true if p is a permutation of 0, 1, 2, ..., p.length - 1
        if(p == null || p.length == 0) return false;
        int n = p.length;
        int[] b = new int[n];
        for(int i = 0; i < n; i++) b[i] = 0;
        for(int i = 0; i < p.length; i++){
            if(p[i] >= n) return false;
            if(b[p[i]] == 1) return false;// repeated value
            b[p[i]] = 1;
        }
        return true;
    }
    public static boolean checkPermutation(String p){
        // return true if p is a permutation of 0, 1, 2, ..., p.length - 1
        if(p == null || p.equals("")) return false;
        int n = p.length();
        int[] b = new int[n];
        for(int i = 0; i < n; i++) b[i] = 0;
        for(int i = 0; i < p.length(); i++){
            int v = p.charAt(i) - '0';
            if(v >= n) return false;
            if(b[v] == 1) return false;// repeated value
            b[v] = 1;
        }
        return true;
    }
    public static int[] genSequence(String permutation, int len){
        int n = permutation.length();
        int buffer_len = 0;
        while(buffer_len <= len){
            buffer_len += n;
        }
        len = buffer_len;
        int[] seq = new int[len];
        //int[] seq = new int[buffer_len];
        if(!checkPermutation(permutation)){
            for(int i = 0; i < len; i++) seq[i] = i;
            return seq;
        }

        for(int i = 0; i < n; i++){
            //if(i >= len) break;
            seq[i] = permutation.charAt(i) - '0';//Integer.valueOf(permutation.charAt(i) + "");
        }
        //System.out.println("Utils.genSequence, n = " + n + " len = " + len + " permutation = " + permutation);
        for(int i = n; i < len; i++){
            seq[i] = seq[i-n] + n;
            //System.out.println("Utils.genSequence, continue seq[" + i + "] = " + seq[i]);
        }
        return seq;
    }
    public static void swap(int[] a, int i, int j){
        int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
    }
    public static int[] getPermutationBasedOnSeq(int seq, int n){
        // return a permutation of 0, 1, 2, . . ., n-1 based on seq (deterministic)
        if(n == 0) return null;
        int[] p = new int[n];
        for(int i = 0; i < n; i++) p[i] = i;
        int i = 0;
        for(int k = 1; k <= seq; k++){
            swap(p,i,n-i-1);
            i = i + 1;
            if(i >= n/2) break;
        }
        i = 0;
        for(int k = 1; k <= seq; k++){
            if(i >= n-1) break;
            swap(p,i,i+1);
            i = i + 2;
        }
        return p;
    }
    public static void main(String[] args){
        String code = stdCode(123,5);
        System.out.println("standard code = " + code);

        String p = Utils.genRandomPermutation(10);
        System.out.println(" p = " + p);

        int[] a = Utils.getPermutationBasedOnSeq(6,10);
        for(int i = 0; i < a.length; i++) System.out.print(a[i] + " "); System.out.println();


    }

}
