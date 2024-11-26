package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface PartyRepo extends JpaRepository<Party, UUID> {

    Party findByPartyId(UUID partyId);

    List<Party> findAllByPartyIdIn(Collection<UUID> partyIds);

    boolean existsByPartyCode(String partyCode);

    List<Party> findAllByNameIgnoreCaseContaining(String name);
}
