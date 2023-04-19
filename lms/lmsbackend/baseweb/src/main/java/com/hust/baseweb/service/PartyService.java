package com.hust.baseweb.service;

import com.hust.baseweb.entity.Party;

import java.util.UUID;

public interface PartyService {

    Party save(String partyType);

    Party disableParty(String partyId);

    Party findByPartyId(UUID partyId);
}
