package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.ClassType;

import java.util.List;

public interface ClassTypeService {
    List<ClassType> getClassType();

    void updateClassType();
}
