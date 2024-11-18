import React, { useState } from "react";
import { Button } from "@mui/material";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { toast } from "react-toastify";
import { useClasses } from "../hooks/useClasses";
import AutoScheduleDialog from "./components/AutoScheduleDialog";
import TimeTable from "./components/TimeTable";

const GeneralScheduleScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isResetLoading, setResetLoading] = useState(false);
  const [isTimeScheduleLoading, setTimeScheduleLoading] = useState(false);
  const [selectedRows, setSelectedRows] = useState([]);
  const [refresh, setRefresh] = useState(false); // Thêm cờ refresh
  const { loading, classes, setClasses } = useClasses(
    selectedGroup,
    selectedSemester,
    refresh
  );
  const [isAutoSaveLoading, setAutoSaveLoading] = useState(false);
  const [isOpenClassroomDialog, setOpenClassroomDialog] = useState(false);
  const [isOpenTimeslotDialog, setOpenTimeslotDialog] = useState(false);
  const [isExportExecelLoading, setExportExcelLoading] = useState(false);
  const [classroomTimeLimit, setClassroomTimeLimit] = useState(5);
  const [timeSlotTimeLimit, setTimeSlotTimeLimit] = useState(5);

  const handleRefreshClasses = () => {
    setRefresh((prev) => !prev);
  };

  const handleResetTimeTabling = () => {
    setResetLoading(true);
    request(
      "post",
      `/general-classes/reset-schedule?semester=${selectedSemester?.semester}`,
      (res) => {
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  id: classObj.id + `-${index + 1}`,
                  roomReservationId: timeSlot.id,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        setClasses(generalClasses);
        setSelectedRows([]);
        setResetLoading(false);
        toast.success("Reset thời khóa biểu thành công!");
      },
      (error) => {
        console.log(error);
        toast.error("Có lỗi khi reset thời khóa biểu!");
        setResetLoading(false);
      },
      { ids: selectedRows }
    );
  };

  const handleAutoScheduleTimeSlotTimeTabling = () => {
    setAutoSaveLoading(true);
    request(
      "post",
      `/general-classes/auto-schedule-time?semester=${selectedSemester?.semester}&groupName=${selectedGroup?.groupName}&timeLimit=${timeSlotTimeLimit}`,
      (res) => {
        setAutoSaveLoading(true);
        let generalClasses = [];
        res?.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  id: classObj.id + `-${index + 1}`,
                  roomReservationId: timeSlot.id,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        setClasses(generalClasses);
        setSelectedRows([]);
        setOpenTimeslotDialog(false);
        setAutoSaveLoading(false);
        toast.success("Tự động xếp thời khóa biểu thành công!");
      },
      (error) => {
        if (error.response.status == 410 || error.response.status == 420) {
          toast.error(error.response.data);
          setAutoSaveLoading(false);
        } else {
          console.log(error);
          toast.error("Có lỗi khi tự động thời khóa biểu!");
          setAutoSaveLoading(false);
        }
      }
    );
  };
  const handleAutoScheduleClassroomTimeTabling = () => {
    setAutoSaveLoading(true);
    request(
      "post",
      `/general-classes/auto-schedule-room?semester=${selectedSemester?.semester}&groupName=${selectedGroup?.groupName}&timeLimit=${classroomTimeLimit}`,
      (res) => {
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  id: classObj.id + `-${index + 1}`,
                  roomReservationId: timeSlot.id,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        setClasses(generalClasses);
        setOpenClassroomDialog(false);
        setSelectedRows([]);
        setAutoSaveLoading(false);

        toast.success("Tự động phòng thành công!");
      },
      (error) => {
        if (error.response.status == 410) {
          toast.error(error.response.data);
          setAutoSaveLoading(false);
        } else {
          console.log(error);
          toast.error("Có lỗi khi tự động xếp phòng");
          setAutoSaveLoading(false);
        }
      }
    );
  };

  const handleExportTimeTabling = () => {
    setExportExcelLoading(true);
    request(
      "post",
      `general-classes/export-excel?semester=${selectedSemester?.semester}`,
      (res) => {
        const blob = new Blob([res.data], {
          type: res.headers["content-type"],
        });
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = `Danh_sach_lop_TKB_${selectedSemester?.semester}.xlsx`;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        setExportExcelLoading(false);
      },
      (error) => {
        setExportExcelLoading(false);
        console.error("Error exporting Excel:", error);
      },
      null,
      { responseType: "arraybuffer" },
      null
    ).then();
  };

  return (
    <div className="flex flex-col gap-4 h-[700px]">
      <AutoScheduleDialog
        title={"Tự động xếp lịch học"}
        open={isOpenTimeslotDialog}
        closeDialog={() => setOpenTimeslotDialog(false)}
        timeLimit={timeSlotTimeLimit}
        setTimeLimit={setTimeSlotTimeLimit}
        submit={handleAutoScheduleTimeSlotTimeTabling}
      />
      <AutoScheduleDialog
        title={"Tự động xếp phòng học"}
        open={isOpenClassroomDialog}
        closeDialog={() => setOpenClassroomDialog(false)}
        setTimeLimit={setClassroomTimeLimit}
        timeLimit={classroomTimeLimit}
        submit={handleAutoScheduleClassroomTimeTabling}
      />
      <div className="flex flex-row justify-between">
        <div className="flex flex-col gap-4">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <GeneralGroupAutoComplete
            selectedGroup={selectedGroup}
            setSelectedGroup={setSelectedGroup}
          />
        </div>
        <div className="flex flex-col justify-end gap-2">
          <div className="flex flex-row justify-end">
            <Button
              disabled={selectedSemester === null || isExportExecelLoading}
              startIcon={
                isExportExecelLoading ? <FacebookCircularProgress /> : null
              }
              variant="contained"
              color="success"
              onClick={handleExportTimeTabling}
              loading={isExportExecelLoading}
              sx={{ width: 200 }}
            >
              Tải xuống File Excel
            </Button>
          </div>
          <div className="flex flex-row gap-2 justify-end">
            {/* <Button
              disabled={saveRequests.length === 0 || isSaveLoading}
              startIcon={isSaveLoading ? <FacebookCircularProgress /> : null}
              variant="contained"
              color="primary"
              loading={isSaveLoading}
              onClick={handleSaveTimeTabling}
            >
              Lưu TKB
            </Button> */}
            <Button
              disabled={selectedRows.length === 0 || isResetLoading}
              loading={isResetLoading}
              startIcon={isResetLoading ? <FacebookCircularProgress /> : null}
              variant="contained"
              color="error"
              onClick={handleResetTimeTabling}
            >
              Xóa lịch học TKB
            </Button>
          </div>
          <div className="flex flex-row gap-2">
            <Button
              loading={isAutoSaveLoading}
              disabled={
                !(selectedSemester !== null && selectedGroup !== null) ||
                isAutoSaveLoading
              }
              startIcon={
                isAutoSaveLoading ? <FacebookCircularProgress /> : null
              }
              variant="contained"
              color="primary"
              onClick={() => setOpenTimeslotDialog(true)}
            >
              Tự động xếp TKB
            </Button>
            <Button
              loading={isAutoSaveLoading}
              disabled={
                !(selectedSemester !== null && selectedGroup !== null) ||
                isAutoSaveLoading
              }
              startIcon={
                isTimeScheduleLoading ? <FacebookCircularProgress /> : null
              }
              variant="contained"
              color="primary"
              onClick={() => setOpenClassroomDialog(true)}
            >
              Tự động xếp phòng học
            </Button>
          </div>
        </div>
      </div>
      <div className="flex flex-row gap-4 w-full overflow-y-hidden h-[550px] border-[1px] border-[#ccc] rounded-[8px]">
        <TimeTable
          selectedSemester={selectedSemester}
          classes={classes}
          selectedGroup={selectedGroup}
          onSaveSuccess={handleRefreshClasses}
          loading={loading}
        />
      </div>
    </div>
  );
};

export default GeneralScheduleScreen;
