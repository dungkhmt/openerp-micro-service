import React, { useState } from "react";
import { useHistory } from "react-router-dom";
import { Button, Typography, Paper, TextField } from "@mui/material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { courseUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";

const AddCourseScreen = () => {
  const history = useHistory();
  const [id, setId] = useState("");
  const [courseName, setCourseName] = useState("");
  const [credit, setCredit] = useState("");
  const [prerequisite, setPrerequisites] = useState(""); 

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!id || !courseName || !credit) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    } 

    const prerequisitesArray = prerequisite
      .split(',')
      .map(prerequisite => prerequisite.trim())
      .filter(prerequisite => prerequisite.length > 0); 
  
    const newCourse = {
      id,
      courseName,
      credit: Number(credit),
      status: "active", 
      prerequisites: prerequisitesArray, 
    };
  
    try {
      await request(
        "post", 
        `${courseUrl.createCourse}`, 
        (res) => {
          successNoti("Khóa học đã được thêm thành công!", 5000);
          history.push("/training_course/teacher/course");
        },
        (error) => {
          console.log(error);
          const errorMessage = error?.response?.data?.message || "Có lỗi xảy ra khi thêm khóa học";
          errorNoti(errorMessage, 5000);
        },
        newCourse
      );
    } catch (error) {
      console.log(error);
      errorNoti("Có lỗi xảy ra khi gửi yêu cầu", 5000);
    }
  };

  return (
    <Paper elevation={3} sx={{ padding: 3 }}>
      <Typography variant="h5" fontWeight="bold" style={updateStyles.title}>
        Thêm Mới Học Phần
      </Typography>

      <form onSubmit={handleSubmit}>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Mã học phần</Typography>
          <TextField
            value={id}
            onChange={(e) => setId(e.target.value)}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập mã môn học"
            required
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Tên học phần</Typography>
          <TextField
            value={courseName}
            onChange={(e) => setCourseName(e.target.value)}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập tên học phần"
            required
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Số tín chỉ</Typography>
          <TextField
            type="number"
            value={credit}
            onChange={(e) => setCredit(e.target.value)}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập số tín chỉ"
            required
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Học phần tiên quyết</Typography>
          <TextField
            value={prerequisite}
            onChange={(e) => setPrerequisites(e.target.value)}
            variant="outlined"
            placeholder="Nhập các học phần tiên quyết, ngăn cách bởi dấu phẩy"
            style={updateStyles.textField}
          />
        </div>

        <div style={updateStyles.buttonGroup}>
          <Button
            type="submit"
            variant="contained"
            style={{ backgroundColor: "#0099FF", color: "white" }}
          >
            Thêm mới
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => history.push("/training_course/teacher/course")}
          >
            Hủy bỏ
          </Button>
        </div>
      </form>
    </Paper>
  );
};

export default AddCourseScreen;
