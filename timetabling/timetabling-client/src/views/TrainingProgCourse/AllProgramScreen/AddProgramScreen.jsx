import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { Button, Typography, Paper, TextField, Autocomplete, Checkbox } from "@mui/material";
import { CheckBoxOutlineBlank, CheckBox as CheckedIcon } from "@mui/icons-material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { programUrl, courseUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";

const AddProgramScreen = () => {
  const history = useHistory();
  const [id, setId] = useState("");
  const [name, setName] = useState("");
  const [courses, setCourses] = useState([]);
  const [selectedCourses, setSelectedCourses] = useState([]);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await request("get", `${courseUrl.getAllCourse}`);
        setCourses(response.data);
      } catch (error) {
        console.error("Error fetching courses:", error);
      }
    };

    fetchCourses();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!id || !name) {
      warningNoti("Vui lòng điền đầy đủ thông tin", 5000);
      return;
    }

    const courseIds = selectedCourses.map((course) => course.id);

    const newProgram = {
        id,
        name,
        createStamp: new Date(), 
        lastUpdated: new Date(), 
        courses: courseIds,
    };

    try {
      await request(
        "post",
        `${programUrl.createProgram}`,
        (res) => {
          successNoti("Chương trình đã được thêm thành công!", 5000);
          history.push("/training_course/teacher/course");
        },
        (error) => {
          console.log(error);
          const errorMessage = error?.response?.data?.message || "Có lỗi xảy ra khi thêm chương trình";
          errorNoti(errorMessage, 5000);
        },
        newProgram
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
        Thêm Mới Chương Trình
      </Typography>

      <form onSubmit={handleSubmit}>
        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Mã chương trình</Typography>
          <TextField
            value={id}
            onChange={(e) => setId(e.target.value)}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập mã chương trình"
            required
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Tên chương trình</Typography>
          <TextField
            value={name}
            onChange={(e) => setName(e.target.value)}
            variant="outlined"
            style={updateStyles.textField}
            placeholder="Nhập tên chương trình"
            required
          />
        </div>

        <div style={updateStyles.textFieldContainer}>
          <Typography variant="h6">Học phần trong chương trình</Typography>
          <Autocomplete
            multiple
            options={courses}
            disableCloseOnSelect
            getOptionLabel={(option) => `${option.id} : ${option.courseName}`}
            value={selectedCourses}
            onChange={(event, newValue) => {
              setSelectedCourses(newValue);
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
                placeholder="Chọn học phần trong chương trình"
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

export default AddProgramScreen;
