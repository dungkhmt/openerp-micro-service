import { useState, useEffect, useCallback } from "react";
import { request } from "api";
import { Button, TextField, Paper, Typography } from "@mui/material";
import { useHistory } from "react-router-dom";
import { SEMESTER } from "../config/localize";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 5,
};

const AllRegisterClassScreen = () => {
  const history = useHistory();
  const [classes, setClasses] = useState([]);
  const [registeredClass, setRegisteredClass] = useState([]);

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [search, setSearch] = useState("");

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  const debouncedSearch = useCallback(
    (search) => {
      const timer = setTimeout(() => {
        setPaginationModel({
          ...DEFAULT_PAGINATION_MODEL,
          page: 0,
        });
        handleFetchData();
      }, 1000);

      return () => clearTimeout(timer);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [search]
  );

  useEffect(() => {
    return debouncedSearch(search);
  }, [search, debouncedSearch]);

  useEffect(() => {
    handleFetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  useEffect(() => {
    fetchRegisteredData();
  }, []);

  const handleFetchData = () => {
    const searchParam =
      search !== "" ? `&search=${encodeURIComponent(search)}` : "";
    setIsLoading(true);
    request(
      "get",
      `/class-call/get-class-by-semester/${SEMESTER}?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}`,
      (res) => {
        setClasses(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const fetchRegisteredData = () => {
    request("get", `/class-call/get-my-registered-class/${SEMESTER}`, (res) => {
      setRegisteredClass(res.data);
    });
  };

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  const handleRegister = (klass) => {
    history.push("/ta-recruitment/student/class-register/", {
      classId: klass.id,
    });
  };

  const actionCell = (params) => {
    const rowData = params.row;
    const isRegistered = registeredClass.some((item) => item.id === rowData.id);
    return (
      <Button
        variant="contained"
        disabled={isRegistered}
        onClick={() => handleRegister(rowData)}
        style={styles.registeredButton}
      >
        {isRegistered ? "ĐÃ ĐĂNG KÝ" : "Đăng ký"}
      </Button>
    );
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã lớp",
      align: "center",
      headerAlign: "center",
    },
    { field: "subjectId", headerName: "Mã môn học", flex: 1 },
    {
      field: "subjectName",
      headerName: "Tên môn học",
      flex: 1,
    },
    {
      field: "day",
      headerName: "Thời gian",
      flex: 1,
    },
    {
      headerName: "Hành động",
      flex: 1,
      align: "center",
      headerAlign: "center",
      renderCell: actionCell,
    },
  ];

  const dataGridRows = classes.map((klass) => ({
    id: klass.id,
    subjectId: klass.subjectId,
    subjectName: klass.subjectName,
    day: `Thứ ${klass.day}, tiết ${klass.startPeriod} - ${klass.endPeriod}`,
    actions: { rowData: klass },
  }));

  return (
    <Paper elevation={3} style={{ paddingTop: "1em" }}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={{ fontWeight: "bold" }}>
          Danh sách lớp học
        </Typography>

        <TextField
          style={styles.searchBox}
          variant="outlined"
          name="search"
          value={search}
          onChange={handleSearch}
          placeholder="Tìm kiếm"
        />
      </div>

      <DataGrid
        loading={isLoading}
        rowHeight={60}
        sx={{ fontSize: 16 }}
        rows={dataGridRows}
        columns={dataGridColumns}
        autoHeight
        rowCount={totalElements}
        pagination
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[5, 10, 20]}
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </Paper>
  );
};

export default AllRegisterClassScreen;
