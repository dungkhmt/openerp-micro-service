package openerp.openerpresourceserver.generaltimetabling.mapper;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.helper.LearningWeekExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Log4j2
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
    public static List<RoomOccupation> mapFromGeneralClassV2(GeneralClass generalClass) {
        List<Integer> learningWeeks = LearningWeekExtractor.extractArray(generalClass.getLearningWeeks());
        List<RoomOccupation> roomOccupations = new ArrayList();
        for(int w: learningWeeks){
            for(RoomReservation rr: generalClass.getTimeSlots()){
                RoomOccupation ro = new RoomOccupation();
                ro.setClassRoom(rr.getRoom());
                log.info("mapFromGeneralClassV2, class " + rr.getId() + " has room " + rr.getRoom());
                ro.setCrew(rr.getCrew());
                ro.setDayIndex(rr.getWeekday());
                ro.setWeekIndex(w);
                ro.setSemester(generalClass.getSemester());
                ro.setClassCode(generalClass.getClassCode());
                ro.setStartPeriod(rr.getStartTime());
                ro.setEndPeriod(rr.getEndTime());
                roomOccupations.add(ro);
            }
        }
        return roomOccupations;
    }
}
