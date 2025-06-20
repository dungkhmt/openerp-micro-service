package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.MeetingPlanDTO;
import com.hust.openerp.taskmanagement.dto.PaginationDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateMeetingPlanForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMeetingPlanForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMeetingStatusForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.service.MeetingPlanService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/meeting-plans")
@Tag(name = "Meeting Plan", description = "APIs for meeting plan management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class MeetingPlanController {

    private final MeetingPlanService meetingPlanService;
    private final ModelMapper modelMapper;

    @GetMapping("/creator/me")
    public PaginationDTO<MeetingPlanDTO> getMeetingPlansByCreatorId(Principal principal, Pageable pageable,
    		@Nullable @RequestParam(name = "q", required = false) String q) {
    	var pageRes = meetingPlanService.getMeetingPlansByCreatorId(principal.getName(), pageable, q);
        return new PaginationDTO<>(pageRes);
    }
    
    @GetMapping("/member/me")
    public PaginationDTO<MeetingPlanDTO> getMeetingPlansByMemberId(Principal principal, Pageable pageable,
    		@Nullable @RequestParam(name = "q", required = false) String q) {
        var pageRes = meetingPlanService.getMeetingPlansByMemberId(principal.getName(), pageable, q);
        return new PaginationDTO<>(pageRes);
    }

    @GetMapping("/{planId}")
    public MeetingPlanDTO getMeetingPlanById(Principal principal, @PathVariable("planId") UUID planId) {
        MeetingPlan meetingPlan =  meetingPlanService.getMeetingPlanById(principal.getName(), planId);
        return modelMapper.map(meetingPlan, MeetingPlanDTO.class);
    }

    @PostMapping
    public MeetingPlanDTO createMeetingPlan(Principal principal, 
                                            @RequestBody @Valid CreateMeetingPlanForm form) {
        return meetingPlanService.createMeetingPlan(principal.getName(), form);
    }

    @PutMapping("/{planId}")
    public void updateMeetingPlan(Principal principal, 
                                  @PathVariable("planId") UUID planId, 
                                  @RequestBody @Valid UpdateMeetingPlanForm form) {
        meetingPlanService.updateMeetingPlan(principal.getName(), planId, form);
    }
    
    @PatchMapping("/{planId}/status")
    public void updateStatus(Principal principal, 
                                  @PathVariable("planId") UUID planId, 
                                  @RequestBody @Valid UpdateMeetingStatusForm form) {
        meetingPlanService.updateStatus(principal.getName(), planId, form.getStatusId());
    }

    @DeleteMapping("/{planId}")
    public void deleteMeetingPlan(Principal principal, @PathVariable("planId") UUID planId) {
        meetingPlanService.deleteMeetingPlan(principal.getName(), planId);
    }
}
