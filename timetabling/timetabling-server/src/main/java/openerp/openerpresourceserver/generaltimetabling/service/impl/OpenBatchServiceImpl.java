package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.model.entity.OpenBatch;
import openerp.openerpresourceserver.generaltimetabling.repo.OpenBatchRepo;
import openerp.openerpresourceserver.generaltimetabling.service.OpenBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenBatchServiceImpl implements OpenBatchService {

    @Autowired
    private OpenBatchRepo openBatchRepo;

    @Override
    public List<OpenBatch> getOpenBatch() {
        return openBatchRepo.findAll();
    }

    @Override
    public void updateOpenBatch() {
        List<String> openBatchDataList = openBatchRepo.getOpenBatch();
        if (!openBatchDataList.isEmpty()) {
            openBatchRepo.deleteAll();
        }
        List<OpenBatch> openBatchList = new ArrayList<>();
        openBatchDataList.forEach(el -> {
            OpenBatch openBatch = OpenBatch.builder()
                    .openBatch(el)
                    .build();
            openBatchList.add(openBatch);
        });
        openBatchRepo.saveAll(openBatchList);
    }
}
