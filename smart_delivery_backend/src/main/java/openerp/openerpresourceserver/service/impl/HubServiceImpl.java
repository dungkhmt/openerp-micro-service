package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.repository.HubRepo;
import openerp.openerpresourceserver.repository.ShipperRepo;
import openerp.openerpresourceserver.dto.HubWithBaysDto;
import openerp.openerpresourceserver.dto.HubGeneral;
import openerp.openerpresourceserver.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HubServiceImpl implements HubService {


    @Autowired
    private HubRepo hubRepo;
    @Autowired
    private ShipperRepo shipperRepo;



    @Override
    @Transactional
    public Hub createHub(HubWithBaysDto request){
        log.info(String.format("alofds"));
        log.info(String.format("daodkao"));
        log.info("Dsad"+request.getId());


          //  UUID hubId = UUID.fromString(request.getId());
            log.info(String.format("daodkao"));

            Hub hub = new Hub();
            hub.setName(request.getName());
            hub.setCode(request.getCode());
            hub.setLength(request.getLength());
            hub.setWidth(request.getWidth());
            hub.setLongitude(request.getLongitude());
            hub.setLatitude(request.getLatitude());
            hub.setAddress(request.getAddress());
            Hub savedHub = hubRepo.save(hub);
            List<HubWithBaysDto.Shelf> listShelf = request.getListShelf();
            log.info(String.format("Saved bay list for hub id %s", hub.getHubId()));
            return savedHub;


        }

        @Override
        public List<HubGeneral> getAllHubGeneral(){
            List<Hub> response = hubRepo.findAll();
            return response.stream().map(hub -> new HubGeneral(hub)).collect(Collectors.toList());
        }


    @Override
    @Transactional
    public Hub updateHub(HubWithBaysDto request) {
        UUID hubId = UUID.fromString(request.getId());
        // Fetch the existing hub from the database
        Optional<Hub> existingHubOpt = hubRepo.findById(hubId);
        if (existingHubOpt.isEmpty()) {
            log.error("Hub not found with id: " + hubId);
            throw new RuntimeException("Hub not found with id: " + hubId);
        }

        Hub existingHub = existingHubOpt.get();

        // Update the hub's properties
        existingHub.setName(request.getName());
        existingHub.setCode(request.getCode());
        existingHub.setLength(request.getLength());
        existingHub.setWidth(request.getWidth());
        existingHub.setLongitude(request.getLongitude());
        existingHub.setLatitude(request.getLatitude());
        existingHub.setAddress(request.getAddress());

        // Save the updated hub
        Hub savedHub = hubRepo.save(existingHub);

        // Update the bays associated with this hub
        List<HubWithBaysDto.Shelf> listShelf = request.getListShelf();

        // Convert listShelf to Bay objects

        // Determine which bays need to be deleted, updated, or added

        log.info(String.format("Updated hub id %s and its bays", savedHub.getHubId()));

        return savedHub;
    }

    @Override
    @Transactional
    public HubWithBaysDto getHubById(String hubId) {
        // Retrieve the Hub entity by its ID
        Optional<Hub> existingHubOpt = hubRepo.findById(UUID.fromString(hubId));

        // Check if the Hub exists
        if (existingHubOpt.isEmpty()) {
            throw new RuntimeException("Hub not found with id: " + hubId);
        }

        Hub existingHub = existingHubOpt.get();

        // Retrieve the associated Bays

        // Convert the Hub and Bays to HubWithBays DTO
        HubWithBaysDto hubWithBays = new HubWithBaysDto();
        hubWithBays.setId(existingHub.getHubId().toString());
        hubWithBays.setName(existingHub.getName());
        hubWithBays.setCode(existingHub.getCode());
        hubWithBays.setLongitude(existingHub.getLongitude());
        hubWithBays.setLatitude(existingHub.getLatitude());
        hubWithBays.setAddress(existingHub.getAddress());
        hubWithBays.setLength(existingHub.getLength());
        hubWithBays.setWidth(existingHub.getWidth());

        return hubWithBays;
    }

    @Override
    @Transactional
    public Hub deleteHub(String hubId) {
        Optional<Hub> existingHubOpt = hubRepo.findById(UUID.fromString(hubId));
        if (existingHubOpt.isEmpty()) {
            throw new RuntimeException("Hub not found with id: " + hubId);
        }
        else {
            Hub existingHub = existingHubOpt.get();
            hubRepo.delete(existingHub);
            return existingHub;
        }
    }




}
