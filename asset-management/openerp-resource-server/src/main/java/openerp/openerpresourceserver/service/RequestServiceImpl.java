package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Request;
import openerp.openerpresourceserver.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
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

    @Override
    public Request createNewRequest(Request request) {
        Request newRequest = new Request();
        newRequest.setName(request.getName());
        newRequest.setDescription(request.getDescription());
        newRequest.setApproval_flow(request.getApproval_flow());
        newRequest.setStatus(0);
        newRequest.setApprovers_id(request.getApprovers_id());

        Date currentDate = new Date();
        newRequest.setSince(currentDate);
        newRequest.setLast_updated(currentDate);

        return requestRepo.save(newRequest);
    }

    public boolean approveRequest(Integer requestId, String userId){
        Request request = requestRepo.findById(requestId).get();
        if(request.getStatus() == -10){ // rejected
            return false;
        }

        if(request.getStatus() == 10){ // request done
            return false;
        }

        // check quyen xem co dc approve ko
        if(!request.getApprovers_id().contains(userId)){
            return false;
        }
//        List<String> approvals = request.getApprovals_id();
//        approvals.add(userId);
//        request.setApprovals_id(approvals.toString());
//        if(approvals.length() == request.getApprovers_id().length()){
//            request.setStatus(10); // mark done
//        }
        return true;
    }
}
