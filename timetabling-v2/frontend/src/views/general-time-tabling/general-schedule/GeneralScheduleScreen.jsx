import { useGeneralSchedule } from "services/useGeneralScheduleData";
import AutoScheduleDialog from "./components/AutoScheduleDialog";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import { Button } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import TimeTable from "./components/TimeTable";
import { useState } from "react";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

const GeneralScheduleScreen = () => {
  const { states, setters, handlers } = useGeneralSchedule();
  const [openResetConfirm, setOpenResetConfirm] = useState(false);

  const handleConfirmReset = () => {
    handlers.handleResetTimeTabling();
    setOpenResetConfirm(false);
  };

  return (
    <div className="flex flex-col gap-4 h-[700px]">
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
          <GeneralSemesterAutoComplete
            selectedSemester={states.selectedSemester}
            setSelectedSemester={setters.setSelectedSemester}
          />
          <GeneralGroupAutoComplete
            selectedGroup={states.selectedGroup}
            setSelectedGroup={setters.setSelectedGroup}
          />
        </div>
        <div className="flex flex-col justify-end gap-2">
          <div className="flex flex-row justify-end">
            <Button
              disabled={states.selectedSemester === null || states.isExportExcelLoading}
              startIcon={
                states.isExportExcelLoading ? <FacebookCircularProgress /> : null
              }
              variant="contained"
              color="success"
              onClick={() => handlers.handleExportTimeTabling(states.selectedSemester?.semester)}
              loading={states.isExportExcelLoading}
              sx={{ width: 200 }}
            >
              Tải xuống File Excel
            </Button>
          </div>
          <div className="flex flex-row gap-2 justify-end">
            <Button
              disabled={states.selectedRows.length === 0 || states.isResetLoading}
              loading={states.isResetLoading}
              startIcon={states.isResetLoading ? <FacebookCircularProgress /> : null}
              variant="contained"
              color="error"
              onClick={() => setOpenResetConfirm(true)}
            >
              Xóa lịch học TKB
            </Button>
          </div>
          <div className="flex flex-row gap-2">
            <Button
              loading={states.isAutoSaveLoading}
              disabled={
                !(states.selectedSemester !== null && states.selectedGroup !== null) ||
                states.isAutoSaveLoading
              }
              startIcon={
                states.isAutoSaveLoading ? <FacebookCircularProgress /> : null
              }
              variant="contained"
              color="primary"
              onClick={() => setters.setOpenTimeslotDialog(true)}
            >
              Tự động xếp TKB
            </Button>
            <Button
              loading={states.isAutoSaveLoading}
              disabled={
                !(states.selectedSemester !== null && states.selectedGroup !== null) ||
                states.isAutoSaveLoading
              }
              startIcon={
                states.isTimeScheduleLoading ? <FacebookCircularProgress /> : null
              }
              variant="contained"
              color="primary"
              onClick={() => setters.setOpenClassroomDialog(true)}
            >
              Tự động xếp phòng học
            </Button>
          </div>
        </div>
      </div>
      <Dialog
        open={openResetConfirm}
        onClose={() => setOpenResetConfirm(false)}
      >
        <DialogTitle>Xác nhận xóa lịch học</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Bạn có chắc chắn muốn xóa {states.selectedRows.length} lịch học đã chọn không?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenResetConfirm(false)}>
            Hủy
          </Button>
          <Button 
            onClick={handleConfirmReset}
            color="error"
            variant="contained"
            autoFocus
          >
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
      <div className="flex flex-row gap-4 w-full overflow-y-hidden h-[550px] border-[1px] border-[#ccc] rounded-[8px]">
        <TimeTable
          selectedSemester={states.selectedSemester}
          classes={states.classes}
          selectedGroup={states.selectedGroup}
          onSaveSuccess={handlers.handleRefreshClasses}
          loading={states.loading}
          selectedRows={states.selectedRows}
          onSelectedRowsChange={setters.setSelectedRows}
        />
      </div>
    </div>
  );
};

export default GeneralScheduleScreen;
