package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.AcademicKeyword;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.AcademicKeywordRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class AcademicKeywordImpl implements AcademicKeywordService{
    private final AcademicKeywordRepo academicKeywordRepo;
    @Override
    public List<AcademicKeyword> getAllAcademicKeywords() {
        return academicKeywordRepo.findAll();
    }
}
