package openerp.containertransport.dto.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MapReqDTO {
    private String display_name;
    private String lat;
    private String lon;
}
