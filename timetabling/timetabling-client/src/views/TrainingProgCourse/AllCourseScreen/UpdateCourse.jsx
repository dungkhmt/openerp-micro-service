import React, { useEffect } from "react";
import { useParams, useHistory } from "react-router-dom";
import { useForm } from "react-hook-form";
import { successNoti, warningNoti } from "utils/notification";
import { Button, Typography, TextField, Paper } from "@mui/material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { courseUrl } from "../apiURL";

const UpdateCoursePage = () => {
  const history = useHistory();
  const { courseId } = useParams();
  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    if (courseId) {
      request(
        "get",
        `${courseUrl.getCourseDetail}/${courseId}`,
        (res) => {
          const { id, courseName, credit, prerequisites } = res.data;

          setValue("id", id);
          setValue("courseName", courseName);
          setValue("credit", credit);
          setValue("prerequisites", prerequisites.join(", ")); // Chuyển đổi mảng thành chuỗi để hiển thị
        }
      );
    }
  }, [courseId, setValue]);

  const onSubmit = (data) => {
    if (!data.id || !data.courseName || !data.credit) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    }

    const prerequisitesArray = data.prerequisites
      .split(',')
      .map(prerequisite => prerequisite.trim())
      .filter(prerequisite => prerequisite.length > 0);

    const updatedCourse = {
      ...data,
      prerequisites: prerequisitesArray, // Chuyển đổi chuỗi thành mảng trước khi gửi lên server
    };

    request(
      "put",
      `${courseUrl.updateCourse}/${courseId}`,
      (res) => {
        successNoti("Cập nhật khóa học thành công!", 5000);
        history.push(`/training_course/teacher/course/${courseId}`);
      },
      {},
      updatedCourse
    );
  };

  return (
    <Paper elevation={3} sx={{ padding: 3 }}>
      <Typography variant="h5" fontWeight="bold" style={updateStyles.title}>
        Chỉnh sửa thông tin khóa học
      </Typography>

      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Mã học phần</Typography>
          <TextField
            {...register("id", { required: "Mã học phần là bắt buộc" })}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập mã môn học"
            error={!!errors.id}
            helperText={errors.id ? errors.id.message : ""}
            disabled
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Tên học phần</Typography>
          <TextField
            {...register("courseName", { required: "Tên học phần là bắt buộc" })}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập tên học phần"
            error={!!errors.courseName}
            helperText={errors.courseName ? errors.courseName.message : ""}
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Số tín chỉ</Typography>
          <TextField
            {...register("credit", {
              required: "Số tín chỉ là bắt buộc",
              validate: value => value > 0 || "Số tín chỉ phải lớn hơn 0",
            })}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập số tín chỉ"
            type="number"
            error={!!errors.credit}
            helperText={errors.credit ? errors.credit.message : ""}
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Học phần tiên quyết</Typography>
          <TextField
            {...register("prerequisites")}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập các học phần tiên quyết, ngăn cách bởi dấu phẩy"
          />
        </div>

        <div style={updateStyles.buttonGroup}>
          <Button
            variant="contained"
            style={{ backgroundColor: "#0099FF", color: "white" }}
            type="submit"
          >
            Cập nhật
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => history.push(`/training_course/teacher/course/${courseId}`)}
          >
            Quay lại
          </Button>
        </div>
      </form>
    </Paper>
  );
};

export default UpdateCoursePage;
