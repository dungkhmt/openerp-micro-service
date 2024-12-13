package openerp.openerpresourceserver.labtimetabling.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;

import java.util.List;

@Data
@NoArgsConstructor
public class HeatMapResponse {
    private HeatMap heatMap;
    private List<Class> classList;
    private List<Room> roomList;
}
