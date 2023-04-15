package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_login")
public class UserLogin {
    @Id
    @Column(name = "user_login_id", nullable = false)
    private String id;

    @Column(name = "current_password", length = 60)
    private String currentPassword;

    @Column(name = "otp_secret", length = 60)
    private String otpSecret;

    @Column(name = "client_token", length = 512)
    private String clientToken;

    @Lob
    @Column(name = "password_hint")
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

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

}