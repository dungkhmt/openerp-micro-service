import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Autocomplete, Checkbox, FormControlLabel } from '@mui/material';
import { request } from "../../../api";

export default function AutomationMakeSchedule({ open, handleClose, handleRefreshData }) {
  const [semesters, setSemesters] = useState([]);
  const [groups, setGroups] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [selectedWeekdayPriority, setSelectedWeekdayPriority] = useState(null);
  const [autoAssignClassroom, setAutoAssignClassroom] = useState(false);

  useEffect(() => {
    request("get", "/semester/get-all", (res) => {
      setSemesters(res.data);
    });

    request("get", "/group/get-all", (res) => {
      setGroups(res.data);
    });
  }, [])

  const weekdayPriorities = [
    "2,3,4,5,6",
    "2,4,5,3,6",
    "3,5,2,4,6",
    "4,6,2,3,5",
    "2,4,6,3,5",
    "2,4,3,5,6"
  ]

  const handleMakeSchedule = () => {
    const url = "/class-opened/auto-make-schedule";

    const semesterName = selectedSemester.semester;
    const groupName = selectedGroup.groupName;
    const weekdayPriority = selectedWeekdayPriority;

    const requestData = {
      semester: semesterName,
      groupName: groupName,
      weekdayPriority: weekdayPriority,
      isClassroomArranged: autoAssignClassroom,
  };

    request("post", url, (res) => {
      // Call handleRefreshData to refresh the data 
      handleRefreshData();
      //close dialog
      handleClose();
    },
      {},
      requestData
    ).then();

    // Close the dialog
    handleClose();
  };

  const handleSemesterChange = (event, newValue) => {
    setSelectedSemester(newValue);
  };

  const handleGroupChange = (event, newValue) => {
    setSelectedGroup(newValue);
  };

  const handleWeekdaypriorityChange = (event, newValue) => {
    setSelectedWeekdayPriority(newValue);
  };

  const handleAutoAssignClassroomChange = (event) => {
    setAutoAssignClassroom(event.target.checked);
  };

  return (
    <Dialog open={open} onClose={handleClose} aria-labelledby="create-new-semester-dialog">
      <DialogTitle id="create-new-semester-dialog-title">Sắp xếp lịch học tự động</DialogTitle>
      <DialogContent>
        <Autocomplete
        autoFocus
          options={semesters}
          getOptionLabel={(option) => option.semester}
          style={{ width: 250, marginBottom: '8px'}}
          value={selectedSemester}
          renderInput={(params) => <TextField {...params} label="Chọn kỳ học" />}
          onChange={handleSemesterChange}
        />
        <Autocomplete
          options={groups}
          getOptionLabel={(option) => option.groupName}
          style={{ width: 250, marginBottom: '8px'}}
          value={selectedGroup}
          renderInput={(params) => <TextField {...params} label="Chọn nhóm học" />}
          onChange={handleGroupChange}
        />
        <Autocomplete
          options={weekdayPriorities}
          getOptionLabel={(option) => option}
          style={{ width: 250 }}
          value={selectedWeekdayPriority}
          renderInput={(params) => <TextField {...params} label="Độ ưu tiên ngày học" />}
          onChange={handleWeekdaypriorityChange}
        />
        <FormControlLabel
          control={<Checkbox checked={autoAssignClassroom} onChange={handleAutoAssignClassroomChange} />}
          label="Tự động sắp xếp phòng học"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">
          Cancel
        </Button>
        <Button
          onClick={handleMakeSchedule}
          color="primary"
          disabled={!selectedSemester || !selectedGroup || !selectedWeekdayPriority} // Disable the button if the input is empty
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
}
