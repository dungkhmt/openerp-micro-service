package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.AddPickupPointRequest;
import com.example.api.controllers.admin.dto.DeletePickupPointRequest;
import com.example.api.controllers.admin.dto.PickupPointFilterParam;
import com.example.api.controllers.admin.dto.UpdatePickupPointRequest;
import com.example.api.services.pickup_point.PickupPointService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/pickup-point")
@RequiredArgsConstructor
@Slf4j
public class PickupPointController {
    private final PickupPointService pickupPointService;

    @GetMapping("/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getPickupPoints(PickupPointFilterParam filterParam,
                                                                  Integer page, Integer size,
                                                                  String sort) {
        if (size == null || size == 0) {
            size = 10000000;
        }
        Pageable pageable = PageableUtils.generate(page, size, sort);

        return ResponseUtil.toSuccessCommonResponse(
            pickupPointService.getListPickupPoint(filterParam, pageable)
        );
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> addPickupPoint(
            @RequestBody AddPickupPointRequest addPickupPointRequest
    ) {
        pickupPointService.addPickupPoint(addPickupPointRequest.toInput());

        return ResponseUtil.toSuccessCommonResponse("Pickup point added successfully" );
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updatePickupPoint(
        @RequestBody UpdatePickupPointRequest updatePickupPointRequest
    ) {
        pickupPointService.updatePickupPoint(updatePickupPointRequest.toInput());

        return ResponseUtil.toSuccessCommonResponse("Pickup point updated successfully");
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> deletePickupPoint(
        @RequestBody DeletePickupPointRequest deletePickupPointRequest
        ) {
        pickupPointService.deletePickupPoint(deletePickupPointRequest.getId());

        return ResponseUtil.toSuccessCommonResponse("Pickup point deleted successfully");
    }

}
