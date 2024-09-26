import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
 
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api"; // Import request từ thư viện của bạn
import { useParams, useHistory } from "react-router-dom";
import { styles } from "./index.style";
import { programUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";


const ProgramDetailPage = () => {
  const { programId } = useParams(); // Lấy programId từ URL
  const [courses, setCourses] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [searchText, setSearchText] = useState(""); // Tìm kiếm
  const history = useHistory(); // Để điều hướng
  const [programName, setProgramName] = useState(""); 

  useEffect(() => {
    setIsLoading(true);
    request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
      if (res && res.data && res.data.schedules) {
        setProgramName(res.data.name); // Set program name from API
        const sortedCourses = res.data.schedules.sort((a, b) => a.semester - b.semester);
        setCourses(sortedCourses);
      }
      setIsLoading(false);
    });
  }, [programId]);

  const handleEditProgram = () => {
    history.push(`edit/${programId}`);
  };

  const handleSearch = (e) => {
    setSearchText(e.target.value);
  };
  

  const filteredCourses = courses.filter((course) =>
    course.courseName.toLowerCase().includes(searchText.toLowerCase())
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
        <Typography>{`Kỳ ${params.row.semester}`}</Typography> // Hiển thị mà không có Select
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
            onClick={handleEditProgram}
          >
            Chỉnh sửa chương trình
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
    </Paper>
  );
};

export default ProgramDetailPage;
