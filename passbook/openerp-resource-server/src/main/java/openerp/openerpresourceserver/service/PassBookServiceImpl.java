package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.PassBook;
import openerp.openerpresourceserver.model.ModelCreatePassBook;
import openerp.openerpresourceserver.model.ModelResponsePassbook;
import openerp.openerpresourceserver.repo.PassBookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class PassBookServiceImpl implements PassBookService{
    private PassBookRepo passBookRepo;

    @Override
    public PassBook save(String userLoginId, ModelCreatePassBook I) {
        PassBook pb = new PassBook();
        pb.setAmountMoneyDeposit(I.getAmountMoney());
        Date currentDate = new Date();
        pb.setCreatedDate(currentDate);
        pb.setEndDate(DateTimeUtils.next(currentDate,I.getDuration()));
        pb.setUserId(I.getUserId());
        Date d = new Date();
        pb.setCreatedStamp(d);
        pb.setCreatedByUserId(userLoginId);
        pb.setDuration(I.getDuration());
        pb.setRate(I.getRate());
        pb.setPassBookName(I.getPassBookName());
        pb = passBookRepo.save(pb);
        return pb;
    }

    @Override
    public List<ModelResponsePassbook> getPassBookList() {
        List<PassBook> lst = passBookRepo.findAll();
        List<ModelResponsePassbook> res = new ArrayList<>();
        for(PassBook pb: lst){
            ModelResponsePassbook m = new ModelResponsePassbook();
            m.setPassBookId(pb.getPassBookId());
            m.setPassBookName(pb.getPassBookName());
            m.setUserId(pb.getUserId());
            m.setDuration(pb.getDuration());
            m.setAmountMoney(pb.getAmountMoneyDeposit());
            m.setRate(pb.getRate());
            m.setStartDate(pb.getCreatedDate());
            m.setEndDate(pb.getEndDate());
            res.add(m);
        }

        return res;
    }

    @Override
    public boolean removePassBook(UUID passBookId) {
        PassBook pb = passBookRepo.findById(passBookId).orElse(null);
        if(pb == null)
        return false;
        passBookRepo.delete(pb);
        return true;
    }
}
