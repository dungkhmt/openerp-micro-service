package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.PassBook;
import openerp.openerpresourceserver.model.ModelCreatePassBook;
import openerp.openerpresourceserver.model.ModelResponsePassbook;

import java.util.List;
import java.util.UUID;

public interface PassBookService {
    public PassBook save(String userLoginId, ModelCreatePassBook I);
    public List<ModelResponsePassbook> getPassBookList();

    public boolean removePassBook(UUID passBookId);
}
