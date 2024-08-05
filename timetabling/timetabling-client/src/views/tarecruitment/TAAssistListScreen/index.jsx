import {
  FormControl,
  InputLabel,
  Paper,
  Select,
  Typography,
  MenuItem,
  Button,
  TextField,
  IconButton,
} from "@mui/material";
import { styles } from "./index.style";
import { DataGrid } from "@mui/x-data-grid";
import { useMemo, useEffect, useState } from "react";
import useDebounce from "../config/debounce";
import { request } from "api";
import { applicationUrl, semesterUrl } from "../apiURL";
import { pdf } from "@react-pdf/renderer";
import TAPdf from "./TAPdf";
import FileSaver from "file-saver";
import FileDownloadIcon from "@mui/icons-material/FileDownload";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const TAAssistListScreen = () => {
  const [ta, setTa] = useState([]);
  const [semester, setSemester] = useState("");
  const [allSemester, setAllSemester] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [search, setSearch] = useState("");

  const debouncedSearch = useDebounce(search, 1000);

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  useEffect(() => {
    request("get", semesterUrl.getCurrentSemester, (res) => {
      setSemester(res.data);
    });
    request("get", semesterUrl.getAllSemester, (res) => {
      setAllSemester(res.data);
    });
  }, []);

  useEffect(() => {
    if (semester !== "") {
      handleFetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [semester, paginationModel, debouncedSearch]);

  const handleFetchData = () => {
    const searchParam =
      debouncedSearch !== ""
        ? `&search=${encodeURIComponent(debouncedSearch)}`
        : "";
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

  const handleSearch = useMemo(
    () => (e) => {
      setSearch(e.target.value);
    },
    []
  );

  const handleDownloadFile = () => {
    request(
      "get",
      `${applicationUrl.getAssignListFile}/${semester}`,
      (res) => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute(
          "download",
          `Danh-sách-trợ-giảng-học-kì-${semester}.xlsx`
        );
        document.body.appendChild(link);
        link.click();
      },
      {},
      {},
      { responseType: "arraybuffer" }
    );
  };

  const downloadPdf = async (data) => {
    console.log(data);
    const blob = await pdf(<TAPdf data={data} />).toBlob();
    FileSaver.saveAs(blob, `${data.mssv}_${data.classId}.pdf`);
  };

  const actionCell = (params) => {
    const row = params.row;
    return (
      <IconButton onClick={() => downloadPdf(row)}>
        <FileDownloadIcon />
      </IconButton>
    );
  };

  const dataGridColumns = [
    {
      headerName: "Hợp đồng",
      align: "center",
      headerAlign: "center",
      flex: 1,
      minWidth: 100,
      renderCell: actionCell,
    },
    {
      field: "name",
      headerName: "Tên sinh viên",
      align: "center",
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
      align: "center",
      flex: 3,
      minWidth: 300,
    },
    {
      field: "time",
      headerName: "Thời gian",
      headerAlign: "center",
      align: "center",
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
    phoneNumber: elm.phoneNumber,
    englishScore: elm.englishScore,
    note: elm.note,
    subjectId: elm.classCall.subjectId,
    subjectName: elm.classCall.subjectName,
    classId: elm.classCall.id,
    classRoom: elm.classCall.classRoom,
    time:
      "Thứ " +
      elm.classCall.day +
      ", tiết " +
      elm.classCall.startPeriod +
      " - " +
      elm.classCall.endPeriod,
  }));

  return (
    <>
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
                {allSemester.map((semester, index) => (
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
          style={{ height: "65vh" }}
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
    </>
  );
};

export default TAAssistListScreen;
