package com.hust.baseweb.repo;

import com.hust.baseweb.entity.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)

public interface PartyTypeRepo extends JpaRepository<PartyType, String> {

    PartyType findByPartyTypeId(String id);
}
