import { request } from "api";
import { useEffect, useState } from "react";
import { successNoti, warningNoti } from "utils/notification";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  TextField,
} from "@mui/material";

import { updateStyles } from "./index.style";
import { applicationUrl } from "../apiURL";

const UpdateApplicationDialog = ({
  open,
  handleClose,
  applicationId,
  fetchData,
}) => {
  const [formData, setFormData] = useState({
    name: "",
    mssv: "",
    phoneNumber: "",
    email: "",
    cpa: "",
    englishScore: "",
    note: "",
  });

  useEffect(() => {
    if (open && applicationId !== "") {
      request(
        "get",
        `${applicationUrl.getApplicationById}/${applicationId}`,
        (res) => {
          const { name, mssv, phoneNumber, email, cpa, englishScore, note } =
            res.data;
          setFormData({
            name,
            mssv,
            phoneNumber,
            email,
            cpa,
            englishScore,
            note,
          });
        }
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [applicationId, open]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleUpdateApplication = () => {
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
        "put",
        `${applicationUrl.updateApplication}/${applicationId}`,
        (res) => {
          successNoti("Cập nhật ứng tuyển thành công!", 5000);
          fetchData();
          handleClose();
        },
        {},
        formData
      );
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      fullWidth
      maxWidth="lg"
      sx={{
        "& .MuiDialog-paper": {
          height: 700,
        },
      }}
    >
      <DialogTitle style={updateStyles.dialogTitle}>
        <Typography variant="h5" fontWeight="bold">
          Chỉnh sửa trạng thái ứng tuyển
        </Typography>
      </DialogTitle>
      <DialogContent>
        <div style={updateStyles.row}>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Họ tên</Typography>
            <TextField
              name="name"
              variant="outlined"
              style={updateStyles.textField}
              value={formData.name}
              onChange={handleChange}
              placeholder="Nguyễn Văn A"
            />
          </div>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Mã số sinh viên</Typography>
            <TextField
              name="mssv"
              variant="outlined"
              style={updateStyles.textField}
              value={formData.mssv}
              onChange={handleChange}
              placeholder="20200000"
            />
          </div>
        </div>

        <div style={updateStyles.row}>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Email</Typography>
            <TextField
              name="email"
              variant="outlined"
              style={updateStyles.textField}
              value={formData.email}
              onChange={handleChange}
              placeholder="email@gmail.com"
            />
          </div>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Số điện thoại</Typography>
            <TextField
              name="phoneNumber"
              variant="outlined"
              style={updateStyles.textField}
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="0912345678"
            />
          </div>
        </div>
        <div style={updateStyles.row}>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">CPA</Typography>
            <TextField
              name="cpa"
              variant="outlined"
              style={updateStyles.textField}
              value={formData.cpa}
              onChange={handleChange}
              placeholder="4.0"
            />
          </div>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Chứng chỉ tiếng Anh</Typography>
            <TextField
              name="englishScore"
              variant="outlined"
              style={updateStyles.textField}
              value={formData.englishScore}
              onChange={handleChange}
              placeholder="990 TOEIC"
            />
          </div>
        </div>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Ghi chú</Typography>
          <TextField
            name="note"
            variant="outlined"
            value={formData.note}
            onChange={handleChange}
            style={updateStyles.textArea}
            multiline
            rows={4}
          />
        </div>
      </DialogContent>
      <DialogActions>
        <Button
          variant="contained"
          color="success"
          onClick={handleUpdateApplication}
        >
          Sửa
        </Button>
        <Button color="error" variant="contained" onClick={handleClose}>
          Quay lại
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UpdateApplicationDialog;
