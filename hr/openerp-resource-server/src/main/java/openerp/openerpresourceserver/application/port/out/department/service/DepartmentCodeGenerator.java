package openerp.openerpresourceserver.application.port.out.department.service;
import openerp.openerpresourceserver.application.port.out.code_generator.CodeGeneratorService;
import org.springframework.stereotype.Component;

@Component
public class DepartmentCodeGenerator extends CodeGeneratorService {

    @Override
    public String getPrefix() {
        return "DEPT-";
    }

    @Override
    public int lengthSuffix() {
        return 4;
    }
}
