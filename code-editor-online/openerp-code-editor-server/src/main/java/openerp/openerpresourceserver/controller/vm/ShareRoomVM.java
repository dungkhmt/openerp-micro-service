package openerp.openerpresourceserver.controller.vm;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.enumeration.AccessPermission;

@Getter
@Setter
public class ShareRoomVM {
    private UUID roomId;

    private List<String> userIds;

    private AccessPermission accessPermission;
}
