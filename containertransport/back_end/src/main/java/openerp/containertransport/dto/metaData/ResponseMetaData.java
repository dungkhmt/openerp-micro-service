package openerp.containertransport.dto.metaData;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseMetaData {
    private Object meta;
    private Object data;

    public ResponseMetaData(Object meta, Object data) {
        this.meta = meta;
        this.data = data;
    }
}
