package com.hust.openerp.taskmanagement.hr_management.application.service;

import lombok.RequiredArgsConstructor;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IExamplePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.IExampleUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExampleService implements IExampleUseCase {
    private final IExamplePort examplePort;
}
