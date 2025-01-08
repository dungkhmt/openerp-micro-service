import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useHistory } from "react-router-dom";
import { request } from "api";
import { courseUrl } from "../apiURL";
import { styles } from './index.style';

const AllCourseScreen = () => {
  const history = useHistory();
  const [courses, setCourses] = useState([]);
  const [search, setSearch] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const fetchCourses = async () => {
      setIsLoading(true);
      try {
        const response = await request("get",`${courseUrl.getAllCourse}`);
        setCourses(response.data);
       } catch (error) {
        console.error("Error fetching courses:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCourses();
  }, []);

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  const filteredCourses = courses.filter((course) =>
    course.courseName.toLowerCase().includes(search.toLowerCase()) ||
    course.id.toString().toLowerCase().includes(search.toLowerCase())
  );

  const handleNavigateCreateCourse = () => {
    history.push(`/training_course/teacher/course/create`);
  };

  const actionCell = (params) => {
    const rowData = params.row;

    return (
      <div>
        <Button variant="outlined" onClick={() => handleNavigateCourseDetail(rowData)}>
          Xem chi tiết
        </Button>
      </div>
    );
  };

  const handleNavigateCourseDetail = (course) => {
    history.push(`/training_course/teacher/course/${course.id}`);
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã học phần",
      align: "center",
      headerAlign: "center",
      flex: 1
    },
    { field: "courseName", headerName: "Tên học phần", flex: 1.5 },
    { field: "credit", headerName: "Số tín chỉ",align: "center", flex: 0.5 },
    {
      field: "prerequisites",
      headerName: "Học phần tiên quyết",
      flex: 1.5,
      renderCell: (params) => {
        const prerequisites = params.value;
        return prerequisites.join(", ");
      },
    },
    {
      headerName: "Hành động",
      renderCell: actionCell,
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
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
            onClick={handleNavigateCreateCourse}
            style={styles.actionButton}
          >
            Thêm học phần
          </Button>
          <Button
           color="error"
           variant="outlined"
          //  disabled={rowSelect.length === 0}
            style={styles.actionButton}
          >
            Xóa
          </Button>
          <TextField
            variant="outlined"
            value={search}
            onChange={handleSearch}
            placeholder="Tìm kiếm học phần hoặc mã học phần"
            style={styles.searchBox}
          />
        </div>
      </div>

      <DataGrid
        rows={filteredCourses}
        columns={dataGridColumns}
        checkboxSelection
        disableRowSelectionOnClick
        loading={isLoading}
        hideFooter
        pagination={false}
        style={styles.table}
       />
    </Paper>
  );
};

export default AllCourseScreen;