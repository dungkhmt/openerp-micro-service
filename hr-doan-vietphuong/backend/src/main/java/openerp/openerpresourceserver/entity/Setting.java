package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "phuongvv_settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
    @Id
    private String key;

    @Column(name = "value", columnDefinition = "text")
    private String value;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updateBy;
}

