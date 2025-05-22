// ==============
// ShiftScheduler.jsx
// ==============
import React, {useCallback, useEffect, useState, useMemo} from 'react';
import {addDays, format, getDay, isValid, parseISO, startOfWeek, subDays, endOfWeek} from 'date-fns';
import vi from 'date-fns/locale/vi';
import {DragDropContext} from 'react-beautiful-dnd';
import {
  Box, Container, Paper, Typography, CircularProgress,
  Button
} from '@mui/material';
import {LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDateFns} from '@mui/x-date-pickers/AdapterDateFns';

import CopyShiftsModal from "./CopyShiftsModal.jsx";
import ShiftModal from "./ShiftModal.jsx";
import CalendarHeader from "./CalendarHeader.jsx";
import ShiftsGrid from "./ShiftsGrid.jsx";
import InfoBanners from "./InfoBanners.jsx";
import BulkActionsBar from "./BulkActionBar.jsx";
import TopBar from "./TopBar.jsx";
import UnassignedShiftsRow from "./UnassignedShiftsRow.jsx";
import SchedulingConflictModal from "./SchedulingConflictModal.jsx";
import DeleteConfirmationModal from "../modals/DeleteConfirmationModal.jsx";
import UserFilterDrawer from "./UserFilterDrawer.jsx";
import {request} from "../../../api.js";

export const TOP_BAR_HEIGHT = 61;
export const AVAILABLE_SHIFTS_BANNER_HEIGHT = 36;
export const INFO_BANNERS_TOTAL_HEIGHT = AVAILABLE_SHIFTS_BANNER_HEIGHT;
export const BULK_ACTIONS_BAR_HEIGHT = 50;
export const CALENDAR_HEADER_HEIGHT = 57;
export const UNASSIGNED_ROW_HEIGHT = 60;
export const WEEK_STARTS_ON = 1;
export const FRONTEND_UNASSIGNED_SHIFT_USER_ID = 'UNASSIGNED_PLACEHOLDER_ID';
export const API_UNASSIGNED_USER_ID = null;
export const DEPARTMENT_HEADER_ROW_HEIGHT = 25;
export const USER_ROW_MIN_HEIGHT = 60;


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

const calculateUserWorkHours = (userId, allShifts) => {
  const userShifts = allShifts.filter(s => s.userId === userId && s.type !== 'time_off' && s.userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID);
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
  if (!shiftToCheck.day || !shiftToCheck.startTime || !shiftToCheck.endTime || !targetUserId || targetUserId === FRONTEND_UNASSIGNED_SHIFT_USER_ID) {
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
      const conflictMessage = existingShift.type === 'time_off'
        ? `Trùng với ngày nghỉ của ${user ? user.name : 'NV này'} (từ ${existingShift.startTime} đến ${existingShift.endTime} ngày ${format(parseISO(existingShift.day), 'dd/MM', { locale: vi })})`
        : `Trùng với ca làm việc của ${user ? user.name : 'NV này'} (${existingShift.startTime} - ${existingShift.endTime} ngày ${format(parseISO(existingShift.day), 'dd/MM', { locale: vi })})`;
      conflicts.push({
        type: existingShift.type === 'time_off' ? 'TimeOffOverlap' : 'TimeOverlap',
        shiftId: existingShift.id,
        userName: user ? user.name : targetUserId,
        day: format(parseISO(existingShift.day), 'EEE, MMM dd', { locale: vi }),
        timeRange: `${existingShift.startTime} - ${existingShift.endTime}`,
        message: conflictMessage
      });
    }
  });
  return conflicts;
};

const transformApiShiftToFrontend = (apiShift) => {
  if (!apiShift) return null;
  const day = apiShift.date ? format(parseISO(apiShift.date.toString()), 'yyyy-MM-dd') : null;
  const startTime = apiShift.start_time ? apiShift.start_time.toString().substring(0, 5) : null;
  const endTime = apiShift.end_time ? apiShift.end_time.toString().substring(0, 5) : null;
  const isActuallyUnassigned = apiShift.user_id === API_UNASSIGNED_USER_ID;

  return {
    id: apiShift.id,
    userId: isActuallyUnassigned ? FRONTEND_UNASSIGNED_SHIFT_USER_ID : apiShift.user_id,
    day: day,
    startTime: startTime,
    endTime: endTime,
    note: apiShift.note || '',
    slots: apiShift.slots,
    type: isActuallyUnassigned ? 'unassigned' : 'regular',
    duration: calculateDuration(day, startTime, endTime),
    ...(isActuallyUnassigned && { muiColor: 'grey.300', muiTextColor: 'text.primary' }),
  };
};

const transformFrontendShiftToApiShiftRequest = (frontendShiftData) => {
  return {
    user_id: frontendShiftData.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? API_UNASSIGNED_USER_ID : frontendShiftData.userId,
    date: frontendShiftData.day,
    start_time: frontendShiftData.startTime,
    end_time: frontendShiftData.endTime,
    note: frontendShiftData.note,
    slots: frontendShiftData.slots,
  };
};

const transformFrontendShiftToApiUpdateShiftRequest = (frontendShiftId, newValues) => {
  const payload = { id: frontendShiftId };
  if (newValues.userId !== undefined) payload.user_id = newValues.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? API_UNASSIGNED_USER_ID : newValues.userId;
  if (newValues.day !== undefined) payload.date = newValues.day;
  if (newValues.startTime !== undefined) payload.start_time = newValues.startTime;
  if (newValues.endTime !== undefined) payload.end_time = newValues.endTime;
  if (newValues.note !== undefined) payload.note = newValues.note;
  if (newValues.slots !== undefined) payload.slots = newValues.slots;
  return payload;
};

