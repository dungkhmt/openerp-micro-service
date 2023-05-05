package openerp.coderunnerservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelRunCodeFromIDE {
    String source;
    String input;
    int timeLimit;
    int memoryLimit;
}
