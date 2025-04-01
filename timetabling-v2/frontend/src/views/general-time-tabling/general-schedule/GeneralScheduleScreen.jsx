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

  // Add a loading state check for scheduling operations
  const isSchedulingInProgress = states.isAutoSaveLoading || 
    states.isTimeScheduleLoading || 
    states.loading;

  return (
    <div className="flex flex-col gap-4 h-[700px]">
      <Tabs 
        value={viewTab} 
        onChange={(e, newVal) => setViewTab(newVal)}
        sx={{
          '& .MuiTab-root': {
            minWidth: '140px',
            fontWeight: 500,
            textTransform: 'none',
            fontSize: '15px',
            py: 1.5,
          }
        }}
      >
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
            <div className="flex flex-wrap justify-end gap-3">
              <Button
                disabled={states.selectedSemester === null || states.isExportExcelLoading}
                startIcon={states.isExportExcelLoading ? <FacebookCircularProgress size={20} /> : null}
                variant="contained"
                color="success"
                onClick={() => handlers.handleExportTimeTabling(states.selectedSemester?.semester)}
                sx={{ 
                  minWidth: '180px', 
                  height: '40px', 
                  padding: '8px 16px',
                  fontWeight: 500,
                  textTransform: 'none',
                  boxShadow: 1
                }}
              >
                Tải xuống File Excel
              </Button>
              <Button
                disabled={states.selectedRows.length === 0 || states.isResetLoading}
                startIcon={states.isResetLoading ? <FacebookCircularProgress size={20} /> : null}
                variant="contained"
                color="error"
                onClick={() => setOpenResetConfirm(true)}
                sx={{ 
                  minWidth: '150px', 
                  height: '40px', 
                  padding: '8px 16px',
                  fontWeight: 500,
                  textTransform: 'none',
                  boxShadow: 1
                }}
              >
                Xóa lịch học TKB
              </Button>
              <Button
                disabled={!states.selectedSemester || states.isAutoSaveLoading}
                startIcon={states.isAutoSaveLoading ? <FacebookCircularProgress size={20} /> : null}
                variant="contained"
                color="primary"
                onClick={() => setters.setOpenTimeslotDialog(true)}
                sx={{ 
                  minWidth: '160px', 
                  height: '40px', 
                  padding: '8px 16px',
                  fontWeight: 500,
                  textTransform: 'none',
                  boxShadow: 1
                }}
              >
                Tự động xếp TKB
              </Button>
              <Button
                disabled={!states.selectedSemester || states.isAutoSaveLoading}
                startIcon={states.isTimeScheduleLoading ? <FacebookCircularProgress size={20} /> : null}
                variant="contained"
                color="primary"
                onClick={() => setters.setOpenClassroomDialog(true)}
                sx={{ 
                  minWidth: '160px', 
                  height: '40px', 
                  padding: '8px 16px',
                  fontWeight: 500,
                  textTransform: 'none',
                  boxShadow: 1
                }}
              >
                Tự động xếp phòng
              </Button>
              <Button
                disabled={states.selectedRows.length === 0 || states.isAutoSaveLoading}
                startIcon={states.isAutoSaveLoading ? <FacebookCircularProgress size={20} /> : null}
                variant="contained"
                color="primary"
                onClick={() => setters.setOpenSelectedDialog(true)}
                sx={{ 
                  minWidth: '200px', 
                  height: '40px', 
                  padding: '8px 16px',
                  fontWeight: 500,
                  textTransform: 'none',
                  boxShadow: 1
                }}
              >
                Xếp lịch lớp đã chọn
              </Button>
            </div>
            
            {/* Dialog components */}
            <div>
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
            
            {/* Confirmation dialog */}
            <Dialog open={openResetConfirm} onClose={() => setOpenResetConfirm(false)}>
              <DialogTitle>Xác nhận xóa lịch học</DialogTitle>
              <DialogContent>
                <DialogContentText>
                  Bạn có chắc chắn muốn xóa {states.selectedRows.length} lịch học đã chọn không?
                </DialogContentText>
              </DialogContent>
              <DialogActions sx={{ padding: '16px', gap: '8px' }}>
                <Button 
                  onClick={() => setOpenResetConfirm(false)}
                  variant="outlined"
                  sx={{ minWidth: '80px', padding: '6px 16px' }}
                >
                  Hủy
                </Button>
                <Button 
                  onClick={handleConfirmReset} 
                  color="error" 
                  variant="contained" 
                  autoFocus
                  sx={{ minWidth: '80px', padding: '6px 16px' }}
                >
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
              loading={states.loading || isSchedulingInProgress}  // Updated loading state
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
