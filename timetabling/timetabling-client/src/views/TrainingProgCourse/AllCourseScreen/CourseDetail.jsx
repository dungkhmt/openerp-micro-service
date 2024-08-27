import React, { useEffect, useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import { Button, Typography, Paper, TextField } from "@mui/material";
import { updateStyles } from "./index.style";
import { request } from "api";
import { courseUrl } from "../apiURL";

const CourseDetailPage = () => {
  const history = useHistory();
  const { courseId } = useParams();
  const [courseDetails, setCourseDetails] = useState(null);

  useEffect(() => {
    if (courseId) {
      request(
        "get",
        `${courseUrl.getCourseDetail}/${courseId}`,
        (res) => {
          setCourseDetails(res.data);
        }
      );
    }
  }, [courseId]);

  if (!courseDetails) return <div>Loading...</div>;

  return (
    <Paper elevation={3} sx={{ padding: 3 }}>
      <Typography variant="h5" fontWeight="bold" style={updateStyles.title}>
        Thông tin học phần
      </Typography>

      <div style={updateStyles.textFieldContainer}>
        <Typography variant="h6">Mã học phần</Typography>
        <TextField
          value={courseDetails.id || ""}
          variant="outlined"
          style={updateStyles.textField}
          placeholder="CS101"
          disabled
        />
      </div>

      <div style={updateStyles.textFieldContainer}>
        <Typography variant="h6">Tên học phần</Typography>
        <TextField
          value={courseDetails.courseName || ""}
          variant="outlined"
          style={updateStyles.textField}
          placeholder="Lập trình Java"
          disabled
        />
      </div>

      <div style={updateStyles.textFieldContainer}>
        <Typography variant="h6">Số tín chỉ</Typography>
        <TextField
          type="number"
          value={courseDetails.credit || ""}
          variant="outlined"
          style={updateStyles.textField}
          placeholder="4"
          disabled
        />
      </div>

      <div style={updateStyles.textFieldContainer}>
        <Typography variant="h6">Học phần tiên quyết</Typography>
        <TextField
          value={courseDetails.prerequisites ? courseDetails.prerequisites.join(", ") : ""}
          variant="outlined"
          style={updateStyles.textField}
          placeholder="CS100, CS102"
          disabled
        />
      </div>

      <div style={updateStyles.buttonGroup}>
        <Button
          variant="contained"
          style={{ backgroundColor: "#0099FF", color: "white" }}
          onClick={() =>
            history.push(`/training_course/teacher/course/${courseId}/edit`)
          }
        >
          Chỉnh sửa
        </Button>
        <Button
          color="error"
          variant="contained"
          onClick={() => history.push(`/training_course/teacher/course`)}
        >
          Quay lại
        </Button>
      </div>
    </Paper>
  );
};

export default CourseDetailPage;
