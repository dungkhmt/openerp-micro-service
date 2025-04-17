package openerp.openerpresourceserver.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingRequest {
    int page = 0;
    int size = Integer.MAX_VALUE;
}
