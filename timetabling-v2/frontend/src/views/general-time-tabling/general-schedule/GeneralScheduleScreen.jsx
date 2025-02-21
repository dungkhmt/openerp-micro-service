import { useGeneralSchedule } from "services/useGeneralScheduleData";
import AutoScheduleDialog from "./components/AutoScheduleDialog";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import { Button, Tabs, Tab } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import TimeTable from "./components/TimeTable";
// Replace with RoomOccupationScreen instead of RoomScheduleScreen:
import RoomOccupationScreen from "../room-occupation/RoomOccupationScreen"; 
import { useState } from "react";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

const GeneralScheduleScreen = () => {
  const { states, setters, handlers } = useGeneralSchedule();
  const [viewTab, setViewTab] = useState(0);
  const [openResetConfirm, setOpenResetConfirm] = useState(false);

  const handleConfirmReset = () => {
    handlers.handleResetTimeTabling();
    setOpenResetConfirm(false);
  };

  return (
    <div className="flex flex-col gap-4 h-[700px]">
      <Tabs value={viewTab} onChange={(e, newVal) => setViewTab(newVal)}>
        <Tab label="View By Class" />
        <Tab label="View By Room" />
      </Tabs>
      {viewTab === 0 ? (
        // View By Class tab content with auto complete controls
        <div>
          <div className="flex flex-row gap-4">
            <GeneralSemesterAutoComplete
              selectedSemester={states.selectedSemester}
              setSelectedSemester={setters.setSelectedSemester}
            />
            <GeneralGroupAutoComplete
              selectedGroup={states.selectedGroup}
              setSelectedGroup={setters.setSelectedGroup}
            />
          </div>
          {/* Group action buttons and dialogs in one segment */}
          <div className="mt-4 flex flex-col gap-4">
            <div className="flex flex-row justify-end gap-2">
              <Button
                disabled={states.selectedSemester === null || states.isExportExcelLoading}
                startIcon={states.isExportExcelLoading ? <FacebookCircularProgress /> : null}
                variant="contained"
                color="success"
                onClick={() => handlers.handleExportTimeTabling(states.selectedSemester?.semester)}
                loading={states.isExportExcelLoading}
                sx={{ width: 200 }}
              >
                Tải xuống File Excel
              </Button>
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
              <Button
                loading={states.isAutoSaveLoading}
                disabled={!states.selectedSemester || states.isAutoSaveLoading}
                startIcon={states.isAutoSaveLoading ? <FacebookCircularProgress /> : null}
                variant="contained"
                color="primary"
                onClick={() => setters.setOpenTimeslotDialog(true)}
              >
                Tự động xếp TKB
              </Button>
              <Button
                loading={states.isAutoSaveLoading}
                disabled={!states.selectedSemester || states.isAutoSaveLoading}
                startIcon={states.isTimeScheduleLoading ? <FacebookCircularProgress /> : null}
                variant="contained"
                color="primary"
                onClick={() => setters.setOpenClassroomDialog(true)}
              >
                Tự động xếp phòng học
              </Button>
              <Button
                disabled={states.selectedRows.length === 0 || states.isAutoSaveLoading}
                loading={states.isAutoSaveLoading}
                startIcon={states.isAutoSaveLoading ? <FacebookCircularProgress /> : null}
                variant="contained"
                color="primary"
                onClick={() => setters.setOpenSelectedDialog(true)}
              >
                Tự động xếp lịch học theo lớp đã chọn
              </Button>
            </div>
            <div className="flex flex-row justify-end gap-2">
              <AutoScheduleDialog
                title={"Tự động xếp lịch học của kì học"}
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
              <AutoScheduleDialog
                title={"Tự động xếp lịch các lớp đã chọn"}
                open={states.isOpenSelectedDialog}
                closeDialog={() => setters.setOpenSelectedDialog(false)}
                timeLimit={states.selectedTimeLimit}
                setTimeLimit={setters.setSelectedTimeLimit}
                submit={handlers.handleAutoScheduleSelected}
              />
            </div>
            <Dialog open={openResetConfirm} onClose={() => setOpenResetConfirm(false)}>
              <DialogTitle>Xác nhận xóa lịch học</DialogTitle>
              <DialogContent>
                <DialogContentText>
                  Bạn có chắc chắn muốn xóa {states.selectedRows.length} lịch học đã chọn không?
                </DialogContentText>
              </DialogContent>
              <DialogActions>
                <Button onClick={() => setOpenResetConfirm(false)}>Hủy</Button>
                <Button onClick={handleConfirmReset} color="error" variant="contained" autoFocus>
                  Xóa
                </Button>
              </DialogActions>
            </Dialog>
          </div>
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
      ) : (
        // View By Room tab now uses RoomOccupationScreen with shared semester
        <RoomOccupationScreen 
          selectedSemester={states.selectedSemester}
          setSelectedSemester={setters.setSelectedSemester}
        />
      )}
    </div>
  );
};

export default GeneralScheduleScreen;
