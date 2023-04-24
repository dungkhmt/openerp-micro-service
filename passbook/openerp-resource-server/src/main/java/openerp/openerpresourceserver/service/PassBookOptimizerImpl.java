package openerp.openerpresourceserver.service;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.PassBook;
import openerp.openerpresourceserver.model.ModelInputComputeLoanSolution;
import openerp.openerpresourceserver.model.ModelResponseLoanElement;
import openerp.openerpresourceserver.model.ModelResponseOptimizePassBookForLoan;
import openerp.openerpresourceserver.repo.PassBookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service

public class PassBookOptimizerImpl implements PassBookOptimizer{
    private PassBookRepo passBookRepo;
    public static void main(String[] args){

        final double INFINITY = java.lang.Double.POSITIVE_INFINITY;

        int n = 10;

        double[] moneyEarly = { 82025, 69024, 99015, 1858091, 82057, 2590075, 933697, 132032, 903782, 79004 };

        double[] moneyMature = { 87823, 69918, 99398, 1865482, 87823, 2768570, 998184, 133757, 929718, 80051 };

        int[] nbDaysRemain = { 304, 24, 2, 21, 231, 2, 28, 45, 22, 80 };

//        double[] moneyEarly = new double[n];
//        double[] moneyMature = new double[n];
//        int[] nbDaysRemain = new int[n];
        int amountLoan = 6828000;

        // These rates are annual rate (yearly)
        double loanRate = 0.07;
        double discountRate = 0.05;

        double[] loanRateArr = new double[n];
        double[] discountRateArr = new double[n];
        // Compute Loan Amount and Discount Amount for each book

        for (int i = 0; i < n; i++) {
            loanRateArr[i] = Math.pow((1 + loanRate / 365), nbDaysRemain[i]);
            discountRateArr[i] = Math.pow((1 + discountRate / 365), nbDaysRemain[i]);
        }

        MPVariable[] x = new MPVariable[n];
        MPVariable[] y = new MPVariable[n];


        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver(
                String.valueOf(MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING));

        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return;
        }

        for(int i = 0; i < n; i++){
            x[i] = solver.makeIntVar(0.0, 1.0, "x[" + i + "]");
            y[i] = solver.makeIntVar(0.0, INFINITY, "y[" + i + "]");
        }

        MPConstraint sumConstraint = solver.makeConstraint(amountLoan, amountLoan);
        for (int i = 0; i < n; i++) {
            sumConstraint.setCoefficient(x[i], moneyEarly[i]);
            sumConstraint.setCoefficient(y[i], 1.0);
        }

        MPConstraint[] loanLimitConstraints = new MPConstraint[n];
        for (int i = 0; i < n; i++) {
            loanLimitConstraints[i] = solver.makeConstraint(0.0, moneyMature[i] / loanRateArr[i]);
            loanLimitConstraints[i].setCoefficient(y[i], 1.0);
            loanLimitConstraints[i].setCoefficient(x[i], moneyMature[i] / loanRateArr[i]);
        }

        MPObjective obj = solver.objective();
        obj.setMaximization();
        for (int i = 0; i < n; i++) {
            obj.setCoefficient(x[i], - moneyMature[i] / discountRateArr[i]);
            obj.setCoefficient(y[i], - loanRateArr[i] / discountRateArr[i]);
        }

