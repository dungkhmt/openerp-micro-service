import {
  FormControl,
  InputLabel,
  Paper,
  Select,
  Typography,
  MenuItem,
  Button,
  TextField,
} from "@mui/material";
import { SEMESTER, SEMESTER_LIST } from "../config/localize";
import { styles } from "./index.style";
import { DataGrid } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import { request } from "api";
import { applicationUrl } from "../apiURL";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const TAAssistListScreen = () => {
  const [ta, setTa] = useState([]);
  const [semester, setSemester] = useState(SEMESTER);
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
  }, [semester, paginationModel]);

  const handleFetchData = () => {
    const searchParam =
      search !== "" ? `&search=${encodeURIComponent(search)}` : "";
    setIsLoading(true);
    request(
      "get",
      `${applicationUrl.getTaBySemester}/${semester}?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}`,
      (res) => {
        setTa(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const handleChangeSemester = (event) => {
    setSemester(event.target.value);
  };

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  const handleDownloadFile = () => {
    request(
      "get",
      `${applicationUrl.getAssignListFile}/${SEMESTER}`,
      (res) => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute(
          "download",
          `Danh-sách-trợ-giảng-học-kì-${SEMESTER}.xlsx`
        );
        document.body.appendChild(link);
        link.click();
      },
      {},
      {},
      { responseType: "arraybuffer" }
    );
  };

  const dataGridColumns = [
    {
      field: "name",
      headerName: "Tên sinh viên",
      headerAlign: "center",
      minWidth: 300,
      flex: 3,
    },
    {
      field: "mssv",
      headerName: "Mã số sinh viên",
      align: "center",
      headerAlign: "center",
      flex: 1.5,
      minWidth: 150,
    },
    {
      field: "email",
      headerName: "Email",
      headerAlign: "center",
      flex: 3,
      minWidth: 300,
    },
    {
      field: "cpa",
      headerName: "CPA",
      headerAlign: "center",
      align: "center",
      flex: 1,
      minWidth: 100,
    },
    {
      field: "englishScore",
      headerName: "Điểm tiếng Anh",
      headerAlign: "center",
      align: "center",
      flex: 2,
      minWidth: 200,
    },
    {
      field: "classId",
      headerName: "Mã lớp",
      flex: 1,
      align: "center",
      headerAlign: "center",
      minWidth: 100,
    },
    {
      field: "subjectId",
      headerName: "Mã môn học",
      flex: 2,
      align: "center",
      headerAlign: "center",
      minWidth: 200,
    },
    {
      field: "subjectName",
      headerName: "Tên môn học",
      headerAlign: "center",
      flex: 3,
      minWidth: 300,
    },
    {
      field: "time",
      headerName: "Thời gian",
      headerAlign: "center",
      flex: 2,
      minWidth: 200,
    },
    {
      field: "note",
      headerName: "Ghi chú",
      headerAlign: "center",
      flex: 4,
      minWidth: 400,
    },
  ];

  const dataGridRows = ta?.map((elm) => ({
    id: elm.id,
    name: elm.name,
    mssv: elm.mssv,
    email: elm.email,
    cpa: elm.cpa,
    englishScore: elm.englishScore,
    note: elm.note,
    subjectId: elm.classCall.subjectId,
    subjectName: elm.classCall.subjectName,
    classId: elm.classCall.id,
    time:
      "Thứ " +
      elm.classCall.day +
      ", tiết " +
      elm.classCall.startPeriod +
      " - " +
      elm.classCall.endPeriod,
  }));

  return (
    <Paper elevation={3}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Danh sách trợ giảng
        </Typography>
        <div style={styles.searchArea}>
          <FormControl style={styles.dropdown} fullWidth size="small">
            <InputLabel id="semester-label">Học kì</InputLabel>
            <Select
              labelId="semester-label"
              id="semester-select"
              value={semester}
              name="day"
              label="Học kì"
              onChange={handleChangeSemester}
              MenuProps={{ PaperProps: { sx: styles.selection } }}
            >
              {SEMESTER_LIST.map((semester, index) => (
                <MenuItem key={index} value={semester}>
                  {semester}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Button
            style={styles.firstButton}
            variant="outlined"
            onClick={handleDownloadFile}
          >
            Export danh sách trợ giảng
          </Button>

          <TextField
            style={styles.searchBox}
            variant="outlined"
            name="search"
            value={search}
            onChange={handleSearch}
            placeholder="Tìm kiếm"
          />
        </div>
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

export default TAAssistListScreen;
