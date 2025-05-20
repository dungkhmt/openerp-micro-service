// ==============
// ShiftScheduler.jsx
// ==============
import React, {useCallback, useEffect, useState} from 'react';
import {addDays, format, getDay, isValid, parseISO, startOfWeek, subDays, isEqual} from 'date-fns'; // Added isEqual
import vi from 'date-fns/locale/vi';
import {DragDropContext} from 'react-beautiful-dnd';
import {Box, Container, Paper, Typography, Checkbox} from '@mui/material';
import {LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDateFns} from '@mui/x-date-pickers/AdapterDateFns';
import AccessTimeFilledIcon from '@mui/icons-material/AccessTimeFilled';

import CopyShiftsModal from "./CopyShiftsModal.jsx";
import ShiftModal from "./ShiftModal.jsx";
import CalendarHeader from "./CalendarHeader.jsx";
import ShiftsGrid from "./ShiftsGrid.jsx";
import InfoBanners from "./InfoBanners.jsx";
import BulkActionsBar from "./BulkActionBar.jsx";
import TopBar from "./TopBar.jsx";
import UnassignedShiftsRow from "./UnassignedShiftsRow.jsx";
import SchedulingConflictModal from "./SchedulingConflictModal.jsx";
import DeleteConfirmationModal from "../modals/DeleteConfirmationModal.jsx"; // Import conflict modal

export const TOP_BAR_HEIGHT = 61;
export const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
export const PROJECTED_SALES_BANNER_HEIGHT = 0;
export const INFO_BANNERS_TOTAL_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT + PROJECTED_SALES_BANNER_HEIGHT;
export const BULK_ACTIONS_BAR_HEIGHT = 50;
export const CALENDAR_HEADER_HEIGHT = 57;
export const UNASSIGNED_ROW_HEIGHT = 60;
export const WEEK_STARTS_ON = 1;
export const UNASSIGNED_SHIFT_USER_ID = 'UNASSIGNED';

const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '12:00', duration: '3h 0m', note: 'Morning Shift u1', muiColor: 'success.light', muiTextColor: 'success.darkerText', type: 'regular' },
  { id: 's1-2', userId: 'u1', day: '2025-05-19', startTime: '13:00', endTime: '17:15', duration: '4h 15m', note: 'Afternoon Shift u1', muiColor: 'success.light', muiTextColor: 'success.darkerText', type: 'regular' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', note: 'Frontend task u2', muiColor: 'error.light', muiTextColor: 'error.darkerText', type: 'regular' },
  { id: 's-unassigned-tue', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-20', startTime: '10:00', endTime: '14:00', duration: '4h 0m', note: 'General Task Tue', slots: 3, muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned' },
  { id: 's-unassigned-wed', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-21', startTime: '15:00', endTime: '18:00', duration: '3h 0m', note: 'Evening Cover Wed', slots: 1, muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned' },
  { id: 'to1-u1', userId: 'u1', day: '2025-05-22', startTime: '00:00', endTime: '23:59', duration: '24h 0m', note: 'Day Off', type: 'time_off' },
  { id: 'to2-u2', userId: 'u2', day: '2025-05-23', startTime: '09:00', endTime: '13:00', duration: '4h 0m', note: 'Doctor Appointment', type: 'time_off' },
];

const initialUsers = [
  { id: 'u1', name: 'NV Ánh Nguyệt', summary: '', avatarLetter: 'AN', avatarBgColor: 'secondary.main' },
  { id: 'u2', name: 'NV Bảo Long', summary: '', avatarLetter: 'BL', avatarBgColor: 'primary.main' },
  { id: 'u3', name: 'NV Cẩm Tú', summary: '', avatarLetter: 'CT', avatarBgColor: 'warning.main' },
];

