package openerp.openerpresourceserver.generaltimetabling.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import openerp.openerpresourceserver.generaltimetabling.exception.ConflictScheduleException;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.OccupationClassPeriod;

public class ClassTimeComparator {
    public static boolean isClassConflict(RoomReservation rr, GeneralClassOpened gClass, List<GeneralClassOpened> classList) {
        boolean isConflict = false;
        /*Filter the class which is different with current class*/
        List<GeneralClassOpened> compareClassList = classList.stream().filter(nonUpdateClass-> !nonUpdateClass.getId().equals(gClass.getId())).toList();
        /*Check conflict with current class*/
        for(int i = 0; i < gClass.getTimeSlots().size(); i++) {
            if(!Objects.equals(gClass.getTimeSlots().get(i).getId(), rr.getId())) {
                RoomReservation gTimeSlot = rr;
                RoomReservation cTimeSlot = gClass.getTimeSlots().get(i);
                if(gTimeSlot.getRoom().equals(cTimeSlot.getRoom()) && Objects.equals(gTimeSlot.getWeekday(), cTimeSlot.getWeekday())) {
                    /*Check A_start <= B_start <= A_end*/
                    if (gTimeSlot.getStartTime() <= cTimeSlot.getStartTime() && cTimeSlot.getStartTime() <= gTimeSlot.getEndTime()) {
                        throw new ConflictScheduleException("Trùng lich với ca " +  (i + 1) + "của lớp " + gClass.getClassCode() + "/" + gClass.getModuleName());
                    }
                    /*Check B_start <= A_start  <= B_end*/
                    else if (cTimeSlot.getStartTime() <= gTimeSlot.getStartTime() && gTimeSlot.getStartTime() <= cTimeSlot.getEndTime()) {
                        throw new ConflictScheduleException("Trùng lich với ca " +  (i + 1) + "của lớp " + gClass.getClassCode() + "/" + gClass.getModuleName());
                    }
                }
            }
        }

        RoomReservation gTimeSlot = rr;
        System.out.println("start: " + gTimeSlot);

        /*Check conflict with different classes*/
        for (GeneralClassOpened cClass : compareClassList) {
            /*Get learning weeks of 2 classes*/
            List<Integer> gWeekIndexs =  LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
            List<Integer> cWeekIndexs =  LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
            List<RoomReservation> cTimeSlots = cClass.getTimeSlots();
            /*Get time slot which is need to be updated of the current class*/
            for (RoomReservation cTimeSlot : cTimeSlots) {
                if (cTimeSlot.isScheduleNotNull()) {
                    /*Check 2 time slot is the same room and same day, same learning weeks and same crew*/
                    if (
                            isWeeksConflict(cWeekIndexs, gWeekIndexs)
                                    && cClass.getCrew().equals(gClass.getCrew())
                                    && gTimeSlot.getRoom().equals(cTimeSlot.getRoom())
                                    && Objects.equals(gTimeSlot.getWeekday(), cTimeSlot.getWeekday())) {
                        /*Check A_start <= B_start <= A_end*/
                        if (gTimeSlot.getStartTime() <= cTimeSlot.getStartTime() && cTimeSlot.getStartTime() <= gTimeSlot.getEndTime()) {
                            throw new ConflictScheduleException("Trùng lich với lớp " + cClass.getClassCode() + "/" + cClass.getModuleName());
                        }
                        /*Check B_start <= A_start  <= B_end*/
                        else if (cTimeSlot.getStartTime() <= gTimeSlot.getStartTime() && gTimeSlot.getStartTime() <= cTimeSlot.getEndTime()) {
                            throw new ConflictScheduleException("Trùng lich với lớp " + cClass.getClassCode() + "/" + cClass.getModuleName());
                        }
                    }
                }
            }
        }
        return isConflict;
    }

