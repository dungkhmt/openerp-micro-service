package com.hust.baseweb.service;

import com.hust.baseweb.applications.taskmanagement.dto.dao.PersonDao;
import com.hust.baseweb.entity.Person;

import java.util.List;
import java.util.UUID;

public interface PersonService {

    Person findByPartyId(UUID partyId);

    List<Person> getALL();
}
