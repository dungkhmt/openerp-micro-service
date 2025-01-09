package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.GetCheckpointPeriodDetails;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigure;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigureDetails;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.service.CheckpointCalculator;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetAllCheckpoint;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetCheckpointStaff;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCheckpointHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointModel, GetAllCheckpoint>
{
    private final ICheckpointStaffPort checkpointStaffPort;
    private final CheckpointCalculator checkpointCalculator;

    @Override
    public void init() {
        register(GetAllCheckpoint.class,this);
    }

    @Override
    public Collection<CheckpointModel> handle(GetAllCheckpoint useCase) {
        var periodConfigureMap = checkpointCalculator.getConfiguresOfPeriod(this, useCase.getPeriodId());
        var checkpointStaffs = checkpointStaffPort.getAllCheckpointStaff(useCase);
        var checkpointStaffMap = new HashMap<String, List<CheckpointStaffModel>>();
        for (var checkpoint : checkpointStaffs) {
            if(!checkpointStaffMap.containsKey(checkpoint.getUserId())) {
                checkpointStaffMap.put(checkpoint.getUserId(), new ArrayList<>());
            }
            checkpointStaffMap.get(checkpoint.getUserId()).add(checkpoint);
        }
        var checkpointModels = new ArrayList<CheckpointModel>();
        var totalCoefficient = checkpointCalculator.getTotalCoefficient(periodConfigureMap);
        for(var entry : checkpointStaffMap.entrySet()) {
            var userId = entry.getKey();
            var checkpointStaffList = entry.getValue();
            var totalPoint = checkpointCalculator.calculatePoint(periodConfigureMap, checkpointStaffList, totalCoefficient);
            var model = CheckpointModel.builder()
                    .userId(userId)
                    .totalPoint(totalPoint)
                    .checkpointStaffs(checkpointStaffList)
                    .periodId(useCase.getPeriodId())
                    .build();
            checkpointModels.add(model);
        }
        return checkpointModels;
    }
}
