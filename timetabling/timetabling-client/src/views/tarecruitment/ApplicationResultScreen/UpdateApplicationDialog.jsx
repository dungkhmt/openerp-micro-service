import { request } from "api";
import { useEffect, useState } from "react";
import { successNoti } from "utils/notification";

const {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  TextField,
} = require("@mui/material");

const styles = {
  textFieldContainer: {
    width: "50%",
    padding: "0 1%",
    marginBottom: "1em",
  },
  textField: {
    width: "100%",
  },
  row: {
    display: "flex",
    flexDirection: "row",
  },
  textArea: {
    width: "205%",
  },
};

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
        `/application/get-application-by-id/${applicationId}`,
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

  const handleUpdateApplication = () => [
    request(
      "put",
      `/application/update-application/${applicationId}`,
      (res) => {
        successNoti("Cập nhật ứng tuyển thành công!", 5000);
        fetchData();
        handleClose();
      },
      {},
      formData
    ),
  ];

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
      <DialogTitle>Chỉnh sửa trạng thái ứng tuyển</DialogTitle>
      <DialogContent>
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
      </DialogContent>
      <DialogActions>
        <Button
          variant="contained"
          color="success"
          onClick={handleUpdateApplication}
        >
          Sửa
        </Button>
        <Button variant="contained" onClick={handleClose}>
          Quay lại
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default UpdateApplicationDialog;
