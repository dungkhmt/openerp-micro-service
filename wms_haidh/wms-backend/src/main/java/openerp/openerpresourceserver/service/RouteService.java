package openerp.openerpresourceserver.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface RouteService {
	
    ResponseEntity<String> fetchRoute(Map<String, Object> requestBody);
    
}

