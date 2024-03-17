package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Request;
import openerp.openerpresourceserver.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImpl implements RequestService{
    private RequestRepo requestRepo;

    @Override
    public List<Request> getAllRequests() {
        List<Request> requests = requestRepo.findAll();
        return requests;
    }
}
