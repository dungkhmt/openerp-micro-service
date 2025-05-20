// ==============
// ShiftScheduler.jsx
// ==============
import React, {useCallback, useEffect, useState} from 'react';
import {addDays, format, getDay, isValid, parseISO, startOfWeek, subDays} from 'date-fns';
import vi from 'date-fns/locale/vi'; // Vietnamese locale for date-fns
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
import UnassignedShiftsRow from "./UnassignedShiftsRow.jsx"; // NEW IMPORT

export const TOP_BAR_HEIGHT = 61;
export const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
export const PROJECTED_SALES_BANNER_HEIGHT = 0; // MODIFIED: Was 36
export const INFO_BANNERS_TOTAL_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT + PROJECTED_SALES_BANNER_HEIGHT;
export const BULK_ACTIONS_BAR_HEIGHT = 50;
export const CALENDAR_HEADER_HEIGHT = 57; // Approximate height of CalendarHeader
export const UNASSIGNED_ROW_HEIGHT = 60; // Approximate height of UnassignedShiftsRow
export const WEEK_STARTS_ON = 1; // Monday
export const UNASSIGNED_SHIFT_USER_ID = '---UNASSIGNED---'; // NEW CONSTANT

const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '12:00', duration: '3h 0m', note: 'Morning Shift with very very very long details to test ellipsis functionality', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's1-2', userId: 'u1', day: '2025-05-19', startTime: '13:00', endTime: '17:15', duration: '4h 15m', note: 'Afternoon Shift', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', note: 'Frontend task', muiColor: 'error.light', muiTextColor: 'error.darkerText' },
  { id: 's3', userId: 'u1', day: '2025-05-21', startTime: '10:00', endTime: '18:30', duration: '8h 30m', note: 'Meeting', muiColor: 'info.light', muiTextColor: 'info.darkerText' },
  { id: 's4', userId: 'u3', day: '2025-05-22', startTime: '14:00', endTime: '18:00', duration: '4h 0m', note: 'Support', muiColor: 'warning.light', muiTextColor: 'warning.darkerText' },
  // Example Unassigned Shift
  { id: 's-unassigned-1', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-19', startTime: '10:00', endTime: '14:00', duration: '4h 0m', note: 'General Task', slots: 3, muiColor: 'grey.300', muiTextColor: 'text.primary' },
  { id: 's-unassigned-2', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-21', startTime: '15:00', endTime: '18:00', duration: '3h 0m', note: 'Evening Cover', slots: 1, muiColor: 'grey.300', muiTextColor: 'text.primary' },
];

const initialUsers = [
  { id: 'u1', name: 'NV Ánh Nguyệt', summary: '8h 15m', avatarLetter: 'AN', avatarBgColor: 'secondary.main' },
  { id: 'u2', name: 'NV Bảo Long', summary: '8h 15m', avatarLetter: 'BL', avatarBgColor: 'primary.main' },
  { id: 'u3', name: 'NV Cẩm Tú', summary: '0h 0m', avatarLetter: 'CT', avatarBgColor: 'warning.main' },
  { id: 'uHPT', name: 'Hieu Phan Trung', summary: '0h 0m', avatarLetter: 'HT', avatarBgColor: 'success.main' },
  { id: 't77', name: 'Test User 1', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't74', name: 'Test User 2', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't714', name: 'Test User 3', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't34', name: 'Test User 5', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 't64', name: 'Test User 9', summary: '0h 0m', avatarLetter: 'T4', avatarBgColor: 'info.main' },
  { id: 'u4', name: 'NV Đăng Khoa', summary: '0h 0m', avatarLetter: 'ĐK', avatarBgColor: 'error.main' },
  { id: 'u5', name: 'NV Thu Hà', summary: '0h 0m', avatarLetter: 'TH', avatarBgColor: 'secondary.light' },
  { id: 'u6', name: 'NV Minh Quân', summary: '0h 0m', avatarLetter: 'MQ', avatarBgColor: 'primary.light' },
  { id: 'u7', name: 'NV Phương Mai', summary: '0h 0m', avatarLetter: 'PM', avatarBgColor: 'warning.light' },
  { id: 'u8', name: 'NV Gia Huy', summary: '0h 0m', avatarLetter: 'GH', avatarBgColor: 'success.light' },
  { id: 'u9', name: 'NV Ngọc Lan', summary: '0h 0m', avatarLetter: 'NL', avatarBgColor: 'info.light' },
  { id: 'u10', name: 'NV Tiến Dũng', summary: '0h 0m', avatarLetter: 'TD', avatarBgColor: 'error.light' },
  { id: 'u11', name: 'NV Hoài An', summary: '0h 0m', avatarLetter: 'HA', avatarBgColor: 'secondary.main' },
  { id: 'u12', name: 'NV Tuấn Kiệt', summary: '0h 0m', avatarLetter: 'TK', avatarBgColor: 'primary.main' },
];


export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date('2025-05-19').setHours(0,0,0,0))); // For consistent testing
  const [shifts, setShifts] = useState(initialShifts);
  const [users, setUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [modalInitialFormState, setModalInitialFormState] = useState({
    userIds: [],
    day: format(new Date(), 'yyyy-MM-dd'),
    startTime: '09:00',
    endTime: '17:00',
    note: '',
    slots: 1, // Default for unassigned shifts
  });
  const [selectedShiftIds, setSelectedShiftIds] = useState([]);
  const [isCopyModalOpen, setIsCopyModalOpen] = useState(false);
  const [unassignedRowSticky, setUnassignedRowSticky] = useState(true); // NEW STATE for sticky unassigned row

  const isAnyShiftSelected = selectedShiftIds.length > 0;

  const calculateDuration = (day, startTime, endTime) => {
    if (!day || !startTime || !endTime) return '0h 0m';
    const start = parseISO(`${day}T${startTime}`);
    const end = parseISO(`${day}T${endTime}`);
    if (!isValid(start) || !isValid(end)) return '0h 0m';
    let durationMs = end.getTime() - start.getTime();
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; }
    const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
    const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${durationHours}h ${durationMinutes}m`;
  };


  useEffect(() => {
    const updatedUsers = initialUsers.map(user => {
      const userShifts = shifts.filter(s => s.userId === user.id && s.userId !== UNASSIGNED_SHIFT_USER_ID);
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
      const totalHours = Math.floor(totalMs / (1000 * 60 * 60));
      const totalMinutes = Math.floor((totalMs % (1000 * 60 * 60)) / (1000 * 60));
      return { ...user, summary: `${totalHours}h ${totalMinutes}m` };
    });
    setUsers(updatedUsers);
  }, [shifts]);


  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date(new Date().setHours(0,0,0,0)));

  const handleOpenModal = (userIdForPreselection, day, shiftToEdit = null) => {
    if (shiftToEdit) {
      setCurrentEditingShift(shiftToEdit);
      setModalInitialFormState({
        userIds: shiftToEdit.userId === UNASSIGNED_SHIFT_USER_ID ? [] : [shiftToEdit.userId], // If unassigned, userIds empty to pick assignees
        day: shiftToEdit.day,
        startTime: shiftToEdit.startTime,
        endTime: shiftToEdit.endTime,
        note: shiftToEdit.note || '',
        slots: shiftToEdit.userId === UNASSIGNED_SHIFT_USER_ID ? (shiftToEdit.slots || 1) : 1,
      });
    } else { // Adding new shift
      setCurrentEditingShift(null);
      const isAddingUnassigned = userIdForPreselection === UNASSIGNED_SHIFT_USER_ID;
      setModalInitialFormState({
        userIds: userIdForPreselection ? [userIdForPreselection] : [],
        day: format(day || new Date(), 'yyyy-MM-dd'),
        startTime: '09:00',
        endTime: '17:00',
        note: '',
        // MODIFIED: Ensure slots is 1 for new unassigned, otherwise 1 (or undefined if you prefer modal to default)
        slots: isAddingUnassigned ? 1 : (initialUsers.length > 0 ? 1 : 1 /* Fallback if needed */),
      });
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentEditingShift(null);
  };

  const handleSaveShift = (formData) => {
    const { userIds, day, startTime, endTime, note, slots: formSlots } = formData;

    if ((!userIds || userIds.length === 0) && (!currentEditingShift || currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID)) {
      if (!(currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID && userIds.length === 0)) { // Allow no user selection if just editing unassigned template
        alert("Vui lòng chọn ít nhất một nhân viên.");
        return;
      }
    }
    if (!day || !startTime || !endTime) {
      alert("Vui lòng điền đầy đủ Ngày, Giờ bắt đầu và Giờ kết thúc.");
      return;
    }
    // ... (validation for day, start, end)

    const newDuration = calculateDuration(day, startTime, endTime);
    const shiftBaseData = { day, startTime, endTime, note, duration: newDuration };

    // Case 1: Editing an existing Unassigned Shift Template (potentially assigning it too)
    if (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID) {
      const actualUserIdsToAssign = userIds.filter(uid => uid !== UNASSIGNED_SHIFT_USER_ID);
      let currentTemplateSlots = parseInt(currentEditingShift.slots, 10);
      let newTemplateSlots = formSlots !== undefined ? parseInt(formSlots, 10) : currentTemplateSlots; // User might have updated slot count for template

      let newShiftsForAssignedUsers = [];
      let assignedCount = 0;

      if (actualUserIdsToAssign.length > 0) {
        for (const selectedUserId of actualUserIdsToAssign) {
          if (currentTemplateSlots <= 0) break; // No more slots in template to assign

          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';

          newShiftsForAssignedUsers.push({
            ...shiftBaseData,
            userId: selectedUserId,
            muiColor,
            muiTextColor,
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`,
          });
          currentTemplateSlots--; // Decrement available slots from template
          assignedCount++;
        }
      }

      setShifts(prevShifts => {
        let updatedShifts = prevShifts.map(s => {
          if (s.id === currentEditingShift.id) {
            // Update the template itself (time, note, and its new total slots)
            // The number of available slots is currentTemplateSlots after assignments
            return { ...s, ...shiftBaseData, slots: newTemplateSlots - assignedCount < 0 ? 0 : newTemplateSlots - assignedCount };
          }
          return s;
        });
        // If all slots of template are used up by this assignment AND its definition, remove it
        if ((newTemplateSlots - assignedCount) <= 0) {
          updatedShifts = updatedShifts.filter(s => s.id !== currentEditingShift.id);
        }
        return [...updatedShifts, ...newShiftsForAssignedUsers];
      });

      // Case 2: Creating a new Unassigned Shift Template
    } else if (!currentEditingShift && userIds.includes(UNASSIGNED_SHIFT_USER_ID)) {
      const newUnassignedShift = {
        ...shiftBaseData,
        id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-unassigned`,
        userId: UNASSIGNED_SHIFT_USER_ID,
        slots: parseInt(formSlots, 10) || 1,
        muiColor: 'grey.300',
        muiTextColor: 'text.primary',
      };
      setShifts(prevShifts => [...prevShifts, newUnassignedShift]);

      // Case 3: Regular shift add/edit for specific users
    } else {
      let newShiftsToAdd = [];
      let shiftsToUpdateDetails = [];
      let originalShiftToDeleteId = null;

      if (currentEditingShift) { // Editing existing user shift
        const originalUserIdOfEditedShift = currentEditingShift.userId;
        let originalUserStillSelected = false;

        userIds.forEach(selectedUserId => {
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
          const completeShiftData = { ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor };

          if (selectedUserId === originalUserIdOfEditedShift) {
            shiftsToUpdateDetails.push({ id: currentEditingShift.id, data: completeShiftData });
            originalUserStillSelected = true;
          } else {
            newShiftsToAdd.push({ ...completeShiftData, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}` });
          }
        });
        if (!originalUserStillSelected) {
          originalShiftToDeleteId = currentEditingShift.id;
        }
      } else { // Adding new shift(s) for users
        userIds.forEach(selectedUserId => {
          if (selectedUserId === UNASSIGNED_SHIFT_USER_ID) return; // Should have been caught by Case 2
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main','.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main','.darkerText') || 'text.primary';
          newShiftsToAdd.push({
            ...shiftBaseData,
            userId: selectedUserId,
            muiColor,
            muiTextColor,
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`
          });
        });
      }

      setShifts(prevShifts => {
        let updatedShifts = [...prevShifts];
        if (originalShiftToDeleteId) {
          updatedShifts = updatedShifts.filter(s => s.id !== originalShiftToDeleteId);
        }
        shiftsToUpdateDetails.forEach(updateInfo => {
          updatedShifts = updatedShifts.map(s => s.id === updateInfo.id ? { ...s, ...updateInfo.data } : s);
        });
        return [...updatedShifts, ...newShiftsToAdd];
      });
    }
    handleCloseModal();
  };


  const handleDeleteSingleShift = (shiftIdToDelete) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ca làm việc này không?")) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
      setSelectedShiftIds(prevSelected => prevSelected.filter(id => id !== shiftIdToDelete));
    }
  };

  const onDragEnd = (result) => {
    const { source, destination, draggableId } = result;

    if (!destination) return;

    const draggedShiftOriginal = shifts.find(shift => shift.id === draggableId);
    if (!draggedShiftOriginal) return;

    // Case 1: Dragging an Unassigned Shift to a User
    if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID&& destination.droppableId && !destination.droppableId.startsWith(`user-${UNASSIGNED_SHIFT_USER_ID}`)) {
      const destParts = destination.droppableId.split('-'); // e.g., "user-u1-day-2025-05-19"
      if (destParts[0] !== 'user') return; // Make sure it's a user droppable

      const targetUserId = destParts[1];
      const targetDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

      if (targetUserId === UNASSIGNED_SHIFT_USER_ID) return; // Cannot drag unassigned to unassigned (use modal for that)

      const userForColor = users.find(u => u.id === targetUserId);
      const newMuiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
      const newMuiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';

      setShifts(prevShifts => {
        let sourceShiftUpdated = false;
        const updatedShifts = prevShifts.map(s => {
          if (s.id === draggableId) { // This is the unassigned template
            sourceShiftUpdated = true;
            if (s.slots > 1) {
              return { ...s, slots: s.slots - 1 }; // Decrement slot count
            }
            return null; // Mark for removal if last slot used
          }
          return s;
        }).filter(Boolean); // Remove nulls

        if (!sourceShiftUpdated) return prevShifts; // Should not happen if draggableId was found

        // Create the new assigned shift for the user
        const newAssignedShift = {
          ...draggedShiftOriginal, // Copy base details
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-assigned`,
          userId: targetUserId,
          day: targetDayString,
          slots: undefined, // Assigned shifts don't have slots
          muiColor: newMuiColor,
          muiTextColor: newMuiTextColor,
        };
        return [...updatedShifts, newAssignedShift];
      });

      // Case 2: Regular shift drag (user to user, user to different day, or unassigned to different day in unassigned row)
    } else {
      if (destination.droppableId === source.droppableId && destination.index === source.index) return;

      const destParts = destination.droppableId.split('-');
      const newUserId = destParts[1];
      const newDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

      let newMuiColor = draggedShiftOriginal.muiColor;
      let newMuiTextColor = draggedShiftOriginal.muiTextColor;

      if (newUserId !== UNASSIGNED_SHIFT_USER_ID && newUserId !== draggedShiftOriginal.userId) {
        const userForColor = users.find(u => u.id === newUserId);
        newMuiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
        newMuiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
      } else if (newUserId === UNASSIGNED_SHIFT_USER_ID) {
        newMuiColor = 'grey.300';
        newMuiTextColor = 'text.primary';
      }


      setShifts(prevShifts =>
        prevShifts.map(shift =>
          shift.id === draggableId
            ? { ...shift, userId: newUserId, day: newDayString, muiColor: newMuiColor, muiTextColor: newMuiTextColor }
            : shift
        )
      );
    }
  };

  const handleToggleSelectShift = useCallback((shiftId) => {
    setSelectedShiftIds(prevSelected =>
      prevSelected.includes(shiftId)
        ? prevSelected.filter(id => id !== shiftId)
        : [...prevSelected, shiftId]
    );
  }, []);

  const handleDeselectAll = useCallback(() => {
    setSelectedShiftIds([]);
  }, []);

  const handleDeleteSelectedShifts = useCallback(() => {
    if (!isAnyShiftSelected || !window.confirm(`Bạn có chắc chắn muốn xóa ${selectedShiftIds.length} ca đã chọn không?`)) return;
    setShifts(prevShifts => prevShifts.filter(shift => !selectedShiftIds.includes(shift.id)));
    setSelectedShiftIds([]);
  }, [selectedShiftIds, isAnyShiftSelected]);

  const getAllShiftIdsInCurrentView = useCallback(() => {
    const weekStart = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
    const weekEnd = addDays(weekStart, 6);
    const idsInView = [];
    shifts.forEach(shift => {
      if (isValid(parseISO(shift.day))) {
        const shiftDate = parseISO(shift.day);
        if (shiftDate >= weekStart && shiftDate <= weekEnd) {
          if (users.find(u => u.id === shift.userId) || shift.userId === UNASSIGNED_SHIFT_USER_ID) { // Include unassigned shifts
            idsInView.push(shift.id);
          }
        }
      }
    });
    return idsInView;
  }, [shifts, users, currentDate]);

  const allShiftIdsInView = getAllShiftIdsInCurrentView();
  const selectedShiftsInViewCount = allShiftIdsInView.filter(id => selectedShiftIds.includes(id)).length;

  const isAllSelectedInView = allShiftIdsInView.length > 0 && selectedShiftsInViewCount === allShiftIdsInView.length;
  const isIndeterminateInView = selectedShiftsInViewCount > 0 && selectedShiftsInViewCount < allShiftIdsInView.length;

  const handleToggleSelectAllInView = useCallback(() => {
    const idsInView = getAllShiftIdsInCurrentView();
    if (isAllSelectedInView) {
      setSelectedShiftIds(prev => prev.filter(id => !idsInView.includes(id)));
    } else {
      setSelectedShiftIds(prev => [...new Set([...prev, ...idsInView])]);
    }
  }, [getAllShiftIdsInCurrentView, isAllSelectedInView]);

  const handleOpenCopyModal = () => {
    if (!isAnyShiftSelected) return;
    setIsCopyModalOpen(true);
  };

  const handleCloseCopyModal = () => {
    setIsCopyModalOpen(false);
  };

  const handleConfirmCopyToWeeks = useCallback(async (targetWeekStartDates) => {
    if (!isAnyShiftSelected || targetWeekStartDates.length === 0) return;

    const shiftsToCopyDetails = shifts.filter(shift => selectedShiftIds.includes(shift.id));
    let newCopiedShifts = [];

    targetWeekStartDates.forEach(targetWeekStartDate => {
      shiftsToCopyDetails.forEach(shift => {
        const originalShiftDate = parseISO(shift.day);
        if (!isValid(originalShiftDate)) return;

        let dayIndexOfWeek = getDay(originalShiftDate);
        if (WEEK_STARTS_ON === 1) {
          dayIndexOfWeek = (dayIndexOfWeek === 0) ? 6 : dayIndexOfWeek - 1;
        }
        const newShiftDate = addDays(targetWeekStartDate, dayIndexOfWeek);
        newCopiedShifts.push({
          ...shift, // retains slots if it's an unassigned shift
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${newCopiedShifts.length}`,
          day: format(newShiftDate, 'yyyy-MM-dd'),
        });
      });
    });
    await new Promise(resolve => setTimeout(resolve, 500)); // Simulate API call
    setShifts(prevShifts => [...prevShifts, ...newCopiedShifts]);
    setSelectedShiftIds([]);
  }, [selectedShiftIds, shifts, isAnyShiftSelected]);


  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;
  const PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER = 8 + 8 + 8; // px + mt + mb
  const FIXED_FOOTER_RESERVED_SPACE = 70; // For the time clock paper

  // Calculate height taken by sticky elements inside the scrollable paper
  let stickyElementsInsidePaperHeight = CALENDAR_HEADER_HEIGHT;
  if (unassignedRowSticky) {
    stickyElementsInsidePaperHeight += UNASSIGNED_ROW_HEIGHT;
  }

  const paperContentMaxHeight = `calc(100vh - ${TOP_BAR_HEIGHT}px - ${dynamicStickyOffset}px - ${INFO_BANNERS_TOTAL_HEIGHT}px - ${PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER}px - ${FIXED_FOOTER_RESERVED_SPACE}px)`;


  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DragDropContext onDragEnd={onDragEnd}>
        <Container maxWidth={false} disableGutters sx={{ bgcolor: 'grey.200', minHeight: '100vh' }}>
          <TopBar
            currentDate={currentDate}
            onPrevWeek={handlePrevWeek}
            onNextWeek={handleNextWeek}
            onToday={handleToday}
          />
          <BulkActionsBar
            selectedCount={selectedShiftIds.length}
            onDeleteSelected={handleDeleteSelectedShifts}
            onOpenCopyModal={handleOpenCopyModal}
            onDeselectAll={handleDeselectAll}
          />
          <Box sx={{px: {xs: 0, sm:1, md:2}, py:1}}>
            <InfoBanners currentDate={currentDate} stickyTopOffset={dynamicStickyOffset} />
            <Paper
              elevation={2}
              sx={{
                mt:1,
                overflowY: 'auto',
                maxHeight: paperContentMaxHeight,
              }}
            >
              <CalendarHeader
                currentDate={currentDate}
                // top: 0 relative to this Paper
                onToggleSelectAll={handleToggleSelectAllInView}
                isAllSelectedInView={isAllSelectedInView}
                isIndeterminateInView={isIndeterminateInView}
              />
              <UnassignedShiftsRow
                currentDate={currentDate}
                shifts={shifts.filter(s => s.userId === UNASSIGNED_SHIFT_USER_ID)}
                onAddShift={(userId, day) => handleOpenModal(UNASSIGNED_SHIFT_USER_ID, day)}
                onEditShift={(shift) => handleOpenModal(null, null, shift)} // Edit unassigned template/assign
                onDeleteShift={handleDeleteSingleShift}
                selectedShiftIds={selectedShiftIds}
                onToggleSelectShift={handleToggleSelectShift}
                isAnyShiftSelected={isAnyShiftSelected}
                isSticky={unassignedRowSticky}
                onToggleSticky={() => setUnassignedRowSticky(prev => !prev)}
                stickyTopOffset={CALENDAR_HEADER_HEIGHT} // Sticks below CalendarHeader
              />
              <Box sx={{ overflowX: 'auto' }}>
                <Box sx={{ minWidth: 1100 }}> {/* Ensure content is wide enough for horizontal scroll */}
                  <ShiftsGrid
                    currentDate={currentDate}
                    shifts={shifts.filter(s => s.userId !== UNASSIGNED_SHIFT_USER_ID)} // Pass only user shifts
                    users={users}
                    onAddShift={handleOpenModal}
                    onDeleteShift={handleDeleteSingleShift}
                    onEditShift={(shift) => handleOpenModal(shift.userId, parseISO(shift.day), shift)}
                    selectedShiftIds={selectedShiftIds}
                    onToggleSelectShift={handleToggleSelectShift}
                    isAnyShiftSelected={isAnyShiftSelected}
                  />
                </Box>
              </Box>
            </Paper>
          </Box>

          <Paper elevation={3} sx={{ position: 'fixed', bottom: 16, right: 16, p: 1.5, display: 'flex', alignItems: 'center', borderRadius: 2 }}>
            <AccessTimeFilledIcon color="primary" sx={{mr:1}}/>
            <Typography variant="caption">Bật đồng hồ chấm công</Typography>
            <Checkbox size="small" sx={{ml:0.5, p:0.2}}/>
          </Paper>

          <ShiftModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            onSave={handleSaveShift}
            users={users} // Pass all users for selection
            initialFormState={modalInitialFormState}
            isEditing={!!currentEditingShift}
            // True if creating new unassigned, or editing an existing unassigned shift template
            isUnassignedTemplateMode={
              (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID) ||
              (!currentEditingShift && modalInitialFormState.userIds && modalInitialFormState.userIds.includes(UNASSIGNED_SHIFT_USER_ID))
            }
            unassignedShiftBeingEdited={currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID ? currentEditingShift : null}

          />
          <CopyShiftsModal
            isOpen={isCopyModalOpen}
            onClose={handleCloseCopyModal}
            onConfirmCopy={handleConfirmCopyToWeeks}
            currentDate={currentDate}
            numSelectedShifts={selectedShiftIds.length}
          />
        </Container>
      </DragDropContext>
    </LocalizationProvider>
  );
}
