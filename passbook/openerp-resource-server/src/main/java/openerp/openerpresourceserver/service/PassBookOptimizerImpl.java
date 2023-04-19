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
    public static void main(String[]args ){
        // FOR TESTING OR-TOOLS
        int n = 3;
        MPVariable[] x = new MPVariable[n];

        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver(String.valueOf(MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING));

        if (solver == null) {
            System.err.println("Could not create solver SCIP");
            return;
        }

        for(int i = 0; i < n; i++){
            x[i] = solver.makeIntVar(0,4,"x[" + i + "]");
        }
        MPConstraint c = solver.makeConstraint(1,10);
        c.setCoefficient(x[0],1);
        c.setCoefficient(x[1],-5);
        c.setCoefficient(x[2],7);

        MPObjective obj = solver.objective();
        obj.setMaximization();
        obj.setCoefficient(x[0],-4);
        obj.setCoefficient(x[2],2);

        final MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            System.out.println(obj.value());
            for(int i = 0; i < n; i++) System.out.println("x[" + i + "] = " + x[i].solutionValue());
        }else{
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
        double[] moneyGot = new double[n];
        int[] nbDaysRemain = new int[n];
        int amountLoan = I.getLoan();
        double loanRate = I.getLoadRate();
        double discountRate = I.getDiscountRate();

        System.out.println(date);
        for(int i = 0; i < n; i++){
            PassBook b = passBooks.get(i);
            //int nbDays = (int)( (b.getCreatedDate().getTime() - date.getTime())
            nbDaysRemain[i] =  (int)( (b.getEndDate().getTime() - date.getTime())
                    / (1000 * 60 * 60 * 24) );
            moneyGot[i] = b.getAmountMoneyDeposit()*(1 + b.getRate()); // TO BE CORRECTED
            System.out.println("number days passbook " + b.getPassBookName() + " = " + nbDaysRemain[i]);
        }

        // modelling
        MPVariable[] x = new MPVariable[n];
        MPVariable[] y= new MPVariable[n]; // if x[i] = 0, then loan y[i] (tat toan dung han thi vay y[i])
        /*
        TO BE CONTINUE... model & solve
         */

        // FORM result and return to client
        List<ModelResponseLoanElement> loanElements = new ArrayList<>();
        for(int i = 0; i < x.length; i++){
            if(x[i].solutionValue() <= 0){// tat toan dung han, co vay 1 khoan y[i]
                // create a loan corresponding to this passbook
                ModelResponseLoanElement e = new ModelResponseLoanElement();
                e.setPassBook(passBooks.get(i));
                e.setAmountMoneyLoan(y[i].solutionValue());
                loanElements.add(e);
            }
        }
        ModelResponseOptimizePassBookForLoan res = new ModelResponseOptimizePassBookForLoan();
        res.setLoans(loanElements);
        return res;
    }
}
