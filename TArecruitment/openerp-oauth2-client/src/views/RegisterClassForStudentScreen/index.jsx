import { Paper, TextField, Button } from "@mui/material";
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
      <h1>Đăng ký lớp trợ giảng</h1>

      <Paper elevation={3}>
        <div style={styles.content}>
          <div style={styles.row}>
            <div style={styles.textFieldContainer}>
              <h3>Họ tên</h3>
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
              <h3>Mã số sinh viên</h3>
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
              <h3>Email</h3>
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
              <h3>Số điện thoại</h3>
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
              <h3>CPA</h3>
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
              <h3>Chứng chỉ tiếng Anh</h3>
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
            <h3>Mã lớp đăng ký</h3>
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
            <h3>Ghi chú</h3>
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
