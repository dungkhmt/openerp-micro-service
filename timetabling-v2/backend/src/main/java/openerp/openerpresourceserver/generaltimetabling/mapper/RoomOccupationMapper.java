package openerp.openerpresourceserver.generaltimetabling.mapper;

import openerp.openerpresourceserver.generaltimetabling.helper.LearningWeekExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

import java.util.List;
import java.util.stream.Collectors;

public class RoomOccupationMapper {
    public static List<RoomOccupation> mapFromGeneralClass(GeneralClass generalClass) {
        List<Integer> learningWeeks = LearningWeekExtractor.extractArray(generalClass.getLearningWeeks());
        return  learningWeeks.stream().map(weekIndex ->
                generalClass.getTimeSlots()
                .stream().map(rr ->
                        new RoomOccupation(
                                rr.getRoom(),
                                generalClass.getClassCode(),
                                rr.getStartTime(),
                                rr.getEndTime(),
                                generalClass.getCrew(),
                                rr.getWeekday(),
                                weekIndex,
                                "study",
                                generalClass.getSemester()
                                )
                ).toList()
        ).flatMap(List::stream).collect(Collectors.toList());
    }
}
