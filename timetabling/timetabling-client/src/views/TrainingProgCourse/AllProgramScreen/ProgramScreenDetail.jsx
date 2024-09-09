import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  Checkbox
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { useParams, useHistory } from "react-router-dom"; // Import useParams to get the programId from the URL
import { styles } from './index.style';
import { programUrl } from "../apiURL";

const ProgramDetailPage = () => {
  const { programId } = useParams(); // Extract the programId from the URL
  const [courses, setCourses] = useState([]);
  const [availableCourses, setAvailableCourses] = useState([]); // List of available courses
  const [selectedCourses, setSelectedCourses] = useState([]); // Courses selected to add
  const [search, setSearch] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [openDialog, setOpenDialog] = useState(false); // Control the dialog state
  const history = useHistory(); // Use history for navigation

  useEffect(() => {
    setIsLoading(true);
    request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
      setCourses(res.data.schedules);
      setIsLoading(false);
    });
  }, [programId]); // Pass programId as a dependency to re-fetch data when the programId changes

  const fetchAvailableCourses = () => {
    request("get", programUrl.getAvailableCourses, (res) => {
      setAvailableCourses(res.data);
    });
  };

  const handleSearchChange = (event) => {
    setSearch(event.target.value);
  };

  const filteredCourses = courses.filter((course) => {
    const courseNameLower = course.courseName ? course.courseName.toLowerCase() : '';
    const courseIdLower = course.id ? course.id.toString().toLowerCase() : '';
  
    return (
      courseNameLower.includes(search.toLowerCase()) ||
      courseIdLower.includes(search.toLowerCase())
    );
  });

  const handleOpenDialog = () => {
    setOpenDialog(true);
    fetchAvailableCourses(); // Fetch available courses when opening the dialog
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSelectCourse = (courseId) => {
    if (selectedCourses.includes(courseId)) {
      setSelectedCourses(selectedCourses.filter(id => id !== courseId));
    } else {
      setSelectedCourses([...selectedCourses, courseId]);
    }
  };

  const handleAddCourses = () => {
    request("post", `${programUrl.addCoursesToProgram}/${programId}`, { courses: selectedCourses }, () => {
      setOpenDialog(false);
      request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
        setCourses(res.data.schedules);
      });
    });
  };

  const handleEditProgram = () => {
    history.push(`/edit-program/${programId}`); // Assuming the edit page URL follows this pattern
  };

  const dataGridColumns = [
    { field: 'id', headerName: 'Mã học phần', align: 'center', headerAlign: 'center', flex: 0.75 },
    { field: 'courseName', headerName: 'Tên học phần', flex: 1.5 },
    { field: 'credit', headerName: 'Số tín chỉ', align: 'center', headerAlign: 'center', flex: 0.5 },
    {
      field: 'semester',
      headerName: 'Kỳ học',
      align: 'center',
      headerAlign: 'center',
      flex: 0.5,
      renderCell: (params) => (
        <Typography>
          {params.row.semester ? `Kỳ ${params.row.semester}` : 'Chưa xác định'}
        </Typography>
      ),
    },
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
          <Button
            variant="contained"
            color="primary"
            style={styles.firstButton}
            onClick={handleEditProgram} // Navigate to the edit page
          >
            Chỉnh sửa chương trình
          </Button>
          <Button
            variant="contained"
            color="primary"
            style={styles.firstButton}  
            onClick={handleOpenDialog} // Open the dialog to add courses
          >
            Thêm môn học
          </Button>
          <TextField
            variant="outlined"
            value={search}
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

      {/* Dialog for adding courses */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Thêm môn học vào chương trình</DialogTitle>
        <DialogContent>
          <List>
            {availableCourses.map(course => (
              <ListItem key={course.id} button onClick={() => handleSelectCourse(course.id)}>
                <Checkbox
                  checked={selectedCourses.includes(course.id)}
                />
                <ListItemText primary={`${course.id} - ${course.name}`} />
              </ListItem>
            ))}
          </List>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="primary">
            Hủy
          </Button>
          <Button onClick={handleAddCourses} color="secondary">
            Thêm
          </Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
};

export default ProgramDetailPage;
