package openerp.openerpresourceserver.fb.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.fb.entity.FbUser;
import openerp.openerpresourceserver.fb.model.ModelResponseUser;
import openerp.openerpresourceserver.fb.repo.FbUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class FbUserServiceImpl implements FbUserService{
    FbUserRepo fbUserRepo;

    @Override
    public List<ModelResponseUser> getAllUsers() {
        List<FbUser> L = fbUserRepo.findAll();
        List<ModelResponseUser> res = L.stream().map(u -> new ModelResponseUser(u.getId(),u.getGroupId(),u.getName(),u.getLink(),u.getCreateStamp())).toList();
        return res;
    }
}
