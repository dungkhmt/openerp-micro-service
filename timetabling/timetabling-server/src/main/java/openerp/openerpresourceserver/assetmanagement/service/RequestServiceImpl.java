package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Asset;
import openerp.openerpresourceserver.assetmanagement.entity.Request;
import openerp.openerpresourceserver.assetmanagement.repo.AssetRepo;
import openerp.openerpresourceserver.assetmanagement.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final Integer DONE = 3;

    // type
    private final Integer BORROW = 1;
    private final Integer PAY = 2;

    @Override
    public List<Request> getAllRequests() {
        return requestRepo.getAllByLastUpdated();
    }

    @Override
    public Request getById(Integer id) {
        return requestRepo.findById(id).orElse(null);
    }

    @Override
    public Request createNewRequest(Request request) {
        Request newRequest = new Request();
        newRequest.setName(request.getName());
        newRequest.setDescription(request.getDescription());
        newRequest.setStatus(PENDING);
        newRequest.setUser_id(request.getUser_id());
        newRequest.setAsset_id(request.getAsset_id());
        newRequest.setParent_id(request.getParent_id());
        newRequest.setType(request.getType());
        Integer asset_id = request.getAsset_id();
        Asset asset = assetRepo.findById(asset_id).get();
        if(asset.getStatus_id().equals(3) || asset.getStatus_id().equals(4)) {
            return null;
        }
        newRequest.setAdmin_id(asset.getAdmin_id());

        if(request.getType() == 1){ // request borrow
            Instant start_date = request.getStart_date().toInstant();
            Instant end_date = request.getEnd_date().toInstant();

            newRequest.setStart_date(Date.from(start_date));
            newRequest.setEnd_date(Date.from(end_date));
        } else if(request.getType() == 2){ // request back
            Instant payback_date = request.getPayback_date().toInstant();
            newRequest.setPayback_date(Date.from(payback_date));
        }

        newRequest.setSince(new Date());
        newRequest.setLast_updated(new Date());
        return requestRepo.save(newRequest);
    }

    @Override
    public Request editRequest(Integer Id, Request request) {
        Request foundRequest = requestRepo.findById(Id).get();
        foundRequest.setName(request.getName());
        foundRequest.setDescription(request.getDescription());

        Instant start_date = request.getStart_date().toInstant();
        Instant end_date = request.getEnd_date().toInstant();
        foundRequest.setStart_date(Date.from(start_date));
        foundRequest.setEnd_date(Date.from(end_date));

        foundRequest.setLast_updated(new Date());
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

        // if request is borrow
        if(foundRequest.getType().equals(BORROW)){
            if(asset.getStatus_id() != 1){
                return foundRequest;
            }
            asset.setStatus_id(2); // inuse
            asset.setAssignee_id(foundRequest.getUser_id());
        }
        // else if request is pay
        else if(foundRequest.getType().equals(PAY)){
            if(asset.getStatus_id() != 2){
                return foundRequest;
            }
            asset.setStatus_id(1);
            // find the parent request
            Request parentRequest = requestRepo.findById(foundRequest.getParent_id()).get();
            parentRequest.setStatus(DONE);
            requestRepo.save(parentRequest);
        }

        if (admin_id.equals(foundRequest.getAdmin_id())) {
//            throw new Exception()
            foundRequest.setStatus(APPROVED);
        }
        foundRequest.setLast_updated(new Date());

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
    public List<Request> getCreatorRequests(String userId) {
        return requestRepo.findByCreatorId(userId);
    }

    @Override
    public List<Request> getAdminRequests(String adminId) {
        return requestRepo.findByAdminId(adminId);
    }

    @Override
    public Request paybackRequest(Integer request_id, String user_id) {
        Request foundRequest = requestRepo.findById(request_id).get();
        if(!foundRequest.getStatus().equals(APPROVED)){
            return foundRequest;
        }
        if(!user_id.equals(foundRequest.getUser_id())){
            return foundRequest;
        }

        Date current = new Date();

        Integer asset_id = foundRequest.getAsset_id();
        Asset asset = assetRepo.findById(asset_id).get();
        asset.setStatus_id(1); // available
        asset.setLast_updated(current);
        foundRequest.setStatus(DONE);
        foundRequest.setLast_updated(current);
        assetRepo.save(asset);
        return requestRepo.save(foundRequest);
    }

    @Override
    public List<String> getTopUsers() {
        return requestRepo.getTopUsers();
    }

    @Override
    public List<Request> getByUser(String user_id) {
        return requestRepo.findByCreatorId(user_id);
    }
}
