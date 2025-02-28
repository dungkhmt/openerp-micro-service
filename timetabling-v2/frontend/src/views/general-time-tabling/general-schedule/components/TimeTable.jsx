import { useEffect, useState } from "react";
import { Checkbox } from "@mui/material";
import { useClassrooms } from "views/general-time-tabling/hooks/useClassrooms";
import { useGeneralSchedule } from "services/useGeneralScheduleData";
import { Autocomplete, Box, Button, CircularProgress, FormControl, Modal, TablePagination, TextField, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions } from "@mui/material";
import { Add, Remove } from "@mui/icons-material";
import {toast} from "react-toastify";

const TimeTable = ({
  classes,
  selectedSemester,
  selectedGroup,
  onSaveSuccess,
  loading,
  selectedRows,
  onSelectedRowsChange,
}) => {
  const [classDetails, setClassDetails] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(100);
  const [isAddSlotDialogOpen, setIsAddSlotDialogOpen] = useState(false);
  const [selectedPeriods, setSelectedPeriods] = useState("");
  const [selectedClassForSlot, setSelectedClassForSlot] = useState(null);

  const { classrooms } = useClassrooms(selectedGroup?.groupName || "", null);
  const { handlers, states } = useGeneralSchedule();

  useEffect(() => {
    if (classes && classes.length > 0) {
      const transformedClassDetails = classes
        .map((cls) => ({
          weekDay: cls.weekday,
          roomReservationId: cls.roomReservationId,
          code: cls.classCode,
          crew: cls.crew,
          batch: cls.course,
          room: cls.room,
          timetable: {
            [convertWeekdayToDay(cls.weekday)]: {
              id: cls.classCode,
              room: cls.room,
              startTime: cls.startTime,
              endTime: cls.endTime,
            },
          },
          studyClass: cls.studyClass,
          learningWeeks: cls.learningWeeks,
          moduleCode: cls.moduleCode,
          moduleName: cls.moduleName,
          quantityMax: cls.quantityMax,
          classType: cls.classType,
          mass: cls.mass,
          duration: cls.duration,
          generalClassId: String(cls.id || ""),
          parentId: cls.parentClassId,
        }))
        .sort((a, b) => {
          if (a.code === b.code) {
            // Safely handle splitting by checking if generalClassId exists and contains "-"
            const getIdNumber = (str) => {
              if (!str || !str.includes("-")) return parseInt(str, 10);
              return parseInt(str.split("-")[0], 10);
            };

            const idA = getIdNumber(a.generalClassId);
            const idB = getIdNumber(b.generalClassId);

            return idB - idA;
          }
          return parseInt(a.code, 10) - parseInt(b.code, 10);
        });
      setClassDetails(transformedClassDetails);
    }
  }, [classes]);

  const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  const periods = [1, 2, 3, 4, 5, 6];

  const convertWeekdayToDay = (weekday) => {
    const dayMap = {
      2: "Mon",
      3: "Tue",
      4: "Wed",
      5: "Thu",
      6: "Fri",
      7: "Sat",
    };
    return dayMap[weekday] || "";
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleSave = async () => {
    if (!selectedClass || !selectedSemester) return;

    const calculatedEndTime =
      selectedClass.startTime + selectedClass.numberOfPeriods - 1;
    const { numberOfPeriods, code, ...filteredSelectedClass } = selectedClass;

    const selectedClassData = {
      ...filteredSelectedClass,
      endTime: calculatedEndTime,
    };

    await handlers.handleSaveTimeSlot(selectedSemester.semester, selectedClassData);
    onSaveSuccess();
    setOpen(false);
  };

  const handleAddTimeSlot = async () => {
    try {
      if (!selectedClassForSlot || !selectedPeriods) return;

      const periodsToAdd = parseInt(selectedPeriods, 10);
      if (periodsToAdd > selectedClassForSlot.duration) {
        toast.error("Số tiết không được lớn hơn số tiết còn lại!");
        return;
      }

      console.log('Selected class:', selectedClassForSlot); 

      await handlers.handleAddTimeSlot({
        generalClassId: selectedClassForSlot.generalClassId,
        parentId: selectedClassForSlot.roomReservationId,
        duration: periodsToAdd,
      });
      
      handleCloseAddSlotDialog();
      onSaveSuccess();
    } catch (error) {
      console.error("Error adding time slot:", error);
      toast.error(error.response?.data || "Thêm ca học thất bại!"); // Add better error handling
    }
  };

  const handleRemoveTimeSlot = async (generalClassId, roomReservationId) => {
    try {
      if (!generalClassId || !roomReservationId) {
        throw new Error('Missing required parameters');
      }
      await handlers.handleRemoveTimeSlot({
        generalClassId: generalClassId.toString(),
        roomReservationId: roomReservationId
      });
      onSaveSuccess();
    } catch (error) {
      console.error("Error removing time slot:", error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    const newValue =
      name === "startTime" || name === "endTime" || name === "numberOfPeriods"
        ? Number(value)
        : value;

    setSelectedClass((prevClass) => ({
      ...prevClass,
      [name]: newValue,
    }));
  };

  const renderCellContent = (classIndex, day, period) => {
    const classInfo = classDetails[classIndex]?.timetable?.[day];

    if (!classInfo) {
      return (
        <td
          key={`${classIndex}-${day}-${period}`}
          style={{ width: "40px" }}
          className="border border-gray-300 text-center cursor-pointer px-1 "
          onClick={() => handleRowClick(classIndex, day, period)}
        ></td>
      );
    }

    if (period === classInfo.startTime) {
      const colSpan = classInfo.endTime - classInfo.startTime + 1;

      return (
        <td
          key={`${classIndex}-${day}-${period}`}
          colSpan={colSpan}
          className="border border-gray-300 bg-yellow-400 text-center cursor-pointer px-1"
          style={{ width: `${40 * colSpan}px` }}
          onClick={() => handleRowClick(classIndex, day, period)}
        >
          <span className="text-[14px]">{classInfo.room}</span>
        </td>
      );
    }

    if (period > classInfo.startTime && period <= classInfo.endTime) {
      return null;
    }

    return (
      <td
        key={`${classIndex}-${day}-${period}`}
        style={{ width: "40px" }}
        className="border border-gray-300 text-center cursor-pointer px-1"
        onClick={() => handleRowClick(classIndex, day, period)}
      ></td>
    );
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleRowClick = (classIndex, day, period) => {
    const classInfo = classDetails[classIndex]?.timetable?.[day];

    if (classInfo) {
      setSelectedClass({
        roomReservationId: Number(classDetails[classIndex].roomReservationId),
        room: classDetails[classIndex].room,
        startTime: period,
        endTime: Number(classInfo.endTime),
        weekday: Number(classDetails[classIndex].weekDay),
        code: classDetails[classIndex].code,
        numberOfPeriods: classDetails[classIndex].duration,
      });
    } else {
      setSelectedClass({
        roomReservationId: Number(classDetails[classIndex].roomReservationId),
        room: classDetails[classIndex].room,
        startTime: period,
        endTime: undefined,
        weekday: days.indexOf(day) + 2,
        code: classDetails[classIndex].code,
        numberOfPeriods: classDetails[classIndex].duration,
      });
    }
    setOpen(true);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleSelectAll = (event) => {
    if (event.target.checked) {
      const newSelected = classDetails.map(row => row.generalClassId);
      onSelectedRowsChange(newSelected);
    } else {
      onSelectedRowsChange([]);
    }
  };

  const handleSelectRow = (event, generalClassId) => {
    const selectedIndex = selectedRows.indexOf(generalClassId);
    let newSelected = [];

    if (selectedIndex === -1) {
      newSelected = [...selectedRows, generalClassId];
    } else {
      newSelected = selectedRows.filter(id => id !== generalClassId);
    }

    onSelectedRowsChange(newSelected);
  };

  const isSelected = (generalClassId) => selectedRows.indexOf(generalClassId) !== -1;

  const handleOpenAddSlotDialog = (classDetail) => {
    setSelectedClassForSlot(classDetail);
    setIsAddSlotDialogOpen(true);
  };

  const handleCloseAddSlotDialog = () => {
    setIsAddSlotDialogOpen(false);
    setSelectedPeriods("");
    setSelectedClassForSlot(null);
  };

  return (
    <div className="h-full w-full flex flex-col justify-start">
      {loading ? (
        <table
          className="overflow-x-auto flex items-center flex-col"
          style={{ flex: "1" }}
        >
          <thead>
            <tr>
              <th className="border border-gray-300 p-2">
                <Checkbox
                  indeterminate={
                    selectedRows.length > 0 &&
                    selectedRows.length < classDetails.length
                  }
                  checked={
                    classDetails.length > 0 &&
                    selectedRows.length === classDetails.length
                  }
                  onChange={handleSelectAll}
                />
              </th>
              <th className="border border-gray-300 p-2">Mã lớp</th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "60px", minWidth: "60px" }}
              >
                Lớp học
              </th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "52px", minWidth: "52px" }}
              >
                Tuần học
              </th>
              <th
                className="border border-gray-300 p-2"
              >
                Mã học phần
              </th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "120px", minWidth: "120px" }}
              >
                Tên học phần
              </th>
              <th className="border border-gray-300 p-2">SL MAX</th>
              <th className="border border-gray-300 p-2">Loại lớp</th>
              <th className="border border-gray-300 p-2">Thời lượng</th>
              <th className="border border-gray-300 p-2">Số tiết</th>
              <th className="border border-gray-300 p-2">Khóa</th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "40px", minWidth: "40px" }}
              >
                Thêm
              </th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "40px", minWidth: "40px" }}
              >
                Xóa
              </th>
              {days.map((day) => (
                <th
                  key={day}
                  colSpan={6}
                  className="border border-gray-300 p-2 text-center min-w-32"
                >
                  {day}
                </th>
              ))}
            </tr>
            <tr>
              <td></td> {/* Empty cell for checkbox column */}
              <td colSpan={14} className="border"></td>{" "}
              {/* Updated from 12 to 13 */}
              {days.flatMap((day) =>
                periods.map((period) => (
                  <td
                    key={`${day}-${period}`}
                    className="border border-gray-300 text-center"
                  >
                    {period}
                  </td>
                ))
              )}
            </tr>
          </thead>
          <div className="flex justify-center items-center h-full w-full ">
            <CircularProgress />
          </div>
        </table>
      ) : (
        <div className="overflow-x-auto" style={{ flex: "1" }}>
          <table className="min-w-full" style={{ tableLayout: "auto" }}>
            <thead>
              <tr>
                <th className="border border-t-0 border-l-0 p-2">
                  <Checkbox
                    indeterminate={
                      selectedRows.length > 0 &&
                      selectedRows.length < classDetails.length
                    }
                    checked={
                      classDetails.length > 0 &&
                      selectedRows.length === classDetails.length
                    }
                    onChange={handleSelectAll}
                  />
                </th>
                <th className="border border-t-0 border-l-0 p-2">Mã lớp</th>
                <th
                  className="border border-t-0  p-2"
                  style={{ width: "100px", minWidth: "100px" }}
                >
                  Lớp học
                </th>
                <th
                  className="border border-t-0  p-2"
                  style={{ width: "52px", minWidth: "52px" }}
                >
                  Tuần học
                </th>
                <th className="border border-t-0  p-2">Mã học phần</th>
                <th
                  className="border border-t-0  p-2"
                  style={{ width: "120px", minWidth: "120px" }}
                >
                  Tên học phần
                </th>
                <th className="border border-t-0 p-2">Kíp</th>
                <th className="border border-t-0 p-2">SL MAX</th>
                <th className="border border-t-0 p-2">Loại lớp</th>
                <th className="border border-t-0 p-2">Thời lượng</th>
                <th className="border border-t-0 p-2">Số tiết</th>
                <th className="border border-t-0 p-2">Khóa</th>
                <th
                  className="border border-t-0  p-2"
                  style={{ width: "40px", minWidth: "40px" }}
                >
                  Thêm
                </th>
                <th className="border border-t-0 p-2">Xóa</th>
                {days.map((day) => (
                  <th
                    key={day}
                    colSpan={6}
                    className="border border-t-0 p-2 text-center min-w-32"
                    style={{ padding: "8px 0" }}  // Add consistent padding
                  >
                    {day}
                  </th>
                ))}
              </tr>
              <tr>
                <td></td> {/* Empty cell for checkbox column */}
                <td colSpan={13} className="border"></td>{" "}
                {/* Period numbers row - Direct child of tr */}
                {days.flatMap((day) =>
                  periods.map((period) => (
                    <td
                      key={`${day}-${period}`}
                      className="border border-t-0 text-center"
                      style={{ width: "40px", padding: "4px" }}  // Fixed width and padding
                    >
                      {period}
                    </td>
                  ))
                )}
              </tr>
            </thead>
            <tbody>
              {classes && classes.length > 0 ? (
                classDetails
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((classDetail, index) => {
                    const isItemSelected = isSelected(
                      classDetail.generalClassId
                    );
                    return (
                      <tr
                        key={`${classDetail.code}-${index}`}
                        style={{ height: "52px" }}
                        className={isItemSelected ? "bg-blue-50" : ""}
                      >
                        <td className="border border-l-0 text-center px-1">
                          <Checkbox
                            checked={isItemSelected}
                            onChange={(event) =>
                              handleSelectRow(event, classDetail.generalClassId)
                            }
                          />
                        </td>
                        <td className="border text-center px-1">
                          {classDetail.code}
                        </td>
                        <td className="border  text-center px-1 w-[120px]">
                          {classDetail.studyClass}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.learningWeeks}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.moduleCode}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.moduleName}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.crew}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.quantityMax}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.classType}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.mass}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.duration}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.batch}
                        </td>
                        <td className="border  text-center px-1">
                          {!classDetail.timeSlots && (  // Only show Add button if no timeSlots
                            <Button
                              onClick={() => handleOpenAddSlotDialog(classDetail)}
                              disabled={classDetail.duration <= 0}
                              sx={{ 
                                minWidth: '32px', 
                                width: '32px', 
                                height: '32px', 
                                padding: '4px',
                                borderRadius: '4px'
                              }}
                              color="primary"
                              size="small"
                            >
                              <Add fontSize="small" />
                            </Button>
                          )}
                        </td>
                        <td className="border  text-center px-1">
                          {classDetail.roomReservationId && (  // Only show Remove if has roomReservationId
                            <Button
                              onClick={() =>
                                handleRemoveTimeSlot(
                                  classDetail.generalClassId,
                                  classDetail.roomReservationId
                                )
                              }
                              sx={{ 
                                minWidth: '32px', 
                                width: '32px', 
                                height: '32px', 
                                padding: '4px',
                                borderRadius: '4px'
                              }}
                              color="error"
                              size="small"
                            >
                              <Remove fontSize="small" />
                            </Button>
                          )}
                        </td>
                        {days.flatMap((day) =>
                          periods.map((period) =>
                            renderCellContent(index, day, period)
                          )
                        )}
                      </tr>
                    );
                  })
              ) : (
                <tr>
                  <td></td> {/* Empty cell for checkbox column */}
                  <td
                    colSpan={14 + days.length * periods.length}
                    className="text-center py-4"
                  >
                    <div className="h-full ">Không có dữ liệu</div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      <TablePagination
        className="border-y-[1px] border-solid border-gray-300"
        component="div"
        count={classDetails.length}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[25, 50, 100]}
        labelRowsPerPage="Rows per page:"
        labelDisplayedRows={({ from, to, count }) =>
          `${from}-${to} trên ${count}`
        }
      />

      <Modal
        open={open}
        disableEscapeKeyDown
        disableBackdropClick
        onClose={(_, reason) => {
          if (reason !== "backdropClick") {
            handleClose();
          }
        }}
        style={{ position: "fixed" }}
      >
        <Box
          onClick={(e) => e.stopPropagation()}
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 400,
            bgcolor: "background.paper",
            borderRadius: 2,
            boxShadow: 24,
            p: 4,
            outline: "none",
            zIndex: 1000,
          }}
        >
          <h2 id="modal-title">Thông tin lớp học</h2>
          {selectedClass ? (
            <div>
              <TextField
                label="Mã lớp"
                name="code"
                value={selectedClass.code || ""}
                onChange={handleInputChange}
                fullWidth
                disabled
                margin="normal"
                InputProps={{
                  readOnly: true,
                }}
              />
              <FormControl fullWidth margin="normal">
                <Autocomplete
                  options={classrooms.map((classroom) => classroom.classroom)}
                  value={selectedClass.room || ""}
                  onChange={(event, newValue) => {
                    handleInputChange({
                      target: { name: "room", value: newValue },
                    });
                  }}
                  renderInput={(params) => (
                    <TextField {...params} label="Phòng học" />
                  )}
                  freeSolo
                />
              </FormControl>
              <TextField
                label="Tiết bắt đầu"
                name="startTime"
                type="number"
                value={selectedClass.startTime || ""}
                onChange={handleInputChange}
                fullWidth
                margin="normal"
              />
              <TextField
                label="Số tiết học"
                name="numberOfPeriods"
                type="number"
                disabled
                value={selectedClass.numberOfPeriods || ""}
                onChange={handleInputChange}
                fullWidth
                margin="normal"
              />
              <TextField
                label="Ngày"
                name="weekday"
                type="number"
                value={selectedClass.weekday || ""}
                onChange={handleInputChange}
                fullWidth
                margin="normal"
                InputProps={{
                  readOnly: true,
                }}
              />
            </div>
          ) : (
            <p>Không có thông tin lớp học</p>
          )}
          <div className="flex justify-between mt-[20px] gap-4">
            <Button
              onClick={handleClose}
              variant="outlined"
              color="secondary"
              disabled={states.isSavingTimeSlot}
              sx={{ minWidth: '100px', padding: '8px 16px' }}
            >
              Đóng
            </Button>
            <Button
              onClick={handleSave}
              variant="contained"
              color="primary"
              disabled={states.isSavingTimeSlot}
              sx={{ minWidth: '100px', padding: '8px 16px' }}
            >
              {states.isSavingTimeSlot ? "Đang lưu..." : "Lưu"}
            </Button>
          </div>
        </Box>
      </Modal>

      <Dialog 
        open={isAddSlotDialogOpen} 
        onClose={handleCloseAddSlotDialog}
      >
        <DialogTitle>Thêm ca học</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Nhập số tiết cho ca học mới (Số tiết còn lại: {selectedClassForSlot?.duration || 0})
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            label="Số tiết"
            type="number"
            fullWidth
            value={selectedPeriods}
            onChange={(e) => setSelectedPeriods(e.target.value)}
            inputProps={{ 
              min: 1,
              max: selectedClassForSlot?.duration || 1
            }}
          />
        </DialogContent>
        <DialogActions sx={{ padding: '16px', gap: '8px' }}>
          <Button 
            onClick={handleCloseAddSlotDialog}
            variant="outlined" 
            sx={{ minWidth: '80px', padding: '6px 16px' }}
          >
            Hủy
          </Button>
          <Button 
            onClick={handleAddTimeSlot} 
            variant="contained" 
            color="primary"
            sx={{ minWidth: '80px', padding: '6px 16px' }}
          >
            Thêm
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default TimeTable;
