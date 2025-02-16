package openerp.openerpresourceserver.api.vrp.service;

import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelInputCVRP;
import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelResonseCVRP;
import org.springframework.stereotype.Service;

@Service
public class CVRPServiceImpl implements  CVRPService{

    @Override
    public ModelResonseCVRP solve(ModelInputCVRP I) {
        CVRPGreedySolver greedySolver = new CVRPGreedySolver();
        ModelResonseCVRP res = greedySolver.solve(I);
        return res;
    }
}
