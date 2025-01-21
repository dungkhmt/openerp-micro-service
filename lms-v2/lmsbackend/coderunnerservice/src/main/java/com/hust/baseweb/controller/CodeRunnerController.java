package com.hust.baseweb.controller;

import com.hust.baseweb.model.ModelRunCodeFromIDE;
import com.hust.baseweb.service.Judge0ProblemTestCaseServiceImpl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeRunnerController {

    Judge0ProblemTestCaseServiceImpl problemTestCaseService;

    @PostMapping("/ide/{computerLanguage}")
    public ResponseEntity<?> runCode(@PathVariable("computerLanguage") String computerLanguage,
                                     @RequestBody ModelRunCodeFromIDE modelRunCodeFromIDE) throws Exception {
        String response = problemTestCaseService.executableIDECode(modelRunCodeFromIDE, computerLanguage);

        return ResponseEntity.status(200).body(response);
    }
}
