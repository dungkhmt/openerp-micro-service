package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.PassBook;
import openerp.openerpresourceserver.model.ModelCreatePassBook;
import openerp.openerpresourceserver.model.ModelResponsePassbook;

import java.util.List;

public interface PassBookService {
    public PassBook save(String userLoginId, ModelCreatePassBook I);
    public List<ModelResponsePassbook> getPassBookList();
}
