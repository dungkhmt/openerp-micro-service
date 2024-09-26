package openerp.openerpresourceserver.labtimetabling.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class HeatValue {
    private Long room_index;
    private Long time_slot;
    private Long value;
    private Long class_index;
}
