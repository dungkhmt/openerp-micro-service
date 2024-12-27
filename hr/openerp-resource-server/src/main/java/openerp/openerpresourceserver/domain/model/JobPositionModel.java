package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobPositionModel {
    private String code;
    private String name;
    private String description;
}
