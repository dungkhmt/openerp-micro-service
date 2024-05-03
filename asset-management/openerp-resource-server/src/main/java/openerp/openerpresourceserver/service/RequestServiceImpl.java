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

    // status
    private final Integer PENDING = 0;
    private final Integer APPROVED = 1;
    private final Integer REJECTED = 2;

    // type
    private final Integer BORROW = 1;
    private final Integer PAY = 2;

    @Override
    public List<Request> getAllRequests() {
        return requestRepo.findAll();
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
    public Request approveRequest(Integer Id, String admin_id) {
        Request foundRequest = requestRepo.findById(Id).get();
        if(!foundRequest.getStatus().equals(PENDING)){
            return foundRequest;
        }
        Integer asset_id = foundRequest.getAsset_id();
        Asset asset = assetRepo.findById(asset_id).get();
        if(asset.getStatus_id() != 1){
            return foundRequest;
        }
        if (admin_id.equals(foundRequest.getAdmin_id())) {
            foundRequest.setStatus(APPROVED);
        }
        foundRequest.setLast_updated(new Date());
        if(foundRequest.getType().equals(BORROW)){
            asset.setStatus_id(2); // inuse
        } else if(foundRequest.getType().equals(PAY)){
            asset.setStatus_id(1); // available
        }
        assetRepo.save(asset);
        return requestRepo.save(foundRequest);
    }

    @Override
    public Request rejectRequest(Integer Id, String admin_id) {
        Request foundRequest = requestRepo.findById(Id).orElse(null);
        if(!foundRequest.getStatus().equals(PENDING)){
            return foundRequest;
        }
        if (admin_id.equals(foundRequest.getAdmin_id())) {
            foundRequest.setStatus(REJECTED);
        }
        foundRequest.setLast_updated(new Date());
        return requestRepo.save(foundRequest);
    }

    @Override
    public void deleteRequest(Integer Id) {
        Optional<Request> request = requestRepo.findById(Id);
        if(request.isPresent()) {
            requestRepo.deleteById(Id);
        }
    }

    @Override
    public List<Request> getUserRequests(String userId) {
        return requestRepo.findByUserId(userId);
    }
}
