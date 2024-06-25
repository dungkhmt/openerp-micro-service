package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.AddBusRequest;
import com.example.api.controllers.admin.dto.BusManipulateParam;
import com.example.api.controllers.admin.dto.DeleteBusRequest;
import com.example.api.controllers.admin.dto.UpdateBusRequest;
import com.example.api.services.bus.BusService;
import com.example.api.services.bus.dto.ListBusFilterParam;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.PageableUtils;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/bus")
@RequiredArgsConstructor
@Slf4j
public class BusController {
    private final BusService busService;

    @GetMapping("/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getBuses(ListBusFilterParam filterParam,
                                                           Integer page, Integer size,
                                                           String sort) {
        Pageable pageable = PageableUtils.generate(page, size, sort);

        return ResponseUtil.toSuccessCommonResponse(
                busService.getListBus(filterParam, pageable)
        );
    }

    @GetMapping("/{busId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getBus(
        @PathVariable Long busId
        ) {
        return ResponseUtil.toSuccessCommonResponse(
            busService.getBus(busId)
        );
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> addBus(
        @RequestBody AddBusRequest request
        ) {
        busService.addBus(request.toInput());
        return ResponseUtil.toSuccessCommonResponse("Add bus successfully");
    }

    @PutMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateBus(
        @RequestBody UpdateBusRequest request
        ) {
        busService.updateBus(request.toInput());
        return ResponseUtil.toSuccessCommonResponse("Update bus successfully");
    }

    @DeleteMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> deleteBus(
        @RequestBody DeleteBusRequest request
        ) {
        busService.deleteBus(request.getId());
        return ResponseUtil.toSuccessCommonResponse("Delete bus successfully");
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getAvailableBuses(
        @RequestParam(required = false) EmployeeRole role,
        @RequestParam(required = false) String numberPlate
        ) {
        return ResponseUtil.toSuccessCommonResponse(
            busService.getAvailableBuses(role, numberPlate)
        );
    }

    @GetMapping("/manipulate")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getListManipulateBus(
        BusManipulateParam param,
        Integer page, Integer size, String sort
        ) {
        Pageable pageable = PageableUtils.generate(page, size, sort);

        return ResponseUtil.toSuccessCommonResponse(
            busService.getListManipulateBusPage(param, pageable)
        );
    }

}
