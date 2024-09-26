package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Bay;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.repo.BayRepo;
import openerp.openerpresourceserver.repo.HubRepo;
import openerp.openerpresourceserver.repo.ShipperRepo;
import openerp.openerpresourceserver.request.HubWithBays;
import openerp.openerpresourceserver.response.HubGeneral;
import openerp.openerpresourceserver.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HubServiceImpl implements HubService {

    @Autowired
    private BayRepo bayRepo;
    @Autowired
    private HubRepo hubRepo;
    @Autowired
    private ShipperRepo shipperRepo;



    @Override
    @Transactional
    public Hub createHub(HubWithBays request){
        log.info(String.format("alofds"));
        log.info(String.format("daodkao"));
        log.info("Dsad"+request.getId());

        List<Bay> prevBays;

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
            prevBays = new ArrayList<Bay>();
            List<HubWithBays.Shelf> listShelf = request.getListShelf();
            List<Bay> bays = listShelf.stream()
                    .filter(shelf -> shelf.getCode() != null
                            && shelf.getX() != null
                            && shelf.getY() != null
                            && shelf.getLength() != null
                            && shelf.getWidth() != null)
                    .map(shelf -> {
                        Bay bay = Bay.builder()
                                .hub(savedHub)
                                .code(shelf.getCode())
                                .x(shelf.getX())
                                .y(shelf.getY())
                                .xLong(shelf.getWidth())
                                .yLong(shelf.getLength())
                                .build();
                        if (shelf.getId() != null) {
                            bay.setId(UUID.fromString(shelf.getId()));
                        }
                        return bay;
                    })
                    .collect(Collectors.toList());
            List<UUID> newBayIds = bays.stream().map(Bay::getId).collect(Collectors.toList());
            List<Bay> deletedBays = prevBays.stream().filter(bay -> !newBayIds.contains(bay.getId())).collect(
                    Collectors.toList());
            bayRepo.deleteAll(deletedBays);
            bayRepo.saveAll(bays);
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
    public Hub updateHub(HubWithBays request) {
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
        List<HubWithBays.Shelf> listShelf = request.getListShelf();
        List<Bay> existingBays = bayRepo.findByHubId(savedHub.getHubId());

        // Convert listShelf to Bay objects
        List<Bay> bays = listShelf.stream()
                .filter(shelf -> shelf.getCode() != null
                        && shelf.getX() != null
                        && shelf.getY() != null
                        && shelf.getLength() != null
                        && shelf.getWidth() != null)
                .map(shelf -> {
                    Bay bay = Bay.builder()
                            .hub(savedHub)
                            .code(shelf.getCode())
                            .x(shelf.getX())
                            .y(shelf.getY())
                            .xLong(shelf.getWidth())
                            .yLong(shelf.getLength())
                            .build();
                    if (shelf.getId() != null) {
                        bay.setId(UUID.fromString(shelf.getId()));
                    }
                    return bay;
                })
                .collect(Collectors.toList());

        // Determine which bays need to be deleted, updated, or added
        List<UUID> newBayIds = bays.stream().map(Bay::getId).collect(Collectors.toList());
        List<Bay> deletedBays = existingBays.stream()
                .filter(bay -> !newBayIds.contains(bay.getId()))
                .collect(Collectors.toList());

        // Delete removed bays
        bayRepo.deleteAll(deletedBays);

        // Save the updated/added bays
        bayRepo.saveAll(bays);

        log.info(String.format("Updated hub id %s and its bays", savedHub.getHubId()));

        return savedHub;
    }

    @Override
    @Transactional
    public HubWithBays getHubById(String hubId) {
        // Retrieve the Hub entity by its ID
        Optional<Hub> existingHubOpt = hubRepo.findById(UUID.fromString(hubId));

        // Check if the Hub exists
        if (existingHubOpt.isEmpty()) {
            throw new RuntimeException("Hub not found with id: " + hubId);
        }

        Hub existingHub = existingHubOpt.get();

        // Retrieve the associated Bays
        List<Bay> bays = bayRepo.findByHubId(existingHub.getHubId());

        // Convert the Hub and Bays to HubWithBays DTO
        HubWithBays hubWithBays = new HubWithBays();
        hubWithBays.setId(existingHub.getHubId().toString());
        hubWithBays.setName(existingHub.getName());
        hubWithBays.setCode(existingHub.getCode());
        hubWithBays.setLongitude(existingHub.getLongitude());
        hubWithBays.setLatitude(existingHub.getLatitude());
        hubWithBays.setAddress(existingHub.getAddress());
        hubWithBays.setLength(existingHub.getLength());
        hubWithBays.setWidth(existingHub.getWidth());

        // Convert each Bay to its corresponding DTO and add to the list
        List<HubWithBays.Shelf> listShelf = bays.stream().map(bay -> {
            return new HubWithBays.Shelf(
                    bay.getId().toString(),
                    bay.getCode(),
                    bay.getX(),
                    bay.getY(),
                    bay.getYLong(),
                    bay.getXLong(),
                    true // or false, depending on whether this boolean flag should be true or false
            );
        }).collect(Collectors.toList());


        hubWithBays.setListShelf(listShelf);

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
            List<Bay> bays = bayRepo.findByHubId(existingHub.getHubId());
            bayRepo.deleteAll(bays);
            hubRepo.delete(existingHub);
            return existingHub;
        }
    }




}
