package openerp.openerpresourceserver.generaltimetabling.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInputCreateSubClass {
    private Long fromParentClassId;
    private String classType;
    private int numberStudents;
    private int duration; // so tiet
    private int numberClasses;
}

