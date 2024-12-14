package openerp.openerpresourceserver.labtimetabling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchedulingResponse implements Serializable {
    private String response;
}
