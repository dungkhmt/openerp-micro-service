package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Request;

import java.util.List;

public interface RequestService {
    List<Request> getAllRequests();

    Request createNewRequest(Request request);
}
