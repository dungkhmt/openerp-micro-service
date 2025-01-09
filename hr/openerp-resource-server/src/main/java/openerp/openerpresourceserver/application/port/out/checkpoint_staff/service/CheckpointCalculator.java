package openerp.openerpresourceserver.application.port.out.checkpoint_staff.service;

import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigure;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CheckpointCalculator {
    public BigDecimal calculatePoint(
            Map<String , CheckpointPeriodConfigureModel> periodConfigureMap,
            List<CheckpointStaffModel> checkpointStaffModels,
            BigDecimal totalCoefficient
    ){
        if(checkpointStaffModels.isEmpty()) return null;

        BigDecimal totalPoint = BigDecimal.ZERO;
        for (var model : checkpointStaffModels){
            var periodConfigure = periodConfigureMap.getOrDefault(model.getConfigureId(), null);
            if(periodConfigure == null) continue;
            var point = model.getPoint().multiply(periodConfigure.getCoefficient());
            totalPoint = totalPoint.add(point);
        }

        return totalPoint.divide(totalCoefficient, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculatePoint(
            Map<String , CheckpointPeriodConfigureModel> periodConfigureMap,
            List<CheckpointStaffModel> checkpointStaffModels
    ){
        var totalCoefficient = getTotalCoefficient(periodConfigureMap);
        return calculatePoint(periodConfigureMap, checkpointStaffModels, totalCoefficient);
    }

    public BigDecimal calculatePoint(
            ObservableUseCasePublisher publisher,
            UUID checkpointPeriodId,
            List<CheckpointStaffModel> checkpointStaffModels
    ){
        var periodConfigureMap = getConfiguresOfPeriod(publisher, checkpointPeriodId);
        var totalCoefficient = getTotalCoefficient(periodConfigureMap);
        return calculatePoint(periodConfigureMap, checkpointStaffModels, totalCoefficient);
    }

    public Map<String , CheckpointPeriodConfigureModel> getConfiguresOfPeriod(
            ObservableUseCasePublisher publisher,
            UUID checkpointPeriodId
    ) {
        var useCase = new GetCheckpointPeriodConfigure(checkpointPeriodId, CheckpointPeriodConfigureStatus.ACTIVE);
        var configuresCollection = publisher.publishCollection(CheckpointPeriodConfigureModel.class, useCase);
        return configuresCollection.stream()
                .collect(Collectors.toMap(
                        CheckpointPeriodConfigureModel::getConfigureId,
                        Function.identity(),
                        (existingValue, newValue) -> existingValue
                ));
    }

    public BigDecimal getTotalCoefficient(Map<String , CheckpointPeriodConfigureModel> configuresOfPeriod){
        var totalCoefficient = BigDecimal.ZERO;
        for(var value : configuresOfPeriod.values()){
            totalCoefficient = totalCoefficient.add(value.getCoefficient());
        }
        return totalCoefficient;
    }

}
