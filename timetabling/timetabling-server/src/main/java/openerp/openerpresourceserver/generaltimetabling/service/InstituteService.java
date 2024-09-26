package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.Institute;

import java.util.List;

public interface InstituteService {

    List<Institute> getInstitute();

    void updateInstitute();
}
