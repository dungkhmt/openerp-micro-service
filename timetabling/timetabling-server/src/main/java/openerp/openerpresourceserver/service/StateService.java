package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.State;

import java.util.List;

public interface StateService {
    List<State> getState();

    void updateState();
}
