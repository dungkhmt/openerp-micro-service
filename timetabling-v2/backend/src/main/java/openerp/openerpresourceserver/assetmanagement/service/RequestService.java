package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.Request;

import java.util.List;

public interface RequestService {
    List<Request> getAllRequests();

    Request getById(Integer id);

    Request createNewRequest(Request request);

    Request editRequest(Integer Id, Request request);

    Request approveRequest(Integer Id, String admin_id);

    Request rejectRequest(Integer Id, String admin_id);

    void deleteRequest(Integer id);

    List<Request> getCreatorRequests(String user_id);

    List<Request> getAdminRequests(String admin_id);

    Request paybackRequest(Integer request_id, String user_id);

    List<String> getTopUsers();

    List<Request> getByUser(String user_id);
}
