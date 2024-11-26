package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lmsanalytic_system_params")
public class LmsanalyticSystemParams {
    public static final String MIGRATE_CONTEST_SUBMISSION_NUMBER_ITEMS_PER_QUERY = "MIGRATE_CONTEST_SUBMISSION_NUMBER_ITEMS_PER_QUERY";

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "param")
    private String param;

    @Column(name = "value")
    private String value;

}
