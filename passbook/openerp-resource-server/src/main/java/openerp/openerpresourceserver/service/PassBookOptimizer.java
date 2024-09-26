package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.ModelInputComputeLoanSolution;
import openerp.openerpresourceserver.model.ModelResponseOptimizePassBookForLoan;

public interface PassBookOptimizer {
    public ModelResponseOptimizePassBookForLoan computeSolution(ModelInputComputeLoanSolution I);
}
