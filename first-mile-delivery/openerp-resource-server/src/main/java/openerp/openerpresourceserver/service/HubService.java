package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.HubDTO;
import openerp.openerpresourceserver.dto.HubUpdateDTO;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.repo.HubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface HubService {



    List<Hub> getAllHubs();

    Hub editHub(String userId, HubUpdateDTO hub);

    public Hub saveHub(String userId, HubDTO hub);

    public boolean deleteHub(UUID id);


}
