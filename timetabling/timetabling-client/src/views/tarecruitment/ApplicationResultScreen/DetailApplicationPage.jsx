import React, { useEffect, useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import { Button, Typography, Paper, TextField } from "@mui/material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { applicationUrl } from "../apiURL";

const ApplicationDetailPage = () => {
  const history = useHistory();
  const { applicationId } = useParams();
  const [applicationDetails, setApplicationDetails] = useState(null);

  useEffect(() => {
    if (applicationId) {
      request(
        "get",
        `${applicationUrl.getApplicationById}/${applicationId}`,
        (res) => {
          setApplicationDetails(res.data);
        }
      );
    }
  }, [applicationId]);

  if (!applicationDetails) return <div>Loading...</div>;

  return (
    <Paper elevation={3} sx={{ padding: 3 }}>
      <Typography variant="h5" fontWeight="bold" style={updateStyles.title}>
        Thông tin đơn xin trợ giảng{" "}
      </Typography>

      <div style={updateStyles.row}>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Họ tên</Typography>
          <TextField
            value={applicationDetails.name || ""}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nguyễn Văn A"
            disabled
          />
        </div>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Mã số sinh viên</Typography>
          <TextField
            value={applicationDetails.mssv || ""}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="20200000"
            disabled
          />
        </div>
      </div>

      <div style={updateStyles.row}>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Email</Typography>
          <TextField
            value={applicationDetails.email || ""}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="email@gmail.com"
            disabled
          />
        </div>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Số điện thoại</Typography>
          <TextField
            value={applicationDetails.phoneNumber || ""}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="0912345678"
            disabled
          />
        </div>
      </div>

      <div style={updateStyles.row}>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">CPA</Typography>
          <TextField
            value={applicationDetails.cpa || ""}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="4.0"
            disabled
          />
        </div>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Chứng chỉ tiếng Anh</Typography>
          <TextField
            value={applicationDetails.englishScore || ""}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="990 TOEIC"
            disabled
          />
        </div>
      </div>

      <div style={updateStyles.textFieldContainer}>
        <Typography variant="h6">Ghi chú</Typography>
        <TextField
          value={applicationDetails.note || ""}
          variant="outlined"
          style={updateStyles.textArea}
          multiline
          rows={4}
          disabled
        />
      </div>

      <div style={updateStyles.buttonGroup}>
        <Button
          variant="contained"
          style={{ backgroundColor: "#0099FF", color: "white" }}
          onClick={() =>
            history.push(`/ta-recruitment/student/result/${applicationId}/edit`)
          }
        >
          Chỉnh sửa
        </Button>
        <Button
          color="error"
          variant="contained"
          onClick={() => history.push(`/ta-recruitment/student/result/`)}
        >
          Quay lại
        </Button>
      </div>
    </Paper>
  );
};

export default ApplicationDetailPage;
