package openerp.openerpresourceserver.fb.service;

import openerp.openerpresourceserver.fb.entity.FbUser;
import openerp.openerpresourceserver.fb.model.ModelResponseUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
public interface FbUserService {
    public List<ModelResponseUser> getAllUsers();

    public List<FbUser> synchronizeUsers(String inputJson, MultipartFile file) throws  Exception;
}
