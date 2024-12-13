package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.AcademicKeywordRepo;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class AcademicKeywordServiceImpl implements AcademicKeywordService {

    @Autowired
    private AcademicKeywordRepo academicKeywordRepo;

    @Override
    public List<AcademicKeyword> getAllAcademicKeywords() {
        return academicKeywordRepo.findAll();
    }

}
