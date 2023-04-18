package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

/**
 * PersonRepo
 */
public interface PersonRepo extends JpaRepository<Person, UUID> {

    Person findByPartyId(UUID partyId);
}
