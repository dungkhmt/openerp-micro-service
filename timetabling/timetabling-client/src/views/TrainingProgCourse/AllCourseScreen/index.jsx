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
import ImportDialog from "./ImportDialog"; // Đường dẫn tới file ImportDialog

const AllCourseScreen = () => {
  const history = useHistory();
  const [courses, setCourses] = useState([]);
  const [search, setSearch] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isImportDialogOpen, setIsImportDialogOpen] = useState(false); // Trạng thái cho dialog Import

  useEffect(() => {
    const fetchCourses = async () => {
      setIsLoading(true);
      try {
        const response = await request("get", `${courseUrl.getAllCourse}`);
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

  const handleImportDialogOpen = () => {
    setIsImportDialogOpen(true);
  };

  const handleImportDialogClose = () => {
    setIsImportDialogOpen(false);
  };

  const fetchData = () => {
    // Reload data after importing
    setIsLoading(true);
    request("get", `${courseUrl.getAllCourse}`)
      .then((res) => {
        setCourses(res.data);
      })
      .catch((err) => console.error(err))
      .finally(() => setIsLoading(false));
  };

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
            variant="contained"
            style={styles.actionButton}
          >
            Xóa
          </Button>
          <Button
            color="success"
            variant="contained"
            onClick={handleImportDialogOpen} // Mở dialog
            style={styles.actionButton}
          >
            Import danh sách
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
        columns={[
          { field: "id", headerName: "Mã học phần", flex: 1 },
          { field: "courseName", headerName: "Tên học phần", flex: 1.5 },
          { field: "credit", headerName: "Số tín chỉ", flex: 0.5 },
          {
            field: "prerequisites",
            headerName: "Học phần tiên quyết",
            flex: 1.5,
            renderCell: (params) => params.value.join(", "),
          },
          {
            headerName: "Hành động",
            renderCell: (params) => (
              <Button
                variant="outlined"
                onClick={() =>
                  history.push(`/training_course/teacher/course/${params.row.id}`)
                }
              >
                Xem chi tiết
              </Button>
            ),
            flex: 1,
          },
        ]}
        checkboxSelection
        disableRowSelectionOnClick
        loading={isLoading}
        hideFooter
        pagination={false}
        style={styles.table}
      />

      {/* Import Dialog */}
      <ImportDialog
        open={isImportDialogOpen}
        handleClose={handleImportDialogClose}
        fetchData={fetchData} // Truyền hàm fetch lại danh sách
        semester={"2025A"} // Ví dụ: học kỳ hiện tại
      />
    </Paper>
  );
};

export default AllCourseScreen;
