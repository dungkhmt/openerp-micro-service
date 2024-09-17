import {
  Paper,
  TextField,
  MenuItem,
  FormControl,
  Select,
  InputLabel,
  Button,
  Typography,
} from "@mui/material";
import { useState, useEffect } from "react";
import styles from "./index.style";
import { useParams, useHistory, useLocation } from "react-router-dom";
import { request } from "api";
import { successNoti, warningNoti } from "utils/notification";
import { classCallUrl } from "../apiURL";

const ClassInformationScreen = () => {
  const location = useLocation();
  const history = useHistory();
  const { id } = useParams();
  const [isEdited, setIsEdited] = useState(location.state?.isEdited || false);
  const [formData, setFormData] = useState({
    id: "",
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
    request("get", `${classCallUrl.getClassById}/${id}`, (res) => {
      setFormData(res.data);
      setOldFormData(res.data);
    }).then();
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
    if (
      !formData.day ||
      !formData.startPeriod ||
      !formData.endPeriod ||
      !formData.subjectId ||
      !formData.subjectName ||
      !formData.classRoom ||
      !formData.semester ||
      !formData.id
    ) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    } else if (formData.startPeriod >= formData.endPeriod) {
      warningNoti("Tiết bắt đầu phải nhỏ hơn tiết kết thúc", 5000);
      return;
    } else {
      setOldFormData({ ...formData });
      request(
        "put",
        `${classCallUrl.updateClass}/${id}`,
        (res) => {},
        {},
        formData
      );
      successNoti("Cập nhật thông tin lớp học thành công!", 5000);
      setIsEdited(false);
    }
  };

  const handleCancelEdit = () => {
    setFormData({ ...oldFormData });
    setIsEdited(false);
  };

  const handleGoBack = () => {
    history.goBack();
  };

  return (
    <Paper elevation={1} style={styles.paper}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Thông tin lớp học
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
                disabled={!isEdited}
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
                disabled={!isEdited}
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
                disabled={!isEdited}
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
                disabled={!isEdited}
              />
            </div>

            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Học kì</Typography>
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
            <Typography variant="h6">Ghi chú</Typography>
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
    </Paper>
  );
};

export default ClassInformationScreen;
