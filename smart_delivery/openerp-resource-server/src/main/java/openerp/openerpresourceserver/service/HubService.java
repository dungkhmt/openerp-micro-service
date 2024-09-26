package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.request.HubRequest;
import openerp.openerpresourceserver.request.HubWithBays;
import openerp.openerpresourceserver.response.HubGeneral;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface HubService {
    Hub createHub(HubWithBays hubREQ);

    List<HubGeneral> getAllHubGeneral();

    Hub updateHub(HubWithBays request);

    HubWithBays getHubById(String id);

    Hub deleteHub(String id);

}
