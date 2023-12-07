package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.entity.State;
import openerp.openerpresourceserver.repo.StateRepo;
import openerp.openerpresourceserver.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    @Autowired
    private StateRepo stateRepo;

    @Override
    public List<State> getState() {
        return stateRepo.findAll();
    }

    @Override
    public void updateState() {
        List<String> stateDataList = stateRepo.getState();
        if (!stateDataList.isEmpty()) {
            stateRepo.deleteAll();
        }
        List<State> stateList = new ArrayList<>();
        stateDataList.forEach(el -> {
            State state = State.builder()
                    .state(el)
                    .build();
            stateList.add(state);
        });
        stateRepo.saveAll(stateList);
    }
}
