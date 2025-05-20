import React, {useCallback, useEffect, useState} from 'react';
import {addDays, format, getDay, isValid, parseISO, startOfWeek, subDays} from 'date-fns';
import vi from 'date-fns/locale/vi'; // Vietnamese locale for date-fns
import {DragDropContext} from 'react-beautiful-dnd';

import {Box, Checkbox, Container, Paper, Typography} from '@mui/material';
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

const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '09:00', endTime: '12:00', duration: '3h 0m', note: 'Morning Shift with very very very long details to test ellipsis functionality and see if it works as expected', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's1-2', userId: 'u1', day: '2025-05-19', startTime: '13:00', endTime: '17:15', duration: '4h 15m', note: 'Afternoon Shift', muiColor: 'success.light', muiTextColor: 'success.darkerText' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '09:00', endTime: '17:15', duration: '8h 15m', note: 'Frontend task with a lot of details that might overflow the container width', muiColor: 'error.light', muiTextColor: 'error.darkerText' },
  { id: 's3', userId: 'u1', day: '2025-05-21', startTime: '10:00', endTime: '18:30', duration: '8h 30m', note: 'Meeting', muiColor: 'info.light', muiTextColor: 'info.darkerText' },
  { id: 's4', userId: 'u3', day: '2025-05-22', startTime: '14:00', endTime: '18:00', duration: '4h 0m', note: 'Support', muiColor: 'warning.light', muiTextColor: 'warning.darkerText' },
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

export const TOP_BAR_HEIGHT = 61;
export const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
export const PROJECTED_SALES_BANNER_HEIGHT = 36;
export const INFO_BANNERS_TOTAL_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT + PROJECTED_SALES_BANNER_HEIGHT;
export const BULK_ACTIONS_BAR_HEIGHT = 50;
export const WEEK_STARTS_ON = 1; // Monday

