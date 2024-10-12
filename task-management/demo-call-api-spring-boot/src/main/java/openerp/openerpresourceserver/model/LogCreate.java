package openerp.openerpresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogCreate {
    private String userId;
    private String ActionType;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;
    private String description;
}
