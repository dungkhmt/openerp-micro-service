package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "scm_entity_authorization")
public class EntityAuthorization {
    @Id
    private String id;
    private String roleId;
    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "created")
    private Instant created;

    @Lob
    @Column(name = "description")
    private String description;

}