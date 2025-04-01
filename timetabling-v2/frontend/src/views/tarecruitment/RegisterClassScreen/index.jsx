import {
  Paper,
  TextField,
  MenuItem,
  FormControl,
  Select,
  InputLabel,
  Button,
  Typography,
  Box,
  IconButton,
} from "@mui/material";
import ClearIcon from '@mui/icons-material/Clear';
import { useState, useEffect } from "react";
import { request } from "api";
import { warningNoti, successNoti, errorNoti } from "utils/notification";
import styles from "./index.style";
import { useParams, useHistory } from "react-router-dom";
import { classCallUrl } from "../apiURL";

const RegisterClassScreen = () => {
  const history = useHistory();
  const { semester } = useParams();
  const [enableAddWeekButton, setEnableAddWeekButton] = useState(false);
  const [weekData, setWeekData] = useState({
    start: "",
    end: "",
  })
  const [weeks, setWeeks] = useState([]);
  const [formData, setFormData] = useState({
    id: "",
    day: "",
    startPeriod: "",
    endPeriod: "",
    subjectId: "",
    subjectName: "",
    classRoom: "",
    note: "",
    weeks: "",
    semester: semester,
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const weeksString = weeks.map(week => `(${week.start}, ${week.end})`).join(', ');
    setFormData((prevData) => ({
      ...prevData,
      weeks: weeksString
    }))
    if (
      !formData.day ||
      !formData.startPeriod ||
      !formData.endPeriod ||
      !formData.subjectId ||
      !formData.subjectName ||
      !formData.classRoom ||
      !formData.semester ||
      !formData.id ||
      !formData.weeks
    ) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    } else if (formData.startPeriod >= formData.endPeriod) {
      warningNoti("Tiết bắt đầu phải nhỏ hơn tiết kết thúc", 5000);
      return;
    } else {
      request(
        "post",
        `${classCallUrl.createClass}`,
        (res) => {
          successNoti("Tạo lớp học thành công", 5000);
          history.push("/ta-recruitment/teacher/class-list");
        },
        (res) => {
          console.log(res);
          errorNoti(res.response.data, 5000);
        },
        formData
      );
    }
  };

  const handleChangeWeek = (event) => {
    const { name, value } = event.target;
    setWeekData((prevData) => ({
      ...prevData,
      [name]: value
    }));
  }

  useEffect(() => {
    if (weekData.start == '' || weekData.end == '') {
      setEnableAddWeekButton(false);
      return;
    }

    if (weekData.start > weekData.end) {
      setEnableAddWeekButton(false);
      return;
    }
    
    const isOverlap = weeks.some(week => {
      if (week.start > weekData.end || weekData.start > week.end) return false;
      return true;
    })

    if (isOverlap) {
      setEnableAddWeekButton(false);
      return;
    }

    setEnableAddWeekButton(true);
  }, [weekData, weeks])

  const handleRemoveWeek = (index) => {
    const updatedWeeks = weeks.filter((_, i) => i !== index);
    setWeeks(updatedWeeks);
  };

  const handleAddWeek = (event) => {
    setWeeks(prevWeeks => [...prevWeeks, weekData]);
    setWeekData({
      start: '',
      end: ''
    })
  }

  return (
    <Paper elevation={1} style={styles.paper}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Tạo lớp học
        </Typography>
      </div>

      <Paper elevation={3}>
        <div style={styles.content}>
          <div style={styles.firstRow}> 
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Mã lớp</Typography>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="id"
                value={formData.id}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={styles.firstRow}>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Mã môn học</Typography>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="subjectId"
                value={formData.subjectId}
                onChange={handleChange}
              />
            </div>

            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Tên môn học</Typography>
              <TextField
                style={styles.rightTextField}
                variant="outlined"
                name="subjectName"
                value={formData.subjectName}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={styles.firstRow}>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Phòng học</Typography>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="classRoom"
                value={formData.classRoom}
                onChange={handleChange}
              />
            </div>

            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Học kì</Typography>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="semester"
                value={formData.semester}
                disabled
              />
            </div>
          </div>

          <div style={styles.textFieldContainer}>
            <Typography variant="h6">Thời gian</Typography>

            <div style={styles.row}>
              <FormControl variant="standard" style={styles.dropdown}>
                <InputLabel id="ngay-label">Ngày</InputLabel>
                <Select
                  labelId="ngay-label"
                  id="ngay-select"
                  value={formData.day}
                  name="day"
                  onChange={handleChange}
                >
                  {Array.from({ length: 7 }, (_, index) => index + 2).map(
                    (day) => (
                      <MenuItem key={day} value={day}>
                        Thứ {day}
                      </MenuItem>
                    )
                  )}
                </Select>
              </FormControl>

              <FormControl variant="standard" style={styles.dropdown}>
                <InputLabel id="tiet-bat-dau-label">Tiết đầu</InputLabel>
                <Select
                  labelId="tiet-bat-dau-label"
                  id="tiet-bat-dau-select"
                  name="startPeriod"
                  value={formData.startPeriod}
                  onChange={handleChange}
                >
                  {Array.from({ length: 12 }, (_, index) => index + 1).map(
                    (tiet) => (
                      <MenuItem key={tiet} value={tiet}>
                        {tiet}
                      </MenuItem>
                    )
                  )}
                </Select>
              </FormControl>

              <FormControl variant="standard" style={styles.dropdown}>
                <InputLabel id="tiet-cuoi-label">Tiết cuối</InputLabel>
                <Select
                  labelId="tiet-cuoi-label"
                  id="tiet-cuoi-select"
                  name="endPeriod"
                  value={formData.endPeriod}
                  onChange={handleChange}
                >
                  {Array.from({ length: 12 }, (_, index) => index + 1).map(
                    (tiet) => (
                      <MenuItem key={tiet} value={tiet}>
                        {tiet}
                      </MenuItem>
                    )
                  )}
                </Select>
              </FormControl>
            </div>
          </div>

          <div style={{...styles.textFieldContainer, width: "100%"}}>
            <Typography variant="h6">Tuần học</Typography>

            <div style={{...styles.row, width: "60%"}}>
              <FormControl variant="standard" style={styles.weekdropdown}>
                <InputLabel id="week-start-label">Tuần bắt đầu</InputLabel>
                <Select
                  labelId="week-start-label"
                  id="week-start-select"
                  value={weekData.start}  
                  name="start"
                  onChange={handleChangeWeek}
                >
                  {Array.from({ length: 50 }, (_, index) => index + 1).map(
                    (week) => (
                      <MenuItem key={week} value={week}>
                        Tuần {week}
                      </MenuItem>
                    )
                  )}
                </Select>
              </FormControl>

              <FormControl variant="standard" style={styles.weekdropdown}>
                <InputLabel id="week-end-label">Tuần kết thúc</InputLabel>
                <Select
                  labelId="week-end-label"
                  id="tiet-bat-dau-select"
                  value={weekData.end}
                  name="end"
                  onChange={handleChangeWeek}
                >
                  {Array.from({ length: 50 }, (_, index) => index + 1).map(
                    (week) => (
                      <MenuItem key={week} value={week}>
                        Tuần {week}
                      </MenuItem>
                    )
                  )}
                </Select>
              </FormControl>
              
              <Button disabled = {!enableAddWeekButton} variant="contained" color="primary" onClick={handleAddWeek}>
                Thêm
              </Button> 
              
            </div>
            
            <Box style={styles.weekList}>
              {weeks.map((week, index) => (
                    <Paper elevation={3} key={index} style={styles.weekItem}
                    >
                      <Box style={styles.weekItemBox}>
                        <Typography variant="body1">
                          Tuần {week.start} - Tuần {week.end}
                        </Typography>
              
                        <IconButton
                          onClick={() => handleRemoveWeek(index)}
                        >
                          <ClearIcon />
                        </IconButton>
                      </Box>
                  </Paper>
              ))}
            </Box>
          </div>

          <div style={styles.textFieldContainer}>
            <Typography variant="h6">Ghi chú</Typography>
            <TextField
              style={styles.textArea}
              multiline
              rows={4}
              name="note"
              value={formData.note}
              onChange={handleChange}
            />
          </div>

          <div style={styles.button}>
            <Button variant="contained" color="primary" onClick={handleSubmit}>
              Xác nhận
            </Button>
          </div>
        </div>
      </Paper>
    </Paper>
  );
};

export default RegisterClassScreen;
