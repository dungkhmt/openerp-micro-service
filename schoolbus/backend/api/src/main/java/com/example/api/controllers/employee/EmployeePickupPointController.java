package com.example.api.controllers.employee;

import com.example.api.services.pickup_point.PickupPointService;
import com.example.shared.db.entities.Account;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.DateConvertUtil;
import com.example.shared.utils.ResponseUtil;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee/pickup-point")
@RequiredArgsConstructor
@Slf4j
public class EmployeePickupPointController {
    private final PickupPointService pickupPointService;

    @GetMapping("/list-ride-id")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getListRideId(
        @AuthenticationPrincipal Account account,
        String date
    ) {
        return ResponseUtil.toSuccessCommonResponse(
            pickupPointService.getListRideId(account, DateConvertUtil.convertStringToInstant(date))
        );
    }

    @GetMapping("/manipulate")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getListManipulatePickupPoint(
        @AuthenticationPrincipal Account account,
        String date,
        Long rideId
    ) {
        return ResponseUtil.toSuccessCommonResponse(
            pickupPointService.getListManipulatePickupPoint(account,
                DateConvertUtil.convertStringToInstant(date), rideId)
        );
    }
}
