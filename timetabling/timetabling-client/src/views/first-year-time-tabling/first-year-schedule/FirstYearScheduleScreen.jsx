import React, { useState } from "react";
import FirstYearScheduleTable from "./components/FirstYearScheduleTable";
import { Button} from "@mui/material";
import FirstYearGroupAutoComplete from "../common-components/FirstYearGroupAutoComplete";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { toast } from "react-toastify";
import { useClasses } from "../hooks/useClasses";
import AutoScheduleDialog from "./components/AutoScheduleDialog";

const FirstYearScheduleScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isResetLoading, setResetLoading] = useState(false);
  const [isSaveLoading, setSaveLoading] = useState(false);
  const [isTimeScheduleLoading, setTimeScheduleLoading] = useState(false);
  const [selectedRows, setSelectedRows] = useState([]);
  const { loading, error, classes, setClasses, setLoading } = useClasses(
    selectedGroup,
    selectedSemester
  );
  const [saveRequests, setSaveRequests] = useState([]);
  const [isAutoSaveLoading, setAutoSaveLoading] = useState(false);
  const [isOpenClassroomDialog, setOpenClassroomDialog] = useState(false);
  const [isOpenTimeslotDialog, setOpenTimeslotDialog] = useState(false);
  const [isExportExecelLoading, setExportExcelLoading] = useState(false);
  const [classroomTimeLimit, setClassroomTimeLimit] = useState(5);
  const [timeSlotTimeLimit, setTimeSlotTimeLimit] = useState(5);

  const handleResetTimeTabling = () => {
    setResetLoading(true);
    request(
      "post",
      `/general-classes/reset-schedule?semester=${selectedSemester?.semester}`,
      (res) => {
        let firstYearClasses = [];
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
              firstYearClasses.push(cloneObj);
            });
          }
        });
        setClasses(firstYearClasses);
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
        let firstYearClasses = [];
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
              firstYearClasses.push(cloneObj);
            });
          }
        });
        setClasses(firstYearClasses);
        setSelectedRows([]);
        setOpenTimeslotDialog(false);
        setAutoSaveLoading(false);
        toast.success("Tự động thời khóa biểu thành công!");
      },
      (error) => {
        if (error.response.status == 410) {
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
        let firstYearClasses = [];
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
              firstYearClasses.push(cloneObj);
            });
            console.log(firstYearClasses);
          }
        });
        setClasses(firstYearClasses);
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

  const handleSaveTimeTabling = () => {
    setSaveLoading(true);
    request(
      "post",
      `/general-classes/update-class-schedule-v2?semester=${selectedSemester?.semester}`,
      (res) => {
        setSaveLoading(false);
        toast.success("Lưu TKB thành công!");
        console.log(res.data);
        setSaveRequests([]);
      },
      (error) => {
        if (error.response.status === 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Có lỗi khi lưu TKB!");
        }
        setSaveLoading(false);
        console.log(error);
      },
      { saveRequests: saveRequests }
    );
  };

  return (
    <div className="flex flex-col gap-4 w-full h-[700px]">
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
          <FirstYearSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <FirstYearGroupAutoComplete
            selectedGroup={selectedGroup}
            setSelectedGroup={setSelectedGroup}
          />
        </div>
        <div className="flex flex-col justify-end gap-2">
          <div className="flex flex-row justify-end">
            <Button
              disabled={selectedSemester === null || isExportExecelLoading}
              startIcon={isExportExecelLoading ? <FacebookCircularProgress/> : null}
              variant="contained"
              color="success"
              onClick={handleExportTimeTabling}
              loading = {isExportExecelLoading}
              sx={{ width: 200 }}
            >
              Tải xuống File Excel
            </Button>
          </div>
          <div className="flex flex-row gap-2 justify-end">
            <Button
              disabled={saveRequests.length === 0 || isSaveLoading}
              startIcon={isSaveLoading ? <FacebookCircularProgress /> : null}
              variant="contained"
              color="primary"
              loading={isSaveLoading}
              onClick={handleSaveTimeTabling}
            >
              Lưu TKB
            </Button>
            <Button
              disabled={selectedRows.length === 0 || isResetLoading}
              loading= {isResetLoading}
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
                isAutoSaveLoading ?
                <FacebookCircularProgress/> : null
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
                isTimeScheduleLoading ? <FacebookCircularProgress/> : null
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
      <FirstYearScheduleTable
        saveRequests={saveRequests}
        setSaveRequests={setSaveRequests}
        isLoading={isResetLoading}
        isDataLoading={loading}
        classes={classes}
        setClasses={setClasses}
        setSelectedRows={setSelectedRows}
        setLoading={setLoading}
        semester={selectedSemester}
      />
    </div>
  );
};

export default FirstYearScheduleScreen;
