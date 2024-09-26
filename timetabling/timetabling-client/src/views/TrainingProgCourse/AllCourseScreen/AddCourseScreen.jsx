import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { Button, Typography, Paper, TextField, Autocomplete, Checkbox } from "@mui/material";
import { CheckBoxOutlineBlank, CheckBox as CheckedIcon } from "@mui/icons-material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { courseUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";

const AddCourseScreen = () => {
  const history = useHistory();
  const [id, setId] = useState("");
  const [courseName, setCourseName] = useState("");
  const [credit, setCredit] = useState("");
  const [prerequisite, setPrerequisites] = useState([]); // Updated state for selected prerequisites
  const [availableCourses, setAvailableCourses] = useState([]); // State for available courses

  useEffect(() => {
    request("get", `${courseUrl.getAllCourse}`, (res) => {
      setAvailableCourses(res.data); // Assumes res.data contains the list of available courses
    });
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!id || !courseName || !credit) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    }

    const prerequisitesArray = prerequisite.map((course) => course.id);

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

  const icon = <CheckBoxOutlineBlank fontSize="small" />;
  const checkedIcon = <CheckedIcon fontSize="small" />;

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
          <Autocomplete
            multiple
            options={availableCourses}
            disableCloseOnSelect
            getOptionLabel={(option) => `${option.id} : ${option.courseName}`}
            value={prerequisite}
            onChange={(event, newValue) => {
              setPrerequisites(newValue);
            }}
            renderOption={(props, option, { selected }) => (
              <li {...props}>
                <Checkbox
                  icon={icon}
                  checkedIcon={checkedIcon}
                  style={{ marginRight: 8 }}
                  checked={selected}
                />
                {`${option.id} : ${option.courseName}`}
              </li>
            )}
            renderInput={(params) => (
              <TextField
                {...params}
                variant="outlined"
                placeholder="Chọn học phần tiên quyết"
                style={updateStyles.textField}
              />
            )}
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
