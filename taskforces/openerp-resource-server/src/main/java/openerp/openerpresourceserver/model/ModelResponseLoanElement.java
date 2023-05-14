package openerp.openerpresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.entity.PassBook;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseLoanElement {
    private double amountMoneyLoan;
    private double earlyMoneyRetrieved; // khoan tien rut truoc han
    private double moneyEarly;
    private double moneyMature;
    private PassBook passBook;

}
