package openerp.openerpresourceserver.tarecruitment.entity.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;

@Getter
@Setter
public class RequestUpdateDTO {

    private String name;

    private String mssv;

    private String phoneNumber;

    private String email;

    private double CPA;

    private String englishScore;

    private String note;
}
