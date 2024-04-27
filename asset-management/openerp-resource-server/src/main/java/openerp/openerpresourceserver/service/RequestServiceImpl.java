package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Asset;
import openerp.openerpresourceserver.entity.Request;
import openerp.openerpresourceserver.repo.AssetRepo;
import openerp.openerpresourceserver.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImpl implements RequestService{
    private RequestRepo requestRepo;
    private AssetRepo assetRepo;

    private final Integer PENDING = 0;
    private final Integer APPROVED = 1;
    private final Integer REJECTED = 2;

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
        newRequest.setStatus(PENDING);
        newRequest.setUser_id(request.getUser_id());
        newRequest.setAsset_id(request.getAsset_id());
        Integer asset_id = request.getAsset_id();
        Asset asset = assetRepo.findById(asset_id).get();
        newRequest.setAdmin_id(asset.getAdmin_id());

        Date currentDate = new Date();
        newRequest.setSince(currentDate);
        newRequest.setLast_updated(currentDate);
        return requestRepo.save(newRequest);
    }

    @Override
    public Request editRequest(Integer Id, Request request) {
        Request foundRequest = requestRepo.findById(Id).get();
        foundRequest.setName(request.getName());
        foundRequest.setDescription(request.getDescription());
        foundRequest.setStart_date(request.getStart_date());
        foundRequest.setEnd_date(request.getEnd_date());
        Date currentDate = new Date();
        foundRequest.setLast_updated(currentDate);
        return requestRepo.save(foundRequest);
    }

    @Override
    public void deleteRequest(Integer Id) {
        Optional<Request> request = requestRepo.findById(Id);
        if(request.isPresent()) {
            requestRepo.deleteById(Id);
        }
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
//        if(!request.getApprovers_id().contains(userId)){
//            return false;
//        }
//        List<String> approvals = request.getApprovals_id();
//        approvals.add(userId);
//        request.setApprovals_id(approvals.toString());
//        if(approvals.length() == request.getApprovers_id().length()){
//            request.setStatus(10); // mark done
//        }
        return true;
    }


}
