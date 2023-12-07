package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.ClassCode;

import java.util.List;

public interface ClassCodeService {
    List<ClassCode> getClassCode();

    void updateClassCode();
}
