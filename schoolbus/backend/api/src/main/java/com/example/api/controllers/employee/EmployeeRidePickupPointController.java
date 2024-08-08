package com.example.api.controllers.employee;

import com.example.api.controllers.employee.dto.UpdateRidePickupPointRequest;
import com.example.api.services.ride_pickup_point.RidePickupPointService;
import com.example.shared.db.entities.Account;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee/ride-pickup-point")
@RequiredArgsConstructor
@Slf4j
public class EmployeeRidePickupPointController {
    private final RidePickupPointService ridePickupPointService;

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateRidePickupPoint(
        @RequestBody UpdateRidePickupPointRequest request,
        @AuthenticationPrincipal Account account
    ) {
        ridePickupPointService.updateRidePickupPointEmployee(request.toInput(), account);
        return ResponseUtil.toSuccessCommonResponse("Update bus pickup point successfully");
    }
}
