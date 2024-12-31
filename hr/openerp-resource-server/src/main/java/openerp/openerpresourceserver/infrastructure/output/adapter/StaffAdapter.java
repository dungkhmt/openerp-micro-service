package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.constant.StaffStatus;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.exception.InvalidParameterException;
import openerp.openerpresourceserver.domain.exception.StaffNotExistException;
import openerp.openerpresourceserver.domain.exception.UserNotExistException;
import openerp.openerpresourceserver.domain.model.PageWrapper;
import openerp.openerpresourceserver.domain.model.StaffModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.User;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.StaffRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.UserRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.StaffInfoSpecification;
import openerp.openerpresourceserver.infrastructure.output.persistence.utils.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StaffAdapter implements IStaffPort {
    private final StaffRepo staffRepo;
    private final UserRepo userRepo;

    @Override
    public StaffModel addStaff(StaffModel staff) {
        if(staffRepo.existsById(staff.getStaffCode())){
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

        var staffEntity =  new StaffEntity();
        staffEntity.setStaffCode(staff.getStaffCode());
        staffEntity.setFullname(staff.getFullname());
        staffEntity.setUser(user);
        staffEntity.setStatus(staff.getStatus());
        return toModel(staffRepo.save(staffEntity));
    }

    @Override
    public void editStaff(StaffModel staff) {
        StaffEntity staffEntity;
        if(staff.getUserLoginId() != null){
            staffEntity = staffRepo.findByUser_Id(staff.getUserLoginId())
                    .orElseThrow(() -> new StaffNotExistException(
                            String.format("Staff with User Login Id '%s' does not exist",
                                    staff.getUserLoginId())
                    ));
        }
        else if(staff.getStaffCode() != null){
            staffEntity = staffRepo.findById(staff.getStaffCode())
                    .orElseThrow(() -> new StaffNotExistException(
                            String.format("Staff code %s does not exist", staff.getStaffCode())
                    ));
        }
        else {
            throw new InvalidParameterException("Staff code or User Login Id is required");
        }
        if(staff.getFullname() != null){
            staffEntity.setFullname(staff.getFullname());
        }
        if(staff.getStatus() != null){
            staffEntity.setStatus(staff.getStatus());
        }
        staffRepo.save(staffEntity);
    }

    @Override
    public PageWrapper<StaffModel> findStaff(IStaffFilter filter, IPageableRequest pageableRequest) {
        var pageable = PageableUtils.getPageable(pageableRequest);
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
                .build();
    }

    private List<StaffModel> toModels(List<StaffEntity> staffEntities){
        return staffEntities.stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public String findMaxCode(String prefix) {
        return staffRepo.findMaxStaffCode(prefix);
    }
}
