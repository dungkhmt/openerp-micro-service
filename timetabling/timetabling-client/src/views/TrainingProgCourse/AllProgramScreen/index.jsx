import React, { useState, useEffect } from "react";
import { TextField, Paper, Typography, Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useHistory } from "react-router-dom";
import { request } from "api";
import { programUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";
import { styles } from "./index.style";

const AllProgramScreen = () => {
  const history = useHistory();
  const [programs, setPrograms] = useState([]);
  const [search, setSearch] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [selectedRows, setSelectedRows] = useState([]);

  const fetchPrograms = async () => {
    setIsLoading(true);
    try {
      const response = await request(
        "get",
        `${programUrl.getAllPrograms}?page=${page}&size=${pageSize}&keyword=${search}`
      );
      if (response && response.data) {
        // Đảm bảo mỗi program có id
        const programsWithIds = response.data.data.map((program) => ({
          ...program,
          id: program.id || program._id,
        }));
        setPrograms(programsWithIds);
        setTotalCount(response.data.totalElement);
      }
    } catch (error) {
      console.error("Error fetching programs:", error);
      errorNoti("Không thể tải danh sách chương trình!", 3000);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPrograms();
  }, [page, pageSize, search]);

  const handleSearch = (e) => {
    setSearch(e.target.value);
    setPage(0); // Reset về trang đầu khi tìm kiếm
  };

  const handleDeleteSelectedPrograms = async () => {
    if (selectedRows.length === 0) {
      warningNoti("Vui lòng chọn ít nhất một chương trình để xóa!", 3000);
      return;
    }

    const confirmDelete = window.confirm(
      `Bạn có chắc chắn muốn xóa ${selectedRows.length} chương trình đã chọn?`
    );

    if (confirmDelete) {
      setIsLoading(true);
      try {
        // Chỉ cần gửi danh sách IDs vào body request
        await request(
          "delete",
          programUrl.deletePrograms, // Đảm bảo đúng URL của API
          (res) => {
            if (res.status === 200) {
              successNoti("Xóa chương trình thành công!", 3000);
              fetchPrograms(); // Tải lại danh sách sau khi xóa
              setSelectedRows([]); // Xóa các lựa chọn
            } else {
              errorNoti("Xóa chương trình không thành công!", 3000);
            }
          },
          (error) => {
            console.error("Error deleting programs:", error);
            errorNoti("Không thể xóa chương trình!", 3000);
          },
          selectedRows // Truyền trực tiếp danh sách các IDs
        );
      } finally {
        setIsLoading(false);
      }
    }
  };

  // Xử lý khi selection thay đổi
  const handleSelectionChange = (newSelectionModel) => {
    setSelectedRows(newSelectionModel);
    console.log("Selected program IDs:", newSelectionModel); // Debug log
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã chương trình",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
    {
      field: "name",
      headerName: "Tên chương trình",
      flex: 1.5,
    },
    {
      field: "actions",
      headerName: "Hành động",
      renderCell: (params) => (
        <Button
          variant="outlined"
          onClick={() =>
            history.push(`/training_course/teacher/program/${params.row.id}`)
          }
        >
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
            onClick={() =>
              history.push(`/training_course/teacher/program/create`)
            }
            style={styles.firstButton}
          >
            Thêm chương trình
          </Button>

          <Button
            variant="contained"
            color="error"
            style={styles.actionButton}
            onClick={handleDeleteSelectedPrograms}
          >
            Xóa Chương Trình
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
        rows={programs}
        columns={dataGridColumns}
        checkboxSelection
        disableRowSelectionOnClick
        loading={isLoading}
        pagination
        pageSize={pageSize}
        rowCount={totalCount}
        paginationMode="server"
        page={page}
        onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
        onPageChange={(newPage) => setPage(newPage)}
        style={styles.table}
        onRowSelectionModelChange={handleSelectionChange}
        rowSelectionModel={selectedRows}
        getRowId={(row) => row.id}
      />
    </Paper>
  );
};

export default AllProgramScreen;
