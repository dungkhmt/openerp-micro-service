package com.hust.openerp.taskmanagement.hr_management.application.port.out;

import java.util.List;
import java.util.Set;

public interface EntityAuthorizationUseCase {

    Set<String> getEntityAuthorization(String id, List<String> roleIds);

}
