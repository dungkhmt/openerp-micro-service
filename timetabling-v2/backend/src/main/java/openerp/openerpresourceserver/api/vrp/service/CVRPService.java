package openerp.openerpresourceserver.api.vrp.service;

import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelInputCVRP;
import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelResonseCVRP;

public interface CVRPService {
    ModelResonseCVRP solve(ModelInputCVRP I);
}
