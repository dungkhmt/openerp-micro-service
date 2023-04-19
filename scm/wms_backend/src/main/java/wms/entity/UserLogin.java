package wms.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_login")
public class UserLogin {
    @Id
    @Size(max = 255)
    @Column(name = "user_login_id", nullable = false)
    private String id;

    @OneToOne(mappedBy = "user")
    private Customer customer;

    @Size(max = 60)
    @Column(name = "current_password", length = 60)
    private String currentPassword;

    @Size(max = 60)
    @Column(name = "otp_secret", length = 60)
    private String otpSecret;

    @Size(max = 512)
    @Column(name = "client_token", length = 512)
    private String clientToken;

    @Column(name = "password_hint")
    @Type(type = "org.hibernate.type.TextType")
    private String passwordHint;

    @Column(name = "is_system")
    private Boolean isSystem;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "has_logged_out")
    private Boolean hasLoggedOut;

    @Column(name = "require_password_change")
    private Boolean requirePasswordChange;

    @Column(name = "disabled_date_time")
    private Instant disabledDateTime;

    @Column(name = "successive_failed_logins")
    private Integer successiveFailedLogins;

    @Column(name = "last_updated_stamp")
    private Instant lastUpdatedStamp;

    @Column(name = "created_stamp")
    private Instant createdStamp;

    @Column(name = "otp_resend_number")
    private Integer otpResendNumber;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 100)
    @Column(name = "first_name", length = 100)
    private String firstName;

    @Size(max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

}