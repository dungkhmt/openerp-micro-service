package com.hust.baseweb.applications.education.teacherclassassignment.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapDataInput {

    public int n;// number of classes

    public int m;// number of teachers

    public HashSet<Integer>[] D;// D[i] is the set of teachers that can be assigned to class i

    public boolean[][] conflict;

    public int[][] priority;

    public double[] hourClass;

    public double[] maxHourTeacher;

    public int[][] preAssignment;

    public boolean[][] classDays;// classDays[i][d] = true indicates that class i happens on day d

    public HashSet<Integer> teacherWantToMinimizeWorkingDays;

    public MapDataInput genDataPerm(int[] p) {
        // p is a permutation of teacher 0,1,...,m
        for (int k : p) {
            System.out.print(k + " ");
        }
        System.out.println();

        HashSet<Integer>[] nD = new HashSet[n];
        for (int i = 0; i < n; i++) {
            nD[i] = new HashSet<Integer>();
        }
        for (int i = 0; i < n; i++) {
            for (int j : D[i]) {
                nD[i].add(p[j]);
            }
        }
        double[] nMaxHourTeacher = new double[m];
        for (int j = 0; j < m; j++) {
            nMaxHourTeacher[p[j]] = maxHourTeacher[j];
        }

        MapDataInput I = new MapDataInput();
        I.setN(n);
        I.setM(m);
        I.setConflict(conflict);
        I.setHourClass(hourClass);
        I.setMaxHourTeacher(nMaxHourTeacher);
        I.setD(nD);
        return I;
    }

    public void genRandomAndStore(String filename, int n, int m, boolean opt) {
        genRandom(n, m, opt);
        savePlainTextFile(filename);
    }

    public void genRandom(int n, int m, boolean opt) {
        this.n = n;
        this.m = m;
        int[] x = new int[n];
        Random R = new Random();
        hourClass = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = R.nextInt(m);
            hourClass[i] = R.nextInt(2) + 2;
        }
        maxHourTeacher = new double[m];
        for (int i = 0; i < m; i++) {
            maxHourTeacher[i] = 0;
        }
        for (int i = 0; i < n; i++) {
            maxHourTeacher[x[i]] += hourClass[i];
        }
        conflict = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                conflict[i][j] = true;
            }
        }
        for (int j = 0; j < m; j++) {
            for (int i1 = 0; i1 < n; i1++) {
                if (x[i1] == j) {
                    for (int i2 = 0; i2 < n; i2++) {
                        if (x[i2] == j) {
                            conflict[i1][i2] = false;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            conflict[i][i] = false;
        }

        D = new HashSet[n];
        for (int i = 0; i < n; i++) {
            D[i] = new HashSet<Integer>();
        }
        int sz = Math.min(10, m / 3);
        for (int i = 0; i < n; i++) {
            D[i].add(x[i]);
            for (int k = 0; k < sz; k++) {
                int j = R.nextInt(m);
                D[i].add(j);
            }
        }


        if (!opt) {
            for (int i = 0; i < n; i++) {
                hourClass[i] += 1;
            }
        }

        // priority not used
        initDefaultPriority();
        //savePlainTextFile(filename);
    }

    private void initDefaultPriority() {
        priority = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                priority[i][j] = 1;
            }
        }

    }

    public void loadDataFromPlanFile(String filename) {
        try {
            Scanner in = new Scanner((new File(filename)));
            n = in.nextInt();
            m = in.nextInt();
            hourClass = new double[n];
            for (int i = 0; i < n; i++) {
                hourClass[i] = in.nextDouble();
            }
            maxHourTeacher = new double[m];
            for (int i = 0; i < m; i++) {
                maxHourTeacher[i] = in.nextDouble();
            }
            D = new HashSet[n];
            for (int i = 0; i < n; i++) {
                D[i] = new HashSet<Integer>();
            }
            for (int i = 0; i < n; i++) {
                int k = in.nextInt();
                for (int j = 0; j < k; j++) {
                    int x = in.nextInt();
                    D[i].add(x);
                }
            }
            conflict = new boolean[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int x = in.nextInt();
                    if (x == 0) {
                        conflict[i][j] = false;
                    } else {
                        conflict[i][j] = true;
                    }
                }
            }
            in.close();

            initDefaultPriority();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlainTextFile(String filename) {
        try {
            PrintWriter out = new PrintWriter(filename);
            out.println(n + " " + m);

            for (int i = 0; i < n; i++) {
                out.print(hourClass[i] + " ");
            }
            out.println();

            for (int j = 0; j < m; j++) {
                out.print(maxHourTeacher[j] + " ");
            }
            out.println();

            for (int i = 0; i < n; i++) {
                out.print(D[i].size() + " ");
                for (int j : D[i]) {
                    out.print(j + " ");
                }
                out.println();
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (conflict[i][j]) {
                        out.print(1 + " ");
                    } else {
                        out.print(0 + " ");
                    }
                }
                out.println();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkSolution(String fi, String fo) {
        try {
            loadDataFromPlanFile(fi);
            Scanner in = new Scanner(new File(fo));
            int[] x = new int[n];
            for (int i = 0; i < n; i++) {
                int k = in.nextInt();
                System.out.println("read sol k = " + k);
                x[k] = in.nextInt();
            }
            in.close();

            // check constraints
            boolean ok = true;
            int ans = 0;
            for (int i = 0; i < n; i++) {
                int j = x[i];
                if (j >= 0) {
                    ans += 1;
                    if (!D[i].contains(j)) {
                        ok = false;
                        System.out.println("FAILED: D[" + i + "] not contains " + j);
                        break;
                    }
                }
            }
            for (int j = 0; j < m; j++) {
                for (int i1 = 0; i1 < n; i1++) {
                    if (x[i1] == j) {
                        for (int i2 = 0; i2 < n; i2++) {
                            if (x[i2] == j) {
                                if (conflict[i1][i2] && i1 != i2) {
                                    ok = false;
                                    System.out.println("FAILED: conflict i1 = " +
                                                       i1 +
                                                       " i2 = " +
                                                       i2 +
                                                       " assign to " +
                                                       j);
                                }
                            }
                        }
                    }
                }
            }
            for (int j = 0; j < m; j++) {
                double L = 0;
                for (int i = 0; i < n; i++) {
                    if (x[i] == j) {
                        L += hourClass[i];
                    }
                }
                if (L > maxHourTeacher[j]) {
                    ok = false;
                    System.out.println("FAILED: teacher " +
                                       j +
                                       " has maxLoad = " +
                                       maxHourTeacher[j] +
                                       " but load = " +
                                       L);
                }
            }
            if (ok) {
                System.out.println("RESULT = " + ans);
            } else {
                System.out.println("NOT FEASIBLE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
