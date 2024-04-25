package openerp.openerpresourceserver.controller;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/company")
public class CompanyController {
    private CompanyService companyService;
    private UserService userService;
    @GetMapping("/all")
    public ResponseEntity<?> getAllCompany() {
        List<Company> companyList = companyService.getAll();
        return ResponseEntity.ok(companyList);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getCompany(@PathVariable String id) {
        Company company = companyService.getByUserId(id);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyByJobId(@PathVariable Integer id) {
        Company company = companyService.getById(id);
        return ResponseEntity.ok(company);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Company company) {
        Company company1 = companyService.save(company);
        return ResponseEntity.ok(company1);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Company company) {
        Company company1 = companyService.save(company);
        return ResponseEntity.ok(company1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        companyService.deleteById(id);
        return ResponseEntity.ok("deleted company with id " + id);
    }
}
