package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Status;
import openerp.openerpresourceserver.repo.StatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class StatusServiceImpl implements StatusService{
    private StatusRepo statusRepo;

    @Override
    public List<Status> getAllStatus() {
        List<Status> statusList = statusRepo.findAll();
        return statusList;
    }
}
