package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CollectorDTO {


    private String collectorName;

    private String userId;

    private String hubId;
}