// --- Conflict Detection Helper ---
const detectConflicts = (shiftToCheck, allShifts, targetUserId, usersList, currentShiftIdToExclude = null) => {
  const conflicts = [];
  if (!shiftToCheck.day || !shiftToCheck.startTime || !shiftToCheck.endTime || !targetUserId) {
    return conflicts; // Not enough info to check
  }

  const startA_raw = parseISO(`${shiftToCheck.day}T${shiftToCheck.startTime}`);
  const endA_raw = parseISO(`${shiftToCheck.day}T${shiftToCheck.endTime}`);

  if (!isValid(startA_raw) || !isValid(endA_raw)) {
    return [];
  }
  // Adjust for overnight shifts for comparison range (A ends on next day if end time < start time)
  const startA = startA_raw;
  const endA = endA_raw < startA_raw ? addDays(endA_raw, 1) : endA_raw;

  allShifts.forEach(existingShift => {
    // Only check against shifts of the same user
    if (existingShift.userId !== targetUserId) return;
    // Exclude the shift being checked/moved if it's an existing shift
    if (currentShiftIdToExclude && existingShift.id === currentShiftIdToExclude) return;
    // Ensure existing shift has necessary time properties
    if (!existingShift.day || !existingShift.startTime || !existingShift.endTime) return;

    const startB_raw = parseISO(`${existingShift.day}T${existingShift.startTime}`);
    const endB_raw = parseISO(`${existingShift.day}T${existingShift.endTime}`);

    if (!isValid(startB_raw) || !isValid(endB_raw)) return;

    const startB = startB_raw;
    const endB = endB_raw < startB_raw ? addDays(endB_raw, 1) : endB_raw;

    // Check for overlap: (StartA < EndB) and (EndA > StartB)
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
  const [currentDate, setCurrentDate] = useState(new Date(new Date('2025-05-19').setHours(0,0,0,0)));
  const [shifts, setShifts] = useState(initialShifts.map(s => {
    const baseShift = s.userId === '---UNASSIGNED---' ? {...s, userId: UNASSIGNED_SHIFT_USER_ID} : s;
    if (!baseShift.type) { // Ensure type is set, default to regular or unassigned
      baseShift.type = baseShift.userId === UNASSIGNED_SHIFT_USER_ID ? 'unassigned' : 'regular';
    }
    return baseShift;
  }));
  const [users, setUsers] = useState(initialUsers);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [modalInitialFormState, setModalInitialFormState] = useState({
    userIds: [], day: format(new Date(), 'yyyy-MM-dd'), startTime: '09:00',
    endTime: '17:00', note: '', slots: 1,
  });
  const [selectedShiftIds, setSelectedShiftIds] = useState([]);
  const [isCopyModalOpen, setIsCopyModalOpen] = useState(false);
  const [unassignedRowSticky, setUnassignedRowSticky] = useState(true);
  const [modalOpeningContext, setModalOpeningContext] = useState(null);

  // State for Delete Confirmation Modals
  const [isDeleteSingleModalOpen, setIsDeleteSingleModalOpen] = useState(false);
  const [shiftIdToDelete, setShiftIdToDelete] = useState(null);
  const [isDeleteSelectedModalOpen, setIsDeleteSelectedModalOpen] = useState(false);

  // State for Scheduling Conflict Modal
  const [schedulingConflicts, setSchedulingConflicts] = useState([]);
  const [isConflictModalOpen, setIsConflictModalOpen] = useState(false);
  const [pendingShiftOperation, setPendingShiftOperation] = useState(null); // { action: 'save' | 'drag', data: relevantData }


  const isAnyShiftSelected = selectedShiftIds.length > 0;

  const calculateDuration = (day, startTime, endTime) => {
    if (!day || !startTime || !endTime) return '0h 0m';
    const start = parseISO(`${day}T${startTime}`);
    const end = parseISO(`${day}T${endTime}`);
    if (!isValid(start) || !isValid(end)) return '0h 0m';
    let durationMs = end.getTime() - start.getTime();
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; }
    const h = Math.floor(durationMs / (1000 * 60 * 60));
    const m = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${h}h ${m}m`;
  };

  useEffect(() => {
    const updatedUsersSummary = initialUsers.map(user => {
      // Exclude 'time_off' shifts from work hour calculation
      const userShifts = shifts.filter(s => s.userId === user.id && s.userId !== UNASSIGNED_SHIFT_USER_ID && s.type !== 'time_off'); //
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
      return { ...user, summary: `${h}h ${m}m` };
    });
    setUsers(updatedUsersSummary);
  }, [shifts]);

  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date(new Date().setHours(0,0,0,0)));

  const handleOpenModal = (userIdForPreselection, day, shiftToEdit = null) => {
    // Note: This function would need modification if you intend to create/edit 'time_off' shifts via the modal.
    // For now, it assumes modal is for 'regular' or 'unassigned' shifts.
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
        // type: shiftToEdit.type || (shiftToEdit.userId === UNASSIGNED_SHIFT_USER_ID ? 'unassigned' : 'regular') // If modal handles type
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
        // type: context === 'newUnassigned' ? 'unassigned' : 'regular' // If modal handles type
      });
    }
    setModalOpeningContext(context);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentEditingShift(null);
    setModalOpeningContext(null);
  };

  const proceedWithSaveShift = (formDataToSave, editingShiftOriginalId) => {
    const { userIds: rawUserIds, day, startTime, endTime, note, slots: formSlots, _initiatedAsNewUnassignedContext } = formDataToSave;
    const userIds = rawUserIds || [];

    // Assuming type is 'regular' or 'unassigned' if not specified.
    // If modal were to set type, it would come from formDataToSave.type
    const shiftType = currentEditingShift?.type || (_initiatedAsNewUnassignedContext ? 'unassigned' : 'regular');


    const newDuration = calculateDuration(day, startTime, endTime);
    const shiftBaseData = { day, startTime, endTime, note, duration: newDuration, type: shiftType }; // Persist type

    let totalInitialSlotsDefined = parseInt(formSlots, 10);
    if (isNaN(totalInitialSlotsDefined) || totalInitialSlotsDefined < 0) {
      totalInitialSlotsDefined = (_initiatedAsNewUnassignedContext || (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID)) ? 1 : 0;
    }

    const actualUserIdsToAssign = userIds.filter(uid => users.find(u => u.id === uid));
    let newShiftsCreatedForUsers = [];
    let assignedCountThisAction = 0;

    if (_initiatedAsNewUnassignedContext) { // Creating new shift(s) from unassigned context modal
      let slotsForThisNewUnassignedOp = totalInitialSlotsDefined;
      if (actualUserIdsToAssign.length > 0) {
        for (const selectedUserId of actualUserIdsToAssign) {
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
          newShiftsCreatedForUsers.push({
            ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor, type: 'regular', // Assigning creates a regular shift
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
          muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned', // Explicitly unassigned
        };
      }
      setShifts(prevShifts => {
        let updatedShifts = [...prevShifts, ...newShiftsCreatedForUsers];
        if (newUnassignedShiftTemplate) {
          updatedShifts.push(newUnassignedShiftTemplate);
        }
        return updatedShifts;
      });
    } else if (editingShiftOriginalId && currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID) { // Editing an existing unassigned shift (e.g., assigning it)
      let slotsAvailableInExistingTemplate = parseInt(currentEditingShift.slots, 10);
      let newTotalSlotsForTemplateDefinition = totalInitialSlotsDefined; // How many slots the unassigned shift should now have (if time/note changed)

      if (actualUserIdsToAssign.length > 0) { // If users are selected for assignment
        for (const selectedUserId of actualUserIdsToAssign) {
          if (slotsAvailableInExistingTemplate <= 0 && !currentEditingShift.allowOverAssign) break; // Or similar logic
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
          newShiftsCreatedForUsers.push({
            ...shiftBaseData, // This carries the time/date/note from the modal
            userId: selectedUserId, muiColor, muiTextColor, type: 'regular', // Assigned shift is 'regular'
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`,
          });
          if (slotsAvailableInExistingTemplate > 0) slotsAvailableInExistingTemplate--;
          assignedCountThisAction++;
        }
      }
      setShifts(prevShifts => {
        // Determine final slots for the template being edited.
        // If 'slots' field was explicitly changed in modal, use that as the new base for the template.
        // Otherwise, just reduce by assigned count.
        let finalTemplateSlots;
        if (formSlots !== undefined && formSlots !== null && !isNaN(parseInt(formSlots,10))) { // If 'slots' field was interacted with
          finalTemplateSlots = newTotalSlotsForTemplateDefinition - assignedCountThisAction;
        } else { // 'slots' field was not changed, just reduce original by assigned count
          finalTemplateSlots = parseInt(currentEditingShift.slots, 10) - assignedCountThisAction;
        }

        if (finalTemplateSlots < 0) finalTemplateSlots = 0;

        let updatedShifts = prevShifts.map(s =>
          (s.id === editingShiftOriginalId)
            ? { ...s, ...shiftBaseData, slots: finalTemplateSlots, type: 'unassigned' } // Update time/note, and new slot count
            : s
        );
        if (finalTemplateSlots <= 0) { // If template has no slots left, remove it
          updatedShifts = updatedShifts.filter(s => s.id !== editingShiftOriginalId);
        }
        return [...updatedShifts, ...newShiftsCreatedForUsers];
      });
    } else { // Regular user shift add/edit (not time_off, not unassigned context)
      let newShiftsToAdd_regular = [];
      let shiftsToUpdateDetails_regular = [];
      let originalShiftToDeleteId_regular = null;

      if (editingShiftOriginalId && currentEditingShift && currentEditingShift.type !== 'time_off') { // Editing a regular user shift
        const originalUserIdOfEditedShift = currentEditingShift.userId;
        if (actualUserIdsToAssign.length === 0 && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID) { // User was removed
          originalShiftToDeleteId_regular = editingShiftOriginalId;
        } else {
          let originalUserStillSelected = false;
          actualUserIdsToAssign.forEach(selectedUserId => {
            const userForColor = users.find(u => u.id === selectedUserId);
            const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
            const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
            const completeShiftData = { ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor, type: 'regular' };

            if (selectedUserId === originalUserIdOfEditedShift) {
              shiftsToUpdateDetails_regular.push({ id: editingShiftOriginalId, data: completeShiftData });
              originalUserStillSelected = true;
            } else { // Shift assigned to a new/additional user
              newShiftsToAdd_regular.push({ ...completeShiftData, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}` });
            }
          });
          if (!originalUserStillSelected && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID) { // Original user was deselected
            originalShiftToDeleteId_regular = editingShiftOriginalId;
          }
        }
      } else if (!editingShiftOriginalId) { // Adding new shift(s) for users (not from unassigned context)
        if (actualUserIdsToAssign.length === 0) { alert("Vui lòng chọn ít nhất một nhân viên."); return; }
        actualUserIdsToAssign.forEach(selectedUserId => {
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main','.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main','.darkerText') || 'text.primary';
          newShiftsToAdd_regular.push({
            ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor, type: 'regular',
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`
          });
        });
      }
      // If currentEditingShift.type === 'time_off', this block is skipped, as time_off editing is not handled by this modal flow.

      setShifts(prevShifts => {
        let result = [...prevShifts];
        if (originalShiftToDeleteId_regular) {
          result = result.filter(s => s.id !== originalShiftToDeleteId_regular);
        }
        shiftsToUpdateDetails_regular.forEach(updateInfo => {
          result = result.map(s => s.id === updateInfo.id ? { ...s, ...updateInfo.data } : s);
        });
        return [...result, ...newShiftsToAdd_regular];
      });
    }
    handleCloseModal();
  };


  const handleSaveShift = (formData) => {
    // This function primarily handles 'regular' and 'unassigned' shifts from the modal.
    // 'time_off' shifts are assumed to be managed elsewhere or pre-initialized.
    // If 'time_off' shifts were to be created/edited via this modal, this function and ShiftModal.jsx would need 'type' handling.

    const { userIds: rawUserIds, day, startTime, endTime, _initiatedAsNewUnassignedContext } = formData;
    const userIds = rawUserIds || [];
    const actualUserIdsToAssign = userIds.filter(uid => users.find(u => u.id === uid));
    let editingId = currentEditingShift ? currentEditingShift.id : null;

    let allDetectedConflicts = [];

    // Perform conflict check for each user being assigned or for the user of an edited shift
    if (_initiatedAsNewUnassignedContext && actualUserIdsToAssign.length > 0) { // New unassigned context, assigning to users
      actualUserIdsToAssign.forEach(userId => {
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, users, null));
      });
    } else if (currentEditingShift && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID && currentEditingShift.type !== 'time_off') { // Editing an existing user's regular shift
      actualUserIdsToAssign.forEach(userId => { // Check for all users this shift is being (re)assigned to
        const idToExclude = userId === currentEditingShift.userId ? editingId : null; // Exclude self only if user hasn't changed
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, users, idToExclude));
      });
    } else if (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID && actualUserIdsToAssign.length > 0) { // Assigning an existing unassigned shift to user(s)
      actualUserIdsToAssign.forEach(userId => {
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, users, null)); // New shift for this user
      });
    } else if (!currentEditingShift && actualUserIdsToAssign.length > 0 && !_initiatedAsNewUnassignedContext) { // Adding new regular shift to user(s) directly
      actualUserIdsToAssign.forEach(userId => {
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, users, null));
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

  const handleDeleteSingleShift = (shiftId) => {
    // Add check if shift is 'time_off' if they are truly undeletable by any means
    // For now, this function is generic.
    setShiftIdToDelete(shiftId);
    setIsDeleteSingleModalOpen(true);
  };

  const confirmDeleteSingleShift = () => {
    if (shiftIdToDelete) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
      setSelectedShiftIds(prevSelected => prevSelected.filter(id => id !== shiftIdToDelete));
      setShiftIdToDelete(null);
    }
    setIsDeleteSingleModalOpen(false);
  };

  const handleDeleteSelectedShifts = useCallback(() => {
    if (!isAnyShiftSelected || selectedShiftIds.length === 0) return;
    setIsDeleteSelectedModalOpen(true);
  }, [selectedShiftIds, isAnyShiftSelected]);

  const confirmDeleteSelectedShifts = () => {
    setShifts(prevShifts => prevShifts.filter(shift => !selectedShiftIds.includes(shift.id)));
    setSelectedShiftIds([]);
    setIsDeleteSelectedModalOpen(false);
  };

  const proceedWithDragEnd = (dragResult) => {
    const { source, destination, draggableId } = dragResult;
    const draggedShiftOriginal = shifts.find(shift => shift.id === draggableId);
    if(!draggedShiftOriginal || draggedShiftOriginal.type === 'time_off') return; // Time off shifts are not draggable


    const destParts = destination.droppableId.split('-');
    const newDestUserId = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID && newDestUserId !== UNASSIGNED_SHIFT_USER_ID) { // Assigning an unassigned shift
      const targetUserId = newDestUserId;
      const userForColor = users.find(u => u.id === targetUserId);
      const newMuiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
      const newMuiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
      setShifts(prevShifts => {
        let sourceFound = false;
        const listAfterTemplateUpdate = prevShifts.map(s => {
          if (s.id === draggableId) { sourceFound = true; return s.slots > 1 ? { ...s, slots: s.slots - 1 } : null; }
          return s;
        }).filter(Boolean);

        if (!sourceFound) return prevShifts; // Should not happen if draggableId is valid

        const newAssignedShift = {
          ...draggedShiftOriginal,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-assigned-${targetUserId}`,
          userId: targetUserId, day: newDestDayString, slots: undefined, // Assigned shifts don't have slots
          muiColor: newMuiColor, muiTextColor: newMuiTextColor,
          type: 'regular', // When an unassigned shift is dragged to a user, it becomes a regular shift
        };
        return [...listAfterTemplateUpdate, newAssignedShift];
      });
    } else if (draggedShiftOriginal.userId !== UNASSIGNED_SHIFT_USER_ID && newDestUserId === UNASSIGNED_SHIFT_USER_ID) { // Moving a regular shift to unassigned
      setShifts(prevShifts => {
        const listWithoutOld = prevShifts.filter(s => s.id !== draggableId);
        const newUnassigned = {
          // ...draggedShiftOriginal, // Spread first to keep times, note etc.
          id: `s${Date.now()}-unassigned-${Math.random().toString(16).slice(2)}`,
          userId: UNASSIGNED_SHIFT_USER_ID, day: newDestDayString,
          startTime: draggedShiftOriginal.startTime, endTime: draggedShiftOriginal.endTime, // explicit
          duration: draggedShiftOriginal.duration, note: draggedShiftOriginal.note,
          slots: 1, // Becomes a new unassigned template with 1 slot
          muiColor: 'grey.300', muiTextColor: 'text.primary',
          type: 'unassigned', // Explicitly unassigned
        };
        return [...listWithoutOld, newUnassigned];
      });
    } else if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID && newDestUserId === UNASSIGNED_SHIFT_USER_ID) { // Moving unassigned within unassigned row
      setShifts(prevShifts => prevShifts.map(s => (s.id === draggableId) ? { ...s, day: newDestDayString, muiColor: 'grey.300', muiTextColor: 'text.primary', type: 'unassigned' } : s));
    } else if (draggedShiftOriginal.userId !== UNASSIGNED_SHIFT_USER_ID && newDestUserId !== UNASSIGNED_SHIFT_USER_ID) { // Moving a regular shift between users or days
      setShifts(prevShifts => prevShifts.map(s => {
        if (s.id === draggableId) {
          const userColor = users.find(u => u.id === newDestUserId);
          return {
            ...s, day: newDestDayString, userId: newDestUserId,
            muiColor: userColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200',
            muiTextColor: userColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary',
            slots: undefined, // Ensure slots is undefined for regular shifts
            type: 'regular', // Ensure type is regular
          };
        }
        return s;
      }));
    }
  };

  const onDragEnd = (result) => {
    const { source, destination, draggableId } = result;
    if (!destination || !destination.droppableId || (source.droppableId === destination.droppableId && source.index === destination.index)) {
      return;
    }
    const draggedShift = shifts.find(shift => shift.id === draggableId);
    if (!draggedShift || draggedShift.type === 'time_off') { // Do not process drag for time_off shifts
      return;
    }

    const destParts = destination.droppableId.split('-');
    if (destParts.length < 6 || destParts[0] !== 'user') return; // Basic validation of droppableId format
    const newDestUserId = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;


    let potentialConflictUserId = null;
    let shiftDetailsForCheck = { // The shift as it would be after the drop
      day: newDestDayString,
      startTime: draggedShift.startTime,
      endTime: draggedShift.endTime,
      // id: draggedShift.id // Exclude itself only if not changing user or type logic fundamentally alters "self"
    };

    // Determine whose schedule to check for conflicts
    if (newDestUserId !== UNASSIGNED_SHIFT_USER_ID) { // Moving to a specific user's row (or within it)
      potentialConflictUserId = newDestUserId;
    }
    // No conflict check needed if moving *to* UNASSIGNED_SHIFT_USER_ID row, or moving *within* UNASSIGNED_SHIFT_USER_ID row.

    if (potentialConflictUserId) {
      // If dragging an unassigned shift to a user, it's a new assignment, so no currentShiftIdToExclude.
      // If dragging a user's own shift, exclude that original instance *if it's not changing users*.
      // If changing users, it's like a new shift for the target user, so don't exclude.
      const idToExcludeForConflictCheck = (draggedShift.userId === potentialConflictUserId) ? draggedShift.id : null;

      const detectedConflicts = detectConflicts(shiftDetailsForCheck, shifts, potentialConflictUserId, users, idToExcludeForConflictCheck);
      if (detectedConflicts.length > 0) {
        setSchedulingConflicts(detectedConflicts);
        setPendingShiftOperation({ action: 'drag', data: result });
        setIsConflictModalOpen(true);
        return;
      }
    }
    proceedWithDragEnd(result);
  };

  const handleConflictModalClose = () => {
    setIsConflictModalOpen(false);
    setSchedulingConflicts([]);
    setPendingShiftOperation(null);
  };

  const handleConflictModalConfirm = () => {
    if (pendingShiftOperation) {
      if (pendingShiftOperation.action === 'save') {
        proceedWithSaveShift(pendingShiftOperation.data, pendingShiftOperation.editingShiftId);
      } else if (pendingShiftOperation.action === 'drag') {
        proceedWithDragEnd(pendingShiftOperation.data);
      }
    }
    handleConflictModalClose();
  };

  // --- Other Callbacks (Selection, Copy) ---
  const handleToggleSelectShift = useCallback((shiftId) => {
    const shiftToToggle = shifts.find(s => s.id === shiftId);
    if (shiftToToggle && shiftToToggle.type === 'time_off') return; // Do not select time_off shifts

    setSelectedShiftIds(prev =>
      prev.includes(shiftId) ? prev.filter(id => id !== shiftId) : [...prev, shiftId]
    );
  }, [shifts]);

  const handleDeselectAll = useCallback(() => { setSelectedShiftIds([]); }, []);

  const getAllShiftIdsInCurrentView = useCallback(() => {
    const weekStart = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
    const weekEnd = addDays(weekStart, 6);
    const ids = [];
    shifts.forEach(s => {
      // Exclude 'time_off' shifts from being part of "select all" logic
      if (s.type === 'time_off') return; //

      if (isValid(parseISO(s.day))) {
        const d = parseISO(s.day);
        if (d >= weekStart && d <= weekEnd && (users.find(u => u.id === s.userId) || s.userId === UNASSIGNED_SHIFT_USER_ID)) {
          ids.push(s.id);
        }
      }
    });
    return ids;
  }, [shifts, users, currentDate]); //

  const allShiftIdsInView = getAllShiftIdsInCurrentView();
  const selectedShiftsInViewCount = allShiftIdsInView.filter(id => selectedShiftIds.includes(id)).length;
  const isAllSelectedInView = allShiftIdsInView.length > 0 && selectedShiftsInViewCount === allShiftIdsInView.length;
  const isIndeterminateInView = selectedShiftsInViewCount > 0 && selectedShiftsInViewCount < allShiftIdsInView.length;
  const handleToggleSelectAllInView = useCallback(() => { const idsInView = getAllShiftIdsInCurrentView(); if (isAllSelectedInView) setSelectedShiftIds(prev => prev.filter(id => !idsInView.includes(id))); else setSelectedShiftIds(prev => [...new Set([...prev, ...idsInView])]); }, [getAllShiftIdsInCurrentView, isAllSelectedInView]);
  const handleOpenCopyModal = () => { if (!isAnyShiftSelected) return; setIsCopyModalOpen(true); };
  const handleCloseCopyModal = () => { setIsCopyModalOpen(false); };
  const handleConfirmCopyToWeeks = useCallback(async (targetWeekStartDates) => { if (!isAnyShiftSelected || targetWeekStartDates.length === 0) return; const toCopy = shifts.filter(s => selectedShiftIds.includes(s.id) && s.type !== 'time_off' /* Don't copy time_off? Or handle as needed */); let newCopies = []; targetWeekStartDates.forEach(weekStart => { toCopy.forEach(s => { const origDate = parseISO(s.day); if (!isValid(origDate)) return; let dayIdx = getDay(origDate); if (WEEK_STARTS_ON === 1) dayIdx = (dayIdx === 0) ? 6 : dayIdx - 1; const newDate = addDays(weekStart, dayIdx); newCopies.push({...s, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${newCopies.length}`, day: format(newDate, 'yyyy-MM-dd')}); }); }); await new Promise(r => setTimeout(r, 100)); setShifts(prev => [...prev, ...newCopies]); setSelectedShiftIds([]); }, [selectedShiftIds, shifts, isAnyShiftSelected]);

  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;
  const PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER = 8 + 8 + 8;
  const FIXED_FOOTER_RESERVED_SPACE = 70;
  const paperContentMaxHeight = `calc(100vh - ${TOP_BAR_HEIGHT}px - ${dynamicStickyOffset}px - ${INFO_BANNERS_TOTAL_HEIGHT}px - ${PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER}px - ${FIXED_FOOTER_RESERVED_SPACE}px)`;

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
              <UnassignedShiftsRow
                currentDate={currentDate}
                shifts={shifts.filter(s => s.userId === UNASSIGNED_SHIFT_USER_ID)} // type 'unassigned'
                onAddShift={(userId, day) => handleOpenModal(UNASSIGNED_SHIFT_USER_ID, day)}
                onEditShift={(shift) => handleOpenModal(null, null, shift)}
                onDeleteShift={handleDeleteSingleShift} selectedShiftIds={selectedShiftIds}
                onToggleSelectShift={handleToggleSelectShift} isAnyShiftSelected={isAnyShiftSelected}
                isSticky={unassignedRowSticky} onToggleSticky={() => setUnassignedRowSticky(prev => !prev)}
                stickyTopOffset={CALENDAR_HEADER_HEIGHT}
              />
              <Box sx={{ overflowX: 'auto' }}>
                <Box sx={{ minWidth: 1100 }}>
                  <ShiftsGrid
                    currentDate={currentDate}
                    shifts={shifts.filter(s => s.userId !== UNASSIGNED_SHIFT_USER_ID)} // 'regular' and 'time_off' types
                    users={users}
                    onAddShift={(userId, day) => handleOpenModal(userId, day)} // This opens modal for regular shifts
                    onDeleteShift={handleDeleteSingleShift}
                    onEditShift={(shift) => {
                      if (shift.type === 'time_off') return; // Prevent editing 'time_off' via modal for now
                      handleOpenModal(shift.userId, parseISO(shift.day), shift);
                    }}
                    selectedShiftIds={selectedShiftIds} onToggleSelectShift={handleToggleSelectShift}
                    isAnyShiftSelected={isAnyShiftSelected}
                  />
                </Box>
              </Box>
            </Paper>
          </Box>
          <Paper elevation={3} sx={{ position: 'fixed', bottom: 16, right: 16, p: 1.5, display: 'flex', alignItems: 'center', borderRadius: 2 }}>
            <AccessTimeFilledIcon color="primary" sx={{mr:1}}/> <Typography variant="caption">Bật đồng hồ chấm công</Typography> <Checkbox size="small" sx={{ml:0.5, p:0.2}}/>
          </Paper>

          <ShiftModal
            isOpen={isModalOpen} onClose={handleCloseModal} onSave={handleSaveShift}
            users={users} initialFormState={modalInitialFormState}
            isEditing={!!currentEditingShift && currentEditingShift.type !== 'time_off'} // Modal doesn't edit time_off for now
            isUnassignedContext={modalOpeningContext === 'newUnassigned' || modalOpeningContext === 'editUnassigned'}
            unassignedShiftBeingEdited={currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID ? currentEditingShift : null}
          />
          <CopyShiftsModal isOpen={isCopyModalOpen} onClose={handleCloseCopyModal} onConfirmCopy={handleConfirmCopyToWeeks} currentDate={currentDate} numSelectedShifts={selectedShiftIds.length} />

          <DeleteConfirmationModal
            open={isDeleteSingleModalOpen}
            onClose={() => { setIsDeleteSingleModalOpen(false); setShiftIdToDelete(null); }}
            onSubmit={confirmDeleteSingleShift}
            title="Xác nhận xóa ca"
            info="Bạn có chắc chắn muốn xóa ca làm việc này không?"
            cancelLabel="Hủy bỏ"
            confirmLabel="Xóa"
          />
          <DeleteConfirmationModal
            open={isDeleteSelectedModalOpen}
            onClose={() => setIsDeleteSelectedModalOpen(false)}
            onSubmit={confirmDeleteSelectedShifts}
            title="Xác nhận xóa các ca đã chọn"
            info={`Bạn có chắc chắn muốn xóa ${selectedShiftIds.length} ca đã chọn không?`}
            cancelLabel="Hủy bỏ"
            confirmLabel="Xóa tất cả"
          />
          <SchedulingConflictModal
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
