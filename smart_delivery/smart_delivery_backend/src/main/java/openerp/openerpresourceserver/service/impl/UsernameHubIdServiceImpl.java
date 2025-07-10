package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.entity.UsernameHubId;
import openerp.openerpresourceserver.repository.UsernameHubIdRepo;
import openerp.openerpresourceserver.service.UsernameHubIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsernameHubIdServiceImpl implements UsernameHubIdService {

    private final UsernameHubIdRepo usernameHubIdRepo;

    @Autowired
    public UsernameHubIdServiceImpl(UsernameHubIdRepo usernameHubIdRepo) {
        this.usernameHubIdRepo = usernameHubIdRepo;
    }

    @Override
    public Optional<UsernameHubId> getHubIdByUsername(String username) {
        return usernameHubIdRepo.findByUserName(username);
    }

    @Override
    public UsernameHubId saveUsernameHubId(String username, String name, UUID hubId) {
        UsernameHubId usernameHubId = new UsernameHubId();
        usernameHubId.setUserName(username);
        usernameHubId.setName(name);
        usernameHubId.setHubId(hubId);
        return usernameHubIdRepo.save(usernameHubId);
    }
} 