// ==============
// ShiftScheduler.jsx
// ==============
import React, {useCallback, useEffect, useState, useMemo} from 'react'; // Added useMemo
import {addDays, format, getDay, isValid, parseISO, startOfWeek, subDays, isEqual, endOfWeek} from 'date-fns';
import vi from 'date-fns/locale/vi';
import {DragDropContext} from 'react-beautiful-dnd';
import {Box, Container, Paper, Typography, Checkbox, CircularProgress} from '@mui/material'; // Added CircularProgress
import {LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDateFns} from '@mui/x-date-pickers/AdapterDateFns';
import AccessTimeFilledIcon from '@mui/icons-material/AccessTimeFilled';

import CopyShiftsModal from "./CopyShiftsModal.jsx";
import ShiftModal from "./ShiftModal.jsx";
import CalendarHeader from "./CalendarHeader.jsx";
import ShiftsGrid from "./ShiftsGrid.jsx"; // Will be updated
import InfoBanners from "./InfoBanners.jsx";
import BulkActionsBar from "./BulkActionBar.jsx";
import TopBar from "./TopBar.jsx";
import UnassignedShiftsRow from "./UnassignedShiftsRow.jsx";
import SchedulingConflictModal from "./SchedulingConflictModal.jsx";
import DeleteConfirmationModal from "../modals/DeleteConfirmationModal.jsx";
import {request} from "../../../api.js";
import toast from "react-hot-toast";

export const TOP_BAR_HEIGHT = 61;
export const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
// export const PROJECTED_SALES_BANNER_HEIGHT = 0; // Already commented out in original InfoBanners
export const INFO_BANNERS_TOTAL_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT; // Adjusted
export const BULK_ACTIONS_BAR_HEIGHT = 50;
export const CALENDAR_HEADER_HEIGHT = 57;
export const UNASSIGNED_ROW_HEIGHT = 60;
export const WEEK_STARTS_ON = 1;
export const UNASSIGNED_SHIFT_USER_ID = 'UNASSIGNED';
export const DEPARTMENT_HEADER_ROW_HEIGHT = 25; // New constant for department row
export const USER_ROW_MIN_HEIGHT = 60; // Existing, for user rows in grid


const initialUnassignedShifts = [
  //{ id: 's-unassigned-tue', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-20', startTime: '10:00', endTime: '14:00', note: 'General Task Tue', slots: 3, muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned' },
  //{ id: 's-unassigned-wed', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-21', startTime: '15:00', endTime: '18:00', note: 'Evening Cover Wed', slots: 1, muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned' },
];

const calculateDuration = (day, startTime, endTime) => {
  if (!day || !startTime || !endTime) return '0h 0m';
  const start = parseISO(`${day}T${startTime}`);
  const end = parseISO(`${day}T${endTime}`);
  if (!isValid(start) || !isValid(end)) return '0h 0m';
  let durationMs = end.getTime() - start.getTime();
  if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; } // Handles overnight shifts
  const h = Math.floor(durationMs / (1000 * 60 * 60));
  const m = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
  return `${h}h ${m}m`;
};

const calculateUserWorkHours = (userId, allShifts) => {
  const userShifts = allShifts.filter(s => s.userId === userId && s.type !== 'time_off' && s.userId !== UNASSIGNED_SHIFT_USER_ID);
  let totalMs = 0;
  userShifts.forEach(s => {
    if (s.day && s.startTime && s.endTime) {
      const start = parseISO(`${s.day}T${s.startTime}`);
      const end = parseISO(`${s.day}T${s.endTime}`);
      if(isValid(start) && isValid(end)) {
        let diff = end.getTime() - start.getTime();
        if (diff < 0) diff += 24 * 60 * 60 * 1000;
        totalMs += diff;
      }
    }
  });
  const h = Math.floor(totalMs / (1000 * 60 * 60));
  const m = Math.floor((totalMs % (1000 * 60 * 60)) / (1000 * 60));
  return `${h}h ${m}m`;
};

