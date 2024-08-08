import { useState, useEffect, useCallback } from "react";
import { request } from "api";
import { Button, TextField, Paper, Typography } from "@mui/material";
import { useHistory } from "react-router-dom";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";
import { classCallUrl, semesterUrl } from "../apiURL";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const AllRegisterClassScreen = () => {
  const history = useHistory();
  const [classes, setClasses] = useState([]);
  const [registeredClass, setRegisteredClass] = useState([]);

  const [semester, setSemester] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [search, setSearch] = useState("");

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  useEffect(() => {
    request("get", semesterUrl.getCurrentSemester, (res) => {
      setSemester(res.data);
    });
  }, []);

  const debouncedSearch = useCallback(
    (search) => {
      const timer = setTimeout(() => {
        setPaginationModel({
          ...DEFAULT_PAGINATION_MODEL,
          page: 0,
        });
        if (semester !== "") {
          handleFetchData();
        }
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
    if (semester !== "") {
      handleFetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel, semester]);

  useEffect(() => {
    if (semester !== "") {
      fetchRegisteredData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [semester]);

  const handleFetchData = () => {
    const searchParam =
      search !== "" ? `&search=${encodeURIComponent(search)}` : "";
    setIsLoading(true);
    request(
      "get",
      `${classCallUrl.getClassBySemesterL}/${semester}?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}`,
      (res) => {
        setClasses(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const fetchRegisteredData = () => {
    request("get", `${classCallUrl.getMyRegisterClass}/${semester}`, (res) => {
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
      <div>
        <Button
          variant="outlined"
          disabled={isRegistered}
          onClick={() => handleRegister(rowData)}
          style={styles.registeredButton}
        >
          {isRegistered ? "ĐÃ ĐĂNG KÝ" : "Đăng ký"}
        </Button>
      </div>
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
    <Paper elevation={3}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
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
        sx={styles.table}
        rows={dataGridRows}
        columns={dataGridColumns}
        rowCount={totalElements}
        pagination
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[10, 20, 50]}
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </Paper>
  );
};

export default AllRegisterClassScreen;
