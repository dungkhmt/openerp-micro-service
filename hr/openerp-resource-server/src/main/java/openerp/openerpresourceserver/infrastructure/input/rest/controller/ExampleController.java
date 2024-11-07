package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.usecasehandler.IExampleUseCase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ExampleController {
    private final IExampleUseCase exampleUseCase;
}
