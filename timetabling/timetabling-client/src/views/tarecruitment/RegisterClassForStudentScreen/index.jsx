import { Paper, TextField, Button, Typography } from "@mui/material";
import styles from "./index.style";
import { useState } from "react";
import { successNoti } from "utils/notification";
import { request } from "api";

const RegisterClassForStudentScreen = (props) => {
  const { classId } = props.location.state ? props.location.state : "";

  const [formData, setFormData] = useState({
    classCall: {
      id: classId,
    },
    user: {
      id: "",
    },
    name: "",
    mssv: "",
    phoneNumber: "",
    email: "",
    cpa: "",
    englishScore: "",
    note: "",
  });

  const setDataEmpty = () => {
    setFormData({
      classCall: {
        id: classId,
      },
      user: {
        id: "",
      },
      name: "",
      mssv: "",
      phoneNumber: "",
      email: "",
      cpa: "",
      englishScore: "",
      note: "",
    });
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    if (name === "classId") {
      setFormData((prevData) => ({
        ...prevData,
        classCall: {
          id: value,
        },
      }));
    } else {
      setFormData((prevData) => ({
        ...prevData,
        [name]: value,
      }));
    }
  };

  const handleSubmit = () => {
    request(
      "post",
      "/application/create-application",
      (res) => {
        console.log(res.data);
      },
      {},
      formData
    );
    successNoti("Đăng ký lớp trợ giảng thành công");
    setDataEmpty();
  };

  return (
    <div>
      <Typography variant="h4" style={{ fontWeight: "bold" }}>
        Đăng ký lớp trợ giảng
      </Typography>

      <Paper elevation={3}>
        <div style={styles.content}>
          <div style={styles.row}>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Họ tên</Typography>
              <TextField
                name="name"
                variant="outlined"
                style={styles.textField}
                value={formData.name}
                onChange={handleChange}
                placeholder="Nguyễn Văn A"
              />
            </div>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Mã số sinh viên</Typography>
              <TextField
                name="mssv"
                variant="outlined"
                style={styles.textField}
                value={formData.mssv}
                onChange={handleChange}
                placeholder="20200000"
              />
            </div>
          </div>

          <div style={styles.row}>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Email</Typography>
              <TextField
                name="email"
                variant="outlined"
                style={styles.textField}
                value={formData.email}
                onChange={handleChange}
                placeholder="email@gmail.com"
              />
            </div>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Số điện thoại</Typography>
              <TextField
                name="phoneNumber"
                variant="outlined"
                style={styles.textField}
                value={formData.phoneNumber}
                onChange={handleChange}
                placeholder="0912345678"
              />
            </div>
          </div>
          <div style={styles.row}>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">CPA</Typography>
              <TextField
                name="cpa"
                variant="outlined"
                style={styles.textField}
                value={formData.cpa}
                onChange={handleChange}
                placeholder="4.0"
              />
            </div>
            <div style={styles.textFieldContainer}>
              <Typography variant="h6">Chứng chỉ tiếng Anh</Typography>
              <TextField
                name="englishScore"
                variant="outlined"
                style={styles.textField}
                value={formData.englishScore}
                onChange={handleChange}
                placeholder="990 TOEIC"
              />
            </div>
          </div>

          <div style={styles.textFieldContainer}>
            <Typography variant="h6">Mã lớp đăng ký</Typography>
            <TextField
              name="classId"
              variant="outlined"
              style={styles.textField}
              value={formData.classCall.id}
              onChange={handleChange}
              disabled
            />
          </div>

          <div style={styles.textFieldContainer}>
            <Typography variant="h6">Ghi chú</Typography>
            <TextField
              name="note"
              variant="outlined"
              value={formData.note}
              onChange={handleChange}
              style={styles.textArea}
              multiline
              rows={4}
            />
          </div>

          <div style={styles.button}>
            <Button variant="contained" color="primary" onClick={handleSubmit}>
              Xác nhận
            </Button>
          </div>
        </div>
      </Paper>
    </div>
  );
};

export default RegisterClassForStudentScreen;