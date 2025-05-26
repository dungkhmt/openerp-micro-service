import React, {useEffect, useMemo, useState, useCallback} from "react";
import {
  ThemeProvider,
  CssBaseline,
  Box,
  Grid,
  TextField,
  Typography,
  Paper,
  CircularProgress,
  Tooltip,
  IconButton,
  Chip,
  Menu,
  MenuItem as MuiMenuItem,
  Stack,
  Card,
  CardHeader,
  CardContent,
  Avatar,
} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { vi } from 'date-fns/locale';
import { startOfWeek, endOfWeek, format, isMonday, parseISO, isEqual, setHours, setMinutes, setSeconds, setMilliseconds, isFuture, isToday } from "date-fns"; // Thêm isFuture, isToday

import { theme } from './theme';
import {request}from "@/api";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal.jsx";
import Pagination from "@/components/item/Pagination";
import UpdateAnnounceAbsenceForm from "./UpdateAnnounceAbsenceForm";

import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import MoreVertIcon from "@mui/icons-material/MoreVert";
import EventNoteIcon from '@mui/icons-material/EventNote';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import DoNotDisturbOnOutlinedIcon from '@mui/icons-material/DoNotDisturbOnOutlined';
import toast from "react-hot-toast";
import EventBusyIcon from "@mui/icons-material/EventBusy";


const parseTimeStringToFixedDate = (timeString) => {
  if (!timeString || typeof timeString !== 'string') return null;
  const [hours, minutes] = timeString.split(':').map(Number);
  if (isNaN(hours) || isNaN(minutes)) return null;
  return setMilliseconds(setSeconds(setMinutes(setHours(new Date(2000, 0, 1), hours), minutes),0),0);
};


