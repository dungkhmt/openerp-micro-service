package com.hust.openerp.taskmanagement.hr_management.application.service;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IExamplePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.IExampleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExampleService implements IExampleUseCase {
    private final IExamplePort examplePort;
}
