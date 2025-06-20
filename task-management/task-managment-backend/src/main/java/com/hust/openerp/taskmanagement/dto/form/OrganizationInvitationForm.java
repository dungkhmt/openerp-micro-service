package com.hust.openerp.taskmanagement.dto.form;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class OrganizationInvitationForm {
    private UUID organizationId;
    private List<Invitee> invitees;

    @Data
    public static class Invitee {
        private String email;
        private String roleId;
    }
}
