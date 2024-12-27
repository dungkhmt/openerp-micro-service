package openerp.openerpresourceserver.application.port.out.job_position.service;
import openerp.openerpresourceserver.application.port.out.code_generator.CodeGeneratorService;
import org.springframework.stereotype.Component;

@Component
public class JobCodeGenerator extends CodeGeneratorService {

    @Override
    public String getPrefix() {
        return "JOB-";
    }

    @Override
    public int lengthSuffix() {
        return 4;
    }
}
