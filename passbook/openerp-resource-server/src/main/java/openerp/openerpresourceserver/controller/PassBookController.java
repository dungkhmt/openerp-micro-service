package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import openerp.openerpresourceserver.entity.PassBook;
import openerp.openerpresourceserver.model.ModelCreatePassBook;
import openerp.openerpresourceserver.model.ModelInputComputeLoanSolution;
import openerp.openerpresourceserver.model.ModelResponseOptimizePassBookForLoan;
import openerp.openerpresourceserver.model.ModelResponsePassbook;
import openerp.openerpresourceserver.service.PassBookOptimizer;
import openerp.openerpresourceserver.service.PassBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class PassBookController {
    private PassBookService passBookService;
    private PassBookOptimizer passBookOptimizer;

    @PostMapping("/create-passbook")
    public ResponseEntity<?> createPassBook(Principal principal, @RequestBody ModelCreatePassBook model){
        PassBook pb = passBookService.save(principal.getName(), model);
        return ResponseEntity.ok().body(pb);
    }
    @GetMapping("/get-passbook-list")
    public ResponseEntity<?> getPassBookList(Principal principal){
        List<ModelResponsePassbook> res = passBookService.getPassBookList();
        return ResponseEntity.ok().body(res);
    }
    @GetMapping("/remove-passbook/{passBookId}")
    public ResponseEntity<?> removePassBook(Principal principal, @PathVariable UUID passBookId){
        boolean ok = passBookService.removePassBook(passBookId);
        return ResponseEntity.ok().body(ok);
    }
    @PostMapping("/compute-loan-solution")
    public ResponseEntity<?> computeLoanSolution(Principal principal, @RequestBody ModelInputComputeLoanSolution model){
        System.out.println("computeLoanSolution, date = " + model.getDate());
        ModelResponseOptimizePassBookForLoan res = passBookOptimizer.computeSolution(model);
        return ResponseEntity.ok().body(res);

    }
}
