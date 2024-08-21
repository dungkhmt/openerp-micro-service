import { useEffect } from "react";
import { useParams, useHistory } from "react-router-dom";
import { useForm } from "react-hook-form";
import { successNoti, warningNoti } from "utils/notification";
import { Button, Typography, TextField, Paper } from "@mui/material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { applicationUrl } from "../apiURL";

const UpdateApplicationPage = () => {
  const history = useHistory();
  const { applicationId } = useParams();
  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    if (applicationId) {
      request(
        "get",
        `${applicationUrl.getApplicationById}/${applicationId}`,
        (res) => {
          const { name, mssv, phoneNumber, email, cpa, englishScore, note } =
            res.data;

          setValue("name", name);
          setValue("mssv", mssv);
          setValue("phoneNumber", phoneNumber);
          setValue("email", email);
          setValue("cpa", cpa);
          setValue("englishScore", englishScore);
          setValue("note", note);
        }
      );
    }
  }, [applicationId, setValue]);

  const onSubmit = (data) => {
    if (data.cpa > 4.0) {
      warningNoti("CPA không thể lớn hơn 4.0", 5000);
      return;
    }

    request(
      "put",
      `${applicationUrl.updateApplication}/${applicationId}`,
      (res) => {
        successNoti("Cập nhật ứng tuyển thành công!", 5000);
        history.push(`/ta-recruitment/student/result/${applicationId}`);
      },
      {},
      data
    );
  };

  return (
    <Paper elevation={3} sx={{ padding: 3 }}>
      <Typography variant="h5" fontWeight="bold" style={updateStyles.title}>
        Chỉnh sửa thông tin
      </Typography>

      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={updateStyles.row}>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Họ tên</Typography>
            <TextField
              {...register("name", { required: "Họ tên là bắt buộc" })}
              variant="outlined"
              style={updateStyles.textField}
              placeholder="Nguyễn Văn A"
              error={!!errors.name}
              helperText={errors.name ? errors.name.message : ""}
            />
          </div>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Mã số sinh viên</Typography>
            <TextField
              {...register("mssv", { required: "MSSV là bắt buộc" })}
              variant="outlined"
              style={updateStyles.textField}
              placeholder="20200000"
              error={!!errors.mssv}
              helperText={errors.mssv ? errors.mssv.message : ""}
            />
          </div>
        </div>

        <div style={updateStyles.row}>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Email</Typography>
            <TextField
              {...register("email", {
                required: "Email là bắt buộc",
                pattern: {
                  value: /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/i,
                  message: "Email không hợp lệ",
                },
              })}
              variant="outlined"
              style={updateStyles.textField}
              placeholder="email@gmail.com"
              error={!!errors.email}
              helperText={errors.email ? errors.email.message : ""}
            />
          </div>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Số điện thoại</Typography>
            <TextField
              {...register("phoneNumber", {
                required: "Số điện thoại là bắt buộc",
                pattern: {
                  value: /^[0-9]{10}$/,
                  message: "Số điện thoại không hợp lệ",
                },
              })}
              variant="outlined"
              style={updateStyles.textField}
              placeholder="0912345678"
              error={!!errors.phoneNumber}
              helperText={errors.phoneNumber ? errors.phoneNumber.message : ""}
            />
          </div>
        </div>

        <div style={updateStyles.row}>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">CPA</Typography>
            <TextField
              {...register("cpa", {
                required: "CPA là bắt buộc",
                validate: (value) =>
                  value <= 4.0 || "CPA không thể lớn hơn 4.0",
              })}
              variant="outlined"
              style={updateStyles.textField}
              placeholder="4.0"
              error={!!errors.cpa}
              helperText={errors.cpa ? errors.cpa.message : ""}
            />
          </div>
          <div style={updateStyles.textFieldContainer}>
            <Typography variant="h6">Chứng chỉ tiếng Anh</Typography>
            <TextField
              {...register("englishScore")}
              variant="outlined"
              style={updateStyles.textField}
              placeholder="990 TOEIC"
            />
          </div>
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Ghi chú</Typography>
          <TextField
            {...register("note")}
            variant="outlined"
            style={updateStyles.textArea}
            multiline
            rows={4}
          />
        </div>

        <div style={updateStyles.buttonGroup}>
          <Button
            variant="contained"
            style={{ backgroundColor: "#0099FF", color: "white" }}
            type="submit"
          >
            Sửa
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() =>
              history.push(`/ta-recruitment/student/result/${applicationId}`)
            }
          >
            Quay lại
          </Button>
        </div>
      </form>
    </Paper>
  );
};

export default UpdateApplicationPage;
