package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassCode;

import java.util.List;

public interface ClassCodeService {
    List<ClassCode> getClassCode();

    void updateClassCode();
}
