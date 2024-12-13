package openerp.openerpresourceserver.thesisdefensejuryassignment.or_tools;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AssignTeacherAndThesisToDefenseJury {
    private List<Teacher> teacherList;
    private int defenseJuryNumber;
    private int teacherNumber;
    private int sessionNumber;

    public AssignTeacherAndThesisToDefenseJury(List<Teacher> teacherList, int sessionNumber, int defenseJuryNumber) {
        this.teacherList = teacherList;
        this.defenseJuryNumber = defenseJuryNumber;
        this.teacherNumber = teacherList.size();
        this.sessionNumber = sessionNumber;
    }

    public int calculateTeacherAndThesisMatchingScore(Thesis thesis, Teacher teacher) {
        int score = 0;
        List<int[]> teacherAndThesisMatchingScore = new ArrayList<>();
        HashMap<String, Integer> teacherKeyword = new HashMap<>();
        for (AcademicKeyword academicKeyword : teacher.getAcademicKeywordList()) {
            teacherKeyword.put(academicKeyword.getKeyword(), 1);
        }
        for (AcademicKeyword academicKeyword : thesis.getAcademicKeywordList()) {
            if (teacherKeyword.containsKey(academicKeyword.getKeyword())) {
                score += 5;
            }
        }
        return score;
    }

    public void assignThesisAndTeacherToDefenseJury() {
//        Loader.loadNativeLibraries();
//        MPSolver solver = MPSolver.createSolver("GLOP");
//        if (solver == null) {
//            System.out.println("Could not create solver GLOP");
//        }
//        int[][] teacherAndThesisMatchingScore = new int[thesisNumber][teacherNumber];
//        for (int i = 0; i < thesisNumber; i++) {
//            for (int j = 0; j < teacherNumber; j++) {
//                teacherAndThesisMatchingScore[i][j] = calculateTeacherAndThesisMatchingScore(thesisList.get(i), teacherList.get(j));
//            }
//        }
//        System.out.println("Matching score: ");
//        for (int i = 0; i < thesisNumber; i++) {
//            for (int j = 0; j < teacherNumber; j++) {
//                System.out.print(teacherAndThesisMatchingScore[i][j] + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("-----------------------------------------");
//        MPVariable[][] thesisInDefenseJury = new MPVariable[thesisNumber][defenseJuryNumber];
//        MPVariable[][] teacherInDefenseJury = new MPVariable[teacherNumber][defenseJuryNumber];
//        MPVariable[][] defenseJuryInSession = new MPVariable[defenseJuryNumber][sessionNumber];
//        MPVariable[][] thesisAndTeacherInTheSameDefenseJury = new MPVariable[thesisNumber][teacherNumber];
//        //create var x[n][k]: do an n trong hoi dong k
//        for (int i = 0; i < thesisNumber; ++i) {
//            for (int j = 0; j < defenseJuryNumber; ++j) {
//                thesisInDefenseJury[i][j] = solver.makeIntVar(0, 1, "x(" + i + ", " + j + ")");
//            }
//        }
//        //create var y[m][k]: giao vien m trong hoi dong k
//        for (int i = 0; i < teacherNumber; ++i) {
//            for (int j = 0; j < defenseJuryNumber; ++j) {
//                teacherInDefenseJury[i][j] = solver.makeIntVar(0, 1, "y(" + i + ", " + j + ")");
//            }
//        }
//        //create var z[k][s]: hoi dong k trong session s
//        for (int i = 0; i < defenseJuryNumber; ++i) {
//            for (int j = 0; j < sessionNumber; ++j) {
//                defenseJuryInSession[i][j] = solver.makeIntVar(0, 1, "z(" + i + ", " + j + ")");
//            }
//        }
//
//        //create var u[n][m]: do an n va giao vien m trong cung hoi dong
//        for (int i = 0; i < thesisNumber; ++i) {
//            for (int j = 0; j < teacherNumber; ++j) {
//                thesisAndTeacherInTheSameDefenseJury[i][j] = solver.makeIntVar(0, 1, "u(" + i + ", " + j + ")");
//            }
//        }
//        System.out.println("Number of variables = " + solver.numVariables());
//        //constraint: X[i, 1] + X[i, 2] + . . . + X[i, K] = 1, với mọi i = 1, 2, …, N
//        for (int i = 0; i < thesisNumber; ++i) {
//            MPConstraint thesisInDefenseJuryConstraints = solver.makeConstraint(1, 1, "");
//            for (int j = 0; j < defenseJuryNumber; ++j) {
//                thesisInDefenseJuryConstraints.setCoefficient(thesisInDefenseJury[i][j], 1);
//            }
//        }
//        // Constraints: mỗi hội đồng tổ chức 1 buổi duy nhất
//        for (int i = 0; i < defenseJuryNumber; ++i) {
//            MPConstraint defenseJuryInSessionConstraints = solver.makeConstraint(1, 1, "");
//            for (int j = 0; j < sessionNumber; ++j) {
//                defenseJuryInSessionConstraints.setCoefficient(defenseJuryInSession[i][j], 1);
//            }
//        }
//
//        //constraint: Y[j, k1] + Y[j, k2] <= 3 - (Z[k1, s] + Z[k2, s]), với mọi j, k1, k2, s
//        MPConstraint[] teacherAndDefenseJuryAndSessionConstraints = new MPConstraint[100000];
//        int constraintCount = 0;
//        for (int i = 0; i < teacherNumber; ++i) {
//            for (int k1 = 0; k1 < defenseJuryNumber - 1; ++k1) {
//                for (int k2 = k1 + 1; k2 < defenseJuryNumber; ++k2) {
//                    for (int s = 0; s < sessionNumber; ++s) {
//                        teacherAndDefenseJuryAndSessionConstraints[constraintCount] = solver.makeConstraint(0, 3);
//                        teacherAndDefenseJuryAndSessionConstraints[constraintCount].setCoefficient(teacherInDefenseJury[i][k1], 1);
//                        teacherAndDefenseJuryAndSessionConstraints[constraintCount].setCoefficient(teacherInDefenseJury[i][k2], 1);
//                        teacherAndDefenseJuryAndSessionConstraints[constraintCount].setCoefficient(defenseJuryInSession[k1][s], 1);
//                        teacherAndDefenseJuryAndSessionConstraints[constraintCount].setCoefficient(defenseJuryInSession[k2][s], 1);
//                        constraintCount++;
//                    }
//                }
//            }
//        }
//        // Constraint: Mỗi hội đồng tối đa 9 đồ án
//        for (int i = 0; i < defenseJuryNumber; ++i) {
//            MPConstraint thesisInDefenseJuryConstraints = solver.makeConstraint(0, 9, "");
//            for (int j = 0; j < thesisNumber; ++j) {
//                thesisInDefenseJuryConstraints.setCoefficient(thesisInDefenseJury[j][i], 1);
//            }
//        }
//        // Constraint: Mỗi hội đồng tối đa 5 giáo viên
//        for (int i = 0; i < defenseJuryNumber; ++i) {
//            MPConstraint thesisInDefenseJuryConstraints = solver.makeConstraint(0, 5, "");
//            for (int j = 0; j < teacherNumber; ++j) {
//                thesisInDefenseJuryConstraints.setCoefficient(teacherInDefenseJury[j][i], 1);
//            }
//        }
//        //Constraint: Mỗi đồ án trg hội đồng phài match ít nhất vs 1 giáo viên`
//        // x[i][k] + y [j][k] <=
//        double neInfinity = Integer.MIN_VALUE;
//        for (int k = 0; k < defenseJuryNumber; ++k) {
//            for (int i = 0; i < thesisNumber; i++) {
//                for (int j = 0; j < teacherNumber; ++j) {
//                    MPConstraint thesisAndTeacherConstraints = solver.makeConstraint(neInfinity, 0, "");
//                    thesisAndTeacherConstraints.setCoefficient(thesisInDefenseJury[i][k], 1);
//                    thesisAndTeacherConstraints.setCoefficient(teacherInDefenseJury[j][k], 1);
//                    thesisAndTeacherConstraints.setCoefficient(thesisAndTeacherInTheSameDefenseJury[i][j] , -2);
//                }
//            }
//        }
//
//        for (int i=0;i< thesisNumber;i++){
//            for (int j=0;j < teacherNumber; j++){
//                MPConstraint c = solver.makeConstraint(neInfinity, -1, "");
//                c.setCoefficient(thesisAndTeacherInTheSameDefenseJury[i][j], defenseJuryNumber);
//                for (int k=0; k < defenseJuryNumber; k++){
//                    c.setCoefficient(thesisInDefenseJury[i][k], -1);
//                    c.setCoefficient(teacherInDefenseJury[j][k], -1);
//                }
//            }
//        }
////        for (int i = 0; i < thesisNumber; ++i) {
////            MPConstraint thesisAndTeacherConstraints = solver.makeConstraint(1, teacherNumber, "");
////            for (int j = 0; j < teacherNumber; ++j) {
////                thesisAndTeacherConstraints.setCoefficient(thesisAndTeacherInTheSameDefenseJury[i][j], 1);
////            }
////        }
//
//        System.out.println("Number of constraints = " + solver.numConstraints());
//        MPObjective objective = solver.objective();
//        for (int i = 0; i < thesisNumber; ++i) {
//            for (int j = 0; j < teacherNumber; ++j) {
//                objective.setCoefficient(thesisAndTeacherInTheSameDefenseJury[i][j], teacherAndThesisMatchingScore[i][j]);
//            }
//        }
//        objective.setMaximization();
//
//        final MPSolver.ResultStatus resultStatus = solver.solve();
//        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
//            for (int i = 0; i < sessionNumber; i++) {
//                System.out.println("Ngay " + i);
//                for (int j = 0; j < defenseJuryNumber; j++) {
//                    if (defenseJuryInSession[j][i].solutionValue() > 0.5) {
//                        System.out.println("Hoi dong " + j);
//                        for (int m = 0; m < teacherNumber; m++) {
////                            if (teacherInDefenseJury[m][j].solutionValue() > 0.5) {
//                                System.out.print("Giao vien: " + teacherList.get(m).getTeacherName() + " ");
//                                for (AcademicKeyword academicKeyword : teacherList.get(m).getAcademicKeywordList()) {
//                                    System.out.print(academicKeyword.getKeyword() + " ");
//                                }
//                                System.out.println("");
////                            }
//                        }
//                        for (int k = 0; k < thesisNumber; k++) {
////                            if (thesisInDefenseJury[k][j].solutionValue() > 0.5) {
//                                System.out.print("Đồ án: " + thesisList.get(k).getThesisName() + " ");
//                                for (AcademicKeyword academicKeyword : thesisList.get(k).getAcademicKeywordList()) {
//                                    System.out.print(academicKeyword.getKeyword() + " ");
//                                }
//                                System.out.println("");
////                            }
//                        }
//                    }
//                }
//            }
//
//        }else{
//            System.err.println("The problem does not have an optimal solution!");
//            if (resultStatus == MPSolver.ResultStatus.FEASIBLE) {
//                System.err.println("A potentially suboptimal solution was found.");
//            } else {
//                System.err.println("The solver could not solve the problem.");
//                return;
//            }
//        }
////        System.out.println("Đồ án vs hội đồng");
////        for (int i = 0; i < thesisNumber; i++) {
////            for (int j = 0; j < defenseJuryNumber; j++) {
////                if (thesisInDefenseJury[i][j].solutionValue() > 0.5) {
////                    System.out.println("đồ án " + i + " ở hội đồng " + j);
////                }
////                System.out.print(thesisInDefenseJury[i][j].solutionValue() + " ");
////            }
////            System.out.println("");
////        }
////        System.out.println("Gvien vs hội đồng");
////        for (int i = 0; i < teacherNumber; i++) {
////            for (int j = 0; j < defenseJuryNumber; j++) {
////                if (teacherInDefenseJury[i][j].solutionValue() > 0) {
////                    System.out.println("giao vien " + i + " ở hội đồng " + j);
////                }
////                System.out.print(teacherInDefenseJury[i][j].solutionValue() + " ");
////            }
////            System.out.println("");
////        }
//        System.out.println("Đồ án thuộc hội đồng");
//        for (int i = 0; i < thesisNumber; i++) {
//            for (int j = 0; j < defenseJuryNumber; j++) {
//                System.out.print(thesisInDefenseJury[i][j].solutionValue() + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("--------------------------------------");
//
//        System.out.println("Giáo viên thuộc hội đồng");
//        for (int i = 0; i < teacherNumber; i++) {
//            for (int j = 0; j < defenseJuryNumber; j++) {
//                System.out.print(teacherInDefenseJury[i][j].solutionValue() + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("--------------------------------------");
//
//        System.out.println("Hội đồng thuộc session");
//        for (int i = 0; i < defenseJuryNumber; i++) {
//            for (int j = 0; j < sessionNumber; j++) {
//                System.out.print(defenseJuryInSession[i][j].solutionValue() + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("--------------------------------------");
//
//        System.out.println("Teacher and thesis:");
//        for (int i = 0; i < thesisNumber; i++) {
//            for (int j = 0; j < teacherNumber; j++) {
//                System.out.print(thesisAndTeacherInTheSameDefenseJury[i][j].solutionValue() + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("---------------------------------------");
//
//        return;
    }
}
