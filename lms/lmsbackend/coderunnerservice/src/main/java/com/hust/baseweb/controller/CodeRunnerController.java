package com.hust.baseweb.controller;

import com.hust.baseweb.model.ModelRunCodeFromIDE;
import com.hust.baseweb.service.ProblemTestCaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CodeRunnerController {
    private ProblemTestCaseService problemTestCaseService;

    @PostMapping("/ide/{computerLanguage}")
    public ResponseEntity<?> runCode(@PathVariable("computerLanguage") String computerLanguage,
                                     @RequestBody ModelRunCodeFromIDE modelRunCodeFromIDE) throws Exception {
        String response = problemTestCaseService.executableIDECode(modelRunCodeFromIDE, computerLanguage);

        return ResponseEntity.status(200).body(response);
    }
}
