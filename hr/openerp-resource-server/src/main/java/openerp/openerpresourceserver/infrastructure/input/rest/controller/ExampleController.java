package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.usecase.IExampleUseCase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ExampleController {
    private final IExampleUseCase exampleUseCase;
}
