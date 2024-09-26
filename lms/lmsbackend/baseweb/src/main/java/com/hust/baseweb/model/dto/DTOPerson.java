package com.hust.baseweb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hust.baseweb.rest.user.DPerson;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DTOPerson {

    @JsonIgnore
    public static Map<String, String> mapDto2Entity = new HashMap<String, String>();

    @SuppressWarnings("rawtypes")
    @JsonIgnore
    public static Map<String, Pair> mapDto2Recursive = new HashMap<String, Pair>();
    @SuppressWarnings("rawtypes")
    @JsonIgnore
    public static Pair<Map<String, String>, Map<String, Pair>> mapPair = new Pair<Map<String, String>, Map<String, Pair>>(
        mapDto2Entity, mapDto2Recursive);

    static {
        mapDto2Entity.put("partyId", "partyId");
        mapDto2Entity.put("fullName", "person.fullName");
        mapDto2Entity.put("status", "status");
        mapDto2Entity.put("partyType", "partyType");
        mapDto2Entity.put("createdDate", "createdDate");
        mapDto2Entity.put("userLoginId", "userLogin.userLoginId");
        mapDto2Entity.put("partyCode", "partyCode");

        mapDto2Recursive.put("partyId", null);
        mapDto2Recursive.put("fullName", null);
        mapDto2Recursive.put("status", null);
        mapDto2Recursive.put("partyType", null);
        mapDto2Recursive.put("createdDate", null);
        mapDto2Recursive.put("userLoginId", null);
        mapDto2Recursive.put("partyCode", null);
    }

    private UUID partyId;
    private String fullName;
    private String status;
    private String partyType;
    private Date createdDate;
    private String userLoginId;
    private String partyCode;

    public DTOPerson(
        String fullName,
        String status,
        String partyType,
        Date createdDate,
        String userLoginId,
        String partyCode
    ) {
        super();
        this.fullName = fullName;
        this.status = status;
        this.partyType = partyType;
        this.createdDate = createdDate;
        this.userLoginId = userLoginId;
        this.partyCode = partyCode;
    }

    public DTOPerson(DPerson dPerson) {
        this.partyId = dPerson.getPartyId();
        this.fullName = dPerson.getPerson() != null
            ?
            dPerson.getPerson().getFirstName() +
            " " +
            dPerson.getPerson().getMiddleName() +
            " " +
            dPerson.getPerson().getLastName()
            :
                null;
        this.status = dPerson.getStatus();
        this.partyType = dPerson.getType().getPartyTypeId();
        this.createdDate = dPerson.getCreatedDate();
        this.userLoginId = dPerson.getUserLogin().getUserLoginId();
        this.partyCode = dPerson.getPartyCode();
    }
}
