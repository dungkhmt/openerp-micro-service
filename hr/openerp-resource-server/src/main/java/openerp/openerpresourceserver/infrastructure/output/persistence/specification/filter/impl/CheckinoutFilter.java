package openerp.openerpresourceserver.infrastructure.output.persistence.specification.filter.impl;

import lombok.*;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.filter.ICheckinoutFilter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckinoutFilter implements ICheckinoutFilter {
    private String userId;
    private LocalDate date;
    private CheckinoutType type;
}
