import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
  Checkbox,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { styles } from './index.style';

const fakeCourses = [
    { id: 'CS101', courseName: 'Introduction to Computer Science', credit: 3, semesters: [1], prerequisites: [] },
    { id: 'CS102', courseName: 'Data Structures', credit: 3, semesters: [2], prerequisites: ['CS101'] },
    { id: 'CS103', courseName: 'Algorithms', credit: 3, semesters: [3], prerequisites: ['CS102'] },
    { id: 'MATH101', courseName: 'Calculus I', credit: 4, semesters: [1], prerequisites: [] },
    { id: 'MATH102', courseName: 'Calculus II', credit: 4, semesters: [2], prerequisites: ['MATH101'] },
    { id: 'PHY101', courseName: 'Physics I', credit: 3, semesters: [2], prerequisites: [] },
    { id: 'PHY102', courseName: 'Physics II', credit: 3, semesters: [3], prerequisites: ['PHY101'] },
];

const CourseInfoPage = () => {
  const [courses, setCourses] = useState(fakeCourses);
  const [searchTerm, setSearchTerm] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const [violationMessage, setViolationMessage] = useState('');

  useEffect(() => {
    setIsLoading(true);
    setTimeout(() => {
      setCourses(fakeCourses);
      setIsLoading(false);
    }, 1000);
  }, []);

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const filteredCourses = courses.filter(course =>
    course.courseName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const checkPrerequisiteViolation = (updatedCourses) => {
    for (const course of updatedCourses) {
      for (const prereq of course.prerequisites) {
        const prereqCourse = updatedCourses.find(c => c.id === prereq);
        if (prereqCourse && prereqCourse.semesters[0] >= course.semesters[0]) {
          return `Môn học ${course.courseName} (Kỳ ${course.semesters[0]}) vi phạm tiên quyết: ${prereqCourse.courseName} (Kỳ ${prereqCourse.semesters[0]})`;
        }
      }
    }
    return '';
  };

  const handleSemesterChange = (courseId, semester) => {
    const updatedCourses = courses.map(course => {
      if (course.id === courseId) {
        return { ...course, semesters: [semester] };
      }
      return course;
    });

    const violation = checkPrerequisiteViolation(updatedCourses);
    if (violation) {
      setViolationMessage(violation);
      setOpenDialog(true);
    } else {
      setCourses(updatedCourses);
    }
  };

  const semesters = [1, 2, 3, 4, 5, 6, 7, 8];

  const dataGridColumns = [
    { field: 'id', headerName: 'Mã học phần', align: 'center', headerAlign: 'center', flex: 0.75 },
    { field: 'courseName', headerName: 'Tên học phần', flex: 1.5 },
    { field: 'credit', headerName: 'Số tín chỉ', align: 'center', headerAlign: 'center', flex: 0.5 },
    ...semesters.map(semester => ({
      field: `semester_${semester}`,
      headerName: `Kỳ ${semester}`,
      width: 60,
      renderCell: (params) => (
        <Checkbox
          checked={params.row.semesters.includes(semester)}
          onChange={() => handleSemesterChange(params.row.id, semester)}
        />
      ),
    })),
    {
      field: 'prerequisites',
      headerName: 'Môn tiên quyết',
      flex: 1,
      renderCell: (params) => (
        <List dense>
          {params.value.map(prereq => (
            <ListItem key={prereq}>
              <ListItemText primary={prereq} />
            </ListItem>
          ))}
        </List>
      ),
    }
  ];

  return (
    <Paper elevation={3} style={styles.paper}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Danh mục học phần
        </Typography>
        <div style={styles.searchArea}>
          <Button variant="contained" color="primary" style={styles.firstButton}>
            Thêm khóa học
          </Button>
          <Button variant="outlined" color="error" style={styles.actionButton}>
            Xóa
          </Button>
          <TextField
            variant="outlined"
            value={searchTerm}
            onChange={handleSearchChange}
            placeholder="Tìm kiếm học phần"
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

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Lỗi môn học tiên quyết</DialogTitle>
        <DialogContent>
          <Typography>{violationMessage}</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} color="primary">
            Đóng
          </Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
};

export default CourseInfoPage;
