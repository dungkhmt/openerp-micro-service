import React, { useEffect, useState } from "react";
import {
  Modal,
  Box,
  Button,
  TextField,
  TablePagination,
  InputLabel,
  FormControl,
  Select,
  MenuItem,
} from "@mui/material";
import { toast } from "react-toastify";
import { request } from "api";
import { useClassrooms } from "views/general-time-tabling/hooks/useClassrooms";

const TimeTable = ({
  classes,
  selectedSemester,
  selectedGroup,
  onSaveSuccess,
}) => {
  const [classDetails, setClassDetails] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(100);
  const [saveLoading, setSaveLoading] = useState(false);

  const { classrooms } = useClassrooms(selectedGroup?.groupName || "", null);

  useEffect(() => {
    if (classes && classes.length > 0) {
      const transformedClassDetails = classes
        .map((cls) => ({
          weekDay: cls.weekday,
          roomReservationId: cls.roomReservationId,
          code: cls.classCode,
          shift: cls.crew,
          elective: "",
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
        }))
        .sort((a, b) => parseInt(a.code) - parseInt(b.code));

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

  const handleSave = () => {
    if (!selectedClass) return;

    setSaveLoading(true);

    const calculatedEndTime =
      selectedClass.startTime + selectedClass.numberOfPeriods - 1;
    const { numberOfPeriods, code, ...filteredSelectedClass } = selectedClass;

    const selectedClassData = {
      ...filteredSelectedClass,
      endTime: calculatedEndTime,
    };

    request(
      "post",
      `/general-classes/update-class-schedule-v2?semester=${selectedSemester?.semester}`,
      (res) => {
        setSaveLoading(false);
        toast.success("Lưu TKB thành công!");

        setClassDetails((prevDetails) => {
          const existingClassIndex = prevDetails.findIndex(
            (cls) => cls.roomReservationId === selectedClass.roomReservationId
          );

          if (existingClassIndex !== -1) {
            const updatedDetails = [...prevDetails];
            updatedDetails[existingClassIndex] = selectedClassData;
            return updatedDetails;
          } else {
            return [...prevDetails, selectedClassData];
          }
        });

        if (onSaveSuccess) {
          onSaveSuccess();
        }

        setOpen(false);
      },
      (error) => {
        setSaveLoading(false);
        if (error.response?.status === 410) {
          toast.warn(error.response.data);
        } else if (error.response?.status === 420) {
          toast.error(error.response.data);
        } else {
          toast.error("Có lỗi khi lưu TKB!");
        }
        console.log(error);
      },
      { saveRequests: [selectedClassData] }
    );
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
          className="border border-gray-300 text-center cursor-pointer px-1"
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
          style={{ width: `${70 * colSpan}px` }}
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
    <div className="w-full h-full flex flex-col justify-start">
      <div className="overflow-y-auto border-b-[1px]" style={{ flex: "1" }}>
        <table
          className="min-w-full border-collapse border border-gray-400"
          style={{ tableLayout: "fixed" }}
        >
          <thead>
            <tr>
              <th className="border border-gray-300 p-2">Mã lớp</th>
              <th className="border border-gray-300 p-2">Kíp</th>
              <th className="border border-gray-300 p-2">Tự chọn</th>
              <th className="border border-gray-300 p-2">Khóa</th>
              {days.map((day) => (
                <th
                  key={day}
                  colSpan={6}
                  className="border border-gray-300 p-2 text-center border-l-0 text-black"
                >
                  {day}
                </th>
              ))}
            </tr>
            <tr className="border-l-0">
              <td colSpan={4}></td>
              {days.flatMap((day) =>
                periods.map((period) => (
                  <th
                    key={`${day}-${period}`}
                    className="border border-gray-300 px-2 py-1 text-center"
                  >
                    {period}
                  </th>
                ))
              )}
            </tr>
          </thead>
          <tbody>
            {classDetails
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((classDetail, index) => (
                <tr
                  key={`${classDetail.code}-${index}`}
                  style={{ height: "52px" }}
                >
                  <td className="border border-gray-300 text-center px-1">
                    {classDetail.code}
                  </td>
                  <td className="border border-gray-300 text-center px-1">
                    {classDetail.shift}
                  </td>
                  <td className="border border-gray-300 text-center px-1">
                    {classDetail.elective}
                  </td>
                  <td className="border border-gray-300 text-center px-1">
                    {classDetail.batch}
                  </td>
                  {days.flatMap((day) =>
                    periods.map((period) =>
                      renderCellContent(index, day, period)
                    )
                  )}
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      <TablePagination
        className="border-r-[1px] border-l-[1px] border-solid border-gray-300"
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
        onClose={handleClose}
        aria-labelledby="modal-title"
        aria-describedby="modal-description"
      >
        <Box
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
                <InputLabel
                  className="bg-white !p-2 !py-0 left-[-4px]"
                  id="room-label"
                  shrink
                >
                  Phòng học
                </InputLabel>
                <Select
                  labelId="room-label"
                  name="room"
                  value={selectedClass.room || ""}
                  onChange={handleInputChange}
                  fullWidth
                >
                  {classrooms.map((classroom) => (
                    <MenuItem
                      key={classroom.classroom}
                      value={classroom.classroom}
                    >
                      {classroom.classroom}
                    </MenuItem>
                  ))}
                </Select>
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
              disabled={saveLoading}
            >
              Đóng
            </Button>
            <Button
              onClick={handleSave}
              variant="contained"
              color="primary"
              disabled={saveLoading}
            >
              {saveLoading ? "Đang lưu..." : "Lưu"}
            </Button>
          </div>
        </Box>
      </Modal>
    </div>
  );
};

export default TimeTable;
