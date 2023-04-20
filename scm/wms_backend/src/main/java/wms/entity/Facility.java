package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "facility")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facility extends BaseEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

//    @Column(name = "phone_number")
//    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "status")
    private String status;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserLogin user;

    @OneToMany(mappedBy = "facility",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<Customer> customers;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "contract_type_code")
//    @NotFound(action = NotFoundAction.IGNORE)
//    private ContractType contractType;
}
