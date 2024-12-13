package openerp.openerpresourceserver.labtimetabling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingRequest implements Serializable {
    private List<Class> classList;
    private List<Room> roomList;
}
