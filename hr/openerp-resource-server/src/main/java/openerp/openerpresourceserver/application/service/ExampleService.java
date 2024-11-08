package openerp.openerpresourceserver.application.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IExamplePort;
import openerp.openerpresourceserver.application.port.out.IExampleUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExampleService implements IExampleUseCase {
    private final IExamplePort examplePort;
}