const AbsenceHistoryPageInternal = () => {
  const [selectedWeekStart, setSelectedWeekStart] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [absenceList, setAbsenceList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(6);

  const [deleteId, setDeleteId] = useState(null);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);

  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const [currentMenuAbsenceId, setCurrentMenuAbsenceId] = useState(null);

  const [openUpdateModal, setOpenUpdateModal] = useState(false);
  const [selectedAbsenceForUpdateId, setSelectedAbsenceForUpdateId] = useState(null);

  const [workTimes, setWorkTimes] = useState({
    startWork: null, endWork: null, lunchStart: null, lunchEnd: null,
  });

  useEffect(() => {
    let isMounted = true;
    request("get", "/configs?configGroup=COMPANY_CONFIGS", (res) => {
      if (!isMounted) return;
      const apiConfigs = res.data?.data || {};
      const newConfigs = {};
      Object.entries(apiConfigs).forEach(([k, v]) => (newConfigs[k] = v.config_value));
      setWorkTimes({
        startWork: parseTimeStringToFixedDate(newConfigs.START_WORK_TIME),
        endWork: parseTimeStringToFixedDate(newConfigs.END_WORK_TIME),
        lunchStart: parseTimeStringToFixedDate(newConfigs.START_LUNCH_TIME),
        lunchEnd: parseTimeStringToFixedDate(newConfigs.END_LUNCH_TIME),
      });
    }, { onError: (err) => {if (isMounted) console.error("Lỗi tải cấu hình công ty:", err);} });
    return () => {isMounted = false;}
  }, []);


  const getAbsenceSessionDisplay = useCallback((startTimeStr, endTimeStr) => {
    if (!workTimes.startWork || !workTimes.endWork || !startTimeStr || !endTimeStr) {
      return (startTimeStr && endTimeStr) ? `${startTimeStr.slice(0,5)} - ${endTimeStr.slice(0,5)}` : "Chưa xác định";
    }
    const absenceStart = parseTimeStringToFixedDate(startTimeStr);
    const absenceEnd = parseTimeStringToFixedDate(endTimeStr);
    if (!absenceStart || !absenceEnd) return `${startTimeStr.slice(0,5)} - ${endTimeStr.slice(0,5)}`;

    if (isEqual(absenceStart, workTimes.startWork) && isEqual(absenceEnd, workTimes.endWork)) return "Cả ngày";
    if (workTimes.lunchStart && isEqual(absenceStart, workTimes.startWork) && isEqual(absenceEnd, workTimes.lunchStart)) return "Buổi sáng";
    if (workTimes.lunchEnd && isEqual(absenceStart, workTimes.lunchEnd) && isEqual(absenceEnd, workTimes.endWork)) return "Buổi chiều";
    return `${format(absenceStart, "HH:mm")} - ${format(absenceEnd, "HH:mm")}`;
  }, [workTimes]);


  const fetchAbsences = useCallback(async () => {
    setLoading(true);
    const start = selectedWeekStart;
    const end = endOfWeek(selectedWeekStart, { weekStartsOn: 1 });

    try {
      await request(
        "get",
        `/absences/me?startDate=${format(start, "yyyy-MM-dd")}&endDate=${format(end, "yyyy-MM-dd")}`,
        (res) => {
          const dataFromApi = (res.data?.data || []).map(item => ({
            ...item,
            id: String(item.id || `__fallback_absence_${item.date}_${item.start_time}`),
            dateObject: parseISO(item.date),
            date_formatted: format(parseISO(item.date), "dd/MM/yyyy (EEEE)", {locale: vi}),
            displayTime: getAbsenceSessionDisplay(item.start_time, item.end_time)
          })).sort((a,b) => parseISO(b.date).getTime() - parseISO(a.date).getTime() || (b.start_time && a.start_time ? b.start_time.localeCompare(a.start_time) : 0) );
          setAbsenceList(dataFromApi);
        },
        { onError: (err) => { console.error("Lỗi tải lịch sử nghỉ phép:", err); toast.error("Không thể tải lịch sử nghỉ phép.");}}
      );
    } catch (error) {
      console.error("Ngoại lệ khi tải lịch sử nghỉ phép:", error);
      toast.error("Lỗi hệ thống khi tải lịch sử nghỉ phép.");
    } finally {
      setLoading(false);
    }
  }, [selectedWeekStart, getAbsenceSessionDisplay]);

  useEffect(() => {
    if (workTimes.startWork) {
      fetchAbsences();
    }
  }, [fetchAbsences, workTimes.startWork]);

  const handleMenuOpen = useCallback((event, absenceId) => {
    setMenuAnchorEl(event.currentTarget);
    setCurrentMenuAbsenceId(absenceId);
  }, []);

  const handleMenuClose = useCallback(() => {
    setMenuAnchorEl(null);
    setCurrentMenuAbsenceId(null);
  }, []);

  const handleEditFromMenu = useCallback(() => {
    if (currentMenuAbsenceId) {
      const absenceToEdit = absenceList.find(abs => abs.id === currentMenuAbsenceId);
      if (absenceToEdit && absenceToEdit.status === "ACTIVE") {
        setSelectedAbsenceForUpdateId(absenceToEdit.id);
        setOpenUpdateModal(true);
      } else if (absenceToEdit) {
        toast.error(`Không thể sửa yêu cầu ở trạng thái "${getStatusProps(absenceToEdit.status).label}".`);
      }
    }
    handleMenuClose();
  }, [currentMenuAbsenceId, absenceList]);

  const handleOpenDeleteModalFromMenu = useCallback(() => {
    if (currentMenuAbsenceId) {
      const absenceToDelete = absenceList.find(abs => abs.id === currentMenuAbsenceId);
      if (absenceToDelete && absenceToDelete.status === "ACTIVE") { // Chỉ cho phép hủy nếu trạng thái là ACTIVE
        setDeleteId(currentMenuAbsenceId);
        setConfirmDeleteOpen(true);
      } else if (absenceToDelete) {
        toast.error(`Không thể hủy yêu cầu ở trạng thái "${getStatusProps(absenceToDelete.status).label}".`);
      }
    }
    handleMenuClose();
  }, [currentMenuAbsenceId, absenceList]);


  const handleDeleteConfirmed = () => {
    if (!deleteId) return;
    setLoading(true);
    request( "delete", `/absences/${deleteId}`, () => {
        toast.success("Đã hủy yêu cầu nghỉ phép thành công.");
        fetchAbsences();
        setConfirmDeleteOpen(false); setDeleteId(null);
      }, { onError: (err) => {
          const errorMsg = err.response?.data?.message || err.response?.data?.data || "Không thể hủy yêu cầu nghỉ phép.";
          toast.error(errorMsg);
          setConfirmDeleteOpen(false); setDeleteId(null);
        }}
    ).finally(() => setLoading(false));
  };

  const pageCount = Math.ceil(absenceList.length / itemsPerPage);
  const currentCardData = useMemo(() => {
    const firstPageIndex = currentPage * itemsPerPage;
    const lastPageIndex = firstPageIndex + itemsPerPage;
    return absenceList.slice(firstPageIndex, lastPageIndex);
  }, [absenceList, currentPage, itemsPerPage]);

  const getStatusProps = (status) => {
    let defaultThemeColors = {
      success: { ultralight: '#e6f7f0', darker: theme.palette.success.dark, light: theme.palette.success.light },
      error: { ultralight: '#ffeeee', darker: theme.palette.error.dark, light: theme.palette.error.light },
      info: { lighter: '#e1f5fe' },
      grey600: theme.palette.grey[600],
      grey100: theme.palette.grey[100],
      grey400: theme.palette.grey[400],
    };

    switch (status) {
      case "ACTIVE":
        return { label: "Đã duyệt", colorChip: "success", variantChip: "filled", icon: <CheckCircleOutlineIcon sx={{ fontSize: '1rem' }}/>, borderColor: theme.palette.success.main, avatarBg: defaultThemeColors.success.light, cardBg: defaultThemeColors.success.ultralight, textColor: defaultThemeColors.success.darker };
      case "INACTIVE":
        return { label: "Đã hủy", colorChip: "default", variantChip: "outlined", icon: <DoNotDisturbOnOutlinedIcon sx={{ fontSize: '1rem' }}/>, borderColor: defaultThemeColors.grey600, avatarBg: defaultThemeColors.grey400, cardBg: defaultThemeColors.grey100, textColor: theme.palette.text.secondary }; // textColor đổi thành secondary cho dễ đọc hơn trên nền xám
      default:
        return { label: status || "Không xác định", colorChip: "default", variantChip: "outlined", icon: <HourglassEmptyOutlinedIcon sx={{ fontSize: '1rem' }}/>, borderColor: theme.palette.grey[500], avatarBg: theme.palette.grey[200], cardBg: theme.palette.background.paper, textColor: theme.palette.text.primary };
    }
  };


  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
      <Box sx={{ p: { xs: 2 } }}>
        <Paper sx={{ p: { xs: 2, md: 3 }, mb: 3 }}>
          <Grid container spacing={2} alignItems="center" justifyContent="space-between">
            <Grid item xs={12} sm={7} md={8} lg={9}>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: 600, display: 'flex', alignItems: 'center' }}>
                <EventNoteIcon sx={{mr:1, color: 'primary.main'}} /> Lịch sử Nghỉ phép Cá nhân
              </Typography>
            </Grid>
            <Grid item xs={12} sm={5} md={4} lg={3}>
              <DatePicker
                label="Chọn Tuần (Thứ 2)"
                value={selectedWeekStart}
                onChange={(date) => date && setSelectedWeekStart(startOfWeek(date, { weekStartsOn: 1 }))}
                shouldDisableDate={(date) => !isMonday(date)}
                renderInput={(params) => <TextField {...params} fullWidth size="small" />}
              />
            </Grid>
          </Grid>
        </Paper>

        {loading && currentCardData.length === 0 ? (
          <Box sx={{display: 'flex', justifyContent: 'center', py: 5}}><CircularProgress /><Typography sx={{ml:2}}>Đang tải...</Typography></Box>
        ) : !loading && absenceList.length === 0 ? (
          <Paper sx={{p:3, textAlign: 'center', mt:2}}>
            <Typography variant="h6" color="text.secondary">Không có lịch sử nghỉ phép nào trong tuần đã chọn.</Typography>
          </Paper>
        ) : (
          <Box sx={{ maxHeight: "calc(100vh - 280px)", overflowY: 'auto', pr:1, pb:4}} className="custom-scrollbar">
            <Grid container spacing={2.5}>
              {currentCardData.map((item) => {
                const statusProps = getStatusProps(item.status);
                const today = new Date();
                today.setHours(0,0,0,0);
                const canPerformActions = item.status === "ACTIVE" && (isFuture(item.dateObject) || isToday(item.dateObject));

                return (
                  <Grid item xs={12} sm={6} md={4} key={item.id}>
                    <Card sx={{
                      display: 'flex',
                      flexDirection: 'column',
                      height: '100%',
                      border: `2px solid ${statusProps.borderColor}`,
                      backgroundColor: statusProps.cardBg,
                      boxShadow: item.status === "ACTIVE" ? theme.shadows[8] : theme.shadows[2],
                      '&:hover': {boxShadow: (t) => t.shadows[item.status === "ACTIVE" ? 4 : 2]},
                      transition: 'box-shadow 0.3s, border-color 0.3s, background-color 0.3s',
                      opacity: item.status === "INACTIVE" ? 0.60 : 1,
                    }}>
                      <CardHeader
                        avatar={ <Avatar sx={{ bgcolor: statusProps.avatarBg, color: theme.palette.getContrastText(statusProps.avatarBg), width: 48, height: 48 }}>{<EventBusyIcon />}</Avatar> }
                        title={ <Typography variant="h6" fontWeight="600" noWrap color={statusProps.textColor}> {item.date_formatted} </Typography> }
                        subheader={ <Typography variant="body1" color={item.status === "INACTIVE" ? "text.disabled" : "text.secondary"}> {item.displayTime} </Typography> }
                        action={
                          canPerformActions && (
                            <IconButton
                              aria-label="tùy chọn"
                              onClick={(e) => handleMenuOpen(e, item.id)}
                              size="medium" // Tăng size IconButton
                              sx={{mr: -1, mt: -1 }} // Điều chỉnh vị trí nếu cần
                            >
                              <MoreVertIcon fontSize="medium"/> {/* Tăng size Icon */}
                            </IconButton>
                          )
                        }
                        sx={{pb:1, pt:1.5, px:2, borderBottom: `1px solid ${item.status === "ACTIVE" ? statusProps.borderColor : theme.palette.divider}` }}
                      />
                      <CardContent sx={{pt:1.5, flexGrow:1, px:2, pb: '16px !important'}}>
                        <Stack spacing={1.5}>
                          <Box>
                            <Typography variant="caption" color="text.secondary" display="block" sx={{fontWeight:500}}>Loại nghỉ:</Typography>
                            <Chip
                              label={item.type === "PAID_LEAVE" ? "Có lương" : "Không lương"}
                              color={item.type === "PAID_LEAVE" ? "success" : "warning"}
                              size="small" sx={{fontSize: '0.8rem', height: '24px', borderRadius: '16px'}} // Bo tròn hơn
                              variant={item.status === "INACTIVE" ? "outlined" : "filled"}
                              disabled={item.status === "INACTIVE"}
                            />
                          </Box>
                          <Box>
                            <Typography variant="caption" color="text.secondary" display="block" sx={{fontWeight:500}}>Trạng thái:</Typography>
                            <Chip
                              label={statusProps.label}
                              color={statusProps.colorChip}
                              size="small" sx={{fontSize: '0.8rem', height: '24px', fontWeight: statusProps.variantChip === "filled" ? 600 : 500, borderRadius: '16px'}}
                              variant={statusProps.variantChip}
                              icon={React.cloneElement(statusProps.icon, { sx: { fontSize: '1rem', marginLeft: '5px'} })} // Style icon trong chip
                            />
                          </Box>
                          <Box>
                            <Typography variant="caption" color="text.secondary" display="block" sx={{fontWeight:500}}>Lý do:</Typography>
                            <Tooltip title={item.reason || "Không có lý do"}>
                              <Typography variant="body2" sx={{
                                display: '-webkit-box', WebkitBoxOrient: 'vertical', WebkitLineClamp: 3,
                                overflow: 'hidden', textOverflow: 'ellipsis', minHeight: 'calc(1.4em * 3)',
                                lineHeight: '1.4em',
                                color: item.status === "INACTIVE" ? "text.disabled" : statusProps.textColor
                              }}>
                                {item.reason || "Không có lý do"}
                              </Typography>
                            </Tooltip>
                          </Box>
                        </Stack>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          </Box>
        )}

        {pageCount > 1 && !loading && absenceList.length > 0 && (
          <Pagination
            currentPage={currentPage}
            pageCount={pageCount}
            itemsPerPage={itemsPerPage}
            onPageChange={setCurrentPage}
            onItemsPerPageChange={(size) => { setItemsPerPage(size); setCurrentPage(0);}}
          />
        )}

        <Menu
          anchorEl={menuAnchorEl}
          open={Boolean(menuAnchorEl)}
          onClose={handleMenuClose}
          PaperProps={{ elevation: 0, sx: { overflow: 'visible', filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))', mt: 1.5, '& .MuiAvatar-root': { width: 32, height: 32, ml: -0.5, mr: 1, }, '&::before': { content: '""', display: 'block', position: 'absolute', top: 0, right: 14, width: 10, height: 10, bgcolor: 'background.paper', transform: 'translateY(-50%) rotate(45deg)', zIndex: 0, }, }, }}
          transformOrigin={{horizontal: 'right', vertical: 'top'}}
          anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
        >
          <MuiMenuItem onClick={handleEditFromMenu} sx={{ gap: 1 }}> <EditIcon fontSize="small" /> Sửa </MuiMenuItem>
          <MuiMenuItem onClick={handleOpenDeleteModalFromMenu} sx={{ gap: 1, color: 'error.main' }}> <DeleteIcon fontSize="small" /> Hủy Yêu Cầu </MuiMenuItem>
        </Menu>

        <DeleteConfirmationModal
          open={confirmDeleteOpen}
          onClose={() => {setConfirmDeleteOpen(false); setDeleteId(null);}}
          onSubmit={handleDeleteConfirmed}
          title="Xác nhận Hủy Yêu Cầu Nghỉ Phép"
          info={(() => {
            const itemToDelete = absenceList.find(item => item.id === deleteId);
            return `Bạn có chắc chắn muốn hủy yêu cầu nghỉ ngày ${itemToDelete?.date_formatted || ''} (${itemToDelete?.displayTime || ''}) không?`;
          })()}
          confirmLabel="Hủy"
          cancelLabel="Không"
        />
        {openUpdateModal && selectedAbsenceForUpdateId && (
          <UpdateAnnounceAbsenceForm
            open={openUpdateModal}
            onClose={() => {setOpenUpdateModal(false); setSelectedAbsenceForUpdateId(null);}}
            absenceId={selectedAbsenceForUpdateId}
            onUpdated={() => {
              fetchAbsences();
              setOpenUpdateModal(false);
              setSelectedAbsenceForUpdateId(null);
            }}
          />
        )}
      </Box>
    </LocalizationProvider>
  );
};

const AbsenceHistoryPage = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AbsenceHistoryPageInternal />
    </ThemeProvider>
  );
};

export default AbsenceHistoryPage;
