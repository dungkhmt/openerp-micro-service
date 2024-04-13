package openerp.openerpresourceserver.helper;

import java.util.List;
import java.util.stream.Collectors;

import openerp.openerpresourceserver.exception.ConflictScheduleException;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;

public class ClassTimeComparator {
    public static boolean isConflict(int scheduleIndex, GeneralClassOpened gClass, List<GeneralClassOpened> classList) {
        boolean isConflict = false;
        /*Filter the class which is different with current class*/
        List<GeneralClassOpened> compareClassList = classList.stream().filter(nonUpdateClass-> !nonUpdateClass.getId().equals(gClass.getId())).collect(Collectors.toList());
        /*Check conflict with current class*/
        for(int i = 0; i < gClass.getTimeSlots().size(); i++) {
            if(i != scheduleIndex) {
                RoomReservation gTimeSlot = gClass.getTimeSlots().get(scheduleIndex);
                RoomReservation cTimeSlot = gClass.getTimeSlots().get(i);
                if(gTimeSlot.getRoom().equals(cTimeSlot.getRoom()) && gTimeSlot.getWeekday() == cTimeSlot.getWeekday()) {
                    /*Get the duration of current time slot*/
                    int duration = gTimeSlot.getEndTime() - gTimeSlot.getStartTime();
                    if(gTimeSlot.getStartTime()+duration < 6 ) {
                        /*Check A_start < B_start < A_end*/
                        if(gTimeSlot.getStartTime() < cTimeSlot.getStartTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime() ) {
                            throw new ConflictScheduleException("Trùng lich với ca " +  (i + 1) + "của lớp " + gClass.getClassCode() + "/" + gClass.getModuleName());
                        }
                        /*Check B_start < A_start < A_end < B_end*/
                        else if (gTimeSlot.getStartTime() > cTimeSlot.getStartTime() && gTimeSlot.getEndTime() < cTimeSlot.getEndTime()) {
                            throw new ConflictScheduleException("Trùng lich với ca " +  (i + 1) + "của lớp " + gClass.getClassCode() + "/" + gClass.getModuleName());
                        }
                        /*Check A_start < B_end < A_end*/
                        else if (gTimeSlot.getStartTime() < cTimeSlot.getEndTime() &&  cTimeSlot.getStartTime()< gTimeSlot.getEndTime() ) {
                            throw new ConflictScheduleException("Trùng lich với ca " +  (i + 1) + "của lớp " + gClass.getClassCode() + "/" + gClass.getModuleName());
                        }
                    }
                }
            }
        }


        /*Check conflict with different classes*/
        for (GeneralClassOpened cClass : compareClassList) {
            /*Check if 2 class is the same crew*/
            if(cClass.getCrew().equals(gClass.getCrew())) {
                /*Get learning weeks of 2 classes*/
                List<Integer> gWeekIndexs =  LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
                List<Integer> cWeekIndexs =  LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
                /*Check 2 classes have at least 1 same week */
                if(checkWeeksConflict(cWeekIndexs, gWeekIndexs)) {
                    List<RoomReservation> cTimeSlots = cClass.getTimeSlots();
                    List<RoomReservation> gTimeSlots = gClass.getTimeSlots();
                    /*Get time slot which is need to be updated of the current class*/
                    RoomReservation gTimeSlot = gTimeSlots.get(scheduleIndex);
                    for(RoomReservation cTimeSlot : cTimeSlots ){
                        /*Check 2 time slot is the same room and same day*/
                        if(gTimeSlot.getRoom().equals(cTimeSlot.getRoom()) && gTimeSlot.getWeekday() == cTimeSlot.getWeekday()) {
                            /*Get the duration of current time slot*/
                            int duration = gTimeSlot.getEndTime() - gTimeSlot.getStartTime();
                            if(gTimeSlot.getStartTime()+duration < 6 ) {
                                /*Check A_start < B_start < A_end*/
                                if(gTimeSlot.getStartTime() < cTimeSlot.getStartTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime() ) {
                                    throw new ConflictScheduleException("Trùng lich với lớp " + cClass.getClassCode() + "/" + cClass.getModuleName());
                                }
                                /*Check B_start < A_start < A_end < B_end*/
                                else if (gTimeSlot.getStartTime() >= cTimeSlot.getStartTime() && gTimeSlot.getEndTime() <= cTimeSlot.getEndTime()) {
                                    throw new ConflictScheduleException("Trùng lich với lớp " + cClass.getClassCode() + "/" + cClass.getModuleName());
                                }
                                /*Check A_start < B_end < A_end*/
                                else if (gTimeSlot.getStartTime() < cTimeSlot.getEndTime() &&  cTimeSlot.getStartTime()< gTimeSlot.getEndTime() ) {
                                    throw new ConflictScheduleException("Trùng lich với lớp " + cClass.getClassCode() + "/" + cClass.getModuleName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return isConflict;
    }


    private static boolean checkWeeksConflict(List<Integer> weekList1, List<Integer> weekList2) {
        for (Integer item : weekList1) {
            if (weekList2.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
