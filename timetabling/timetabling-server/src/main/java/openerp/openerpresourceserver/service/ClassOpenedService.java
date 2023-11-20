package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;

import java.util.List;

public interface ClassOpenedService {

    List<ClassOpened> getAll();

    List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto);
}
