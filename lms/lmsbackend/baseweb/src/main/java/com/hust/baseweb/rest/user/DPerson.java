package com.hust.baseweb.rest.user;

import com.hust.baseweb.entity.PartyType;
import com.hust.baseweb.entity.Person;
import com.hust.baseweb.entity.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

/**
 * UserDetailEntity
 */
@Entity
@Table(name = "party")
@Getter
@Setter
public class DPerson {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "party_id")
    private UUID partyId;

    private String partyCode;
    @JoinColumn(name = "party_type_id", referencedColumnName = "party_type_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PartyType type;

    @JoinColumn(name = "party_id", referencedColumnName = "party_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Person person;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Status status;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "party")
    private DPersonUserLogin userLogin;

    private boolean isUnread;

    @Column(name = "created_by_user_login")
    private String createdBy;

    @Column(name = "last_modified_by_user_login")
    private String modifiedBy;

    private Date createdDate;
    private Date createdStamp;
    private Date lastUpdatedStamp;

    public DPerson() {
    }

    public String getStatus() {
        return this.status != null ? this.status.getId() : null;
    }
}
