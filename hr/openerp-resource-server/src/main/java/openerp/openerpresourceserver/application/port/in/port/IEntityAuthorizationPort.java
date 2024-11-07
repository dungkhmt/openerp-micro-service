package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.EntityAuthorization;

import java.util.List;

public interface IEntityAuthorizationPort {
    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
