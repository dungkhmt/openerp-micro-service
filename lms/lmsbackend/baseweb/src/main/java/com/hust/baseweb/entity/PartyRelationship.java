package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class PartyRelationship {

    @Id
    @Column(name = "party_relationship_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID partyRelationshipId;

    @JoinColumn(name = "from_party_id", referencedColumnName = "party_id")
    @ManyToOne
    private Party fromParty;

    @JoinColumn(name = "to_party_id", referencedColumnName = "party_id")
    @ManyToOne
    private Party toParty;

    @JoinColumn(name = "role_type_id", referencedColumnName = "role_type_id")
    @ManyToOne
    private RoleType roleType;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "thru_date")
    private Date thruDate;

}
