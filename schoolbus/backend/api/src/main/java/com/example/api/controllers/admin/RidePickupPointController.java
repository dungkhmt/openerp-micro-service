package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.AddRidePickupPointRequest;
import com.example.api.controllers.admin.dto.DeleteRidePickupPointRequest;
import com.example.api.controllers.admin.dto.UpdateRidePickupPointRequest;
import com.example.api.services.ride_pickup_point.dto.RidePickupPointFilterParam;
import com.example.api.services.ride_pickup_point.RidePickupPointService;
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
@RequestMapping("/api/v1/admin/ride-pickup-point")
@RequiredArgsConstructor
@Slf4j
public class RidePickupPointController {
    private final RidePickupPointService ridePickupPointService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getRidePickupPoints(
        RidePickupPointFilterParam filterParam,
        Integer page, Integer size, String sort
    ) {
        Pageable pageable = PageableUtils.generate(page, size, sort);
        return ResponseUtil.toSuccessCommonResponse(
            ridePickupPointService.getListRidePickupPoint(filterParam, pageable)
        );
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> addRidePickupPoint(
        @RequestBody AddRidePickupPointRequest addRidePickupPointRequest
    ) {
        ridePickupPointService.addRidePickupPoint(addRidePickupPointRequest.toInput());

        return ResponseUtil.toSuccessCommonResponse("Ride pickup point added successfully");
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateRidePickupPoint(
        @RequestBody UpdateRidePickupPointRequest request
        ) {
        ridePickupPointService.updateRidePickupPoint(request.toInput());

        return ResponseUtil.toSuccessCommonResponse("Ride pickup point updated successfully");
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> deleteRidePickupPoint(
        @RequestBody DeleteRidePickupPointRequest request
        ) {
        ridePickupPointService.deleteRidePickupPoints(request.getRidePickupPointIds());

        return ResponseUtil.toSuccessCommonResponse("Ride pickup point deleted successfully");
    }
}
