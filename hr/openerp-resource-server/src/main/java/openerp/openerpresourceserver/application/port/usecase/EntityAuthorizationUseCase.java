package openerp.openerpresourceserver.application.port.usecase;

import java.util.List;
import java.util.Set;

public interface EntityAuthorizationUseCase {

    Set<String> getEntityAuthorization(String id, List<String> roleIds);

}
