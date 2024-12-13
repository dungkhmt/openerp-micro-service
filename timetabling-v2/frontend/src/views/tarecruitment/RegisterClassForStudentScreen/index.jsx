import { Paper, TextField, Button, Typography } from "@mui/material";
import styles from "./index.style";
import { useEffect, useState } from "react";
import { successNoti, warningNoti } from "utils/notification";
import { request } from "api";
import { useHistory } from "react-router-dom";
import { userUrl, applicationUrl } from "../apiURL";

const RegisterClassForStudentScreen = (props) => {
  const history = useHistory();
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

  useEffect(() => {
    request("get", `${userUrl.getUserInfo}`, (res) => {
      console.log(res.data);
      setFormData((prevData) => ({
        ...prevData,
        email: res.data.email,
      }));
    });
  }, []);

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
    if (
      formData.name === "" ||
      formData.mssv === "" ||
      formData.phoneNumber === "" ||
      formData.email === "" ||
      formData.cpa === ""
    ) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
    } else {
      request(
        "post",
        `${applicationUrl.createApplication}`,
        (res) => {
          successNoti("Đăng ký lớp trợ giảng thành công", 5000);
          setDataEmpty();
          history.push("/ta-recruitment/student/result");
        },
        {},
        formData
      );
    }
  };

  return (
    <Paper elevation={3}>
      <div>
        <Typography variant="h4" style={styles.title}>
          Đăng ký lớp trợ giảng
        </Typography>

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
      </div>
    </Paper>
  );
};

export default RegisterClassForStudentScreen;
