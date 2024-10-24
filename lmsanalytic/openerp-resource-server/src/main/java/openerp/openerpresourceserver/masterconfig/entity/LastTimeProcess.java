package openerp.openerpresourceserver.masterconfig.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "last_time_process")
public class LastTimeProcess {
    @Id
    @Column(name="table_name")
    private String tableName;

    @Column(name="last_time_process")
    private Date lastTimeProcess;

}
