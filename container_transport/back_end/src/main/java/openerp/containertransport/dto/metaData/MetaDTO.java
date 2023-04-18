package openerp.containertransport.dto.metaData;

import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.containertransport.constants.MetaData;

@Data
@NoArgsConstructor
public class MetaDTO {
    private Integer code;
    private String message;

    public MetaDTO(MetaData metaData) {
        this.code = metaData.getMetaCode();
        this.message = metaData.getMessage();
    }

    public MetaDTO(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
