package wms.service.auth;

import java.util.List;
import java.util.Set;

public interface EntityAuthorizationService {

    Set<String> getEntityAuthorization(String id, List<String> roleIds);

}
