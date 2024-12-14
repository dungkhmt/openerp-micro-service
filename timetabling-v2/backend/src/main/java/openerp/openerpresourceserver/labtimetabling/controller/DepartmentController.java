package openerp.openerpresourceserver.labtimetabling.controller;

import openerp.openerpresourceserver.labtimetabling.entity.Department;
import openerp.openerpresourceserver.labtimetabling.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lab-timetabling/department")
public class DepartmentController{
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        List<Department> departmentList = departmentService.getAllDepartments();
        return ResponseEntity.status(HttpStatus.OK).body(departmentList);
    }
}