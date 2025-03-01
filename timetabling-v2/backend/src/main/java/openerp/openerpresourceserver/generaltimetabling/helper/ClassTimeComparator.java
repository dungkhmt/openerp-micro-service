package openerp.openerpresourceserver.generaltimetabling.helper;

import openerp.openerpresourceserver.generaltimetabling.exception.ConflictScheduleException;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.OccupationClassPeriod;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ClassTimeComparator {
    public static boolean isClassConflict(RoomReservation rr, GeneralClass gClass, List<GeneralClass> classList) {
        boolean isConflict = false;

        // Lọc các lớp học khác với lớp hiện tại
        List<GeneralClass> compareClassList = classList.stream()
                .filter(nonUpdateClass -> !nonUpdateClass.getId().equals(gClass.getId()))
                .toList();

        // Kiểm tra xung đột với các ca học trong lớp hiện tại
        for (RoomReservation cTimeSlot : gClass.getTimeSlots()) {
            if (!Objects.equals(cTimeSlot.getId(), rr.getId())) {  // Loại bỏ trường hợp so sánh với chính nó
                if (cTimeSlot.getRoom().equals(rr.getRoom()) && Objects.equals(cTimeSlot.getWeekday(), rr.getWeekday())) {
                    // Kiểm tra thời gian học có xung đột
                    if (timeSlotsOverlap(rr, cTimeSlot)) {
                        throw new ConflictScheduleException("Trùng lịch với ca " + cTimeSlot.getStartTime() + " của lớp " + gClass.getClassCode() + "/" + gClass.getModuleName());
                    }
                }
            }
        }

        // Kiểm tra xung đột với các lớp học khác
        for (GeneralClass cClass : compareClassList) {
            List<Integer> gWeekIndexs = LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
            List<Integer> cWeekIndexs = LearningWeekExtractor.extractArray(cClass.getLearningWeeks());

            for (RoomReservation cTimeSlot : cClass.getTimeSlots()) {
                if (cTimeSlot.isScheduleNotNull()) {
                    // Kiểm tra xung đột về phòng học, ngày học, tuần học và đội ngũ giảng viên
                    if (isWeeksConflict(cWeekIndexs, gWeekIndexs) &&
                            cClass.getCrew().equals(gClass.getCrew()) &&
                            rr.getRoom().equals(cTimeSlot.getRoom()) &&
                            Objects.equals(rr.getWeekday(), cTimeSlot.getWeekday())) {
                        // Kiểm tra thời gian học có xung đột
                        if (timeSlotsOverlap(rr, cTimeSlot)) {
                            throw new ConflictScheduleException("Trùng lịch với lớp " + cClass.getClassCode() + "/" + cClass.getModuleName());
                        }
                    }
                    // Kiểm tra xung đột nếu lớp học khác có cùng phòng học và ngày học nhưng khác thời gian
                    else if (cTimeSlot.getRoom().equals(rr.getRoom()) && Objects.equals(cTimeSlot.getWeekday(), rr.getWeekday())) {
                        throw new ConflictScheduleException("Lớp học đã trùng phòng và ngày học: " + cClass.getClassCode() + "/" + cClass.getModuleName());
                    }
                }
            }
        }
        return isConflict;
    }

    // Phương thức kiểm tra xung đột thời gian giữa hai ca học
    private static boolean timeSlotsOverlap(RoomReservation rr, RoomReservation cTimeSlot) {
        // Kiểm tra xem thời gian học của hai lớp có chồng lên nhau không
        return rr.getStartTime() < cTimeSlot.getEndTime() && cTimeSlot.getStartTime() < rr.getEndTime();
    }

    public static GeneralClass findClassConflict(RoomReservation rr, GeneralClass gClass, List<GeneralClass> classList) {
        /*Filter the class which is different with current class*/
        List<GeneralClass> compareClassList = classList.stream().filter(nonUpdateClass -> !nonUpdateClass.getId().equals(gClass.getId())).toList();
        /*Check conflict with current class*/
        for (int i = 0; i < gClass.getTimeSlots().size(); i++) {
            if (!Objects.equals(gClass.getTimeSlots().get(i).getId(), rr.getId())) {
                RoomReservation gTimeSlot = rr;
                RoomReservation cTimeSlot = gClass.getTimeSlots().get(i);
                if (Objects.equals(gTimeSlot.getWeekday(), cTimeSlot.getWeekday())) {
                    if (gTimeSlot.getEndTime() <= 6) {
                        /*Check A_start < B_start < A_end*/
                        if (gTimeSlot.getStartTime() < cTimeSlot.getStartTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime()) {
                            return gClass;
                        }
                        /*Check B_start < A_start < A_end < B_end*/
                        else if (gTimeSlot.getStartTime() > cTimeSlot.getStartTime() && gTimeSlot.getEndTime() < cTimeSlot.getEndTime()) {
                            return gClass;
                        }
                        /*Check A_start < B_end < A_end*/
                        else if (gTimeSlot.getStartTime() < cTimeSlot.getEndTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime()) {
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
        for (GeneralClass cClass : compareClassList) {
            /*Check if 2 class is the same crew*/
            if (cClass.getCrew().equals(gClass.getCrew())) {
                /*Get learning weeks of 2 classes*/
                List<Integer> gWeekIndexs = LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
                List<Integer> cWeekIndexs = LearningWeekExtractor.extractArray(gClass.getLearningWeeks());
                /*Check 2 classes have at least 1 same week */
                if (isWeeksConflict(cWeekIndexs, gWeekIndexs)) {
                    List<RoomReservation> cTimeSlots = cClass.getTimeSlots();
                    List<RoomReservation> gTimeSlots = gClass.getTimeSlots();
                    /*Get time slot which is need to be updated of the current class*/
                    RoomReservation gTimeSlot = rr;
                    for (RoomReservation cTimeSlot : cTimeSlots) {
                        /*Check 2 time slot is the same room and same day*/
                        if (Objects.equals(gTimeSlot.getWeekday(), cTimeSlot.getWeekday())) {
                            if (gTimeSlot.getEndTime() <= 6) {
                                /*Check A_start < B_start < A_end*/
                                if (gTimeSlot.getStartTime() < cTimeSlot.getStartTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime()) {
                                    return cClass;
                                }
                                /*Check B_start < A_start < A_end < B_end*/
                                else if (gTimeSlot.getStartTime() >= cTimeSlot.getStartTime() && gTimeSlot.getEndTime() <= cTimeSlot.getEndTime()) {
                                    return cClass;
                                }
                                /*Check A_start < B_end < A_end*/
                                else if (gTimeSlot.getStartTime() < cTimeSlot.getEndTime() && cTimeSlot.getStartTime() < gTimeSlot.getEndTime()) {
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


    public static boolean isPeriodConflict(OccupationClassPeriod periodA, OccupationClassPeriod periodB) {
        return !(periodA.getEndPeriodIndex() < periodB.getStartPeriodIndex() ||
                periodB.getEndPeriodIndex() < periodA.getStartPeriodIndex());
    }

    public static OccupationClassPeriod findConflictPeriod(OccupationClassPeriod comparePeriod, HashMap<String, List<OccupationClassPeriod>> periodMap) {
        for (OccupationClassPeriod storedPeriod : periodMap.get(comparePeriod.getRoom())) {
            /*Check A_start <= B_start <= A_end*/
            if (comparePeriod.getStartPeriodIndex() <= storedPeriod.getStartPeriodIndex() && storedPeriod.getStartPeriodIndex() <= comparePeriod.getEndPeriodIndex()) {
                return storedPeriod;
            }
            /*Check B_start <= A_start < A_end <= B_end*/
            else if (comparePeriod.getStartPeriodIndex() >= storedPeriod.getStartPeriodIndex() && comparePeriod.getEndPeriodIndex() <= storedPeriod.getStartPeriodIndex()) {
                return storedPeriod;
            }
            /*Check A_start <= B_end <= A_end*/
            else if (comparePeriod.getStartPeriodIndex() <= storedPeriod.getEndPeriodIndex() && comparePeriod.getEndPeriodIndex() >= storedPeriod.getEndPeriodIndex()) {
                return storedPeriod;
            }
        }
        /*If any class is not conflict then return null*/
        return null;
    }
}
