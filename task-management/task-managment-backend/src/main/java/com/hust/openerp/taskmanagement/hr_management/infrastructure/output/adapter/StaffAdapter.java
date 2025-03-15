package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.filter.IStaffFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.InvalidParameterException;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.StaffNotExistException;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.UserNotExistException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.StaffEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.User;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.StaffRepo;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.UserRepo;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification.StaffInfoSpecification;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StaffAdapter implements IStaffPort {
    private final StaffRepo staffRepo;
    private final UserRepo userRepo;

    @Transactional
    @Override
    public StaffModel addStaff(StaffModel staff) {
        if(staffRepo.existsById(staff.getStaffCode())){
            log.error(String.format("Staff code %s existed", staff.getStaffCode()));
            throw new ApplicationException(
                    ResponseCode.STAFF_EXISTED,
                    String.format("Staff code %s existed", staff.getStaffCode())
            );
        }
        User user;
        if(staff.getUserLoginId() != null){
            user = userRepo.findById(staff.getStaffCode()).orElseThrow(
                    () -> new UserNotExistException(String.format("user id %s does not exist", staff.getUserLoginId()))
            );
        }
        else if(staff.getEmail() != null){
            user = userRepo.findByEmail(staff.getEmail()).orElseThrow(
                    () -> new UserNotExistException(String.format("user email %s does not exist", staff.getEmail()))
            );
        }
        else
            throw new InvalidParameterException("user login id or email is required");

        if(staffRepo.existsByUser(user)){
            throw new  ApplicationException(
                    ResponseCode.STAFF_EXISTED,
                    String.format("Staff existed by email %s", staff.getEmail())
            );
        }


        var staffEntity =  new StaffEntity();
        staffEntity.setStaffCode(staff.getStaffCode());
        staffEntity.setFullname(staff.getFullname());
        staffEntity.setUser(user);
        staffEntity.setStatus(staff.getStatus());
        return toModel(staffRepo.save(staffEntity));
    }

    @Override
    public StaffModel editStaff(StaffModel staff) {
        StaffEntity staffEntity;
        if(staff.getUserLoginId() != null){
            staffEntity = findEntityByUserLoginId(staff.getUserLoginId());
        }
        else if(staff.getStaffCode() != null){
            staffEntity = findEntityByStaffCode(staff.getStaffCode());
        }
        else {
            log.error("Staff code or User Login Id is required");
            throw new InvalidParameterException("Staff code or User Login Id is required");
        }

        if(staff.getFullname() != null){
            staffEntity.setFullname(staff.getFullname());
        }
        if(staff.getStatus() != null){
            staffEntity.setStatus(staff.getStatus());
        }
        return toModel(staffRepo.save(staffEntity));
    }

    @Override
    public StaffModel findByStaffCode(String staffCode) {
        return toModel(findEntityByStaffCode(staffCode));
    }

    @Override
    public StaffModel findByUserLoginId(String userLoginId) {
        return toModel(findEntityByUserLoginId(userLoginId));
    }

    @Override
    public PageWrapper<StaffModel> findStaff(IStaffFilter filter, IPageableRequest pageableRequest) {
        var pageable = PageableUtils.getPageable(pageableRequest, "staffCode");
        var spec = new StaffInfoSpecification(filter);
        var page = staffRepo.findAll(spec ,pageable);
        return PageWrapper.<StaffModel>builder()
                .pageInfo(PageableUtils.getPageInfo(page))
                .pageContent(toModels(page.getContent()))
                .build();
    }

    @Override
    public List<StaffModel> findStaff(IStaffFilter filter) {
        var spec = new StaffInfoSpecification(filter);
        var staffEntities = staffRepo.findAll(spec);
        return toModels(staffEntities);
    }

    private StaffModel toModel(StaffEntity staffEntity){
        return StaffModel.builder()
                .staffCode(staffEntity.getStaffCode())
                .fullname(staffEntity.getFullname())
                .userLoginId(staffEntity.getUser().getId())
                .email(staffEntity.getUser().getEmail())
                .status(staffEntity.getStatus())
                .dateOfJoin(staffEntity.getCreatedStamp())
                .build();
    }

    private List<StaffModel> toModels(List<StaffEntity> staffEntities){
        return staffEntities.stream()
                .map(this::toModel)
                .toList();
    }

    public StaffEntity findEntityByStaffCode(String staffCode) {
        return staffRepo.findById(staffCode)
                .orElseThrow(() -> new StaffNotExistException(
                        String.format("Staff code %s does not exist", staffCode)
                ));
    }


    public StaffEntity findEntityByUserLoginId(String userLoginId) {
        return staffRepo.findByUser_Id(userLoginId)
                .orElseThrow(() -> new StaffNotExistException(
                        String.format("Staff with User Login Id '%s' does not exist",
                                userLoginId)
                ));
    }

    @Override
    public String findMaxCode(String prefix) {
        return staffRepo.findMaxStaffCode(prefix);
    }
}
