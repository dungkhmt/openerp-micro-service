import React, { useEffect, useState } from "react";
import {
  Modal,
  Box,
  Button,
  TextField,
  TablePagination,
  FormControl,
  Autocomplete,
  CircularProgress,
} from "@mui/material";
import { toast } from "react-toastify";
import { request } from "api";
import { useClassrooms } from "views/general-time-tabling/hooks/useClassrooms";
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
  const [saveLoading, setSaveLoading] = useState(false);

  const { classrooms } = useClassrooms(selectedGroup?.groupName || "", null);

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
          onSaveSuccess();
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

  const handleAddTimeSlot = (generalClassId) => {
    const cleanGeneralClassId = generalClassId.split("-")[0];
    request(
      "post",
      `/general-classes/${cleanGeneralClassId}/room-reservations/`,
      (res) => {
        toast.success("Thêm ca học thành công!");
        onSaveSuccess();
      },
      (err) => {
        if (err.response?.status === 410) {
          toast.error(err.response.data);
        } else {
          toast.error("Thêm ca học thất bại!");
        }
      }
    );
  };

  const handleRemoveTimeSlot = (generalClassId, roomReservationId) => {
    const cleanGeneralClassId = generalClassId.split("-")[0];

    request(
      "delete",
      `/general-classes/${cleanGeneralClassId}/room-reservations/${roomReservationId}`,
      (res) => {
        toast.success("Xóa ca học thành công!");
        onSaveSuccess();
      },
      (err) => {
        if (err.response?.status === 410) {
          toast.error(err.response.data);
        } else {
          toast.error("Xóa ca học thất bại!");
        }
      }
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
