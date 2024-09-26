package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassType;

import java.util.List;

public interface ClassTypeService {
    List<ClassType> getClassType();

    void updateClassType();
}
