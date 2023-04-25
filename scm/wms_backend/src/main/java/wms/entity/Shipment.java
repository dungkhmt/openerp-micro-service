package wms.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "shipment")
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends BaseEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "started_date")
    private ZonedDateTime startedDate;

    @Column(name = "expected_end_date")
    private ZonedDateTime endedDate;

    @Column(name = "max_size_item")
    private Integer maxSize;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserLogin user;
}
