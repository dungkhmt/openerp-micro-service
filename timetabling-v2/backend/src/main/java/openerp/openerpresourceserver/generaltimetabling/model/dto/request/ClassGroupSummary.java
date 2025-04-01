package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassGroupSummary {
    private Long id;
    private String groupName;
    private boolean isAssigned;
}