export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date().setHours(0,0,0,0)));
  const [shifts, setShifts] = useState(initialShifts);
  const [users, setUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEditingShift, setCurrentEditingShift] = useState(null);
  const [modalInitialFormState, setModalInitialFormState] = useState({
    userIds: [], // CHANGED: from userId to userIds (array)
    day: format(new Date(), 'yyyy-MM-dd'),
    startTime: '09:00',
    endTime: '17:00',
    note: ''
  });
  const [selectedShiftIds, setSelectedShiftIds] = useState([]);
  const [isCopyModalOpen, setIsCopyModalOpen] = useState(false);


  const isAnyShiftSelected = selectedShiftIds.length > 0;

  useEffect(() => {
    const updatedUsers = initialUsers.map(user => {
      const userShifts = shifts.filter(s => s.userId === user.id);
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
        userIds: [shiftToEdit.userId], // Set as array with the single user
        day: shiftToEdit.day,
        startTime: shiftToEdit.startTime,
        endTime: shiftToEdit.endTime,
        note: shiftToEdit.note || '',
      });
    } else {
      setCurrentEditingShift(null);
      setModalInitialFormState({
        userIds: userIdForPreselection ? [userIdForPreselection] : [], // Set as array or empty array
        day: format(day || new Date(), 'yyyy-MM-dd'),
        startTime: '09:00',
        endTime: '17:00',
        note: '',
      });
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentEditingShift(null);
  };

  const handleSaveShift = (formData) => {
    const { userIds, day, startTime, endTime, note } = formData; // userIds is now an array

    if (!userIds || userIds.length === 0) {
      alert("Vui lòng chọn ít nhất một nhân viên.");
      return;
    }
    if (!day || !startTime || !endTime) {
      alert("Vui lòng điền đầy đủ Ngày, Giờ bắt đầu và Giờ kết thúc.");
      return;
    }

    const parsedDay = parseISO(day);
    if (!isValid(parsedDay)) {
      alert("Ngày không hợp lệ. Vui lòng kiểm tra lại định dạng (YYYY-MM-DD).");
      return;
    }

    const start = parseISO(`${format(parsedDay, 'yyyy-MM-dd')}T${startTime}`);
    const end = parseISO(`${format(parsedDay, 'yyyy-MM-dd')}T${endTime}`);

    if (!isValid(start) || !isValid(end)) {
      alert("Giờ bắt đầu hoặc kết thúc không hợp lệ. Vui lòng kiểm tra lại định dạng (HH:mm).");
      return;
    }

    let durationMs = end.getTime() - start.getTime();
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; } // Handles overnight shifts within the same day entry

    const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
    const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    const shiftBaseData = {
      day: format(parsedDay, 'yyyy-MM-dd'),
      startTime,
      endTime,
      duration: `${durationHours}h ${durationMinutes}m`,
      note,
    };

    let newShiftsToAdd = [];
    let shiftsToUpdateDetails = []; // Store {id, data} for updates
    let originalShiftToDeleteId = null;


    if (currentEditingShift) { // Editing mode
      const originalUserIdOfEditedShift = currentEditingShift.userId;
      let originalUserStillSelected = false;

      userIds.forEach(selectedUserId => {
        const userForColor = users.find(u => u.id === selectedUserId);
        const muiColor = userForColor?.avatarBgColor?.replace('.main', '.light') || 'grey.200';
        const muiTextColor = userForColor?.avatarBgColor?.replace('.main', '.darkerText') || 'text.primary';

        const completeShiftData = {
          ...shiftBaseData,
          userId: selectedUserId,
          muiColor,
          muiTextColor,
        };

        if (selectedUserId === originalUserIdOfEditedShift) {
          // This user's shift is the one being edited, mark for update
          shiftsToUpdateDetails.push({ id: currentEditingShift.id, data: completeShiftData });
          originalUserStillSelected = true;
        } else {
          // This is an additional user selected during edit, create a new shift for them
          newShiftsToAdd.push({ ...completeShiftData, id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${selectedUserId}` });
        }
      });

      // If the original user of the edited shift was deselected, mark the original shift for deletion
      if (!originalUserStillSelected) {
        originalShiftToDeleteId = currentEditingShift.id;
      }

    } else { // Adding new shift(s) mode
      userIds.forEach(selectedUserId => {
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

      // 1. Remove original shift if its user was deselected during edit
      if (originalShiftToDeleteId) {
        updatedShifts = updatedShifts.filter(s => s.id !== originalShiftToDeleteId);
      }

      // 2. Apply updates to existing shifts
      shiftsToUpdateDetails.forEach(updateInfo => {
        updatedShifts = updatedShifts.map(s => s.id === updateInfo.id ? { ...s, ...updateInfo.data } : s);
      });

      // 3. Add newly created shifts
      updatedShifts = [...updatedShifts, ...newShiftsToAdd];

      return updatedShifts;
    });

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
    if (destination.droppableId === source.droppableId && destination.index === source.index) return;

    const draggedShift = shifts.find(shift => shift.id === draggableId);
    if (!draggedShift) return;

    const destParts = destination.droppableId.split('-');
    const newUserId = destParts[1];
    const newDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    const userForColor = users.find(u => u.id === newUserId);
    const newMuiColor = userForColor?.avatarBgColor?.replace('.main','.light') || 'grey.200';
    const newMuiTextColor = userForColor?.avatarBgColor?.replace('.main','.darkerText') || 'text.primary';

    setShifts(prevShifts =>
      prevShifts.map(shift =>
        shift.id === draggableId
          ? { ...shift, userId: newUserId, day: newDayString, muiColor: newMuiColor, muiTextColor: newMuiTextColor }
          : shift
      )
    );
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
          if (users.find(u => u.id === shift.userId)) {
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
          ...shift,
          id: `s${Date.now()}-${Math.random().toString(16).slice(2)}-${newCopiedShifts.length}`,
          day: format(newShiftDate, 'yyyy-MM-dd'),
        });
      });
    });

    await new Promise(resolve => setTimeout(resolve, 500));

    setShifts(prevShifts => [...prevShifts, ...newCopiedShifts]);
    setSelectedShiftIds([]);
  }, [selectedShiftIds, shifts, isAnyShiftSelected]);


  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;
  const PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER = 8 + 8 + 8;
  const FIXED_FOOTER_RESERVED_SPACE = 70;
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
                // The CalendarHeader inside will manage its own sticky position relative to this Paper
              }}
            >
              <CalendarHeader
                currentDate={currentDate}
                // No direct stickyTopOffset here, as it's relative to Paper's scroll container.
                // It will stick to top:0 of this scrollable Paper.
                onToggleSelectAll={handleToggleSelectAllInView}
                isAllSelectedInView={isAllSelectedInView}
                isIndeterminateInView={isIndeterminateInView}
              />
              <Box sx={{ overflowX: 'auto' }}>
                <Box sx={{ minWidth: 1100 }}>
                  <ShiftsGrid
                    currentDate={currentDate}
                    shifts={shifts}
                    users={users}
                    onAddShift={handleOpenModal} // Note: onAddShift in DayCell passes (userId, day), onAddAnotherShift in ShiftCard passes (shift.userId, day)
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
            users={users}
            initialFormState={modalInitialFormState}
            isEditing={!!currentEditingShift}
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

