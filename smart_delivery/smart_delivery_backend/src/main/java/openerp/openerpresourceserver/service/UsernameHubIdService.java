package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.UsernameHubId;
import java.util.Optional;
import java.util.UUID;

public interface UsernameHubIdService {
    Optional<UsernameHubId> getHubIdByUsername(String username);
    UsernameHubId saveUsernameHubId(String username, String name, UUID hubId);
} 