import {
  Paper,
  TextField,
  MenuItem,
  FormControl,
  Select,
  InputLabel,
  Button,
} from "@mui/material";
import { useState, useEffect } from "react";
import styles from "./index.style";
import { useParams, useHistory } from "react-router-dom";
import { request } from "api";
import { successNoti } from "utils/notification";

const ClassInformationScreen = () => {
  const history = useHistory();
  const { id } = useParams();
  const [isEdited, setIsEdited] = useState(false);
  const [formData, setFormData] = useState({
    day: "",
    startPeriod: "",
    endPeriod: "",
    subjectId: "",
    subjectName: "",
    classRoom: "",
    note: "",
    semester: "",
  });
  const [oldFormData, setOldFormData] = useState({ ...formData });

  useEffect(() => {
    request("get", `/class-call/get-class/${id}`, (res) => {
      setFormData(res.data);
      setOldFormData(res.data);
    }).then();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleEdit = () => {
    setIsEdited(true);
  };

  const handleUpdate = () => {
    /**
     * @todo: Checking data before updating if it's empty or not
     */
    setOldFormData({ ...formData });
    request("put", `/class-call/update-class/${id}`, (res) => {}, {}, formData);
    successNoti("Cập nhật thông tin lớp học thành công!");
    setIsEdited(false);
  };

  const handleCancelEdit = () => {
    setFormData({ ...oldFormData });
    setIsEdited(false);
  };

  const handleGoBack = () => {
    history.goBack();
  };

  return (
    <div>
      <h1>Thông tin lớp học</h1>
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
                disabled={!isEdited}
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
                disabled={!isEdited}
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
                disabled={!isEdited}
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
                disabled={!isEdited}
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
                  disabled={!isEdited}
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
                  disabled={!isEdited}
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
                  disabled={!isEdited}
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
              disabled={!isEdited}
            />
          </div>

          <div style={styles.buttonRow}>
            <div style={styles.button}>
              <Button
                variant="contained"
                color="primary"
                onClick={isEdited ? handleUpdate : handleEdit}
              >
                {isEdited ? "Lưu" : "Sửa"}
              </Button>
            </div>
            <div style={styles.rightButton}>
              <Button
                variant="contained"
                color="error"
                onClick={isEdited ? handleCancelEdit : handleGoBack}
              >
                {isEdited ? "Hủy" : "Quay lại"}
              </Button>
            </div>
          </div>
        </div>
      </Paper>
    </div>
  );
};

export default ClassInformationScreen;
