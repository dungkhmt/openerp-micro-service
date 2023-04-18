package com.hust.baseweb.applications.education.thesisdefensejury.controller;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.AcademicKeyword;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisOM;
import com.hust.baseweb.applications.education.thesisdefensejury.service.AcademicKeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AcademicKeywordController {
    private final AcademicKeywordService academicKeywordService;
    @GetMapping("/academic_keywords")
    public ResponseEntity<?> getThesis(Pageable pageable){
        List<AcademicKeyword> res = academicKeywordService.getAllAcademicKeywords();
        return ResponseEntity.ok().body(res);
    }
}
