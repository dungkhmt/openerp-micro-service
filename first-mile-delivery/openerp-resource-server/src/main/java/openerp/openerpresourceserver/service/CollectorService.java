package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.CollectorDTO;
import openerp.openerpresourceserver.entity.Collector;

import java.util.List;
import java.util.UUID;

public interface CollectorService {

    Collector createCollector(CollectorDTO collectorDTO, String userId);


    boolean deleteCollector(UUID collectorId);

    Collector updateCollector(UUID collectorId, CollectorDTO collectorDTO);

    Collector getCollector(UUID collectorId);

    List<Collector> getAllCollectorsInHub(UUID hubId);




}
