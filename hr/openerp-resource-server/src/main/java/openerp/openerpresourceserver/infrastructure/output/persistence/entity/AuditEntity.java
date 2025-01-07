package openerp.openerpresourceserver.infrastructure.output.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity implements Serializable {

    @UpdateTimestamp
    @Column(name = "last_updated_stamp")
    private LocalDate lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp", nullable = false, updatable = false)
    private LocalDate createdStamp;
}
