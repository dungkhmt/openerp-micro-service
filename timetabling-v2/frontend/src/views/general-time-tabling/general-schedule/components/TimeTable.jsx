import { useEffect, useState } from "react";


import { useClassrooms } from "views/general-time-tabling/hooks/useClassrooms";
import { useGeneralSchedule } from "services/useGeneralScheduleData";
import { Autocomplete, Box, Button, CircularProgress, FormControl, Modal, TablePagination, TextField } from "@mui/material";
import { Add, Remove } from "@mui/icons-material";

const TimeTable = ({
  classes,
  selectedSemester,
  selectedGroup,
  onSaveSuccess,
  loading,
}) => {
  const [classDetails, setClassDetails] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(100);

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
          generalClassId: cls.id,
        }))
        .sort((a, b) => {
          if (a.code === b.code) {
            const idA = parseInt(a.generalClassId.split("-")[0], 10);
            const idB = parseInt(b.generalClassId.split("-")[0], 10);
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

  const handleAddTimeSlot = async (generalClassId) => {
    try {
      if (!generalClassId) {
        throw new Error('Missing generalClassId');
      }
      await handlers.handleAddTimeSlot({
        generalClassId: generalClassId.toString()
      });
      onSaveSuccess();
    } catch (error) {
      console.error("Error adding time slot:", error);
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
        numberOfPeriods: Number(classInfo.endTime) - period + 1,
      });
    } else {
      setSelectedClass({
        roomReservationId: Number(classDetails[classIndex].roomReservationId),
        room: classDetails[classIndex].room,
        startTime: period,
        endTime: undefined,
        weekday: days.indexOf(day) + 2,
        code: classDetails[classIndex].code,
        numberOfPeriods: undefined,
      });
    }
    setOpen(true);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <div className="h-full w-full flex flex-col justify-start">
      {loading ? (
        <table
          className=" overflow-x-auto flex items-center flex-col"
          style={{ flex: "1" }}
        >
          <thead>
            <tr>
              <th className="border border-gray-300 p-2">Mã lớp</th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "100px", minWidth: "100px" }}
              >
                Lớp học
              </th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "52px", minWidth: "52px" }}
              >
                Tuần học
              </th>
              <th className="border border-gray-300 p-2">Mã học phần</th>
              <th
                className="border border-gray-300 p-2"
                style={{ width: "120px", minWidth: "120px" }}
              >
                Tên học phần
              </th>
              <th className="border border-gray-300 p-2">SL MAX</th>
              <th className="border border-gray-300 p-2">Loại lớp</th>
              <th className="border border-gray-300 p-2">Thời lượng</th>
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
              <td colSpan={12} className="border"></td>
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
        <div className=" overflow-x-auto" style={{ flex: "1" }}>
          <table className="min-w-full " style={{ tableLayout: "auto" }}>
            <thead>
              <tr>
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
                  >
                    {day}
                  </th>
                ))}
              </tr>
              <tr>
                <td colSpan={12} className="border"></td>
                {days.flatMap((day) =>
                  periods.map((period) => (
                    <td
                      key={`${day}-${period}`}
                      className="border border-t-0 text-center"
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
                  .map((classDetail, index) => (
                    <tr
                      key={`${classDetail.code}-${index}`}
                      style={{ height: "52px" }}
                    >
                      <td className="border border-l-0  text-center px-1">
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
                        {classDetail.batch}
                      </td>
                      <td className="border  text-center px-1">
                        <Button
                          onClick={() =>
                            handleAddTimeSlot(classDetail.generalClassId)
                          }
                        >
                          <Add />
                        </Button>
                      </td>
                      <td className="border  text-center px-1">
                        <Button
                          onClick={() =>
                            handleRemoveTimeSlot(
                              classDetail.generalClassId,
                              classDetail.roomReservationId
                            )
                          }
                        >
                          <Remove />
                        </Button>
                      </td>
                      {days.flatMap((day) =>
                        periods.map((period) =>
                          renderCellContent(index, day, period)
                        )
                      )}
                    </tr>
                  ))
              ) : (
                <tr>
                  <td
                    colSpan={12 + days.length * periods.length}
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
          <div className="flex justify-between mt-[20px]">
            <Button
              onClick={handleClose}
              variant="contained"
              color="secondary"
              disabled={states.isSavingTimeSlot}
            >
              Đóng
            </Button>
            <Button
              onClick={handleSave}
              variant="contained"
              color="primary"
              disabled={states.isSavingTimeSlot}
            >
              {states.isSavingTimeSlot ? "Đang lưu..." : "Lưu"}
            </Button>
          </div>
        </Box>
      </Modal>
    </div>
  );
};

export default TimeTable;