        final MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Raw objective = " + obj.value());
            double temp = 0; // The constant in the objective
            for (int i = 0; i < n; i++) {
                temp += moneyMature[i] / discountRateArr[i];
            }
            System.out.println("Objective value = " + (obj.value() + temp));
            for (int i = 0; i < n; i++) {
                System.out.println("x[" + i + "] = " + x[i].solutionValue());
                if (x[i].solutionValue() == 0) {
                    System.out.println("Tat toan dung han so thu " + i);
                    if (y[i].solutionValue() == 0)
                        System.out.println("Khong the chap so thu " + i);
                    else
                        System.out.println("The chap so thu " + i + " de vay " + y[i].solutionValue());
                }
                else
                    System.out.println("Tat toan truoc han so thu " + i);
            }
            System.out.println("Checking objective value...");
            double obj_value = 0;
            for (int i = 0; i < n; i++) {
                obj_value += (moneyMature[i] * (1 - x[i].solutionValue())
                        - y[i].solutionValue() * loanRateArr[i]) / discountRateArr[i];
            }
            System.out.println(obj_value);
        } else{
            System.out.println("No optimal");
        }
    }

    @Override
    public ModelResponseOptimizePassBookForLoan computeSolution(ModelInputComputeLoanSolution I) {
        List<PassBook> passBooks = passBookRepo.findAllByUserId(I.getUserId());
        log.info("computeSolution, passBooks = " + passBooks.size());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
            date = formatter.parse(I.getDate());
        }catch(Exception e){
            e.printStackTrace();
        }
        // mapped data input
        int n = passBooks.size();
        double[] moneyEarly = new double[n];// = { 82025, 69024, 99015, 1858091, 82057, 2590075, 933697, 132032, 903782, 79004 };

        double[] moneyMature = new double[n];// = { 87823, 69918, 99398, 1865482, 87823, 2768570, 998184, 133757, 929718, 80051 };

        int[] nbDaysRemain = new int[n];
        int amountLoan = I.getLoan();
        double loanRate = I.getLoadRate();
        double discountRate = I.getDiscountRate();

        System.out.println(date);
        String info = "";
        for(int i = 0; i < n; i++){
            PassBook b = passBooks.get(i);
            //int nbDays = (int)( (b.getCreatedDate().getTime() - date.getTime())
            nbDaysRemain[i] =  (int)( (b.getEndDate().getTime() - date.getTime())
                    / (1000 * 60 * 60 * 24) );
            moneyMature[i] = b.getAmountMoneyDeposit()*(1 + b.getRate()); // TO BE CORRECTED
            int dayPass = b.getDuration() - nbDaysRemain[i];
            moneyEarly[i] = b.getAmountMoneyDeposit()*(1 + b.getRate()*(dayPass)/b.getDuration());
            System.out.println("number days passbook " + b.getPassBookName() + " = " + nbDaysRemain[i]+
                    " dayPass = " + dayPass + " moneyEarly = " + moneyEarly[i] + " moneyMature = " + moneyMature[i]);
        }


        double[] loanRateArr = new double[n];
        double[] discountRateArr = new double[n];
        // Compute Loan Amount and Discount Amount for each book

        for (int i = 0; i < n; i++) {
            loanRateArr[i] = Math.pow((1 + loanRate / 365), nbDaysRemain[i]);
            discountRateArr[i] = Math.pow((1 + discountRate / 365), nbDaysRemain[i]);
        }
        final double INFINITY = java.lang.Double.POSITIVE_INFINITY;
        MPVariable[] x = new MPVariable[n];
        MPVariable[] y = new MPVariable[n];

        log.info(("computeSolution starting to load library or-tools"));
        Loader.loadNativeLibraries();
        log.info(("computeSolution load library or-tools OK"));

        MPSolver solver = MPSolver.createSolver(
                String.valueOf(MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING));

        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return null;
        }

        for(int i = 0; i < n; i++){
            x[i] = solver.makeIntVar(0.0, 1.0, "x[" + i + "]");
            y[i] = solver.makeIntVar(0.0, INFINITY, "y[" + i + "]");
        }

        MPConstraint sumConstraint = solver.makeConstraint(amountLoan, amountLoan);
        for (int i = 0; i < n; i++) {
            sumConstraint.setCoefficient(x[i], moneyEarly[i]);
            sumConstraint.setCoefficient(y[i], 1.0);
        }

        MPConstraint[] loanLimitConstraints = new MPConstraint[n];
        for (int i = 0; i < n; i++) {
            loanLimitConstraints[i] = solver.makeConstraint(0.0, moneyMature[i] / loanRateArr[i]);
            loanLimitConstraints[i].setCoefficient(y[i], 1.0);
            loanLimitConstraints[i].setCoefficient(x[i], moneyMature[i] / loanRateArr[i]);
        }

        MPObjective obj = solver.objective();
        obj.setMaximization();
        for (int i = 0; i < n; i++) {
            obj.setCoefficient(x[i], - moneyMature[i] / discountRateArr[i]);
            obj.setCoefficient(y[i], - loanRateArr[i] / discountRateArr[i]);
        }

        final MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("Raw objective = " + obj.value());
            info += "Raw objective = " + obj.value() + "\n";
            double temp = 0; // The constant in the objective
            for (int i = 0; i < n; i++) {
                temp += moneyMature[i] / discountRateArr[i];
            }
            System.out.println("Objective value = " + (obj.value() + temp));
            info += " Objective value = " + (obj.value() + temp) + "\n";
            for (int i = 0; i < n; i++) {
                System.out.println("x[" + i + "] = " + x[i].solutionValue());
                info += " x[" + i + "] = " + x[i].solutionValue() + "\n";
                if (x[i].solutionValue() == 0) {
                    System.out.println("Tat toan dung han so thu " + i);
                    info += " Tat toan dung han so thu " + i + "\n";
                    if (y[i].solutionValue() == 0) {
                        System.out.println("Khong the chap so thu " + i);
                        info += " Khong the chap so thu " + i + "\n";
                    }else {
                        System.out.println("The chap so thu " + i + " de vay " + y[i].solutionValue());
                        info += " The chap so thu " + i + " de vay " + y[i].solutionValue() + "\n";
                    }
                }
                else {
                    System.out.println("Tat toan truoc han so thu " + i);
                    info += " Tat toan truoc han so thu " + i + "\n";
                }
            }
            System.out.println("Checking objective value...");
            double obj_value = 0;
            for (int i = 0; i < n; i++) {
                obj_value += (moneyMature[i] * (1 - x[i].solutionValue())
                        - y[i].solutionValue() * loanRateArr[i]) / discountRateArr[i];
            }
            System.out.println(obj_value);
        } else{
            System.out.println("No optimal");
            info += " No optimal" + "\n";
        }


        // FORM result and return to client
        List<ModelResponseLoanElement> loanElements = new ArrayList<>();
        for(int i = 0; i < x.length; i++){
            if(x[i].solutionValue() <= 0){// tat toan dung han, co vay 1 khoan y[i]
                // create a loan corresponding to this passbook
                ModelResponseLoanElement e = new ModelResponseLoanElement();
                e.setPassBook(passBooks.get(i));
                e.setMoneyEarly(moneyEarly[i]);
                e.setMoneyMature(moneyMature[i]);
                e.setAmountMoneyLoan(y[i].solutionValue());
                loanElements.add(e);
            }
        }
        ModelResponseOptimizePassBookForLoan res = new ModelResponseOptimizePassBookForLoan();
        res.setLoans(loanElements);
        res.setInfo(info);
        return res;
    }
}
