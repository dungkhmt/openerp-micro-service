package thesisdefensejuryassignment.thesisdefenseserver.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import thesisdefensejuryassignment.thesisdefenseserver.entity.AcademicKeyword;
import thesisdefensejuryassignment.thesisdefenseserver.service.AcademicKeywordServiceImpl;

import java.util.List;

@Controller
@Validated
@AllArgsConstructor
@RequestMapping("/academic_keywords")
public class AcademicKeywordController {
    @Autowired
    private AcademicKeywordServiceImpl academicKeywordService;

    @GetMapping("/get-all")
    public ResponseEntity<List<AcademicKeyword>> getThesis() {
        List<AcademicKeyword> res = academicKeywordService.getAllAcademicKeywords();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
