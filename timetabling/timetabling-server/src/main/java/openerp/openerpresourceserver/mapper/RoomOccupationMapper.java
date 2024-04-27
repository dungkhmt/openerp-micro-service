package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.helper.LearningWeekExtractor;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RoomOccupationMapper {
    public static List<RoomOccupation> mapFromGeneralClass(GeneralClassOpened generalClassOpened) {
        List<Integer> learningWeeks = LearningWeekExtractor.extractArray(generalClassOpened.getLearningWeeks());
        return  learningWeeks.stream().map(weekIndex ->
                generalClassOpened.getTimeSlots()
                .stream().map(rr ->
                        new RoomOccupation(
                                rr.getRoom(),
                                generalClassOpened.getClassCode(),
                                rr.getStartTime(),
                                rr.getEndTime(),
                                generalClassOpened.getCrew(),
                                rr.getWeekday(),
                                weekIndex,
                                "study",
                                generalClassOpened.getSemester()
                                )
                ).toList()
        ).flatMap(List::stream).collect(Collectors.toList());
    }
}
