package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.UpsertRideRequest;
import com.example.api.controllers.admin.dto.UpdateRideRequest;
import com.example.api.services.ride.RideService;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/ride")
@RequiredArgsConstructor
@Slf4j
public class RideController {
    private final RideService rideService;

    @PostMapping("/upsert")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> upsertRide(
            @RequestBody UpsertRideRequest upsertRideRequest
    ) {
        rideService.upsertRide(upsertRideRequest.toInput());

        return ResponseUtil.toSuccessCommonResponse("Ride added successfully" );
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateRide(
            @RequestBody UpdateRideRequest updateRideRequest
    ) {
        rideService.updateRide(updateRideRequest.toInput());

        return ResponseUtil.toSuccessCommonResponse("Ride updated successfully");
    }
}
