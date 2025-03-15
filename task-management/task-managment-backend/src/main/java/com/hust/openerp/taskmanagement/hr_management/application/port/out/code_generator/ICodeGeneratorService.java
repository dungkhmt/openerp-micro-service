package com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator;

import openerp.openerpresourceserver.application.port.in.port.ICodeGeneratorPort;

public interface ICodeGeneratorService {
    String generateCode(ICodeGeneratorPort port);
    String getPrefix();
    int lengthSuffix();
}
