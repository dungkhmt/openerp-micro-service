package openerp.openerpresourceserver.infrastructure.output.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class AuditEntity {

    @ColumnDefault("CURRENT_DATE")
    @UpdateTimestamp
    @Column(name = "last_updated_stamp")
    private LocalDateTime lastUpdatedStamp;

    @ColumnDefault("CURRENT_DATE")
    @CreationTimestamp
    @Column(name = "created_stamp", nullable = false, updatable = false)
    private LocalDateTime createdStamp;
}
