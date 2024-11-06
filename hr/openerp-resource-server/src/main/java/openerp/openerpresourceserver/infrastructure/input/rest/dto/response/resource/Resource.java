package openerp.openerpresourceserver.infrastructure.input.rest.dto.response.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Resource {
    private Object data;
    private Object meta;

    public Resource(Object data) {
        this.meta = new MetaResource(ResponseCode.OK);
        this.data = data;
    }

    public Resource(Long code, String message){
        this.meta = new MetaResource(code, message);
        this.data = null;
    }

    public Resource(Long code, String message, Object data){
        this.meta = new MetaResource(code, message);
        this.data = data;
    }

    public Resource(ResponseCode responseCode, Object data){
        this.meta = new MetaResource(responseCode);
        this.data = data;
    }

}

