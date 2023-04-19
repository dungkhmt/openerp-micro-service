package openerp.openerpresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseOptimizePassBookForLoan {
    private int score;
    private List<ModelResponseLoanElement> loans;
}
