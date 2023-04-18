package com.hust.baseweb.service;

import com.hust.baseweb.entity.Party;
import com.hust.baseweb.entity.PartyRelationship;
import com.hust.baseweb.entity.RoleType;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface PartyRelationshipService {

    PartyRelationship save(PartyRelationship partyRelationship);

    List<PartyRelationship> findAllByFromPartyAndRoleTypeAndThruDate(
        Party fromParty,
        RoleType roleType,
        Date thruDate
    );

    List<PartyRelationship> findAllByToPartyAndRoleTypeAndThruDate(
        Party toParty,
        RoleType roleType,
        Date thruDate
    );

    List<PartyRelationship> findAllByFromPartyAndToPartyAndRoleTypeAndThruDate(
        Party fromParty,
        Party toParty,
        RoleType roleType,
        Date thruDate
    );

}
