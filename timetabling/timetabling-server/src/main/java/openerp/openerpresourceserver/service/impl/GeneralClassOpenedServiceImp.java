package openerp.openerpresourceserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.controller.general.GeneralClassOpenedController;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.repo.GeneralClassOpenedRepository;
import openerp.openerpresourceserver.service.GeneralClassOpenedService;

/**
 * GeneralClassOpenedServiceImp
 */
@Service
public class GeneralClassOpenedServiceImp implements GeneralClassOpenedService {

    @Autowired
    private GeneralClassOpenedRepository gcoRepo;
    @Override
    public List<GeneralClassOpened> getGeneralClasses(String semester) {
        return gcoRepo.findAllBySemester(semester);
    }

    @Override
    public GeneralClassOpened updateGeneralClass(GeneralClassOpenedController generalClassOpened) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGeneralClass'");
    }

    @Override
    public void deleteAllGeneralClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllGeneralClasses'");
    }

}