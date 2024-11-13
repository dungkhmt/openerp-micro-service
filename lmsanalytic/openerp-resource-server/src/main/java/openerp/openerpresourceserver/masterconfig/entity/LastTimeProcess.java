package openerp.openerpresourceserver.masterconfig.entity;

import jakarta.persistence.*;
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
@IdClass(CompositeLastTimeProcessId.class)
public class LastTimeProcess {
    @Id
    @Column(name="table_name")
    private String tableName;

    @Id
    @Column(name="module")
    private String module;

    @Column(name="last_time_process")
    private Date lastTimeProcess;

}
