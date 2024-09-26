package com.example.api.controllers.admin;

import com.example.api.services.history.HistoryService;
import com.example.api.services.history.dto.AdminHistoryRideFilterParam;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.PageableUtils;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/history")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/ride/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getAdminHistoryRides(
        AdminHistoryRideFilterParam filterParam
    ) {
        Pageable pageable = PageableUtils.generate(
            filterParam.getPage(),
            filterParam.getSize(),
            filterParam.getSort(),
            "-id"
        );

        return ResponseUtil.toSuccessCommonResponse(
            historyService.getAdminHistoryRides(filterParam, pageable)
        );
    }
}
