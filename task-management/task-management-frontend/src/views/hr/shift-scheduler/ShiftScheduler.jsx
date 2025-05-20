// ==============
// ShiftScheduler.jsx
// ==============
import React, {useCallback, useEffect, useState} from 'react';
import {addDays, format, getDay, isValid, parseISO, startOfWeek, subDays} from 'date-fns';
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
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '12:00', duration: '3h 0m', note: 'Morning Shift u1', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's1-2', userId: 'u1', day: '2025-05-19', startTime: '13:00', endTime: '17:15', duration: '4h 15m', note: 'Afternoon Shift u1', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', note: 'Frontend task u2', muiColor: 'error.light', muiTextColor: 'error.darkerText' },
  { id: 's-unassigned-tue', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-20', startTime: '10:00', endTime: '14:00', duration: '4h 0m', note: 'General Task Tue', slots: 3, muiColor: 'grey.300', muiTextColor: 'text.primary' },
  { id: 's-unassigned-wed', userId: UNASSIGNED_SHIFT_USER_ID, day: '2025-05-21', startTime: '15:00', endTime: '18:00', duration: '3h 0m', note: 'Evening Cover Wed', slots: 1, muiColor: 'grey.300', muiTextColor: 'text.primary' },
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
  const [currentDate, setCurrentDate] = useState(new Date(new Date('2025-05-19').setHours(0,0,0,0)));
  const [shifts, setShifts] = useState(initialShifts);
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
    setIsModalOpen(false);
    setCurrentEditingShift(null);
    setModalOpeningContext(null);
  };

  const handleSaveShift = (formData) => {
    const { userIds: rawUserIds, day, startTime, endTime, note, slots: formSlots, _initiatedAsNewUnassignedContext } = formData;
    const userIds = rawUserIds || [];

    if (userIds.length === 0 && !_initiatedAsNewUnassignedContext && !(currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID && userIds.length === 0) ) {
      alert("Vui lòng chọn ít nhất một nhân viên.");
      return;
    }
    if (!day || !startTime || !endTime) {
      alert("Vui lòng điền đầy đủ Ngày, Giờ bắt đầu và Giờ kết thúc."); return;
    }
    try {
      if(!isValid(parseISO(day))) throw new Error("Invalid day format");
      if(!/^\d{2}:\d{2}$/.test(startTime) || !/^\d{2}:\d{2}$/.test(endTime)) throw new Error("Invalid time format (HH:mm)");
      if(!isValid(parseISO(`${day}T${startTime}`))) throw new Error("Invalid start time value");
      if(!isValid(parseISO(`${day}T${endTime}`))) throw new Error("Invalid end time value");
    } catch (e) {
      alert(`Dữ liệu ngày giờ không hợp lệ: ${e.message}.`);
      return;
    }

    const newDuration = calculateDuration(day, startTime, endTime);
    const shiftBaseData = { day, startTime, endTime, note, duration: newDuration };

    let totalInitialSlotsDefined = parseInt(formSlots, 10);
    if (isNaN(totalInitialSlotsDefined) || totalInitialSlotsDefined < 0) {
      totalInitialSlotsDefined = (_initiatedAsNewUnassignedContext || (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID)) ? 1 : 0;
    }

    const actualUserIdsToAssign = userIds.filter(uid => users.find(u => u.id === uid));
    let newShiftsCreatedForUsers = [];
    let assignedCountThisAction = 0;

    if (_initiatedAsNewUnassignedContext) {
      let slotsForThisNewUnassignedOp = totalInitialSlotsDefined;

      if (actualUserIdsToAssign.length > 0) {
        for (const selectedUserId of actualUserIdsToAssign) {
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';

          newShiftsCreatedForUsers.push({
            ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor,
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
          muiColor: 'grey.300', muiTextColor: 'text.primary',
        };
      }

      setShifts(prevShifts => {
        let updatedShifts = [...prevShifts, ...newShiftsCreatedForUsers];
        if (newUnassignedShiftTemplate) {
          updatedShifts.push(newUnassignedShiftTemplate);
        }
        return updatedShifts;
      });

    } else if (currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID) {
      let slotsAvailableInExistingTemplate = parseInt(currentEditingShift.slots, 10);
      let newTotalSlotsForTemplateDefinition = totalInitialSlotsDefined;

      if (actualUserIdsToAssign.length > 0) {
        for (const selectedUserId of actualUserIdsToAssign) {
          if (slotsAvailableInExistingTemplate <= 0) break;
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
          newShiftsCreatedForUsers.push({
            ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor,
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`,
          });
          slotsAvailableInExistingTemplate--;
          assignedCountThisAction++;
        }
      }

      setShifts(prevShifts => {
        let finalTemplateSlots = newTotalSlotsForTemplateDefinition - assignedCountThisAction;
        if (formSlots === undefined || formSlots === null || isNaN(parseInt(formSlots,10))) {
          finalTemplateSlots = parseInt(currentEditingShift.slots, 10) - assignedCountThisAction;
        }
        if (finalTemplateSlots < 0) finalTemplateSlots = 0;

        let updatedShifts = prevShifts.map(s => {
          if (s.id === currentEditingShift.id) {
            return { ...s, ...shiftBaseData, slots: finalTemplateSlots };
          }
          return s;
        });

        if (finalTemplateSlots <= 0 && currentEditingShift) {
          updatedShifts = updatedShifts.filter(s => s.id !== currentEditingShift.id);
        }
        return [...updatedShifts, ...newShiftsCreatedForUsers];
      });

    } else {
      let newShiftsToAdd_regular = [];
      let shiftsToUpdateDetails_regular = [];
      let originalShiftToDeleteId_regular = null;

      if (currentEditingShift) {
        const originalUserIdOfEditedShift = currentEditingShift.userId;
        if (actualUserIdsToAssign.length === 0 && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID) {
          originalShiftToDeleteId_regular = currentEditingShift.id;
        } else {
          let originalUserStillSelected = false;
          actualUserIdsToAssign.forEach(selectedUserId => {
            const userForColor = users.find(u => u.id === selectedUserId);
            const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
            const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
            const completeShiftData = { ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor };

            if (selectedUserId === originalUserIdOfEditedShift) {
              shiftsToUpdateDetails_regular.push({ id: currentEditingShift.id, data: completeShiftData });
              originalUserStillSelected = true;
            } else {
              newShiftsToAdd_regular.push({ ...completeShiftData, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}` });
            }
          });
          if (!originalUserStillSelected && currentEditingShift.userId !== UNASSIGNED_SHIFT_USER_ID) {
            originalShiftToDeleteId_regular = currentEditingShift.id;
          }
        }
      } else {
        if (actualUserIdsToAssign.length === 0) {
          alert("Vui lòng chọn ít nhất một nhân viên."); return;
        }
        actualUserIdsToAssign.forEach(selectedUserId => {
          const userForColor = users.find(u => u.id === selectedUserId);
          const muiColor = userForColor?.avatarBgColor?.replace('.main','.light') || 'grey.200';
          const muiTextColor = userForColor?.avatarBgColor?.replace('.main','.darkerText') || 'text.primary';
          newShiftsToAdd_regular.push({
            ...shiftBaseData, userId: selectedUserId, muiColor, muiTextColor,
            id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}`
          });
        });
      }

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

  const handleDeleteSingleShift = (shiftIdToDelete) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ca làm việc này không?")) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
      setSelectedShiftIds(prevSelected => prevSelected.filter(id => id !== shiftIdToDelete));
    }
  };

  const onDragEnd = (result) => {
    const { source, destination, draggableId } = result;

    if (!destination || !destination.droppableId) { return; }
    if (source.droppableId === destination.droppableId && source.index === destination.index) { return; }

    const draggedShiftOriginal = shifts.find(shift => shift.id === draggableId);
    if (!draggedShiftOriginal) { return; }

    const destParts = destination.droppableId.split('-');
    if (destParts.length < 6 || destParts[0] !== 'user') { return; }
    const newDestUserId = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    // Scenario 1: An Unassigned Shift is dragged to a User's row (Assignment)
    if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID && newDestUserId !== UNASSIGNED_SHIFT_USER_ID) {
      const targetUserId = newDestUserId;
      const userForColor = users.find(u => u.id === targetUserId);
      const newMuiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
      const newMuiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';

      setShifts(prevShifts => {
        let sourceTemplateProcessed = false;
        const updatedShiftList = prevShifts.map(s => {
          if (s.id === draggableId) {
            sourceTemplateProcessed = true;
            return s.slots > 1 ? { ...s, slots: s.slots - 1 } : null;
          }
          return s;
        }).filter(Boolean);

        if (!sourceTemplateProcessed) return prevShifts;

        const newAssignedShift = {
          ...draggedShiftOriginal,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-assigned-${targetUserId}`,
          userId: targetUserId, day: newDestDayString, slots: undefined,
          muiColor: newMuiColor, muiTextColor: newMuiTextColor,
        };
        return [...updatedShiftList, newAssignedShift];
      });
    }
    // Scenario 2: A User's Shift is dragged to the Unassigned Row (Create New Unassigned Template)
    else if (draggedShiftOriginal.userId !== UNASSIGNED_SHIFT_USER_ID && newDestUserId === UNASSIGNED_SHIFT_USER_ID) {
      setShifts(prevShifts => {
        const remainingShifts = prevShifts.filter(s => s.id !== draggableId);
        const newUnassignedShiftTemplate = {
          id: `s${Date.now()}-unassigned-${Math.random().toString(16).slice(2)}`,
          userId: UNASSIGNED_SHIFT_USER_ID,
          day: newDestDayString,
          startTime: draggedShiftOriginal.startTime,
          endTime: draggedShiftOriginal.endTime,
          duration: draggedShiftOriginal.duration,
          note: draggedShiftOriginal.note,
          slots: 1,
          muiColor: 'grey.300',
          muiTextColor: 'text.primary',
        };
        return [...remainingShifts, newUnassignedShiftTemplate];
      });
    }
    // Scenario 3: An Unassigned Shift is moved within the Unassigned Row
    else if (draggedShiftOriginal.userId === UNASSIGNED_SHIFT_USER_ID && newDestUserId === UNASSIGNED_SHIFT_USER_ID) {
      setShifts(prevShifts =>
        prevShifts.map(shift => {
          if (shift.id === draggableId) {
            return {
              ...shift, // Preserves ID, slots, note, times, etc.
              day: newDestDayString, // Only day changes
              // Ensure unassigned styling is maintained
              muiColor: 'grey.300',
              muiTextColor: 'text.primary',
            };
          }
          return shift;
        })
      );
    }
    // Scenario 4: A User's Shift is moved to another User's row or their own different day
    else if (draggedShiftOriginal.userId !== UNASSIGNED_SHIFT_USER_ID && newDestUserId !== UNASSIGNED_SHIFT_USER_ID) {
      setShifts(prevShifts =>
        prevShifts.map(shift => {
          if (shift.id === draggableId) {
            const userForColor = users.find(u => u.id === newDestUserId);
            const newMuiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
            const newMuiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';
            return {
              ...shift,
              day: newDestDayString,
              userId: newDestUserId,
              muiColor: newMuiColor,
              muiTextColor: newMuiTextColor,
              slots: undefined,
            };
          }
          return shift;
        })
      );
    }
  };

  const handleToggleSelectShift = useCallback((shiftId) => { setSelectedShiftIds(prev => prev.includes(shiftId) ? prev.filter(id => id !== shiftId) : [...prev, shiftId]); }, []);
  const handleDeselectAll = useCallback(() => { setSelectedShiftIds([]); }, []);
  const handleDeleteSelectedShifts = useCallback(() => { if (!isAnyShiftSelected || !window.confirm(`Xóa ${selectedShiftIds.length} ca đã chọn?`)) return; setShifts(prev => prev.filter(s => !selectedShiftIds.includes(s.id))); setSelectedShiftIds([]); }, [selectedShiftIds, isAnyShiftSelected]);
  const getAllShiftIdsInCurrentView = useCallback(() => { const weekStart = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON }); const weekEnd = addDays(weekStart, 6); const ids = []; shifts.forEach(s => { if (isValid(parseISO(s.day))) { const d = parseISO(s.day); if (d >= weekStart && d <= weekEnd && (users.find(u => u.id === s.userId) || s.userId === UNASSIGNED_SHIFT_USER_ID)) ids.push(s.id); } }); return ids; }, [shifts, users, currentDate]);
  const allShiftIdsInView = getAllShiftIdsInCurrentView();
  const selectedShiftsInViewCount = allShiftIdsInView.filter(id => selectedShiftIds.includes(id)).length;
  const isAllSelectedInView = allShiftIdsInView.length > 0 && selectedShiftsInViewCount === allShiftIdsInView.length;
  const isIndeterminateInView = selectedShiftsInViewCount > 0 && selectedShiftsInViewCount < allShiftIdsInView.length;
  const handleToggleSelectAllInView = useCallback(() => { const idsInView = getAllShiftIdsInCurrentView(); if (isAllSelectedInView) setSelectedShiftIds(prev => prev.filter(id => !idsInView.includes(id))); else setSelectedShiftIds(prev => [...new Set([...prev, ...idsInView])]); }, [getAllShiftIdsInCurrentView, isAllSelectedInView]);
  const handleOpenCopyModal = () => { if (!isAnyShiftSelected) return; setIsCopyModalOpen(true); };
  const handleCloseCopyModal = () => { setIsCopyModalOpen(false); };
  const handleConfirmCopyToWeeks = useCallback(async (targetWeekStartDates) => { if (!isAnyShiftSelected || targetWeekStartDates.length === 0) return; const toCopy = shifts.filter(s => selectedShiftIds.includes(s.id)); let newCopies = []; targetWeekStartDates.forEach(weekStart => { toCopy.forEach(s => { const origDate = parseISO(s.day); if (!isValid(origDate)) return; let dayIdx = getDay(origDate); if (WEEK_STARTS_ON === 1) dayIdx = (dayIdx === 0) ? 6 : dayIdx - 1; const newDate = addDays(weekStart, dayIdx); newCopies.push({...s, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${newCopies.length}`, day: format(newDate, 'yyyy-MM-dd')}); }); }); await new Promise(r => setTimeout(r, 100)); setShifts(prev => [...prev, ...newCopies]); setSelectedShiftIds([]); }, [selectedShiftIds, shifts, isAnyShiftSelected]);

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
                shifts={shifts.filter(s => s.userId === UNASSIGNED_SHIFT_USER_ID)}
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
                    shifts={shifts.filter(s => s.userId !== UNASSIGNED_SHIFT_USER_ID)}
                    users={users}
                    onAddShift={(userId, day) => handleOpenModal(userId, day)}
                    onDeleteShift={handleDeleteSingleShift}
                    onEditShift={(shift) => handleOpenModal(shift.userId, parseISO(shift.day), shift)}
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
            isEditing={!!currentEditingShift}
            isUnassignedContext={modalOpeningContext === 'newUnassigned' || modalOpeningContext === 'editUnassigned'}
            unassignedShiftBeingEdited={currentEditingShift && currentEditingShift.userId === UNASSIGNED_SHIFT_USER_ID ? currentEditingShift : null}
          />
          <CopyShiftsModal isOpen={isCopyModalOpen} onClose={handleCloseCopyModal} onConfirmCopy={handleConfirmCopyToWeeks} currentDate={currentDate} numSelectedShifts={selectedShiftIds.length} />
        </Container>
      </DragDropContext>
    </LocalizationProvider>
  );
}
