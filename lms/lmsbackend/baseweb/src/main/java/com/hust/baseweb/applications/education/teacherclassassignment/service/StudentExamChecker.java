package com.hust.baseweb.applications.education.teacherclassassignment.service;

import java.io.File;
import java.util.Random;

public class StudentExamChecker {
    int[] code = {20173020,
                  20183592,
                  20183507,
                  20183649,
                  20172998,
                  20170121,
                  20173320,
                  20183608,
                  20180010,
                  20173178,
                  20173455,
                  20173264,
                  20183633,
                  20183500,
                  20173122,
                  20183568,
                  20170115,
                  20173249,
                  20172945,
                  20183522,
                  20173394,
                  20183504,
                  20183587,
                  20183515,
                  20183648,
                  20173393,
                  20183660,
                  20183527,
                  20183578,
                  20183550,
                  20173275,
                  20173221,
                  20173141,
                  20183567,
                  20173313,
                  20183549,
                  20183490
    };
    String rootDir = "D:\\teaching\\soict\\toi-uu-va-lap-ke-hoach\\HK2020-2021-2-124193\\thi-cuoi-ky\\";

    public void createdFolders(){
        try{
            for(int i = 0; i < code.length; i++) {
                String dir = rootDir + "//" + code[i];

                File file = new File(dir);

                // true if the directory was created, false otherwise
                if (file.mkdirs()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }

                String dirDapan = dir + "//dap-an";
                file = new File(dirDapan);
                if (file.mkdirs()) {
                    System.out.println("Directory " + dirDapan + " is created!");
                } else {
                    System.out.println("Failed to create directory! " + dirDapan);
                }
                String dirSV = dir + "//ketquasinhvien";
                file = new File(dirSV);
                if (file.mkdirs()) {
                    System.out.println("Directory " + dirSV + " is created!");
                } else {
                    System.out.println("Failed to create directory! " + dirSV);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void genData(int n, int m, int index, boolean opt){
        MapDataInput I = new MapDataInput();
        I.genRandom(n,m,opt);
        int[] p = new int[m];
        for(int i = 0; i < m; i++) p[i] = i;
        Random R = new Random();
        for(int i = 0; i < code.length; i++){
            System.out.println("Gen for " + code[i]);
            // shuffle
            for(int k = 1; k <= 20; k++){
                int i1 = R.nextInt(m);
                int i2 = R.nextInt(m);
                int tmp = p[i1]; p[i1] = p[i2]; p[i2] = tmp;
                MapDataInput J = I.genDataPerm(p);
                String filename = rootDir + "//" + code[i] + "//" + code[i] + "-" + index + ".txt";
                J.savePlainTextFile(filename);
            }
        }
    }
    public static void main(String[] args){
        StudentExamChecker app= new StudentExamChecker();
        //app.createdFolders();
        int index = 5;

        /*
        //app.genData(500,50,index, true);

        String ffi = "D:\\teaching\\soict\\toi-uu-va-lap-ke-hoach\\HK2020-2021-2-124193\\thi-cuoi-ky\\data-bca\\input\\3.txt";
        String ffo = "D:\\teaching\\soict\\toi-uu-va-lap-ke-hoach\\HK2020-2021-2-124193\\thi-cuoi-ky\\data-bca\\output\\3-out.txt";
        MapDataInput input = new MapDataInput();
        input.loadDataFromPlanFile(ffi);
        ORToolMIPSolver mipSolver = new ORToolMIPSolver(input);
        mipSolver.testSolveOffline(ffo, 10000);
        if(true) return;
        */

        //for(int i = 0; i < app.code.length;  i++) {
            //int codesv = app.code[i];
        //for(index = 1; index <= 5; index++) {
        index = 4;
            int codesv = 20173020;//app.code[i];
            //int codesv = 20172945;

            String fi = app.rootDir + "\\" + codesv + "\\" + codesv + "-" + index + ".txt";
            //String fo = app.rootDir + "\\" + codesv + "\\dap-an\\" + codesv + "-" + index + "-da-g-out.txt";
            String fo = app.rootDir + "\\" + codesv + "\\ketquasinhvienchaythem\\" + codesv + "-" + index + "-output.txt";
            MapDataInput I = new MapDataInput();
            I.loadDataFromPlanFile(fi);
            //ORToolMIPSolver solver = new ORToolMIPSolver(I);
            //GreedySolver gSolver = new GreedySolver(I);
            //gSolver.solve(fo, 10000);
            //solver.testSolveOffline(fo, 10000);
            I.checkSolution(fi, fo);
        //}
        //}
    }
}
