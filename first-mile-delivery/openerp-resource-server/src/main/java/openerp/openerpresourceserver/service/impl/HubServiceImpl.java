package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.dto.HubDTO;
import openerp.openerpresourceserver.dto.HubUpdateDTO;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.repo.HubRepo;
import openerp.openerpresourceserver.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HubServiceImpl implements HubService {
    @Autowired
    private HubRepo hubRepo;

    @Override
    public List<Hub> getAllHubs() {
        return hubRepo.findAll();
    }

    @Override
    public Hub editHub(String userId, HubUpdateDTO hub) {

        Hub existingHub = hubRepo.findById(hub.getId()).orElseThrow(() -> new RuntimeException("Hub not found"));

        existingHub.setHubCode(hub.getHubCode());
        existingHub.setAddress(hub.getAddress());
        existingHub.setLatitude(hub.getLatitude());
        existingHub.setLongitude(hub.getLongitude());
        existingHub.setStatus(hub.getStatus());
        return hubRepo.save(existingHub);
    }

    @Override
    public Hub saveHub(String userId, HubDTO hub) {

        Hub newHub = Hub.builder()
                .hubCode(hub.getHubCode())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .status(hub.getStatus())
                .createdByUserId(userId)
                .build();

        return hubRepo.save(newHub);
    }

    @Override
    public boolean deleteHub(UUID id) {
        Hub existingHub = hubRepo.findById(id).orElse(null);

        if (existingHub == null) {
            return false;
        }
        hubRepo.delete(existingHub);
        return true;
    }
}
