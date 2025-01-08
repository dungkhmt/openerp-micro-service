package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.State;

import java.util.List;

public interface StateService {
    List<State> getState();

    void updateState();
}
