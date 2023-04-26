package com.hust.baseweb.applications.education.teacherclassassignment.service;

import localsearch.constraints.basic.NotEqual;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.*;

public class CBLSSolver {

    private int n;// number of classes
    private int m;// number of teachers
    private HashSet<Integer>[] D;
    private boolean[][] conflict;
    private int[][] priority;
    private double[] hourClass;
    private double[] maxHourTeacher;
    private LocalSearchManager mgr;
    private ConstraintSystem S;
    private VarIntLS[] x;
    private Random R = new Random();
    // data structures for greedy algo
    private int[] solution;
    private int[] teacherAssigned2Class;
    private HashSet<Integer>[] classesAssigned2Teacher;
    private double[] load;

    public CBLSSolver(int n, int m, HashSet[] D, int[][] priority, boolean[][] conflict, double[] hourClass, double[] maxHourTeacher) {
        this.n = n;
        this.m = m;
        this.D = D;
        this.priority = priority;
        this.conflict = conflict;
        this.hourClass = hourClass;
        this.maxHourTeacher = maxHourTeacher;
        classesAssigned2Teacher = new HashSet[m];
        for (int i = 0; i < m; i++) {
            classesAssigned2Teacher[i] = new HashSet<Integer>();
        }
    }

    private void stateModel() {
        mgr = new LocalSearchManager();
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, D[i]);
        }
        S = new ConstraintSystem(mgr);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (conflict[i][j]) {
                    S.post(new NotEqual(x[i], x[j]));
                }
            }
        }
        mgr.close();
    }

    class Move {

        int i, v;

        public Move(int i, int v) {
            this.i = i;
            this.v = v;
        }
    }

    public String name() {
        return "CBLSSolver";
    }

    private boolean checkDomain() {
        for (int t = 0; t < m; t++) {
            for (int i = 0; i < n; i++) {
                if (teacherAssigned2Class[i] == t) {
                    if (!D[i].contains(t)) {
                        System.out.println(name() +
                                           "::checkDomain FAILED???, teacher " +
                                           t +
                                           " is not in the domain of course " +
                                           i);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkConflict() {
        for (int t = 0; t < m; t++) {
            for (int i = 0; i < n; i++) {
                if (teacherAssigned2Class[i] == t) {
                    for (int j = i + 1; j < n; j++) {
                        if (teacherAssigned2Class[j] == t) {
                            if (conflict[i][j]) {
                                System.out.println(name() +
                                                   "::checkConflict " +
                                                   i +
                                                   " and " +
                                                   j +
                                                   " of teacher " +
                                                   t +
                                                   " failed???");
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private HashSet<Integer> initSolution() {
        HashSet<Integer> cand = new HashSet();
        HashSet<Integer> assigned = new HashSet();
        HashSet<Integer> notAssigned = new HashSet<>();
        for (int i = 0; i < n; i++) {
            cand.add(i);
        }
        load = new double[m];
        for (int t = 0; t < m; t++) {
            load[t] = 0;
        }
        teacherAssigned2Class = new int[n];
        for (int i = 0; i < n; i++) {
            teacherAssigned2Class[i] = -1;// init NULL
        }
        while (cand.size() > 0) {
            int minD = Integer.MAX_VALUE;
            int sel_i = -1;
            for (int i : cand) {
                if (D[i].size() < minD) {
                    minD = D[i].size();
                    sel_i = i;
                }
            }
            // select teacher t for class sel_i such that load[t] is minimal
            double minLoad = Integer.MAX_VALUE;
            int sel_t = -1;
            for (int t : D[sel_i]) {
                boolean ok = true;
                for (int j : assigned) {
                    if (conflict[sel_i][j] && teacherAssigned2Class[j] == t) {
                        ok = false;
                        break;
                    }
                }
                if (!ok) {
                    continue;
                }
                if (load[t] < minLoad) {
                    minLoad = load[t];
                    sel_t = t;
                }
            }
            if (sel_t == -1) {
                notAssigned.add(sel_i);
            } else {
                //x[sel_i].setValuePropagate(sel_t);
                teacherAssigned2Class[sel_i] = sel_t;
                //if(sel_t == 0) System.out.println("Assign class " + sel_i + " to teacher " + sel_t);
                assigned.add(sel_i);
                classesAssigned2Teacher[sel_t].add(sel_i);
                load[sel_t] += hourClass[sel_i];
                if (!checkConflict()) {
                    System.out.println("BUG???");
                    break;
                }
            }
            cand.remove(sel_i);
        }
        System.out.println("After INIT, not assigned = " + notAssigned.size());
        return notAssigned;
    }

    private boolean canAssignClass2Teacher(int i, int t) {
        for (int j : classesAssigned2Teacher[t]) {
            if (conflict[i][j]) {
                return false;
            }
        }
        return true;
    }

    private HashSet<Integer> getPossibleNewTeachers4Class(int i) {
        HashSet<Integer> cand = new HashSet();
        for (int t : D[i]) {
            if (t != teacherAssigned2Class[i]) {
                if (canAssignClass2Teacher(i, t)) {
                    cand.add(t);
                }
            }
        }
        return cand;
    }

    private HashSet<Integer> getConflictClasses(int i, int t) {
        HashSet<Integer> s = new HashSet();
        for (int j : classesAssigned2Teacher[t]) {
            if (conflict[i][j]) {
                s.add(j);
            }
        }
        return s;
    }

    class Move2Path {

        int i;
        int ti;
        int j;
        int tj;

        // class i is assigned to teacher ti
        // class j (conflict with i) f teacher ti is re-assigned to teacher tj
        public Move2Path(int i, int ti, int j, int tj) {
            this.i = i;
            this.ti = ti;
            this.j = j;
            this.tj = tj;
        }
    }

    private List<Move2Path> computeReInsertImprove(int i) {
        List<Move2Path> moves = new ArrayList<>();
        int sel_t = -1;
        //for(int t = 0; t < m; t++){
        for (int t : D[i]) {
            HashSet<Integer> C = getConflictClasses(i, t);
            if (C.size() == 0) {
                //sel_t = t; break;
                moves.add(new Move2Path(i, t, -1, -1));
            }
            if (C.size() == 1) {
                for (int j : C) {
                    HashSet<Integer> cand = getPossibleNewTeachers4Class(j);
                    for (int tj : cand) {
                        moves.add(new Move2Path(i, t, j, tj));
                    }
                }
            }
        }
        return moves;
    }

    private HashSet<Integer> reinsertMovePath(HashSet<Integer> notAssigned) {
        HashSet<Integer> remain = new HashSet();
        for (int i : notAssigned) {
            boolean ok = findMultiMoves(i);
            if (!ok) {
                remain.add(i);
            } else {
                System.out.println("reinsertMovePath SUCCESS for class " + i);
            }
        }
        return remain;
    }

    private HashSet<Integer> reinsertImprove(HashSet<Integer> notAssigned) {
        HashSet<Integer> remain = new HashSet<Integer>();
        for (int i : notAssigned) {
            List<Move2Path> moves = computeReInsertImprove(i);
            if (moves.size() == 0) {
                remain.add(i);
                if (D[i].size() > 0) {
                    System.out.println("Improve, remain " + i + ", D = " + D[i].size());
                }
            } else {
                Move2Path m = moves.get(R.nextInt(moves.size()));
                if (m.j == -1) {
                    teacherAssigned2Class[m.i] = m.ti;
                    classesAssigned2Teacher[m.ti].add(m.i);
                    load[m.ti] += hourClass[m.i];
                } else {
                    /*
                    current: m.ti is assign to class m.j
                    new: m.ti is no longer to be assigned to m.j, it (m.ti) is assigned to class m.i,
                         m.j is assigned to teacher m.tj
                     */
                    teacherAssigned2Class[m.j] = m.tj;
                    teacherAssigned2Class[m.i] = m.ti;
                    classesAssigned2Teacher[m.ti].remove(m.j);
                    classesAssigned2Teacher[m.ti].add(m.i);
                    classesAssigned2Teacher[m.tj].add(m.j);
                    load[m.ti] = load[m.ti] - hourClass[m.j] + hourClass[m.i];
                    load[m.tj] = load[m.tj] + hourClass[m.j];
                }
                System.out.println("Move2Path(" + m.i + "," + m.ti + "," + m.j + "," + m.tj);
            }
        }
        System.out.println("REMAIN " + remain.size());
        return remain;
    }

    class MoveClass2NewTeachers {

        HashMap<Integer, HashSet<Integer>> mClass2PossibleNewTeacher = new HashMap();

        public void put(int cls, HashSet<Integer> T) {
            mClass2PossibleNewTeacher.put(cls, T);
        }
    }

    private boolean findMultiMoves(int cls) {
        if (D[cls].size() == 0) {
            System.out.println("findMultiMoves(" + cls + ") domain empty!!");
            return false;
        }
        //HashMap<Integer, HashSet<Integer>> mClass2PossibleNewTeacher = new HashMap();
        HashMap<Integer, MoveClass2NewTeachers> map = new HashMap<>();
        int minDT = Integer.MAX_VALUE;
        int sel_t = -1;
        for (int t : D[cls]) {
            boolean ok = true;
            for (int j : classesAssigned2Teacher[t]) {
                if (conflict[cls][j]) {
                    HashSet<Integer> Tj = getPossibleNewTeachers4Class(j);
                    if (Tj.size() == 0) {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok) {
                int dt = 0;
                MoveClass2NewTeachers mt = new MoveClass2NewTeachers();
                for (int j : classesAssigned2Teacher[t]) {
                    if (conflict[cls][j]) {
                        HashSet<Integer> Tj = getPossibleNewTeachers4Class(j);
                        mt.put(j, Tj);
                        dt += hourClass[j];
                    }
                }
                map.put(t, mt);
                if (dt < minDT) {
                    minDT = dt;
                    sel_t = t;
                }
                System.out.println("FOUND multimoves for class " + cls);
            } else {
                return false;
            }
        }

        // select to move class cls to teacher sel_t
        teacherAssigned2Class[cls] = sel_t;
        classesAssigned2Teacher[sel_t].add(cls);
        load[sel_t] += hourClass[cls];

        MoveClass2NewTeachers mcnt = map.get(sel_t);
        if (mcnt.mClass2PossibleNewTeacher.keySet() == null || mcnt.mClass2PossibleNewTeacher.keySet().size() == 0) {
            return true;
        }
        int[] sorted_list = new int[mcnt.mClass2PossibleNewTeacher.keySet().size()];
        int idx = -1;
        for (int j : mcnt.mClass2PossibleNewTeacher.keySet()) {
            idx++;
            sorted_list[idx] = j;
        }
        // sort the list in descreasing order of hourClass
        for (int i = 0; i < sorted_list.length - 1; i++) {
            for (int j = i + 1; j < sorted_list.length; j++) {
                if (hourClass[i] < hourClass[j]) {
                    int tmp = sorted_list[i];
                    sorted_list[i] = sorted_list[j];
                    sorted_list[j] = tmp;
                }
            }
        }
        for (int i = 0; i < sorted_list.length; i++) {
            // find min_load teacher for assigning class sorted_list[i]
            double min_load = Integer.MAX_VALUE;
            int sel_ti = -1;
            for (int t : mcnt.mClass2PossibleNewTeacher.get(sorted_list[i])) {
                if (load[t] < min_load) {
                    min_load = load[t];
                    sel_ti = t;
                }
            }
            teacherAssigned2Class[sorted_list[i]] = sel_ti;
            classesAssigned2Teacher[sel_ti].add(sorted_list[i]);
            load[sel_ti] += hourClass[sorted_list[i]];
            classesAssigned2Teacher[sel_t].remove(sorted_list[i]);
            load[sel_t] -= hourClass[sorted_list[i]];
        }
        return true;
    }

    private void analyzeNotAssigned(HashSet<Integer> notAssigned) {
        for (int i : notAssigned) {
            if (D[i].size() > 0) {
                for (int t : D[i]) {
                    System.out.print("Class " + i + " conflict with classes of teacher " + t + ": ");
                    for (int j : classesAssigned2Teacher[t]) {
                        if (conflict[i][j]) {
                            System.out.print(j + " ");
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    private boolean checkAll() {
        return checkDomain() && checkConflict();
    }

    private HashSet<Integer> search(int maxIter, int maxTime) {
        HashSet<Integer> notAssigned = initSolution();
        for (int i : notAssigned) {
            //System.out.println("not assigned class " + i + ", D = " + D[i].size());
        }
        boolean ok = checkAll();
        if (!ok) {
            System.out.println(name() + "::search, after initSolution, FAILED check???");
            return notAssigned;
        }

        notAssigned = reinsertImprove(notAssigned);
        ok = checkAll();
        if (!ok) {
            System.out.println(name() + "::search, after reinsertImprove, FAILED check???");
            return notAssigned;
        }

        //notAssigned = reinsertImprove(notAssigned);
        //notAssigned = reinsertImprove(notAssigned);
        //notAssigned = reinsertImprove(notAssigned);
        //analyzeNotAssigned(notAssigned);
        notAssigned = reinsertMovePath(notAssigned);
        ok = checkAll();
        if (!ok) {
            System.out.println(name() + "::search, after reinsertMovePath, FAILED check???");
            return notAssigned;
        }

        /*for(int cls: notAssigned){
            findMultiMoves(cls);
        }*/
        if (true) {
            return notAssigned;
        }

        double t0 = System.currentTimeMillis();
        ArrayList<Move> cand = new ArrayList<Move>();
        //Random R = new Random();
        for (int it = 0; it < maxIter; it++) {
            if (System.currentTimeMillis() - t0 > maxTime) {
                break;
            }
            cand.clear();
            int minD = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                for (int v : D[i]) {
                    int d = S.getAssignDelta(x[i], v);
                    if (d < minD) {
                        minD = d;
                        cand.clear();
                        cand.add(new Move(i, v));
                    } else if (d == minD) {
                        cand.add(new Move(i, v));
                    }
                }
            }
            Move m = cand.get(R.nextInt(cand.size()));
            x[m.i].setValuePropagate(m.v);
            //System.out.println("Step " + it + ": " + S.violations());
            if (S.violations() == 0) {
                break;
            }
        }
        return notAssigned;
    }

    public void solve() {
        stateModel();
        HashSet<Integer> notAssign = search(10000, 10000);
        solution = new int[x.length];
        for (int i = 0; i < n; i++) {
            if (!notAssign.contains(i)) {
                solution[i] = teacherAssigned2Class[i];////x[i].getValue();
            } else {
                solution[i] = -1;
            }
        }
        for (int i : notAssign) {
            //System.out.println("not assigned " + i + ", D = " + D[i].size());
        }
        boolean ok = checkDomain() && checkConflict();

        System.out.println("Solver finished with " + ok + ", notAssigned = " + notAssign.size());
    }

    public int[] getSolution() {
        //int[] s = new int[x.length];
        //for(int i = 0; i < n; i++) s[i] = x[i].getValue();
        //return s;
        return solution;
    }
}
