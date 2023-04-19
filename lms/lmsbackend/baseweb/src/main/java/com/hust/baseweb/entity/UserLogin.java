package com.hust.baseweb.entity;

import com.hust.baseweb.applications.whiteboard.entity.UserWhiteboard;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Setter
@Getter
public class UserLogin {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    @Column(name = "user_login_id", updatable = false, nullable = false)
    private String userLoginId;

    @Column(name = "current_password")
    private String password;

    private String passwordHint;

    private String otpSecret;

    private boolean isSystem;

    private boolean enabled;

    private boolean hasLoggedOut;

    private boolean requirePasswordChange;

    private Integer successiveFailedLogins;

    private String clientToken;

    private int otpResendNumber;


    @JoinColumn(name = "party_id", referencedColumnName = "party_id")
    @OneToOne(fetch = FetchType.EAGER)
    private Party party;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_login_security_group", joinColumns = @JoinColumn(name = "user_login_id", referencedColumnName = "user_login_id"), inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id"))
    private Set<SecurityGroup> roles;

    @OneToMany(mappedBy = "userLogin")
    Set<UserWhiteboard> userWhiteboards;
    private Date disabledDateTime;

    public UserLogin() {
    }

    public UserLogin(String userLoginId) {
        this.userLoginId = userLoginId;
    }

    public UserLogin(String userLoginId, String password, Set<SecurityGroup> roles, boolean enabled) {
        this.userLoginId = userLoginId;
        this.password = PASSWORD_ENCODER.encode(password);
        this.roles = roles;

        this.enabled = enabled;
    }

    public UserLogin(
            String password, String passwordHint, boolean isSystem,
            boolean enabled, boolean hasLoggedOut,
            boolean requirePasswordChange, int successiveFailedLogins,
            Date disabledDateTime) {
        super();
        this.password = password;
        this.passwordHint = passwordHint;
        this.isSystem = isSystem;
        this.enabled = enabled;
        this.hasLoggedOut = hasLoggedOut;
        this.requirePasswordChange = requirePasswordChange;
        this.successiveFailedLogins = successiveFailedLogins;
        this.disabledDateTime = disabledDateTime;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public boolean hasRole(String groupId){
        for(SecurityGroup g: roles){
            if(g.getGroupId().equals(groupId)) return true;
        }
        return false;
    }
}
