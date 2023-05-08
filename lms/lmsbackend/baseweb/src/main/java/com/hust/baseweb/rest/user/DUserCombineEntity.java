package com.hust.baseweb.rest.user;

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
@SecondaryTables({
                     @SecondaryTable(name = "person"),
                     @SecondaryTable(name = "user_login", pkJoinColumns = @PrimaryKeyJoinColumn(name = "party_id"))})
public class DUserCombineEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(name = "user_login_id", table = "user_login")
    private String userLoginId;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "party_id")
    private UUID partyId;
    @Column(table = "person")
    private String firstName;
    @Column(table = "person")
    private String middleName;
    @Column(table = "person")
    private String lastName;
    @Column(table = "person")
    private String gender;
    @Column(table = "person")
    private Date birthDate;
    // @OneToMany(fetch = FetchType.EAGER)
    // @JoinTable(name = "user_login_security_group", joinColumns = @JoinColumn(name
    // = "user_login_id", referencedColumnName = "user_login_id"),
    // inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName =
    // "group_id"))
    // private List<SecurityGroup> roles;

    public DUserCombineEntity() {
    }
}