const detectConflicts = (shiftToCheck, allShifts, targetUserId, usersList, currentShiftIdToExclude = null) => {
  const conflicts = [];
  if (!shiftToCheck.day || !shiftToCheck.startTime || !shiftToCheck.endTime || !targetUserId) {
    return conflicts;
  }
  const startA_raw = parseISO(`${shiftToCheck.day}T${shiftToCheck.startTime}`);
  const endA_raw = parseISO(`${shiftToCheck.day}T${shiftToCheck.endTime}`);
  if (!isValid(startA_raw) || !isValid(endA_raw)) return [];

  const startA = startA_raw;
  const endA = endA_raw < startA_raw ? addDays(endA_raw, 1) : endA_raw;

  allShifts.forEach(existingShift => {
    if (existingShift.userId !== targetUserId) return;
    if (currentShiftIdToExclude && existingShift.id === currentShiftIdToExclude) return;
    if (!existingShift.day || !existingShift.startTime || !existingShift.endTime) return;

    const startB_raw = parseISO(`${existingShift.day}T${existingShift.startTime}`);
    const endB_raw = parseISO(`${existingShift.day}T${existingShift.endTime}`);
    if (!isValid(startB_raw) || !isValid(endB_raw)) return;

    const startB = startB_raw;
    const endB = endB_raw < startB_raw ? addDays(endB_raw, 1) : endB_raw;

    if (startA < endB && endA > startB) {
      const user = usersList.find(u => u.id === targetUserId);
      const conflictingShiftType = existingShift.type === 'time_off' ? 'lịch nghỉ' : 'ca làm việc';
      conflicts.push({
        type: 'TimeOverlap',
        shiftId: existingShift.id,
        userName: user ? user.name : targetUserId,
        day: format(parseISO(existingShift.day), 'EEE, MMM dd', { locale: vi }),
        timeRange: `${existingShift.startTime} - ${existingShift.endTime}`,
        message: `Trùng với ${conflictingShiftType} của ${user ? user.name : 'NV này'} (${existingShift.startTime} - ${existingShift.endTime} ngày ${format(parseISO(existingShift.day), 'dd/MM', { locale: vi })})`
      });
    }
  });
  return conflicts;
};


