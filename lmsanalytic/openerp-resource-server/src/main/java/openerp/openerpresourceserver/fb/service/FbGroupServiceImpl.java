package openerp.openerpresourceserver.fb.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.fb.entity.FbGroup;
import openerp.openerpresourceserver.fb.model.ModelResponseFbGroup;
import openerp.openerpresourceserver.fb.repo.FbGroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class FbGroupServiceImpl implements FbGroupService{
    private FbGroupRepo fbGroupRepo;
    @Override
    public List<ModelResponseFbGroup> getAllGroups() {
        List<FbGroup> L = fbGroupRepo.findAll();
        List<ModelResponseFbGroup> res = new ArrayList<>();
        for(FbGroup g: L){
            ModelResponseFbGroup gr = new ModelResponseFbGroup();
            gr.setGroupType(g.getGroupType());
            gr.setGroupName(g.getGroupName());
            gr.setId(g.getId());
            gr.setLink(g.getLink());
            gr.setNumberMembers(g.getNumberMembers());
            res.add(gr);
        }
        return res;
    }
}
