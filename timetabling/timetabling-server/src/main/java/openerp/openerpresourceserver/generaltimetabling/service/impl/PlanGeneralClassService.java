package openerp.openerpresourceserver.generaltimetabling.service.impl;


import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.repo.GeneralClassRepository;
import openerp.openerpresourceserver.generaltimetabling.repo.PlanGeneralClassRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PlanGeneralClassService {
    private GeneralClassRepository generalClassRepository;
    private PlanGeneralClassRepository planGeneralClassRepository;
    public GeneralClass makeClass(MakeClassRequest request) {

        return null;
    }
}