    public static GeneralClassOpened findClassConflict(RoomReservation rr, GeneralClassOpened gClass, List<GeneralClassOpened> classList) {
        /*Filter the class which is different with current class*/
        List<GeneralClassOpened> compareClassList = classList.stream().filter(nonUpdateClass-> !nonUpdateClass.getId().equals(gClass.getId())).toList();
        /*Check conflict with current class*/
        for(int i = 0; i < gClass.getTimeSlots().size(); i++) {
            if(!Objects.equals(gClass.getTimeSlots().get(i).getId(), rr.getId())) {
                RoomReservation gTimeSlot = rr;
                RoomReservation cTimeSlot = gClass.getTimeSlots().get(i);
                if(Objects.equals(gTimeSlot.getWeekday(), cTimeSlot.getWeekday())) {
                    if(gTimeSlot.getEndTime() <= 6 ) {
                        /*Check A_start < B_start < A_end*/
                        if(gTimeSlot.getStartTime() < cTimeSlot.getStartTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime() ) {
                            return gClass;
                        }
                        /*Check B_start < A_start < A_end < B_end*/
                        else if (gTimeSlot.getStartTime() > cTimeSlot.getStartTime() && gTimeSlot.getEndTime() < cTimeSlot.getEndTime()) {
                            return gClass;
                        }
                        /*Check A_start < B_end < A_end*/
                        else if (gTimeSlot.getStartTime() < cTimeSlot.getEndTime() &&  cTimeSlot.getStartTime()< gTimeSlot.getEndTime() ) {
                            return gClass;
                        }
                        /*Check if A_end equal B_start*/
                        else if (gTimeSlot.getEndTime().equals(cTimeSlot.getStartTime())) {
                            return gClass;
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
                if(isWeeksConflict(cWeekIndexs, gWeekIndexs)) {
                    List<RoomReservation> cTimeSlots = cClass.getTimeSlots();
                    List<RoomReservation> gTimeSlots = gClass.getTimeSlots();
                    /*Get time slot which is need to be updated of the current class*/
                    RoomReservation gTimeSlot = rr;
                    for(RoomReservation cTimeSlot : cTimeSlots ){
                        /*Check 2 time slot is the same room and same day*/
                        if( Objects.equals(gTimeSlot.getWeekday(), cTimeSlot.getWeekday())) {
                            if(gTimeSlot.getEndTime() <= 6 ) {
                                /*Check A_start < B_start < A_end*/
                                if(gTimeSlot.getStartTime() < cTimeSlot.getStartTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime() ) {
                                    return cClass;
                                }
                                /*Check B_start < A_start < A_end < B_end*/
                                else if (gTimeSlot.getStartTime() >= cTimeSlot.getStartTime() && gTimeSlot.getEndTime() <= cTimeSlot.getEndTime()) {
                                    return cClass;
                                }
                                /*Check A_start < B_end < A_end*/
                                else if (gTimeSlot.getStartTime() < cTimeSlot.getEndTime() &&  cTimeSlot.getStartTime()< gTimeSlot.getEndTime() ) {
                                    return cClass;
                                }
                                /*Check if A_end equal B_start*/
                                else if (gTimeSlot.getEndTime().equals(cTimeSlot.getStartTime())) {
                                    return cClass;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    private static boolean isWeeksConflict(List<Integer> weekList1, List<Integer> weekList2) {
        for (Integer item : weekList1) {
            if (weekList2.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPeriodConflict(OccupationClassPeriod comparePeriod, HashMap<String, List<OccupationClassPeriod>> periodMap) {
        for (List<OccupationClassPeriod> periodList : periodMap.values()) {
            for (OccupationClassPeriod storedPeriod  :periodList) {
                /*Check A_start < B_start < A_end*/
                if(comparePeriod.getStartPeriodIndex() < storedPeriod.getStartPeriodIndex() && storedPeriod.getStartPeriodIndex() < comparePeriod.getEndPeriodIndex()) {
                    return true;
                }
                /*Check B_start < A_start < A_end < B_end*/
                else if (comparePeriod.getStartPeriodIndex() > storedPeriod.getStartPeriodIndex() && comparePeriod.getEndPeriodIndex() < storedPeriod.getStartPeriodIndex()) {
                    return true;
                }
                /*Check A_start < B_end < A_end*/
                else if (comparePeriod.getStartPeriodIndex() < storedPeriod.getEndPeriodIndex() &&  comparePeriod.getEndPeriodIndex() > storedPeriod.getEndPeriodIndex()) {
                    return true;
                }
            }
        }
        return false;
    }
}
