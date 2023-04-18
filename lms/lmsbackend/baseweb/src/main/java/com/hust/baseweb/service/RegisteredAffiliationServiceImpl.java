package com.hust.baseweb.service;

import com.hust.baseweb.entity.RegisteredAffiliation;
import com.hust.baseweb.repo.RegisteredAffiliationRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RegisteredAffiliationServiceImpl implements RegisteredAffiliationService{
    @Autowired
    private RegisteredAffiliationRepo registeredAffiliationRepo;

    @Override
    public List<RegisteredAffiliation> findAll() {
        log.info("findAll");
        List<RegisteredAffiliation> affiliations = registeredAffiliationRepo.findAll();
        log.info("findAll, return list = " + affiliations.size());
        return affiliations;
    }
}
