package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.controller.general.GeneralClassOpenedController;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;

public interface GeneralClassOpenedService {
    public List<GeneralClassOpened> getGeneralClasses(String semester);

    public GeneralClassOpened updateGeneralClass(GeneralClassOpenedController generalClassOpened);

    public void deleteAllGeneralClasses();
}
