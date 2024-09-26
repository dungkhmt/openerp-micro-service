package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.DashboardEntity;
import com.real_estate.post.services.DashboardService;
import com.real_estate.post.utils.TypeProperty;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/dashboard")
public class PublicDashboardController {
    @Value("${dashboard.duration}")
    Long DURATION;

    @Autowired
    DashboardService dashboardService;

    @GetMapping("")
    @Operation(summary = "get pricePerM2 by districtId", operationId = "publicDashboard.getPricePerM2")
    public ResponseEntity<ResponseDto<List<DashboardEntity>>> getDashboard(
            @RequestParam(value = "fromTime" ,required = false) Long fromTime,
            @RequestParam(value = "toTime", required = false) Long toTime,
            @RequestParam("typeProperty") TypeProperty typeProperty,
            @RequestParam("districtId") String districtId
    ) {
        if (toTime == null) {
            toTime = System.currentTimeMillis();
        }
        if (fromTime == null) {
            fromTime = toTime - toTime % DURATION - 10 * DURATION - 1;
        }
        List<DashboardEntity> entities = dashboardService.getBy(fromTime, toTime, typeProperty, districtId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
    }

    @GetMapping("/top")
    @Operation(summary = "get top of province" , operationId = "publicDashboard.getTop")
    public ResponseEntity<ResponseDto<List<DashboardTopResponseDto>>> getTopOfProvince(
            @RequestParam("provinceId") String provinceId,
            @RequestParam("typeProperty") TypeProperty typeProperty
    ) {
        Long now = System.currentTimeMillis();
        Long startTime = now - now % DURATION - DURATION;
        List<DashboardTopResponseDto> result = dashboardService.getTop(provinceId, typeProperty, startTime);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, result));
    }
}
