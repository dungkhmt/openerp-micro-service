import React, { useState, useEffect } from "react";
import { useParams, useHistory } from "react-router-dom";
import { Button, Typography, TextField, Paper, Autocomplete, Checkbox } from "@mui/material";
import { CheckBoxOutlineBlank, CheckBox as CheckedIcon } from "@mui/icons-material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { courseUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";

const UpdateCoursePage = () => {
  const history = useHistory();
  const { courseId } = useParams();
  const [id, setId] = useState("");
  const [courseName, setCourseName] = useState("");
  const [credit, setCredit] = useState("");
  const [prerequisite, setPrerequisites] = useState([]); // State for selected prerequisites
  const [availableCourses, setAvailableCourses] = useState([]); // State for available courses

  useEffect(() => {
    // Fetch course details for updating
    if (courseId) {
      request(
        "get",
        `${courseUrl.getCourseDetail}/${courseId}`,
        (res) => {
          const { id, courseName, credit, prerequisites } = res.data;
          setId(id);
          setCourseName(courseName);
          setCredit(credit);
          setPrerequisites(prerequisites.map((p) => ({ id: p, courseName: "" }))); // Assuming prerequisites are just IDs
        },
        (error) => {
          console.error("Error fetching course details:", error);
          errorNoti("Không thể tải thông tin khóa học", 5000);
        }
      );
    }

    // Fetch available courses for prerequisites selection
    request("get", `${courseUrl.getAllCourse}`, (res) => {
      setAvailableCourses(res.data);
    });
  }, [courseId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!id || !courseName || !credit) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    }

    const prerequisitesArray = prerequisite.map((course) => course.id);

    const updatedCourse = {
      id,
      courseName,
      credit: Number(credit),
      status: "active",
      prerequisites: prerequisitesArray,
    };

    try {
      await request(
        "put",
        `${courseUrl.updateCourse}/${courseId}`,
        (res) => {
          successNoti("Cập nhật khóa học thành công!", 5000);
          history.push(`/training_course/teacher/course/${courseId}`);
        },
        (error) => {
          console.error("Error updating course:", error);
          const errorMessage = error?.response?.data?.message || "Có lỗi xảy ra khi cập nhật khóa học";
          errorNoti(errorMessage, 5000);
        },
        updatedCourse
      );
    } catch (error) {
      console.error("Error during request:", error);
      errorNoti("Có lỗi xảy ra khi gửi yêu cầu", 5000);
    }
  };

  const icon = <CheckBoxOutlineBlank fontSize="small" />;
  const checkedIcon = <CheckedIcon fontSize="small" />;

  return (
    <Paper elevation={3} sx={{ padding: 3 }}>
      <Typography variant="h5" fontWeight="bold" style={updateStyles.title}>
        Chỉnh sửa thông tin khóa học
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
            disabled
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
