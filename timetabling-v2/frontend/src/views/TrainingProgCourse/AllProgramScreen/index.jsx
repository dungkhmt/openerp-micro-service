import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useHistory } from "react-router-dom";
import { request } from "api"; // Import your API helper
import { programUrl } from "../apiURL"; // Define your API URL
import { styles } from './index.style'; // Import styles from index.style

const AllProgramScreen = () => {
  const history = useHistory();
  const [programs, setPrograms] = useState([]);
  const [search, setSearch] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    const fetchPrograms = async () => {
      setIsLoading(true);
      try {
        const response = await request("get", `${programUrl.getAllPrograms}?page=${page}&size=${pageSize}&keyword=${search}`);
        setPrograms(response.data.data); // Assuming 'data' contains the list of programs
        setTotalCount(response.data.totalElement); // Assuming 'totalElement' is the total count of programs
      } catch (error) {
        console.error("Error fetching programs:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPrograms();
  }, [page, pageSize, search]);

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  const filteredPrograms = programs.filter((program) =>
    program.name.toLowerCase().includes(search.toLowerCase()) ||
  program.id.toString().toLowerCase().includes(search.toLowerCase())
  );

  const handleNavigateCreateProgram = () => {
    history.push(`/training_course/teacher/program/create`);
  };

  const handleNavigateProgramDetail = (program) => {
    history.push(`/training_course/teacher/program/${program.id}`);
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã chương trình",
      align: "center",
      headerAlign: "center",
      flex: 1
    },
    { field: "name", headerName: "Tên chương trình", flex: 1.5 },
    {
      headerName: "Hành động",
      renderCell: (params) => (
        <Button variant="outlined" onClick={() => handleNavigateProgramDetail(params.row)}>
          Xem chi tiết
        </Button>
      ),
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
  ];

  return (
    <Paper elevation={3} style={styles.paper}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Danh mục chương trình học
        </Typography>
        <div style={styles.searchArea}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleNavigateCreateProgram}
            style={styles.actionButton}
          >
            Thêm chương trình
          </Button>
          <TextField
            variant="outlined"
            value={search}
            onChange={handleSearch}
            placeholder="Tìm kiếm chương trình"
            style={styles.searchBox}
          />
        </div>
      </div>

      <DataGrid
        rows={filteredPrograms}
        columns={dataGridColumns}
        checkboxSelection
        disableRowSelectionOnClick
        loading={isLoading}
        pagination
        pageSize={pageSize}
        rowCount={totalCount}
        paginationMode="server"
        onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
        onPageChange={(newPage) => setPage(newPage)}
        style={styles.table}
      />
    </Paper>
  );
};

export default AllProgramScreen;
