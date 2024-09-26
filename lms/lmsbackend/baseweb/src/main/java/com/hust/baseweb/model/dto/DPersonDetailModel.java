package com.hust.baseweb.model.dto;

import com.hust.baseweb.rest.user.DPerson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class DPersonDetailModel extends RepresentationModel<DPersonDetailModel> {

    private UUID partyId;
    private String fullName;
    private String firstName;
    private String middleName;
    private String lastName;

    private String status;
    private String partyType;
    private Date createdDate;
    private Date birthDate;
    private String userLoginId;
    private String partyCode;
    private List<String> roles;
    private String enabled;
    private String email;

    public DPersonDetailModel(
        String fullName, String status, String partyType, Date createdDate, String userLoginId,
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

    public DPersonDetailModel(DPerson p) {
        this.partyId = p.getPartyId();
        this.fullName = p.getPerson() != null
            ? p.getPerson().getFirstName() + " " + p.getPerson().getMiddleName() + " " + p.getPerson().getLastName()
            : null;
        this.middleName = p.getPerson().getMiddleName();
        this.firstName = p.getPerson().getFirstName();
        this.lastName = p.getPerson().getLastName();
        this.birthDate = p.getPerson().getBirthDate();
        this.status = p.getStatus();
        this.partyType = p.getType().getPartyTypeId();
        this.createdDate = p.getCreatedDate();
        this.userLoginId = p.getUserLogin().getUserLoginId();
        this.partyCode = p.getPartyCode();
        this.roles = p.getUserLogin().getRoles().stream().map(r -> r.getGroupId()).collect(Collectors.toList());
    }
}
