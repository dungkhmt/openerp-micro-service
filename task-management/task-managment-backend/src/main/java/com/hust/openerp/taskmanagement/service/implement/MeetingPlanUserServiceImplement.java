package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.algorithm.HopcroftKarpBinarySearch;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignRequestDTO;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignResponseDTO;
import com.hust.openerp.taskmanagement.dto.MeetingPlanUserDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.AddMeetingPlanUserForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberAssignmentsForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser.MeetingPlanUserId;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.MeetingPlanUserRepository;
import com.hust.openerp.taskmanagement.repository.MeetingSessionUserRepository;
import com.hust.openerp.taskmanagement.service.AssignmentService;
import com.hust.openerp.taskmanagement.service.MeetingPlanUserService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import com.hust.openerp.taskmanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MeetingPlanUserServiceImplement implements MeetingPlanUserService {

    private final MeetingPlanUserRepository meetingPlanUserRepository;
    private final MeetingSessionUserRepository meetingSessionUserRepository;
    private final UserService userService;
    private final PermissionService permissionService;
    private final ModelMapper modelMapper;
    private final AssignmentService assignmentService;

    @Override
    public List<User> getAllMeetingPlanUsers(String userId, UUID planId) {
        permissionService.checkMeetingPlanCreatorOrMember(userId, planId);
        return meetingPlanUserRepository.findUsersByPlanId(planId);
    }

    @Override
    public void addMeetingPlanUser(String userId, AddMeetingPlanUserForm addForm) {
        UUID planId = addForm.getPlanId();
        permissionService.checkMeetingPlanCreator(userId, planId);

        List<String> memberIds = addForm.getUserId();
        for (String memberId : memberIds) {
            User member = userService.findById(memberId);
            if (member == null) {
                throw new ApiException(ErrorCode.USER_NOT_EXIST);
            }
            MeetingPlanUser meetingPlanUser = MeetingPlanUser.builder().planId(planId).userId(memberId).build();
            meetingPlanUserRepository.save(meetingPlanUser);
        }
    }

    @Override
    @Transactional
    public void removeMeetingPlanUser(String userId, UUID planId, String memberId) {
        permissionService.checkMeetingPlanCreator(userId, planId);

        meetingSessionUserRepository.deleteByUserId(userId);

        MeetingPlanUserId compositeKey = new MeetingPlanUserId(planId, memberId);
        if (!meetingPlanUserRepository.existsById(compositeKey)) {
            throw new ApiException(ErrorCode.MEETING_PLAN_USER_NOT_FOUND);
        }
        meetingPlanUserRepository.deleteById(compositeKey);
    }

    @Override
    public MeetingSessionDTO getMyAssignment(String userId, UUID planId) {
        permissionService.checkMeetingPlanMember(userId, planId);

        MeetingPlanUser res = meetingPlanUserRepository.findByPlanIdAndUserId(planId, userId);
        if (res == null || res.getMeetingSession() == null) return null;
        return modelMapper.map(res.getMeetingSession(), MeetingSessionDTO.class);
    }

    @Override
    public List<MeetingPlanUserDTO> getMemberAssignments(String userId, UUID planId) {
        permissionService.checkMeetingPlanCreator(userId, planId);

        List<MeetingPlanUser> res = meetingPlanUserRepository.findByPlanId(planId);
        return res.stream().map(plan -> modelMapper.map(plan, MeetingPlanUserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void updateMemberAssignment(String userId, UUID planId, UpdateMemberAssignmentsForm assignments) {
        permissionService.checkMeetingPlanCreator(userId, planId);

        for (MeetingPlanUserDTO assignment : assignments.getAssignments()) {
            MeetingPlanUserId compositeKey = new MeetingPlanUserId(planId, assignment.getUserId());
            MeetingPlanUser member = meetingPlanUserRepository.findById(compositeKey)
                .orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_USER_NOT_FOUND));
            member.setSessionId(assignment.getSessionId());
            meetingPlanUserRepository.save(member);
        }
    }

    @Override
    public MeetingAutoAssignResponseDTO autoAssignMembers(String userId, UUID planId,
                                                          MeetingAutoAssignRequestDTO request) {
        permissionService.checkMeetingPlanCreator(userId, planId);

        return assignmentService.autoAssign(request.getMemberPreferences());
    }
}