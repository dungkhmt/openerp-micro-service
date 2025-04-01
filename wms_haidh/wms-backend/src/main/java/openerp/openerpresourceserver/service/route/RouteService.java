package openerp.openerpresourceserver.service.route;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface RouteService {
	
    ResponseEntity<String> fetchRoute(Map<String, Object> requestBody);
    
}

