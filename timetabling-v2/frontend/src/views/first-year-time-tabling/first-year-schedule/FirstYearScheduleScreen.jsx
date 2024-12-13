import React from "react";
import { useGeneralSchedule } from "services/useGeneralScheduleData";
import { toast } from "react-toastify";
import AutoScheduleDialog from "./components/AutoScheduleDialog";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import FirstYearGroupAutoComplete from "../common-components/FirstYearGroupAutoComplete";
import { Button } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import FirstYearScheduleTable from "./components/FirstYearScheduleTable";
const FirstYearScheduleScreen = () => {
  const { states, setters, handlers } = useGeneralSchedule();

  // Create a memoized callback for saving time slots
  const handleSaveTimeSlot = React.useCallback((data) => {
    if (!states.selectedSemester?.semester) {
      toast.error("Vui lòng chọn học kỳ!");
      return;
    }
    return handlers.handleSaveTimeSlot(states.selectedSemester.semester, data);
  }, [states.selectedSemester, handlers.handleSaveTimeSlot]);

  return (
    <div className="flex flex-col gap-4 w-full h-[700px]">
      <AutoScheduleDialog
        title={"Tự động xếp lịch học"}
        open={states.isOpenTimeslotDialog}
        closeDialog={() => setters.setOpenTimeslotDialog(false)}
        timeLimit={states.timeSlotTimeLimit}
        setTimeLimit={setters.setTimeSlotTimeLimit}
        submit={handlers.handleAutoScheduleTimeSlotTimeTabling}
      />
      <AutoScheduleDialog
        title={"Tự động xếp phòng học"}
        open={states.isOpenClassroomDialog}
        closeDialog={() => setters.setOpenClassroomDialog(false)}
        setTimeLimit={setters.setClassroomTimeLimit}
        timeLimit={states.classroomTimeLimit}
        submit={handlers.handleAutoScheduleClassroomTimeTabling}
      />
      <div className="flex flex-row justify-between">
        <div className="flex flex-col gap-4">
          <FirstYearSemesterAutoComplete
            selectedSemester={states.selectedSemester}
            setSelectedSemester={setters.setSelectedSemester}
          />
          <FirstYearGroupAutoComplete
            selectedGroup={states.selectedGroup}
            setSelectedGroup={setters.setSelectedGroup}
          />
        </div>
        <div className="flex flex-col justify-end gap-2">
          <div className="flex flex-row justify-end">
            <Button
              disabled={!states.selectedSemester || states.isExportExcelLoading}
              startIcon={states.isExportExcelLoading ? <FacebookCircularProgress/> : null}
              variant="contained"
              color="success"
              onClick={() => handlers.handleExportTimeTabling(states.selectedSemester?.semester)}  
              sx={{ width: 200 }}
            >
              Tải xuống File Excel
            </Button>
          </div>
          <div className="flex flex-row gap-2 justify-end">
            <Button
              disabled={states.selectedRows.length === 0 || states.isResetLoading}
              loading={states.isResetLoading}
              startIcon={states.isResetLoading ? <FacebookCircularProgress/> : null}
              variant="contained"
              color="error"
              onClick={handlers.handleResetTimeTabling}
            >
              Xóa lịch học TKB
            </Button>
          </div>
          <div className="flex flex-row gap-2">
            <Button
              loading={states.isAutoSaveLoading}
              disabled={!(states.selectedSemester && states.selectedGroup) || states.isAutoSaveLoading}
              startIcon={states.isAutoSaveLoading ? <FacebookCircularProgress/> : null}
              variant="contained"
              color="primary"
              onClick={() => setters.setOpenTimeslotDialog(true)}
            >
              Tự động xếp TKB
            </Button>
            <Button
              loading={states.isAutoSaveLoading}
              disabled={!(states.selectedSemester && states.selectedGroup) || states.isAutoSaveLoading}
              startIcon={states.isAutoSaveLoading ? <FacebookCircularProgress/> : null}
              variant="contained"
              color="primary"
              onClick={() => setters.setOpenClassroomDialog(true)}
            >
              Tự động xếp phòng học
            </Button>
          </div>
        </div>
      </div>
      <FirstYearScheduleTable
        isLoading={states.isResetLoading}
        isDataLoading={states.loading}
        classes={states.classes}
        setSelectedRows={setters.setSelectedRows}
        semester={states.selectedSemester}
        onSaveTimeSlot={handleSaveTimeSlot}
      />
    </div>
  );
};

export default FirstYearScheduleScreen;
