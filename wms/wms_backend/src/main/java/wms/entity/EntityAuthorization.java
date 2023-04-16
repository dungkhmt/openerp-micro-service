package wms.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "entity_authorization")
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