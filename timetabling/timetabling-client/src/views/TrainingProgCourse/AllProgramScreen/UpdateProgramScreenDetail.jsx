import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
  MenuItem,
  Select,
  FormControl,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { useParams, useHistory } from "react-router-dom";
import { styles } from "./index.style";
import { programUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";
import AddCourseDialog from './AddCourseDialog'; 

const UpdateProgramPage = () => {
  const { programId } = useParams();
  const [courses, setCourses] = useState([]);
  const [semesterUpdates, setSemesterUpdates] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [programName, setProgramName] = useState("");
  const [dialogOpen, setDialogOpen] = useState(false); // State to control dialog open/close

  const history = useHistory();

  useEffect(() => {
    setIsLoading(true);
    request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
      if (res && res.data && res.data.schedules) {
        setProgramName(res.data.name);
        const sortedCourses = res.data.schedules.sort((a, b) => a.semester - b.semester);
        setCourses(sortedCourses);
      }
      setIsLoading(false);
    });
  }, [programId]);

  const checkCoursePrerequisites = (updatedCourses, changedCourseId) => {
    const errors = [];
    const warnings = [];

    const changedCourse = updatedCourses.find(course => course.id === changedCourseId);

    if (changedCourse) {
      changedCourse.prerequisites.forEach(prereqId => {
        const prereqCourse = updatedCourses.find(c => c.id === prereqId);
        if (prereqCourse && prereqCourse.semester >= changedCourse.semester) {
          errors.push(`Môn học "${changedCourse.courseName}" (Kỳ ${changedCourse.semester}) phải học sau môn tiên quyết "${prereqCourse.courseName}" (Kỳ ${prereqCourse.semester}).`);
        }
      });

      updatedCourses.forEach(otherCourse => {
        if (otherCourse.prerequisites.includes(changedCourse.id)) {
          if (changedCourse.semester >= otherCourse.semester) {
            warnings.push(`Môn học "${changedCourse.courseName}" (Kỳ ${changedCourse.semester}) là môn tiên quyết của "${otherCourse.courseName}" (Kỳ ${otherCourse.semester}) nhưng lại học sau hoặc cùng kỳ.`);
          }
        }
      });
    }

    return { errors, warnings };
  };

  const handleSemesterChange = (courseId, newSemester) => {
    const updatedSemesterUpdates = {
      ...semesterUpdates,
      [courseId]: newSemester,
    };
    setSemesterUpdates(updatedSemesterUpdates);

    const updatedCourses = courses.map((course) => ({
      ...course,
      semester: updatedSemesterUpdates[course.id] || course.semester,
    }));

    const { errors, warnings } = checkCoursePrerequisites(updatedCourses, courseId);

    if (errors.length > 0) {
      errors.forEach(error => warningNoti(error, 5000));
    }
    if (warnings.length > 0) {
      warnings.forEach(warning => warningNoti(warning, 5000));
    }
  };

  const validatePrerequisiteCourses = (updatedCourses) => {
    let errors = [];

    updatedCourses.forEach((course) => {
      course.prerequisites.forEach((prereqId) => {
        const prereqCourse = updatedCourses.find(c => c.id === prereqId);
        if (prereqCourse && prereqCourse.semester >= course.semester) {
          errors.push(`Môn học "${course.courseName}" (Kỳ ${course.semester}) phải học sau môn tiên quyết "${prereqCourse.courseName}" (Kỳ ${prereqCourse.semester}).`);
        }
      });
    });

    return errors;
  };

  const handleUpdateSemesters = () => {
    const updatedCourses = courses.map((course) => ({
      ...course,
      semester: semesterUpdates[course.id] || course.semester,
    }));

    const errors = validatePrerequisiteCourses(updatedCourses);

    if (errors.length > 0) {
      errors.forEach(error => errorNoti(error, 5000));
      return; 
    }

    const scheduleUpdates = updatedCourses.map((course) => ({
      programId,
      courseId: course.id,
      semesterId: course.semester,
    }));

    request(
      "put",
      `${programUrl.updateProgram}`,
      (res) => {
        if (res.status === 200) {
          successNoti("Cập nhật chương trình thành công!", 5000);
          history.push( `/training_course/teacher/program/${programId}`);
         
        } else {
          errorNoti("Cập nhật chương trình không thành công!", 5000);
        }
      },
      (error) => {
        console.error("Error updating:", error);
        errorNoti("Cập nhật chương trình không thành công!", 5000);
      },
      scheduleUpdates
    );
  };
  const fetchProgramData = () => {
    setIsLoading(true);
    request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
      if (res && res.data && res.data.schedules) {
        setProgramName(res.data.name);
        const sortedCourses = res.data.schedules.sort((a, b) => a.semester - b.semester);
        setCourses(sortedCourses);
      }
      setIsLoading(false);
    });
  };

  useEffect(() => {
    fetchProgramData();
  }, [programId]);

  const handleAddCourse = (needReload) => {
    if (needReload) {
      fetchProgramData(); // Tải lại dữ liệu chương trình
    }
  };

  const handleSearch = (e) => {
    setSearchText(e.target.value);
  };

  const handleBack = (e) => {
    history.push( `/training_course/teacher/program/${programId}`);
  };

  const filteredCourses = courses.filter((course) =>
    course.courseName.toLowerCase().includes(searchText.toLowerCase())||
    course.id.toLowerCase().includes(searchText.toLowerCase())

  );

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã học phần",
      align: "center",
      headerAlign: "center",
      flex: 0.75,
    },
    {
      field: "courseName",
      headerName: "Tên học phần",
      flex: 1.5,
    },
    {
      field: "credit",
      headerName: "Số tín chỉ",
      align: "center",
      headerAlign: "center",
      flex: 0.5,
    },
    {
      field: "semester",
      headerName: "Kỳ học",
      align: "center",
      headerAlign: "center",
      flex: 0.5,
      renderCell: (params) => (
        <FormControl fullWidth variant="outlined">
          <Select
            value={semesterUpdates[params.row.id] || params.row.semester || ""}
            onChange={(e) =>
              handleSemesterChange(params.row.id, e.target.value)
            }
          >
            {[1, 2, 3, 4, 5, 6, 7, 8].map((semester) => (
              <MenuItem key={semester} value={semester}>
                Kỳ {semester}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      ),
    },
    {
      field: "prerequisites",
      headerName: "Môn tiên quyết",
      flex: 1,
      renderCell: (params) => (
        <List dense>
          {params.value && params.value.length > 0 ? (
            params.value.map((prereq) => (
              <ListItem key={prereq}>
                <ListItemText primary={prereq} />
              </ListItem>
            ))
          ) : (
            <ListItem>
              <ListItemText primary="Không có" />
            </ListItem>
          )}
        </List>
      ),
    },
  ];

  return (
    <Paper elevation={3} style={styles.paper}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          {`Chương trình: ${programName} (Mã: ${programId})`}
        </Typography>
        <div style={styles.searchArea}>
          <Button
            variant="contained"
            color="primary"
            style={styles.firstButton}
            onClick={() => setDialogOpen(true)} // Open dialog
          >
            Thêm môn học
          </Button>
        
          <Button
            variant="contained"
            color="primary"
            style={styles.firstButton}
            onClick={handleUpdateSemesters}
          >
            Cập nhật học kỳ
          </Button>

          <TextField
            label="Tìm kiếm"
            variant="outlined"
            value={searchText}
            onChange={handleSearch}
            style={styles.searchBox}
          />
        </div>
      </div>

      <DataGrid
        rows={filteredCourses}
        columns={dataGridColumns}
        checkboxSelection={false}
        disableRowSelectionOnClick
        loading={isLoading}
        hideFooter
        pagination={false}
        style={styles.table}
      />

<AddCourseDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        programId={programId}
        onCourseAdded={handleAddCourse}
        existingCourses={courses}
      />
    </Paper>
  );
};

export default UpdateProgramPage;
