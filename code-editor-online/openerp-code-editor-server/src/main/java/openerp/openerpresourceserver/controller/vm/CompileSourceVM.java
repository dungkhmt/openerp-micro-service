package openerp.openerpresourceserver.controller.vm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompileSourceVM {
    private String source;

    private String input;

    private String language;
}
