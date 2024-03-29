package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.dto.CollectorDTO;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.repo.CollectorRepo;
import openerp.openerpresourceserver.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollectorServiceImpl implements CollectorService {

    @Autowired
    CollectorRepo collectorRepo;

    @Override
    public Collector createCollector(CollectorDTO collectorDTO, String userId) {

            Collector collector = Collector.builder()
                    .collectorName(collectorDTO.getCollectorName())
                    .userId(userId)
                    .hubId(collectorDTO.getHubId())
                    .build();

            return collectorRepo.save(collector);
    }

    @Override
    public boolean deleteCollector(UUID collectorId) {
        try {
            collectorRepo.deleteById(collectorId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Collector updateCollector(UUID collectorId, CollectorDTO collectorDTO) {
        Collector collector = collectorRepo.findById(collectorId).orElse(null);
        if (collector == null) {
            return null;
        }
        collector.setCollectorName(collectorDTO.getCollectorName());
        collector.setHubId(collectorDTO.getHubId());
        return collectorRepo.save(collector);
    }

    @Override
    public Collector getCollector(UUID collectorId) {
        return collectorRepo.findById(collectorId).orElse(null);
    }

    @Override
    public List<Collector> getAllCollectorsInHub(UUID hubId) {
        return collectorRepo.findByHubId(hubId);
    }
}
