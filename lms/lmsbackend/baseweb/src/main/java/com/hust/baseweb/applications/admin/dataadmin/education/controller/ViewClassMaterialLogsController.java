package com.hust.baseweb.applications.admin.dataadmin.education.controller;

import com.hust.baseweb.applications.admin.dataadmin.education.service.ViewClassMaterialLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/admin/data/education/view-class-material-logs")
public class ViewClassMaterialLogsController {

    private final ViewClassMaterialLogsService viewClassMaterialLogsService;

    @GetMapping("/{studentLoginId}")
    public ResponseEntity<?> getViewClassMaterialLogsOfStudent(
        @PathVariable String studentLoginId,
        @RequestParam String search,
        @RequestParam int page,
        @RequestParam int size) {
        Pageable sortDescendingByTimestampAndPaging = PageRequest.of(
            page, size,
            Sort.by("created_stamp").descending()
        );
        return ResponseEntity.ok(
            viewClassMaterialLogsService.findViewClassMaterialLogsOfStudent(
                studentLoginId, search, sortDescendingByTimestampAndPaging
            )
        );
    }
}
