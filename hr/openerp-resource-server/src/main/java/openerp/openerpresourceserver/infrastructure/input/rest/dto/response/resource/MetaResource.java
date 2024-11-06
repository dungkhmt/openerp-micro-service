package openerp.openerpresourceserver.infrastructure.input.rest.dto.response.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MetaResource {
    private Long code;
    private String message;

    public MetaResource(ResponseCode responseCode) {
        code = responseCode.getCode();
        message = responseCode.getMessage();
    }
}
