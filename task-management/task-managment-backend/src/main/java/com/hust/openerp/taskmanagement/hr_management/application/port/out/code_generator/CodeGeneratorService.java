package com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator;

import openerp.openerpresourceserver.application.port.in.port.ICodeGeneratorPort;
import org.apache.commons.lang3.StringUtils;

public abstract class CodeGeneratorService implements ICodeGeneratorService {

    @Override
    public String generateCode(ICodeGeneratorPort port) {
        String maxCode = port.findMaxCode(getPrefix());
        var lengthPrefix = getPrefix().length();
        var lengthSuffix = lengthSuffix();
        int number = 1;
        if(StringUtils.isNotBlank(maxCode)) {
            String numericPart = maxCode.substring(lengthPrefix, lengthPrefix + lengthSuffix);
            number = Integer.parseInt(numericPart);
            number++;
        }
        return String.format("%s%0" + lengthSuffix + "d", getPrefix(), number);
    }
}
