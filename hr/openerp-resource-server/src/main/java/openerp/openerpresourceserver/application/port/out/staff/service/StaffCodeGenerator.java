package openerp.openerpresourceserver.application.port.out.staff.service;
import openerp.openerpresourceserver.application.port.out.code_generator.CodeGeneratorService;
import org.springframework.stereotype.Component;

@Component
public class StaffCodeGenerator extends CodeGeneratorService {

    @Override
    public String getPrefix() {
        return "EMP-";
    }

    @Override
    public int lengthSuffix() {
        return 4;
    }
}
