import React, { useState, useEffect } from "react";
import { request } from "../../api";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Autocomplete from '@mui/material/Autocomplete';

const UpdateDialog = ({ open, handleClose, handleUpdate, handleRefreshData }) => {
  const [classrooms, setClassrooms] = useState([]);
  const [semesters, setSemesters] = useState([]);
  const [selectedClassroom, setSelectedClassroom] = useState(null);
  const [selectedSemester, setSelectedSemester] = useState(null);

  useEffect(() => {
    // Fetch classrooms
    request("get", "/classroom/get-all", (res) => {
      setClassrooms(res.data);
    }).then();

    // Fetch semesters
    request("get", "/semester/get-all", (res) => {
      setSemesters(res.data);
    }).then();
  }, []);

  const isUpdateDisabled = !selectedClassroom || !selectedSemester;

  const handleUpdateClick = () => {
    // Combine selected and new values based on user input
    const classRoom = selectedClassroom ? selectedClassroom.classroom : "";
    const semester = selectedSemester ? selectedSemester.semester : "";
    const requestData = {
      classRoom: classRoom,
      semester: semester
    };
    // Validate and handle the update logic
    request("post", "/schedule/calculate-time", (res) => {
      // Call your handleUpdate function if needed
      handleUpdate(res.data);
      // Call handleRefreshData to refresh the data in TimePerformanceScreen
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

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Cập nhật hiệu suất sử dụng thời gian</DialogTitle>
      <DialogContent>
        <Autocomplete
          value={selectedClassroom}
          onChange={(_, newValue) => setSelectedClassroom(newValue)}
          options={classrooms}
          getOptionLabel={(option) => option.classroom}
          renderInput={(params) => (
            <TextField {...params} label="Chọn hoặc nhập phòng học" fullWidth />
          )}
        />
        <div style={{ margin: '16px' }} />
        <Autocomplete
          value={selectedSemester}
          onChange={(_, newValue) => setSelectedSemester(newValue)}
          options={semesters}
          getOptionLabel={(option) => option.semester}
          renderInput={(params) => (
            <TextField {...params} label="Chọn hoặc nhập kỳ học" fullWidth />
          )}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Cancel
        </Button>
        <Button onClick={handleUpdateClick} color="primary" disabled={isUpdateDisabled}>
          Update
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UpdateDialog;
