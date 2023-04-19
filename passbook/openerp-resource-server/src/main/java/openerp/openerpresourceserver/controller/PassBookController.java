package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.PassBook;
import openerp.openerpresourceserver.model.ModelCreatePassBook;
import openerp.openerpresourceserver.model.ModelResponsePassbook;
import openerp.openerpresourceserver.service.PassBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class PassBookController {
    private PassBookService passBookService;

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
}
