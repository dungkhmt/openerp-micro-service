package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.EmployeeRequestDto;
import org.keycloak.admin.client.Keycloak;

public interface KeycloakAdminClient {

    Keycloak getKeycloakInstance();

    void createUserInKeycloak(EmployeeRequestDto employeeRequestDto);

    void updateUserInKeycloak(EmployeeRequestDto employeeRequestDto);
}
