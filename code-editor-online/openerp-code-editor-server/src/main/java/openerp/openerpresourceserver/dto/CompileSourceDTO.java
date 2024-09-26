package openerp.openerpresourceserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompileSourceDTO {
    private String output;

    private Long cpuTime;

    private Boolean isKill;

    private Boolean isError;
}