export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date(new Date('2025-05-19').setHours(0,0,0,0)));
  const [shifts, setShifts] = useState([]);
  const [rawUsers, setRawUsers] = useState([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  const [isLoadingShifts, setIsLoadingShifts] = useState(true); // For full data refetches
  const [initialLoadComplete, setInitialLoadComplete] = useState(false);
  const [isPerformingApiAction, setIsPerformingApiAction] = useState(false); // For disabling buttons during any API call


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

  const [isDeleteSingleModalOpen, setIsDeleteSingleModalOpen] = useState(false);
  const [shiftIdToDelete, setShiftIdToDelete] = useState(null);
  const [isDeleteSelectedModalOpen, setIsDeleteSelectedModalOpen] = useState(false);

  const [schedulingConflicts, setSchedulingConflicts] = useState([]);
  const [isConflictModalOpen, setIsConflictModalOpen] = useState(false);
  const [pendingShiftOperation, setPendingShiftOperation] = useState(null);

  const [isFilterDrawerOpen, setFilterDrawerOpen] = useState(false);
  const [nameFilterInput, setNameFilterInput] = useState("");
  const [departmentFilterInput, setDepartmentFilterInput] = useState(null);
  const [jobPositionFilterInput, setJobPositionFilterInput] = useState(null);

  const [appliedNameFilter, setAppliedNameFilter] = useState("");
  const [appliedDepartmentFilter, setAppliedDepartmentFilter] = useState(null);
  const [appliedJobPositionFilter, setAppliedJobPositionFilter] = useState(null);

  const [departmentOptions, setDepartmentOptions] = useState([]);
  const [jobPositionOptions, setJobPositionOptions] = useState([]);


  const isAnyShiftSelected = selectedShiftIds.length > 0;

  const refetchCurrentWeekShifts = useCallback(async (isInitialGlobalLoad = false) => {
    setIsLoadingShifts(true); // Use this for full refetch loading state
    const weekStart = format(startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON }), 'yyyy-MM-dd');
    const weekEnd = format(endOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON }), 'yyyy-MM-dd');
    const userIdsToFetch = rawUsers.map(u => u.id).filter(id => id !== FRONTEND_UNASSIGNED_SHIFT_USER_ID);

    try {
      const absencesData = await fetchAbsences(userIdsToFetch, weekStart, weekEnd);
      let regularAndUnassignedShiftsData = [];

      const shiftParams = { startDate: weekStart, endDate: weekEnd };
      if (userIdsToFetch.length > 0) {
        shiftParams.userIds = userIdsToFetch.join(',');
      }

      await request(
        "get", `/shifts`,
        (res) => { regularAndUnassignedShiftsData = res.data?.data || []; },
        { onError: (err) => {
            console.error("Failed to fetch shifts:", err.response?.data || err.message);
            regularAndUnassignedShiftsData = [];
          }},
        null, { params: shiftParams }
      );

      const timeOffShifts = absencesData.map(absence => ({
        id: absence.id,
        userId: absence.user_id,
        day: format(parseISO(absence.date), 'yyyy-MM-dd'),
        startTime: absence.start_time.substring(0, 5),
        endTime: absence.end_time.substring(0, 5),
        note: absence.reason || 'Nghỉ phép',
        type: 'time_off',
        duration: calculateDuration(absence.date, absence.start_time.substring(0,5), absence.end_time.substring(0,5))
      }));

      const fetchedRegularShifts = regularAndUnassignedShiftsData.map(transformApiShiftToFrontend).filter(s => s);
      setShifts([...fetchedRegularShifts, ...timeOffShifts]);

    } catch (error) {
      console.error("Error fetching week data (absences or shifts):", error.response?.data || error.message);
    } finally {
      setIsLoadingShifts(false); // End full refetch loading state
      if (isInitialGlobalLoad) setInitialLoadComplete(true);
    }
  }, [currentDate, rawUsers, isLoadingUsers]); // Removed isLoadingUsers to prevent potential loop if it changes during refetch


  useEffect(() => {
    const loadUsers = async () => {
      setIsLoadingUsers(true);
      request(
        "get", "/staffs/details",
        (res) => {
          const fetchedUsers = (res.data.data || []).map(staff => ({
            id: staff.user_login_id, name: staff.fullname,
            departmentCode: staff.department?.department_code || 'UNKNOWN_DEPT',
            departmentName: staff.department?.department_name || 'Không có phòng ban',
            jobPositionName: staff.job_position?.job_position_name || 'Không có chức vụ'
          }));
          setRawUsers(fetchedUsers);
        },
        { onError: (err) => { console.error("Failed to fetch users:", err.response?.data || err.message); setRawUsers([]); }},
        null, { params: { status: "ACTIVE" } }
      ).finally(() => setIsLoadingUsers(false));
    };
    loadUsers();
  }, []);

  useEffect(() => {
    // Only refetch if users are loaded OR if it's not the initial phase where users might still be loading
    if (!isLoadingUsers) {
      if (!initialLoadComplete) {
        refetchCurrentWeekShifts(true);
      } else {
        refetchCurrentWeekShifts(false);
      }
    }
  }, [currentDate, isLoadingUsers, initialLoadComplete]); // refetchCurrentWeekShifts removed from deps here as it depends on isLoadingUsers

  useEffect(() => {
    if (rawUsers.length > 0) {
      const uniqueDepartments = rawUsers.reduce((acc, user) => {
        if (user.departmentCode && user.departmentName && !acc.find(d => d.code === user.departmentCode)) {
          acc.push({ code: user.departmentCode, name: user.departmentName });
        }
        return acc;
      }, []);
      setDepartmentOptions(uniqueDepartments.sort((a, b) => a.name.localeCompare(b.name)));
      const uniqueJobPositions = rawUsers.reduce((acc, user) => {
        if (user.jobPositionName && !acc.find(jp => jp.name === user.jobPositionName)) {
          acc.push({ code: user.jobPositionName, name: user.jobPositionName });
        }
        return acc;
      }, []);
      setJobPositionOptions(uniqueJobPositions.sort((a,b) => a.name.localeCompare(b.name)));
    }
  }, [rawUsers]);

  const allUsers = useMemo(() => {
    let filteredUsers = rawUsers;
    if (appliedNameFilter) filteredUsers = filteredUsers.filter(user => user.name.toLowerCase().includes(appliedNameFilter.toLowerCase()));
    if (appliedDepartmentFilter) filteredUsers = filteredUsers.filter(user => user.departmentCode === appliedDepartmentFilter.code);
    if (appliedJobPositionFilter) filteredUsers = filteredUsers.filter(user => user.jobPositionName === appliedJobPositionFilter.name);
    return filteredUsers.map(user => ({ ...user, workHoursSummary: calculateUserWorkHours(user.id, shifts) }));
  }, [rawUsers, shifts, appliedNameFilter, appliedDepartmentFilter, appliedJobPositionFilter]);

  const groupedUsersForGrid = useMemo(() => {
    if (allUsers.length === 0 && !isLoadingUsers) return []; // Return empty if no users after filtering and not loading
    if (isLoadingUsers) return []; // Still loading users, don't compute yet

    const groups = {};
    allUsers.forEach(user => {
      const deptKey = user.departmentCode || 'UNKNOWN_DEPT';
      if (!groups[deptKey]) {
        groups[deptKey] = { departmentCode: user.departmentCode, departmentName: user.departmentName || 'Không có phòng ban', users: [] };
      }
      groups[deptKey].users.push(user);
    });
    return Object.values(groups)
      .sort((a, b) => a.departmentName.localeCompare(b.departmentName))
      .map(group => { group.users.sort((a, b) => a.name.localeCompare(b.name)); return group; });
  }, [allUsers, isLoadingUsers]);

  const fetchAbsences = async (userIds, start, end) => {
    if (!userIds || userIds.length === 0) return [];
    return new Promise((resolve) => {
      request(
        "get", `/absences`,
        (res) => resolve(res.data?.data || []),
        { onError: (err) => { console.error("Failed to fetch absences:", err.response?.data || err.message); resolve([]); }},
        null, { params: { userIds: userIds.join(','), startDate: start, endDate: end } }
      );
    });
  };

  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date(new Date().setHours(0,0,0,0)));

  const handleOpenModal = (userIdForPreselection, day, shiftToEdit = null) => {
    let context = null;
    if (shiftToEdit) {
      setCurrentEditingShift(shiftToEdit);
      context = shiftToEdit.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? 'editUnassigned' : 'editUser';
      setModalInitialFormState({
        userIds: shiftToEdit.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? [] : [shiftToEdit.userId],
        day: shiftToEdit.day, startTime: shiftToEdit.startTime, endTime: shiftToEdit.endTime,
        note: shiftToEdit.note || '', slots: shiftToEdit.slots !== undefined ? shiftToEdit.slots : 1,
      });
    } else {
      setCurrentEditingShift(null);
      context = userIdForPreselection === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? 'newUnassigned' : 'newUser';
      setModalInitialFormState({
        userIds: context === 'newUnassigned' ? [] : (userIdForPreselection ? [userIdForPreselection] : []),
        day: format(day || new Date(), 'yyyy-MM-dd'), startTime: '09:00', endTime: '17:00',
        note: '', slots: context === 'newUnassigned' ? 1 : 1,
      });
    }
    setModalOpeningContext(context); setIsModalOpen(true);
  };

  const handleCloseModal = () => { setIsModalOpen(false); setCurrentEditingShift(null); setModalOpeningContext(null); };

  const proceedWithSaveShift = async (formDataToSave, editingShiftOriginalId) => {
    const { userIds: rawUserIdsForm, day, startTime, endTime, note, slots: formSlotsStr, _initiatedAsNewUnassignedContext } = formDataToSave;
    const formUserIds = rawUserIdsForm || [];
    const formSlots = formSlotsStr !== undefined && formSlotsStr !== null ? parseInt(formSlotsStr, 10) : undefined;

    let shiftsToCreateApiPayload = [];
    let shiftToUpdateApiPayload = null;
    let idToDeleteOnSuccess = null;
    let performedCreateOperation = false;

    const commonShiftData = { day, startTime, endTime, note };
    setIsPerformingApiAction(true);

    try {
      if (_initiatedAsNewUnassignedContext) {
        performedCreateOperation = true;
        const assignedUsers = formUserIds.filter(uid => uid !== FRONTEND_UNASSIGNED_SHIFT_USER_ID);
        let totalInitialSlots = formSlots !== undefined ? formSlots : 1;
        if (isNaN(totalInitialSlots) || totalInitialSlots < 0) totalInitialSlots = 1;
        assignedUsers.forEach(uid => shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: uid, slots: undefined })));
        const remainingSlots = totalInitialSlots - assignedUsers.length;
        if (remainingSlots > 0) shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: FRONTEND_UNASSIGNED_SHIFT_USER_ID, slots: remainingSlots }));
      } else if (editingShiftOriginalId && currentEditingShift) {
        if (currentEditingShift.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID) {
          const assignedUsers = formUserIds.filter(uid => uid !== FRONTEND_UNASSIGNED_SHIFT_USER_ID);
          if (assignedUsers.length > 0) performedCreateOperation = true;
          let currentTemplateActualSlots = currentEditingShift.slots || 0;
          let newTotalSlotsForTemplateDefinition = formSlots !== undefined ? formSlots : currentTemplateActualSlots;
          if (isNaN(newTotalSlotsForTemplateDefinition) || newTotalSlotsForTemplateDefinition < 0) newTotalSlotsForTemplateDefinition = currentTemplateActualSlots;
          assignedUsers.forEach(uid => shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: uid, slots: undefined })));
          const finalTemplateSlots = formSlots !== undefined ? newTotalSlotsForTemplateDefinition - assignedUsers.length : currentTemplateActualSlots - assignedUsers.length;
          if (finalTemplateSlots > 0) shiftToUpdateApiPayload = transformFrontendShiftToApiUpdateShiftRequest(editingShiftOriginalId, { ...commonShiftData, userId: FRONTEND_UNASSIGNED_SHIFT_USER_ID, slots: finalTemplateSlots });
          else idToDeleteOnSuccess = editingShiftOriginalId;
        } else {
          if (formUserIds.length === 1 && formUserIds[0] === currentEditingShift.userId) {
            shiftToUpdateApiPayload = transformFrontendShiftToApiUpdateShiftRequest(editingShiftOriginalId, { ...commonShiftData, userId: currentEditingShift.userId });
          } else {
            performedCreateOperation = true;
            idToDeleteOnSuccess = editingShiftOriginalId;
            formUserIds.filter(uid => uid !== FRONTEND_UNASSIGNED_SHIFT_USER_ID).forEach(uid => shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: uid })));
            if (formUserIds.some(uid => uid === FRONTEND_UNASSIGNED_SHIFT_USER_ID) && formSlots > 0 && !isNaN(formSlots)) shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: FRONTEND_UNASSIGNED_SHIFT_USER_ID, slots: formSlots }));
          }
        }
      } else {
        performedCreateOperation = true;
        if (formUserIds.length === 0 && (formSlots === undefined || formSlots <= 0 || isNaN(formSlots))) { setIsPerformingApiAction(false); return; }
        formUserIds.filter(uid => uid !== FRONTEND_UNASSIGNED_SHIFT_USER_ID).forEach(uid => shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: uid })));
        if (formUserIds.some(uid => uid === FRONTEND_UNASSIGNED_SHIFT_USER_ID) && formSlots > 0 && !isNaN(formSlots)) shiftsToCreateApiPayload.push(transformFrontendShiftToApiShiftRequest({ ...commonShiftData, userId: FRONTEND_UNASSIGNED_SHIFT_USER_ID, slots: formSlots }));
      }

      const promises = [];
      // IMPORTANT: Order matters if deleting and then creating with potentially similar attributes for a different ID
      if (idToDeleteOnSuccess) {
        promises.push(request("delete", `/shifts`, null, {onError: (e) => {throw e}}, null, {params: {ids: idToDeleteOnSuccess}}));
      }
      // Perform update before create if it's the same original shift being modified vs. new ones from it
      if (shiftToUpdateApiPayload) {
        promises.push(request("put", `/shifts/${shiftToUpdateApiPayload.id}`, null, {onError: (e) => {throw e}}, shiftToUpdateApiPayload));
      }
      if (shiftsToCreateApiPayload.length > 0) {
        promises.push(request("post", "/shifts", null, {onError: (e) => {throw e}}, { shifts: shiftsToCreateApiPayload }));
      }

      await Promise.all(promises);

      if (performedCreateOperation) {
        await refetchCurrentWeekShifts();
      } else { // Pure update or pure delete of template
        if (shiftToUpdateApiPayload && editingShiftOriginalId) {
          setShifts(prevShifts => prevShifts.map(s =>
            s.id === editingShiftOriginalId
              ? { ...s, ...commonShiftData,
                userId: shiftToUpdateApiPayload.user_id === API_UNASSIGNED_USER_ID ? FRONTEND_UNASSIGNED_SHIFT_USER_ID : shiftToUpdateApiPayload.user_id,
                slots: shiftToUpdateApiPayload.slots,
                duration: calculateDuration(commonShiftData.day, commonShiftData.startTime, commonShiftData.endTime)
              }
              : s ));
        } else if (idToDeleteOnSuccess && !shiftToUpdateApiPayload) { // Was a delete without an update (e.g. template slots to 0)
          setShifts(prevShifts => prevShifts.filter(s => s.id !== idToDeleteOnSuccess));
        }
      }
      // toast.success("Lưu ca thành công!");
      handleCloseModal();

    } catch (error) {
      console.error("Error saving shift(s):", error.response?.data || error.message);
      // toast.error(`Lỗi khi lưu ca: ${error.response?.data?.message || 'Vui lòng thử lại.'}`);
      await refetchCurrentWeekShifts();
    } finally {
      setIsPerformingApiAction(false);
    }
  };

  const handleSaveShift = (formData) => {
    const { userIds: rawUserIdsForm, day, startTime, endTime, _initiatedAsNewUnassignedContext } = formData;
    const formUserIds = rawUserIdsForm || [];
    const actualUserIdsToAssign = formUserIds.filter(uid => uid !== FRONTEND_UNASSIGNED_SHIFT_USER_ID && rawUsers.find(u => u.id === uid));
    let editingId = currentEditingShift ? currentEditingShift.id : null;
    let allDetectedConflicts = [];

    if (_initiatedAsNewUnassignedContext && actualUserIdsToAssign.length > 0) {
      actualUserIdsToAssign.forEach(userId => allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, rawUsers, null)));
    } else if (currentEditingShift && currentEditingShift.userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID && currentEditingShift.type !== 'time_off') {
      actualUserIdsToAssign.forEach(userId => {
        const idToExclude = userId === currentEditingShift.userId ? editingId : null;
        allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, rawUsers, idToExclude));
      });
    } else if (currentEditingShift && currentEditingShift.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID && actualUserIdsToAssign.length > 0) {
      actualUserIdsToAssign.forEach(userId => allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, rawUsers, null)));
    } else if (!currentEditingShift && actualUserIdsToAssign.length > 0 && !_initiatedAsNewUnassignedContext) {
      actualUserIdsToAssign.forEach(userId => allDetectedConflicts.push(...detectConflicts({ day, startTime, endTime }, shifts, userId, rawUsers, null)));
    }

    allDetectedConflicts = allDetectedConflicts.filter((value, index, self) => index === self.findIndex((t) => ( t.shiftId === value.shiftId && t.message === value.message )));

    if (allDetectedConflicts.length > 0) {
      setSchedulingConflicts(allDetectedConflicts);
      setPendingShiftOperation({ action: 'save', data: formData, editingShiftId: editingId });
      setIsConflictModalOpen(true); return;
    }
    proceedWithSaveShift(formData, editingId);
  }

  const handleDeleteSingleShift = (shiftId) => { setShiftIdToDelete(shiftId); setIsDeleteSingleModalOpen(true); };

  const confirmDeleteSingleShift = async () => {
    if (shiftIdToDelete) {
      setIsPerformingApiAction(true);
      request(
        "delete", `/shifts`,
        () => {
          setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
          setSelectedShiftIds(prevSelected => prevSelected.filter(id => id !== shiftIdToDelete));
          setShiftIdToDelete(null);
          // toast.success("Xóa ca thành công!");
        },
        { onError: async (err) => {
            console.error("Error deleting single shift:", err.response?.data || err.message);
            // toast.error("Lỗi khi xóa ca.");
            await refetchCurrentWeekShifts();
          }},
        null, { params: { ids: shiftIdToDelete } }
      ).finally(() => { setIsPerformingApiAction(false); setIsDeleteSingleModalOpen(false); });
    }
  };

  const handleDeleteSelectedShifts = useCallback(() => {
    if (!isAnyShiftSelected || selectedShiftIds.length === 0) return;
    setIsDeleteSelectedModalOpen(true);
  }, [selectedShiftIds, isAnyShiftSelected]);

  const confirmDeleteSelectedShifts = async () => {
    if (selectedShiftIds.length === 0) return;
    setIsPerformingApiAction(true);
    request(
      "delete", `/shifts`,
      () => {
        setShifts(prevShifts => prevShifts.filter(shift => !selectedShiftIds.includes(shift.id)));
        setSelectedShiftIds([]);
        // toast.success(`Đã xóa ${selectedShiftIds.length} ca thành công!`);
      },
      { onError: async (err) => {
          console.error("Error deleting selected shifts:", err.response?.data || err.message);
          // toast.error("Lỗi khi xóa các ca đã chọn.");
          await refetchCurrentWeekShifts();
        }},
      null, { params: { ids: selectedShiftIds.join(',') } }
    ).finally(() => { setIsPerformingApiAction(false); setIsDeleteSelectedModalOpen(false); });
  };

  const proceedWithDragEnd = async (dragResult) => {
    const { source, destination, draggableId } = dragResult;
    const draggedShiftOriginal = shifts.find(shift => shift.id === draggableId);
    if (!draggedShiftOriginal || draggedShiftOriginal.type === 'time_off') return;

    const destParts = destination.droppableId.split('-');
    const newDestUserIdForFrontend = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;

    setIsPerformingApiAction(true);
    let requiresRefetchDueToCreate = false;
    let locallyUpdatedShift = null;

    try {
      const operations = [];
      if (draggedShiftOriginal.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID && newDestUserIdForFrontend !== FRONTEND_UNASSIGNED_SHIFT_USER_ID) {
        requiresRefetchDueToCreate = true;
        const createNewAssignedShiftReq = transformFrontendShiftToApiShiftRequest({ ...draggedShiftOriginal, userId: newDestUserIdForFrontend, day: newDestDayString, slots: undefined });
        operations.push(request("post", "/shifts", null, {onError: (e) => {throw e}}, { shifts: [createNewAssignedShiftReq] }));
        if (draggedShiftOriginal.slots && draggedShiftOriginal.slots > 1) {
          const updateUnassignedReq = transformFrontendShiftToApiUpdateShiftRequest(draggedShiftOriginal.id, { slots: draggedShiftOriginal.slots - 1 });
          operations.push(request("put", `/shifts/${draggedShiftOriginal.id}`, null, {onError: (e) => {throw e}}, updateUnassignedReq));
        } else {
          operations.push(request("delete", `/shifts`, null, {onError: (e) => {throw e}}, null, {params: {ids:draggedShiftOriginal.id}}));
        }
      } else if (draggedShiftOriginal.userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID && newDestUserIdForFrontend === FRONTEND_UNASSIGNED_SHIFT_USER_ID) {
        requiresRefetchDueToCreate = true;
        const createNewUnassignedShiftReq = transformFrontendShiftToApiShiftRequest({ ...draggedShiftOriginal, userId: FRONTEND_UNASSIGNED_SHIFT_USER_ID, day: newDestDayString, slots: 1 });
        operations.push(request("post", "/shifts", null, {onError: (e) => {throw e}}, { shifts: [createNewUnassignedShiftReq] }));
        operations.push(request("delete", `/shifts`, null, {onError: (e) => {throw e}}, null, {params: {ids:draggedShiftOriginal.id}}));
      } else if (draggedShiftOriginal.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID && newDestUserIdForFrontend === FRONTEND_UNASSIGNED_SHIFT_USER_ID) {
        const updateUnassignedReq = transformFrontendShiftToApiUpdateShiftRequest(draggedShiftOriginal.id, { day: newDestDayString });
        operations.push(request("put", `/shifts/${draggedShiftOriginal.id}`, null, {onError: (e) => {throw e}}, updateUnassignedReq));
        locallyUpdatedShift = { ...draggedShiftOriginal, day: newDestDayString, duration: calculateDuration(newDestDayString, draggedShiftOriginal.startTime, draggedShiftOriginal.endTime) };
      } else {
        const updateAssignedReq = transformFrontendShiftToApiUpdateShiftRequest(draggedShiftOriginal.id, { userId: newDestUserIdForFrontend, day: newDestDayString });
        operations.push(request("put", `/shifts/${draggedShiftOriginal.id}`, null, {onError: (e) => {throw e}}, updateAssignedReq));
        locallyUpdatedShift = { ...draggedShiftOriginal, userId: newDestUserIdForFrontend, day: newDestDayString, duration: calculateDuration(newDestDayString, draggedShiftOriginal.startTime, draggedShiftOriginal.endTime) };
      }

      await Promise.all(operations);

      if (requiresRefetchDueToCreate) {
        await refetchCurrentWeekShifts();
      } else if (locallyUpdatedShift) {
        setShifts(prevShifts => prevShifts.map(s => s.id === locallyUpdatedShift.id ? locallyUpdatedShift : s));
      }
      // toast.success("Di chuyển ca thành công!");
    } catch (error) {
      console.error("Error during drag and drop:", error.response?.data || error.message);
      // toast.error("Lỗi khi di chuyển ca.");
      await refetchCurrentWeekShifts();
    } finally {
      setIsPerformingApiAction(false);
    }
  };

  const onDragEnd = (result) => {
    const { source, destination, draggableId } = result;
    if (!destination || !destination.droppableId || (source.droppableId === destination.droppableId && source.index === destination.index)) return;
    const draggedShift = shifts.find(shift => shift.id === draggableId);
    if (!draggedShift || draggedShift.type === 'time_off') return;
    const destParts = destination.droppableId.split('-');
    if (destParts.length < 6 || destParts[0] !== 'user') return;
    const newDestUserId = destParts[1];
    const newDestDayString = `${destParts[3]}-${destParts[4]}-${destParts[5]}`;
    let potentialConflictUserId = null;
    let shiftDetailsForCheck = { day: newDestDayString, startTime: draggedShift.startTime, endTime: draggedShift.endTime };
    if (newDestUserId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID) potentialConflictUserId = newDestUserId;
    if (potentialConflictUserId) {
      let idToExcludeForConflictCheck = null;
      if (draggedShift.userId === potentialConflictUserId && draggedShift.day === newDestDayString) idToExcludeForConflictCheck = draggedShift.id;
      const detectedConflicts = detectConflicts(shiftDetailsForCheck, shifts, potentialConflictUserId, rawUsers, idToExcludeForConflictCheck);
      if (detectedConflicts.length > 0) {
        setSchedulingConflicts(detectedConflicts);
        setPendingShiftOperation({ action: 'drag', data: result });
        setIsConflictModalOpen(true); return;
      }
    }
    proceedWithDragEnd(result);
  };

  const handleConflictModalClose = () => { setIsConflictModalOpen(false); setSchedulingConflicts([]); setPendingShiftOperation(null); };
  const handleConflictModalConfirm = () => {
    if (pendingShiftOperation) {
      if (pendingShiftOperation.action === 'save') proceedWithSaveShift(pendingShiftOperation.data, pendingShiftOperation.editingShiftId);
      else if (pendingShiftOperation.action === 'drag') proceedWithDragEnd(pendingShiftOperation.data);
    }
    handleConflictModalClose();
  };

  const handleToggleSelectShift = useCallback((shiftId) => {
    const shiftToToggle = shifts.find(s => s.id === shiftId);
    if (shiftToToggle && shiftToToggle.type === 'time_off') return;
    setSelectedShiftIds(prev => prev.includes(shiftId) ? prev.filter(id => id !== shiftId) : [...prev, shiftId]);
  }, [shifts]);
  const handleDeselectAll = useCallback(() => { setSelectedShiftIds([]); }, []);
  const getAllShiftIdsInCurrentView = useCallback(() => {
    const weekStartFilter = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
    const weekEndRange = addDays(weekStartFilter, 6);
    const ids = [];
    shifts.forEach(s => {
      if (s.type === 'time_off') return;
      if (isValid(parseISO(s.day))) {
        const d = parseISO(s.day);
        if (d >= weekStartFilter && d <= weekEndRange) {
          const userIsVisible = allUsers.find(u => u.id === s.userId);
          const isUnassigned = s.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID;
          if(userIsVisible || isUnassigned) ids.push(s.id);
        }
      }
    });
    return ids;
  }, [shifts, allUsers, currentDate]);

  const allShiftIdsInView = getAllShiftIdsInCurrentView();
  const selectedShiftsInViewCount = allShiftIdsInView.filter(id => selectedShiftIds.includes(id)).length;
  const isAllSelectedInView = allShiftIdsInView.length > 0 && selectedShiftsInViewCount === allShiftIdsInView.length;
  const isIndeterminateInView = selectedShiftsInViewCount > 0 && selectedShiftsInViewCount < allShiftIdsInView.length;

  const handleToggleSelectAllInView = useCallback(() => {
    const idsInView = getAllShiftIdsInCurrentView();
    if (isAllSelectedInView) setSelectedShiftIds(prev => prev.filter(id => !idsInView.includes(id)));
    else setSelectedShiftIds(prev => [...new Set([...prev, ...idsInView])]);
  }, [getAllShiftIdsInCurrentView, isAllSelectedInView]);

  const handleOpenCopyModal = () => { if (!isAnyShiftSelected) return; setIsCopyModalOpen(true); };
  const handleCloseCopyModal = () => { setIsCopyModalOpen(false); };

  const handleConfirmCopyToWeeks = useCallback(async (targetWeekStartDates) => {
    if (!isAnyShiftSelected || targetWeekStartDates.length === 0) return;
    const toCopy = shifts.filter(s => selectedShiftIds.includes(s.id) && s.type !== 'time_off');
    let newShiftApiRequests = [];
    targetWeekStartDates.forEach(weekStart => {
      toCopy.forEach(s => {
        const origDate = parseISO(s.day);
        if (!isValid(origDate)) return;
        let dayIdx = getDay(origDate);
        if (WEEK_STARTS_ON === 1) dayIdx = (dayIdx === 0) ? 6 : dayIdx - 1;
        const newDate = addDays(weekStart, dayIdx);
        const newDayString = format(newDate, 'yyyy-MM-dd');
        newShiftApiRequests.push(transformFrontendShiftToApiShiftRequest({
          userId: s.userId, day: newDayString, startTime: s.startTime, endTime: s.endTime,
          note: s.note, slots: s.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? s.slots : undefined,
        }));
      });
    });

    if (newShiftApiRequests.length > 0) {
      setIsPerformingApiAction(true);
      request(
        "post", "/shifts",
        async () => {
          await refetchCurrentWeekShifts(); // <<< Refetch needed
          setSelectedShiftIds([]);
          handleCloseCopyModal();
          // toast.success("Sao chép ca thành công!");
        },
        { onError: async (err) => {
            console.error("Error copying shifts:", err.response?.data || err.message);
            // toast.error("Lỗi khi sao chép ca.");
            await refetchCurrentWeekShifts();
          }},
        { shifts: newShiftApiRequests }
      ).finally(() => setIsPerformingApiAction(false));
    } else { handleCloseCopyModal(); }
  }, [selectedShiftIds, shifts, isAnyShiftSelected, refetchCurrentWeekShifts]); // Added refetchCurrentWeekShifts to dep array

  const handleOpenFilterDrawer = () => { setNameFilterInput(appliedNameFilter); setDepartmentFilterInput(appliedDepartmentFilter); setJobPositionFilterInput(appliedJobPositionFilter); setFilterDrawerOpen(true); };
  const handleCloseFilterDrawer = () => setFilterDrawerOpen(false);
  const handleApplyFiltersFromDrawer = () => { setAppliedNameFilter(nameFilterInput); setAppliedDepartmentFilter(departmentFilterInput); setAppliedJobPositionFilter(jobPositionFilterInput); handleCloseFilterDrawer(); };
  const handleClearFiltersFromDrawer = () => { setNameFilterInput(""); setDepartmentFilterInput(null); setJobPositionFilterInput(null); setAppliedNameFilter(""); setAppliedDepartmentFilter(null); setAppliedJobPositionFilter(null); };

  const dynamicStickyOffset = isAnyShiftSelected ? BULK_ACTIONS_BAR_HEIGHT : 0;
  const PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER = 24;
  const FIXED_FOOTER_RESERVED_SPACE = 40;
  const paperContentMaxHeight = `calc(100vh - ${TOP_BAR_HEIGHT}px - ${dynamicStickyOffset}px - ${INFO_BANNERS_TOTAL_HEIGHT}px - ${PADDING_AND_MARGIN_AROUND_SCROLLABLE_PAPER}px - ${FIXED_FOOTER_RESERVED_SPACE}px)`;

  // Adjusted mainContentLoading: do not consider isPerformingApiAction here
  // as it would hide the grid during optimistic updates.
  // isLoadingShifts will cover full refetches.
  const mainContentLoading = isLoadingUsers || isLoadingShifts;


  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <DragDropContext onDragEnd={onDragEnd}>
        <Container maxWidth={false} disableGutters sx={{ bgcolor: 'grey.200', minHeight: '100vh' }}>
          <TopBar
            currentDate={currentDate} onPrevWeek={handlePrevWeek} onNextWeek={handleNextWeek}
            onToday={handleToday} onOpenFilterDrawer={handleOpenFilterDrawer}
            isFilterApplied={!!(appliedNameFilter || appliedDepartmentFilter || appliedJobPositionFilter)}
            isLoading={isPerformingApiAction} // Pass this to TopBar for its own loading indicator if needed
          />
          <BulkActionsBar
            selectedCount={selectedShiftIds.length} onDeleteSelected={handleDeleteSelectedShifts}
            onOpenCopyModal={handleOpenCopyModal} onDeselectAll={handleDeselectAll}
          />
          <UserFilterDrawer
            open={isFilterDrawerOpen} onClose={handleCloseFilterDrawer}
            nameFilterValue={nameFilterInput} onNameFilterChange={(e) => setNameFilterInput(e.target.value)}
            departmentFilterValue={departmentFilterInput} onDepartmentFilterChange={(event, newValue) => setDepartmentFilterInput(newValue)}
            jobPositionFilterValue={jobPositionFilterInput} onJobPositionFilterChange={(event, newValue) => setJobPositionFilterInput(newValue)}
            departmentOptions={departmentOptions} jobPositionOptions={jobPositionOptions}
            onApply={handleApplyFiltersFromDrawer} onClear={handleClearFiltersFromDrawer}
          />

          <Box sx={{px: {xs: 0, sm:1, md:2}, py:1}}>
            <InfoBanners currentDate={currentDate} stickyTopOffset={dynamicStickyOffset} />
            <Paper elevation={2} sx={{ mt:1, overflowY: 'auto', maxHeight: paperContentMaxHeight }}>
              <CalendarHeader currentDate={currentDate} onToggleSelectAll={handleToggleSelectAllInView} isAllSelectedInView={isAllSelectedInView} isIndeterminateInView={isIndeterminateInView} stickyTopOffset={0} />

              {(!isLoadingUsers && !initialLoadComplete && isLoadingShifts) ? null : (
                !isLoadingUsers && !isLoadingShifts &&
                <UnassignedShiftsRow
                  currentDate={currentDate} shifts={shifts.filter(s => s.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID)}
                  onAddShift={(userId, day) => handleOpenModal(FRONTEND_UNASSIGNED_SHIFT_USER_ID, day)}
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
                      <CircularProgress /> <Typography sx={{ml: 2}}>Đang tải dữ liệu...</Typography>
                    </Box>
                  ) : groupedUsersForGrid.length > 0 || shifts.some(s => s.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID) ? (
                    <ShiftsGrid
                      currentDate={currentDate} shifts={shifts.filter(s => s.userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID)}
                      groupedUsers={groupedUsersForGrid}
                      onAddShift={(userId, day) => handleOpenModal(userId, day)}
                      onEditShift={(shift) => { if (shift.type === 'time_off') return; handleOpenModal(shift.userId, parseISO(shift.day), shift); }}
                      selectedShiftIds={selectedShiftIds} onToggleSelectShift={handleToggleSelectShift}
                      isAnyShiftSelected={isAnyShiftSelected}
                    />
                  ) : (
                    <Box display="flex" justifyContent="center" alignItems="center" sx={{ height: 100, p:2, flexDirection: 'column' }}>
                      <Typography color="text.secondary">
                        { (rawUsers.length > 0 && allUsers.length === 0) ? "Không có nhân viên nào khớp với bộ lọc."
                          : (rawUsers.length === 0 && !isLoadingUsers ? "Không có nhân viên nào trong hệ thống." : "") }
                      </Typography>
                      { (appliedNameFilter || appliedDepartmentFilter || appliedJobPositionFilter) && allUsers.length === 0 && rawUsers.length > 0 &&
                        <Button variant="text" size="small" onClick={handleClearFiltersFromDrawer} sx={{mt:1, textTransform:'none'}}>Xóa bộ lọc?</Button> }
                    </Box>
                  )}
                </Box>
              </Box>
            </Paper>
          </Box>

          <ShiftModal
            isOpen={isModalOpen} onClose={handleCloseModal} onSave={handleSaveShift} users={rawUsers}
            isSaving={isPerformingApiAction}
            initialFormState={modalInitialFormState} isEditing={!!currentEditingShift && currentEditingShift.type !== 'time_off'}
            isUnassignedContext={modalOpeningContext === 'newUnassigned' || modalOpeningContext === 'editUnassigned'}
            unassignedShiftBeingEdited={currentEditingShift && currentEditingShift.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? currentEditingShift : null}
          />
          <CopyShiftsModal
            isOpen={isCopyModalOpen} onClose={handleCloseCopyModal} onConfirmCopy={handleConfirmCopyToWeeks}
            currentDate={currentDate} numSelectedShifts={selectedShiftIds.length}
          />
          <DeleteConfirmationModal open={isDeleteSingleModalOpen} onClose={() => { setIsDeleteSingleModalOpen(false); setShiftIdToDelete(null); }} onSubmit={confirmDeleteSingleShift} title="Xác nhận xóa ca" info="Bạn có chắc chắn muốn xóa ca làm việc này không?" cancelLabel="Hủy bỏ" confirmLabel="Xóa"/>
          <DeleteConfirmationModal open={isDeleteSelectedModalOpen} onClose={() => setIsDeleteSelectedModalOpen(false)} onSubmit={confirmDeleteSelectedShifts} title="Xác nhận xóa các ca đã chọn" info={`Bạn có chắc chắn muốn xóa ${selectedShiftIds.length} ca đã chọn không?`} cancelLabel="Hủy bỏ" confirmLabel="Xóa tất cả"/>
          <SchedulingConflictModal open={isConflictModalOpen} onClose={handleConflictModalClose} onConfirm={handleConflictModalConfirm} conflicts={schedulingConflicts}/>
        </Container>
      </DragDropContext>
    </LocalizationProvider>
  );
}
