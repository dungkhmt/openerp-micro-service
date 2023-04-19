package com.hust.baseweb.service;

import com.hust.baseweb.entity.Party;
import com.hust.baseweb.entity.PartyRelationship;
import com.hust.baseweb.entity.RoleType;
import com.hust.baseweb.repo.PartyRelationshipRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))

public class PartyRelationshipServiceImpl implements PartyRelationshipService {

    private PartyRelationshipRepo partyRelationshipRepo;

    @Override
    public PartyRelationship save(PartyRelationship partyRelationship) {
        return partyRelationshipRepo.save(partyRelationship);
    }

    @Override
    public List<PartyRelationship> findAllByFromPartyAndRoleTypeAndThruDate(
        Party fromParty,
        RoleType roleType,
        Date thruDate
    ) {
        return partyRelationshipRepo.findAllByFromPartyAndRoleTypeAndThruDate(fromParty, roleType, thruDate);
    }

    @Override
    public List<PartyRelationship> findAllByToPartyAndRoleTypeAndThruDate(
        Party toParty,
        RoleType roleType,
        Date thruDate
    ) {
        return partyRelationshipRepo.findAllByToPartyAndRoleTypeAndThruDate(toParty, roleType, thruDate);
    }

    @Override
    public List<PartyRelationship> findAllByFromPartyAndToPartyAndRoleTypeAndThruDate(
        Party fromParty,
        Party toParty,
        RoleType roleType,
        Date thruDate
    ) {
        return partyRelationshipRepo.findAllByFromPartyAndToPartyAndRoleTypeAndThruDate(
            fromParty,
            toParty,
            roleType,
            thruDate);
    }
}