export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date('2025-05-19').setHours(0,0,0,0))); // Ensure it matches mock data for initial view
  const [shifts, setShifts] = useState([]); // Initialize with empty array, will be populated by API calls

  const [rawUsers, setRawUsers] = useState([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  const [isLoadingShifts, setIsLoadingShifts] = useState(true); // New loading state for all shifts

  const [isModalOpen, setIsModalOpen] = useState(false);
  // ... (other states remain the same)
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [modalInitialFormState, setModalInitialFormState] = useState({
    userIds: [], day: format(new Date(), 'yyyy-MM-dd'), startTime: '09:00',
    endTime: '17:00', note: '', slots: 1,
  });
  const [selectedShiftIds, setSelectedShiftIds] = useState([]);
  const [isCopyModalOpen, setIsCopyModalOpen] = useState(false);
  const [unassignedRowSticky, setUnassignedRowSticky] = useState(true);
  const [modalOpeningContext, setModalOpeningContext] = useState(null);

  const [isDeleteSingleModalOpen, setIsDeleteSingleModalOpen] = useState(false);
  const [shiftIdToDelete, setShiftIdToDelete] = useState(null);
  const [isDeleteSelectedModalOpen, setIsDeleteSelectedModalOpen] = useState(false);

  const [schedulingConflicts, setSchedulingConflicts] = useState([]);
  const [isConflictModalOpen, setIsConflictModalOpen] = useState(false);
  const [pendingShiftOperation, setPendingShiftOperation] = useState(null);


  const isAnyShiftSelected = selectedShiftIds.length > 0;

  // Derived state for users with work hours
  const allUsers = useMemo(() => {
    return rawUsers.map(user => ({
      ...user,
      workHoursSummary: calculateUserWorkHours(user.id, shifts)
    }));
  }, [rawUsers, shifts]);

  // Grouped users for the grid
  const groupedUsersForGrid = useMemo(() => {
    // ... (same as previous version)
    if (isLoadingUsers || allUsers.length === 0) return [];
    const groups = {};
    allUsers.forEach(user => {
      const deptKey = user.departmentCode;
      if (!groups[deptKey]) {
        groups[deptKey] = {
          departmentCode: user.departmentCode,
          departmentName: user.departmentName,
          users: []
        };
      }
      groups[deptKey].users.push(user);
    });

    return Object.values(groups)
      .sort((a, b) => a.departmentName.localeCompare(b.departmentName))
      .map(group => {
        group.users.sort((a, b) => a.name.localeCompare(b.name));
        return group;
      });
  }, [allUsers, isLoadingUsers]);


  // Function to fetch absences (time off)
  const fetchAbsences = async (userIds, start, end) => {
    if (!userIds || userIds.length === 0) return [];
    return new Promise((resolve) => {
      const params = new URLSearchParams();
      userIds.forEach(id => params.append("userIds", id));
      params.append("startDate", start); // format YYYY-MM-DD
      params.append("endDate", end);     // format YYYY-MM-DD

      request(
        "get",
        `/absences?${params.toString()}`,
        (res) => resolve(res.data?.data || []),
        (err) => {
          console.error("Failed to fetch absences:", err);
          resolve([]); // Resolve with empty array on error
        },
        null
      );
    });
  };

  // Function to fetch regular shifts (TODO: Implement actual API call)
  const fetchRegularShifts = async (userIds, start, end) => {
    console.log("TODO: Fetch regular shifts from API for users:", userIds, "between", start, "and", end);
    // This is where you'd call your actual API for regular schedules
    // For now, returning an empty array as per requirement
    await new Promise(r => setTimeout(r, 50)); // Simulate API delay
    return []; // Placeholder
  };


  // Fetch all data (users, then shifts for the current week)
  useEffect(() => {
    const fetchInitialData = async () => {
      setIsLoadingUsers(true);
      setIsLoadingShifts(true);
      let fetchedUsers = [];
      try {
        const userResponse = await request("get", "/staffs/details", null, null, null, { params: { status: "ACTIVE" } });
        fetchedUsers = (userResponse.data.data || []).map(staff => ({
          id: staff.user_login_id,
          name: staff.fullname,
          departmentCode: staff.department?.department_code || 'UNKNOWN_DEPT',
          departmentName: staff.department?.department_name || 'Không có phòng ban',
          jobPositionName: staff.job_position?.job_position_name || 'Không có chức vụ'
        }));
        setRawUsers(fetchedUsers);
      } catch (error) {
        console.error("Failed to fetch users:", error);
        setRawUsers([]);
      } finally {
        setIsLoadingUsers(false);
      }
      
      if (fetchedUsers.length > 0) {
        const userIds = fetchedUsers.map(u => u.id);
        const weekStart = format(startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON }), 'yyyy-MM-dd');
        const weekEnd = format(endOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON }), 'yyyy-MM-dd');
        try {
          const [absencesData, regularShiftsData] = await Promise.all([
            fetchAbsences(userIds, weekStart, weekEnd),
            fetchRegularShifts(userIds, weekStart, weekEnd) // TODO: Implement this
          ]);

          const timeOffShifts = absencesData.map(absence => ({
            id: absence.id,
            userId: absence.user_id,
            day: absence.date,
            startTime: absence.start_time.substring(0, 5),
            endTime: absence.end_time.substring(0, 5),
            note: absence.reason || 'Nghỉ phép',
            type: 'time_off',
            duration: calculateDuration(absence.date, absence.start_time.substring(0,5), absence.end_time.substring(0,5))
          }));

          // TODO: Transform regularShiftsData if its structure is different
          const processedRegularShifts = regularShiftsData.map(rs => ({
            ...rs, // Spread existing properties
            duration: rs.duration || calculateDuration(rs.day, rs.startTime, rs.endTime),
            type: 'regular'
          }));

          const currentUnassignedShifts = initialUnassignedShifts.map(s => ({
            ...s,
            duration: s.duration || calculateDuration(s.day, s.startTime, s.endTime)
          }));

          setShifts([...processedRegularShifts, ...timeOffShifts, ...currentUnassignedShifts]);

        } catch (error) {
          console.error("Failed to fetch shifts (absences or regular):", error);
          setShifts(initialUnassignedShifts.map(s => ({...s, duration: calculateDuration(s.day, s.startTime, s.endTime)}))); // Fallback to only unassigned
        } finally {
          setIsLoadingShifts(false);
        }
      } else {
        // No users fetched, so no user-specific shifts to fetch
        setShifts(initialUnassignedShifts.map(s => ({...s, duration: calculateDuration(s.day, s.startTime, s.endTime)})));
        setIsLoadingShifts(false);
      }
    };

    fetchInitialData();
  }, [currentDate]); // Refetch when currentDate (week) changes


  // --- Event Handlers & Logic (Keep existing handlers, ensure they use the new 'shifts' state) ---
  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date(new Date().setHours(0,0,0,0)));

  const handleOpenModal = (userIdForPreselection, day, shiftToEdit = null) => {
    // ... (same as previous version)
    let context = null;
    if (shiftToEdit) {
      setCurrentEditingShift(shiftToEdit);
      context = shiftToEdit.userId === UNASSIGNED_SHIFT_USER_ID ? 'editUnassigned' : 'editUser';
      setModalInitialFormState({
        userIds: shiftToEdit.userId === UNASSIGNED_SHIFT_USER_ID ? [] : [shiftToEdit.userId],
        day: shiftToEdit.day,
        startTime: shiftToEdit.startTime,
        endTime: shiftToEdit.endTime,
        note: shiftToEdit.note || '',
        slots: shiftToEdit.slots !== undefined ? shiftToEdit.slots : 1,
      });
    } else {
      setCurrentEditingShift(null);
      context = userIdForPreselection === UNASSIGNED_SHIFT_USER_ID ? 'newUnassigned' : 'newUser';
      setModalInitialFormState({
        userIds: context === 'newUnassigned' ? [] : (userIdForPreselection ? [userIdForPreselection] : []),
        day: format(day || new Date(), 'yyyy-MM-dd'),
        startTime: '09:00',
        endTime: '17:00',
        note: '',
        slots: context === 'newUnassigned' ? 1 : 1,
      });
    }
    setModalOpeningContext(context);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    // ... (same as previous version)
    setIsModalOpen(false);
    setCurrentEditingShift(null);
    setModalOpeningContext(null);
  };

  const proceedWithSaveShift = (formDataToSave, editingShiftOriginalId) => {
    // ... (same as previous version, ensure duration is calculated for new/updated shifts)
    const { userIds: rawUserIds, day, startTime, endTime, note, slots: formSlots, _initiatedAsNewUnassignedContext } = formDataToSave;
    const userIds = rawUserIds || [];
    const shiftType = currentEditingShift?.type || (_initiatedAsNewUnassignedContext ? 'unassigned' : 'regular');
    const newDuration = calculateDuration(day, startTime, endTime);
    const shiftBaseData = { day, startTime, endTime, note, duration: newDuration, type: shiftType };

    let totalInitialSlotsDefined = parseInt(formSlots, 10);
    if (isNaN(totalInitialSlotsDefined) || totalInitialSlotsDefined < 0) {
      totalInitialSlotsDefined = (_initiatedAsNewUnassignedContext || (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID)) ? 1 : 0;
    }

    const actualUserIdsToAssign = userIds.filter(uid => allUsers.find(u => u.id === uid));
    let newShiftsCreatedForUsers = [];
    let assignedCountThisAction = 0;

    if (_initiatedAsNewUnassignedContext) {
      let slotsForThisNewUnassignedOp = totalInitialSlotsDefined;
      if (actualUserIdsToAssign.length > 0) {
        for (const selectedUserId of actualUserIdsToAssign) {
          newShiftsCreatedForUsers.push({
            ...shiftBaseData, userId: selectedUserId, type: 'regular',
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`,
          });
          assignedCountThisAction++;
        }
      }
      const remainingSlotsForTemplate = slotsForThisNewUnassignedOp - assignedCountThisAction;
      let newUnassignedShiftTemplate = null;
      if (remainingSlotsForTemplate > 0) {
        newUnassignedShiftTemplate = {
          ...shiftBaseData,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-unassigned`,
          userId: UNASSIGNED_SHIFT_USER_ID, slots: remainingSlotsForTemplate,
          muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned',
        };
      }
      setShifts(prevShifts => {
        let updatedShifts = [...prevShifts, ...newShiftsCreatedForUsers];
        if (newUnassignedShiftTemplate) updatedShifts.push(newUnassignedShiftTemplate);
        return updatedShifts;
      });
    } else if (editingShiftOriginalId && currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID) {
      let slotsAvailableInExistingTemplate = parseInt(currentEditingShift.slots, 10);
      let newTotalSlotsForTemplateDefinition = totalInitialSlotsDefined;

      if (actualUserIdsToAssign.length > 0) {
        for (const selectedUserId of actualUserIdsToAssign) {
          if (slotsAvailableInExistingTemplate <= 0 && !currentEditingShift.allowOverAssign) break;
          newShiftsCreatedForUsers.push({
            ...shiftBaseData, userId: selectedUserId, type: 'regular',
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`,
          });
          if (slotsAvailableInExistingTemplate > 0) slotsAvailableInExistingTemplate--;
          assignedCountThisAction++;
        }
      }
      setShifts(prevShifts => {
        let finalTemplateSlots;
        if (formSlots !== undefined && formSlots !== null && !isNaN(parseInt(formSlots,10))) {
          finalTemplateSlots = newTotalSlotsForTemplateDefinition - assignedCountThisAction;
        } else {
          finalTemplateSlots = parseInt(currentEditingShift.slots, 10) - assignedCountThisAction;
        }
        if (finalTemplateSlots < 0) finalTemplateSlots = 0;

        let updatedShifts = prevShifts.map(s =>
          (s.id === editingShiftOriginalId)
            ? { ...s, ...shiftBaseData, slots: finalTemplateSlots, type: 'unassigned' }
            : s
        );
        if (finalTemplateSlots <= 0) {
          updatedShifts = updatedShifts.filter(s => s.id !== editingShiftOriginalId);
        }
        return [...updatedShifts, ...newShiftsCreatedForUsers];
      });
    } else {
      let newShiftsToAdd_regular = [];
      let shiftsToUpdateDetails_regular = [];
      let originalShiftToDeleteId_regular = null;

      if (editingShiftOriginalId && currentEditingShift && currentEditingShift.type !== 'time_off') {
        const originalUserIdOfEditedShift = currentEditingShift.userId;
        if (actualUserIdsToAssign.length === 0 && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID) {
          originalShiftToDeleteId_regular = editingShiftOriginalId;
        } else {
          let originalUserStillSelected = false;
          actualUserIdsToAssign.forEach(selectedUserId => {
            const completeShiftData = { ...shiftBaseData, userId: selectedUserId, type: 'regular' };
            if (selectedUserId === originalUserIdOfEditedShift) {
              shiftsToUpdateDetails_regular.push({ id: editingShiftOriginalId, data: completeShiftData });
              originalUserStillSelected = true;
            } else {
              newShiftsToAdd_regular.push({ ...completeShiftData, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}` });
            }
          });
          if (!originalUserStillSelected && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID) {
            originalShiftToDeleteId_regular = editingShiftOriginalId;
          }
        }
      } else if (!editingShiftOriginalId) {
        if (actualUserIdsToAssign.length === 0) { alert("Vui lòng chọn ít nhất một nhân viên."); return; }
        actualUserIdsToAssign.forEach(selectedUserId => {
          newShiftsToAdd_regular.push({
            ...shiftBaseData, userId: selectedUserId, type: 'regular',
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`
          });
        });
      }
      setShifts(prevShifts => {
        let result = [...prevShifts];
        if (originalShiftToDeleteId_regular) result = result.filter(s => s.id !== originalShiftToDeleteId_regular);
        shiftsToUpdateDetails_regular.forEach(updateInfo => {
          result = result.map(s => s.id === updateInfo.id ? { ...s, ...updateInfo.data } : s);
        });
        return [...result, ...newShiftsToAdd_regular];
      });
    }
    handleCloseModal();
  };

  const handleSaveShift = (formData) => {
    // ... (same as previous version)
    const { userIds: rawUserIds, day, startTime, endTime, _initiatedAsNewUnassignedContext } = formData;
    const userIds = rawUserIds || [];
    const actualUserIdsToAssign = userIds.filter(uid => allUsers.find(u => u.id === uid));
    let editingId = currentEditingShift ? currentEditingShift.id : null;
    let allDetectedConflicts = [];

    if (_initiatedAsNewUnassignedContext && actualUserIdsToAssign.length > 0) {
      actualUserIdsToAssign.forEach(userId => {
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, allUsers, null));
      });
    } else if (currentEditingShift && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID && currentEditingShift.type !== 'time_off') {
      actualUserIdsToAssign.forEach(userId => {
        const idToExclude = userId === currentEditingShift.userId ? editingId : null;
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, allUsers, idToExclude));
      });
    } else if (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID && actualUserIdsToAssign.length > 0) {
      actualUserIdsToAssign.forEach(userId => {
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, allUsers, null));
      });
    } else if (!currentEditingShift && actualUserIdsToAssign.length > 0 && !_initiatedAsNewUnassignedContext) {
      actualUserIdsToAssign.forEach(userId => {
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, allUsers, null));
      });
    }

    if (allDetectedConflicts.length > 0) {
      setSchedulingConflicts(allDetectedConflicts);
      setPendingShiftOperation({ action: 'save', data: formData, editingShiftId: editingId });
      setIsConflictModalOpen(true);
      return;
    }
    proceedWithSaveShift(formData, editingId);
  };

  const handleDeleteSingleShift = (shiftId) => { /* ... (same) ... */
    setShiftIdToDelete(shiftId);
    setIsDeleteSingleModalOpen(true);
  };
  const confirmDeleteSingleShift = () => { /* ... (same) ... */
    if (shiftIdToDelete) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
      setSelectedShiftIds(prevSelected => prevSelected.filter(id => id !== shiftIdToDelete));
      setShiftIdToDelete(null);
    }
    setIsDeleteSingleModalOpen(false);
  };
  const handleDeleteSelectedShifts = useCallback(() => { /* ... (same) ... */
    if (!isAnyShiftSelected || selectedShiftIds.length === 0) return;
    setIsDeleteSelectedModalOpen(true);
  }, [selectedShiftIds, isAnyShiftSelected]);
  const confirmDeleteSelectedShifts = () => { /* ... (same) ... */
    setShifts(prevShifts => prevShifts.filter(shift => !selectedShiftIds.includes(shift.id)));
    setSelectedShiftIds([]);
    setIsDeleteSelectedModalOpen(false);
  };

  const proceedWithDragEnd = (dragResult) => { /* ... (same, ensure duration is handled) ... */
    const { source, destination, draggableId } = dragResult;
    const draggedShiftOriginal = shifts.find(shift => shift.id === draggableId);
    if(!draggedShiftOriginal || draggedShiftOriginal.type === 'time_off') return;

    const destParts = destination.droppableId.split('-');
    const newDestUserId = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;
    const newDuration = calculateDuration(newDestDayString, draggedShiftOriginal.startTime, draggedShiftOriginal.endTime);


    if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID && newDestUserId !== UNASSIGNED_SHIFT_USER_ID) {
      const targetUserId = newDestUserId;
      setShifts(prevShifts => {
        let sourceFound = false;
        const listAfterTemplateUpdate = prevShifts.map(s => {
          if (s.id === draggableId) { sourceFound = true; return s.slots > 1 ? { ...s, slots: s.slots - 1 } : null; }
          return s;
        }).filter(Boolean);
        if (!sourceFound) return prevShifts;
        const newAssignedShift = {
          ...draggedShiftOriginal,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-assigned-${targetUserId}`,
          userId: targetUserId, day: newDestDayString, slots: undefined,
          type: 'regular', duration: newDuration
        };
        return [...listAfterTemplateUpdate, newAssignedShift];
      });
    } else if (draggedShiftOriginal.userId !== UNASSIGNED_SHIFT_USER_ID && newDestUserId === UNASSIGNED_SHIFT_USER_ID) {
      setShifts(prevShifts => {
        const listWithoutOld = prevShifts.filter(s => s.id !== draggableId);
        const newUnassigned = {
          id: `s${Date.now()}-unassigned-${Math.random().toString(16).slice(2)}`,
          userId: UNASSIGNED_SHIFT_USER_ID, day: newDestDayString,
          startTime: draggedShiftOriginal.startTime, endTime: draggedShiftOriginal.endTime,
          duration: newDuration,
          note: draggedShiftOriginal.note, slots: 1,
          muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned',
        };
        return [...listWithoutOld, newUnassigned];
      });
    } else if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID && newDestUserId === UNASSIGNED_SHIFT_USER_ID) {
      setShifts(prevShifts => prevShifts.map(s => (s.id === draggableId) ? {
        ...s, day: newDestDayString, duration: newDuration,
        muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned'
      } : s));
    } else if (draggedShiftOriginal.userId !== UNASSIGNED_SHIFT_USER_ID && newDestUserId !== UNASSIGNED_SHIFT_USER_ID) {
      setShifts(prevShifts => prevShifts.map(s => {
        if (s.id === draggableId) {
          return {
            ...s, day: newDestDayString, userId: newDestUserId,
            slots: undefined, type: 'regular', duration: newDuration
          };
        }
        return s;
      }));
    }
  };
  const onDragEnd = (result) => { /* ... (same) ... */
    const { source, destination, draggableId } = result;
    if (!destination || !destination.droppableId || (source.droppableId === destination.droppableId && source.index === destination.index)) {
      return;
    }
    const draggedShift = shifts.find(shift => shift.id === draggableId);
    if (!draggedShift || draggedShift.type === 'time_off') return;

    const destParts = destination.droppableId.split('-');
    if (destParts.length < 6 || destParts[0] !== 'user') return;
    const newDestUserId = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    let potentialConflictUserId = null;
    let shiftDetailsForCheck = {
      day: newDestDayString,
      startTime: draggedShift.startTime,
      endTime: draggedShift.endTime,
    };

    if (newDestUserId !== UNASSIGNED_SHIFT_USER_ID) {
      potentialConflictUserId = newDestUserId;
    }

    if (potentialConflictUserId) {
      const idToExcludeForConflictCheck = (draggedShift.userId === potentialConflictUserId && draggedShift.day === newDestDayString) ? draggedShift.id : null;
      const detectedConflicts = detectConflicts(shiftDetailsForCheck, shifts, potentialConflictUserId, allUsers, idToExcludeForConflictCheck);
      if (detectedConflicts.length > 0) {
        setSchedulingConflicts(detectedConflicts);
        setPendingShiftOperation({ action: 'drag', data: result });
        setIsConflictModalOpen(true);
        return;
      }
    }
    proceedWithDragEnd(result);
  };

  const handleConflictModalClose = () => { /* ... (same) ... */
    setIsConflictModalOpen(false);
    setSchedulingConflicts([]);
    setPendingShiftOperation(null);
  };
  const handleConflictModalConfirm = () => { /* ... (same) ... */
    if (pendingShiftOperation) {
      if (pendingShiftOperation.action === 'save') {
        proceedWithSaveShift(pendingShiftOperation.data, pendingShiftOperation.editingShiftId);
      } else if (pendingShiftOperation.action === 'drag') {
        proceedWithDragEnd(pendingShiftOperation.data);
      }
    }
    handleConflictModalClose();
  };
  const handleToggleSelectShift = useCallback((shiftId) => { /* ... (same) ... */
    const shiftToToggle = shifts.find(s => s.id === shiftId);
    if (shiftToToggle && shiftToToggle.type === 'time_off') return;
    setSelectedShiftIds(prev =>
      prev.includes(shiftId) ? prev.filter(id => id !== shiftId) : [...prev, shiftId]
    );
  }, [shifts]);
  const handleDeselectAll = useCallback(() => { setSelectedShiftIds([]); }, []);
  const getAllShiftIdsInCurrentView = useCallback(() => { /* ... (same) ... */
    const weekStart = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
    const weekEnd = addDays(weekStart, 6);
    const ids = [];
    shifts.forEach(s => {
      if (s.type === 'time_off') return; // Do not include time_off shifts in bulk selection
      if (isValid(parseISO(s.day))) {
        const d = parseISO(s.day);
        // Check if shift's user is in the current (possibly filtered) list of allUsers or if it's an unassigned shift
        if (d >= weekStart && d <= weekEnd && (allUsers.find(u => u.id === s.userId) || s.userId === UNASSIGNED_SHIFT_USER_ID)) {
          ids.push(s.id);
        }
      }
    });
    return ids;
  }, [shifts, allUsers, currentDate]);

  const allShiftIdsInView = getAllShiftIdsInCurrentView();
  const selectedShiftsInViewCount = allShiftIdsInView.filter(id => selectedShiftIds.includes(id)).length;
  const isAllSelectedInView = allShiftIdsInView.length > 0 && selectedShiftsInViewCount === allShiftIdsInView.length;
  const isIndeterminateInView = selectedShiftsInViewCount > 0 && selectedShiftsInViewCount < allShiftIdsInView.length;

  const handleToggleSelectAllInView = useCallback(() => { /* ... (same) ... */
    const idsInView = getAllShiftIdsInCurrentView();
    if (isAllSelectedInView) {
      setSelectedShiftIds(prev => prev.filter(id => !idsInView.includes(id)));
    } else {
      setSelectedShiftIds(prev => [...new Set([...prev, ...idsInView])]);
    }
  }, [getAllShiftIdsInCurrentView, isAllSelectedInView]);
  const handleOpenCopyModal = () => { /* ... (same) ... */ if (!isAnyShiftSelected) return; setIsCopyModalOpen(true); };
  const handleCloseCopyModal = () => { /* ... (same) ... */ setIsCopyModalOpen(false); };
  const handleConfirmCopyToWeeks = useCallback(async (targetWeekStartDates) => { /* ... (same, ensure duration) ... */
    if (!isAnyShiftSelected || targetWeekStartDates.length === 0) return;
    const toCopy = shifts.filter(s => selectedShiftIds.includes(s.id) && s.type !== 'time_off');
    let newCopies = [];
    targetWeekStartDates.forEach(weekStart => {
      toCopy.forEach(s => {
        const origDate = parseISO(s.day);
        if (!isValid(origDate)) return;
        let dayIdx = getDay(origDate);
        if (WEEK_STARTS_ON === 1) dayIdx = (dayIdx === 0) ? 6 : dayIdx - 1;
        const newDate = addDays(weekStart, dayIdx);
        const newDayString = format(newDate, 'yyyy-MM-dd');
        newCopies.push({
          ...s,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${newCopies.length}`,
          day: newDayString,
          duration: s.duration || calculateDuration(newDayString, s.startTime, s.endTime)
        });
      });
    });
    await new Promise(r => setTimeout(r, 100));
    setShifts(prev => [...prev, ...newCopies]);
    setSelectedShiftIds([]);
  }, [selectedShiftIds, shifts, isAnyShiftSelected]);

  // --- Layout Calculations ---
  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;
  // ... (other layout constants are the same)
  const PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER = 8 + 8 + 8;
  const FIXED_FOOTER_RESERVED_SPACE = 70;
  const paperContentMaxHeight = `calc(100vh - ${TOP_BAR_HEIGHT}px - ${dynamicStickyOffset}px - ${INFO_BANNERS_TOTAL_HEIGHT}px - ${PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER}px - ${FIXED_FOOTER_RESERVED_SPACE}px)`;


  // --- Render ---
  const mainContentLoading = isLoadingUsers || isLoadingShifts;

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DragDropContext onDragEnd={onDragEnd}>
        <Container maxWidth={false} disableGutters sx={{ bgcolor: 'grey.200', minHeight: '100vh' }}>
          <TopBar currentDate={currentDate} onPrevWeek={handlePrevWeek} onNextWeek={handleNextWeek} onToday={handleToday} />
          <BulkActionsBar selectedCount={selectedShiftIds.length} onDeleteSelected={handleDeleteSelectedShifts} onOpenCopyModal={handleOpenCopyModal} onDeselectAll={handleDeselectAll}/>
          <Box sx={{px: {xs: 0, sm:1, md:2}, py:1}}>
            <InfoBanners currentDate={currentDate} stickyTopOffset={dynamicStickyOffset} />
            <Paper elevation={2} sx={{ mt:1, overflowY: 'auto', maxHeight: paperContentMaxHeight }}>
              <CalendarHeader currentDate={currentDate} onToggleSelectAll={handleToggleSelectAllInView} isAllSelectedInView={isAllSelectedInView} isIndeterminateInView={isIndeterminateInView} />
              {!mainContentLoading && ( // Only render UnassignedShiftsRow if not loading main content
                <UnassignedShiftsRow
                  currentDate={currentDate}
                  shifts={shifts.filter(s => s.userId === UNASSIGNED_SHIFT_USER_ID)}
                  onAddShift={(userId, day) => handleOpenModal(UNASSIGNED_SHIFT_USER_ID, day)}
                  onEditShift={(shift) => handleOpenModal(null, null, shift)}
                  onDeleteShift={handleDeleteSingleShift} selectedShiftIds={selectedShiftIds}
                  onToggleSelectShift={handleToggleSelectShift} isAnyShiftSelected={isAnyShiftSelected}
                  isSticky={unassignedRowSticky} onToggleSticky={() => setUnassignedRowSticky(prev => !prev)}
                  stickyTopOffset={CALENDAR_HEADER_HEIGHT}
                />
              )}
              <Box sx={{ overflowX: 'auto' }}>
                <Box sx={{ minWidth: 1100 }}>
                  {mainContentLoading ? (
                    <Box display="flex" justifyContent="center" alignItems="center" sx={{ height: 200, p: 2 }}>
                      <CircularProgress />
                      <Typography sx={{ml: 2}}>Đang tải dữ liệu lịch làm việc...</Typography>
                    </Box>
                  ) : groupedUsersForGrid.length > 0 || shifts.some(s => s.userId === UNASSIGNED_SHIFT_USER_ID) ? ( // Check if there are any users OR unassigned shifts to display
                    <ShiftsGrid
                      currentDate={currentDate}
                      // Pass only assigned user shifts to ShiftsGrid, unassigned are handled by UnassignedShiftsRow
                      shifts={shifts.filter(s => s.userId !== UNASSIGNED_SHIFT_USER_ID)}
                      groupedUsers={groupedUsersForGrid}
                      onAddShift={(userId, day) => handleOpenModal(userId, day)}
                      onDeleteShift={handleDeleteSingleShift}
                      onEditShift={(shift) => {
                        if (shift.type === 'time_off') return;
                        handleOpenModal(shift.userId, parseISO(shift.day), shift);
                      }}
                      selectedShiftIds={selectedShiftIds} onToggleSelectShift={handleToggleSelectShift}
                      isAnyShiftSelected={isAnyShiftSelected}
                    />
                  ) : (
                    <Box display="flex" justifyContent="center" alignItems="center" sx={{ height: 100, p:2 }}>
                      <Typography color="text.secondary">Không có nhân viên hoặc ca làm việc nào để hiển thị cho tuần này.</Typography>
                    </Box>
                  )}
                </Box>
              </Box>
            </Paper>
          </Box>
          <Paper elevation={3} sx={{ position: 'fixed', bottom: 16, right: 16, p: 1.5, display: 'flex', alignItems: 'center', borderRadius: 2 }}>
            <AccessTimeFilledIcon color="primary" sx={{mr:1}}/> <Typography variant="caption">Bật đồng hồ chấm công</Typography> <Checkbox size="small" sx={{ml:0.5, p:0.2}}/>
          </Paper>

          {/* Modals */}
          <ShiftModal /* ... (props same as previous) ... */
            isOpen={isModalOpen} onClose={handleCloseModal} onSave={handleSaveShift}
            users={allUsers}
            initialFormState={modalInitialFormState}
            isEditing={!!currentEditingShift && currentEditingShift.type !== 'time_off'}
            isUnassignedContext={modalOpeningContext === 'newUnassigned' || modalOpeningContext === 'editUnassigned'}
            unassignedShiftBeingEdited={currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID ? currentEditingShift : null}
          />
          <CopyShiftsModal /* ... (props same as previous) ... */
            isOpen={isCopyModalOpen} onClose={handleCloseCopyModal} onConfirmCopy={handleConfirmCopyToWeeks} currentDate={currentDate} numSelectedShifts={selectedShiftIds.length}
          />
          <DeleteConfirmationModal /* ... (props same as previous for single delete) ... */
            open={isDeleteSingleModalOpen}
            onClose={() => { setIsDeleteSingleModalOpen(false); setShiftIdToDelete(null); }}
            onSubmit={confirmDeleteSingleShift}
            title="Xác nhận xóa ca"
            info="Bạn có chắc chắn muốn xóa ca làm việc này không?"
            cancelLabel="Hủy bỏ"
            confirmLabel="Xóa"
          />
          <DeleteConfirmationModal /* ... (props same as previous for bulk delete) ... */
            open={isDeleteSelectedModalOpen}
            onClose={() => setIsDeleteSelectedModalOpen(false)}
            onSubmit={confirmDeleteSelectedShifts}
            title="Xác nhận xóa các ca đã chọn"
            info={`Bạn có chắc chắn muốn xóa ${selectedShiftIds.length} ca đã chọn không?`}
            cancelLabel="Hủy bỏ"
            confirmLabel="Xóa tất cả"
          />
          <SchedulingConflictModal /* ... (props same as previous) ... */
            open={isConflictModalOpen}
            onClose={handleConflictModalClose}
            onConfirm={handleConflictModalConfirm}
            conflicts={schedulingConflicts}
          />
        </Container>
      </DragDropContext>
    </LocalizationProvider>
  );
}