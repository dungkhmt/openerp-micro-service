import {
  Paper,
  TextField,
  MenuItem,
  FormControl,
  Select,
  InputLabel,
  Button,
} from "@mui/material";
import { useState } from "react";
import { request } from "../../api";
import { warningNoti, successNoti } from "../../utils/notification";
import { SEMESTER } from "config/localize";
import styles from "./index.style";

const RegisterClassScreen = () => {
  const [formData, setFormData] = useState({
    day: "",
    startPeriod: "",
    endPeriod: "",
    subjectId: "",
    subjectName: "",
    classRoom: "",
    note: "",
    semester: SEMESTER,
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
    if (
      !formData.day ||
      !formData.startPeriod ||
      !formData.endPeriod ||
      !formData.subjectId ||
      !formData.subjectName ||
      !formData.classRoom ||
      !formData.semester
    ) {
      warningNoti("Vui lòng điền đầy đủ thông tin");
      return;
    } else if (formData.startPeriod >= formData.endPeriod) {
      warningNoti("Tiết bắt đầu phải nhỏ hơn tiết kết thúc");
      return;
    } else {
      request(
        "post",
        "/class-call/create-class",
        (res) => {
          console.log(res.data);
        },
        {},
        formData
      );
      successNoti("Tạo lớp học thành công");
    }
  };

  return (
    <Paper elevation={1} style={{ padding: "1em" }}>
      <div style={styles.tableToolBar}>
        <h1>Tạo lớp học</h1>
      </div>

      <Paper elevation={3}>
        <div style={styles.content}>
          <div style={styles.firstRow}>
            <div style={styles.textFieldContainer}>
              <h3>Mã lớp</h3>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="subjectId"
                value={formData.subjectId}
                onChange={handleChange}
              />
            </div>

            <div style={styles.textFieldContainer}>
              <h3>Tên lớp</h3>
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
              <h3>Phòng học</h3>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="classRoom"
                value={formData.classRoom}
                onChange={handleChange}
              />
            </div>

            <div style={styles.textFieldContainer}>
              <h3>Học kì</h3>
              <TextField
                style={styles.textField}
                variant="outlined"
                name="semester"
                value={formData.semester}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={styles.textFieldContainer}>
            <h3>Thời gian</h3>

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

          <div style={styles.textFieldContainer}>
            <h3>Ghi chú</h3>
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
