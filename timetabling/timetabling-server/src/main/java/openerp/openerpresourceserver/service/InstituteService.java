package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.Institute;

import java.util.List;

public interface InstituteService {

    List<Institute> getInstitute();

    void updateInstitute();
}
