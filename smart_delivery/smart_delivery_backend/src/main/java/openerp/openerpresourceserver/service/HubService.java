package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.dto.HubWithBaysDto;
import openerp.openerpresourceserver.dto.HubGeneral;

import java.util.List;

public interface HubService {
    Hub createHub(HubWithBaysDto hubREQ);

    List<HubGeneral> getAllHubGeneral();

    Hub updateHub(HubWithBaysDto request);

    HubWithBaysDto getHubById(String id);

    Hub deleteHub(String id);

}
